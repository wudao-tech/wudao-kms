package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 创建助手会话请求DTO
 */
@Data
@Schema(description = "创建助手会话请求")
public class AssistantSessionCreateRequest {
    
    @NotBlank(message = "助手UUID不能为空")
    @Schema(description = "助手UUID", example = "uuid123")
    private String assistantUuid;
    
    @Schema(description = "会话名称", example = "我的AI助手对话")
    private String sessionName;
    
    @Schema(description = "会话配置（JSON格式）", example = "{\"temperature\": 0.7}")
    private String sessionConfig;

    @Schema(description = "创建人")
    private Long createBy;

    @Schema(description = "渠道类型", example = "aiot、kms")
    private String sourceType;
}