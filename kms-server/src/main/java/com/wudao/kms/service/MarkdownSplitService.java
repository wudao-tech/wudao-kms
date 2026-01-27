package com.wudao.kms.service;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.wudao.common.exception.ServiceException;
import com.wudao.kms.common.MarkdownSplitter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Markdown分段服务类
 * 提供本地Markdown文档分段功能，替代远程Python API调用
 *
 * @author Claude
 * @since 2025-09-23
 */
@Slf4j
@Service
public class MarkdownSplitService {

    /**
     * 分段响应结果类
     */
    @Getter
    public static class SplitResponse {
        // Getters and Setters
        private List<Map<String, Object>> chunks;
        private int totalChunks;
        private boolean success;
        private String message;

        public SplitResponse() {
        }

        public SplitResponse(List<Map<String, Object>> chunks, int totalChunks, boolean success, String message) {
            this.chunks = chunks;
            this.totalChunks = totalChunks;
            this.success = success;
            this.message = message;
        }

        public void setChunks(List<Map<String, Object>> chunks) {
            this.chunks = chunks;
        }

        public void setTotalChunks(int totalChunks) {
            this.totalChunks = totalChunks;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * 分段Markdown文档（通过文件路径）
     *
     * @param markdownFilePath markdown文件路径
     * @param maxTokens        最大token数，默认500，最小50
     * @param overlap          重叠token数，默认100
     * @param customSeparators 自定义分隔符，可为空
     * @return 分段结果的JSON字符串
     */
    public SplitResponse splitMarkdownFromFile(String markdownFilePath, Integer maxTokens, Integer overlap, String customSeparators) {
        // 读取markdown文件内容
        String content = FileUtil.readUtf8String(markdownFilePath);
        if (content == null || content.trim().isEmpty()) {
            throw new ServiceException("文件不能为空");
        }

        return splitMarkdownFromContent(content, maxTokens, overlap, customSeparators);
    }

    /**
     * 分段Markdown文档（通过内容字符串）
     *
     * @param content          markdown内容
     * @param maxTokens        最大token数，默认500，最小50
     * @param overlap          重叠token数，默认100
     * @param customSeparators 自定义分隔符，可为空
     * @return 分段结果的JSON字符串
     */
    public SplitResponse splitMarkdownFromContent(String content, Integer maxTokens, Integer overlap, String customSeparators) {
        return splitMarkdownFromContent(content, maxTokens, overlap, customSeparators, null, null);
    }

    /**
     * 分段Markdown文档（通过内容字符串，支持分段类型）
     *
     * @param content          markdown内容
     * @param maxTokens        最大token数，默认500，最小50
     * @param overlap          重叠token数，默认100
     * @param customSeparators 自定义分隔符，可为空
     * @param segmentType      分段类型：paragraph(段落)/contentSize(长度)/symbol(符号)
     * @param maxParagraph     最大段落深度（仅对paragraph类型有效）
     * @return 分段结果
     */
    public SplitResponse splitMarkdownFromContent(String content, Integer maxTokens, Integer overlap,
                                                 String customSeparators, String segmentType, Integer maxParagraph) {
        // 参数验证和默认值设置
        int actualMaxTokens = validateMaxTokens(maxTokens);
        int actualOverlap = 0;
        String actualCustomSeps = (customSeparators != null) ? customSeparators : "";
        String actualSegmentType = (segmentType != null) ? segmentType : "contentSize";

        log.info("开始分段处理，maxTokens: {}, overlap: {}, customSeparators: {}, segmentType: {}, maxParagraph: {}",
                actualMaxTokens, actualOverlap, actualCustomSeps, actualSegmentType, maxParagraph);

        // 使用MarkdownSplitter进行分段
        MarkdownSplitter splitter = new MarkdownSplitter();
        List<MarkdownSplitter.SplitResult> splitResults;

        if ("paragraph".equals(actualSegmentType)) {
            // 段落分割：按标题级别分割，但控制最大长度
            splitResults = splitter.splitMarkdownByParagraph(
                    content, actualMaxTokens, actualOverlap, maxParagraph);
        } else if ("symbol".equals(actualSegmentType)) {
            // 符号分割：使用自定义分隔符
            splitResults = splitter.splitMarkdownText(
                    content, actualMaxTokens, actualOverlap, actualCustomSeps);
        } else {
            // 默认长度分割
            splitResults = splitter.splitMarkdownText(
                    content, actualMaxTokens, actualOverlap, "");
        }

        // 构造返回结果
        return createSuccessResponse(splitResults);
    }

    /**
     * 验证maxTokens参数
     */
    private int validateMaxTokens(Integer maxTokens) {
        if (maxTokens == null || maxTokens < 50) {
            log.warn("maxTokens参数无效: {}, 使用默认值500", maxTokens);
            return 500;
        }
        return maxTokens;
    }
    /**
     * 创建成功响应
     */
    private SplitResponse createSuccessResponse(List<MarkdownSplitter.SplitResult> splitResults) {
        List<Map<String, Object>> chunks = new ArrayList<>();
        for (MarkdownSplitter.SplitResult result : splitResults) {
            // 过滤空内容
            if (result.content == null || result.content.trim().isEmpty()) {
                continue;
            }
            Map<String, Object> chunk = new HashMap<>();
            chunk.put("content", result.content);
            chunk.put("metadata", result.metadata);
            chunks.add(chunk);
        }

        String message = String.format("成功分段，共生成 %d 个分段", chunks.size());
        log.info(message);

        return new SplitResponse(chunks, chunks.size(), true, message);
    }

    /**
     * 创建错误响应的JSON字符串
     */
    private String createErrorResponse(String errorMessage) {
        SplitResponse errorResponse = new SplitResponse(new ArrayList<>(), 0, false, errorMessage);
        return JSON.toJSONString(errorResponse);
    }

    /**
     * 兼容原有API的方法
     * 模拟Python API的split_markdown接口
     *
     * @param markdownFilePath 文件路径
     * @param maxTokens        最大token数
     * @param overlap          重叠数
     * @param customSeparators 自定义分隔符
     * @return JSON格式的响应字符串，与Python API格式兼容
     */
    public SplitResponse splitMarkdown(String markdownFilePath, Integer maxTokens, Integer overlap, String customSeparators) {
        return splitMarkdownFromFile(markdownFilePath, maxTokens, overlap, customSeparators);
    }
}