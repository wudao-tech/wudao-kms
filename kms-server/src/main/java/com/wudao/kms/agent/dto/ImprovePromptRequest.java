package com.wudao.kms.agent.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提示词优化请求DTO
 */
@Data
public class ImprovePromptRequest {
    private String assistantUuid;
    @NotNull(message = "提示词不能为空")
    private String prompt;
    @NotNull(message = "模型厂商不能为空")
    private String providerCode;
    @NotNull(message = "模型ID不能为空")
    private Long modelId;
} 