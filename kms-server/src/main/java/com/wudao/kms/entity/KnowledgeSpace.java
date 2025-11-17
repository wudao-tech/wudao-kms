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
 * 知识库空间实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("knowledge_space")
@Schema(description = "知识库空间")
public class KnowledgeSpace extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "父级ID，0表示根目录")
    private Long parentId;

    @Schema(description = "空间名称")
    private String name;

    @Schema(description = "类型：1-文件夹，2-文件")
    private Integer type;

    @Schema(description = "完整路径")
    private String path;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "文件数量")
    private Integer fileCount;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;
} 