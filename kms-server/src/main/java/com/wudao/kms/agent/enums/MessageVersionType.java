package com.wudao.kms.agent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息版本类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageVersionType {
    
    ORIGINAL("ORIGINAL", "原始消息"),
    REGENERATED("REGENERATED", "重新生成"),
    EDITED("EDITED", "编辑后的消息"),
    BRANCH("BRANCH", "分支消息");
    
    private final String code;
    private final String desc;
    
    public static MessageVersionType getByCode(String code) {
        for (MessageVersionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 