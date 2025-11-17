package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ChatApiRequest {
    @Schema(description = "助手Uuid")
    private String assistantId;
    @Schema(description = "用户传入的问题")
    private String query;
    @Schema(description = "文件列表")
    private List<String> fileUrls;
    @Schema(description ="用户ID" )
    private Long userId;
}
