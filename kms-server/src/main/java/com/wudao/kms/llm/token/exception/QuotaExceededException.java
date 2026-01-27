package com.wudao.kms.llm.token.exception;

import lombok.Getter;

@Getter
public class QuotaExceededException extends RuntimeException {

    private final String code;

    public QuotaExceededException(String message) {
        super(message);
        this.code = "QUOTA_EXCEEDED";
    }

    public QuotaExceededException(String code, String message) {
        super(message);
        this.code = code;
    }
}
