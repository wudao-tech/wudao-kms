package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.R;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeBasePermission;
import com.wudao.kms.mapper.KnowledgeBaseMapper;
import com.wudao.kms.mapper.KnowledgeBasePermissionMapper;
import com.wudao.security.utils.SecurityUtils;
import com.wudao.system.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 知识库权限Service
 */
@Service
public class KnowledgeBasePermissionService extends MPJBaseServiceImpl<KnowledgeBasePermissionMapper, KnowledgeBasePermission> {

    @Resource
    private SysUserMapper userMapper;
    @Resource
    private KnowledgeBaseMapper knowledgeBaseMapper;

    /**
     * 创建默认的超级管理员权限
     */
    public Boolean createDefaultSuperAdminPermission(Long knowledgeBaseId, Long userId, String userEmail) {
        KnowledgeBasePermission permission = new KnowledgeBasePermission();
        permission.setKnowledgeBaseId(knowledgeBaseId);
        permission.setUserId(userId);
        permission.setUserName(SecurityUtils.getUserId().toString());
        permission.setUserEmail(userEmail);
        permission.setUserRole("admin");
        permission.setPermissionType(1); // 管理权限
        permission.setStatus(1); // 启用状态
        permission.setDeleteFlag(false);
        permission.setRemark("知识库创建时自动生成的超级管理员权限");
        
        return this.save(permission);
    }

    /**
     * 根据知识库ID查询权限列表
     */
    public List<KnowledgeBasePermission> listByKnowledgeBaseId(Long knowledgeBaseId) {
        // 校验权限，只有本人或管理员可以查看权限列表
        MPJLambdaWrapper<KnowledgeBasePermission> wrapper = new MPJLambdaWrapper<>();
        wrapper
                .selectAll(KnowledgeBasePermission.class)
                .selectAs(SysUser::getUsername, KnowledgeBasePermission::getUserName)
                .leftJoin(SysUser.class, SysUser::getId, KnowledgeBasePermission::getUserId)
                .eq(KnowledgeBasePermission::getKnowledgeBaseId, knowledgeBaseId)
               .eq(KnowledgeBasePermission::getDeleteFlag, false)
               .eq(KnowledgeBasePermission::getStatus, 1)
               .orderByDesc(KnowledgeBasePermission::getCreatedAt);
        return this.list(wrapper);
    }

