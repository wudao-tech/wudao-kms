package com.wudao.kms.llm.chat.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.model.vo.R;
import com.wudao.common.utils.StringUtils;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.service.AssistantService;
import com.wudao.kms.dto.AgentChatReq;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.chat.ChatModelStrategyFactory;
import com.wudao.kms.llm.chat.factory.ToolContext;
import com.wudao.kms.llm.chat.tool.KnowledgeTool;
import com.wudao.kms.llm.chat.tool.KnowledgeToolStrategy;
import com.wudao.kms.llm.chat.tool.WorkflowTool;
import com.wudao.kms.llm.chat.tool.WorkflowToolStrategy;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.service.LLMModelService;
import com.wudao.kms.llm.message.domain.AgentSession;
import com.wudao.kms.llm.message.service.AgentSessionService;
import com.wudao.kms.service.SearchHistoryService;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.StringUtil;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/knowledge")
@Tag(name = "知识库对话", description = "增加知识库能力搜索")
@RequiredArgsConstructor
public class KnowledgeChatController {

    private final LLMModelService llmModelService;

    private final ChatModelStrategyFactory chatModelStrategyFactory;

    private final AgentSessionService agentSessionService;

    private final AssistantService assistantService;

    @Value("${aidojo.api-url}")
    private String apiUrl;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 发送消息
     */
    @Operation(summary = "发送消息")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestBody AgentChatReq request, HttpServletResponse response) {
        response.setHeader("X-Accel-Buffering", "no");
        Assistant assistant = assistantService.getAssistantByUuid(request.getAgentUuid());
        if (assistant == null) {
            throw new ServiceException("智能体不能为空");
        }
        assistant.setActive(assistant.getActive() + 1);
        assistantService.updateById(assistant);
        request.setAssistant(assistant);
        LambdaQueryWrapper<LLMModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LLMModel::getModel, assistant.getModelName());
        wrapper.last("limit 1");
        LLMModel llmModel = llmModelService.getOne(wrapper);
        ChatModelStrategy chatModelStrategy = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode());
        Map<String, Object> toolMap = new HashMap<>();
        toolMap.put("agentUuid", request.getAgentUuid());
        AgentSession session = agentSessionService.getByUuid(request.getSessionUuid());
        if (session != null && session.getSessionName().equals("新对话")) {
            session.setSessionName(request.getQuery());
            agentSessionService.updateById(session);
        }
        if (assistant.getKnowledgeList() != null) {
            assistant.setPrompt(assistant.getPrompt() + """
                     请根据知识库搜索结果生成回答，并严格遵守以下要求：
                    
                     **内容筛选与优先级：**
                     - 分析每个检索结果的score和rerankScore，优先使用综合分数高的内容
                     - QA类型内容优先级：高分数QA > 低分数QA > 高分数DOC > 低分数DOC
                     - 对于相同信息，只引用分数最高的来源
                     - 避免引用分数过低（如score<0.5且rerankScore<0.6）的可信内容
                    
                     **引用格式规范（重要更新）：**
                     1. 同一段落中连续来自同一chunkId或者QA的内容，只在最后一个信息点添加引用标记
                     2. 格式：`[type:qa]` 或 `[type:doc,chunkId:xxx]`
                     3. 当信息来源切换chunkId时，需要在新段落的合适位置重新标注引用
                     4. 总结句、过渡句不添加引用标记
                     5. 确保引用标记准确对应到具体的信息来源
                    """);
        }

        List<ToolCallback> toolCallbacks = new ArrayList<>();

        if (CollUtil.isNotEmpty(assistant.getWorkflowUuids())) {
            String url = apiUrl + "/agent/v2/assistant/queryList";
            // 定义json header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            JSONObject body = new JSONObject();
            body.put("uuids", assistant.getWorkflowUuids());
            HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(body), headers);
            Map workflowResp = restTemplate.postForObject(url, entity, Map.class);
            Object data = workflowResp.get("data");
            // 创建工具
            JSONArray workflows = JSONArray.parseArray(JSONObject.toJSONString(data));
            workflows.forEach(item->{
                JSONObject workflow = JSONObject.parseObject(item.toString());
                String uuid = workflow.getString("uuid");
                String description = workflow.getString("description");
                String toolName = String.format("workflow_tool_%s",uuid);

                WorkflowToolStrategy workflowToolStrategy = SpringUtil.getBean(WorkflowToolStrategy.class);

                Function<WorkflowTool.Request, Object> adapterFunction = (toolRequest) -> {
                    try {
                        // 创建工具上下文
                        ToolContext context = new ToolContext();
                        context.put("uuid",uuid);
                        // 执行工具逻辑
                        return workflowToolStrategy.execute(toolRequest, context);

                    } catch (Exception e) {
                        throw new RuntimeException("工具执行失败: " + e.getMessage(), e);
                    }
                };

                // 构建FunctionToolCallback
                FunctionToolCallback<WorkflowTool.Request, Object> toolCallback = FunctionToolCallback
                        .builder(toolName, adapterFunction)
                        .description(description)
                        .inputType(WorkflowTool.Request.class)
                        .build();
                toolCallbacks.add(toolCallback);
            });
        }



        if (assistant.getKnowledgeList() != null) {
            String toolName = "knowledge_search_tool";
            String description = "知识库搜索工具，可以从指定的知识库中搜索相关信息，支持语义检索、全文检索和混合检索";
            KnowledgeToolStrategy typedStrategy = new KnowledgeToolStrategy();
            Function<KnowledgeTool.Request, Object> adapterFunction = (toolRequest) -> {
                try {
                    // 创建工具上下文
                    ToolContext context = new ToolContext();
                    for (Map.Entry<String, Object> e : toolMap.entrySet()) {
                        String key = e.getKey();
                        Object value = e.getValue();
                        context.put(key, value);
                    }
                    // 执行工具逻辑
                    return typedStrategy.execute(toolRequest, context);

                } catch (Exception e) {
                    throw new RuntimeException("工具执行失败: " + e.getMessage(), e);
                }
            };

            // 构建FunctionToolCallback
            FunctionToolCallback<KnowledgeTool.Request, Object> toolCallback = FunctionToolCallback
                    .builder(toolName, adapterFunction)
                    .description(description)
                    .inputType(typedStrategy.getMetadata().getRequestType())
                    .build();
            toolCallbacks.add(toolCallback);
        }


        // 放入searchHistory用于推荐
        return chatModelStrategy.chat(llmModel,
                request.getQuery(),
                toolMap,
                request.getSessionUuid(), request,toolCallbacks);
    }
}
