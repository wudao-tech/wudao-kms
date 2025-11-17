package com.wudao.kms.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RagResponse {
    @JsonPropertyDescription("rag搜索段落")
    private String chunkId;
    @JsonPropertyDescription("分段内容")
    private String content;
    @JsonPropertyDescription("文件URL，用于引用")
    private String s3Url;
    @JsonPropertyDescription("高亮部分")
    private String highlight;
    @JsonPropertyDescription("回答类型，QA回答/DOC回答")
    private String answerType;
}
