package com.wudao.kms.llm.chat.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.dto.AssistantApiRequest;
import com.wudao.kms.agent.dto.LLMModelTestReq;
import com.wudao.kms.dto.AgentChatReq;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.RerankResp;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.KnowledgeFileSegment;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.message.domain.AgentMessage;
import com.wudao.kms.llm.message.mapper.AgentMessageMapper;
import com.wudao.kms.llm.provider.mapper.ModelProviderMapper;
import com.wudao.kms.mapper.KnowledgeFileSegmentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OllamaStrategy implements ChatModelStrategy {

    @Resource
    private AgentMessageMapper agentMessageMapper;

    @Resource
    private KnowledgeFileSegmentMapper knowledgeFileSegmentMapper;

    @Resource
    private ModelProviderMapper modelProviderMapper;

    private static final String PROVIDER_CODE = "ollama";

    /**
     * 在使用时直接创建ChatClient
     */
    private ChatClient createChatClient() {
        String baseUrl = modelProviderMapper.getByProviderCode(PROVIDER_CODE).getEndpoint();
        org.springframework.ai.ollama.api.OllamaApi ollamaApi =
                org.springframework.ai.ollama.api.OllamaApi.builder()
                        .baseUrl(baseUrl)
                        .build();
        OllamaChatModel customChatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .build();
        return ChatClient.builder(customChatModel).build();
    }

    /**
     * 在使用时直接创建EmbeddingModel
     */
    private OllamaEmbeddingModel createEmbeddingModel() {
        String baseUrl = modelProviderMapper.getByProviderCode(PROVIDER_CODE).getEndpoint();
        org.springframework.ai.ollama.api.OllamaApi ollamaApi =
                org.springframework.ai.ollama.api.OllamaApi.builder()
                        .baseUrl(baseUrl)
                        .build();
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .build();
    }

    @Override
    public Flux<ServerSentEvent<String>> chat(LLMModel llmModel, String userPrompt, Map<String, Object> toolParam, String sessionUuid, AgentChatReq chatReq, List<ToolCallback> toolCallbacks) {
        Assistant assistant = chatReq.getAssistant();

        OllamaChatOptions ollamaOptions = OllamaChatOptions.builder()
                .model(llmModel.getModel())
                .temperature(assistant.getTemperature())
                .topP(assistant.getTopP())
                .build();

        AgentMessage agentMessage = new AgentMessage();
        StringBuilder stringBuilder = new StringBuilder();

        // 构建历史消息
        LambdaQueryWrapper<AgentMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentMessage::getSessionUuid, sessionUuid);
        wrapper.eq(AgentMessage::getCreatedBy, chatReq.getUserId());
        wrapper.orderByDesc(AgentMessage::getCreatedAt);
        wrapper.last("limit 10");
        List<AgentMessage> agentMessages = agentMessageMapper.selectList(wrapper);
        List<Message> memoryMessages = new ArrayList<>();
        for (AgentMessage node : agentMessages) {
            memoryMessages.add(new UserMessage(node.getUserMessage()));
            memoryMessages.add(new AssistantMessage(node.getAgent()));
        }

        // LLM响应流
        Flux<ServerSentEvent<String>> llmFlux = createChatClient().prompt()
                .options(ollamaOptions)
                .system(assistant.getPrompt())
                .messages(memoryMessages)
                .toolCallbacks(toolCallbacks)
                .user(userPrompt)
                .stream().chatResponse().map(content -> {
                    Map<String, Object> outputData = new HashMap<>();
                    String currentTextData = content.getResult().getOutput().getText();

                    outputData.put("outputType", "text");
                    outputData.put("content", currentTextData);
                    stringBuilder.append(currentTextData);
                    outputData.put("timestamp", System.currentTimeMillis());

                    return ServerSentEvent.builder(JSON.toJSONString(outputData))
                            .event("assistant_message")
                            .build();
                }).doFinally(content -> {
                    agentMessage.setSessionUuid(sessionUuid);
                    agentMessage.setUserMessage(userPrompt);
                    agentMessage.setChatUuid(chatReq.getChatUuid());
                    agentMessage.setAgent(stringBuilder.toString());
                    agentMessage.setCreatedBy(chatReq.getUserId());
                    agentMessageMapper.insert(agentMessage);
                });

        // 构建fileId引用流
        Flux<ServerSentEvent<String>> fileReferenceFlux = Flux.defer(() -> {
            String fullResponse = stringBuilder.toString();

            // 正则提取chunkId: [type:doc,chunkId:xxx]
            Pattern pattern = Pattern.compile("\\[type:doc,chunkId:(\\d+)\\]");
            Matcher matcher = pattern.matcher(fullResponse);

            Set<Long> chunkIds = new HashSet<>();
            while (matcher.find()) {
                try {
                    chunkIds.add(Long.parseLong(matcher.group(1)));
                } catch (NumberFormatException e) {
                    log.warn("Invalid chunkId format: {}", matcher.group(1));
                }
            }

            if (chunkIds.isEmpty()) {
                return Flux.empty();
            }

            // 根据chunkId查询fileId和文件信息
            MPJLambdaWrapper<KnowledgeFileSegment> referenceWrapper = new MPJLambdaWrapper<>();
            referenceWrapper.selectAs(KnowledgeFileSegment::getId, KnowledgeSearchResult::getChunkId)
                    .selectAs(KnowledgeFileSegment::getFileId, KnowledgeSearchResult::getDocumentId)
                    .selectAs(KnowledgeFile::getFileName, KnowledgeSearchResult::getFilename)
                    .selectAs(KnowledgeFile::getFileType, KnowledgeSearchResult::getFileType)
                    .selectAs(KnowledgeFile::getFilePath, KnowledgeSearchResult::getS3Url);
            referenceWrapper.in(KnowledgeFileSegment::getId, chunkIds);
            referenceWrapper.leftJoin(KnowledgeFile.class, KnowledgeFile::getId, KnowledgeFileSegment::getFileId);
            List<KnowledgeSearchResult> rawSegments = knowledgeFileSegmentMapper.selectJoinList(KnowledgeSearchResult.class, referenceWrapper);

            if (CollUtil.isEmpty(rawSegments)) {
                return Flux.empty();
            }

            // 根据fileId去重
            List<KnowledgeSearchResult> segments = new ArrayList<>(rawSegments.stream()
                    .filter(item -> item.getDocumentId() != null)
                    .collect(Collectors.toMap(
                            KnowledgeSearchResult::getDocumentId,
                            Function.identity(),
                            (existing, replacement) -> existing
                    ))
                    .values());

            agentMessage.setQuoteList(segments);

            JSONObject fileData = new JSONObject();
            fileData.put("quoteList", segments);
            fileData.put("timestamp", System.currentTimeMillis());

            return Flux.just(ServerSentEvent.builder(fileData.toJSONString())
                    .event("reference_data")
                    .build());
        });

        return llmFlux.concatWith(fileReferenceFlux);
    }

    @Override
    public Flux<ServerSentEvent<String>> chat(LLMModel llmModel, String userPrompt, Boolean webSearch, Boolean deepThinking, Boolean multimodal, Map<String, Object> toolParam, String sessionUuid, Long userId, AssistantApiRequest request) {
        // Ollama 不支持此高级聊天接口
        log.warn("Ollama 暂不支持高级聊天接口（webSearch, deepThinking等功能）");
        return Flux.empty();
    }

    @Override
    public String vl(String model, String prompt, List<Media> mediaList, Long userId) {
        // 使用Ollama的多模态模型（如llava）
        try {
            UserMessage message = UserMessage.builder()
                    .text(prompt)
                    .media(mediaList)
                    .build();

            Prompt chatPrompt = new Prompt(message,
                    OllamaChatOptions.builder()
                            .model(model)  // 使用支持视觉的模型，如 llava
                            .build());

            return createChatClient().prompt(chatPrompt).call().content();
        } catch (Exception e) {
            log.error("Ollama多模态调用失败", e);
            return "";
        }
    }

    @Override
    public boolean supports(String provider) {
        return "ollama".equals(provider);
    }

    @Override
    public List<float[]> embedding(String model, List<String> contents, Long userId) {
        if (CollUtil.isEmpty(contents)) {
            return new ArrayList<>();
        }

        try {
            // Ollama embedding支持批量处理
            EmbeddingRequest request = new EmbeddingRequest(contents,
                    EmbeddingOptions.builder()
                            .model(model)
                            .build());

            OllamaEmbeddingModel embeddingModel = createEmbeddingModel();
            EmbeddingResponse response = embeddingModel.call(request);
            return response.getResults().stream()
                    .map(Embedding::getOutput)
                    .toList();
        } catch (Exception e) {
            log.error("Ollama向量化失败: model={}", model, e);
            return new ArrayList<>();
        }
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt, Boolean formatRespFlag) {
        try {
            OllamaChatOptions options = OllamaChatOptions.builder()
                    .model(model)
                    .build();

            // Ollama 不支持强制JSON格式，但可以在prompt中要求
            if (Boolean.TRUE.equals(formatRespFlag)) {
                systemPrompt = (systemPrompt == null ? "" : systemPrompt) +
                        "\n请以JSON格式返回响应。";
            }

            return createChatClient().prompt()
                    .options(options)
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Ollama简单对话失败: model={}", model, e);
            return "";
        }
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt, List<Media> mediaList) {
        try {
            OllamaChatOptions options = OllamaChatOptions.builder()
                    .model(model)
                    .build();

            return createChatClient().prompt()
                    .options(options)
                    .system(systemPrompt)
                    .user(u -> u.text(userPrompt).media(mediaList.isEmpty() ? null : mediaList.getFirst()))
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Ollama多模态简单对话失败: model={}", model, e);
            return "";
        }
    }

    @Override
    public Flux<ServerSentEvent<String>> optimizePrompt(LLMModelTestReq req) {
        return createChatClient()
                .prompt()
                .options(OllamaChatOptions.builder()
                        .model(req.getLlmModel().getModel())
                        .build())
                .user(req.getPrompt())
                .system(req.getSystemPrompt())
                .stream()
                .chatResponse()
                .map(content -> {
                    Map<String, Object> outputData = new HashMap<>();
                    outputData.put("sessionUuid", req.getSessionUuid());
                    if (content.getResults() == null || content.getResults().isEmpty()) {
                        return ServerSentEvent.builder("assistant_error")
                                .data("No results found")
                                .build();
                    }
                    outputData.put("outputType", "text");
                    outputData.put("content", content.getResult().getOutput().getText());
                    outputData.put("timestamp", System.currentTimeMillis());
                    return ServerSentEvent.builder(JSON.toJSONString(outputData))
                            .event("assistant_message")
                            .build();
                });
    }

    @Override
    public List<RerankResp> rerank(String model, String question, List<String> answer, Long userId) {
        // Ollama 不支持 rerank 功能
        log.warn("Ollama 不支持 rerank 功能，返回空列表");
        return List.of();
    }

    @Override
    public String asr(String fileUrl, Long userId, String model) {
        // Ollama 不支持 ASR（语音识别）功能
        log.warn("Ollama 不支持 ASR 功能");
        return "";
    }
}
