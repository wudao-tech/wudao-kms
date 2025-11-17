package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 记忆查询请求
 *
 * @author wudao  
 * @date 2025-08-27
 */
@Data
@Schema(description = "记忆查询请求")
public class MemoryQueryRequest {

    @NotBlank(message = "助手UUID不能为空")
    @Schema(description = "助手UUID", required = true)
    private String agentUuid;

    @Schema(description = "会话UUID")
    private String sessionUuid;

    @NotBlank(message = "记忆类型不能为空")
    @Schema(description = "记忆类型：variable(变量) 或 table(记忆表)", required = true, allowableValues = {"variable", "table"})
    private String type;

    private Long userId;
}