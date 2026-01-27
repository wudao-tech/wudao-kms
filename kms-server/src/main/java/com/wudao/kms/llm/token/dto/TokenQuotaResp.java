package com.wudao.kms.llm.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Token配额响应")
public class TokenQuotaResp {

    @Schema(description = "每日配额上限")
    private Long dailyMaxToken;

    @Schema(description = "今日已使用token")
    private Long todayUsedToken;

    @Schema(description = "剩余配额")
    private Long remainingToken;
}
