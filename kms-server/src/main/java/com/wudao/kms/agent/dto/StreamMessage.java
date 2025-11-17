package com.wudao.kms.agent.dto;

import lombok.Data;

/**
 * 流式消息DTO
 */
@Data
public class StreamMessage {
    
    private String type;
    
    private Object message;
    
    private Object data;
    
    public static StreamMessage of(String type, Object message) {
        StreamMessage streamMessage = new StreamMessage();
        streamMessage.setType(type);
        streamMessage.setMessage(message);
        return streamMessage;
    }
    
    public static StreamMessage end() {
        StreamMessage streamMessage = new StreamMessage();
        streamMessage.setType("end");
        streamMessage.setMessage("");
        return streamMessage;
    }
} 