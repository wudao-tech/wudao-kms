package com.wudao.kms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Qwen AI服务
 * 用于调用阿里云Qwen大模型API生成文件摘要和标签
 */
@Slf4j
@Service
public class QwenAiService {

    @Value("${ai.qwen.api-key}")
    private String apiKey;

    @Value("${ai.qwen.base-url}")
    private String baseUrl;

    @Value("${ai.qwen.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * AI分析结果
     */
    @Data
    public static class AiAnalysisResult {
        private String summary;
        private List<String> tags;
        private boolean success;
        private String errorMessage;

        public static AiAnalysisResult success(String summary, List<String> tags) {
            AiAnalysisResult result = new AiAnalysisResult();
            result.summary = summary;
            result.tags = tags;
            result.success = true;
            return result;
        }

        public static AiAnalysisResult failure(String errorMessage) {
            AiAnalysisResult result = new AiAnalysisResult();
            result.summary = "AI分析失败，请手动添加摘要";
            result.tags = List.of("待分类");
            result.success = false;
            result.errorMessage = errorMessage;
            return result;
        }
    }

    /**
     * 分析文件内容
     */
    public AiAnalysisResult analyzeFile(String fileUrl, String fileName) {
        log.info("开始AI分析文件: {}, URL: {}", fileName, fileUrl);

        try {
            // 构建提示词
            String prompt = buildAnalysisPrompt(fileUrl, fileName);
            
            // 调用Qwen API
            String response = callQwenApi(prompt);
            
            // 解析响应
            return parseResponse(response);
            
        } catch (Exception e) {
            log.error("AI分析文件失败: {}", fileName, e);
            return AiAnalysisResult.failure(e.getMessage());
        }
    }

    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(String fileUrl, String fileName) {
        return String.format(
            "%s 这是一个文件链接，请访问并分析文件内容。文件名: %s\n\n" +
            "请根据文件内容生成：\n" +
            "1. 一个简洁的摘要（不超过200字）\n" +
            "2. 3-5个相关标签\n\n" +
            "请严格按照以下JSON格式返回，不要包含任何其他内容：\n" +
            "{\"summary\": \"摘要内容\", \"tags\": [\"标签1\", \"标签2\", \"标签3\"]}",
            fileUrl, fileName
        );
    }

    /**
     * 调用Qwen API
     */
    private String callQwenApi(String prompt) {
        try {
            log.info("开始调用Qwen API");
            
            // 创建OpenAI客户端
            OpenAIClient client = OpenAIOkHttpClient.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .build();

            // 构建请求参数
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addUserMessage(prompt)
                    .model(model)
                    .maxTokens(1000)
                    .temperature(0.3)
                    .build();

            log.info("发送请求到Qwen API，模型: {}", model);

            // 发送请求
            ChatCompletion chatCompletion = client.chat().completions().create(params);
            
            // 获取响应内容
            String content = chatCompletion.choices().get(0).message().content().orElse("");
            
            log.info("收到Qwen API响应，内容长度: {}", content.length());
            log.debug("响应内容: {}", content);
            
            return content;
            
        } catch (Exception e) {
            log.error("调用Qwen API失败，错误类型: {}, 错误信息: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("调用Qwen AI服务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析AI响应
     */
    private AiAnalysisResult parseResponse(String response) {
        try {
            log.info("开始解析AI响应");
            
            // 清理响应内容，移除可能的markdown格式
            String cleanResponse = response.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();

            log.debug("清理后的响应: {}", cleanResponse);

            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(cleanResponse);
            
            String summary = jsonNode.path("summary").asText("AI分析失败");
            
            // 解析标签数组
            List<String> tags = new ArrayList<>();
            JsonNode tagsNode = jsonNode.path("tags");
            if (tagsNode.isArray()) {
                for (JsonNode tagNode : tagsNode) {
                    tags.add(tagNode.asText());
                }
            }
            
            // 如果没有标签，添加默认标签
            if (tags.isEmpty()) {
                tags.add("待分类");
            }

            log.info("AI分析成功，摘要长度: {}, 标签数量: {}", summary.length(), tags.size());
            
            return AiAnalysisResult.success(summary, tags);
            
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            return AiAnalysisResult.failure("响应解析失败: " + e.getMessage());
        }
    }
} 