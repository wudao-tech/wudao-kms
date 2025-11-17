package com.wudao.kms.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.dto.KnowledgeBaseQueryDTO;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeBasePermission;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.KnowledgeSpace;
import com.wudao.kms.mapper.KnowledgeBaseMapper;
import com.wudao.kms.vo.KnowledgeBaseVO;
import com.wudao.security.utils.SecurityUtils;
import com.wudao.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识库Service
 */
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService extends MPJBaseServiceImpl<KnowledgeBaseMapper, KnowledgeBase> {

    private final KnowledgeBasePermissionService knowledgeBasePermissionService;
    private final KnowledgeFileService knowledgeFileService;
    private final KnowledgeSpaceService knowledgeSpaceService;
    private final SysUserMapper sysUserMapper;
    @Value("${vectorization.api.url:http://localhost:8080}")
    private String vectorizationApiUrl;
    private final RestTemplate restTemplate;
    @Value("${basic.profile}")
    private String basicProfile;
    private final KnowledgeCommonService knowledgeCommonService;

    /**
     * 按照查询条件查询知识库列表（带关联统计信息和权限过滤）
     */
    public List<KnowledgeBaseVO> list(KnowledgeBaseQueryDTO queryDTO, PageDomain<KnowledgeBaseVO> page) {
        // 1. 首先根据用户ID获取有权限的知识库ID列表
        List<Long> accessibleKnowledgeBaseIds = new ArrayList<>();
        //查询公共知识库
        List<KnowledgeBasePermission> knowledgePermissions = knowledgeBasePermissionService.getKnowledgeBaseByUserId(queryDTO.getUserId());
        if (queryDTO.getUserId() != null) {
            accessibleKnowledgeBaseIds = knowledgePermissions.stream().map(KnowledgeBasePermission::getKnowledgeBaseId).collect(Collectors.toList());
            //查询公共知识库
            List<Long> publicKnowledgeBaseIds = this.list(new LambdaQueryWrapper<KnowledgeBase>()
                            .eq(KnowledgeBase::getDeleteFlag, false)
                            .eq(KnowledgeBase::getPermissionType, 1)) // 0表示公共知识库
                    .stream()
                    .map(KnowledgeBase::getId)
                    .toList();
            if (CollUtil.isNotEmpty(publicKnowledgeBaseIds)) {
                if (CollUtil.isEmpty(accessibleKnowledgeBaseIds)) {
                    accessibleKnowledgeBaseIds = publicKnowledgeBaseIds;
                } else {
                    // 如果用户有权限的知识库ID列表  不为空，则合并公共知识库ID
                    accessibleKnowledgeBaseIds = new ArrayList<>(accessibleKnowledgeBaseIds);
                    accessibleKnowledgeBaseIds.addAll(publicKnowledgeBaseIds);
                }
                accessibleKnowledgeBaseIds = accessibleKnowledgeBaseIds.stream().distinct().toList();
            }
            if (CollUtil.isEmpty(accessibleKnowledgeBaseIds)) {
                return List.of();
            }
        }

        // 2. 查询知识库基本信息
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getName() != null, KnowledgeBase::getName, queryDTO.getName())
                .eq(queryDTO.getStatus() != null, KnowledgeBase::getStatus, queryDTO.getStatus())
                .in(KnowledgeBase::getId, accessibleKnowledgeBaseIds)
                //tags是list，库里是存的1,1,3,5 需要调用函数进行contains
                .apply(StringUtil.isNotBlank(queryDTO.getTags()), "string_to_array(tags, ',')::TEXT[] && string_to_array({0}, ',')::TEXT[]", queryDTO.getTags())
                .eq(KnowledgeBase::getDeleteFlag, false)
                .orderByDesc(KnowledgeBase::getCreatedAt);

        IPage<KnowledgeBase> pageResult = this.page(new Page<>(page.getPageNum(), page.getPageSize()), wrapper);
        page.setTotal(pageResult.getTotal());
        List<KnowledgeBase> knowledgeBases = pageResult.getRecords();
        if (knowledgeBases.isEmpty()) {
            return List.of();
        }

        // 3. 批量查询每个知识库的文件统计信息
        List<Long> knowledgeBaseIds = knowledgeBases.stream().map(KnowledgeBase::getId).toList();

        // 使用Map来存储文件统计信息，key为知识库ID  
        Map<Long, Map<String, Object>> fileStatsMap = new HashMap<>();

        // 批量查询所有相关文件，然后在内存中分组统计，避免N+1查询问题
        if (!knowledgeBaseIds.isEmpty()) {
            LambdaQueryWrapper<KnowledgeFile> fileWrapper = new LambdaQueryWrapper<>();
            fileWrapper.in(KnowledgeFile::getKnowledgeBaseId, knowledgeBaseIds)
                    .eq(KnowledgeFile::getDeleteFlag, false);

            List<KnowledgeFile> allFiles = knowledgeFileService.list(fileWrapper);

            // 按知识库ID分组统计
            Map<Long, List<KnowledgeFile>> filesByKnowledgeBase = allFiles.stream()
                    .collect(java.util.stream.Collectors.groupingBy(KnowledgeFile::getKnowledgeBaseId));

            for (Long kbId : knowledgeBaseIds) {
                List<KnowledgeFile> files = filesByKnowledgeBase.getOrDefault(kbId, List.of());
                long fileCount = files.size();
                long totalSize = files.stream().mapToLong(f -> f.getFileSize() != null ? f.getFileSize() : 0L).sum();
                Map<String, Object> stat = new HashMap<>();
                stat.put("fileCount", fileCount);
                stat.put("totalSize", totalSize);
                fileStatsMap.put(kbId, stat);
            }
        }

        // 4. 如果指定了用户ID，获取该用户对每个知识库的权限信息
        Map<Long, KnowledgeBasePermission> permissionMap = knowledgePermissions.stream().collect(Collectors.toMap(KnowledgeBasePermission::getKnowledgeBaseId, p -> p));

        // 5. 批量查询创建人和修改人姓名
        // 收集所有需要查询的用户ID
        List<Long> allUserIds = new ArrayList<>();
        for (KnowledgeBase kb : knowledgeBases) {
            if (kb.getCreatedBy() != null) {
                allUserIds.add(kb.getCreatedBy());
            }
            if (kb.getUpdatedBy() != null) {
                allUserIds.add(kb.getUpdatedBy());
            }
        }

        // 批量查询用户名
        List<SysUser> sysUsers = sysUserMapper.selectByIds(allUserIds);
        Map<Long, String> userNameMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));

        // 6. 组装结果
        List<KnowledgeBaseVO> result = new ArrayList<>();
        for (KnowledgeBase kb : knowledgeBases) {
            KnowledgeBaseVO vo = new KnowledgeBaseVO();
            BeanUtil.copyProperties(kb, vo, true); // 复制属性，忽略null值
            // 设置创建人和修改人姓名
            vo.setCreatedByName(userNameMap.get(kb.getCreatedBy()));
            vo.setUpdatedByName(userNameMap.get(kb.getUpdatedBy()));

            // 设置实际文件统计信息
            Map<String, Object> fileStat = fileStatsMap.get(kb.getId());
            if (fileStat != null) {
                vo.setActualFileCount(((Number) fileStat.get("fileCount")).longValue());
                vo.setActualTotalSize(((Number) fileStat.get("totalSize")).longValue());
            } else {
                vo.setActualFileCount(0L);
                vo.setActualTotalSize(0L);
            }

            // 设置权限信息
            KnowledgeBasePermission permission = permissionMap.get(kb.getId());
            if (permission != null) {
                vo.setPermissionId(permission.getId());
                vo.setPermissionUserId(permission.getUserId());
                vo.setPermissionUserName(permission.getUserName());
                vo.setPermissionUserEmail(permission.getUserEmail());
                vo.setPermissionUserRole(permission.getUserRole());
                vo.setKnowledgePermissionType(permission.getPermissionType());
                vo.setPermissionStatus(permission.getStatus());
                vo.setCurrentUserRoleKey(permission.getUserRole());
            } else {
                vo.setKnowledgePermissionType(3);
            }

            result.add(vo);
        }
        return result;
    }

    /**
     * 带权限信息的分页查询知识库列表
     */
    public List<KnowledgeBaseVO> listWithPermission(KnowledgeBaseQueryDTO queryDTO, Long currentUserId, PageDomain<KnowledgeBaseVO> page) {
        queryDTO.setUserId(currentUserId);
        List<KnowledgeBaseVO> resultList = this.list(queryDTO, page);

        // 为每个知识库设置当前用户的角色Key
        for (KnowledgeBaseVO vo : resultList) {
            vo.setCurrentUserRoleKey(vo.getPermissionUserRole());
        }

        return resultList;
    }

    /**
     * 查询所有可用的知识库
     */
    public List<KnowledgeBase> listAvailableKnowledgeBase() {
        List<Long> baseIds = queryPermissionKnowledgeList(SecurityUtils.getUserId());
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getDeleteFlag, false)
                .eq(KnowledgeBase::getStatus, 1)
                .in(KnowledgeBase::getId, baseIds)
                .orderByDesc(KnowledgeBase::getCreatedAt);
        return this.list(wrapper);
    }

    /**
     * 创建知识库
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean createKnowledgeBase(KnowledgeBase knowledgeBase) {
        // 检查名称是否重复
        if (checkNameExists(knowledgeBase.getName())) {
            throw new ServiceException("req_knowledge_base_name_exists", "知识库名称已存在");
        }

        // 设置默认状态
        if (knowledgeBase.getStatus() == null) {
            knowledgeBase.setStatus(1);
        }

        // 保存知识库
        boolean result = this.save(knowledgeBase);

        if (result && knowledgeBase.getId() != null) {
            // 创建默认的超级管理员权限
            // 这里需要获取当前用户信息，暂时使用默认值
            Long currentUserId = SecurityUtils.getUserId(); // 需要实现获取当前用户ID的方法
            String currentUserEmail = "test@wudao-tech.com"; // 需要实现获取当前用户邮箱的方法

            knowledgeBasePermissionService.createDefaultSuperAdminPermission(
                    knowledgeBase.getId(),
                    currentUserId,
                    currentUserEmail
            );

            // 创建默认根空间
            createDefaultRootSpace(knowledgeBase.getId(), knowledgeBase.getName());
        }

        return result;
    }

    /**
     * 更新知识库
     */
    public Boolean updateKnowledgeBase(KnowledgeBase knowledgeBase) {
        if (knowledgeBase.getId() == null) {
            throw new ServiceException("req_knowledge_base_id_required", "知识库ID不能为空");
        }
        if (!knowledgeCommonService.hasRootPermission(knowledgeBase.getId(), SecurityUtils.getUserId())) {
            throw new ServiceException("req_knowledge_base_permission_denied", "没有权限修改该知识库");
        }
        return this.updateById(knowledgeBase);
    }

    /**
     * 删除知识库
     */
    public Boolean deleteKnowledgeBase(Long id) {
        // 软删除
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setId(id);
        knowledgeBase.setDeleteFlag(true);
        // 删除文件
        ThreadUtil.execute(() -> {
            try {
                String url = vectorizationApiUrl + "/delete";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("knowledge_base_id", id);
                restTemplate.postForLocation(url, jsonObject);

                // 同时删除磁盘上的文件
                String folderPath = basicProfile + File.separator +
                        "knowledge" + File.separator +
                        id;
                FileUtil.del(folderPath);

                LambdaQueryWrapper<KnowledgeFile> fileWrapper = new LambdaQueryWrapper<>();
                fileWrapper.eq(KnowledgeFile::getKnowledgeBaseId,id);
                knowledgeFileService.remove(fileWrapper);

                //删除空间 删除文件
                LambdaQueryWrapper<KnowledgeSpace> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(KnowledgeSpace::getKnowledgeBaseId,id);
                knowledgeSpaceService.remove(wrapper);

                //删除收藏
            } catch (Exception e) {
                log.error("删除失败");
            }
        });
        return this.removeById(id);
    }

    /**
     * 检查名称是否已存在
     */
    private boolean checkNameExists(String name) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getName, name)
                .eq(KnowledgeBase::getDeleteFlag, false)
                .eq(KnowledgeBase::getCreatedBy, SecurityUtils.getUserId());
        return this.count(wrapper) > 0;
    }

    /**
     * 检查是否有关联的文件
     */
    private boolean hasRelatedFiles(Long knowledgeBaseId) {
        return false;
    }

    /**
     * 启用知识库
     */
    public Boolean enableKnowledgeBase(Long id) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setId(id);
        knowledgeBase.setStatus(1);
        return this.updateById(knowledgeBase);
    }

    /**
     * 禁用知识库
     */
    public Boolean disableKnowledgeBase(Long id) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setId(id);
        knowledgeBase.setStatus(0);
        return this.updateById(knowledgeBase);
    }

    /**
     * 根据名称搜索知识库
     *
     * @param name      知识库名称
     * @param createdBy 创建人
     */
    public List<KnowledgeBase> searchByName(String name, String createdBy) {
        List<Long> knowledgeList = queryPermissionKnowledgeList(createdBy);
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), KnowledgeBase::getName, name)
                .eq(KnowledgeBase::getDeleteFlag, false)
                .eq(KnowledgeBase::getStatus, 1)
                .in(KnowledgeBase::getId, knowledgeList)
                .orderByDesc(KnowledgeBase::getCreatedAt);
        return this.list(wrapper);
    }

    /**
     * 创建默认根空间
     */
    private void createDefaultRootSpace(Long knowledgeBaseId, String knowledgeBaseName) {
        try {
            KnowledgeSpace rootSpace = new KnowledgeSpace();
            rootSpace.setKnowledgeBaseId(knowledgeBaseId);
            rootSpace.setParentId(0L);
            rootSpace.setName("根目录"); // 使用固定的根目录名称，避免重名问题
            rootSpace.setType(1); // 文件夹类型
            rootSpace.setPath("/根目录");
            rootSpace.setLevel(1);
            rootSpace.setSortOrder(0);
            rootSpace.setFileCount(0);
            rootSpace.setDeleteFlag(false);

            knowledgeSpaceService.save(rootSpace);
        } catch (Exception e) {
            // 如果创建默认根空间失败，记录日志但不影响知识库创建
            System.err.println("创建知识库默认根空间失败: " + e.getMessage());
        }
    }

    /**
     * 查询有权限的知识库id列表
     *
     * @param userId
     * @return
     */
    public List<Long> queryPermissionKnowledgeList(Long userId) {
        // 根据用户id查询用户username
        SysUser user = sysUserMapper.selectById(userId);
        List<Long> accessibleKnowledgeBaseIds;
        accessibleKnowledgeBaseIds = knowledgeBasePermissionService.getKnowledgeBaseIdsByUserId(user.getUsername());
        //查询公共知识库
        List<Long> publicKnowledgeBaseIds = this.list(new LambdaQueryWrapper<KnowledgeBase>()
                        .eq(KnowledgeBase::getDeleteFlag, false)
                        .eq(KnowledgeBase::getPermissionType, 1)) // 0表示公共知识库
                .stream()
                .map(KnowledgeBase::getId)
                .toList();
        if (CollUtil.isNotEmpty(publicKnowledgeBaseIds)) {
            if (CollUtil.isEmpty(accessibleKnowledgeBaseIds)) {
                accessibleKnowledgeBaseIds = publicKnowledgeBaseIds;
            } else {
                // 如果用户有权限的知识库ID列表  不为空，则合并公共知识库ID
                accessibleKnowledgeBaseIds = new ArrayList<>(accessibleKnowledgeBaseIds);
                accessibleKnowledgeBaseIds.addAll(publicKnowledgeBaseIds);
            }
            accessibleKnowledgeBaseIds = accessibleKnowledgeBaseIds.stream().distinct().toList();
        }
        if (CollUtil.isEmpty(accessibleKnowledgeBaseIds)) {
            return List.of();
        }
        return accessibleKnowledgeBaseIds;
    }

    /**
     * 查询有权限的知识库id列表
     *
     * @param userName
     * @return
     */
    public List<Long> queryPermissionKnowledgeList(String userName) {
        List<Long> accessibleKnowledgeBaseIds;
        accessibleKnowledgeBaseIds = knowledgeBasePermissionService.getKnowledgeBaseIdsByUserId(userName);
        //查询公共知识库
        List<Long> publicKnowledgeBaseIds = this.list(new LambdaQueryWrapper<KnowledgeBase>()
                        .eq(KnowledgeBase::getDeleteFlag, false)
                        .eq(KnowledgeBase::getPermissionType, 1)) // 0表示公共知识库
                .stream()
                .map(KnowledgeBase::getId)
                .toList();
        if (CollUtil.isNotEmpty(publicKnowledgeBaseIds)) {
            if (CollUtil.isEmpty(accessibleKnowledgeBaseIds)) {
                accessibleKnowledgeBaseIds = publicKnowledgeBaseIds;
            } else {
                // 如果用户有权限的知识库ID列表  不为空，则合并公共知识库ID
                accessibleKnowledgeBaseIds = new ArrayList<>(accessibleKnowledgeBaseIds);
                accessibleKnowledgeBaseIds.addAll(publicKnowledgeBaseIds);
            }
            accessibleKnowledgeBaseIds = accessibleKnowledgeBaseIds.stream().distinct().toList();
        }
        if (CollUtil.isEmpty(accessibleKnowledgeBaseIds)) {
            return List.of();
        }
        return accessibleKnowledgeBaseIds;
    }

    public List<Long> queryDisableKnowledge() {
        return this.baseMapper.queryDisableKnowledge();
    }

} 