package com.wudao.kms.agent.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI聊天记忆实体类
 */
@Data
public class AiChatMemory {
    private Long id;
    private String conversationId; // 会话ID
    private String content; // 记忆内容
    private String type; // 记忆类型，例如：USER, ASSISTANT
    private LocalDateTime timestamp; // 记忆时间戳
}
