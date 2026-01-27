package com.wudao.kms.llm.chat.advisor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.wudao.kms.llm.token.service.TokenQuotaService;
import com.wudao.kms.llm.token.service.TokenUsageLogService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QuotaCheckAdvisor implements BaseAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(QuotaCheckAdvisor.class);

    public static final String USER_ID_KEY = "userId";
    public static final String OPERATION_TYPE_KEY = "operationType";
    private static final String START_TIME_KEY = "startTime";
    private static final String MODEL_KEY = "model";

    private final TokenUsageLogService tokenUsageLogService;
    private final TokenQuotaService tokenQuotaService;

    @Override
    public ChatClientRequest before(@NotNull ChatClientRequest request, @NotNull AdvisorChain chain) {
        // 从 request 的 chatOptions 中获取 userId 和 operationType
        ChatOptions chatOptions = request.prompt().getOptions();
        Map<String, Object> toolContext = getToolContext(chatOptions);

        // 检查配额
        if (toolContext != null) {
            Object userIdObj = toolContext.get(USER_ID_KEY);
            if (userIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                tokenQuotaService.checkQuota(userId);
            }
        }

        Map<String, Object> contextData = new HashMap<>();
        contextData.put(START_TIME_KEY, System.currentTimeMillis());

        if (toolContext != null) {
            contextData.putAll(toolContext);
        }

        String model = getModel(chatOptions);
        if (StrUtil.isNotEmpty(model)) {
            contextData.put(MODEL_KEY, model);
        }

        request.context().putAll(contextData);
        return request;
    }

    /**
     * 从不同类型的 ChatOptions 中获取 toolContext
     */
    private Map<String, Object> getToolContext(ChatOptions chatOptions) {
        if (chatOptions instanceof DashScopeChatOptions dashScopeOptions) {
            return dashScopeOptions.getToolContext();
        } else if (chatOptions instanceof DeepSeekChatOptions deepSeekOptions) {
            return deepSeekOptions.getToolContext();
        }
        return null;
    }

    public String getModel(ChatOptions chatOptions) {
        if (chatOptions instanceof DashScopeChatOptions dashScopeOptions) {
            return dashScopeOptions.getModel();
        } else if (chatOptions instanceof DeepSeekChatOptions deepSeekOptions) {
            return deepSeekOptions.getModel();
        }
        return null;
    }

    @Override
    public ChatClientResponse after(@NotNull ChatClientResponse response, @NotNull AdvisorChain chain) {
        ChatResponse chatResponse = response.chatResponse();
        if (chatResponse != null) {
            Usage usage = chatResponse.getMetadata().getUsage();
            if (usage != null) {
                // 从 context 获取数据
                Map<String, Object> context = response.context();

                // 计算耗时
                Object startTimeObj = context.get(START_TIME_KEY);
                Integer latencyMs = null;
                if (startTimeObj != null) {
                    latencyMs = (int) (System.currentTimeMillis() - (Long) startTimeObj);
                }

                // 获取 userId 和 operationType
                Object userIdObj = context.get(USER_ID_KEY);
                Long userId = userIdObj != null ? Long.valueOf(userIdObj.toString()) : null;

                Object operationTypeObj = context.get(OPERATION_TYPE_KEY);
                String operationType = operationTypeObj != null ? operationTypeObj.toString() : "CHAT";

                Object modelObj = context.get(MODEL_KEY);
                String model = modelObj != null ? modelObj.toString() : null;

                logger.info("Token Usage - User: {}, Model: {}, Type: {}, Input: {}, Output: {}, Total: {}, Latency: {}ms",
                        userId, model, operationType,
                        usage.getPromptTokens(),
                        usage.getCompletionTokens(),
                        usage.getTotalTokens(),
                        latencyMs);

                // 异步记录到数据库
                if (userId != null) {
                    tokenUsageLogService.logSuccess(userId, operationType, model,
                            usage.getPromptTokens() != null ? usage.getPromptTokens() : 0,
                            usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0,
                            usage.getTotalTokens() != null ? usage.getTotalTokens() : 0,
                            latencyMs);
                }
            }
        }
        return response;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
