package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.KnowledgeSpace;
import com.wudao.kms.entity.VisitRecord;
import com.wudao.kms.mapper.VisitRecordMapper;
import com.wudao.kms.vo.VisitRecordVO;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 访问记录Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitRecordService extends MPJBaseServiceImpl<VisitRecordMapper, VisitRecord> {

    /**
     * 记录访问
     */
    public Long recordVisit(String targetType, Long targetId, String targetName, String targetUrl, String ipAddress, String userAgent) {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                log.warn("用户未登录，无法记录访问");
                return 0L;
            }

            VisitRecord visitRecord = new VisitRecord()
                    .setUserId(userId)
                    .setTargetType(targetType)
                    .setTargetId(targetId)
                    .setTargetName(targetName)
                    .setTargetUrl(targetUrl)
                    .setVisitTime(LocalDateTime.now())
                    .setIpAddress(ipAddress)
                    .setUserAgent(userAgent);

            this.save(visitRecord);
            log.debug("记录访问成功: userId={}, targetType={}, targetId={}", userId, targetType, targetId);

            //查询总数
            LambdaQueryWrapper<VisitRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(VisitRecord::getUserId, userId)
                    .eq(VisitRecord::getTargetType, targetType)
                    .eq(VisitRecord::getTargetId, targetId)
                    .eq(VisitRecord::getDeleteFlag, false);
            return this.count(wrapper);
        } catch (Exception e) {
            log.error("记录访问失败: targetType={}, targetId={}", targetType, targetId, e);
            return 0L;
        }
    }

    /**
     * 获取用户访问记录列表
     */
    public List<VisitRecordVO> getUserVisitRecords(Integer limit, PageDomain<VisitRecordVO> page) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }

        if (limit == null || limit <= 0) {
            limit = 20;
        }

        MPJLambdaWrapper<VisitRecord> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(VisitRecord.class);
        wrapper.selectAs(KnowledgeSpace::getName,VisitRecordVO::getSpaceName);
        wrapper.selectAs(KnowledgeSpace::getId,VisitRecordVO::getSpaceId);
        wrapper.selectAs(KnowledgeBase::getName,VisitRecordVO::getKnowledgeName);
        wrapper.selectAs(KnowledgeBase::getId,VisitRecordVO::getKnowledgeId);
        wrapper.selectAs(SysUser::getNickname, VisitRecordVO::getCreatorName);
        wrapper.leftJoin(KnowledgeFile.class, KnowledgeFile::getId, VisitRecord::getTargetId)
                .leftJoin(KnowledgeSpace.class, KnowledgeSpace::getId, KnowledgeFile::getSpaceId)
                .leftJoin(KnowledgeBase.class, KnowledgeBase::getId, KnowledgeFile::getKnowledgeBaseId)
                .leftJoin(SysUser.class, SysUser::getId, KnowledgeFile::getCreatedBy)
                .eq(VisitRecord::getUserId, userId)
                .eq(VisitRecord::getDeleteFlag, false)

                .eq(VisitRecord::getTargetType, "knowledge_file")
                .orderByDesc(VisitRecord::getVisitTime);
        Page<VisitRecordVO> objectPage = new Page<>(page.getPageNum(), page.getPageSize());
        Page<VisitRecordVO> pageResult = this.selectJoinListPage(objectPage, VisitRecordVO.class, wrapper);

        page.setTotal(pageResult.getTotal());
        
        return pageResult.getRecords();
    }

    /**
     * 统计用户访问总数
     */
    public Long countUserVisits(Long userId) {
        return this.count(new LambdaQueryWrapper<VisitRecord>()
                .eq(VisitRecord::getUserId, userId)
                .eq(VisitRecord::getDeleteFlag, false));
    }

    public Long visitCountByFileIds(){
        MPJLambdaWrapper<VisitRecord> wrapper = new MPJLambdaWrapper<>();
        wrapper.leftJoin(KnowledgeFile.class,KnowledgeFile::getId,VisitRecord::getTargetId);
        wrapper.leftJoin(KnowledgeBase.class,KnowledgeBase::getId,KnowledgeFile::getKnowledgeBaseId);
        wrapper.select(VisitRecord::getTargetId)
                .eq(VisitRecord::getUserId,SecurityUtils.getUserId())

                .eq(VisitRecord::getDeleteFlag, false);

        return this.count(wrapper);
    }

    /**
     * 统计用户今日访问数
     */
    public Long countUserTodayVisits(Long userId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        return this.count(new LambdaQueryWrapper<VisitRecord>()
                .eq(VisitRecord::getUserId, userId)
                .eq(VisitRecord::getDeleteFlag, false)
                .between(VisitRecord::getVisitTime, todayStart, todayEnd));
    }

    /**
     * 统计用户本周访问数
     */
    public Long countUserThisWeekVisits(Long userId) {
        LocalDateTime weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        
        return this.count(new LambdaQueryWrapper<VisitRecord>()
                .eq(VisitRecord::getUserId, userId)
                .eq(VisitRecord::getDeleteFlag, false)
                .between(VisitRecord::getVisitTime, weekStart, now));
    }

    /**
     * 删除访问记录
     */
    public Boolean deleteVisitRecord(Long id) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }

        VisitRecord visitRecord = this.getById(id);
        if (visitRecord == null || !visitRecord.getUserId().equals(userId)) {
            return false;
        }

        visitRecord.setDeleteFlag(true);
        return this.updateById(visitRecord);
    }

    public Map<Long,Integer> queryViewCountByTargetId(List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return Map.of();
        }

        LambdaQueryWrapper<VisitRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(VisitRecord::getTargetId, VisitRecord::getId)
                .in(VisitRecord::getTargetId, targetIds)
                .eq(VisitRecord::getDeleteFlag, false);

        List<VisitRecord> records = this.list(wrapper);

        return records.stream()
                .collect(Collectors.groupingBy(VisitRecord::getTargetId, Collectors.summingInt(r -> 1)));
    }

    /**
     * 转换为VO对象
     */
    private VisitRecordVO convertToVO(VisitRecord visitRecord) {
        VisitRecordVO vo = new VisitRecordVO();
        BeanUtils.copyProperties(visitRecord, vo);
        return vo;
    }
} 