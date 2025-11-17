package com.wudao.kms.llm.chat.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeResponseFormat;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import com.alibaba.cloud.ai.model.RerankRequest;
import com.alibaba.cloud.ai.model.RerankResponse;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
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
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.message.domain.AgentMessage;
import com.wudao.kms.llm.message.mapper.AgentMessageMapper;
import com.wudao.kms.mapper.KnowledgeFileSegmentMapper;
import com.wudao.kms.entity.KnowledgeFileSegment;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DashScopeStrategy implements ChatModelStrategy {

    @Resource
    private AgentMessageMapper agentMessageMapper;

    @Resource
    private KnowledgeFileSegmentMapper knowledgeFileSegmentMapper;

    @Value("${env.api-key.dashscope}")
    private String dashscopeApiKey;

    /**
     * 在使用时直接创建ChatClient
     */
    private ChatClient createChatClient() {
        DashScopeApi scopeApi = DashScopeApi.builder().apiKey(dashscopeApiKey).build();
        return ChatClient.create(DashScopeChatModel.builder().dashScopeApi(scopeApi).build());
    }

    /**
     * 在使用时直接创建EmbeddingModel
     */
    private DashScopeEmbeddingModel createEmbeddingModel() {
        DashScopeApi scopeApi = DashScopeApi.builder().apiKey(dashscopeApiKey).build();
        return new DashScopeEmbeddingModel(scopeApi);
    }

    /**
     * 在使用时直接创建RerankModel
     */
    private DashScopeRerankModel createRerankModel() {
        DashScopeApi scopeApi = DashScopeApi.builder().apiKey(dashscopeApiKey).build();
        return new DashScopeRerankModel(scopeApi);
    }


    @Override
    public Flux<ServerSentEvent<String>> chat(LLMModel llmModel,
                                              String userPrompt,
                                              Map<String, Object> toolParam,
                                              String sessionUuid,
                                              AgentChatReq chatReq,
                                              List<ToolCallback> toolCallbacks) {
        Assistant assistant = chatReq.getAssistant();
        var searchOptions = DashScopeApi.SearchOptions.builder()
                .forcedSearch(chatReq.getWebSearch())
                .enableSource(chatReq.getWebSearch())
                .searchStrategy("pro")
                .enableCitation(chatReq.getWebSearch())
                .citationFormat("[<number>]")
                .build();

        DashScopeChatOptions chatOptions = DashScopeChatOptions.builder()
                .withModel(llmModel.getModel())
                .withToolContext(toolParam)
                .withSearchOptions(searchOptions)
                .withTemperature(assistant.getTemperature())
                .withTopP(assistant.getTopP())
                .withEnableSearch(chatReq.getWebSearch())
                .withEnableThinking(chatReq.getWebSearch())
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
                    String reasoningContent = content.getResult().getOutput().getMetadata().get("reasoningContent").toString();
                    String currentTextData = content.getResult().getOutput().getText();

                    if (StringUtil.isNotBlank(reasoningContent)) {
                        // DeepSeek不支持reasoning content，直接设置为text类型
                        outputData.put("outputType", "reasoning");
                        outputData.put("content", reasoningContent);
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
    public String vl(String model, String prompt, List<Media> mediaList) {
// 创建包含图片的用户消息
        UserMessage message = UserMessage.builder()
                .text(prompt)
                .media(mediaList)
                .build();

        // 设置消息格式为图片
        message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

        // 创建提示词，启用多模态模型
        Prompt chatPrompt = new Prompt(message,
                DashScopeChatOptions.builder()
                        .withModel(model)  // 使用传入的模型参数
                        .withMultiModel(true)             // 启用多模态
                        .withVlHighResolutionImages(true) // 启用高分辨率图片处理
                        .withTemperature(0.7)
                        .build());
        // 调用模型进行图片分析
        return createChatClient().prompt(chatPrompt).call().content();
    }

    @Override
    public boolean supports(String provider) {
        return "qwen".equals(provider);
    }

    @Override
    public List<float[]> embedding(String model, List<String> contents) {
        if (CollUtil.isEmpty(contents)) {
            return new ArrayList<>();
        }
        // 最大支持contents为10条，需要在这里做拆分
        List<float[]> allEmbeddings = new ArrayList<>();
        List<List<String>> batches = CollUtil.split(contents, 10);

        DashScopeEmbeddingModel embeddingModel = createEmbeddingModel();
        for (List<String> batch : batches) {
            List<float[]> batchEmbeddings = embeddingModel.call(new EmbeddingRequest(
                    batch,
                    DashScopeEmbeddingOptions.builder().withModel(model).withDimensions(1024).build())
            ).getResults().stream().map(Embedding::getOutput).toList();
            allEmbeddings.addAll(batchEmbeddings);
        }

        return allEmbeddings;
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt,Boolean formatRespFlag) {
        DashScopeChatOptions options = DashScopeChatOptions
                .builder()
                .withModel(model)
                .build();
        if (formatRespFlag) {
            options.setResponseFormat(DashScopeResponseFormat.builder().type(DashScopeResponseFormat.Type.JSON_OBJECT).build());
        }
        return createChatClient().prompt().options(options).system(systemPrompt).user(userPrompt).call().content();
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt, List<Media> mediaList) {
        DashScopeChatOptions options = DashScopeChatOptions
                .builder()
                .withModel(model)
                .build();
        return createChatClient().prompt().options(options).system(systemPrompt).user(u->u.text(userPrompt).media(mediaList.getFirst())).call().content();
    }

    @Override
    public Flux<ServerSentEvent<String>> optimizePrompt(LLMModelTestReq req) {
        return createChatClient()
                .prompt()
                .options(DashScopeChatOptions.builder()
                        .withEnableThinking(false)
                        .withModel(req.getLlmModel().getModel())
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
                    Object reasoningContent = content.getResult().getOutput().getMetadata().get("reasoningContent");
                    if (reasoningContent != null && StringUtils.hasText(reasoningContent.toString())) {
                        outputData.put("outputType", "reasoning");
                        outputData.put("content", reasoningContent.toString());
                    } else {
                        outputData.put("outputType", "text");
                        outputData.put("content", content.getResult().getOutput().getText());
                    }
                    outputData.put("timestamp", System.currentTimeMillis());
                    return ServerSentEvent.builder(JSON.toJSONString(outputData))
                            .event("assistant_message")
                            .build();
                });
    }

    @Override
    public List<RerankResp> rerank(String model, String question, List<String> answer) {
        DashScopeRerankOptions scopeRerankOptions = DashScopeRerankOptions.builder().withTopN(100).withModel(model).withReturnDocuments(true).build();
        List<Document> documents = answer.stream().map(item-> Document.builder().text(item).build()).toList();
        RerankRequest request = new RerankRequest(question,documents,scopeRerankOptions);
        RerankResponse rerankResponse = createRerankModel().call(request);
        return rerankResponse.getResults().stream().map(item -> RerankResp.builder()
                .rerankScore(item.getScore()).rerankContent(item.getOutput().getText()).build()).toList();
    }

    @Override
    public String asr(String fileUrl, Long userId, String model) {
        return simpleMultiModalConversationCall(fileUrl, model);
    }

    public String simpleMultiModalConversationCall(String fileUrl, String model) {
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(List.of(
                            Collections.singletonMap("audio", fileUrl)))
                    .build();
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                    // 新加坡和北京地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
                    .apiKey(dashscopeApiKey)
                    .model(model)
                    .message(userMessage)
                    .incrementalOutput(true)
                    .languageType("zh")
                    .build();
            MultiModalConversationResult result = conv.call(param);
            return result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
        }catch (Exception e){
            log.error("语音转文字失败", e);
            return "";
        }
    }
}
