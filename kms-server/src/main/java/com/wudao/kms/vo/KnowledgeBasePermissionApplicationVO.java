package com.wudao.kms.vo;

import com.wudao.kms.entity.KnowledgeBasePermissionApplication;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 知识库权限申请VO（包含关联查询结果）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "知识库权限申请VO")
public class KnowledgeBasePermissionApplicationVO extends KnowledgeBasePermissionApplication {

    @Schema(description = "知识库名称")
    private String knowledgeBaseName;

    @Schema(description = "申请状态文本描述")
    public String getApplicationStatusText() {
        if (getApplicationStatus() == null) {
            return "未知";
        }
        
        return switch (getApplicationStatus()) {
            case 0 -> "待审批";
            case 1 -> "已通过";
            case 2 -> "已拒绝";
            case 3 -> "已撤回";
            default -> "未知";
        };
    }

    @Schema(description = "权限类型文本描述")
    public String getPermissionTypeText() {
        if (getTargetPermissionType() == null) {
            return "未知";
        }
        
        return switch (getTargetPermissionType()) {
            case 1 -> "只读";
            case 2 -> "读写";
            case 3 -> "管理";
            default -> "未知";
        };
    }
} 