package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 知识库文件分页查询DTO
 */
@Data
@Schema(description = "知识库文件分页查询参数")
public class KnowledgeFilePageDTO {

    @Schema(description = "页码", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "空间ID")
    private Long spaceId;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "状态")
    private Integer status;

    private String approveStatus;
}