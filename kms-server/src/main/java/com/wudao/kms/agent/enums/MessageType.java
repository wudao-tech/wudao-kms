package com.wudao.kms.agent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    
    USER("USER", "用户消息"),
    ASSISTANT("ASSISTANT", "助手回复"),
    SYSTEM("SYSTEM", "系统消息"),
    TOOL_CALL("TOOL_CALL", "工具调用"),
    TOOL_RESULT("TOOL_RESULT", "工具调用结果");
    
    private final String code;
    private final String desc;
    
    public static MessageType getByCode(String code) {
        for (MessageType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 