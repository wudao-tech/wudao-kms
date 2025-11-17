package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 助手聊天消息DTO
 */
@Data
@Schema(description = "助手聊天消息")
public class AssistantChatMessage {
    
    @Schema(description = "消息ID")
    private Long messageId;
    
    @Schema(description = "消息类型：start, processing, chunk, tool_call, end, error")
    private String type;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "轮次索引")
    private Integer roundIndex;
    
    @Schema(description = "工具调用信息")
    private String toolInfo;
    
    @Schema(description = "进度信息")
    private String progress;
    
    @Schema(description = "时间戳")
    private LocalDateTime timestamp;
    
    public static AssistantChatMessage start(String content) {
        AssistantChatMessage message = new AssistantChatMessage();
        message.setType("start");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    public static AssistantChatMessage processing(String content) {
        AssistantChatMessage message = new AssistantChatMessage();
        message.setType("processing");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    public static AssistantChatMessage chunk(String content) {
        AssistantChatMessage message = new AssistantChatMessage();
        message.setType("chunk");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    public static AssistantChatMessage toolCall(String toolInfo) {
        AssistantChatMessage message = new AssistantChatMessage();
        message.setType("tool_call");
        message.setToolInfo(toolInfo);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    public static AssistantChatMessage end(String content, Long messageId, Integer roundIndex) {
        AssistantChatMessage message = new AssistantChatMessage();
        message.setType("end");
        message.setContent(content);
        message.setMessageId(messageId);
        message.setRoundIndex(roundIndex);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    public static AssistantChatMessage error(String content) {
        AssistantChatMessage message = new AssistantChatMessage();
        message.setType("error");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
} 