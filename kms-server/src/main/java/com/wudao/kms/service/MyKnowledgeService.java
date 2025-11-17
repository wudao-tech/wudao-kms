package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeSpace;
import com.wudao.kms.mapper.KnowledgeFileMapper;
import com.wudao.kms.vo.MyKnowledgeVO;
import com.wudao.security.utils.SecurityUtils;
import com.wudao.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 我的知识Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MyKnowledgeService {

    private final KnowledgeFileMapper knowledgeFileMapper;
    private final KnowledgeBaseService knowledgeBaseService;
    private final KnowledgeSpaceService knowledgeSpaceService;
    private final SysUserMapper sysUserMapper;

    /**
     * 获取我的知识列表
     */
    public List<MyKnowledgeVO> getMyKnowledgeList(PageDomain<MyKnowledgeVO> page) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }

        // 简单查询用户创建的文件
        MPJLambdaWrapper<KnowledgeFile> wrapper = new MPJLambdaWrapper<>();
        wrapper.leftJoin(KnowledgeBase.class,KnowledgeBase::getId,KnowledgeFile::getKnowledgeBaseId);
        wrapper.leftJoin(KnowledgeSpace.class,KnowledgeSpace::getId,KnowledgeFile::getSpaceId);
        wrapper.eq(KnowledgeFile::getCreatedBy, userId)

                .eq(KnowledgeSpace::getDeleteFlag,false)
                .eq(KnowledgeFile::getDeleteFlag, false)
                .orderByDesc(KnowledgeFile::getCreatedAt);

        IPage<KnowledgeFile> pageResult = knowledgeFileMapper.selectPage(
                new Page<>(page.getPageNum(), page.getPageSize()),
                wrapper);

        page.setTotal(pageResult.getTotal());
        List<KnowledgeFile> files = pageResult.getRecords();

        if (files.isEmpty()) {
            return List.of();
        }

        // 获取创建人信息
        List<Long> userIds = files.stream()
                .map(KnowledgeFile::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<SysUser> users = sysUserMapper.selectByIds(userIds);
        Map<Long, String> userNameMap = users.stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));

        // 获取知识库信息
        List<Long> knowledgeBaseIds = files.stream()
                .map(KnowledgeFile::getKnowledgeBaseId)
                .distinct()
                .toList();

        LambdaQueryWrapper<KnowledgeBase> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.in(KnowledgeBase::getId,knowledgeBaseIds);
        List<KnowledgeBase> knowledgeBases = knowledgeBaseService.list(baseWrapper);
        if (CollUtil.isEmpty(knowledgeBases)){
            return new ArrayList<>();
        }

        Map<Long, String> knowledgeBaseNameMap = knowledgeBases.stream().collect(Collectors.toMap(KnowledgeBase::getId, KnowledgeBase::getName));

        // 获取知识空间信息
        List<Long> spaceIds = files.stream()
                .map(KnowledgeFile::getSpaceId)
                .distinct()
                .toList();

        LambdaQueryWrapper<KnowledgeSpace> spaceWrapper = new LambdaQueryWrapper<>();
        spaceWrapper.in(KnowledgeSpace::getId, spaceIds);
        spaceWrapper.eq(KnowledgeSpace::getDeleteFlag,false);
        List<KnowledgeSpace> knowledgeSpaces = knowledgeSpaceService.list(spaceWrapper);
        Map<Long, String> spaceNameMap = knowledgeSpaces.stream().collect(Collectors.toMap(KnowledgeSpace::getId, KnowledgeSpace::getName));


        // 转换为VO
        return files.stream()
                .filter(item -> knowledgeBaseNameMap.containsKey(item.getKnowledgeBaseId()) && spaceNameMap.containsKey(item.getSpaceId()))
                .map(file -> convertToVO(file, userNameMap, knowledgeBaseNameMap, spaceNameMap))
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO对象
     */
    private MyKnowledgeVO convertToVO(KnowledgeFile file, Map<Long, String> userNameMap,
                                      Map<Long, String> knowledgeBaseNameMap, Map<Long, String> spaceNameMap) {
        MyKnowledgeVO vo = new MyKnowledgeVO();
        vo.setKnowledgeBaseId(file.getKnowledgeBaseId())
                .setKnowledgeBaseName(knowledgeBaseNameMap.get(file.getKnowledgeBaseId()))
                .setKnowledgeSpaceId(file.getSpaceId())
                .setFileId(file.getId())
                .setKnowledgeSpaceName(spaceNameMap.get(file.getSpaceId()))
                .setFileName(file.getFileName())
                .setCreatedBy(userNameMap.get(file.getCreatedBy()))
                .setCreatedAt(file.getCreatedAt());

        return vo;
    }
} 