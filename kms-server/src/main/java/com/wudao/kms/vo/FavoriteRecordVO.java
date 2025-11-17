package com.wudao.kms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 收藏记录VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "收藏记录VO")
public class FavoriteRecordVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收藏目标类型")
    private String targetType;

    @Schema(description = "目标ID")
    private String targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "收藏URL")
    private String targetUrl;

    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "知识库名称")
    private String knowledgeBaseName;

    @Schema(description = "知识空间ID")
    private Long knowledgeSpaceId;

    @Schema(description = "知识空间名称")
    private String knowledgeSpaceName;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "创建人名字")
    private String creatorName;
} 