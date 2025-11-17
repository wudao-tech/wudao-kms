package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 助手聊天请求DTO
 */
@Data
@Schema(description = "助手聊天请求")
public class AssistantChatRequest {
    
    @NotBlank(message = "会话UUID不能为空")
    @Schema(description = "会话UUID")
    private String sessionUuid;
    
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "用户消息内容")
    private String content;
    
    @Schema(description = "附件文件列表")
    private List<String> attachments;
    
    @Schema(description = "是否启用工具调用", defaultValue = "true")
    private Boolean enableTools = true;
} 