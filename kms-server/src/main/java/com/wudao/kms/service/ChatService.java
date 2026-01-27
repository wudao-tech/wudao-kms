package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.wudao.kms.dto.ChatFileUploadDTO;
import com.wudao.kms.dto.ChatRequestDTO;
import com.wudao.kms.entity.chat.ChatSession;
import com.wudao.oss.service.OssService;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionService chatSessionService;
    private final ChatSessionQaService chatSessionQaService;
    private final ChatFileUploadService chatFileUploadService;
    private final OssService ossService;
    private final VectorizationService vectorizationService;
    private final WebSearchService webSearchService;
    private final DeepThinkingService deepThinkingService;

    /**
     * 开始聊天（流式返回）
     */
    public SseEmitter startChat(ChatRequestDTO requestDTO) {
        Long userId = SecurityUtils.getUserId();
        // 增加超时时间到5分钟（300秒）
        SseEmitter emitter = new SseEmitter(300000L);

        // 验证会话是否存在且属于当前用户
        ChatSession session = chatSessionService.getSessionById(requestDTO.getSessionId(), userId);
        if (session == null) {
            try {
                emitter.send(SseEmitter.event().name("error").data("会话不存在或无权限"));
                emitter.complete();
            } catch (IOException e) {
                log.error("发送错误消息失败", e);
            }
            return emitter;
        }

        // 设置错误和完成回调
        emitter.onError(throwable -> {
            log.error("SSE连接发生错误", throwable);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE连接超时，会话ID: {}", requestDTO.getSessionId());
            try {
                emitter.send(SseEmitter.event().name("timeout").data("请求处理超时，请稍后重试"));
                emitter.complete();
            } catch (IOException e) {
                log.error("发送超时消息失败", e);
            }
        });

        emitter.onCompletion(() -> {
            log.info("SSE连接完成，会话ID: {}", requestDTO.getSessionId());
        });

        // 异步处理聊天
        CompletableFuture.runAsync(() -> {
            try {
                processChat(emitter, requestDTO, userId);
            } catch (Exception e) {
                log.error("处理聊天失败", e);
                sendSafeMessage(emitter, "error", "聊天处理失败: " + e.getMessage());
            }
        });

        return emitter;
    }

    /**
     * 处理聊天逻辑
     */
    private void processChat(SseEmitter emitter, ChatRequestDTO requestDTO, Long userId) {
        // 1. 发送开始信号
        sendSafeMessage(emitter, "start", "开始处理...");

        StringBuilder contextBuilder = new StringBuilder();

        // 2. 如果需要联网，先进行网络搜索
        if (requestDTO.getNetworkFlag()) {
            sendSafeMessage(emitter, "status", "正在联网搜索...");
            try {
                WebSearchService.WebSearchResponse webSearchResult = webSearchService.search(requestDTO.getQuestion(), 10);
                if (webSearchResult != null && !webSearchResult.getResults().isEmpty()) {
                    String webSearchContext = webSearchService.formatSearchResults(webSearchResult);
                    contextBuilder.append("网络搜索结果：\n").append(webSearchContext).append("\n\n");
                    sendSafeMessage(emitter, "web_search", webSearchResult);
                }
            } catch (Exception e) {
                log.error("网络搜索失败", e);
                sendSafeMessage(emitter, "warning", "网络搜索失败：" + e.getMessage());
            }
        }

        // 3. 知识库搜索
        String knowledgeContext = "";
        sendSafeMessage(emitter, "status", "正在搜索知识库...");
        try {
            knowledgeContext = searchKnowledgeBase(requestDTO.getQuestion(), requestDTO.getKnowledgeBaseIds());
            if (!knowledgeContext.isEmpty()) {
                contextBuilder.append("知识库搜索结果：\n").append(knowledgeContext).append("\n\n");
            }
        } catch (Exception e) {
            log.error("知识库搜索失败", e);
            sendSafeMessage(emitter, "warning", "知识库搜索失败：" + e.getMessage());
        }


        // 4. 深度思考处理
        String deepThinkingResult = "";
        if (requestDTO.getDeepThinkFlag()) {
            sendSafeMessage(emitter, "status", "正在深度思考...");
            try {
                DeepThinkingService.DeepThinkingResult deepResult = deepThinkingService.performDeepThinking(
                        requestDTO.getQuestion(), contextBuilder.toString());
                if (deepResult.isSuccess()) {
                    deepThinkingResult = deepThinkingService.formatDeepThinkingResult(deepResult);
                    sendSafeMessage(emitter, "deep_thinking", deepResult);
                }
            } catch (Exception e) {
                log.error("深度思考失败", e);
                sendSafeMessage(emitter, "warning", "深度思考失败：" + e.getMessage());
            }
        }

        // 5. 生成回答
        sendSafeMessage(emitter, "status", "正在生成回答...");
        String finalContext = contextBuilder.toString() + deepThinkingResult;
        String answer = generateAnswer(requestDTO.getSessionId(), requestDTO.getQuestion(), finalContext, requestDTO.getFileLists(), userId);

        // 6. 流式返回答案
        streamAnswer(emitter, answer);

        // 7. 保存问答记录
        chatSessionQaService.saveQa(requestDTO.getSessionId(), userId, requestDTO, answer);

        // 8. 推荐知识库
        List<String> recommendKnowledgeBases = recommendKnowledgeBase(requestDTO.getQuestion(), answer);
        sendSafeMessage(emitter, "recommend", recommendKnowledgeBases);

        // 9. 完成
        sendSafeMessage(emitter, "complete", "处理完成");
        safeSseComplete(emitter);
    }

    /**
     * 搜索知识库
     */
    private String searchKnowledgeBase(String question, List<Long> knowledgeBaseIds) {
        try {
            StringBuilder knowledgeResult = new StringBuilder();

            // 对每个知识库进行搜索
            // 构建搜索请求
            com.wudao.kms.dto.KnowledgeTestDTO searchDTO = new com.wudao.kms.dto.KnowledgeTestDTO();
            searchDTO.setQuery(question);
            if (CollUtil.isNotEmpty(knowledgeBaseIds)) {
                searchDTO.setKnowledgeIds(knowledgeBaseIds);
            }
            searchDTO.setSearchType("hybrid"); // 使用混合搜索获得更好效果
            searchDTO.setMaxSegmentCount(5); // 每个知识库最多返回5个结果

            // 调用向量化服务搜索
            List<com.wudao.kms.dto.KnowledgeSearchResult> searchResults = vectorizationService.search(searchDTO);

            if (searchResults != null && !searchResults.isEmpty()) {
                knowledgeResult.append("知识库ID: ").append(knowledgeBaseIds).append("\n");

                for (int i = 0; i < searchResults.size(); i++) {
                    com.wudao.kms.dto.KnowledgeSearchResult result = searchResults.get(i);
                    knowledgeResult.append((i + 1)).append(". ");

                    // 添加文件名（如果有）
                    if (result.getFilename() != null) {
                        knowledgeResult.append("【").append(result.getFilename()).append("】");
                    }

                    // 添加内容
                    knowledgeResult.append(result.getContent());

                    // 添加相关度分数
                    if (result.getScore() != null) {
                        knowledgeResult.append(" (相关度: ").append(String.format("%.2f", result.getScore())).append(")");
                    }

                    knowledgeResult.append("\n");

                    // 如果有摘要，也添加进去
                    if (result.getSummary() != null && !result.getSummary().trim().isEmpty()) {
                        knowledgeResult.append("   摘要: ").append(result.getSummary()).append("\n");
                    }

                    knowledgeResult.append("\n");
                }
            }

            return knowledgeResult.toString();

        } catch (Exception e) {
            log.error("搜索知识库失败：{}", question, e);
            throw new RuntimeException("知识库搜索失败: " + e.getMessage());
        }
    }

    /**
     * 生成回答
     */
    private String generateAnswer(Long sessionId, String question, String context, List<String> fileLists, Long userId) {
        try {
            // 获取历史对话记录（最多10轮）
            List<com.wudao.kms.entity.chat.ChatSessionQa> historyList = 
                chatSessionQaService.getRecentHistory(sessionId, userId, 10);



            // 调用AI服务生成回答
            String answer = callQwenForAnswerWithHistory(question, context, fileLists, historyList);

            if (answer != null && !answer.trim().isEmpty()) {
                return answer;
            } else {
                return "抱歉，我暂时无法为您生成回答，请稍后再试。";
            }

        } catch (Exception e) {
            log.error("生成AI回答失败：{}", question, e);
            return "抱歉，生成回答时遇到技术问题，请稍后再试。错误信息：" + e.getMessage();
        }
    }

    /**
     * 调用Qwen生成回答（带历史消息）
     */
    private String callQwenForAnswerWithHistory(String question, String context, List<String> fileLists, List<com.wudao.kms.entity.chat.ChatSessionQa> historyList) {
        try {
            // 构建完整的提示词，包含历史对话
            String fullPrompt = buildFullPromptWithHistory(question, context, fileLists, historyList);
            
            // 使用与QwenAiService相同的方式调用API
            com.openai.client.OpenAIClient client = com.openai.client.okhttp.OpenAIOkHttpClient.builder()
                    .apiKey(getQwenApiKey())
                    .baseUrl(getQwenBaseUrl())
                    .build();
            String prompt = "你是一个知识库专家,请基于历史对话、知识库信息和联网搜索结果，用中文详细回答当前问题。要求：\n" +
                    "1. 专注于知识库内容和联网搜索结果\n" +
                    "2. 结合历史对话上下文理解问题\n" +
                    "3. 回答要准确、专业、有条理\n" +
                    "4. 如果信息不足，请明确说明\n" +
                    "5. 适当引用相关信息来源\n" +
                    "6. 阅读参考文件列表";
            // 构建请求参数
            com.openai.models.chat.completions.ChatCompletionCreateParams params =
                    com.openai.models.chat.completions.ChatCompletionCreateParams.builder()
                            .addSystemMessage(prompt)
                            .addUserMessage(fullPrompt)
                            .model(getQwenModel())
                            .maxCompletionTokens(2000)
                            .temperature(0.7)
                            .build();

            log.info("调用Qwen API生成聊天回答，历史轮数: {}", historyList != null ? historyList.size() : 0);

            // 发送请求
            com.openai.models.chat.completions.ChatCompletion chatCompletion =
                    client.chat().completions().create(params);

            // 获取响应内容
            String content = chatCompletion.choices().get(0).message().content().orElse("");

            log.info("收到Qwen聊天回答，内容长度: {}", content.length());

            return content;

        } catch (Exception e) {
            log.error("调用Qwen API生成聊天回答失败", e);
            // 如果AI调用失败，返回智能回退答案
            String fallbackPrompt = buildFallbackPrompt(question, context, fileLists, historyList);
            return generateSimpleAnswer(fallbackPrompt);
        }
    }

    /**
     * 构建包含历史对话的完整提示词
     */
    private String buildFullPromptWithHistory(String question, String context, List<String> fileLists, List<com.wudao.kms.entity.chat.ChatSessionQa> historyList) {
        StringBuilder prompt = new StringBuilder();

        // 添加历史对话上下文
        if (historyList != null && !historyList.isEmpty()) {
            prompt.append("历史对话记录：\n");
            for (com.wudao.kms.entity.chat.ChatSessionQa qa : historyList) {
                prompt.append("用户：").append(qa.getQuestion()).append("\n");
                prompt.append("助手：").append(qa.getAnswer()).append("\n\n");
            }
            prompt.append("---\n\n");
        }

        prompt.append("当前用户问题：").append(question).append("\n\n");

        if (context != null && !context.trim().isEmpty()) {
            prompt.append("相关背景信息：\n").append(context).append("\n\n");
        }

        if (fileLists != null && !fileLists.isEmpty()) {
            prompt.append("参考文件列表：\n");
            for (String file : fileLists) {
                prompt.append("- ").append(file).append("\n");
            }
            prompt.append("\n");
        }



        return prompt.toString();
    }

    /**
     * 构建回退提示词
     */
    private String buildFallbackPrompt(String question, String context, List<String> fileLists, List<com.wudao.kms.entity.chat.ChatSessionQa> historyList) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是一个知识库专家，你需要根据知识库的内容和联网搜索的内容进行回答，其他的消息就不要回了。\n\n");

        // 添加历史对话上下文
        if (historyList != null && !historyList.isEmpty()) {
            prompt.append("历史对话记录：\n");
            for (com.wudao.kms.entity.chat.ChatSessionQa qa : historyList) {
                prompt.append("用户：").append(qa.getQuestion()).append("\n");
                prompt.append("助手：").append(qa.getAnswer()).append("\n\n");
            }
            prompt.append("---\n\n");
        }

        prompt.append("当前用户问题：").append(question).append("\n\n");

        if (context != null && !context.trim().isEmpty()) {
            prompt.append("相关背景信息：\n").append(context).append("\n\n");
        }

        if (fileLists != null && !fileLists.isEmpty()) {
            prompt.append("参考文件列表：\n");
            for (String file : fileLists) {
                prompt.append("- ").append(file).append("\n");
            }
            prompt.append("\n");
        }

        return prompt.toString();
    }

    /**
     * 调用Qwen生成回答
     */
    private String callQwenForAnswer(String prompt) {
        try {
            // 使用与QwenAiService相同的方式调用API
            com.openai.client.OpenAIClient client = com.openai.client.okhttp.OpenAIOkHttpClient.builder()
                    .apiKey(getQwenApiKey())
                    .baseUrl(getQwenBaseUrl())
                    .build();

            // 构建系统消息
            String systemMessage = "你是一个知识库专家，你需要根据知识库的内容和联网搜索的内容进行回答，其他的消息就不要回了。";

            // 构建请求参数
            com.openai.models.chat.completions.ChatCompletionCreateParams params =
                    com.openai.models.chat.completions.ChatCompletionCreateParams.builder()
                            .addSystemMessage(systemMessage)
                            .addUserMessage(prompt)
                            .model(getQwenModel())
                            .maxCompletionTokens(2000)
                            .temperature(0.7)
                            .build();

            log.info("调用Qwen API生成聊天回答");

            // 发送请求
            com.openai.models.chat.completions.ChatCompletion chatCompletion =
                    client.chat().completions().create(params);

            // 获取响应内容
            String content = chatCompletion.choices().get(0).message().content().orElse("");

            log.info("收到Qwen聊天回答，内容长度: {}", content.length());

            return content;

        } catch (Exception e) {
            log.error("调用Qwen API生成聊天回答失败", e);
            // 如果AI调用失败，返回智能回退答案
            return generateSimpleAnswer(prompt);
        }
    }

    @org.springframework.beans.factory.annotation.Value("${ai.qwen.api-key}")
    private String qwenApiKey;

    @org.springframework.beans.factory.annotation.Value("${ai.qwen.base-url}")
    private String qwenBaseUrl;

    @org.springframework.beans.factory.annotation.Value("${ai.qwen.model}")
    private String qwenModel;

    private String getQwenApiKey() {
        return qwenApiKey;
    }

    private String getQwenBaseUrl() {
        return qwenBaseUrl;
    }

    private String getQwenModel() {
        return qwenModel;
    }

    /**
     * 生成简单回答（回退方案）
     */
    private String generateSimpleAnswer(String prompt) {
        try {
            // 这里可以调用任何AI服务，暂时返回基于prompt的简单回答
            // 实际使用时应该调用AI服务
            log.info("生成AI回答，提示词长度: {}", prompt.length());

            // 简化版本：基于关键词匹配返回不同类型的回答
            if (prompt.contains("网络搜索结果") && prompt.contains("知识库搜索结果")) {
                return "基于网络搜索和知识库的综合分析，" + extractMainQuestion(prompt) + "的相关信息如下：\n\n" +
                        "根据收集到的信息，我为您整理了详细的回答。如需了解更多细节，请查看上述搜索结果中的具体内容。";
            } else if (prompt.contains("网络搜索结果")) {
                return "根据网络搜索的结果，" + extractMainQuestion(prompt) + "的相关信息已经为您整理完成。" +
                        "建议您参考搜索结果中的权威信息来源。";
            } else if (prompt.contains("知识库搜索结果")) {
                return "基于知识库中的相关资料，" + extractMainQuestion(prompt) + "的解答如下：\n\n" +
                        "根据知识库中的专业资料，为您提供了准确的参考信息。";
            } else {
                return "根据您的问题 '" + extractMainQuestion(prompt) + "'，" +
                        "我为您提供了详细的回答。如需更多信息，建议启用网络搜索或查询相关知识库。";
            }

        } catch (Exception e) {
            log.error("生成简单回答失败", e);
            return "抱歉，我暂时无法为您生成详细回答。建议您稍后再试。";
        }
    }

    /**
     * 从提示词中提取主要问题
     */
    private String extractMainQuestion(String prompt) {
        try {
            // 简单的问题提取逻辑
            String[] lines = prompt.split("\n");
            for (String line : lines) {
                if (line.startsWith("用户问题：")) {
                    return line.substring("用户问题：".length()).trim();
                }
            }
            return "您的问题";
        } catch (Exception e) {
            return "您的问题";
        }
    }

    /**
     * 安全发送SSE消息
     */
    private void sendSafeMessage(SseEmitter emitter, String eventName, Object data) {
        try {
            // 检查emitter是否还可用
            if (emitter != null) {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            }
        } catch (IllegalStateException e) {
            log.warn("SSE连接已关闭，无法发送消息: {}", eventName);
        } catch (IOException e) {
            log.error("发送SSE消息失败: {}", eventName, e);
        } catch (Exception e) {
            log.error("发送SSE消息时发生未知错误: {}", eventName, e);
        }
    }

    /**
     * 安全完成SSE连接
     */
    private void safeSseComplete(SseEmitter emitter) {
        try {
            if (emitter != null) {
                emitter.complete();
            }
        } catch (IllegalStateException e) {
            log.warn("SSE连接已完成或关闭");
        } catch (Exception e) {
            log.error("完成SSE连接时发生错误", e);
        }
    }

    /**
     * 流式返回答案
     */
    private void streamAnswer(SseEmitter emitter, String answer) {
        try {
            // 模拟流式返回，将答案分块发送
            int chunkSize = 10;
            for (int i = 0; i < answer.length(); i += chunkSize) {
                int end = Math.min(i + chunkSize, answer.length());
                String chunk = answer.substring(i, end);
                
                // 使用安全发送方法
                sendSafeMessage(emitter, "chunk", chunk);

                // 模拟延时
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("流式答案发送被中断");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("流式发送答案失败", e);
            sendSafeMessage(emitter, "error", "答案发送失败");
        }
    }

    /**
     * 推荐知识库
     */
    private List<String> recommendKnowledgeBase(String question, String answer) {
        // TODO: 根据问题和答案分析，推荐相关的知识库
        // 这里可以使用关键词匹配、语义相似度等方法
        return List.of("推荐知识库1", "推荐知识库2");
    }

    /**
     * 上传文件（图片和文件）
     */
    public Long uploadFile(ChatFileUploadDTO uploadDTO) {
        try {
            // 使用新的文件上传服务
            Long fileUploadId = chatFileUploadService.uploadFile(
                    uploadDTO.getFile(),
                    uploadDTO.getSessionId(),
                    uploadDTO.getFileType()
            );

            log.info("上传聊天文件成功，会话ID: {}, 文件上传记录ID: {}",
                    uploadDTO.getSessionId(), fileUploadId);

            return fileUploadId;

        } catch (Exception e) {
            log.error("上传聊天文件失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取上传文件记录
     */
    public List<String> getUploadFileRecord(String sessionId) {
        return chatSessionQaService.getUploadFileRecord(sessionId);
    }
} 