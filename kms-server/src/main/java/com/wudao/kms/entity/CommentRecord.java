package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 评论记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("comment_record")
@Schema(description = "评论记录")
public class CommentRecord extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "评论目标类型：knowledge_base, knowledge_file, knowledge_space")
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

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;
} 