    /**
     * 检查用户是否对指定知识库有权限
     */
    public boolean hasPermission(Long knowledgeBaseId, Long userId) {
        LambdaQueryWrapper<KnowledgeBasePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBasePermission::getKnowledgeBaseId, knowledgeBaseId)
               .eq(KnowledgeBasePermission::getUserId, userId)
               .eq(KnowledgeBasePermission::getDeleteFlag, false)
               .eq(KnowledgeBasePermission::getStatus, 1);
        return this.count(wrapper) > 0;
    }

    /**
     * 获取用户对指定知识库的权限类型
     */
    public Integer getUserPermissionType(Long knowledgeBaseId, Long userId) {
        LambdaQueryWrapper<KnowledgeBasePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBasePermission::getKnowledgeBaseId, knowledgeBaseId)
               .eq(KnowledgeBasePermission::getUserId, userId)
               .eq(KnowledgeBasePermission::getDeleteFlag, false)
               .eq(KnowledgeBasePermission::getStatus, 1);
        
        KnowledgeBasePermission permission = this.getOne(wrapper);
        return permission != null ? permission.getPermissionType() : null;
    }

    /**
     * 添加用户权限
     */
    public Boolean addUserPermission(Long knowledgeBaseId, Long userId, String userName, String userEmail, 
                                   String userRole, Integer permissionType) {
        // 检查是否已存在权限
        if (hasPermission(knowledgeBaseId, userId)) {
            throw new RuntimeException("用户已拥有该知识库权限");
        }
        
        KnowledgeBasePermission permission = new KnowledgeBasePermission();
        permission.setKnowledgeBaseId(knowledgeBaseId);
        permission.setUserId(userId);
        permission.setUserName(userName);
        permission.setUserEmail(userEmail);
        permission.setUserRole(userRole);
        permission.setPermissionType(permissionType);
        permission.setStatus(1);
        permission.setDeleteFlag(false);
        
        return this.save(permission);
    }

    /**
     * 删除用户权限
     */
    public R<Boolean> removeUserPermission(Long knowledgeBaseId, Long userId) {
        // 管理员只能删除读写，创建者可以删除所有权限
        Long currentUserId = SecurityUtils.getUserId();
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId);
        if (knowledgeBase == null) {
            return R.fail("知识库不存在");
        }
        LambdaQueryWrapper<KnowledgeBasePermission> queryWrapper = Wrappers.lambdaQuery(KnowledgeBasePermission.class)
                .eq(KnowledgeBasePermission::getKnowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeBasePermission::getUserId, currentUserId)
                .last("limit 1");
        KnowledgeBasePermission currentUserPermission = this.getOne(queryWrapper);
        if (currentUserPermission == null) {
            return R.fail("权限不足");
        }
        LambdaQueryWrapper<KnowledgeBasePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBasePermission::getKnowledgeBaseId, knowledgeBaseId)
               .eq(KnowledgeBasePermission::getUserId, userId)
                .last("limit 1");
        KnowledgeBasePermission permission = this.getOne(wrapper);
        if (permission == null) {
            return R.ok();
        }
        if (Objects.equals(knowledgeBase.getCreatedBy(), currentUserId)
                || currentUserPermission.getPermissionType() > permission.getPermissionType()) {
            return R.fail("权限不足，无法删除该用户权限");
        }
        return R.ok(this.removeById(permission.getId()));
    }

    /**
     * 更新用户权限
     */
    public Boolean updateUserPermission(Long knowledgeBaseId, Long userId, Integer permissionType, String userRole) {
        LambdaQueryWrapper<KnowledgeBasePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBasePermission::getKnowledgeBaseId, knowledgeBaseId)
               .eq(KnowledgeBasePermission::getUserId, userId)
               .eq(KnowledgeBasePermission::getDeleteFlag, false);
        
        KnowledgeBasePermission permission = this.getOne(wrapper);
        if (permission != null) {
            permission.setPermissionType(permissionType);
            permission.setUserRole(userRole);
            return this.updateById(permission);
        }
        return false;
    }

    /**
     * 根据用户ID获取有权限的知识库ID列表
     */
    public List<Long> getKnowledgeBaseIdsByUserId(Long userId) {
        LambdaQueryWrapper<KnowledgeBasePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(KnowledgeBasePermission::getKnowledgeBaseId)
               .eq(KnowledgeBasePermission::getUserId, userId)
               .eq(KnowledgeBasePermission::getDeleteFlag, false)
               .eq(KnowledgeBasePermission::getStatus, 1);
        
        return this.list(wrapper).stream()
                .map(KnowledgeBasePermission::getKnowledgeBaseId)
                .toList();
    }

    /**
     * 根据用户ID获取有权限的知识库ID列表
     */
    public List<KnowledgeBasePermission> getKnowledgeBaseByUserId(Long userId) {
        LambdaQueryWrapper<KnowledgeBasePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBasePermission::getUserId, userId)
                .eq(KnowledgeBasePermission::getDeleteFlag, false)
                .eq(KnowledgeBasePermission::getStatus, 1);

        return this.list(wrapper);
    }

    /**
     * 根据用户ID获取有权限的知识库ID列表
     */
    public List<Long> getKnowledgeBaseIdsByUserId(String  userName) {
        if (StringUtil.isBlank(userName)){
            return new ArrayList<>();
        }
        // 根据用户名查询用户ID
        List<SysUser> users = userMapper.findByUsernames(List.of(userName));
        if (CollUtil.isEmpty(users)){
            return new ArrayList<>();
        }
        MPJLambdaWrapper<KnowledgeBasePermission> wrapper = new MPJLambdaWrapper<>();
        wrapper.leftJoin(KnowledgeBase.class, KnowledgeBase::getId,KnowledgeBasePermission::getKnowledgeBaseId);
        wrapper.select(KnowledgeBasePermission::getKnowledgeBaseId)
                .eq(KnowledgeBase::getDeleteFlag, false)
                .eq(KnowledgeBasePermission::getUserId, users.get(0).getId())
                .eq(KnowledgeBasePermission::getDeleteFlag, false)
                .eq(KnowledgeBasePermission::getStatus, 1);

        return this.list(wrapper).stream()
                .map(KnowledgeBasePermission::getKnowledgeBaseId)
                .toList();
    }
} 