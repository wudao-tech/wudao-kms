package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.utils.DateFormats;
import com.wudao.common.utils.Threads;
import com.wudao.framework.redis.RedisCache;
import com.wudao.kms.common.DocumentToMarkdownTool;
import com.wudao.kms.dto.KnowledgeFilePageDTO;
import com.wudao.kms.dto.knowledgefile.*;
import com.wudao.kms.entity.*;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.chat.ChatModelStrategyFactory;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.service.LLMModelService;
import com.wudao.kms.llm.tool.ChangeMediaTool;
import com.wudao.kms.mapper.KnowledgeBaseMapper;
import com.wudao.kms.mapper.KnowledgeFileMapper;
import com.wudao.kms.mineru.MinerUService;
import com.wudao.kms.vo.KnowledgeFileVO;
import com.wudao.oss.service.OssService;
import com.wudao.security.utils.SecurityUtils;
import com.xkcoding.http.util.StringUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.DeviceRequest;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.content.Media;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 知识库文件Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeFileService extends MPJBaseServiceImpl<KnowledgeFileMapper, KnowledgeFile> {

    private final RedisCache redisCache;
    private final FileContentExtractorService fileContentExtractorService;
    @Lazy
    @Resource
    private VectorizationService vectorizationService;

    @Value("${basic.profile}")
    private String basicProfile;
    @Value("${env.mineru-image}")
    private String minerUImage;
    @Value("${env.docker-bind-path}")
    private String dockerBindPath;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final CoverGenerationService coverGenerationService;
    private final OssService ossService;
    private final DocumentToMarkdownTool documentToMarkdownTool;
    private final KnowledgeFileSegmentService knowledgeFileSegmentService;
    private final MarkdownSplitService markdownSplitService;
    private final MinerUService minerUService;
    private final ChatModelStrategyFactory chatModelStrategyFactory;
    private final LLMModelService llmModelService;
    private final QaImproveService qaImproveService;
    private final KnowledgeFileProgressService knowledgeFileProgressService;
    private final DockerClient dockerClient;
    /**
     * 按照对象参数方式查询文件列表
     */
    public Page<KnowledgeFileVO> list(KnowledgeFilePageDTO knowledgeFilePageDTO) {
        MPJLambdaWrapper<KnowledgeFile> wrapper = new MPJLambdaWrapper<>();

        // 选择字段
        wrapper.selectAll(KnowledgeFile.class)
                .selectAs(KnowledgeBase::getName, KnowledgeFileVO::getKnowledgeBaseName)
                .selectAs(KnowledgeSpace::getName, KnowledgeFileVO::getSpaceName)
                // 关联查询
                .leftJoin(KnowledgeBase.class, KnowledgeBase::getId, KnowledgeFile::getKnowledgeBaseId)
                .leftJoin(KnowledgeSpace.class, KnowledgeSpace::getId, KnowledgeFile::getSpaceId)
                // 查询条件
                .eq(knowledgeFilePageDTO.getKnowledgeBaseId() != null, KnowledgeFile::getKnowledgeBaseId, knowledgeFilePageDTO.getKnowledgeBaseId())
                .eq(knowledgeFilePageDTO.getSpaceId() != null, KnowledgeFile::getSpaceId, knowledgeFilePageDTO.getSpaceId())
                .like(knowledgeFilePageDTO.getFileName() != null, KnowledgeFile::getFileName, knowledgeFilePageDTO.getFileName())
                .eq(knowledgeFilePageDTO.getFileType() != null, KnowledgeFile::getFileType, knowledgeFilePageDTO.getFileType())
                .eq(knowledgeFilePageDTO.getStatus() != null, KnowledgeFile::getStatus, knowledgeFilePageDTO.getStatus())
                .eq(KnowledgeFile::getDeleteFlag, false)
                .orderByDesc(KnowledgeFile::getCreatedAt);

        Page<KnowledgeFileVO> page = new Page<>(knowledgeFilePageDTO.getCurrent(), knowledgeFilePageDTO.getSize());
        return this.selectJoinListPage(page, KnowledgeFileVO.class, wrapper);
    }

    /**
     * 分页查询文件（带关联查询）
     */
    public IPage<KnowledgeFileVO> pageKnowledgeFile(Integer current, Integer size,
                                                    Long knowledgeBaseId, Long spaceId,
                                                    String fileName, String fileType, Integer status) {
        Page<KnowledgeFileVO> page = new Page<>(current, size);
        MPJLambdaWrapper<KnowledgeFile> wrapper = new MPJLambdaWrapper<>();

        // 选择字段
        wrapper.selectAll(KnowledgeFile.class)
                .selectAs(KnowledgeBase::getName, KnowledgeFileVO::getKnowledgeBaseName)
                .selectAs(KnowledgeSpace::getName, KnowledgeFileVO::getSpaceName)
                // 关联查询
                .leftJoin(KnowledgeBase.class, KnowledgeBase::getId, KnowledgeFile::getKnowledgeBaseId)
                .leftJoin(KnowledgeSpace.class, KnowledgeSpace::getId, KnowledgeFile::getSpaceId)
                // 查询条件
                .eq(knowledgeBaseId != null, KnowledgeFile::getKnowledgeBaseId, knowledgeBaseId)
                .eq(spaceId != null, KnowledgeFile::getSpaceId, spaceId)
                .like(fileName != null, KnowledgeFile::getFileName, fileName)
                .eq(fileType != null, KnowledgeFile::getFileType, fileType)
                .eq(status != null, KnowledgeFile::getStatus, status)
                .eq(KnowledgeFile::getDeleteFlag, false)
                .orderByDesc(KnowledgeFile::getCreatedAt);

        return this.selectJoinListPage(page, KnowledgeFileVO.class, wrapper);
    }

    /**
     * 根据知识库ID查询文件列表
     */
    public List<KnowledgeFile> listByKnowledgeBaseId(Long knowledgeBaseId) {
        LambdaQueryWrapper<KnowledgeFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeFile::getKnowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeFile::getDeleteFlag, false)
                .orderByDesc(KnowledgeFile::getCreatedAt);
        return this.list(wrapper);
    }

    public List<KnowledgeFile> listByUserId(Long userId) {
        LambdaQueryWrapper<KnowledgeFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeFile::getCreatedBy, userId)
                .eq(KnowledgeFile::getDeleteFlag, false)
                .orderByDesc(KnowledgeFile::getCreatedAt);
        return this.list(wrapper);
    }

    /**
     * 根据空间ID查询文件列表
     */
    public List<KnowledgeFile> listBySpaceId(Long spaceId) {
        LambdaQueryWrapper<KnowledgeFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeFile::getSpaceId, spaceId)
                .eq(KnowledgeFile::getDeleteFlag, false)
                .orderByDesc(KnowledgeFile::getCreatedAt);
        return this.list(wrapper);
    }

    /**
     * 创建文件
     */
    public Boolean createKnowledgeFile(KnowledgeFile knowledgeFile) {
        // 检查文件名是否重复
        if (checkFileNameExists(knowledgeFile.getFileName(), knowledgeFile.getKnowledgeBaseId(), null)) {
            throw new RuntimeException("该知识库下已存在同名文件");
        }

        // 设置默认状态
        if (knowledgeFile.getStatus() == null) {
            knowledgeFile.setStatus(1); // 默认待处理
        }

        // 这里可以添加其他业务逻辑

        return this.save(knowledgeFile);
    }

    /**
     * 更新文件
     */
    public Boolean updateKnowledgeFile(KnowledgeFile knowledgeFile) {
        // 检查文件名是否重复（排除自己）
        if (org.springframework.util.StringUtils.hasText(knowledgeFile.getFileName()) &&
                checkFileNameExists(knowledgeFile.getFileName(), knowledgeFile.getKnowledgeBaseId(), knowledgeFile.getId())) {
            throw new RuntimeException("该知识库下已存在同名文件");
        }

        return this.updateById(knowledgeFile);
    }

    /**
     * 删除文件
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteKnowledgeFile(Long id) {
        // 检查是否有关联的分段
        if (hasRelatedSegments(id)) {
            throw new RuntimeException("该文件下还有分段，无法删除");
        }

        // 软删除
        KnowledgeFile knowledgeFile = getById(id);
        this.removeById(id);

        //调用向量的接口删除文件
        knowledgeFileSegmentService.remove(Wrappers.lambdaQuery(KnowledgeFileSegment.class).eq(KnowledgeFileSegment::getFileId, id));

        //删除知识库对应的字数和分段数
        knowledgeBaseMapper.updateSegmentAndWordCountForUpdate(knowledgeFile.getKnowledgeBaseId(),
                -knowledgeFile.getPageCount(), -knowledgeFile.getWordCount());
        return this.updateById(knowledgeFile);
    }

    /**
     * 检查文件名是否已存在
     */
    private boolean checkFileNameExists(String fileName, Long knowledgeBaseId, Long excludeId) {
        LambdaQueryWrapper<KnowledgeFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeFile::getFileName, fileName)
                .eq(KnowledgeFile::getKnowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeFile::getDeleteFlag, false)
                .ne(excludeId != null, KnowledgeFile::getId, excludeId);
        return this.count(wrapper) > 0;
    }

    /**
     * 检查是否有关联的分段
     */
    private boolean hasRelatedSegments(Long fileId) {
        // 这里可以注入KnowledgeFileSegmentService来检查
        // 为了简化，先返回false
        return false;
    }


    /**
     * 批量上传文件
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchCreateKnowledgeFile(List<KnowledgeFile> files) {
        // 批量验证
        for (KnowledgeFile file : files) {
            if (checkFileNameExists(file.getFileName(), file.getKnowledgeBaseId(), null)) {
                throw new RuntimeException("文件 " + file.getFileName() + " 已存在");
            }
            // 设置默认状态
            if (file.getStatus() == null) {
                file.setStatus(1); // 默认待处理
            }
        }

        return this.saveBatch(files);
    }

    /**
     * 创建知识库文件临时信息
     */
    public void createKnowledgeTempFile(KnowledgeTempDTO knowledgeTempDTO) {
        String key = "knowledge_temp_file:" + knowledgeTempDTO.getKnowledgeBaseId() + ":" + knowledgeTempDTO.getKnowledgeSpaceId() + ":" + SecurityUtils.getUserId();
        if (redisCache.hasKey(key)) {
            KnowledgeTempDTO cacheObject = redisCache.getCacheObject(key);
            List<String> fileMd5s = cacheObject.getKnowledgeFileUploadTempDTOList().stream().map(KnowledgeFileUploadTempDTO::getFileMd5).toList();
            List<KnowledgeFileUploadTempDTO> tempDTOS = knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().stream()
                    .filter(item -> !fileMd5s.contains(item.getFileMd5())).toList();
            List<KnowledgeFileUploadTempDTO> fileUploadTempDTOS = JSONArray.parseArray(JSONObject.toJSONString(cacheObject.getKnowledgeFileUploadTempDTOList()), KnowledgeFileUploadTempDTO.class);
            fileUploadTempDTOS.addAll(tempDTOS);
            knowledgeTempDTO.setKnowledgeFileUploadTempDTOList(fileUploadTempDTOS);
            redisCache.setCacheObject(key, knowledgeTempDTO);
        } else {
            redisCache.setCacheObject(key, knowledgeTempDTO);
        }
    }

    /**
     * 创建知识库文件临时信息
     */
    public void createKnowledgeTempFile(KnowledgeTempDTO knowledgeTempDTO, boolean deleteFlag) {
        String key = "knowledge_temp_file:" + knowledgeTempDTO.getKnowledgeBaseId() + ":" + knowledgeTempDTO.getKnowledgeSpaceId() + ":" + SecurityUtils.getUserId();
        redisCache.setCacheObject(key, knowledgeTempDTO);
    }

    public void updateKnowledgeTempFile(KnowledgeTempDTO knowledgeTempDTO) {
        String key = "knowledge_temp_file:" + knowledgeTempDTO.getKnowledgeBaseId() + ":" + knowledgeTempDTO.getKnowledgeSpaceId() + ":" + SecurityUtils.getUserId();
        KnowledgeTempDTO dto = redisCache.getCacheObject(key);
        Map<String, KnowledgeFileUploadTempDTO> map = dto.getKnowledgeFileUploadTempDTOList().stream()
                .collect(Collectors.toMap(KnowledgeFileUploadTempDTO::getFileMd5, item -> item));
        knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().forEach(item -> {
            if (map.containsKey(item.getFileMd5())) {
                KnowledgeFileUploadTempDTO tempDTO = map.get(item.getFileMd5());
                BeanUtils.copyProperties(item, tempDTO);
            }
        });
        redisCache.setCacheObject(key, dto);
    }

    /**
     * 获取知识库文件临时信息
     */
    public KnowledgeTempDTO getKnowledgeTempFile(Long knowledgeBaseId, Long spaceId, Long userId) {
        String key = "knowledge_temp_file:" + knowledgeBaseId + ":" + spaceId + ":" + userId;
        return redisCache.getCacheObject(key);
    }

    /**
     * 获取知识库文件临时信息
     */
    public KnowledgeTempDTO getKnowledgeTempFile(Long knowledgeBaseId, Long spaceId, Long userId, String createType) {
        String key = "knowledge_temp_file:" + knowledgeBaseId + ":" + spaceId + ":" + userId;
        if (!redisCache.hasKey(key)) {
            return null;
        }
        KnowledgeTempDTO cacheObject = redisCache.getCacheObject(key);
        List<KnowledgeFileUploadTempDTO> uploadTempDTOS = cacheObject.getKnowledgeFileUploadTempDTOList().stream()
                .filter(item -> createType.equals(item.getCreateType()))
                .toList();
        cacheObject.setKnowledgeFileUploadTempDTOList(uploadTempDTOS);
        return cacheObject;
    }

    public KnowledgeFileUploadTempDTO uploadKnowledgeFile(MultipartFile file, Long knowledgeBaseId, Long spaceId) {
        //先获取到文件名 文件大小 文件类型 文件md5 解析结果为 解析中 摘要获取中 标签获取中
        try {
            String fileUuid = IdUtil.fastSimpleUUID();
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            String fileType = FileUtil.getSuffix(file.getOriginalFilename());
            fileUuid += "." + fileType;
            String fileMd5 = DigestUtils.md5Hex(file.getBytes());
            Long userId = SecurityUtils.getUserId();

            // MD5校验 - 检查是否已存在相同文件
            KnowledgeTempDTO existingTempDTO = getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);
            if (existingTempDTO != null && existingTempDTO.getKnowledgeFileUploadTempDTOList() != null) {
                // 检查临时文件列表中是否已存在相同MD5的文件
                boolean md5Exists = existingTempDTO.getKnowledgeFileUploadTempDTOList().stream()
                        .anyMatch(tempFile -> fileMd5.equals(tempFile.getFileMd5()));
                if (md5Exists) {
                    throw new ServiceException("duplicate_file", "文件已存在（相同内容），请勿重复上传");
                }
            }

            // 检查正式文件表中是否已存在相同MD5的文件
            LambdaQueryWrapper<KnowledgeFile> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(KnowledgeFile::getKnowledgeBaseId, knowledgeBaseId)
                    .eq(KnowledgeFile::getDeleteFlag, false);

            // 保存原文件到本地
            String localSourcePath = saveFileToLocalSource(file, knowledgeBaseId, userId, fileUuid);

            KnowledgeFileUploadTempDTO knowledgeFileUploadTempDTO = new KnowledgeFileUploadTempDTO();
            knowledgeFileUploadTempDTO.setFileName(fileName);
            knowledgeFileUploadTempDTO.setFileSize(fileSize);
            knowledgeFileUploadTempDTO.setFileType(fileType);
            knowledgeFileUploadTempDTO.setFileMd5(fileMd5);
            knowledgeFileUploadTempDTO.setLocalSourcePath(localSourcePath);
//            knowledgeFileUploadTempDTO.setFileUuid(fileUuid);
            knowledgeFileUploadTempDTO.setCreateType("UPLOAD");
            knowledgeFileUploadTempDTO.setExtraMd(documentToMarkdownTool.convertToMdFile(localSourcePath));

            // 将文件复制到临时位置
            try {
                AtomicReference<String> s3PathRef = new AtomicReference<>(); // 使用AtomicReference
                try {
                    String s3Path = "wudao/kms/knowledge-base/" + knowledgeBaseId + knowledgeFileUploadTempDTO.getFileName();
                    // 上传S3
                    ossService.putObject(localSourcePath, s3Path);
                    s3PathRef.set(s3Path);
                    // 给这个文件生成一个外网可以访问的url
                    String url = ossService.getHttpUrl(s3Path);

                    // 判断文件超出10MB就不调用大模型解析
                    if (fileSize > 10 * 1024 * 1024) {
                        log.warn("文件 {} 超过10MB，跳过AI分析", fileName);
                        knowledgeFileUploadTempDTO.setParseResult("解析失败");
                        knowledgeFileUploadTempDTO.setSummary("文件过大，请手动添加摘要");
                        knowledgeFileUploadTempDTO.setTags("文件过大，请手动添加标签");
                        knowledgeFileUploadTempDTO.setS3Path(s3Path);
                        return knowledgeFileUploadTempDTO;
                    }

                    // 调用AI服务生成摘要和标签
//                    QwenAiService.AiAnalysisResult aiResult = qwenAiService.analyzeFile(url, fileName);

                    // 更新知识库文件临时信息
//                    String finalParseResult = "解析完成";
//                    String finalSummary = aiResult.getSummary();
//                    String finalTags = String.join(",", aiResult.getTags());

                    // 根据md5修改内容
//                    knowledgeFileUploadTempDTO.setParseResult(finalParseResult);
//                    knowledgeFileUploadTempDTO.setSummary(finalSummary);
//                    knowledgeFileUploadTempDTO.setTags(finalTags);
                    knowledgeFileUploadTempDTO.setS3Path(s3Path);
                    return knowledgeFileUploadTempDTO;

                } catch (Exception e) {
                    log.error("AI分析文件失败", e);
                    // AI分析失败时的处理
                    KnowledgeTempDTO errorKnowledgeTempDTO = getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);
                    if (errorKnowledgeTempDTO != null && errorKnowledgeTempDTO.getKnowledgeFileUploadTempDTOList() != null) {
                        List<KnowledgeFileUploadTempDTO> knowledgeFileUploadTempDTOList = errorKnowledgeTempDTO.getKnowledgeFileUploadTempDTOList();
                        knowledgeFileUploadTempDTOList.stream()
                                .filter(item -> item.getFileMd5().equals(fileMd5))
                                .findFirst()
                                .ifPresent(item -> {
                                    item.setParseResult("解析失败");
                                    item.setSummary("AI分析失败，请手动添加摘要");
                                    item.setTags("未分类");
                                    // 即使失败也要记录S3路径，方便后续重试
                                    if (item.getS3Path() == null && s3PathRef.get() != null) {
                                        item.setS3Path(s3PathRef.get());
                                    }
                                });
                        errorKnowledgeTempDTO.setKnowledgeFileUploadTempDTOList(knowledgeFileUploadTempDTOList);
                        createKnowledgeTempFile(errorKnowledgeTempDTO);
                    }
                }

            } catch (Exception e) {
                log.error("创建临时文件失败", e);
                throw new RuntimeException("文件上传失败", e);
            }

            return knowledgeFileUploadTempDTO;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 从URL上传知识文件
     *
     * @param fileUrl         文件的URL地址
     * @param fileName        文件名（如果为空则从URL中提取）
     * @param knowledgeBaseId 知识库ID
     * @param spaceId         空间ID
     * @return 上传的临时文件信息
     */
    public KnowledgeFileUploadTempDTO uploadKnowledgeFileFromUrl(String fileUrl, String fileName, Long knowledgeBaseId, Long spaceId) {
        try {
            // 验证URL
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                throw new ServiceException("invalid_url", "文件URL不能为空");
            }

            // 从URL下载文件
            FileDownloadResult downloadResult = downloadFileFromUrl(fileUrl, fileName);

            String fileUuid = IdUtil.fastSimpleUUID();
            String actualFileName = downloadResult.fileName();
            Long fileSize = downloadResult.fileSize();
            String fileType = FileUtil.getSuffix(actualFileName);
            fileUuid += "." + fileType;
            String fileMd5 = DigestUtils.md5Hex(downloadResult.fileContent());
            Long userId = SecurityUtils.getUserId();

            // MD5校验 - 检查是否已存在相同文件
            KnowledgeTempDTO existingTempDTO = getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);
            if (existingTempDTO != null && existingTempDTO.getKnowledgeFileUploadTempDTOList() != null) {
                // 检查临时文件列表中是否已存在相同MD5的文件
                boolean md5Exists = existingTempDTO.getKnowledgeFileUploadTempDTOList().stream()
                        .anyMatch(tempFile -> fileMd5.equals(tempFile.getFileMd5()));
                if (md5Exists) {
                    throw new ServiceException("duplicate_file", "文件已存在（相同内容），请勿重复上传");
                }
            }

            // 检查正式文件表中是否已存在相同MD5的文件
            LambdaQueryWrapper<KnowledgeFile> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(KnowledgeFile::getKnowledgeBaseId, knowledgeBaseId)
                    .eq(KnowledgeFile::getDeleteFlag, false);

            // 保存原文件到本地
            String localSourcePath = saveFileToLocalSourceFromUrl(downloadResult.fileContent(), knowledgeBaseId, userId, fileUuid);

            KnowledgeFileUploadTempDTO knowledgeFileUploadTempDTO = new KnowledgeFileUploadTempDTO();
            knowledgeFileUploadTempDTO.setFileName(actualFileName);
            knowledgeFileUploadTempDTO.setFileSize(fileSize);
            knowledgeFileUploadTempDTO.setFileType(fileType);
            knowledgeFileUploadTempDTO.setFileMd5(fileMd5);
            knowledgeFileUploadTempDTO.setLocalSourcePath(localSourcePath);
