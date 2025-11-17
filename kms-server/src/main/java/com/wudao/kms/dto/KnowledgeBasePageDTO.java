package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 知识库分页查询DTO
 */
@Data
@Schema(description = "知识库分页查询参数")
public class KnowledgeBasePageDTO {

    @Schema(description = "页码", defaultValue = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "知识库名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;
}