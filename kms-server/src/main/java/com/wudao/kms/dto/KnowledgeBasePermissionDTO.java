package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 知识库权限DTO
 */
@Data
@Schema(description = "知识库权限DTO")
public class KnowledgeBasePermissionDTO {

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "用户邮箱")
    private String userEmail;

    @Schema(description = "用户角色")
    private String userRole;

    @Schema(description = "权限类型：1-管理，2-读写，3-只读")
    private Integer permissionType;

    @Schema(description = "状态：1-启用，0-禁用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
} 