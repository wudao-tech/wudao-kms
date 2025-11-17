package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 知识库权限申请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("knowledge_base_permission_application")
@Schema(description = "知识库权限申请")
public class KnowledgeBasePermissionApplication extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "申请人用户ID")
    private Long applicantUserId;

    @Schema(description = "申请人姓名")
    private String applicantUserName;

    @Schema(description = "申请人邮箱")
    private String applicantUserEmail;

    @Schema(description = "目标角色")
    private String targetRole;

    @Schema(description = "目标权限类型：1-只读，2-读写，3-管理")
    private Integer targetPermissionType;

    @Schema(description = "申请理由")
    private String applicationReason;

    @Schema(description = "申请状态：0-待审批，1-已通过，2-已拒绝，3-已撤回")
    private Integer applicationStatus;

    @Schema(description = "审批人用户ID")
    private Long approverUserId;

    @Schema(description = "审批人姓名")
    private String approverUserName;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批意见")
    private String approveReason;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;

    /**
     * 申请状态枚举
     */
    public static class ApplicationStatus {
        public static final int PENDING = 0;      // 待审批
        public static final int APPROVED = 1;     // 已通过
        public static final int REJECTED = 2;     // 已拒绝
        public static final int WITHDRAWN = 3;    // 已撤回
    }
} 