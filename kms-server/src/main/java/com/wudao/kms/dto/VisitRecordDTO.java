package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 访问记录DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "访问记录DTO")
public class VisitRecordDTO {

    @Schema(description = "目标类型：knowledge_base, knowledge_file, knowledge_space", required = true)
    private String targetType;

    @Schema(description = "目标ID", required = true)
    private Long targetId;

    @Schema(description = "目标名称", required = true)
    private String targetName;

    @Schema(description = "目标URL")
    private String targetUrl;
} 