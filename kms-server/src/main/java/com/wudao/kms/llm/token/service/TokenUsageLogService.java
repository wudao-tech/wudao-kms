package com.wudao.kms.llm.token.service;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.kms.llm.token.domain.TokenUsageLog;
import com.wudao.kms.llm.token.mapper.TokenUsageLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenUsageLogService extends MPJBaseServiceImpl<TokenUsageLogMapper, TokenUsageLog> {

    /**
     * 异步记录 Token 使用情况
     */
    @Async
    public void logTokenUsageAsync(Long userId, String operationType, String model,
                                   Integer inputTokens, Integer outputTokens, Integer totalTokens,
                                   Integer latencyMs, String status, String errorMessage) {
        try {
            TokenUsageLog tokenUsageLog = new TokenUsageLog();
            tokenUsageLog.setUserId(userId);
            tokenUsageLog.setOperationType(operationType);
            tokenUsageLog.setModel(model);
            tokenUsageLog.setInputTokens(inputTokens != null ? inputTokens : 0);
            tokenUsageLog.setOutputTokens(outputTokens != null ? outputTokens : 0);
            tokenUsageLog.setTotalTokens(totalTokens != null ? totalTokens : 0);
            tokenUsageLog.setLatencyMs(latencyMs);
            tokenUsageLog.setStatus(status);
            tokenUsageLog.setErrorMessage(errorMessage);
            this.save(tokenUsageLog);
            log.debug("Token usage logged - User: {}, Type: {}, Model: {}, Input: {}, Output: {}, Total: {}",
                    userId, operationType, model, inputTokens, outputTokens, totalTokens);
        } catch (Exception e) {
            log.error("Failed to log token usage", e);
        }
    }

    /**
     * 记录成功的 Token 使用
     */
    @Async
    public void logSuccess(Long userId, String operationType, String model,
                           Integer inputTokens, Integer outputTokens, Integer totalTokens, Integer latencyMs) {
        logTokenUsageAsync(userId, operationType, model, inputTokens, outputTokens, totalTokens, latencyMs, "SUCCESS", null);
    }

    /**
     * 记录失败的 Token 使用
     */
    @Async
    public void logFailed(Long userId, String operationType, String model, Integer latencyMs, String errorMessage) {
        logTokenUsageAsync(userId, operationType, model, 0, 0, 0, latencyMs, "FAILED", errorMessage);
    }

    /**
     * 获取用户当日已使用的token总数
     */
    public Long getTodayTotalTokens(Long userId) {
        return baseMapper.getTodayTotalTokens(userId);
    }
}
