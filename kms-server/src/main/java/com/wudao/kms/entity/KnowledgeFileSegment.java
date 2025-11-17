package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 文件分段实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("knowledge_file_segment")
@Schema(description = "文件分段")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeFileSegment extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属文件ID")
    private Long fileId;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "分段序号")
    private Integer segmentIndex;

    @Schema(description = "空间ID")
    private Long knowledgeSpaceId;

    private String fileMd5;

    @Schema(description = "QA提取的标题")
    private String title;

    @Schema(description = "分段内容")
    private String segmentContent;

    @Schema(description = "分段字数")
    private Integer wordCount;

    @Schema(description = "在原文中的起始位置")
    private Integer startPosition;

    @Schema(description = "在原文中的结束位置")
    private Integer endPosition;

    @Schema(description = "向量")
    private float[] vector;

    @Schema(description = "是否被向量")
    private Boolean vectorFlag;

    private String vlContent;
}