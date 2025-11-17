package com.wudao.kms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 评论记录VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "评论记录VO")
public class CommentRecordVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "评论目标类型")
    private String targetType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "评论内容")
    private String commentContent;

    @Schema(description = "评分：1-5分")
    private Integer rating;

    @Schema(description = "评论时间")
    private LocalDateTime commentTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
} 