package com.wudao.kms.llm.chat;

import com.wudao.kms.agent.dto.AssistantApiRequest;
import com.wudao.kms.agent.dto.LLMModelTestReq;
import com.wudao.kms.dto.AgentChatReq;
import com.wudao.kms.dto.RerankResp;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import org.springframework.ai.content.Media;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface ChatModelStrategy {


    /**
     * 知识库检索工具
     * @param llmModel 大模型
     * @param userPrompt 用户的prompt
     * @return sse的内容
     */
    Flux<ServerSentEvent<String>> chat(
            LLMModel llmModel,
            String userPrompt,
            Map<String, Object> toolParam,
            String sessionUuid,
            AgentChatReq chatReq,
            List<ToolCallback> toolCallbacks
    );

    /**
     * 知识库检索工具
     * @param llmModel 大模型
     * @param userPrompt 用户的prompt
     * @return sse的内容
     */
    Flux<ServerSentEvent<String>> chat(
            LLMModel llmModel,
            String userPrompt,
            Boolean webSearch,
            Boolean deepThinking,
            Boolean multimodal,
            Map<String, Object> toolParam,
            String sessionUuid,
            Long userId,
            AssistantApiRequest request
    );

    /**
     * vl识别
     * @param model 模型
     * @param prompt 用户prompt
     * @param mediaList 图片文件
     * @return 返回解释的内容
     */
    String vl(String model, String prompt, List<Media> mediaList, Long userId);

    /**
     * 判断是否支持指定的模型
     *
     * @param provider 模型厂商
     * @return 是否支持
     */
    boolean supports(String provider);

    /**
     * 向量化
     * @param model 向量模型
     * @param contents 向量内容
     * @param userId 用户ID，用于记录token使用
     * @return 返回1024维度内容
     */
    List<float[]> embedding(String model, List<String> contents, Long userId);

    /**
     * 简单对话
     * @param model 模型
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户传入的内容
     * @param formatRespFlag 结构化返回标识符
     * @return 返回大模型返回的文本内容
     */
    String simpleChat(String model, String systemPrompt, String userPrompt,Boolean formatRespFlag);

    String simpleChat(String model, String systemPrompt, String userPrompt,List<Media> mediaList);

    /**
     * 优化提示词
     * @param req 提示词优化请求
     * @return 流式响应
     */
    Flux<ServerSentEvent<String>> optimizePrompt(LLMModelTestReq req);

    /**
     * rerank
     * @param model 模型
     * @param question 问题
     * @param answer 答案
     * @param userId 用户ID，用于配额检查和记录
     * @return 重排后的结果
     */
    List<RerankResp> rerank(String model, String question, List<String> answer, Long userId);

    /**
     * 音频转中文
     *
     * @param fileUrl 文件地址
     * @param userId  用户ID
     * @param model
     * @return
     */
    String asr(String fileUrl, Long userId, String model);
}