package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提示词状态切换请求DTO
 */
@Data
@Schema(description = "提示词状态切换请求")
public class PromptStatusRequest {
    
    @NotNull(message = "提示词ID不能为空")
    @Schema(description = "提示词ID")
    private Long id;
    
    @NotNull(message = "状态值不能为空")
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}