package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量删除提示词请求DTO
 */
@Data
@Schema(description = "批量删除提示词请求")
public class PromptBatchDeleteRequest {
    
    @NotEmpty(message = "提示词ID列表不能为空")
    @Schema(description = "提示词ID列表")
    private List<Long> ids;
}