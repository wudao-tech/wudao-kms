package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 评论记录DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "评论记录DTO")
public class CommentRecordDTO {

    @Schema(description = "目标类型：knowledge_base, knowledge_file, knowledge_space", required = true)
    private String targetType;

    @Schema(description = "目标ID", required = true)
    private Long targetId;

    @Schema(description = "目标名称", required = true)
    private String targetName;

    @Schema(description = "评论内容", required = true)
    private String commentContent;

    @Schema(description = "评分：1-5分")
    private Integer rating;
} 