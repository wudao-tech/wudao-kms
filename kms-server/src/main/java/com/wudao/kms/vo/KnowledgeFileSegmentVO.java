package com.wudao.kms.vo;

import com.wudao.kms.entity.KnowledgeFileSegment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文件分段VO（包含关联查询结果）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "文件分段VO")
public class KnowledgeFileSegmentVO extends KnowledgeFileSegment {

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "知识库名称")
    private String knowledgeBaseName;
} 