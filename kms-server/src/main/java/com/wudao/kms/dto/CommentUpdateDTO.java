package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 评论更新DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "评论更新DTO")
public class CommentUpdateDTO {

    @Schema(description = "评论内容", required = true)
    private String commentContent;

    @Schema(description = "评分：1-5分")
    private Integer rating;
} 