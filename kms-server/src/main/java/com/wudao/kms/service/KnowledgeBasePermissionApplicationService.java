package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.R;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeBasePermissionApplication;
import com.wudao.kms.mapper.KnowledgeBasePermissionApplicationMapper;
import com.wudao.kms.vo.KnowledgeBasePermissionApplicationVO;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库权限申请Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBasePermissionApplicationService extends MPJBaseServiceImpl<KnowledgeBasePermissionApplicationMapper, KnowledgeBasePermissionApplication> {

    private final KnowledgeBasePermissionService knowledgeBasePermissionService;

    /**
     * 提交权限申请
     */
    public R<Boolean> submitApplication(Long knowledgeBaseId, Long applicantUserId,
                                 String applicantUserEmail, String currentRole, String targetRole,
                                 Integer targetPermissionType, String applicationReason) {
        try {
            // 检查是否已有待审批的申请
            if (hasPendingApplication(knowledgeBaseId, applicantUserId, targetPermissionType)) {
                return R.fail("您已有待审批的申请，请耐心等待审批结果");
            }

            // 检查用户是否已拥有该权限或更高权限
            Integer currentPermissionType = knowledgeBasePermissionService.getUserPermissionType(knowledgeBaseId, applicantUserId);
            if (currentPermissionType != null && currentPermissionType.equals(targetPermissionType)) {
                return R.fail("您已拥有该权限或更高权限，无需申请");
            }

            KnowledgeBasePermissionApplication application = new KnowledgeBasePermissionApplication();
            application.setKnowledgeBaseId(knowledgeBaseId);
            application.setApplicantUserId(applicantUserId);
            application.setApplicantUserEmail(applicantUserEmail);
            application.setTargetRole(targetRole);
            application.setTargetPermissionType(targetPermissionType);
            application.setApplicationReason(applicationReason);
            application.setApplicationStatus(KnowledgeBasePermissionApplication.ApplicationStatus.PENDING);
            application.setDeleteFlag(false);

            return R.ok(save(application));
        }catch (Exception e){
            log.error("提交申请失败", e);
            throw new ServiceException("提交申请失败: " + e.getMessage(), e);
        }
    }

    /**
     * 审批申请（通过）
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveApplication(Long applicationId, String approveReason) {
        KnowledgeBasePermissionApplication application = this.getById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        // 检查审批人是否有权限查看申请
        Integer permissionType = knowledgeBasePermissionService.getUserPermissionType(application.getKnowledgeBaseId(), SecurityUtils.getUserId());
        if (permissionType == null || permissionType > 1) {
            throw new ServiceException("您没有权限查看申请列表");
        }
        
        if (!Integer.valueOf(KnowledgeBasePermissionApplication.ApplicationStatus.PENDING).equals(application.getApplicationStatus())) {
            throw new RuntimeException("申请状态不正确，无法审批");
        }
        
        // 更新申请状态
        application.setApplicationStatus(KnowledgeBasePermissionApplication.ApplicationStatus.APPROVED);
        application.setApproverUserId(SecurityUtils.getUserId());
        application.setApproveTime(LocalDateTime.now());
        application.setApproveReason(approveReason);
        
        boolean updateResult = this.updateById(application);
        
        if (updateResult) {
            // 更新或创建用户权限
            try {
                knowledgeBasePermissionService.addUserPermission(
                    application.getKnowledgeBaseId(),
                    application.getApplicantUserId(),
                    application.getApplicantUserName(),
                    application.getApplicantUserEmail(),
                    application.getTargetRole(),
                    application.getTargetPermissionType()
                );
            } catch (RuntimeException e) {
                // 如果用户已有权限，则更新权限
                if (e.getMessage().contains("已拥有该知识库权限")) {
                    knowledgeBasePermissionService.updateUserPermission(
                        application.getKnowledgeBaseId(),
                        application.getApplicantUserId(),
                        application.getTargetPermissionType(),
                        application.getTargetRole()
                    );
                } else {
                    throw e;
                }
            }
        }
        
        return updateResult;
    }

    /**
     * 拒绝申请
     */
    public Boolean rejectApplication(Long applicationId,  String approveReason) {
        KnowledgeBasePermissionApplication application = this.getById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        // 检查审批人是否有权限查看申请
        Integer permissionType = knowledgeBasePermissionService.getUserPermissionType(application.getKnowledgeBaseId(), SecurityUtils.getUserId());
        if (permissionType == null || permissionType > 1) {
            throw new ServiceException("您没有权限查看申请列表");
        }
        
        if (!Integer.valueOf(KnowledgeBasePermissionApplication.ApplicationStatus.PENDING).equals(application.getApplicationStatus())) {
            throw new RuntimeException("申请状态不正确，无法审批");
        }
        application.setApplicationStatus(KnowledgeBasePermissionApplication.ApplicationStatus.REJECTED);
        application.setApproverUserId(SecurityUtils.getUserId());
        application.setApproveTime(LocalDateTime.now());
        application.setApproveReason(approveReason);
        
        return this.updateById(application);
    }

    /**
     * 撤回申请
     */
    public Boolean withdrawApplication(Long applicationId, Long applicantUserId) {
        KnowledgeBasePermissionApplication application = this.getById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (!application.getApplicantUserId().equals(applicantUserId)) {
            throw new RuntimeException("只能撤回自己的申请");
        }
        
        if (!Integer.valueOf(KnowledgeBasePermissionApplication.ApplicationStatus.PENDING).equals(application.getApplicationStatus())) {
            throw new RuntimeException("申请状态不正确，无法撤回");
        }
        
        application.setApplicationStatus(KnowledgeBasePermissionApplication.ApplicationStatus.WITHDRAWN);
        
        return this.updateById(application);
    }

    /**
     * 查询待审批的申请列表（管理员和超级管理员可查看）
     */
    public List<KnowledgeBasePermissionApplicationVO> listPendingApplications(Long knowledgeBaseId, Long reviewerUserId) {
        // 检查审批人是否有权限查看申请
        Integer permissionType = knowledgeBasePermissionService.getUserPermissionType(knowledgeBaseId, reviewerUserId);
        if (permissionType == null || permissionType > 1) {
            throw new ServiceException("您没有权限查看申请列表");
        }
        
        MPJLambdaWrapper<KnowledgeBasePermissionApplication> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(KnowledgeBasePermissionApplication.class)
               .selectAs(KnowledgeBase::getName, KnowledgeBasePermissionApplicationVO::getKnowledgeBaseName)
                .selectAs(SysUser::getNickname, KnowledgeBasePermissionApplicationVO::getApplicantUserName)
               .leftJoin(KnowledgeBase.class, KnowledgeBase::getId, KnowledgeBasePermissionApplication::getKnowledgeBaseId)
                .leftJoin(SysUser.class, SysUser::getId, KnowledgeBasePermissionApplication::getApplicantUserId)
               .eq(KnowledgeBasePermissionApplication::getKnowledgeBaseId, knowledgeBaseId)
               .eq(KnowledgeBasePermissionApplication::getApplicationStatus, KnowledgeBasePermissionApplication.ApplicationStatus.PENDING)
               .eq(KnowledgeBasePermissionApplication::getDeleteFlag, false)
               .orderByDesc(KnowledgeBasePermissionApplication::getCreatedAt);
        
        return this.selectJoinList(KnowledgeBasePermissionApplicationVO.class, wrapper);
    }

    /**
     * 查询用户的申请历史
     */
    public List<KnowledgeBasePermissionApplicationVO> listUserApplications(Long applicantUserId) {
        MPJLambdaWrapper<KnowledgeBasePermissionApplication> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(KnowledgeBasePermissionApplication.class)
               .selectAs(KnowledgeBase::getName, KnowledgeBasePermissionApplicationVO::getKnowledgeBaseName)
               .leftJoin(KnowledgeBase.class, KnowledgeBase::getId, KnowledgeBasePermissionApplication::getKnowledgeBaseId)
               .eq(KnowledgeBasePermissionApplication::getApplicantUserId, applicantUserId)
               .eq(KnowledgeBasePermissionApplication::getDeleteFlag, false)
               .orderByDesc(KnowledgeBasePermissionApplication::getCreatedAt);
        
        return this.selectJoinList(KnowledgeBasePermissionApplicationVO.class, wrapper);
    }

    /**
     * 检查是否有待审批的申请
     */
    private boolean hasPendingApplication(Long knowledgeBaseId, Long applicantUserId, Integer targetRole) {
        LambdaQueryWrapper<KnowledgeBasePermissionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBasePermissionApplication::getKnowledgeBaseId, knowledgeBaseId)
               .eq(KnowledgeBasePermissionApplication::getApplicantUserId, applicantUserId)
               .eq(KnowledgeBasePermissionApplication::getTargetPermissionType, targetRole)
               .eq(KnowledgeBasePermissionApplication::getApplicationStatus, KnowledgeBasePermissionApplication.ApplicationStatus.PENDING)
               .eq(KnowledgeBasePermissionApplication::getDeleteFlag, false)
                .last("limit 1");
        
        return this.count(wrapper) > 0;
    }

    /**
     * 获取所有待审批申请统计（管理员可查看）
     */
    public List<KnowledgeBasePermissionApplicationVO> listAllPendingApplicationsForAdmin(Long adminUserId) {
        // 获取管理员有权限的知识库列表
        List<Long> knowledgeBaseIds = knowledgeBasePermissionService.getKnowledgeBaseIdsByUserId(adminUserId);
        
        if (knowledgeBaseIds.isEmpty()) {
            return List.of();
        }
        
        MPJLambdaWrapper<KnowledgeBasePermissionApplication> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(KnowledgeBasePermissionApplication.class)
               .selectAs(KnowledgeBase::getName, KnowledgeBasePermissionApplicationVO::getKnowledgeBaseName)
                .selectAs(SysUser::getNickname, KnowledgeBasePermissionApplicationVO::getApplicantUserName)
               .leftJoin(KnowledgeBase.class, KnowledgeBase::getId, KnowledgeBasePermissionApplication::getKnowledgeBaseId)
                .leftJoin(SysUser.class, SysUser::getId, KnowledgeBasePermissionApplication::getApplicantUserId)
               .in(KnowledgeBasePermissionApplication::getKnowledgeBaseId, knowledgeBaseIds)
               .eq(KnowledgeBasePermissionApplication::getApplicationStatus, KnowledgeBasePermissionApplication.ApplicationStatus.PENDING)
               .eq(KnowledgeBasePermissionApplication::getDeleteFlag, false)

               .orderByDesc(KnowledgeBasePermissionApplication::getCreatedAt);
        
        return this.selectJoinList(KnowledgeBasePermissionApplicationVO.class, wrapper);
    }
} 