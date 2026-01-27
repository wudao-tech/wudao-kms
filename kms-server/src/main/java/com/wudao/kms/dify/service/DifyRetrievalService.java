package com.wudao.kms.dify.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wudao.kms.dify.dto.*;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.service.KnowledgeBaseService;
import com.wudao.kms.service.VectorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dify外部知识库检索服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DifyRetrievalService {

    private final VectorizationService vectorizationService;
    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 执行知识库检索
     *
     * @param request 检索请求
     * @return 检索响应
     */
    public DifyRetrievalResponse retrieval(DifyRetrievalRequest request) {
        log.info("Dify检索请求: knowledgeId={}, query={}", request.getKnowledgeId(), request.getQuery());

        // 解析知识库ID
        Long knowledgeBaseId = parseKnowledgeId(request.getKnowledgeId());

        // 获取检索参数
        DifyRetrievalSetting setting = request.getRetrievalSetting();
        Integer topK = setting.getTopK() != null ? setting.getTopK() : 5;
        Double scoreThreshold = setting.getScoreThreshold() != null ? setting.getScoreThreshold() : 0.5;

        // 构建内部检索请求
        KnowledgeTestDTO testDTO = KnowledgeTestDTO.builder()
                .query(request.getQuery())
                .knowledgeIds(Collections.singletonList(knowledgeBaseId))
                .searchType("hybrid")
                .maxSegmentCount(topK)
                .userId(request.getUserId())
                .ragScore(scoreThreshold)
                .ragTopK(topK)
                .build();

        // 执行检索
        List<KnowledgeSearchResult> searchResults = vectorizationService.searchPro(testDTO);

        // 过滤低于阈值的结果
        if (CollUtil.isNotEmpty(searchResults)) {
            searchResults = searchResults.stream()
                    .filter(r -> r.getScore() != null && r.getScore() >= scoreThreshold)
                    .sorted(Comparator.comparing(KnowledgeSearchResult::getScore).reversed())
                    .limit(topK)
                    .collect(Collectors.toList());
        }

        // 转换为Dify响应格式
        List<DifyRecord> records = convertToRecords(searchResults);

        log.info("Dify检索完成: knowledgeId={}, 结果数量={}", request.getKnowledgeId(), records.size());
        return DifyRetrievalResponse.of(records);
    }

    /**
     * 验证知识库是否存在
     *
     * @param knowledgeId 知识库ID
     * @return 是否存在
     */
    public boolean validateKnowledgeExists(String knowledgeId) {
        try {
            Long id = parseKnowledgeId(knowledgeId);
            KnowledgeBase knowledgeBase = knowledgeBaseService.getById(id);
            return knowledgeBase != null;
        } catch (Exception e) {
            log.warn("验证知识库存在性失败: knowledgeId={}", knowledgeId, e);
            return false;
        }
    }

    /**
     * 解析知识库ID
     */
    private Long parseKnowledgeId(String knowledgeId) {
        if (StrUtil.isBlank(knowledgeId)) {
            throw new IllegalArgumentException("知识库ID不能为空");
        }
        try {
            return Long.parseLong(knowledgeId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("知识库ID格式无效: " + knowledgeId);
        }
    }

    /**
     * 将内部检索结果转换为Dify格式
     */
    private List<DifyRecord> convertToRecords(List<KnowledgeSearchResult> searchResults) {
        if (CollUtil.isEmpty(searchResults)) {
            return Collections.emptyList();
        }

        return searchResults.stream()
                .map(this::convertToRecord)
                .collect(Collectors.toList());
    }

    /**
     * 转换单条记录
     */
    private DifyRecord convertToRecord(KnowledgeSearchResult result) {
        Map<String, Object> metadata = new HashMap<>();
        // Dify要求document_id必须是字符串类型
        if (result.getDocumentId() != null) {
            metadata.put("document_id", String.valueOf(result.getDocumentId()));
        }
        if (result.getKnowledgeBaseId() != null) {
            metadata.put("knowledge_base_id", String.valueOf(result.getKnowledgeBaseId()));
        }
        if (StrUtil.isNotBlank(result.getS3Url())) {
            metadata.put("path", result.getS3Url());
        }
        if (StrUtil.isNotBlank(result.getSummary())) {
            metadata.put("description", result.getSummary());
        }
        if (result.getKnowledgeSpaceId() != null) {
            metadata.put("space_id", String.valueOf(result.getKnowledgeSpaceId()));
        }
        if (StrUtil.isNotBlank(result.getFileType())) {
            metadata.put("file_type", result.getFileType());
        }

        return DifyRecord.builder()
                .content(result.getContent())
                .score(result.getScore())
                .title(result.getFilename())
                .metadata(metadata)
                .build();
    }
}
