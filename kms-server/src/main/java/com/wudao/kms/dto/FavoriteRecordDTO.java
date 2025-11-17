package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 收藏记录DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "收藏记录DTO")
public class FavoriteRecordDTO {

    @Schema(description = "目标类型：knowledge_base, knowledge_file, knowledge_space", required = true)
    private String targetType;

    @Schema(description = "目标ID", required = true)
    private String targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "目标URL")
    private String targetUrl;
} 