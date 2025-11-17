package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 知识库实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("knowledge_base")
@Schema(description = "知识库")
public class KnowledgeBase extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "知识库名称")
    private String name;

    @Schema(description = "知识库描述")
    private String description;

    @Schema(description = "标签，多个用逗号分隔")
    private String tags;

    @Schema(description = "权限类型：1-公开，2-私有")
    private Integer permissionType;

    @Schema(description = "状态：1-启用，0-禁用")
    private Integer status;

    @Schema(description = "文件数量")
    private Integer fileCount;

    @Schema(description = "总大小（字节）")
    private Long totalSize;

    @Schema(description = "分段数量")
    private Integer segmentCount;

    @Schema(description = "总字数")
    private Integer wordCount;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    @TableLogic
    private Boolean deleteFlag;

    @NotBlank
    private String textModel;
    @NotBlank
    private String embeddingModel;
    @NotBlank
    private String multimodalModel;
    @NotBlank
    @Schema(description = "音频转文字")
    private String audioModel;
}