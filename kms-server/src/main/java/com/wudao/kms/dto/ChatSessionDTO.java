package com.wudao.kms.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 聊天会话DTO
 */
@Data
@Schema(description = "聊天会话DTO")
public class ChatSessionDTO {
    
    @Schema(description = "会话ID")
    private String sessionId;
    
    @Schema(description = "会话名称")
    private String sessionName;
    
    @Schema(description = "用户ID")
    private Long userId;
} 