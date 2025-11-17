package com.wudao.kms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 我的知识VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "我的知识VO")
public class MyKnowledgeVO {

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "知识库名称")
    private String knowledgeBaseName;

    @Schema(description = "知识空间ID")
    private Long knowledgeSpaceId;

    @Schema(description = "知识空间名称")
    private String knowledgeSpaceName;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
} 