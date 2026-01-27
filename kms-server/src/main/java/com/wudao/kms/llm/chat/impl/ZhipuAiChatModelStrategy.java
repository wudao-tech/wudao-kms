package com.wudao.kms.llm.chat.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.dto.AssistantApiRequest;
import com.wudao.kms.agent.dto.LLMModelTestReq;
import com.wudao.kms.dto.AgentChatReq;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.RerankResp;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.message.domain.AgentMessage;
import com.wudao.kms.llm.message.mapper.AgentMessageMapper;
import com.wudao.kms.llm.provider.mapper.ModelProviderMapper;
import com.wudao.kms.service.VectorizationService;
import com.wudao.kms.mapper.KnowledgeFileSegmentMapper;
import com.wudao.kms.entity.KnowledgeFileSegment;
import com.wudao.kms.entity.KnowledgeFile;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ZhipuAiChatModelStrategy implements ChatModelStrategy {

    @Resource
    private AgentMessageMapper agentMessageMapper;

    @Resource
    @Lazy
    private VectorizationService vectorizationService;

    @Resource
    private KnowledgeFileSegmentMapper knowledgeFileSegmentMapper;

    @Resource
    private ModelProviderMapper modelProviderMapper;

    private static final String PROVIDER_CODE = "zhipu";

    /**
     * 在使用时直接创建ChatClient
     */
    private ChatClient createChatClient() {
        String apiKey = modelProviderMapper.getByProviderCode(PROVIDER_CODE).getApiKey();
        ZhiPuAiApi zhiPuAiApi = ZhiPuAiApi.builder().apiKey(apiKey).build();
        ZhiPuAiChatModel customChatModel = new ZhiPuAiChatModel(zhiPuAiApi);
        return ChatClient.builder(customChatModel).build();
    }

    /**
     * 在使用时直接创建EmbeddingModel
     */
    private ZhiPuAiEmbeddingModel createEmbeddingModel() {
        String apiKey = modelProviderMapper.getByProviderCode(PROVIDER_CODE).getApiKey();
        ZhiPuAiApi zhiPuAiApi = ZhiPuAiApi.builder().apiKey(apiKey).build();
        return new ZhiPuAiEmbeddingModel(zhiPuAiApi);
    }


    @Override
    public Flux<ServerSentEvent<String>> chat(LLMModel llmModel,
                                              String userPrompt,
                                              Map<String, Object> toolParam,
                                              String sessionUuid,
                                              AgentChatReq chatReq,
                                              List<ToolCallback> toolCallbacks) {
        Assistant assistant = chatReq.getAssistant();

        ZhiPuAiChatOptions chatOptions = ZhiPuAiChatOptions.builder()
                .model(llmModel.getModel())
                .toolContext(toolParam)
                .temperature(assistant.getTemperature())
                .topP(assistant.getTopP())
                .build();

        AgentMessage agentMessage = new AgentMessage();

        StringBuilder stringBuilder = new StringBuilder();

        // 构建引用数据事件流
        Flux<ServerSentEvent<String>> referenceFlux = Flux.empty();
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
                .options(chatOptions)
                .system(assistant.getPrompt())
                .messages(memoryMessages)
                .toolCallbacks(toolCallbacks)
                .user(userPrompt)
                .stream().chatResponse().map(content -> {
                    Map<String, Object> outputData = new HashMap<>();
                    Object reasoningContent = content.getResult().getOutput().getMetadata().get("reasoningContent");
                    String currentTextData = content.getResult().getOutput().getText();

                    if (reasoningContent != null) {
                        // DeepSeek不支持reasoning content，直接设置为text类型
                        outputData.put("outputType", "reasoning");
                        outputData.put("content", reasoningContent.toString());
                    } else {
                        outputData.put("outputType", "text");
                        outputData.put("content", currentTextData);
                    }
                    stringBuilder.append(currentTextData);

                    outputData.put("timestamp", System.currentTimeMillis());

                    return ServerSentEvent.builder(com.alibaba.fastjson.JSON.toJSONString(outputData))
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

        // 构建fileId引用流 - 在LLM响应完成后发送
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

            // 根据fileId(documentId)去重，保留第一个出现的记录
            List<KnowledgeSearchResult> segments = new ArrayList<>(rawSegments.stream()
                    .filter(item -> item.getDocumentId() != null)
                    .collect(Collectors.toMap(
                            KnowledgeSearchResult::getDocumentId,
                            Function.identity(),
                            (existing, replacement) -> existing
                    ))
                    .values());

            // 设置 quoteList，后续在 doFinally 中统一插入数据库
            agentMessage.setQuoteList(segments);

            JSONObject fileData = new JSONObject();
            fileData.put("quoteList", segments);
            fileData.put("timestamp", System.currentTimeMillis());

            return Flux.just(ServerSentEvent.builder(fileData.toJSONString())
                    .event("reference_data")
                    .build());
        });

        // 先发送引用数据,再发送LLM响应,最后发送fileId引用
        return referenceFlux.concatWith(llmFlux).concatWith(fileReferenceFlux);
    }

    @Override
    public Flux<ServerSentEvent<String>> chat(LLMModel llmModel, String userPrompt, Boolean webSearch, Boolean deepThinking, Boolean multimodal, Map<String, Object> toolParam, String sessionUuid, Long userId, AssistantApiRequest request) {
        return null;
    }

    @Override
    public String vl(String model, String prompt, List<Media> mediaList, Long userId) {
        return "";
    }

    @Override
    public boolean supports(String provider) {
        return "zhipu".equals(provider);
    }

    @Override
    public List<float[]> embedding(String model, List<String> contents, Long userId) {
        if (CollUtil.isEmpty(contents)) {
            return new ArrayList<>();
        }
        // 最大支持contents为64条，需要在这里做拆分
        List<float[]> allEmbeddings = new ArrayList<>();
        List<List<String>> batches = CollUtil.split(contents, 64);

        ZhiPuAiEmbeddingModel embeddingModel = createEmbeddingModel();
        for (List<String> batch : batches) {
            ZhiPuAiEmbeddingOptions embeddingOptions = ZhiPuAiEmbeddingOptions.builder().model(model).dimensions(1024).build();
            EmbeddingRequest request = new EmbeddingRequest(batch, embeddingOptions);
            EmbeddingResponse embeddingResponse = embeddingModel.call(request);
            List<float[]> batchEmbeddings = embeddingResponse.getResults().stream().map(Embedding::getOutput).toList();
            allEmbeddings.addAll(batchEmbeddings);
        }

        return allEmbeddings;
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt, Boolean formatRespFlag) {
        return "";
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt, List<Media> mediaList) {
        return "";
    }

    @Override
    public Flux<ServerSentEvent<String>> optimizePrompt(LLMModelTestReq req) {
        return createChatClient()
                .prompt()
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
                    // ZhipuAI不支持reasoning content，直接设置为text类型
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
        return List.of();
    }

    @Override
    public String asr(String fileUrl, Long userId, String model) {
        return "";
    }
}