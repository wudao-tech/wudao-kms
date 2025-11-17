package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件分段分页查询DTO
 */
@Data
@Schema(description = "文件分段分页查询参数")
public class KnowledgeFileSegmentPageDTO {

    @Schema(description = "页码", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "分段内容关键词")
    private String keyword;

    @Schema(description = "状态")
    private Integer status;
}