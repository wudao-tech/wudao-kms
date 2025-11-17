package com.wudao.kms.vo;

import com.wudao.kms.entity.KnowledgeBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 知识库VO（包含关联查询结果）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "知识库VO")
public class KnowledgeBaseVO extends KnowledgeBase {

    @Schema(description = "实际文件数量")
    private Long actualFileCount;

    @Schema(description = "实际总大小（字节）")
    private Long actualTotalSize;

    // 权限相关字段
    @Schema(description = "权限ID")
    private Long permissionId;

    @Schema(description = "用户ID")
    private Long permissionUserId;

    @Schema(description = "用户姓名")
    private String permissionUserName;

    @Schema(description = "用户邮箱")
    private String permissionUserEmail;

    @Schema(description = "用户角色")
    private String permissionUserRole;

    @Schema(description = "权限类型：1-只读，2-读写，3-管理")
    private Integer permissionType;

    @Schema(description = "权限状态：1-启用，0-禁用")
    private Integer permissionStatus;

    @Schema(description = "当前用户角色Key")
    private String currentUserRoleKey;

    @Schema(description = "创建人姓名")
    private String createdByName;

    @Schema(description = "修改人姓名")
    private String updatedByName;
    @Schema(description = "知识库访问权限 1管理 2读写 3普通")
    private Integer knowledgePermissionType;
}