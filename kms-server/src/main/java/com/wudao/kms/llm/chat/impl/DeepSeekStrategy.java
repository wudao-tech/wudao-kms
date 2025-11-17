package com.wudao.kms.llm.chat.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
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
import com.wudao.kms.service.VectorizationService;
import com.wudao.kms.mapper.KnowledgeFileSegmentMapper;
import com.wudao.kms.entity.KnowledgeFileSegment;
import com.wudao.kms.entity.KnowledgeFile;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class DeepSeekStrategy implements ChatModelStrategy {

    @Resource
    private AgentMessageMapper agentMessageMapper;
    @Autowired
    @Lazy
    private VectorizationService vectorizationService;
    @Resource
    private KnowledgeFileSegmentMapper knowledgeFileSegmentMapper;

    @Value("${env.api-key.deepseek:#{null}}")
    private String deepseekApiKey;

    /**
     * 在使用时直接创建ChatClient
     */
    private ChatClient createChatClient() {
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey(deepseekApiKey)
                .build();
        DeepSeekChatModel customChatModel = DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .build();
        return ChatClient.builder(customChatModel).build();
    }

    @Override
    public Flux<ServerSentEvent<String>> chat(LLMModel llmModel,
                                              String userPrompt,
                                              Map<String, Object> toolParam,
                                              String sessionUuid,
                                              AgentChatReq chatReq,
                                              List<ToolCallback> toolCallbacks) {
        Assistant assistant = chatReq.getAssistant();
        String systemPrompt = """
                   ## 任务描述
                   你是一个知识库回答助手，可以使用RAG中的内容作为你本次回答的参考。
                   ## 通用规则
                    - 如果你不清楚答案，你需要澄清。
                    - 保持答案与知识库中描述的一致。
                    - 使用 Markdown 语法优化回答格式。尤其是图片、表格、序列号等内容，需严格完整输出。
                    - 如果有合适的图片作为回答，则必须输出图片。输出图片时，仅需输出图片的 url，不要输出图片描述，例如：![](url)。
                    - 使用与问题相同的语言回答。
                """;
        var searchOptions = DashScopeApi.SearchOptions.builder()
                .forcedSearch(chatReq.getWebSearch())
                .enableSource(chatReq.getWebSearch())
                .searchStrategy("pro")
                .enableCitation(chatReq.getWebSearch())
                .citationFormat("[<number>]")
                .build();

        DeepSeekChatOptions chatOptions = DeepSeekChatOptions.builder()
                .model(llmModel.getModel())
                .toolContext(toolParam)
                .temperature(assistant.getTemperature())
                .topP(assistant.getTopP())
                .build();

        AgentMessage agentMessage = new AgentMessage();

        StringBuilder stringBuilder = new StringBuilder();
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
        Flux<ServerSentEvent<String>> referenceFlux = Flux.empty();
        Flux<ServerSentEvent<String>> llmFlux = createChatClient().prompt()
                .options(chatOptions)
                .system(assistant.getPrompt())
                .messages(memoryMessages)
                .toolCallbacks(toolCallbacks)
                .user(userPrompt)
                .stream().chatResponse().map(content -> {
                    Map<String, Object> outputData = new HashMap<>();
                    String reasoningContent = ((DeepSeekAssistantMessage) content.getResult().getOutput()).getReasoningContent();
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
        return "";
    }

    @Override
    public boolean supports(String provider) {
        return "deepseek".equals(provider);
    }

    @Override
    public List<float[]> embedding(String model, List<String> contents) {
        return new ArrayList<>();
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt, Boolean formatRespFlag) {
        DeepSeekChatOptions chatOptions = DeepSeekChatOptions.builder()
                .model("deepseek-chat")
                .temperature(0.2)
                .topP(0.5)
                .build();
        return createChatClient().prompt().options(chatOptions)
                .system(systemPrompt).user(userPrompt).call().content();
    }

    @Override
    public String simpleChat(String model, String systemPrompt, String userPrompt, List<Media> mediaList) {
        DeepSeekChatOptions chatOptions = DeepSeekChatOptions.builder()
                .model("deepseek-chat")
                .temperature(0.2)
                .topP(0.5)
                .build();
        return createChatClient().prompt().options(chatOptions)
                .system(systemPrompt).user(u -> u.text(userPrompt).media(mediaList.getFirst())).call().content();
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
                    // DeepSeek不支持reasoning content，直接设置为text类型
                    outputData.put("outputType", "text");
                    outputData.put("content", content.getResult().getOutput().getText());
                    outputData.put("timestamp", System.currentTimeMillis());
                    return ServerSentEvent.builder(JSON.toJSONString(outputData))
                            .event("assistant_message")
                            .build();
                });
    }

    @Override
    public List<RerankResp> rerank(String model, String question, List<String> answer) {
        return List.of();
    }

    @Override
    public String asr(String fileUrl, Long userId, String model) {
        return "";
    }
}
