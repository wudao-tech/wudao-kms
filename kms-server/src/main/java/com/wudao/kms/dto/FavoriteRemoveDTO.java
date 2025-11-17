package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 取消收藏DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "取消收藏DTO")
public class FavoriteRemoveDTO {

    @Schema(description = "目标类型：knowledge_base, knowledge_file, knowledge_space", required = true)
    private String targetType;

    @Schema(description = "目标ID", required = true)
    private String targetId;
} 