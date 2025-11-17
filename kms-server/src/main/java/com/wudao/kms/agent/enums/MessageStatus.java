package com.wudao.kms.agent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
@AllArgsConstructor
public enum MessageStatus {
    
    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    COMPLETED("COMPLETED", "已完成"),
    ERROR("ERROR", "处理失败"),
    REGENERATING("REGENERATING", "重新生成中"),
    EDITING("EDITING", "编辑中"),
    CANCELLED("CANCELLED", "已取消"),
    TIMEOUT("TIMEOUT", "处理超时");
    
    private final String code;
    private final String desc;
    
    public static MessageStatus getByCode(String code) {
        for (MessageStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 