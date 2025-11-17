package com.wudao.kms.agent.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemoryTableFieldEnums {
    STRING("String"),
    INTEGER("Integer"),
    NUMBER("Number"),
    TIME("Time"),
    DATE("Date"),
    DATETIME("Datetime"),
    BOOLEAN("Boolean"),
    ;
    @JsonValue
    private final String code;
}
