package com.wudao.kms.agent.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TempMemoryEnums {
    FOREVER("FOREVER","持久"),
    SESSION("SESSION","单会话");
    @JsonValue
    private final String code;
    private final String desc;

    public static TempMemoryEnums getByCode(String code) {
        for (TempMemoryEnums type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public boolean isDisk(){
        return this.getCode().equals(FOREVER.getCode());
    }
}
