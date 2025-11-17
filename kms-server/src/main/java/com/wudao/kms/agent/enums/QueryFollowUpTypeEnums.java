package com.wudao.kms.agent.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QueryFollowUpTypeEnums {
    DEFAULT("default","默认"),
    DIV("div","自定义"),
    CLOSE("close","关闭")
    ;
    @JsonValue
    private final String code;
    private final String desc;

    public static QueryFollowUpTypeEnums getByCode(String code) {
        for (QueryFollowUpTypeEnums type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
