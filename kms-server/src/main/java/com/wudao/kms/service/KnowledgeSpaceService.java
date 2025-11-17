package com.wudao.kms.service;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.kms.entity.KnowledgeSpace;
import com.wudao.kms.mapper.KnowledgeSpaceMapper;
import com.wudao.kms.vo.KnowledgeSpaceTreeVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库空间Service
 */
@Service
public class KnowledgeSpaceService extends MPJBaseServiceImpl<KnowledgeSpaceMapper, KnowledgeSpace> {

    @Value("${vectorization.api.url:http://localhost:8080}")
    private String vectorizationApiUrl;
    @Resource
    private RestTemplate restTemplate;

    /**
     * 根据知识库ID查询空间树
     */
    public List<KnowledgeSpace> listByKnowledgeBaseId(Long knowledgeBaseId) {
        LambdaQueryWrapper<KnowledgeSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeSpace::getKnowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeSpace::getDeleteFlag, false)
                .orderByAsc(KnowledgeSpace::getLevel)
                .orderByAsc(KnowledgeSpace::getSortOrder)
                .orderByAsc(KnowledgeSpace::getName);
        return this.list(wrapper);
    }

    /**
     * 根据父级ID查询子空间
     */
    public List<KnowledgeSpace> listByParentId(Long parentId) {
        LambdaQueryWrapper<KnowledgeSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeSpace::getParentId, parentId)
                .eq(KnowledgeSpace::getDeleteFlag, false)
                .orderByAsc(KnowledgeSpace::getSortOrder)
                .orderByAsc(KnowledgeSpace::getName);
        return this.list(wrapper);
    }

    /**
     * 创建空间
     */
    public Boolean createSpace(KnowledgeSpace space) {
        // 检查名称是否重复
        if (checkNameExists(space.getName(), space.getKnowledgeBaseId(), space.getParentId(), null)) {
            throw new RuntimeException("同级目录下已存在同名空间");
        }

        // 设置默认值
        if (space.getType() == null) {
            space.setType(1); // 默认为文件夹
        }
        if (space.getSortOrder() == null) {
            space.setSortOrder(0);
        }
        if (space.getFileCount() == null) {
            space.setFileCount(0);
        }

        // 计算层级和路径
        if (space.getParentId() == null || space.getParentId() == 0) {
            // 根目录
            space.setParentId(0L);
            space.setLevel(1);
            space.setPath("/" + space.getName());
        } else {
            // 子目录
            KnowledgeSpace parent = this.getById(space.getParentId());
            if (parent == null) {
                throw new RuntimeException("父级空间不存在");
            }
            space.setLevel(parent.getLevel() + 1);
            space.setPath(parent.getPath() + "/" + space.getName());
        }

        return this.save(space);
    }

    /**
     * 更新空间
     */
    public Boolean updateSpace(KnowledgeSpace space) {
        // 检查名称是否重复（排除自己）
        if (StringUtils.hasText(space.getName())) {
            KnowledgeSpace existing = this.getById(space.getId());
            if (existing != null &&
                    checkNameExists(space.getName(), existing.getKnowledgeBaseId(), existing.getParentId(), space.getId())) {
                throw new RuntimeException("同级目录下已存在同名空间");
            }
        }

        return this.updateById(space);
    }

    /**
     * 删除空间
     */
    public Boolean deleteSpace(Long id) {
        // 检查是否有子空间
        if (hasChildren(id)) {
            throw new RuntimeException("该空间下还有子空间，无法删除");
        }
        // 软删除
        KnowledgeSpace space = new KnowledgeSpace();
        space.setId(id);
        space.setDeleteFlag(true);
        boolean flag = this.updateById(space);
        if (flag) {
            // 删除文件
            ThreadUtil.execute(() -> {
                try {
                    String url = vectorizationApiUrl + "/document";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("knowledge_space", id);
                    restTemplate.postForLocation(url, jsonObject);
                } catch (Exception e) {
                    log.error("删除失败");
                }
            });
        }
        return flag;
    }

    /**
     * 移动空间
     */
    public Boolean moveSpace(Long spaceId, Long newParentId) {
        KnowledgeSpace space = this.getById(spaceId);
        if (space == null) {
            throw new RuntimeException("空间不存在");
        }

        // 检查是否移动到自己的子目录
        if (isDescendant(newParentId, spaceId)) {
            throw new RuntimeException("不能移动到自己的子目录");
        }

        // 检查目标位置是否有同名空间
        if (checkNameExists(space.getName(), space.getKnowledgeBaseId(), newParentId, spaceId)) {
            throw new RuntimeException("目标位置已存在同名空间");
        }

        // 更新空间信息
        if (newParentId == null || newParentId == 0) {
            // 移动到根目录
            space.setParentId(0L);
            space.setLevel(1);
            space.setPath("/" + space.getName());
        } else {
            // 移动到指定目录
            KnowledgeSpace parent = this.getById(newParentId);
            if (parent == null) {
                throw new RuntimeException("目标父级空间不存在");
            }
            space.setParentId(newParentId);
            space.setLevel(parent.getLevel() + 1);
            space.setPath(parent.getPath() + "/" + space.getName());
        }

        boolean result = this.updateById(space);

        // 更新所有子空间的路径和层级
        if (result) {
            updateChildrenPath(spaceId);
        }

        return result;
    }

    /**
     * 检查名称是否已存在
     */
    private boolean checkNameExists(String name, Long knowledgeBaseId, Long parentId, Long excludeId) {
        LambdaQueryWrapper<KnowledgeSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeSpace::getName, name)
                .eq(KnowledgeSpace::getKnowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeSpace::getParentId, parentId)
                .eq(KnowledgeSpace::getDeleteFlag, false)
                .ne(excludeId != null, KnowledgeSpace::getId, excludeId);
        return this.count(wrapper) > 0;
    }

    /**
     * 检查是否有子空间
     */
    private boolean hasChildren(Long spaceId) {
        LambdaQueryWrapper<KnowledgeSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeSpace::getParentId, spaceId)
                .eq(KnowledgeSpace::getDeleteFlag, false);
        return this.count(wrapper) > 0;
    }

    /**
     * 检查是否有文件
     */
    private boolean hasFiles(Long spaceId) {
        // 这里需要注入KnowledgeFileService来检查
        // 为了简化，先返回false，实际项目中需要实现
        return false;
    }

    /**
     * 检查是否为后代节点
     */
    private boolean isDescendant(Long ancestorId, Long descendantId) {
        if (ancestorId == null || ancestorId == 0) {
            return false;
        }

        KnowledgeSpace space = this.getById(ancestorId);
        while (space != null && space.getParentId() != 0) {
            if (space.getParentId().equals(descendantId)) {
                return true;
            }
            space = this.getById(space.getParentId());
        }
        return false;
    }

    /**
     * 更新子空间的路径和层级
     */
    private void updateChildrenPath(Long parentId) {
        List<KnowledgeSpace> children = listByParentId(parentId);
        for (KnowledgeSpace child : children) {
            KnowledgeSpace parent = this.getById(parentId);
            child.setLevel(parent.getLevel() + 1);
            child.setPath(parent.getPath() + "/" + child.getName());
            this.updateById(child);

            // 递归更新子空间
            updateChildrenPath(child.getId());
        }
    }

    /**
     * 获取空间的完整路径
     */
    public String getFullPath(Long spaceId) {
        KnowledgeSpace space = this.getById(spaceId);
        return space != null ? space.getPath() : "";
    }

    /**
     * 根据路径查询空间
     */
    public KnowledgeSpace getByPath(Long knowledgeBaseId, String path) {
        LambdaQueryWrapper<KnowledgeSpace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeSpace::getKnowledgeBaseId, knowledgeBaseId)
                .eq(KnowledgeSpace::getPath, path)
                .eq(KnowledgeSpace::getDeleteFlag, false);
        return this.getOne(wrapper);
    }

    /**
     * 根据知识库ID查询空间树形结构
     */
    public List<KnowledgeSpaceTreeVO> getSpaceTree(Long knowledgeBaseId) {
        // 获取该知识库下所有空间
        List<KnowledgeSpace> allSpaces = listByKnowledgeBaseId(knowledgeBaseId);

        // 转换为VO对象
        List<KnowledgeSpaceTreeVO> spaceVOs = allSpaces.stream()
                .map(KnowledgeSpaceTreeVO::fromEntity)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildSpaceTree(spaceVOs);
    }

    /**
     * 构建空间树形结构
     */
    private List<KnowledgeSpaceTreeVO> buildSpaceTree(List<KnowledgeSpaceTreeVO> allSpaces) {
        List<KnowledgeSpaceTreeVO> rootSpaces = new ArrayList<>();

        // 找出根节点（parentId为0或null）
        for (KnowledgeSpaceTreeVO space : allSpaces) {
            if (space.getParentId() == null || space.getParentId() == 0) {
                rootSpaces.add(space);
            }
        }

        // 为每个根节点构建子树
        for (KnowledgeSpaceTreeVO rootSpace : rootSpaces) {
            buildChildren(rootSpace, allSpaces);
        }

        return rootSpaces;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildren(KnowledgeSpaceTreeVO parent, List<KnowledgeSpaceTreeVO> allSpaces) {
        List<KnowledgeSpaceTreeVO> children = new ArrayList<>();

        // 找出当前节点的直接子节点
        for (KnowledgeSpaceTreeVO space : allSpaces) {
            if (space.getParentId() != null && space.getParentId().equals(parent.getId())) {
                children.add(space);
                // 递归构建子节点的子树
                buildChildren(space, allSpaces);
            }
        }

        // 按排序和名称排序
        children.sort((a, b) -> {
            int sortCompare = Integer.compare(
                    a.getSortOrder() != null ? a.getSortOrder() : 0,
                    b.getSortOrder() != null ? b.getSortOrder() : 0
            );
            if (sortCompare != 0) {
                return sortCompare;
            }
            return a.getName().compareTo(b.getName());
        });

        parent.setChildren(children);
    }
} 