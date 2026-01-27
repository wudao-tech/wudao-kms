package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.dashscope.rerank.TextReRank;
import com.alibaba.dashscope.rerank.TextReRankOutput;
import com.alibaba.dashscope.rerank.TextReRankParam;
import com.alibaba.dashscope.rerank.TextReRankResult;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.model.entity.SysUser;
import com.wudao.kms.dto.KnowledgeSearchResponse;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.dto.RerankResp;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeBasePermission;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.KnowledgeFileSegment;
import com.wudao.kms.entity.SearchHistory;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.chat.ChatModelStrategyFactory;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.service.LLMModelService;
import com.wudao.kms.mapper.KnowledgeFileMapper;
import com.wudao.kms.mapper.KnowledgeFileSegmentMapper;
import com.wudao.kms.mapper.QaImproveMapper;
import com.wudao.kms.mapper.SystemDictionaryMapper;
import com.wudao.kms.entity.SystemDictionary;
import com.wudao.kms.vo.RecommendKnowledgeResponse;
import com.wudao.security.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 向量化处理服务
 * 负责调用向量化API对文件进行向量化处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorizationService {

    private final RestTemplate restTemplate;

    @Resource
    private KnowledgeFileMapper fileMapper;
    @Resource
    private KnowledgeFileSegmentMapper segmentMapper;
    @Resource
    private VisitRecordService visitRecordService;
    @Resource
    @Lazy
    private KnowledgeBaseService knowledgeBaseService;

    @Resource
    private SearchHistoryService searchHistoryService;

    @Resource
    private KnowledgeBasePermissionService knowledgeBasePermissionService;
    private final LLMModelService llmModelService;


    @Value("${vectorization.api.url:http://localhost:8080}")
    private String vectorizationApiUrl;
    @Autowired
    private ChatModelStrategyFactory chatModelStrategyFactory;
    @Resource
    private QaImproveMapper qaImproveMapper;
    @Resource
    private SystemDictionaryMapper systemDictionaryMapper;


    /**
     * 向量化处理结果
     */
    @Data
    public static class VectorizationResult {
        private boolean success;
        private String message;
        private String documentId;
        private String status;

        public static VectorizationResult success(String documentId, String message) {
            VectorizationResult result = new VectorizationResult();
            result.success = true;
            result.documentId = documentId;
            result.message = message;
            result.status = "processing";
            return result;
        }

        public static VectorizationResult failure(String message) {
            VectorizationResult result = new VectorizationResult();
            result.success = false;
            result.message = message;
            result.status = "failed";
            return result;
        }
    }

    /**
     * 调用向量化API处理文件（带摘要和标签）
     *
     * @param fileUrl         文件URL
     * @param knowledgeBaseId 知识库ID
     * @param documentId      文档ID
     * @param userId          用户
     * @param knowledgeSpace  知识空间ID
     * @return 向量化处理结果
     */
    public VectorizationResult processFileVectorization(String fileUrl,
                                                        Long knowledgeBaseId,
                                                        Long documentId,
                                                        Long userId,
                                                        Long knowledgeSpace) {
        try {
            log.info("开始向量化处理: fileUrl={}, knowledgeBaseId={}, documentId={}, knowledgeSpace={}", fileUrl, knowledgeBaseId, documentId, knowledgeSpace);
            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("knowledge_base_id", knowledgeBaseId);
            requestBody.put("knowledge_file_id", documentId);
            requestBody.put("created_by", userId);
            // 添加知识空间ID
            if (knowledgeSpace != null) {
                requestBody.put("space_id", knowledgeSpace);
                log.debug("添加知识空间ID: {}", knowledgeSpace);
            }

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 调用向量化API
            String url = vectorizationApiUrl + "/upload";

            log.info("调用向量化API: {}, 请求体: {}", url, JSONObject.toJSONString(requestBody));

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                // 判断向量是否正常
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                // 更新文档处理状态
                KnowledgeFile knowledgeFile = new KnowledgeFile();
                if (jsonObject != null && jsonObject.getBoolean("success")) {
                    knowledgeFile.setId(documentId);
                    knowledgeFile.setStatus(3); // 设置为处理中状态
                    knowledgeFile.setProcessingStatus("处理完成");
                } else {
                    knowledgeFile.setId(documentId);
                    knowledgeFile.setStatus(4); // 设置为处理失败状态
                    knowledgeFile.setProcessingStatus("处理失败");
                }
                fileMapper.updateById(knowledgeFile);
                log.info("向量化处理请求成功: {}", responseBody);
                // 删除缓存
                return VectorizationResult.success(documentId.toString(), "向量化处理已提交，正在处理中");
            } else {
                log.error("向量化处理请求失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return VectorizationResult.failure("向量化处理请求失败: HTTP " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("调用向量化API失败: fileUrl={}, documentId={}", fileUrl, documentId, e);
            return VectorizationResult.failure("向量化处理失败: " + e.getMessage());
        }
    }

    public List<KnowledgeSearchResult> searchPro(KnowledgeTestDTO knowledgeTestDTO) {
        try {
            if (StringUtil.isBlank(knowledgeTestDTO.getQuery())) {
                return Collections.emptyList();
            }

            // 参数验证和默认值设置
            if (StringUtil.isBlank(knowledgeTestDTO.getSearchType())) {
                knowledgeTestDTO.setSearchType("semantic");
            }
            if (knowledgeTestDTO.getMaxSegmentCount() == null || knowledgeTestDTO.getMaxSegmentCount() <= 0) {
                knowledgeTestDTO.setMaxSegmentCount(10);
            }

            log.debug("开始searchPro数据库检索: searchType={}, query={}, maxSegmentCount={}",
                    knowledgeTestDTO.getSearchType(), knowledgeTestDTO.getQuery(), knowledgeTestDTO.getMaxSegmentCount());

            // 词库处理
            String originalQuery = knowledgeTestDTO.getQuery();
            String processedQuery = processDictionary(originalQuery, knowledgeTestDTO.getKnowledgeBaseIds());

            // 如果返回敏感词提示，直接返回结果
            if (processedQuery.startsWith("SENSITIVE:")) {
                KnowledgeSearchResult sensitiveResult = new KnowledgeSearchResult();
                sensitiveResult.setHighlight("检索涉及敏感词");
                sensitiveResult.setAnswerType("SENSITIVE");
                sensitiveResult.setScore(0.0);
                return Collections.singletonList(sensitiveResult);
            }

            // 更新处理后的查询词
            knowledgeTestDTO.setQuery(processedQuery);
            log.debug("词库处理后的查询词: {}", processedQuery);

            // 根据搜索类型调用不同的检索方法
            String searchType = knowledgeTestDTO.getSearchType().toLowerCase();
            // 将查询向量化
            List<LLMModel> llmModels = llmModelService.queryEmbeddingModelByKnowledgeId(knowledgeTestDTO.getKnowledgeIds());
            // 得到对应的知识库
            List<KnowledgeBase> knowledgeBases = knowledgeBaseService.list(Wrappers.lambdaQuery(KnowledgeBase.class).in(KnowledgeBase::getId, knowledgeTestDTO.getKnowledgeIds()));
            // 进行分组
            Map<String, List<KnowledgeBase>> knowledgeMap = knowledgeBases.stream().collect(Collectors.groupingBy(KnowledgeBase::getEmbeddingModel));
            Map<String, LLMModel> llmMap = llmModels.stream().collect(Collectors.toMap(LLMModel::getModel, Function.identity(),(v1,v2) -> v1));
            final List<KnowledgeSearchResult> allResults = new ArrayList<>();

            // 保存原始的 knowledgeIds，用于全文检索
            List<Long> originalKnowledgeIds = knowledgeTestDTO.getKnowledgeIds();

            // 全文检索不依赖向量，只执行一次
            if ("fulltext".equals(searchType) || "hybrid".equals(searchType)) {
                knowledgeTestDTO.setKnowledgeIds(originalKnowledgeIds);
                allResults.addAll(performFulltextSearch(knowledgeTestDTO));
            }

            knowledgeMap.forEach((key,value) -> {
                LLMModel llmModel = llmMap.get(key);
                float[] queryVector = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode()).embedding(llmModel.getModel(), List.of(knowledgeTestDTO.getQuery()),
                        knowledgeTestDTO.getUserId()).getFirst();
                knowledgeTestDTO.setVector(queryVector);
                List<Long> knowledgeIds = value.stream().map(KnowledgeBase::getId).toList();
                knowledgeTestDTO.setKnowledgeIds(knowledgeIds);
                List<KnowledgeSearchResult> searchResults = qaImproveMapper.qaVectorByCondition(Arrays.toString(queryVector), knowledgeIds,
                            knowledgeTestDTO.getKnowledgeBaseIds());
                if (CollUtil.isNotEmpty(searchResults)) {
                    searchResults = searchResults.stream().filter(item -> Objects.nonNull(item.getScore())).collect(Collectors.toList());
                }

                // 只执行语义搜索（依赖向量）
                if ("semantic".equals(searchType) || "hybrid".equals(searchType)) {
                    allResults.addAll(performSemanticSearch(knowledgeTestDTO));
                }

                allResults.addAll(searchResults);
            });

            if (CollUtil.isEmpty(allResults)) {
                return new ArrayList<>();
            }

            // 对于混合检索，需要按照id去重（全文+语义可能返回相同的结果）
            List<KnowledgeSearchResult> results = allResults;

            // 统一处理重排功能
            results.sort(Comparator.comparing(KnowledgeSearchResult::getScore).reversed());
            // 处理一些RAG的筛选条件
            if (knowledgeTestDTO.getRagScore() != null) {
                results = results.stream().filter(item -> item.getScore() >= knowledgeTestDTO.getRagScore()).collect(Collectors.toList());
            }

            if (CollUtil.isEmpty(results)) {
                return new ArrayList<>();
            }

            if (knowledgeTestDTO.getRagTopK() != null) {
                results = results.stream().limit(knowledgeTestDTO.getRagTopK()).collect(Collectors.toList());
            }

            if (StringUtil.isNotBlank(knowledgeTestDTO.getRerankModel()) && CollUtil.isNotEmpty(results)) {
                log.debug("开始执行重排功能，重排类型: {}", knowledgeTestDTO.getRerankModel());
                results = new ArrayList<>(performRerank(results, knowledgeTestDTO.getRerankModel(), knowledgeTestDTO.getQuery(), knowledgeTestDTO.getUserId()));
                if (knowledgeTestDTO.getRerankScore() != null) {
                    results = results.stream().filter(item -> item.getRerankScore() >= knowledgeTestDTO.getRerankScore()).collect(Collectors.toList());
                }
                if (knowledgeTestDTO.getRerankTopK() != null) {
                    results.sort(Comparator.comparing(KnowledgeSearchResult::getRerankScore).reversed());
                    results = results.stream().limit(knowledgeTestDTO.getRerankTopK()).collect(Collectors.toList());
                }
            }

            // 统一处理高亮功能
            if (CollUtil.isNotEmpty(results)) {
                addHighlight(results, knowledgeTestDTO.getQuery(), searchType);
            }

            // 如果开启重排，根据重排分+score排序，没有开启则只根据score排序，倒叙
            if (StringUtil.isNotBlank(knowledgeTestDTO.getRerankModel())) {
                results = results.stream().filter(item -> item.getRerankScore() != null).collect(Collectors.toList());
                results.sort(Comparator.comparing(KnowledgeSearchResult::getRerankScore).reversed());
            }
            Map<Long, Integer> fileVisit = visitRecordService.queryViewCountByTargetId(results.stream().map(KnowledgeSearchResult::getDocumentId).collect(Collectors.toList()));
            results.forEach(item -> {
                if (fileVisit.containsKey(item.getDocumentId())) {
                    item.setViewCount(fileVisit.get(item.getDocumentId()));
                }
            });
            // 增加知识库的权限
            if (knowledgeTestDTO.getUserId() != null) {
                // 获取用户对知识库的权限信息
                List<KnowledgeBasePermission> knowledgePermissions =
                    knowledgeBasePermissionService.getKnowledgeBaseByUserId(knowledgeTestDTO.getUserId());
                // 构建知识库ID -> 权限对象的映射
                Map<Long, KnowledgeBasePermission> permissionMap = knowledgePermissions.stream()
                    .collect(Collectors.toMap(KnowledgeBasePermission::getKnowledgeBaseId, p -> p, (p1, p2) -> p1));

                // 为每个搜索结果设置权限类型
                results.stream()
                    .filter(item -> item.getKnowledgeBaseId() != null)
                    .forEach(item -> {
                        KnowledgeBasePermission permission = permissionMap.get(item.getKnowledgeBaseId());
                        if (permission != null) {
                            item.setPermissionType(permission.getPermissionType());
                        } else {
                            item.setPermissionType(3); // 没有权限记录时设置为3
                        }
                    });
            }
            return results;

        } catch (Exception e) {
            log.error("searchPro数据库检索失败: query={}", knowledgeTestDTO.getQuery(), e);
            return new ArrayList<>();
        }
    }

    public List<KnowledgeSearchResult> search(KnowledgeTestDTO knowledgeTestDTO) {
        try {
            if (StringUtil.isBlank(knowledgeTestDTO.getQuery())) {
                throw new ServiceException("req_para_null", "知识库ID和查询内容不能为空");
            }
            // 如果是知识库测试，传过来的知识库里需要有文件
            if ("knowledge_test".equals(knowledgeTestDTO.getEntranceType())
                    && knowledgeTestDTO.getKnowledgeIds() != null) {
                LambdaQueryWrapper<KnowledgeFile> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(KnowledgeFile::getKnowledgeBaseId, knowledgeTestDTO.getKnowledgeIds());
                wrapper.eq(KnowledgeFile::getDeleteFlag, false);
                Long fileCount = fileMapper.selectCount(wrapper);
                if (fileCount < 1) {
                    throw new ServiceException("knowledge_file_not_found", "知识库中没有文件，请先上传文件");
                }
            }
            log.info("开始向量化搜索: knowledgeId={}, query={}", knowledgeTestDTO.getKnowledgeIds(), knowledgeTestDTO.getQuery());
            if (StringUtil.isBlank(knowledgeTestDTO.getSearchType())) {
                knowledgeTestDTO.setSearchType("semantic");
            }
            if (knowledgeTestDTO.getMaxSegmentCount() == null || knowledgeTestDTO.getMaxSegmentCount() <= 0) {
                knowledgeTestDTO.setMaxSegmentCount(10);
            }
            // 调用向量化API
            String url = vectorizationApiUrl + "/search";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", knowledgeTestDTO.getQuery());
            if (knowledgeTestDTO.getKnowledgeIds() != null) {
                requestBody.put("knowledge_base_id", knowledgeTestDTO.getKnowledgeIds());
            } else {
                // 查询拥有知识库的列表
                List<Long> knowledgeList = knowledgeBaseService.queryPermissionKnowledgeList(SecurityUtils.getUserId());
                requestBody.put("knowledge_base_id", knowledgeList);
            }
            if (CollUtil.isNotEmpty(knowledgeTestDTO.getDocumentIds())) {
                requestBody.put("document_id", knowledgeTestDTO.getDocumentIds());
            }
            if (StringUtil.isNotBlank(knowledgeTestDTO.getSearchType())) {
                requestBody.put("search_type", knowledgeTestDTO.getSearchType());
            }
            if (CollUtil.isNotEmpty(knowledgeTestDTO.getKnowledgeBaseIds())) {
                requestBody.put("knowledge_space", knowledgeTestDTO.getKnowledgeBaseIds());
            }
            if (StringUtil.isNotBlank(knowledgeTestDTO.getUpdateTime())) {
                requestBody.put("update_time", knowledgeTestDTO.getUpdateTime());
            }
            if (StringUtil.isNotBlank(knowledgeTestDTO.getFileType())) {
                requestBody.put("suffix", knowledgeTestDTO.getFileType());
            }
            if (StringUtil.isNotBlank(knowledgeTestDTO.getRerankModel())) {
                requestBody.put("enable_rerank", true);
            } else {
                requestBody.put("enable_rerank", false);
            }

            // 查询当前已经禁用的知识库
            List<Long> disableKnowledge = knowledgeBaseService.queryDisableKnowledge();
            requestBody.put("filter_knowledge_id", disableKnowledge);

            requestBody.put("k", knowledgeTestDTO.getMaxSegmentCount());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用向量化搜索API: {}, 请求体: {}", url, JSONObject.toJSONString(requestBody));

            ResponseEntity<KnowledgeSearchResponse> response = restTemplate.postForEntity(url, entity, KnowledgeSearchResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                KnowledgeSearchResponse searchResponse = response.getBody();
                if (searchResponse.getResults() != null && !searchResponse.getResults().isEmpty()) {
                    log.info("向量化搜索请求成功，结果数量: {}, 耗时: {}ms",
                            searchResponse.getTotalResults(), searchResponse.getTookMs());
                    // 获取搜索结果并填充创建者昵称
                    List<KnowledgeSearchResult> responseResults = searchResponse.getResults();
                    List<Long> documentIds = responseResults.stream().map(KnowledgeSearchResult::getDocumentId).toList();
                    MPJLambdaWrapper<KnowledgeFile> wrapper = new MPJLambdaWrapper<>();
                    wrapper.selectAll(KnowledgeFile.class);
                    wrapper.selectAs(SysUser::getNickname, KnowledgeFile::getCreateByNickName);
                    wrapper.leftJoin(SysUser.class, SysUser::getId, KnowledgeFile::getCreatedBy);
                    wrapper.in(KnowledgeFile::getId, documentIds);
                    List<KnowledgeFile> knowledgeFiles = fileMapper.selectJoinList(wrapper);
                    Map<Long, KnowledgeFile> fileMap = knowledgeFiles.stream().collect(Collectors.toMap(KnowledgeFile::getId, kf -> kf));
                    for (KnowledgeSearchResult result : responseResults) {
                        KnowledgeFile file = fileMap.get(result.getDocumentId());
                        if (file != null) {
                            result.setCreateBy(file.getCreateByNickName());
                            result.setFilename(file.getFileName());
                            result.setS3Url(file.getFilePath());
                        }
                    }
                    // 查看浏览次数
                    Map<Long, Integer> longIntegerMap = visitRecordService.queryViewCountByTargetId(documentIds);
                    for (KnowledgeSearchResult result : responseResults) {
                        Integer viewCount = longIntegerMap.get(result.getDocumentId());
                        result.setViewCount(Objects.requireNonNullElse(viewCount, 0));
                    }

                    return responseResults;
                } else {
                    log.warn("向量化搜索请求成功，但未找到相关结果");
                    return List.of();
                }
            } else {
                log.error("向量化搜索请求失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return List.of();
            }
        } catch (Exception e) {
            log.error("向量化搜索失败", e);
            return List.of();
        }
    }

    /**
     * 执行语义检索（基于向量相似度）
     */
    private List<KnowledgeSearchResult> performSemanticSearch(KnowledgeTestDTO knowledgeTestDTO) {
        log.info("执行数据库语义检索: query={}", knowledgeTestDTO.getQuery());

        // 准备筛选条件
        List<Long> knowledgeBaseIds = getPermissionKnowledgeList(knowledgeTestDTO);
        List<Long> knowledgeSpaceIds = knowledgeTestDTO.getKnowledgeBaseIds();
        List<Long> documentIds = knowledgeTestDTO.getDocumentIds();
        List<Long> disableKnowledgeIds = knowledgeBaseService.queryDisableKnowledge();

        // 调用向量搜索
        List<KnowledgeSearchResult> results = segmentMapper.vectorSearch(
                knowledgeBaseIds,
                knowledgeSpaceIds,
                documentIds,
                disableKnowledgeIds,
                Arrays.toString(knowledgeTestDTO.getVector()),
                knowledgeTestDTO.getMaxSegmentCount().longValue(),
                knowledgeTestDTO.getFileType()
        );

        if (CollUtil.isEmpty(results)) {
            log.info("向量搜索未找到匹配结果");
            return new ArrayList<>();
        }

        log.info("向量搜索找到 {} 个分段结果", results.size());

        // 只需要补充浏览次数（文件信息已通过联表查询获取）
        Map<Long, Integer> viewCountMap = visitRecordService.queryViewCountByTargetId(documentIds);
        for (KnowledgeSearchResult result : results) {
            Integer viewCount = viewCountMap.get(result.getDocumentId());
            result.setViewCount(Objects.requireNonNullElse(viewCount, 0));
            result.setAnswerType("DOC");
        }
        return results;

    }

    /**
     * 执行全文检索（基于文本匹配）
     */
    private List<KnowledgeSearchResult> performFulltextSearch(KnowledgeTestDTO knowledgeTestDTO) {
        log.info("执行数据库全文检索: query={}", knowledgeTestDTO.getQuery());
        return searchByContent(knowledgeTestDTO, "fulltext");
    }

    /**
     * 基于内容进行数据库检索（使用PostgreSQL全文搜索+分词）
     */
    private List<KnowledgeSearchResult> searchByContent(KnowledgeTestDTO knowledgeTestDTO, String searchType) {
        try {
            log.info("执行PostgreSQL全文检索（分词），searchType: {}, query: {}, 最大结果数: {}",
                    searchType, knowledgeTestDTO.getQuery(), knowledgeTestDTO.getMaxSegmentCount());

            // 步骤1: 对查询词进行分词并拼接成 "词1 & 词2 & 词3" 格式
            String tokenizedQuery = tokenizeAndJoinQuery(knowledgeTestDTO.getQuery());
            log.info("查询词分词结果: {} -> {}", knowledgeTestDTO.getQuery(), tokenizedQuery);

            // 步骤2: 准备查询参数
            List<Long> knowledgeBaseIds = getPermissionKnowledgeList(knowledgeTestDTO);
            List<Long> knowledgeSpaceIds = knowledgeTestDTO.getKnowledgeBaseIds();
            List<Long> documentIds = knowledgeTestDTO.getDocumentIds();

            // 步骤3: 调用XML Mapper中定义的全文搜索方法
            List<KnowledgeSearchResult> results = segmentMapper.fulltextSearch(
                    tokenizedQuery,
                    knowledgeBaseIds,
                    knowledgeSpaceIds,
                    documentIds,
                    knowledgeTestDTO.getMaxSegmentCount().longValue(),
                    knowledgeTestDTO.getFileType()
            );

            if (CollUtil.isEmpty(results)) {
                log.info("PostgreSQL全文检索未找到匹配结果");
                return new ArrayList<>();
            }

            log.info("PostgreSQL全文检索找到 {} 个分段结果", results.size());

            // 步骤4: 补充浏览次数
            List<Long> resultDocIds = results.stream()
                    .map(KnowledgeSearchResult::getDocumentId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            Map<Long, Integer> viewCountMap = visitRecordService.queryViewCountByTargetId(resultDocIds);
            for (KnowledgeSearchResult result : results) {
                Integer viewCount = viewCountMap.get(result.getDocumentId());
                result.setViewCount(Objects.requireNonNullElse(viewCount, 0));
                result.setAnswerType("DOC");
            }

            return results;

        } catch (Exception e) {
            log.error("PostgreSQL全文检索失败: searchType={}, query={}", searchType, knowledgeTestDTO.getQuery(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取用户有权限的知识库列表
     */
    private List<Long> getPermissionKnowledgeList(KnowledgeTestDTO knowledgeTestDTO) {
        if (knowledgeTestDTO.getKnowledgeIds() != null) {
            return knowledgeTestDTO.getKnowledgeIds();
        } else {
            return knowledgeBaseService.queryPermissionKnowledgeList(SecurityUtils.getUserId());
        }
    }

    /**
     * 使用PostgreSQL的to_tsvector对查询词进行分词，并拼接成 "词1 & 词2 & 词3" 格式
     * @param query 原始查询词
     * @return 拼接后的分词结果，例如："物道 & 知识库"
     */
    private String tokenizeAndJoinQuery(String query) {
        try {
            if (StringUtil.isBlank(query)) {
                return "";
            }

            // 调用PostgreSQL的分词功能
            List<String> tokens = segmentMapper.tokenizeText(query);

            if (CollUtil.isEmpty(tokens)) {
                log.warn("分词结果为空，使用原始查询词: {}", query);
                return query;
            }

            // 使用 & 连接分词结果
            String tokenizedText = String.join(" | ", tokens);
            log.debug("分词结果: {} -> {}", query, tokenizedText);

            return tokenizedText;

        } catch (Exception e) {
            log.error("查询词分词失败，使用原始查询词: query={}", query, e);
            return query;
        }
    }


    /**
     * 将分段结果转换为搜索结果
     */
    private List<KnowledgeSearchResult> convertToSearchResults(List<KnowledgeFileSegment> segments, float[] queryVector) {
        List<KnowledgeSearchResult> results = new ArrayList<>();
        String queryVectorStr = queryVector != null ? Arrays.toString(queryVector) : null;

        for (KnowledgeFileSegment segment : segments) {
            KnowledgeSearchResult result = new KnowledgeSearchResult();
            result.setDocumentId(segment.getFileId());
            result.setContent(segment.getSegmentContent());

            // 使用PostgreSQL的向量能力计算分数
            double score = 0.0; // 默认相似度分数
            if (queryVectorStr != null) {
                try {
                    Double calculatedScore = segmentMapper.calculateScore(segment.getId(), queryVectorStr);
                    if (calculatedScore != null) {
                        score = calculatedScore;
                    } else {
                        continue;
                    }
                } catch (Exception e) {
                    log.warn("计算向量分数失败，使用默认分数: segmentId={}", segment.getId(), e);
                }
            }
            result.setScore(score);
            result.setFilename(segment.getVlContent()); // 从关联查询中获取文件名
            result.setAnswerType("DOC");
            results.add(result);
        }

        // 查询浏览次数
        List<Long> documentIds = results.stream().map(KnowledgeSearchResult::getDocumentId).toList();
        Map<Long, Integer> viewCountMap = visitRecordService.queryViewCountByTargetId(documentIds);
        for (KnowledgeSearchResult result : results) {
            Integer viewCount = viewCountMap.get(result.getDocumentId());
            result.setViewCount(Objects.requireNonNullElse(viewCount, 0));
        }

        return results;
    }

    /**
     * 添加高亮显示
     *
     * @param results    搜索结果列表
     * @param query      查询关键词
     * @param searchType 搜索类型
     */
    private void addHighlight(List<KnowledgeSearchResult> results, String query, String searchType) {
        if (StringUtil.isBlank(query) || CollUtil.isEmpty(results)) {
            return;
        }

        try {
            log.info("开始添加高亮显示: searchType={}, query={}", searchType, query);

            for (KnowledgeSearchResult result : results) {
                if (StringUtil.isNotBlank(result.getContent())) {
                    String highlightContent = addHighlightToText(result.getContent(), query, searchType);
                    result.setHighlight(highlightContent);
                }
            }

            log.info("高亮显示添加完成，处理了 {} 个结果", results.size());

        } catch (Exception e) {
            log.error("添加高亮显示失败: query={}", query, e);
        }
    }

    /**
     * 对文本添加高亮标签（使用分词后的关键词）
     *
     * @param content    原始内容
     * @param query      查询关键词
     * @param searchType 搜索类型
     * @return 添加高亮标签后的内容
     */
    private String addHighlightToText(String content, String query, String searchType) {
        if (StringUtil.isBlank(content) || StringUtil.isBlank(query)) {
            return content;
        }

        try {
            // 对查询词进行分词，获取分词后的关键词列表
            List<String> tokens = segmentMapper.tokenizeText(query);

            if (CollUtil.isEmpty(tokens)) {
                log.warn("查询词分词失败，使用原始查询词进行高亮: {}", query);
                return highlight(content, query);
            }

            log.debug("高亮分词结果: {} -> {}", query, tokens);

            // 使用分词后的关键词进行高亮
            String result = content;
            for (String token : tokens) {
                if (StringUtil.isNotBlank(token)) {
                    // 大小写不敏感的正则替换
                    String regex = "(?i)(" + Pattern.quote(token) + ")";
                    result = result.replaceAll(regex, "<mark>$1</mark>");
                }
            }

            return result;

        } catch (Exception e) {
            log.error("文本高亮处理失败: query={}, content length={}", query, content.length(), e);
            // 发生异常时使用原始查询词进行高亮
            return highlight(content, query);
        }
    }

    /**
     * 高亮显示文本中的关键词
     *
     * @param content 检索结果文本
     * @param query   检索内容（查询词）
     * @param preTag  高亮前缀标签
     * @param postTag 高亮后缀标签
     * @return 高亮后的文本
     */
    public static String highlight(String content, String query,
                                   String preTag, String postTag) {
        if (content == null || query == null || query.trim().isEmpty()) {
            return content;
        }

        // 将查询词按空格分割
        String[] keywords = query.trim().split("\\s+");
        String result = content;

        for (String keyword : keywords) {
            if (!keyword.isEmpty()) {
                // 大小写不敏感的正则替换
                String regex = "(?i)(" + Pattern.quote(keyword) + ")";
                result = result.replaceAll(regex, preTag + "$1" + postTag);
            }
        }

        return result;
    }

    // 重载方法，使用默认HTML标签
    public static String highlight(String content, String query) {
        return highlight(content, query, "<mark>", "</mark>");
    }

    /**
     * 执行重排功能
     *
     * @param results    搜索结果列表
     * @param rerankModel 重排类型
     * @param query      查询语句
     * @return 重排后的结果
     */
    private List<KnowledgeSearchResult> performRerank(List<KnowledgeSearchResult> results, String rerankModel, String query, Long userId) {
        try {
            log.info("执行重排功能: rerankModel={}, resultsCount={}", rerankModel, results.size());
            LLMModel llmModel = llmModelService.queryLLMByModel(rerankModel);
            ChatModelStrategy strategy = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode());
            List<RerankResp> rerankResults = strategy.rerank(llmModel.getModel(), query, results.stream().map(KnowledgeSearchResult::getContent).toList(), userId);
            Map<String, Object> map = new HashMap<>();
            rerankResults.forEach(item-> map.put(item.getRerankContent(),item.getRerankScore()));
            results.forEach(item-> item.setRerankScore((Double) map.get(item.getContent())));
            return results;
        } catch (Exception e) {
            log.error("重排功能执行失败: rerankModel={}", rerankModel, e);
            return results; // 重排失败时返回原始结果
        }
    }


    /**
     * 推荐知识库
     *
     * @return 向量化API的URL
     */
    public RecommendKnowledgeResponse recommendKnowledgeBase() {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<Long> knowledgeIds = new ArrayList<>();
            if (!SecurityUtils.getLoginUser().isAdmin()) {
                //找到用户拥有的知识库
                knowledgeIds = knowledgeBaseService.queryPermissionKnowledgeList(SecurityUtils.getUserId());
                if (CollUtil.isEmpty(knowledgeIds)) {
                    return new RecommendKnowledgeResponse();
                }
                requestBody.put("knowledge_base_id", knowledgeIds);
            } else {
                knowledgeIds = knowledgeBaseService.list().stream().map(KnowledgeBase::getId).toList();
            }
            //找到用户的搜索历史
            LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(SearchHistory::getContent);
            wrapper.eq(SearchHistory::getUserId, SecurityUtils.getUserId())
                    .groupBy(SearchHistory::getContent)
                    .last("ORDER BY MAX(id) DESC limit 5");
            List<String> searchHistory = new ArrayList<>(searchHistoryService.list(wrapper).stream()
                    .map(SearchHistory::getContent)
                    .filter(Objects::nonNull)
                    .toList());
            // 按照搜索热度
            wrapper = new LambdaQueryWrapper<>();
            wrapper.select(SearchHistory::getContent);
            wrapper.ne(SearchHistory::getUserId, SecurityUtils.getUserId())
                    .groupBy(SearchHistory::getContent)
                    .last("limit 5");
            List<String> popular = searchHistoryService.list(wrapper).stream()
                    .map(SearchHistory::getContent)
                    .filter(Objects::nonNull)
                    .toList();
            if (CollUtil.isNotEmpty(popular)) {
                searchHistory.addAll(popular);
            }
            requestBody.put("user_search_list", searchHistory);
            log.info("推荐知识库请求体: {}", JSONObject.toJSONString(requestBody));

            KnowledgeTestDTO knowledgeTestDTO = KnowledgeTestDTO.builder().query(CollUtil.join(searchHistory, "")).build();
            if (!knowledgeIds.isEmpty()) {
                knowledgeTestDTO.setKnowledgeIds(knowledgeIds);
            }
            knowledgeTestDTO.setRerankModel("qwen3-rerank");
            knowledgeTestDTO.setMaxCount(10);
            knowledgeTestDTO.setRagScore(0.6);
            knowledgeTestDTO.setRagTopK(20);
            knowledgeTestDTO.setSearchType("hybrid");
            List<KnowledgeSearchResult> results = searchPro(knowledgeTestDTO);

            // 查询热度
            results = results.stream()
                    .filter(item -> item.getDocumentId() != null)
                    .collect(Collectors.groupingBy(
                            KnowledgeSearchResult::getDocumentId,
                            Collectors.maxBy(Comparator.comparing(
                                    result -> result.getScore() != null ? result.getScore() : 0.0
                            ))
                    ))
                    .values().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            List<RecommendKnowledgeResponse.RecommendKnowledgeItem> items = new ArrayList<>();
            for (KnowledgeSearchResult result : results) {
                RecommendKnowledgeResponse.RecommendKnowledgeItem item = new RecommendKnowledgeResponse.RecommendKnowledgeItem();
                item.setFileId(result.getDocumentId());
                item.setFileName(result.getFilename());
                item.setKnowledgeBaseName(result.getKnowledgeBaseId() != null ? result.getKnowledgeBaseId().toString() : "未知知识库");
                item.setCreatedAt(result.getCreateTime());
                item.setCreatedTimeText(result.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                item.setScore(result.getScore());
                item.setReason("基于您的搜索历史推荐");
                items.add(item);
            }
            RecommendKnowledgeResponse recommendKnowledgeResponse = new RecommendKnowledgeResponse();
            recommendKnowledgeResponse.setRecommendKnowledge(items);
            return recommendKnowledgeResponse;
        } catch (Exception e) {
            log.error("推荐知识库失败", e);
            throw new ServiceException("recommend_knowledge_base_failed", "推荐知识库失败: " + e.getMessage());
        }
//        return new RecommendKnowledgeResponse();
    }

    /**
     * 处理词库逻辑
     *
     * @param query            原始查询词
     * @param knowledgeBaseIds 知识空间ID列表
     * @return 处理后的查询词，如果是敏感词则返回"SENSITIVE:"前缀
     */
    private String processDictionary(String query, List<Long> knowledgeBaseIds) {
        try {
            // 构建查询条件：spaceId=0 或 spaceId in knowledgeBaseIds
            LambdaQueryWrapper<SystemDictionary> wrapper = Wrappers.lambdaQuery(SystemDictionary.class);
            wrapper.eq(SystemDictionary::getStatus, 1)
                    .eq(SystemDictionary::getDeleteFlag, false)
                    .and(w -> {
                        w.eq(SystemDictionary::getSpaceId, 0L);
                        if (CollUtil.isNotEmpty(knowledgeBaseIds)) {
                            w.or().in(SystemDictionary::getSpaceId, knowledgeBaseIds);
                        }
                    });

            List<SystemDictionary> dictionaries = systemDictionaryMapper.selectList(wrapper);

            if (CollUtil.isEmpty(dictionaries)) {
                log.debug("未找到词库数据，knowledgeBaseIds: {}", knowledgeBaseIds);
                return query;
            }

            log.debug("查询到词库数据共 {} 条", dictionaries.size());

            // 按类型分组
            Map<String, List<SystemDictionary>> dictMap = dictionaries.stream()
                    .collect(Collectors.groupingBy(SystemDictionary::getDictType));

            // 1. 先检查敏感词
            List<SystemDictionary> sensitiveWords = dictMap.get(SystemDictionary.DictType.SENSITIVE);
            if (CollUtil.isNotEmpty(sensitiveWords)) {
                log.debug("敏感词数量: {}", sensitiveWords.size());
                for (SystemDictionary sensitive : sensitiveWords) {
                    if (StringUtil.isNotBlank(sensitive.getDictKey())) {
                        // 去除前后空格进行匹配
                        String trimmedKey = sensitive.getDictKey().trim();
                        log.debug("检查敏感词: [{}], 查询词: [{}]", trimmedKey, query);
                        if (query.contains(trimmedKey)) {
                            log.info("检测到敏感词: {}", trimmedKey);
                            return "SENSITIVE:" + trimmedKey;
                        }
                    }
                }
            }

            String processedQuery = query;

            // 2. 处理纠错词（错误词 -> 正确词）
            List<SystemDictionary> correctionWords = dictMap.get(SystemDictionary.DictType.CORRECTION);
            if (CollUtil.isNotEmpty(correctionWords)) {
                log.debug("纠错词数量: {}", correctionWords.size());
                for (SystemDictionary correction : correctionWords) {
                    if (StringUtil.isNotBlank(correction.getDictKey())
                            && StringUtil.isNotBlank(correction.getDictValue())) {
                        // 去除前后空格进行匹配
                        String trimmedKey = correction.getDictKey().trim();
                        String trimmedValue = correction.getDictValue().trim();
                        if (processedQuery.contains(trimmedKey)) {
                            log.info("纠错词替换: {} -> {}", trimmedKey, trimmedValue);
                            processedQuery = processedQuery.replace(trimmedKey, trimmedValue);
                        }
                    }
                }
            }

            // 3. 处理同义词（扩展查询）
            List<SystemDictionary> synonymWords = dictMap.get(SystemDictionary.DictType.SYNONYM);
            if (CollUtil.isNotEmpty(synonymWords)) {
                log.debug("同义词数量: {}", synonymWords.size());
                StringBuilder synonymQuery = new StringBuilder(processedQuery);
                for (SystemDictionary synonym : synonymWords) {
                    if (StringUtil.isNotBlank(synonym.getDictKey())
                            && StringUtil.isNotBlank(synonym.getDictValue())) {
                        // 去除前后空格进行匹配
                        String trimmedKey = synonym.getDictKey().trim();
                        String trimmedValue = synonym.getDictValue().trim();
                        if (processedQuery.contains(trimmedKey)) {
                            log.info("同义词扩展: {} -> {}", trimmedKey, trimmedValue);
                            // 将同义词追加到查询中
                            synonymQuery.append(" ").append(trimmedValue);
                        }
                    }
                }
                processedQuery = synonymQuery.toString();
            }

            return processedQuery;

        } catch (Exception e) {
            log.error("词库处理失败: query={}", query, e);
            return query; // 发生异常时返回原始查询
        }
    }
} 