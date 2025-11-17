package com.wudao.kms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识推荐响应VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "知识推荐响应VO")
public class RecommendKnowledgeResponse {

    @Schema(description = "推荐知识列表")
    private List<RecommendKnowledgeItem> recommendKnowledge;

    @Data
    @Accessors(chain = true)
    @Schema(description = "推荐知识项")
    public static class RecommendKnowledgeItem {
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

        @Schema(description = "推荐分数")
        private Double score;

        @Schema(description = "推荐原因")
        private String reason;
    }
} 