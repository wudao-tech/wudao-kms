package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 知识库权限申请DTO
 */
@Data
@Schema(description = "知识库权限申请DTO")
public class KnowledgeBasePermissionApplicationDTO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "申请人用户ID")
    private Long applicantUserId;

    @Schema(description = "申请人姓名")
    private String applicantUserName;

    @Schema(description = "申请人邮箱")
    private String applicantUserEmail;

    @Schema(description = "当前角色")
    private String currentRole;

    @Schema(description = "目标角色")
    private String targetRole;

    @Schema(description = "目标权限类型：1-管理员，2-读写，3-只读")
    private Integer targetPermissionType;

    @Schema(description = "申请理由")
    private String applicationReason;

    @Schema(description = "审批人用户ID")
    private Long approverUserId;

    @Schema(description = "审批人姓名")
    private String approverUserName;

    @Schema(description = "审批意见")
    private String approveReason;
} 