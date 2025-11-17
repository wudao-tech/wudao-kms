package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeBasePermission;
import com.wudao.kms.mapper.KnowledgeBaseMapper;
import com.wudao.kms.mapper.KnowledgeBasePermissionMapper;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KnowledgeCommonService {
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBasePermissionMapper knowledgeBasePermissionMapper;

    /**
     * 通用权限方法
     */
    public Boolean hasRootPermission(Long knowledgeBaseId, Long userId) {
        Long currentUser = SecurityUtils.getUserId();
        if (SecurityUtils.assertLoginUser().isAdmin()){
            return true;
        }
        // 查询知识库是否存在
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId);
        if (knowledgeBase == null) {
            return false; // 知识库不存在
        }
        // 检查当前用户是否为知识库的创建者
        if (knowledgeBase.getCreatedBy().equals(currentUser)) {
            return true; // 当前用户是知识库创建者
        }
        // 判断是否为知识库的管理员
        LambdaQueryWrapper<KnowledgeBasePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBasePermission::getKnowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeBasePermission::getUserId, userId)
                .last("limit 1");
        KnowledgeBasePermission permission = knowledgeBasePermissionMapper.selectOne(wrapper);
        return permission != null && permission.getPermissionType() == 1; // 用户是知识库管理员
    }
}
