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
 * 收藏记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("favorite_record")
@Schema(description = "收藏记录")
public class FavoriteRecord extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收藏目标类型：knowledge_base, knowledge_file, knowledge_space")
    private String targetType;

    @Schema(description = "目标ID")
    private String targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "收藏URL")
    private String targetUrl;

    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;
} 