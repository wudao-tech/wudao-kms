package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 检查收藏状态DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "检查收藏状态DTO")
public class FavoriteCheckDTO {

    @Schema(description = "目标类型：knowledge_base, knowledge_file, knowledge_space", required = true)
    private String targetType;

    @Schema(description = "目标ID", required = true)
    private String targetId;
} 