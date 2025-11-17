package com.wudao.kms.controller;

import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.KnowledgeBasePermissionApplicationDTO;
import com.wudao.kms.service.KnowledgeBasePermissionApplicationService;
import com.wudao.kms.vo.KnowledgeBasePermissionApplicationVO;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库权限申请Controller
 */
@Tag(name = "知识库权限申请管理", description = "知识库权限申请相关接口")
@RestController
@RequestMapping("/api/knowledge-base-permission-application")
@RequiredArgsConstructor
public class KnowledgeBasePermissionApplicationController {

    private final KnowledgeBasePermissionApplicationService applicationService;

    @Operation(summary = "提交权限申请")
    @PostMapping("/submit")
    public R<Boolean> submitApplication(@RequestBody KnowledgeBasePermissionApplicationDTO applicationDTO) {
        return applicationService.submitApplication(
                applicationDTO.getKnowledgeBaseId(),
                SecurityUtils.getUserId(),
                applicationDTO.getApplicantUserEmail(),
                applicationDTO.getCurrentRole(),
                applicationDTO.getTargetRole(),
                applicationDTO.getTargetPermissionType(),
                applicationDTO.getApplicationReason()
        );
    }

    @Operation(summary = "审批通过申请")
    @PostMapping("/approve/{applicationId}")
    public R<Boolean> approveApplication(
            @Parameter(description = "申请ID") @PathVariable Long applicationId,
            @RequestBody KnowledgeBasePermissionApplicationDTO applicationDTO) {
        Boolean result = applicationService.approveApplication(
                applicationId,
                applicationDTO.getApproveReason()
        );
        return R.ok(result);
    }

    @Operation(summary = "拒绝申请")
    @PostMapping("/reject/{applicationId}")
    public R<Boolean> rejectApplication(
            @Parameter(description = "申请ID") @PathVariable Long applicationId,
            @RequestBody KnowledgeBasePermissionApplicationDTO applicationDTO) {
        Boolean result = applicationService.rejectApplication(
                applicationId,
                applicationDTO.getApproveReason()
        );
        return R.ok(result);
    }

    @Operation(summary = "撤回申请")
    @PostMapping("/withdraw/{applicationId}")
    public R<Boolean> withdrawApplication(
            @Parameter(description = "申请ID") @PathVariable Long applicationId,
            @Parameter(description = "申请人用户ID") @RequestParam Long applicantUserId) {
        Boolean result = applicationService.withdrawApplication(applicationId, applicantUserId);
        return R.ok(result);
    }

    @Operation(summary = "查询待审批申请列表")
    @GetMapping("/pending/{knowledgeBaseId}")
    public R<List<KnowledgeBasePermissionApplicationVO>> listPendingApplications(
            @Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId) {
        List<KnowledgeBasePermissionApplicationVO> result = applicationService.listPendingApplications(knowledgeBaseId, SecurityUtils.getUserId());
        return R.ok(result);
    }

    @Operation(summary = "查询用户申请历史")
    @GetMapping("/user")
    public R<List<KnowledgeBasePermissionApplicationVO>> listUserApplications() {
        List<KnowledgeBasePermissionApplicationVO> result = applicationService.listUserApplications(SecurityUtils.getUserId());
        return R.ok(result);
    }

    @Operation(summary = "查询管理员可审批的所有待处理申请")
    @GetMapping("/admin/{adminUserId}")
    public R<List<KnowledgeBasePermissionApplicationVO>> listAllPendingApplicationsForAdmin(
            @Parameter(description = "管理员用户ID") @PathVariable Long adminUserId) {
        List<KnowledgeBasePermissionApplicationVO> result = applicationService.listAllPendingApplicationsForAdmin(adminUserId);
        return R.ok(result);
    }
} 