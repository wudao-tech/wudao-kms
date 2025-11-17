package com.wudao.kms.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天会话VO
 */
@Data
@Schema(description = "聊天会话VO")
public class ChatSessionVO {
    
    @Schema(description = "会话ID")
    private Long id;
    
    @Schema(description = "会话标识")
    private String sessionId;
    
    @Schema(description = "会话名称")
    private String sessionName;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    @Schema(description = "最后一条消息")
    private String lastMessage;
    
    @Schema(description = "消息数量")
    private Integer messageCount;
} 