package com.wudao.kms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 最新知识响应VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "最新知识响应VO")
public class LatestKnowledgeResponse {

    @Schema(description = "最新知识列表")
    private List<LatestKnowledgeItem> latestKnowledge;

    @Schema(description = "热门知识列表")
    private List<LatestKnowledgeItem> hotKnowledge;

    @Data
    @Accessors(chain = true)
    @Schema(description = "最新知识项")
    public static class LatestKnowledgeItem {
        @Schema(description = "文件ID")
        private Long fileId;

        @Schema(description = "文件名称")
        private String fileName;

        @Schema(description = "知识库名称")
        private String knowledgeBaseName;

        @Schema(description = "创建时间")
        private LocalDateTime createdAt;

        @Schema(description = "创建时间格式化")
        private String createdTimeText;
    }
} 