//            knowledgeFileUploadTempDTO.setFileUuid(fileUuid);

            // 将文件复制到临时位置
            try {
                AtomicReference<String> s3PathRef = new AtomicReference<>();
                try {
                    String s3Path = "wudao/kms/knowledge-base/" + knowledgeBaseId + knowledgeFileUploadTempDTO.getFileName();
                    // 上传S3
                    ossService.putObject(localSourcePath, s3Path);
                    s3PathRef.set(s3Path);
                    // 给这个文件生成一个外网可以访问的url
                    String url = ossService.getHttpUrl(s3Path);

                    // 调用AI服务生成摘要和标签
//                    QwenAiService.AiAnalysisResult aiResult = qwenAiService.analyzeFile(url, actualFileName);
//
//                    // 更新知识库文件临时信息
//                    String finalParseResult = "解析完成";
//                    String finalSummary = aiResult.getSummary();
//                    String finalTags = String.join(",", aiResult.getTags());
//
//                    // 根据md5修改内容
//                    knowledgeFileUploadTempDTO.setParseResult(finalParseResult);
//                    knowledgeFileUploadTempDTO.setSummary(finalSummary);
//                    knowledgeFileUploadTempDTO.setTags(finalTags);
//                    knowledgeFileUploadTempDTO.setS3Path(s3Path);
                    return knowledgeFileUploadTempDTO;

                } catch (Exception e) {
                    log.error("AI分析文件失败", e);
                    // AI分析失败时的处理
                    KnowledgeTempDTO errorKnowledgeTempDTO = getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);
                    if (errorKnowledgeTempDTO != null && errorKnowledgeTempDTO.getKnowledgeFileUploadTempDTOList() != null) {
                        List<KnowledgeFileUploadTempDTO> knowledgeFileUploadTempDTOList = errorKnowledgeTempDTO.getKnowledgeFileUploadTempDTOList();
                        knowledgeFileUploadTempDTOList.stream()
                                .filter(item -> item.getFileMd5().equals(fileMd5))
                                .findFirst()
                                .ifPresent(item -> {
                                    item.setParseResult("解析失败");
                                    item.setSummary("AI分析失败，请手动添加摘要");
                                    item.setTags("未分类");
                                    // 即使失败也要记录S3路径，方便后续重试
                                    if (item.getS3Path() == null && s3PathRef.get() != null) {
                                        item.setS3Path(s3PathRef.get());
                                    }
                                });
                        errorKnowledgeTempDTO.setKnowledgeFileUploadTempDTOList(knowledgeFileUploadTempDTOList);
                        createKnowledgeTempFile(errorKnowledgeTempDTO);
                    }
                }

            } catch (Exception e) {
                log.error("创建临时文件失败", e);
                throw new RuntimeException("文件上传失败", e);
            }

            return knowledgeFileUploadTempDTO;
        } catch (Exception e) {
            log.error("从URL上传文件失败: {}", fileUrl, e);
            throw new RuntimeException("从URL上传文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从URL下载文件（支持重试）
     */
    private FileDownloadResult downloadFileFromUrl(String fileUrl, String fileName) throws IOException {
        return downloadFileFromUrlInternal(fileUrl, fileName);
    }

    /**
     * 实际的下载逻辑
     */
    private FileDownloadResult downloadFileFromUrlInternal(String fileUrl, String fileName) throws IOException {
        URL url = URI.create(fileUrl).toURL();
        URLConnection connection = getUrlConnection(url);

        // 检查HTTP响应状态
        if (connection instanceof java.net.HttpURLConnection httpConnection) {

            // 设置自动重定向
            httpConnection.setInstanceFollowRedirects(true);

            int responseCode = httpConnection.getResponseCode();
            log.debug("URL {} 的响应状态码: {}", fileUrl, responseCode);

            // 处理重定向
            if (responseCode >= 300 && responseCode < 400) {
                String location = httpConnection.getHeaderField("Location");
                if (location != null) {
                    log.info("检测到重定向: {} -> {}", fileUrl, location);
                    // 递归调用处理重定向
                    return downloadFileFromUrlInternal(location, fileName);
                }
            }

            if (responseCode != 200) {
                String errorMessage = "HTTP请求失败，状态码: " + responseCode;
                if (responseCode == 403) {
                    errorMessage += " (访问被拒绝，可能需要认证或被反爬虫保护)";
                } else if (responseCode == 404) {
                    errorMessage += " (页面不存在)";
                } else if (responseCode == 429) {
                    errorMessage += " (请求过于频繁，被限流)";
                } else if (responseCode >= 500) {
                    errorMessage += " (服务器内部错误)";
                }
                throw new IOException(errorMessage);
            }
        }

        // 获取Content-Type用于早期HTML检测
        String contentType = connection.getContentType();
        log.debug("URL {} 的 Content-Type: {}", fileUrl, contentType);

        // 确定文件名
        String actualFileName = fileName;
        if (actualFileName == null || actualFileName.trim().isEmpty()) {
            // 从URL中提取文件名
            String urlPath = url.getPath();
            if (urlPath != null && urlPath.contains("/")) {
                actualFileName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
            }

            // 如果还是没有文件名，使用默认名称
            if (actualFileName == null || actualFileName.trim().isEmpty()) {
                actualFileName = "downloaded_file";
            }

            // 如果没有扩展名，尝试从Content-Type中获取
            if (!actualFileName.contains(".")) {
                if (contentType != null) {
                    if (contentType.contains("pdf")) {
                        actualFileName += ".pdf";
                    } else if (contentType.contains("word") || contentType.contains("msword")) {
                        actualFileName += ".docx";
                    } else if (contentType.contains("text/html") || contentType.contains("application/xhtml")) {
                        actualFileName += ".html";
                    } else if (contentType.contains("text")) {
                        actualFileName += ".txt";
                    }
                }
            }
        }

        // 提前检测是否为HTML内容（基于Content-Type和文件扩展名）
        String fileExtension = actualFileName.contains(".") ?
                actualFileName.substring(actualFileName.lastIndexOf(".") + 1).toLowerCase() : "";

        boolean isLikelyHtml = isLikelyHtmlContent(contentType, fileExtension);

        if (isLikelyHtml) {
            log.info("检测到可能的HTML内容，开始下载并解析网页：{}", fileUrl);

            // 读取HTML内容并处理压缩
            byte[] htmlContent = readAndDecompressContent(connection);

            // 最终确认是否为HTML内容
            if (isHtmlContent(contentType, fileExtension, htmlContent)) {
                // 处理HTML内容，提取文本和图片链接
                HtmlProcessResult htmlResult = processHtmlContent(htmlContent, fileUrl, actualFileName);

                // 将处理后的内容作为TXT文件返回
                byte[] processedContent = htmlResult.extractedContent().getBytes(StandardCharsets.UTF_8);
                String txtFileName = actualFileName.replaceAll("\\.(html?|htm)$", "") + "_extracted.txt";

                return new FileDownloadResult(txtFileName, processedContent, processedContent.length);
            } else {
                // 虽然疑似HTML但实际不是，按普通文件处理
                long fileSize = connection.getContentLengthLong();
                if (fileSize <= 0) {
                    fileSize = htmlContent.length;
                }
                return new FileDownloadResult(actualFileName, htmlContent, fileSize);
            }
        } else {
            log.debug("检测为非HTML内容，按普通文件处理：{}", fileUrl);

            // 获取文件大小
            long fileSize = connection.getContentLengthLong();

            // 读取文件内容并处理压缩
            byte[] fileContent = readAndDecompressContent(connection);

            // 如果无法从HTTP头获取文件大小，使用实际读取的大小
            if (fileSize <= 0) {
                fileSize = fileContent.length;
            }

            return new FileDownloadResult(actualFileName, fileContent, fileSize);
        }
    }

    private static @NotNull URLConnection getUrlConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();

        // 设置完整的浏览器请求头，模拟真实浏览器访问
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "identity");
        connection.setRequestProperty("DNT", "1");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("Sec-Fetch-Dest", "document");
        connection.setRequestProperty("Sec-Fetch-Mode", "navigate");
        connection.setRequestProperty("Sec-Fetch-Site", "none");
        connection.setRequestProperty("Cache-Control", "max-age=0");

        connection.setConnectTimeout(15000); // 15秒连接超时
        connection.setReadTimeout(60000); // 60秒读取超时
        return connection;
    }

    /**
     * 保存从URL下载的文件到本地source目录
     */
    private String saveFileToLocalSourceFromUrl(byte[] fileContent, Long knowledgeBaseId, Long userId, String fileName) {
        try {
            String localPath = buildLocalSourcePath(knowledgeBaseId, userId, fileName);
            File localFile = new File(localPath);

            // 确保目录存在
            FileUtil.mkdir(localFile.getParentFile());

            // 保存文件
            try (FileOutputStream fos = new FileOutputStream(localFile)) {
                fos.write(fileContent);
            }

            log.info("从URL下载的文件已保存到本地source目录: {}", localPath);
            return localPath;
        } catch (Exception e) {
            log.error("保存从URL下载的文件到本地source目录失败: knowledgeId={}, userId={}, fileName={}",
                    knowledgeBaseId, userId, fileName, e);
            throw new RuntimeException("保存文件到本地失败: " + e.getMessage());
        }
    }

    /**
     * 读取并解压缩内容
     */
    private byte[] readAndDecompressContent(URLConnection connection) throws IOException {
        String encoding = connection.getHeaderField("Content-Encoding");
        log.debug("Content-Encoding: {}", encoding);

        InputStream inputStream = connection.getInputStream();

        // 根据编码类型选择合适的解压缩方式
        if (encoding != null) {
            encoding = encoding.toLowerCase();
            if (encoding.contains("gzip")) {
                log.debug("使用GZIP解压缩");
                inputStream = new java.util.zip.GZIPInputStream(inputStream);
            } else if (encoding.contains("deflate")) {
                log.debug("使用DEFLATE解压缩");
                inputStream = new java.util.zip.InflaterInputStream(inputStream);
            }
            // br (brotli) 压缩暂时不处理，因为Java标准库不支持
        }

        try (InputStream stream = inputStream) {
            return stream.readAllBytes();
        }
    }

    /**
     * 基于Content-Type和文件扩展名进行初步HTML检测
     * 用于在下载前快速判断是否可能为HTML内容
     */
    private boolean isLikelyHtmlContent(String contentType, String fileExtension) {
        // 1. 根据Content-Type判断
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (lowerContentType.contains("text/html") || lowerContentType.contains("application/xhtml")) {
                return true;
            }
        }

        // 2. 根据文件扩展名判断
        return "html".equals(fileExtension) || "htm".equals(fileExtension);
    }

    /**
     * 检测是否为HTML内容（完整检测，包括内容分析）
     */
    private boolean isHtmlContent(String contentType, String fileExtension, byte[] fileContent) {
        // 1. 根据Content-Type判断
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (lowerContentType.contains("text/html") || lowerContentType.contains("application/xhtml")) {
                return true;
            }
        }

        // 2. 根据文件扩展名判断
        if ("html".equals(fileExtension) || "htm".equals(fileExtension)) {
            return true;
        }

        // 3. 根据文件内容特征判断（检查前1000个字符）
        if (fileContent.length > 0) {
            String contentSample = new String(fileContent, 0, Math.min(1000, fileContent.length), StandardCharsets.UTF_8);
            String lowerContent = contentSample.toLowerCase();

            // 检查HTML标记
            return lowerContent.contains("<!doctype html") ||
                    lowerContent.contains("<html") ||
                    (lowerContent.contains("<head") && lowerContent.contains("<body")) ||
                    (lowerContent.contains("<meta") && lowerContent.contains("charset"));
        }

        return false;
    }

    /**
     * 智能检测字符编码并转换
     */
    private String detectEncodingAndConvert(byte[] content) {
        try {
            // 先尝试UTF-8
            String utf8String = new String(content, StandardCharsets.UTF_8);

            // 检查是否包含乱码字符（替换字符）
            if (!utf8String.contains("\uFFFD")) {
                return utf8String;
            }

            // UTF-8失败，尝试其他常见编码
            String[] encodings = {"GBK", "GB2312", "ISO-8859-1", "Windows-1252"};
            for (String encoding : encodings) {
                try {
                    String result = new String(content, encoding);
                    if (!result.contains("\uFFFD")) {
                        log.debug("使用字符编码 {} 解码成功", encoding);
                        return result;
                    }
                } catch (Exception e) {
                    // 继续尝试下一个编码
                }
            }

            // 所有编码都失败，返回UTF-8结果
            log.warn("无法确定正确的字符编码，使用UTF-8");
            return utf8String;

        } catch (Exception e) {
            log.error("字符编码检测失败", e);
            return new String(content, StandardCharsets.UTF_8);
        }
    }

    /**
     * 递归提取内容和图片，将它们混合在一起
     */
    private void extractContentWithImages(org.jsoup.nodes.Element element, StringBuilder result, String baseUrl) {
        for (org.jsoup.nodes.Node node : element.childNodes()) {
            if (node instanceof org.jsoup.nodes.TextNode) {
                // 处理文本节点
                String text = ((org.jsoup.nodes.TextNode) node).text().trim();
                if (!text.isEmpty()) {
                    result.append(text).append(" ");
                }
            } else if (node instanceof org.jsoup.nodes.Element childElement) {

                if (childElement.tagName().equals("img")) {
                    // 处理图片元素，使用HTML img标签格式
                    String imgSrc = childElement.absUrl("src");
                    if (!imgSrc.trim().isEmpty()) {
                        String imgAlt = childElement.attr("alt");
                        // 生成图片hash用于alt属性
                        String imageHash = Integer.toHexString(imgSrc.hashCode());

                        // 使用HTML img标签格式
                        String imgTag = String.format("<img src=\"%s\" alt=\"web_img_%s\"/>",
                                imgSrc,
                                imageHash);

                        result.append("\n").append(imgTag).append("\n");
                    }
                } else if (childElement.tagName().equals("p") ||
                        childElement.tagName().equals("div") ||
                        childElement.tagName().equals("br")) {
                    // 段落分隔
                    extractContentWithImages(childElement, result, baseUrl);
                    if (childElement.tagName().equals("p") || childElement.tagName().equals("br")) {
                        result.append("\n");
                    }
                } else if (childElement.tagName().equals("h1") ||
                        childElement.tagName().equals("h2") ||
                        childElement.tagName().equals("h3") ||
                        childElement.tagName().equals("h4") ||
                        childElement.tagName().equals("h5") ||
                        childElement.tagName().equals("h6")) {
                    // 标题
                    result.append("\n\n").append(childElement.text().trim()).append("\n");
                } else {
                    // 其他元素，递归处理
                    extractContentWithImages(childElement, result, baseUrl);
                }
            }
        }
    }

    /**
     * 处理HTML内容，提取文本和图片链接
     */
    private HtmlProcessResult processHtmlContent(byte[] htmlContent, String sourceUrl, String fileName) {
        try {
            // 智能检测字符编码
            String htmlString = detectEncodingAndConvert(htmlContent);
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(htmlString, sourceUrl);

            StringBuilder extractedText = new StringBuilder();

            // 移除脚本和样式标签
            doc.select("script, style, nav, footer, aside").remove();

            // 提取正文内容容器，优先从main、article、content等标签中提取
            org.jsoup.select.Elements mainContent = doc.select("main, article, .content, .post-content, .entry-content");
            org.jsoup.nodes.Element contentElement;

            if (!mainContent.isEmpty()) {
                contentElement = mainContent.first();
            } else {
                // 如果没找到主要内容区域，使用body
                doc.body();
                contentElement = doc.body();
            }

            // 遍历内容元素，提取文本和图片
            if (contentElement != null) {
                extractContentWithImages(contentElement, extractedText, sourceUrl);
            }

            // 统计图片数量
            org.jsoup.select.Elements allImages = doc.select("img[src]");
            int imageCount = 0;
            for (org.jsoup.nodes.Element img : allImages) {
                if (!img.absUrl("src").trim().isEmpty()) {
                    imageCount++;
                }
            }

            return new HtmlProcessResult(extractedText.toString().trim(), imageCount, 0);

        } catch (Exception e) {
            log.error("处理HTML内容失败: {}", fileName, e);
            return new HtmlProcessResult(
                    "HTML内容解析失败: " + e.getMessage() + "\n原始URL: " + sourceUrl,
                    0, 0
            );
        }
    }

    /**
     * HTML处理结果
     */
    private record HtmlProcessResult(String extractedContent, int imageCount, int linkCount) {

    }

    /**
     * 文件下载结果
     */
    private record FileDownloadResult(String fileName, byte[] fileContent, long fileSize) {

    }

    public boolean audioFlag(String fileName) {
        String suffix = FileUtil.getSuffix(fileName);
        if ("wav".equalsIgnoreCase(suffix)
                || "mp3".equalsIgnoreCase(suffix)
                || "mp4".equalsIgnoreCase(suffix)) {
            return true;
        }
        return false;
    }

    /**
     * 使用ffmpeg将mp4转换成wav格式
     *
     * @param mp4Path mp4文件路径
     * @param fileMd5 文件MD5
     * @return wav文件路径
     */
    private String convertMp4ToWav(String mp4Path, String fileMd5) {
        try {
            // 生成输出文件路径
            String outputDir = "/uploadPath/uploads/audio/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss);
            FileUtil.mkdir(outputDir);
            String wavPath = outputDir + "/" + fileMd5 + ".wav";

            // 构建ffmpeg命令
            // -i: 输入文件
            // -vn: 不处理视频
            // -acodec pcm_s16le: 音频编码格式
            // -ar 16000: 采样率16kHz
            // -ac 1: 单声道
            // -y: 覆盖已存在的文件
            String[] command = {
                    "ffmpeg",
                    "-i", mp4Path,
                    "-vn",
                    "-acodec", "pcm_s16le",
                    "-ar", "16000",
                    "-ac", "1",
                    "-y",
                    wavPath
            };

            // 执行命令
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                // 读取错误信息
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    StringBuilder errorMsg = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        errorMsg.append(line).append("\n");
                    }
                    log.error("ffmpeg转换失败: {}", errorMsg);
                }
                throw new RuntimeException("ffmpeg转换mp4到wav失败,退出码: " + exitCode);
            }

            log.info("mp4转wav成功: {} -> {}", mp4Path, wavPath);
            return wavPath;

        } catch (Exception e) {
            log.error("转换mp4到wav失败", e);
            throw new RuntimeException("转换mp4到wav失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理文档分段（支持SSE进度推送）
     * 根据前端传过来的临时文件列表，处理文档分段并通过SSE推送进度
     *
     * @param knowledgeTempDTO 包含临时文件列表和分段配置的DTO
     */
    public void processDocumentSegmentationWithProgress(KnowledgeTempDTO knowledgeTempDTO) throws Exception {
        String key = "knowledge_temp_file:" + knowledgeTempDTO.getKnowledgeBaseId() + ":" + knowledgeTempDTO.getKnowledgeSpaceId() + ":" + knowledgeTempDTO.getUserId();
        // 判断没有打开高级设置
        Boolean changeFlag = knowledgeTempDTO.getKnowLedgeSegmentConfig().getExtraChangeFlag();
        for (KnowledgeFileUploadTempDTO file : knowledgeTempDTO.getKnowledgeFileUploadTempDTOList()) {// 将配置映射到接口参数
            Integer maxTokens = knowledgeTempDTO.getKnowLedgeSegmentConfig().getCustomSegmentSize();      // 对应 curl 的 max_tokens
            Integer overlap = knowledgeTempDTO.getKnowLedgeSegmentConfig().getCustomSegmentOverlap();   // 对应 curl 的 overlap
            String customSeps = knowledgeTempDTO.getKnowLedgeSegmentConfig().getCustomSegmentType();      // 这里作为 custom_separators 传；没有就传空
            String segmentType = knowledgeTempDTO.getKnowLedgeSegmentConfig().getSegmentType();          // 分段类型：paragraph/contentSize/symbol
            Integer maxParagraph = knowledgeTempDTO.getKnowLedgeSegmentConfig().getMaxParagraph();       // 最大段落深度
            if (StringUtil.isEmpty(file.getS3Path())) {
                String s3Input = String.format("wudao/mineru/%s/input/%s", file.getFileMd5(), file.getFileName());
                ossService.putObject(file.getLocalSourcePath(), s3Input);
                file.setS3Path(s3Input);
            }
            if (changeFlag) {
                if (knowledgeTempDTO.getKnowLedgeSegmentConfig().getPdfIncrease() && !audioFlag(file.getFileName())) {
                    String extraMdPath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
                    if (knowledgeTempDTO.getKnowLedgeSegmentConfig().getPdfIncrease()) {
                        try {
                            // 使用 Docker 离线版本执行 MinerU 解析
                            String outputDir = basicProfile + "/mineru_output/" + file.getFileMd5();
                            String resultMdPath = executeMinerUDocker(file.getLocalSourcePath(), outputDir, file.getFileName());

                            // 处理图片并替换路径（上传到 OSS）
                            String processedContent = processDockerMinerUResult(resultMdPath, outputDir, file.getFileName());

                            // 保存处理后的 markdown
                            FileUtil.writeUtf8String(processedContent, extraMdPath);
                            file.setExtraMd(extraMdPath);
                        } catch (Exception e) {
                            log.error("Docker MinerU 解析失败", e);
                        }
                    }
                } else {
                    if (audioFlag(file.getFileName())) {
                        // 如果是mp4格式,需要先转换成wav格式
                        String audioUrl;
                        String suffix = FileUtil.getSuffix(file.getFileName());
                        if ("mp4".equalsIgnoreCase(suffix)) {
                            // 使用ffmpeg将mp4转换成wav
                            String wavPath = convertMp4ToWav(file.getLocalSourcePath(), file.getFileMd5());
                            // 上传wav文件到OSS
                            String wavS3Path = "wudao/kms/audio/" + file.getFileMd5() + ".wav";
                            ossService.putObject(wavPath, wavS3Path);
                            audioUrl = ossService.getHttpUrl(wavS3Path);
                            // 删除临时wav文件
                            FileUtil.del(wavPath);
                        } else {
                            audioUrl = ossService.getHttpUrl(file.getS3Path());
                        }
                        // 查询对应的语音模型
                        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeTempDTO.getKnowledgeBaseId());
                        LLMModel llmModel = llmModelService.queryLLMByModel(knowledgeBase.getAudioModel());
                        String asr = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode()).asr(audioUrl, knowledgeTempDTO.getUserId(), llmModel.getModel());
                        String audioTxt = "/uploadPath/uploads/fileMd5/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss)
                                + "/" + FileUtil.getPrefix(file.getFileName()) + ".txt";
                        FileUtil.writeUtf8String(asr, audioTxt);
                        file.setExtraMd(documentToMarkdownTool.convertToMdFile(audioTxt));
                    } else {
                        file.setExtraMd(documentToMarkdownTool.convertToMdFile(file.getLocalSourcePath()));
                    }
                }


                // 删除原有分段数据
                LambdaQueryWrapper<KnowledgeFileSegment> deleteWrapper = new LambdaQueryWrapper<>();
                deleteWrapper.eq(KnowledgeFileSegment::getKnowledgeBaseId, knowledgeTempDTO.getKnowledgeBaseId());
                deleteWrapper.eq(KnowledgeFileSegment::getKnowledgeSpaceId, knowledgeTempDTO.getKnowledgeSpaceId());
                deleteWrapper.eq(KnowledgeFileSegment::getFileMd5, file.getFileMd5());
                deleteWrapper.isNull(KnowledgeFileSegment::getFileId);
                knowledgeFileSegmentService.remove(deleteWrapper);

                MarkdownSplitService.SplitResponse splitResponse = markdownSplitService.splitMarkdownFromContent(
                        FileUtil.readUtf8String(file.getExtraMd()), maxTokens, overlap, customSeps, segmentType, maxParagraph);

                String localSegmentPath = buildLocalSegmentPath(knowledgeTempDTO.getKnowledgeBaseId(), knowledgeTempDTO.getUserId(),
                        file.getFileMd5() + ".json");
                FileUtil.writeUtf8String(JSONObject.toJSONString(splitResponse), localSegmentPath);
                // 存储到数据库
                JSONArray ragResult = JSONArray.parseArray(JSONObject.toJSONString(splitResponse.getChunks()));
                int wordCount = 0;
                List<KnowledgeFileSegment> segments = new ArrayList<>();
                for (int i = 1; i <= ragResult.size(); i++) {
                    Object item = ragResult.get(i - 1);
                    if (item instanceof JSONObject jsonObject) {
                        String content = jsonObject.getString("content");
                        KnowledgeFileSegment segment = new KnowledgeFileSegment();
                        segment.setSegmentIndex(i);
                        segment.setKnowledgeBaseId(knowledgeTempDTO.getKnowledgeBaseId());
                        segment.setKnowledgeSpaceId(knowledgeTempDTO.getKnowledgeSpaceId());
                        segment.setFileMd5(file.getFileMd5());
                        content = cleanTextComprehensive(content);
                        segment.setSegmentContent(content);
                        wordCount += content.length();
                        segments.add(segment);
                    }
                }
                knowledgeFileSegmentService.saveBatch(segments);
                file.setSegmentedDocPath(localSegmentPath);
                file.setContentCount(wordCount);
                file.setSegmentCount(segments.size());
            }
            redisCache.setCacheObject(key, knowledgeTempDTO);
        }
    }

    public static String cleanTextComprehensive(String text) {
        if (text == null) {
            return null;
        }

        // 1. 移除空字节
        text = text.replace("\u0000", "");

        // 2. 移除其他常见问题字符
        text = text.replace("\uFFFD", ""); // 替换字符（�）
        text = text.replace("\u00A0", " "); // 不间断空格替换为普通空格

        // 3. 移除所有其他控制字符（保留换行、制表符）
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        // 4. 规范化多余空白
        text = text.replaceAll("[ \t]+", " "); // 多个空格/制表符合并为一个
        text = text.replaceAll("\n{3,}", "\n\n"); // 多个换行最多保留2个

        return text.trim();
    }

    /**
     * 清理文本中的无效UTF8字符
     * 移除空字节(0x00)和其他可能导致数据库编码错误的字符
     *
     * @param originalText 需要清理的文本
     * @return 清理后的文本
     */
    private String cleanTextForDatabase(String originalText) {
        if (originalText == null) {
            return null;
        }

        // 移除空字节和其他控制字符，但保留换行符和制表符
        String cleanedText = originalText.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
                .trim();

        // 如果文本被修改了，记录日志
        if (!originalText.equals(cleanedText)) {
            log.debug("文本清理：移除了无效字符，原长度: {}, 清理后长度: {}",
                    originalText.length(), cleanedText.length());
        }

        return cleanedText;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchCreateKnowledgeFilesWithIds(KnowledgeTempDTO knowledgeTempDTO) {

        List<KnowledgeFile> knowledgeFiles = new ArrayList<>();

        for (KnowledgeFileUploadTempDTO tempFile : knowledgeTempDTO.getKnowledgeFileUploadTempDTOList()) {
            KnowledgeFile knowledgeFile = new KnowledgeFile();
            BeanUtils.copyProperties(tempFile, knowledgeFile);
            knowledgeFile.setId(tempFile.getFileId());
            knowledgeFile.setSpaceId(knowledgeTempDTO.getKnowledgeSpaceId());
            String filePath = basicProfile + "/knowledgeFile/" + knowledgeTempDTO.getKnowledgeBaseId()
                    + "/" + knowledgeTempDTO.getKnowledgeSpaceId() + "/" + knowledgeFile.getFileMd5() + "." + FileUtil.getSuffix(tempFile.getFileName());
            FileUtil.move(new File(tempFile.getLocalSourcePath()), new File(filePath), true);
            knowledgeFile.setFilePath(filePath);
            // 生成封面
            String coverPath = coverGenerationService.generateCover(knowledgeFile, tempFile.getExtraMd());
            knowledgeFile.setCoverPath(cleanTextForDatabase(coverPath));
            knowledgeFile.setSegmentedDocPath(cleanTextForDatabase(tempFile.getSegmentedDocPath()));
            knowledgeFile.setStatus(2); // 处理中
            if (knowledgeTempDTO.getKnowLedgeSegmentConfig().getQaExtract()) {
                knowledgeFile.setChunkType("qaExtract");
            } else {
                knowledgeFile.setChunkType("chunk");
            }
            if (knowledgeTempDTO.getKnowLedgeSegmentConfig().getQaExtract()) {
                knowledgeFile.setExtraType("QA");
            } else {
                knowledgeFile.setExtraType("SEGMENT");
            }
            knowledgeFile.setProcessingStatus(cleanTextForDatabase("处理中"));
            knowledgeFile.setUploadTime(LocalDateTime.now());
            knowledgeFile.setPageCount(tempFile.getSegmentCount() == null ? 0 : tempFile.getSegmentCount());
            knowledgeFile.setWordCount(tempFile.getContentCount() == null ? 0 : tempFile.getContentCount());
            knowledgeFile.setPageCount(tempFile.getSegmentCount());
            knowledgeFile.setWordCount(tempFile.getContentCount());
            knowledgeFile.setProcessTime(LocalDateTime.now());
            knowledgeFile.setDeleteFlag(false);
            knowledgeFile.setFileType(FileUtil.getSuffix(tempFile.getFileName()));
            knowledgeFile.setUuid(NanoId.randomNanoId(10));
            knowledgeFile.setFileMd5(cleanTextForDatabase(tempFile.getFileMd5()));
            knowledgeFile.setExtraMd(tempFile.getExtraMd());
            knowledgeFile.setKnowledgeBaseId(knowledgeTempDTO.getKnowledgeBaseId());
            knowledgeFile.setOssPath(tempFile.getS3Path());
            knowledgeFile.setKnowledgeSegmentConfig(knowledgeTempDTO.getKnowLedgeSegmentConfig());
            knowledgeFiles.add(knowledgeFile);
        }

        this.saveBatch(knowledgeFiles);

        Long userId = SecurityUtils.getUserId();
        Threads.newThread(() -> {
            try {
                //清除缓存，防止用户重复上传文件
                String key = "knowledge_temp_file:" + knowledgeTempDTO.getKnowledgeBaseId() + ":" + knowledgeTempDTO.getKnowledgeSpaceId() + ":" + userId;
                KnowledgeTempDTO cacheObject = redisCache.getCacheObject(key);
                // 把传过来的内容做一个处理，
                List<KnowledgeFileUploadTempDTO> saveList = cacheObject.getKnowledgeFileUploadTempDTOList().stream()
                        .filter(item -> !item.getCreateType().equals(knowledgeTempDTO.getCreateType()))
                        .toList();
                if (CollUtil.isEmpty(saveList)) {
                    redisCache.deleteObject(key);
                } else {
                    cacheObject.setKnowledgeFileUploadTempDTOList(saveList);
                    redisCache.setCacheObject(key, cacheObject);
                }
                // 拿到这次批量上传的总段落数和总字数
                int totalSegments = knowledgeFiles.stream().filter(item -> item.getPageCount() != null).mapToInt(KnowledgeFile::getPageCount).sum();
                int totalWords = knowledgeFiles.stream().filter(item -> item.getWordCount() != null).mapToInt(KnowledgeFile::getWordCount).sum();
                // 更新知识库的总段落数和总字数
                knowledgeBaseMapper.updateSegmentAndWordCountForUpdate(
                        knowledgeTempDTO.getKnowledgeBaseId(),
                        totalSegments,
                        totalWords
                );
                //将标签提取出来，更新知识库
                List<String> tagsList = knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().stream().map(KnowledgeFileUploadTempDTO::getTags).toList();
                //合并标签,去重复，然后存储到知识库实体中
                String tags = tagsList.stream().filter(Objects::nonNull).map(String::trim).filter(tag -> !tag.isEmpty()).flatMap(tag -> Arrays.stream(tag.split(","))).map(java.lang.String::trim).filter(tag -> !tag.isEmpty()).distinct().collect(Collectors.joining(","));
                log.info("合并后的标签: {}", tags);
                //更新知识库的标签
                KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeTempDTO.getKnowledgeBaseId());
                if (knowledgeBase != null) {
                    knowledgeBase.setTags(tags);
                    knowledgeBaseMapper.updateById(knowledgeBase);
                }

                summary(knowledgeTempDTO.getKnowledgeBaseId(), knowledgeFiles, knowledgeTempDTO.getKnowLedgeSegmentConfig());

                vector(knowledgeFiles, knowledgeTempDTO.getKnowLedgeSegmentConfig(), knowledgeTempDTO.getKnowledgeBaseId(),
                        knowledgeTempDTO.getKnowledgeSpaceId(), userId);


            } catch (Exception e) {
                log.error("批量新增知识文件失败，知识库ID: {}, 文件数量: {}",
                        knowledgeTempDTO.getKnowledgeBaseId(),
                        knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().size(), e);

                // 如果是UTF8编码错误，提供更详细的错误信息
                if (e.getMessage() != null && e.getMessage().contains("invalid byte sequence for encoding")) {
                    log.error("检测到UTF8编码错误，可能存在无效字符。错误详情: {}", e.getMessage());
                    throw new RuntimeException("文件内容包含无效字符，请检查文件编码格式: " + e.getMessage());
                }
                throw new RuntimeException("批量新增知识文件失败: " + e.getMessage());
            }
        }).start();
        // 提取保存后生成的ID列表
        return knowledgeFiles.stream()
                .map(KnowledgeFile::getId)
                .collect(Collectors.toList());
    }

    /**
     * 用于提取文件的总结和标签
     *
     * @param knowledgeFiles 知识库文件集合
     */
    private void summary(Long knowledgeBaseId, List<KnowledgeFile> knowledgeFiles, KnowLedgeSegmentConfig knowLedgeSegmentConfig) {
        if (!knowLedgeSegmentConfig.getSummary() && !knowLedgeSegmentConfig.getTags()) {
            return;
        }
        knowledgeFiles.stream().filter(item -> StringUtil.isNotEmpty(item.getExtraMd())).forEach(item -> {
            try {
                ChatModelStrategyFactory modelStrategy = SpringUtil.getBean(ChatModelStrategyFactory.class);

                String systemPrompt = """
                        你是一个总结专家，帮用户发送过来的文件进行总结和得到三个标签，希望得到是json不是md格式，例子为：
                        {
                            "summary":"这边文章是干嘛的",
                            "tags":["tags1","tags2"]
                        }
                        """;
                LLMModel textModel = llmModelService.queryTextModelByKnowledgeId(knowledgeBaseId);
                ChatModelStrategy strategy = modelStrategy.getStrategy(textModel.getProviderCode());
                String result = strategy.simpleChat(textModel.getModel(), systemPrompt, FileUtil.readUtf8String(item.getExtraMd()), true);
                result = result.replaceAll("```", "").replaceAll("json", "");
                JSONObject jsonObject = JSONObject.parseObject(result);
                String summary = jsonObject.getString("summary");
                if (knowLedgeSegmentConfig.getSummary()) {
                    item.setSummary(summary);
                }
                List<String> tags = jsonObject.getJSONArray("tags").toList(String.class, JSONReader.Feature.values());
                if (knowLedgeSegmentConfig.getTags()) {
                    item.setTags(CollUtil.join(tags, ","));
                }
            } catch (Exception e) {
                log.error("调用总结异常", e);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void vector(List<KnowledgeFile> knowledgeFiles, KnowLedgeSegmentConfig config, Long knowledgeBaseId, Long knowledgeSpaceId, Long userId) {

        knowledgeFiles.forEach(item -> {
            try {
                // 重置所有进度为0
                knowledgeFileProgressService.resetAllProgress(item.getId());

                // 更新knowledge_file_segment 里面的file_id
                LambdaUpdateWrapper<KnowledgeFileSegment> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(KnowledgeFileSegment::getKnowledgeBaseId, item.getKnowledgeBaseId());
                wrapper.eq(KnowledgeFileSegment::getKnowledgeSpaceId, item.getSpaceId());
                wrapper.eq(KnowledgeFileSegment::getFileMd5, item.getFileMd5());
                wrapper.set(KnowledgeFileSegment::getFileId, item.getId());
                knowledgeFileSegmentService.update(wrapper);


                // 查询所有的分段
                List<KnowledgeFileSegment> fileSegments =
                        knowledgeFileSegmentService.list(Wrappers.lambdaQuery(KnowledgeFileSegment.class)
                                .eq(KnowledgeFileSegment::getFileId, item.getId()).orderByAsc(KnowledgeFileSegment::getSegmentIndex));

                int totalSegments = fileSegments.size();

                // ==================== 阶段1: VL处理 ====================
                if (config.getVlFlag()) {
                    LLMModel vlModel = llmModelService.queryVlModelByKnowledgeId(knowledgeBaseId);
                    ChatModelStrategy vlStrategy = chatModelStrategyFactory.getStrategy(vlModel.getProviderCode());
                    //要判断是否有图片
                    Pattern imagePattern = Pattern.compile("!\\[.*?\\]\\(.*?\\)");

                    int processedCount = 0;

                    // 遍历所有段落,如果有图片就处理,没有就直接计数加一
                    for (KnowledgeFileSegment content : fileSegments) {
                        // 检查是否包含图片
                        if (imagePattern.matcher(content.getSegmentContent()).find()) {
                            // 拿到这个段落里面的所有图片然后交给qwen-vl-plus进行解析，识别出来的内容追加到content，然后统一进行向量化
                            List<String> imageUrls = extractImageUrlsFromMarkdown(content.getSegmentContent());
                            List<Media> mediaList = ChangeMediaTool.convertUrlsToMedia(imageUrls);
                            // 这里可以进一步处理imageUrls，比如调用qwen-vl-plus进行图片识别
                            String vlContent = vlStrategy.vl(vlModel.getModel(), "请直接输出分析图片的内容", mediaList);
                            content.setVlContent(vlContent);
                        }

                        // 无论是否有图片,每处理完一个段落就更新进度
                        processedCount++;
                        knowledgeFileProgressService.updateVlProgress(item.getId(), processedCount);
                        log.debug("VL处理进度: fileId={}, progress={}/{}",
                                item.getId(), processedCount, totalSegments);
                    }

                    // VL处理完成，确保更新为总数
                    knowledgeFileProgressService.updateVlProgress(item.getId(), totalSegments);
                }
                // 如果不需要VL处理，保持为0（不更新）
                // ==================== 阶段2: QA提取 ====================
                if (config.getQaExtract()) {
                    LLMModel textModel = llmModelService.queryTextModelByKnowledgeId(knowledgeBaseId);
                    ChatModelStrategy textStrategy = chatModelStrategyFactory.getStrategy(textModel.getProviderCode());
                    // 使用大模型提取qa内容
                    String qaPrompt = config.getQaExtractPrompt() + """
                            生成的问题和答案请以纯JSON内容比如:[{"q":"问题","a":"答案"}]
                            """;
                    List<KnowledgeFileSegment> qaSegment = new ArrayList<>();

                    // 使用AtomicInteger来在异步任务中追踪进度
                    final int totalQaSegments = fileSegments.size();
                    final java.util.concurrent.atomic.AtomicInteger qaProcessedCount = new java.util.concurrent.atomic.AtomicInteger(0);

                    // 异步处理每个分段的QA提取
                    List<CompletableFuture<Void>> futures = fileSegments.stream()
                            .map(qa -> CompletableFuture.runAsync(() -> {
                                try {
                                    String segmentContent = textStrategy.simpleChat(textModel.getModel(), qaPrompt, qa.getSegmentContent(), true);
                                    // 清除markdown格式
                                    segmentContent = segmentContent.replace("```json", "").replace("```", "");
                                    JSONArray jsonArray = JSONArray.parseArray(segmentContent);
                                    jsonArray.forEach(segment -> {
                                        JSONObject jsonObject = (JSONObject) segment;
                                        String q = jsonObject.getString("q");
                                        String a = jsonObject.getString("a");
                                        qaSegment.add(KnowledgeFileSegment.builder()
                                                .knowledgeBaseId(knowledgeBaseId)
                                                .knowledgeSpaceId(knowledgeSpaceId)
                                                .fileId(item.getId())
                                                .title(q)
                                                .segmentContent(a).build());
                                    });

                                    // 每处理完一个段落，更新QA进度
                                    int processed = qaProcessedCount.incrementAndGet();
                                    knowledgeFileProgressService.updateQaProgress(item.getId(), processed);
                                } catch (Exception e) {
                                    // 即使失败也要更新进度
                                    int processed = qaProcessedCount.incrementAndGet();
                                    knowledgeFileProgressService.updateQaProgress(item.getId(), processed);
                                }
                            }))
                            .toList();
                    // 等待所有异步任务完成
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                    // QA提取完成，确保更新为总数
                    knowledgeFileProgressService.updateQaProgress(item.getId(), totalQaSegments);

                    // 移除之前产生的分段，然后把得到qa的结果放入新分段
                    knowledgeFileSegmentService.remove(Wrappers.lambdaQuery(KnowledgeFileSegment.class).eq(KnowledgeFileSegment::getFileId, item.getId()));
                    knowledgeFileSegmentService.saveBatch(qaSegment);
                    fileSegments.clear();
                    fileSegments.addAll(qaSegment);
                }
                // 如果不需要QA提取，保持为0（不更新）
                // ==================== 阶段3: 向量化 ====================
                if (config.getQaExtract()) {
                    // 存储到对应的QA库
                    List<QaImprove> qaImproveList = fileSegments.stream()
                            .map(chunk -> QaImprove.builder()
                                    .question(chunk.getTitle())
                                    .knowledgeId(knowledgeBaseId)
                                    .knowledgeSpaceId(knowledgeSpaceId)
                                    .answer(chunk.getSegmentContent())
                                    .generateType("extra")
                                    .createBy(userId)
                                    .build())
                            .toList();
                    qaImproveService.addBatch(qaImproveList, knowledgeBaseId);

                    // QA提取完成后，向量化进度直接设置为QA的数量
                    knowledgeFileProgressService.updateVectorProgress(item.getId(), fileSegments.size());
                } else {
                    // 普通分段需要向量化
                    List<String> contents = fileSegments.stream()
                            .map(content -> content.getSegmentContent() + content.getVlContent())
                            .toList();

                    if (CollUtil.isNotEmpty(contents)) {
                        // 通过文件id找到对应知识库的向量模型
                        LLMModel embeddingModel = llmModelService.queryEmbeddingModelByKnowledgeId(knowledgeBaseId);
                        ChatModelStrategy embeddingStrategy = chatModelStrategyFactory.getStrategy(embeddingModel.getProviderCode());

                        // 执行向量化（批量处理）
                        int totalContents = contents.size();

                        // 开始向量化
                        List<float[]> embeddings = embeddingStrategy.embedding(embeddingModel.getModel(), contents);

                        // 更新分段的向量
                        for (int i = 0; i < fileSegments.size(); i++) {
                            fileSegments.get(i).setVector(embeddings.get(i));
                            fileSegments.get(i).setVectorFlag(true);
                        }

                        // 批量更新到数据库
                        knowledgeFileSegmentService.updateBatchById(fileSegments);

                        // 向量化完成，更新为总数
                        knowledgeFileProgressService.updateVectorProgress(item.getId(), totalContents);
                    }
                    // 如果没有内容需要向量化，保持为0（不更新）
                }

                // 所有处理完成
                item.setStatus(3); // 设置为已可用状态
                item.setProcessingStatus("处理完成");
            } catch (Exception e) {
                log.error("处理向量", e);
                item.setStatus(4); // 设置为处理失败状态
                item.setProcessingStatus("处理失败");
            }
        });
        this.updateBatchById(knowledgeFiles);
    }

    /**
     * 批量新增知识文件并通过Flux实时推送进度
     * 将临时文件信息保存到正式的knowledge_file表中，通过响应式流实时推送处理进度
     *
     * @param knowledgeTempDTO 包含临时文件列表的DTO
     * @return 进度推送的Flux流
     */
    public Flux<String> batchCreateKnowledgeFilesWithProgress(KnowledgeTempDTO knowledgeTempDTO) {
        return Flux.create(sink -> {
            try {
                // 清除临时文件信息
                String key = "knowledge_temp_file:" + knowledgeTempDTO.getKnowledgeBaseId() + ":" + knowledgeTempDTO.getKnowledgeSpaceId() + ":" + SecurityUtils.getUserId();
                redisCache.deleteObject(key);

                sink.next("data: {\"type\":\"start\",\"message\":\"开始批量创建文件\",\"progress\":0}\n\n");

                List<KnowledgeFile> knowledgeFiles = new ArrayList<>();
                int totalFiles = knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().size();

                // 构建文件对象
                for (int i = 0; i < knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().size(); i++) {
                    KnowledgeFileUploadTempDTO tempFile = knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().get(i);

                    KnowledgeFile knowledgeFile = new KnowledgeFile();
                    knowledgeFile.setKnowledgeBaseId(knowledgeTempDTO.getKnowledgeBaseId());
                    knowledgeFile.setSpaceId(knowledgeTempDTO.getKnowledgeSpaceId());
                    knowledgeFile.setFileName(tempFile.getFileName());
                    knowledgeFile.setOriginalName(tempFile.getFileName());
                    knowledgeFile.setFilePath(tempFile.getS3Path());
                    knowledgeFile.setFileSize(tempFile.getFileSize());
                    knowledgeFile.setFileType(FileUtil.getSuffix(tempFile.getFileName()));
                    knowledgeFile.setSegmentedDocPath(tempFile.getSegmentedDocPath());
                    knowledgeFile.setStatus(3); // 已可用
                    knowledgeFile.setProcessingStatus("处理完成");
                    knowledgeFile.setSummary(tempFile.getSummary());
                    knowledgeFile.setTags(tempFile.getTags());
                    // 设置内容用于封面生成
                    try {
                        String content = fileContentExtractorService.extractContentFromLocalFileContent(tempFile.getLocalSourcePath());
                        knowledgeFile.setContent(content);
                    } catch (Exception e) {
                        log.warn("提取文件内容用于封面生成失败: {}", tempFile.getFileName(), e);
                        knowledgeFile.setContent(tempFile.getSummary()); // 如果提取失败，使用摘要
                    }
                    // 生成封面
                    String coverPath = coverGenerationService.generateCover(knowledgeFile, tempFile.getExtraMd());
                    knowledgeFile.setCoverPath(coverPath);
                    knowledgeFile.setUploadTime(LocalDateTime.now());
                    knowledgeFile.setProcessTime(LocalDateTime.now());
                    knowledgeFile.setDeleteFlag(false);
                    knowledgeFile.setFileMd5(tempFile.getFileMd5());
                    knowledgeFiles.add(knowledgeFile);

                    double progress = (double) (i + 1) / totalFiles * 30; // 30%用于文件准备
                    sink.next(String.format("data: {\"type\":\"prepare\",\"message\":\"准备文件: %s\",\"progress\":%.1f,\"fileName\":\"%s\"}\n\n",
                            tempFile.getFileName(), progress, tempFile.getFileName()));

                    if (StringUtil.isEmpty(tempFile.getS3Path())) {
                        // 生成摘要和标签
                        String s3Path = "wudao/kms/knowledge-base/" + knowledgeTempDTO.getKnowledgeBaseId() + tempFile.getFileName();
                        ossService.putObject(tempFile.getLocalSourcePath(), s3Path);
                        tempFile.setS3Path(s3Path);
                    }
                    LLMModel llmModel = llmModelService.queryEmbeddingModelByKnowledgeId(knowledgeTempDTO.getKnowledgeBaseId());
                    ChatModelStrategy strategyFactoryStrategy = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode());
                    String httpUrl = ossService.getHttpUrl(tempFile.getS3Path(), 1, TimeUnit.DAYS);
                    String json = strategyFactoryStrategy.simpleChat(llmModel.getModel(), String.format("这是我的文件：file:%s", httpUrl), """
                            请对我的文件生成摘要和标签，以json返回比如
                            {"summary":"摘要","tags":"三个标签,三个标签,三个标签"}
                            """, true);
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    knowledgeFile.setSummary(jsonObject.getString("summary"));
                    knowledgeFile.setTags(jsonObject.getString("tags"));
                    ossService.deleteFile(tempFile.getS3Path());
                }

                // 批量保存文件
                sink.next("data: {\"type\":\"save\",\"message\":\"批量保存文件到数据库\",\"progress\":35}\n\n");
                boolean result = saveBatch(knowledgeFiles);

                if (!result) {
                    sink.next("data: {\"type\":\"error\",\"message\":\"批量保存文件失败\",\"progress\":35}\n\n");
                    sink.complete();
                    return;
                }

                // 更新知识库标签
                List<String> tagsList = knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().stream()
                        .map(KnowledgeFileUploadTempDTO::getTags).toList();
                String mergedTags = tagsList.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(tag -> !tag.isEmpty())
                        .flatMap(tag -> Arrays.stream(tag.split(",")))
                        .map(String::trim)
                        .filter(tag -> !tag.isEmpty())
                        .distinct()
                        .collect(Collectors.joining(","));

                KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeTempDTO.getKnowledgeBaseId());
                if (knowledgeBase != null) {
                    knowledgeBase.setTags(mergedTags);
                    knowledgeBaseMapper.updateById(knowledgeBase);
                }

                // 提取文件ID列表
                List<Long> fileIds = knowledgeFiles.stream()
                        .map(KnowledgeFile::getId)
                        .collect(Collectors.toList());


                sink.next(String.format("data: {\"type\":\"save_complete\",\"message\":\"文件保存完成，共保存 %d 个文件\",\"progress\":40,\"fileIds\":%s}\n\n",
                        knowledgeFiles.size(), JSONObject.toJSONString(fileIds)));

                // 开始向量化处理
                sink.next("data: {\"type\":\"vectorization_start\",\"message\":\"开始向量化处理\",\"progress\":45}\n\n");

                // 处理向量化
                processVectorization(knowledgeFiles, knowledgeTempDTO, sink);

                sink.next("data: {\"type\":\"complete\",\"message\":\"所有文件处理完成\",\"progress\":100}\n\n");
                sink.complete();

            } catch (Exception e) {
                log.error("批量创建文件失败", e);
                sink.next(String.format("data: {\"type\":\"error\",\"message\":\"批量创建文件失败: %s\",\"progress\":-1}\n\n", e.getMessage()));
                sink.error(e);
            }
        });
    }

    /**
     * 处理向量化并推送进度
     */
    private void processVectorization(List<KnowledgeFile> knowledgeFiles, KnowledgeTempDTO knowledgeTempDTO,
                                      reactor.core.publisher.FluxSink<String> sink) {
        int totalFiles = knowledgeFiles.size();
        double baseProgress = 45;
        double progressStep = 50.0 / totalFiles; // 50%用于向量化处理

        for (int i = 0; i < knowledgeFiles.size(); i++) {
            KnowledgeFile item = knowledgeFiles.get(i);
            double currentProgress = baseProgress + (i + 1) * progressStep;

            try {
                sink.next(java.lang.String.format("data: {\"type\":\"vectorization_processing\",\"message\":\"正在处理: %s\",\"progress\":%.1f,\"fileName\":\"%s\",\"fileIndex\":%d,\"totalFiles\":%d}\n\n",
                        item.getFileName(), currentProgress, item.getFileName(), i + 1, totalFiles));

                String s3Path = "wudao/kms/downloadTemp/" + FileUtil.getName(item.getSegmentedDocPath());
                ossService.putObject(item.getSegmentedDocPath(), s3Path);

                // 获取文件URL
                String fileUrl = ossService.getHttpUrl(s3Path);
                Long documentId = item.getId();

                // 调用向量化处理
                VectorizationService.VectorizationResult vectorizationResult = vectorizationService.processFileVectorization(
                        fileUrl, item.getKnowledgeBaseId(), documentId, SecurityUtils.getUserId(), item.getSpaceId());

                if (vectorizationResult.isSuccess()) {
                    sink.next(java.lang.String.format("data: {\"type\":\"vectorization_success\",\"message\":\"文件 %s 向量化处理成功\",\"progress\":%.1f,\"fileName\":\"%s\",\"fileMd5\":\"%s\"}\n\n",
                            item.getFileName(), currentProgress, item.getFileName(), item.getFileMd5()));
                    log.info("文件 {} (ID: {}) 向量化处理提交成功", item.getFileName(), item.getId());
                } else {
                    sink.next(java.lang.String.format("data: {\"type\":\"vectorization_warning\",\"message\":\"文件 %s 向量化处理失败: %s\",\"progress\":%.1f,\"fileName\":\"%s\"}\n\n",
                            item.getFileName(), vectorizationResult.getMessage(), currentProgress, item.getFileName()));
                    log.warn("文件 {} (ID: {}) 向量化处理提交失败: {}", item.getFileName(), item.getId(), vectorizationResult.getMessage());
                }

                // 清理临时文件
                ossService.deleteFile(s3Path);

            } catch (Exception e) {
                log.error("处理文件 {} (ID: {}) 向量化时出错", item.getFileName(), item.getId(), e);
                sink.next(java.lang.String.format("data: {\"type\":\"vectorization_error\",\"message\":\"文件 %s 处理出错: %s\",\"progress\":%.1f,\"fileName\":\"%s\"}\n\n",
                        item.getFileName(), e.getMessage(), currentProgress, item.getFileName()));
                // 继续处理下一个文件，不中断整个流程
            }
        }
    }

    /**
     * 保存文件到本地source目录
     * 路径格式：basic.profile/knowledge/knowledgeId/temp/userId/source/文件
     *
     * @param fileName 采用uuid，保证每个文件都是不一样的
     */
    private String saveFileToLocalSource(MultipartFile file, Long knowledgeBaseId, Long userId, String fileName) {
        try {
            String localPath = buildLocalSourcePath(knowledgeBaseId, userId, fileName);
            File localFile = new File(localPath);

            // 确保目录存在
            localFile.getParentFile().mkdirs();

            // 保存文件
            file.transferTo(localFile);

            log.info("文件已保存到本地source目录: {}", localPath);
            return localPath;
        } catch (Exception e) {
            log.error("保存文件到本地source目录失败: knowledgeId={}, userId={}, fileName={}",
                    knowledgeBaseId, userId, fileName, e);
            throw new RuntimeException("保存文件到本地失败: " + e.getMessage());
        }
    }

    /**
     * 构建本地source文件路径
     */
    private String buildLocalSourcePath(Long knowledgeBaseId, Long userId, String fileName) {
        return basicProfile + File.separator +
                "knowledge" + File.separator +
                knowledgeBaseId + File.separator +
                userId + File.separator +
                "source" + File.separator +
                fileName;
    }

    /**
     * 构建本地segment文件路径
     */
    private String buildLocalSegmentPath(Long knowledgeBaseId, Long userId, String fileName) {
        return basicProfile + File.separator +
                "knowledge" + File.separator +
                knowledgeBaseId + File.separator +
                userId + File.separator +
                "segment" + File.separator +
                fileName;
    }

    /**
     * 根据MD5删除临时文件
     * 从Redis中删除指定知识库和用户的临时文件列表中的指定MD5文件
     *
     * @param knowledgeBaseId 知识库ID
     * @param spaceId         空间ID
     * @param fileMd5         文件MD5值
     * @return 删除结果
     */
    public Boolean deleteTempFileByMd5(Long knowledgeBaseId, Long spaceId, String fileMd5) {
        try {
            Long userId = SecurityUtils.getUserId();
            KnowledgeTempDTO knowledgeTempDTO = getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);

            if (knowledgeTempDTO == null || knowledgeTempDTO.getKnowledgeFileUploadTempDTOList() == null) {
                throw new RuntimeException("未找到临时文件信息");
            }

            List<KnowledgeFileUploadTempDTO> fileList = knowledgeTempDTO.getKnowledgeFileUploadTempDTOList();

            // 查找要删除的文件
            KnowledgeFileUploadTempDTO fileToDelete = fileList.stream()
                    .filter(item -> fileMd5.equals(item.getFileMd5()))
                    .findFirst()
                    .orElse(null);

            if (fileToDelete == null) {
                throw new RuntimeException("未找到指定MD5的临时文件");
            }

            // 删除本地文件
            deleteLocalFiles(knowledgeBaseId, userId, fileToDelete);

            // 从列表中移除
            fileList.removeIf(item -> fileMd5.equals(item.getFileMd5()));

            // 更新Redis
            if (fileList.isEmpty()) {
                // 如果没有文件了，删除整个Redis键
                String key = "knowledge_temp_file:" + knowledgeBaseId + ":" + knowledgeTempDTO.getKnowledgeSpaceId() + ":" + userId;
                redisCache.deleteObject(key);
                log.info("临时文件列表已清空，删除Redis键: {}", key);
            } else {
                // 更新Redis中的临时文件列表
                knowledgeTempDTO.setKnowledgeFileUploadTempDTOList(fileList);
                createKnowledgeTempFile(knowledgeTempDTO, true);
                log.info("已从临时文件列表中删除MD5为 {} 的文件", fileMd5);
            }

            return true;
        } catch (Exception e) {
            log.error("删除临时文件失败: knowledgeBaseId={}, fileMd5={}", knowledgeBaseId, fileMd5, e);
            throw new RuntimeException("删除临时文件失败: " + e.getMessage());
        }
    }

    /**
     * 删除本地文件（包括source和segment目录的文件）
     */
    private void deleteLocalFiles(Long knowledgeBaseId, Long userId, KnowledgeFileUploadTempDTO fileDTO) {
        try {
            // 删除source目录的文件
            if (fileDTO.getLocalSourcePath() != null) {
                File sourceFile = new File(fileDTO.getLocalSourcePath());
                if (sourceFile.exists() && sourceFile.delete()) {
                    log.info("已删除本地source文件: {}", fileDTO.getLocalSourcePath());
                }
            }

            // 删除segment目录的文件
            if (fileDTO.getLocalSegmentPath() != null) {
                File segmentFile = new File(fileDTO.getLocalSegmentPath());
                if (segmentFile.exists() && segmentFile.delete()) {
                    log.info("已删除本地segment文件: {}", fileDTO.getLocalSegmentPath());
                }
            }

            // 尝试删除空目录
            cleanupEmptyDirectories(knowledgeBaseId, userId);

        } catch (Exception e) {
            log.warn("删除本地文件时发生错误，但不影响临时记录删除: {}", e.getMessage());
        }
    }

    /**
     * 清理空的目录结构
     */
    private void cleanupEmptyDirectories(Long knowledgeBaseId, Long userId) {
        try {
            // 清理source目录
            String sourceDirPath = basicProfile + File.separator +
                    "knowledge" + File.separator +
                    knowledgeBaseId + File.separator +
                    "temp" + File.separator +
                    userId + File.separator +
                    "source";
            File sourceDir = new File(sourceDirPath);
            if (sourceDir.exists() && sourceDir.isDirectory() && sourceDir.list() != null && sourceDir.list().length == 0) {
                sourceDir.delete();
                log.info("已删除空的source目录: {}", sourceDirPath);
            }

            // 清理segment目录
            String segmentDirPath = basicProfile + File.separator +
                    "knowledge" + File.separator +
                    knowledgeBaseId + File.separator +
                    "temp" + File.separator +
                    userId + File.separator +
                    "segment";
            File segmentDir = new File(segmentDirPath);
            if (segmentDir.exists() && segmentDir.isDirectory() && segmentDir.list() != null && segmentDir.list().length == 0) {
                segmentDir.delete();
                log.info("已删除空的segment目录: {}", segmentDirPath);
            }

            // 清理用户临时目录
            String userTempDirPath = basicProfile + File.separator +
                    "knowledge" + File.separator +
                    knowledgeBaseId + File.separator +
                    "temp" + File.separator +
                    userId;
            File userTempDir = new File(userTempDirPath);
            if (userTempDir.exists() && userTempDir.isDirectory() && userTempDir.list() != null && userTempDir.list().length == 0) {
                userTempDir.delete();
                log.info("已删除空的用户临时目录: {}", userTempDirPath);
            }

        } catch (Exception e) {
            log.warn("清理空目录时发生错误: {}", e.getMessage());
        }
    }

    /**
     * 批量从URL下载文件
     * 支持同时下载多个文件并创建临时文件记录
     *
     * @param batchDownloadDTO 批量下载请求DTO
     * @return 批量下载的临时文件信息列表
     */
    public List<KnowledgeFileUploadTempDTO> batchDownloadFilesFromUrls(KnowledgeFileBatchDownloadDTO batchDownloadDTO) {
        List<KnowledgeFileUploadTempDTO> resultList = new ArrayList<>();
        Long knowledgeBaseId = batchDownloadDTO.getKnowledgeBaseId();
        Long spaceId = batchDownloadDTO.getSpaceId();
        Long userId = SecurityUtils.getUserId();

        // 获取现有的临时文件信息
        KnowledgeTempDTO existingTempDTO = getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);
        List<KnowledgeFileUploadTempDTO> existingFiles = new ArrayList<>();
        if (existingTempDTO != null && existingTempDTO.getKnowledgeFileUploadTempDTOList() != null) {
            existingFiles = existingTempDTO.getKnowledgeFileUploadTempDTOList();
        }

        for (KnowledgeFileBatchDownloadDTO.FileDownloadInfo fileInfo : batchDownloadDTO.getFileList()) {
            try {
                log.info("开始下载文件: {}", fileInfo.getFileUrl());

                // 使用现有的单文件下载方法
                KnowledgeFileUploadTempDTO tempDTO = uploadKnowledgeFileFromUrl(
                        fileInfo.getFileUrl(),
                        fileInfo.getFileName(),
                        knowledgeBaseId,
                        spaceId
                );

                // 如果提供了描述，添加到摘要中
                if (fileInfo.getDescription() != null && !fileInfo.getDescription().trim().isEmpty()) {
                    String currentSummary = tempDTO.getSummary();
                    if (currentSummary == null || currentSummary.trim().isEmpty()) {
                        tempDTO.setSummary(fileInfo.getDescription());
                    } else {
                        tempDTO.setSummary(currentSummary + " | " + fileInfo.getDescription());
                    }
                }
                tempDTO.setCreateType("URL");
                resultList.add(tempDTO);
                existingFiles.add(tempDTO);
                log.info("文件下载成功: {} -> {}", fileInfo.getFileUrl(), tempDTO.getFileName());

            } catch (Exception e) {
                log.error("文件下载失败: {}", fileInfo.getFileUrl(), e);
                // 创建失败记录，但不中断其他文件的下载
                KnowledgeFileUploadTempDTO failedDTO = new KnowledgeFileUploadTempDTO();
                String failedFileName = fileInfo.getFileName() != null ? fileInfo.getFileName() : "下载失败";
                String errorMessage = "下载失败: " + e.getMessage();

                // 为失败记录生成唯一的UUID和MD5值
                String failedContent = "文件下载失败\n" +
                        "原始URL: " + fileInfo.getFileUrl() + "\n" +
                        "失败原因: " + e.getMessage() + "\n" +
                        "失败时间: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String fileMd5 = DigestUtils.md5Hex(failedContent.getBytes(StandardCharsets.UTF_8));

                failedDTO.setFileName(failedFileName);
                failedDTO.setFileMd5(fileMd5);
                failedDTO.setFileSize((long) failedContent.getBytes(StandardCharsets.UTF_8).length);
                failedDTO.setFileType("txt");
                failedDTO.setParseResult("下载失败");
                failedDTO.setSummary(errorMessage);
                failedDTO.setTags("下载失败");
                failedDTO.setCreateType("URL");
                resultList.add(failedDTO);
            }
        }

        // 更新临时文件记录
        if (!existingFiles.isEmpty()) {
            KnowledgeTempDTO knowledgeTempDTO = new KnowledgeTempDTO();
            knowledgeTempDTO.setKnowledgeBaseId(knowledgeBaseId);
            knowledgeTempDTO.setKnowledgeSpaceId(spaceId);
            knowledgeTempDTO.setUserId(userId);
            knowledgeTempDTO.setKnowledgeFileUploadTempDTOList(existingFiles);
            createKnowledgeTempFile(knowledgeTempDTO);
        }

        log.info("批量下载完成，成功下载 {} 个文件，共处理 {} 个URL",
                resultList.stream().filter(dto -> !"下载失败".equals(dto.getParseResult())).count(),
                batchDownloadDTO.getFileList().size());

        return resultList;
    }

    /**
     * 在线创建文件
     * 根据前端传过来的文件内容创建文件并上传
     *
     * @param createDTO 在线创建文件DTO
     * @return 创建的临时文件信息
     */
    public KnowledgeFileUploadTempDTO createKnowledgeFileFromContent(KnowledgeFileOnlineCreateDTO createDTO) {
        try {
            String fileUuid = IdUtil.fastSimpleUUID();
            String fileName = createDTO.getFileName();
            String fileContent = createDTO.getFileContent();
            Long knowledgeBaseId = createDTO.getKnowledgeBaseId();
            Long spaceId = createDTO.getSpaceId();
            Long userId = SecurityUtils.getUserId();

            // 确定文件类型
            String fileType = createDTO.getFileType();
            if (fileType == null || fileType.trim().isEmpty()) {
                // 从文件名中提取扩展名
                if (fileName.contains(".")) {
                    fileType = FileUtil.getSuffix(fileName);
                } else {
                    fileType = "txt"; // 默认为文本文件
                }
            }
            fileUuid += "." + fileType;

            // 计算文件大小和MD5
            byte[] contentBytes = fileContent.getBytes(StandardCharsets.UTF_8);
            Long fileSize = (long) contentBytes.length;
            String fileMd5 = DigestUtils.md5Hex(contentBytes);

            // MD5校验 - 检查是否已存在相同文件
            KnowledgeTempDTO existingTempDTO = getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);
            if (existingTempDTO != null && existingTempDTO.getKnowledgeFileUploadTempDTOList() != null) {
                boolean md5Exists = existingTempDTO.getKnowledgeFileUploadTempDTOList().stream()
                        .anyMatch(tempFile -> fileMd5.equals(tempFile.getFileMd5()));
                if (md5Exists) {
                    throw new ServiceException("duplicate_file", "相同内容的文件已存在，请勿重复创建");
                }
            }

            // 检查文件名是否重复
            if (checkFileNameExists(fileName, knowledgeBaseId, null)) {
                throw new RuntimeException("该知识库下已存在同名文件");
            }

            // 保存文件内容到本地
            String localSourcePath = saveContentToLocalSource(fileContent, knowledgeBaseId, userId, fileUuid);

            // 创建临时文件DTO
            KnowledgeFileUploadTempDTO knowledgeFileUploadTempDTO = new KnowledgeFileUploadTempDTO();
            knowledgeFileUploadTempDTO.setFileName(fileName);
            knowledgeFileUploadTempDTO.setFileSize(fileSize);
            knowledgeFileUploadTempDTO.setFileType(fileType);
            knowledgeFileUploadTempDTO.setFileMd5(fileMd5);
            knowledgeFileUploadTempDTO.setLocalSourcePath(localSourcePath);
//            knowledgeFileUploadTempDTO.setFileUuid(fileUuid);
            knowledgeFileUploadTempDTO.setCreateType("ONLINE");

            try {
                // 上传到S3
                String s3Path = "wudao/kms/knowledge-base/" + knowledgeBaseId + "/" + fileUuid;
                File tempFile = new File(localSourcePath);
                ossService.putObject(localSourcePath, s3Path);
                knowledgeFileUploadTempDTO.setS3Path(s3Path);

                // 设置摘要和标签
                String summary = createDTO.getSummary();
                String tags = createDTO.getTags();

                // 如果没有提供摘要和标签，且文件大小不超过10MB，可以调用AI分析
                if ((summary == null || summary.trim().isEmpty() || tags == null || tags.trim().isEmpty())
                        && fileSize <= 10 * 1024 * 1024) {
                    try {
                        // 生成外网可访问的URL
//                        String url = ossService.getHttpUrl(s3Path);
//                        QwenAiService.AiAnalysisResult aiResult = qwenAiService.analyzeFile(url, fileName);
//
//                        if (summary == null || summary.trim().isEmpty()) {
//                            summary = aiResult.getSummary();
//                        }
//                        if (tags == null || tags.trim().isEmpty()) {
//                            tags = String.join(",", aiResult.getTags());
//                        }
                    } catch (Exception e) {
                        log.warn("AI分析在线创建的文件失败，使用默认值: {}", fileName, e);
                        if (summary == null || summary.trim().isEmpty()) {
                            summary = "在线创建的" + fileType.toUpperCase() + "文档";
                        }
                        if (tags == null || tags.trim().isEmpty()) {
                            tags = "在线创建,文档";
                        }
                    }
                } else {
                    // 设置默认值
                    if (summary == null || summary.trim().isEmpty()) {
                        summary = "在线创建的" + fileType.toUpperCase() + "文档";
                    }
                    if (tags == null || tags.trim().isEmpty()) {
                        tags = "在线创建,文档";
                    }
                }

                knowledgeFileUploadTempDTO.setParseResult("解析完成");
                knowledgeFileUploadTempDTO.setSummary(summary);
                knowledgeFileUploadTempDTO.setTags(tags);

                log.info("在线创建文件成功: {}", fileName);
                return knowledgeFileUploadTempDTO;

            } catch (Exception e) {
                log.error("处理在线创建文件失败: {}", fileName, e);
                knowledgeFileUploadTempDTO.setParseResult("处理失败");
                knowledgeFileUploadTempDTO.setSummary("文件创建失败: " + e.getMessage());
                knowledgeFileUploadTempDTO.setTags("创建失败");
                return knowledgeFileUploadTempDTO;
            }

        } catch (Exception e) {
            log.error("在线创建文件失败: {}", createDTO.getFileName(), e);
            throw new RuntimeException("在线创建文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 保存文件内容到本地source目录
     *
     * @param fileContent     文件内容
     * @param knowledgeBaseId 知识库ID
     * @param userId          用户ID
     * @param fileName        文件名
     * @return 本地文件路径
     */
    private String saveContentToLocalSource(String fileContent, Long knowledgeBaseId, Long userId, String fileName) {
        try {
            String localPath = buildLocalSourcePath(knowledgeBaseId, userId, fileName);
            File localFile = new File(localPath);

            // 确保目录存在
            localFile.getParentFile().mkdirs();

            // 保存文件内容
            FileUtil.writeString(fileContent, localPath, StandardCharsets.UTF_8);

            log.info("在线创建的文件已保存到本地source目录: {}", localPath);
            return localPath;
        } catch (Exception e) {
            log.error("保存在线创建文件到本地source目录失败: knowledgeId={}, userId={}, fileName={}",
                    knowledgeBaseId, userId, fileName, e);
            throw new RuntimeException("保存在线创建文件到本地失败: " + e.getMessage());
        }
    }

    /**
     * 从Markdown内容中提取图片URL
     * 识别格式：![alt text](url) 或 ![](url)
     *
     * @param content Markdown格式的内容
     * @return 图片URL列表
     */
    private List<String> extractImageUrlsFromMarkdown(String content) {
        List<String> imageUrls = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return imageUrls;
        }

        // 正则表达式匹配Markdown图片格式：![alt text](url)
        java.util.regex.Pattern imagePattern = java.util.regex.Pattern.compile("!\\[([^\\]]*)\\]\\(([^)]+)\\)");
        java.util.regex.Matcher matcher = imagePattern.matcher(content);

        while (matcher.find()) {
            String imageUrl = matcher.group(2).trim();

            // 验证URL是否有效
            if (isValidImageUrl(imageUrl)) {
                imageUrls.add(imageUrl);
                log.debug("提取到图片URL: {}", imageUrl);
            }
        }

        log.info("从内容中提取到 {} 个图片URL", imageUrls.size());
        return imageUrls;
    }

    /**
     * 验证图片URL是否有效
     *
     * @param url 图片URL
     * @return 是否为有效的图片URL
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        // 移除可能的空格
        url = url.trim();

        // 检查是否为HTTP(S)链接
        if (url.matches("^https?://.*")) {
            return true;
        }

        // 检查是否为相对路径的图片文件
        if (url.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp|svg)$")) {
            return true;
        }

        // 检查是否为Base64编码的图片
        if (url.startsWith("data:image/")) {
            return true;
        }

        log.warn("无效的图片URL格式: {}", url);
        return false;
    }

    /**
     * 执行 Docker MinerU 解析
     * @param inputFilePath 输入文件的本地路径
     * @param outputBaseDir 输出基础目录
     * @param fileName 文件名
     * @return 生成的 markdown 文件路径
     */
    private String executeMinerUDocker(String inputFilePath, String outputBaseDir, String fileName) {
        try {
            log.info("开始执行 Docker MinerU 解析，输入文件: {}, 输出目录: {}", inputFilePath, outputBaseDir);

            // 检查缓存：如果已经解析过（基于 fileMd5），直接使用缓存结果
            // 优先查找当前文件名对应的缓存
            String filePrefix = FileUtil.getPrefix(fileName);
            String cachedMdPath = outputBaseDir + File.separator + filePrefix + File.separator + "auto" + File.separator + filePrefix + ".md";

            if (FileUtil.exist(cachedMdPath)) {
                log.info("发现缓存的 MinerU 解析结果（文件名匹配），直接使用: {}", cachedMdPath);
                return cachedMdPath;
            }

            // 如果当前文件名的缓存不存在，查找 fileMd5 目录下是否有其他文件名的解析结果
            // 因为 MD5 相同说明文件内容相同，可以复用之前的解析结果
            if (FileUtil.exist(outputBaseDir)) {
                File[] subDirs = FileUtil.file(outputBaseDir).listFiles(File::isDirectory);
                if (subDirs != null && subDirs.length > 0) {
                    // 遍历所有子目录，查找第一个有效的 .md 文件
                    for (File subDir : subDirs) {
                        File autoDir = new File(subDir, "auto");
                        if (autoDir.exists() && autoDir.isDirectory()) {
                            File[] mdFiles = autoDir.listFiles((dir, name) -> name.endsWith(".md"));
                            if (mdFiles != null && mdFiles.length > 0) {
                                String existingMdPath = mdFiles[0].getAbsolutePath();
                                log.info("发现缓存的 MinerU 解析结果（相同 MD5，不同文件名），直接使用: {}", existingMdPath);
                                return existingMdPath;
                            }
                        }
                    }
                }
            }

            log.info("未找到缓存，开始执行 Docker MinerU 解析...");

            // 创建输出目录
            FileUtil.mkdir(outputBaseDir);

            // 获取输入文件所在目录
            String inputDir = FileUtil.getParent(inputFilePath, 1);

            // 将容器内路径转换为宿主机路径（用于 Docker 挂载）
            // 容器内路径: /uploadPath/xxx -> 宿主机路径: /uploadPath/wudao-kms/uploadPath/xxx
            String hostInputDir = convertContainerPathToHostPath(inputDir);
            String hostOutputDir = convertContainerPathToHostPath(outputBaseDir);

            log.info("路径转换 - 输入目录: 容器内[{}] -> 宿主机[{}]", inputDir, hostInputDir);
            log.info("路径转换 - 输出目录: 容器内[{}] -> 宿主机[{}]", outputBaseDir, hostOutputDir);

            // 配置 Docker 挂载目录（使用宿主机路径）
            Bind inputBind = new Bind(hostInputDir, new Volume("/data/input"));
            Bind outputBind = new Bind(hostOutputDir, new Volume("/data/output"));

            // 配置 NVIDIA GPU
            DeviceRequest nvidia = new DeviceRequest()
                .withDriver("nvidia")
                .withCount(-1)
                .withCapabilities(Arrays.asList(Arrays.asList("gpu")));

            // 配置 Host Config
            HostConfig hostConfig = HostConfig.newHostConfig()
                .withRuntime("nvidia")
                .withDeviceRequests(Collections.singletonList(nvidia))
                .withBinds(inputBind, outputBind);

            // 创建容器
            CreateContainerResponse containerResponse = dockerClient
                .createContainerCmd(minerUImage)
                .withWorkingDir("/vllm-workspace")
                .withHostConfig(hostConfig)
                .withCmd("mineru", "-p", "/data/input/" + fileName, "-o", "/data/output",
                         "--source", "modelscope", "--b", "vlm-mlx-engine")
                .exec();

            String containerId = containerResponse.getId();
            log.info("Docker 容器已创建，容器ID: {}", containerId);

            // 启动容器
            dockerClient.startContainerCmd(containerId).exec();
            log.info("Docker 容器已启动，等待执行完成（超时时间：30分钟）...");

            // 等待容器执行完成，设置超时时间为 30 分钟
            Integer statusCode = dockerClient.waitContainerCmd(containerId)
                .exec(new WaitContainerResultCallback())
                .awaitStatusCode(30, TimeUnit.MINUTES);

            log.info("Docker 容器执行完成，状态码: {}", statusCode);

            // 删除容器
            try {
                dockerClient.removeContainerCmd(containerId).exec();
                log.info("Docker 容器已删除");
            } catch (Exception e) {
                log.warn("删除容器失败: {}", e.getMessage());
            }

            // 返回生成的 markdown 文件路径
            String mdPath = outputBaseDir + File.separator + filePrefix + File.separator + "auto" + File.separator + filePrefix + ".md";

            if (!FileUtil.exist(mdPath)) {
                throw new RuntimeException("MinerU 解析失败，未找到输出文件: " + mdPath);
            }

            log.info("MinerU 解析成功，markdown 文件: {}", mdPath);
            return mdPath;

        } catch (Exception e) {
            log.error("执行 Docker MinerU 解析失败: {}", e.getMessage(), e);
            throw new RuntimeException("Docker MinerU 解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理 Docker MinerU 结果，上传图片到 OSS 并替换路径
     * @param markdownPath 生成的 markdown 文件路径
     * @param outputBaseDir 输出基础目录
     * @param fileName 原始文件名
     * @return 处理后的 markdown 内容
     */
    private String processDockerMinerUResult(String markdownPath, String outputBaseDir, String fileName) {
        try {
            log.info("开始处理 Docker MinerU 结果，markdown: {}", markdownPath);

            // 1. 读取 markdown 内容
            String content = FileUtil.readUtf8String(markdownPath);

            // 2. 获取 images 目录路径
            String filePrefix = FileUtil.getPrefix(fileName);
            String imagesDir = outputBaseDir + File.separator + filePrefix + File.separator + "auto" + File.separator + "images";

            if (!FileUtil.exist(imagesDir)) {
                log.warn("未找到 images 目录，跳过图片处理: {}", imagesDir);
                return content;
            }

            // 3. 正则匹配图片引用：![alt](images/xxx.jpg)
            Pattern imagePattern = Pattern.compile("!\\[([^\\]]*)\\]\\((images/[^\\)]+)\\)");
            Matcher matcher = imagePattern.matcher(content);

            StringBuilder result = new StringBuilder();
            int processedCount = 0;

            while (matcher.find()) {
                String altText = matcher.group(1);
                String imagePath = matcher.group(2);  // 例如：images/xxx.jpg

                try {
                    // 4. 上传图片到 OSS
                    String localImagePath = outputBaseDir + File.separator + filePrefix + File.separator + "auto" + File.separator + imagePath;

                    if (!FileUtil.exist(localImagePath)) {
                        log.warn("图片文件不存在，跳过: {}", localImagePath);
                        matcher.appendReplacement(result, matcher.group(0));
                        continue;
                    }

                    // 生成 OSS 路径
                    String imageFileName = FileUtil.getName(localImagePath);
                    String s3Key = String.format("wudao/kms/mineru-images/%s/%s",
                        LocalDateTime.now().toLocalDate(),
                        IdUtil.randomUUID() + "." + FileUtil.extName(imageFileName));

                    // 上传到 OSS
                    ossService.putObject(localImagePath, s3Key);
                    String httpsUrl = ossService.getHttpUrl(s3Key);

                    // 5. 替换为 OSS URL
                    String replacement = String.format("![%s](%s)", altText, httpsUrl);
                    matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));

                    processedCount++;
                    log.info("图片上传成功 [{}/{}]: {} -> {}", processedCount, imagePath, localImagePath, httpsUrl);

                } catch (Exception e) {
                    log.error("图片上传失败: {}, 错误: {}", imagePath, e.getMessage());
                    // 保持原样
                    matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
                }
            }
            matcher.appendTail(result);

            log.info("Docker MinerU 结果处理完成，共处理 {} 张图片", processedCount);
            return result.toString();

        } catch (Exception e) {
            log.error("处理 Docker MinerU 结果失败: {}", e.getMessage(), e);
            throw new RuntimeException("处理 Docker MinerU 结果失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将容器内路径转换为宿主机路径
     * 当 KMS 服务运行在容器中时，需要将容器内的路径转换为宿主机路径，才能正确挂载到新创建的 Docker 容器
     *
     * @param containerPath 容器内路径，例如：/uploadPath/mineru_output/abc123
     * @return 宿主机路径，例如：/uploadPath/wudao-kms/uploadPath/mineru_output/abc123
     */
    private String convertContainerPathToHostPath(String containerPath) {
        // 如果路径不是以 basicProfile 开头，说明可能已经是宿主机路径或其他路径，直接返回
        if (!containerPath.startsWith(basicProfile)) {
            log.warn("路径 [{}] 不以 basicProfile [{}] 开头，直接使用原路径", containerPath, basicProfile);
            return containerPath;
        }

        // 将容器内路径转换为宿主机路径
        // 容器内路径: /uploadPath/mineru_output/abc123
        // basicProfile: /uploadPath
        // dockerBindPath: /uploadPath/wudao-kms/uploadPath
        // 结果: /uploadPath/wudao-kms/uploadPath/mineru_output/abc123

        String relativePath = containerPath.substring(basicProfile.length());
        String hostPath = dockerBindPath + relativePath;

        return hostPath;
    }
}