package com.wudao.kms.dify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dify错误响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dify错误响应")
public class DifyErrorResponse {

    @JsonProperty("error_code")
    @Schema(description = "错误码: 1001-Authorization header格式无效, 1002-授权失败, 2001-知识库不存在")
    private Integer errorCode;

    @JsonProperty("error_msg")
    @Schema(description = "错误信息")
    private String errorMsg;

    public static DifyErrorResponse invalidAuthHeader() {
        return new DifyErrorResponse(1001, "Invalid Authorization header format");
    }

    public static DifyErrorResponse authFailed() {
        return new DifyErrorResponse(1002, "Authorization failed");
    }

    public static DifyErrorResponse knowledgeNotFound() {
        return new DifyErrorResponse(2001, "Knowledge does not exist");
    }
}
