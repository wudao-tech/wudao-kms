package com.wudao.kms.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 深度思考服务
 * 使用Spring AI调用Qwen 7B模型进行深度分析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepThinkingService {

    private final RestTemplate restTemplate;

    @Value("${spring.ai.qwen.api-key:}")
    private String qwenApiKey;

    @Value("${spring.ai.qwen.base-url:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String qwenBaseUrl;

    @Value("${spring.ai.qwen.model:qwen-turbo}")
    private String qwenModel;

    /**
     * 深度思考结果
     */
    @Data
    public static class DeepThinkingResult {
        private String originalQuestion;
        private String deepAnalysis;
        private List<String> keyPoints;
        private List<String> relatedQuestions;
        private String reasoning;
        private boolean success;
        private String errorMessage;

        public static DeepThinkingResult success(String originalQuestion, String deepAnalysis, 
                                               List<String> keyPoints, List<String> relatedQuestions, String reasoning) {
            DeepThinkingResult result = new DeepThinkingResult();
            result.originalQuestion = originalQuestion;
            result.deepAnalysis = deepAnalysis;
            result.keyPoints = keyPoints;
            result.relatedQuestions = relatedQuestions;
            result.reasoning = reasoning;
            result.success = true;
            return result;
        }

        public static DeepThinkingResult failure(String originalQuestion, String errorMessage) {
            DeepThinkingResult result = new DeepThinkingResult();
            result.originalQuestion = originalQuestion;
            result.success = false;
            result.errorMessage = errorMessage;
            result.deepAnalysis = "深度思考暂时不可用";
            result.keyPoints = List.of();
            result.relatedQuestions = List.of();
            result.reasoning = "";
            return result;
        }
    }

    /**
     * 执行深度思考
     */
    public DeepThinkingResult performDeepThinking(String question, String context) {
        try {
            log.info("开始深度思考，问题: {}", question);

            if (qwenApiKey == null || qwenApiKey.trim().isEmpty()) {
                log.warn("Qwen API Key未配置，跳过深度思考");
                return DeepThinkingResult.failure(question, "AI服务未配置");
            }

            // 构建深度思考的提示词
            String prompt = buildDeepThinkingPrompt(question, context);

            // 调用Qwen API
            String response = callQwenApi(prompt);

            // 解析响应
            return parseDeepThinkingResponse(question, response);

        } catch (Exception e) {
            log.error("深度思考失败: {}", question, e);
            return DeepThinkingResult.failure(question, e.getMessage());
        }
    }

    /**
     * 构建深度思考提示词
     */
    private String buildDeepThinkingPrompt(String question, String context) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("请对以下问题进行深度思考和分析：\n\n");
        prompt.append("问题：").append(question).append("\n\n");
        
        if (context != null && !context.trim().isEmpty()) {
            prompt.append("相关背景信息：\n").append(context).append("\n\n");
        }
        
        prompt.append("请从以下几个维度进行深度分析：\n");
        prompt.append("1. 问题的本质和核心要素\n");
        prompt.append("2. 可能的多个角度和观点\n");
        prompt.append("3. 潜在的关联问题和延伸思考\n");
        prompt.append("4. 逻辑推理过程\n\n");
        
        prompt.append("请按照以下JSON格式返回结果：\n");
        prompt.append("{\n");
        prompt.append("  \"deepAnalysis\": \"深度分析内容\",\n");
        prompt.append("  \"keyPoints\": [\"要点1\", \"要点2\", \"要点3\"],\n");
        prompt.append("  \"relatedQuestions\": [\"相关问题1\", \"相关问题2\"],\n");
        prompt.append("  \"reasoning\": \"推理过程\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    /**
     * 调用Qwen API
     */
    private String callQwenApi(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            
            // 构建模型参数
            Map<String, Object> model = new HashMap<>();
            model.put("model", qwenModel);
            
            // 构建输入参数
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", prompt);
            
            // 构建参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("max_tokens", 2000);
            parameters.put("temperature", 0.7);
            parameters.put("top_p", 0.8);
            
            requestBody.put("model", qwenModel);
            requestBody.put("input", input);
            requestBody.put("parameters", parameters);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(qwenApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用Qwen API进行深度思考");
            ResponseEntity<Map> response = restTemplate.postForEntity(qwenBaseUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> output = (Map<String, Object>) responseBody.get("output");
                if (output != null) {
                    return (String) output.get("text");
                }
            }

            throw new RuntimeException("Qwen API响应异常");

        } catch (Exception e) {
            log.error("调用Qwen API失败", e);
            throw new RuntimeException("深度思考服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 解析深度思考响应
     */
    private DeepThinkingResult parseDeepThinkingResponse(String question, String response) {
        try {
            // 尝试解析JSON格式的响应
            // 这里简化处理，实际项目中可以使用Jackson等JSON库
            if (response.contains("deepAnalysis")) {
                // 模拟解析JSON
                String deepAnalysis = extractJsonValue(response, "deepAnalysis");
                List<String> keyPoints = extractJsonArray(response, "keyPoints");
                List<String> relatedQuestions = extractJsonArray(response, "relatedQuestions");
                String reasoning = extractJsonValue(response, "reasoning");

                return DeepThinkingResult.success(question, deepAnalysis, keyPoints, relatedQuestions, reasoning);
            } else {
                // 如果不是JSON格式，直接使用原文作为分析结果
                return DeepThinkingResult.success(question, response, List.of(), List.of(), "");
            }

        } catch (Exception e) {
            log.error("解析深度思考响应失败", e);
            return DeepThinkingResult.failure(question, "响应解析失败");
        }
    }

    /**
     * 从JSON字符串中提取值（简化实现）
     */
    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            log.warn("提取JSON值失败: {}", key, e);
        }
        return "";
    }

    /**
     * 从JSON字符串中提取数组（简化实现）
     */
    private List<String> extractJsonArray(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\\[([^\\]]+)\\]";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                String arrayContent = m.group(1);
                String[] items = arrayContent.split(",");
                List<String> result = new java.util.ArrayList<>();
                for (String item : items) {
                    String cleaned = item.trim().replaceAll("^\"|\"$", "");
                    if (!cleaned.isEmpty()) {
                        result.add(cleaned);
                    }
                }
                return result;
            }
        } catch (Exception e) {
            log.warn("提取JSON数组失败: {}", key, e);
        }
        return List.of();
    }

    /**
     * 格式化深度思考结果
     */
    public String formatDeepThinkingResult(DeepThinkingResult result) {
        if (result == null || !result.isSuccess()) {
            return "深度思考暂时不可用。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("深度思考分析：\n\n");
        
        if (result.getDeepAnalysis() != null && !result.getDeepAnalysis().isEmpty()) {
            sb.append("📊 核心分析：\n");
            sb.append(result.getDeepAnalysis()).append("\n\n");
        }
        
        if (result.getKeyPoints() != null && !result.getKeyPoints().isEmpty()) {
            sb.append("🔑 关键要点：\n");
            for (int i = 0; i < result.getKeyPoints().size(); i++) {
                sb.append((i + 1)).append(". ").append(result.getKeyPoints().get(i)).append("\n");
            }
            sb.append("\n");
        }
        
        if (result.getRelatedQuestions() != null && !result.getRelatedQuestions().isEmpty()) {
            sb.append("🤔 相关问题：\n");
            for (int i = 0; i < result.getRelatedQuestions().size(); i++) {
                sb.append((i + 1)).append(". ").append(result.getRelatedQuestions().get(i)).append("\n");
            }
            sb.append("\n");
        }
        
        if (result.getReasoning() != null && !result.getReasoning().isEmpty()) {
            sb.append("💭 推理过程：\n");
            sb.append(result.getReasoning()).append("\n");
        }

        return sb.toString();
    }
} 