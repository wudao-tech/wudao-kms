package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 知识库标签实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("knowledge_tag")
@Schema(description = "知识库标签")
public class KnowledgeTag extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "标签颜色")
    private String tagColor;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "使用次数")
    private Integer useCount;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;
} 