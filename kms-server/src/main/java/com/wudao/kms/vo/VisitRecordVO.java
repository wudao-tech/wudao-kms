package com.wudao.kms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 访问记录VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "访问记录VO")
public class VisitRecordVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "访问目标类型")
    private String targetType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "访问URL")
    private String targetUrl;

    @Schema(description = "访问时间")
    private LocalDateTime visitTime;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "知识库ID")
    private Long knowledgeId;

    @Schema(description = "知识库名称")
    private String knowledgeName;

    @Schema(description = "空间ID")
    private Long spaceId;

    @Schema(description = "空间名称")
    private String spaceName;
} 