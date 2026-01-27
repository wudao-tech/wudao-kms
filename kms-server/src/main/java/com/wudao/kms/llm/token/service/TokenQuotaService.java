package com.wudao.kms.llm.token.service;

import com.wudao.kms.llm.token.dto.TokenQuotaResp;
import com.wudao.kms.llm.token.exception.QuotaExceededException;
import com.wudao.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenQuotaService {

    private final TokenUsageLogService tokenUsageLogService;

    private final ISysConfigService sysConfigService;

    /**
     * 检查用户配额，超出则抛出异常
     */
    public void checkQuota(Long userId) {
        if (userId == null) {
            return;
        }
        Long todayUsed = tokenUsageLogService.getTodayTotalTokens(userId);
        Long dailyMaxToken = sysConfigService.getValue("daily.max.token", Long.class);
        if (todayUsed >= dailyMaxToken) {
            log.debug("用户 {} 今日token使用已达上限，已使用: {}, 上限: {}", userId, todayUsed, dailyMaxToken);
            throw new QuotaExceededException("今日Token使用已到达上限，请联系客服升级套餐或者等待明日重置");
        }
    }

    /**
     * 获取用户剩余配额
     */
    public Long getRemainingQuota(Long userId) {
        Long dailyMaxToken = sysConfigService.getValue("daily.max.token", Long.class);
        if (userId == null) {
            return dailyMaxToken;
        }
        Long todayUsed = tokenUsageLogService.getTodayTotalTokens(userId);
        return Math.max(0, dailyMaxToken - todayUsed);
    }

    /**
     * 获取用户配额使用情况
     */
    public TokenQuotaResp getQuotaInfo(Long userId) {
        Long dailyMaxToken = sysConfigService.getValue("daily.max.token", Long.class);
        Long todayUsed = userId != null ? tokenUsageLogService.getTodayTotalTokens(userId) : 0L;
        Long remaining = Math.max(0, dailyMaxToken - todayUsed);
        return TokenQuotaResp.builder()
                .dailyMaxToken(dailyMaxToken)
                .todayUsedToken(todayUsed)
                .remainingToken(remaining)
                .build();
    }
}
