package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 复制提示词请求DTO
 */
@Data
@Schema(description = "复制提示词请求")
public class PromptCopyRequest {
    
    @NotNull(message = "原提示词ID不能为空")
    @Schema(description = "原提示词ID")
    private Long sourceId;
    
    @Schema(description = "新提示词名称")
    private String newName;
    
    @Schema(description = "新提示词类型：official-官方，personal-个人")
    private String newType;
}