package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.entity.CommentRecord;
import com.wudao.kms.mapper.CommentRecordMapper;
import com.wudao.kms.vo.CommentRecordVO;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论记录Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentRecordService extends MPJBaseServiceImpl<CommentRecordMapper, CommentRecord> {

    /**
     * 添加评论
     */
    public Boolean addComment(String targetType, Long targetId, String targetName, String commentContent, Integer rating) {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                log.warn("用户未登录，无法添加评论");
                return false;
            }

            CommentRecord commentRecord = new CommentRecord()
                    .setUserId(userId)
                    .setTargetType(targetType)
                    .setTargetId(targetId)
                    .setTargetName(targetName)
                    .setCommentContent(commentContent)
                    .setRating(rating)
                    .setCommentTime(LocalDateTime.now())
                    .setDeleteFlag(false);

            boolean result = this.save(commentRecord);
            if (result) {
                log.debug("添加评论成功: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            }
            return result;
        } catch (Exception e) {
            log.error("添加评论失败: targetType={}, targetId={}", targetType, targetId, e);
            return false;
        }
    }

    /**
     * 更新评论
     */
    public Boolean updateComment(Long commentId, String commentContent, Integer rating) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }

        CommentRecord commentRecord = this.getById(commentId);
        if (commentRecord == null || !commentRecord.getUserId().equals(userId) || commentRecord.getDeleteFlag()) {
            return false;
        }

        commentRecord.setCommentContent(commentContent);
        if (rating != null) {
            commentRecord.setRating(rating);
        }

        boolean result = this.updateById(commentRecord);
        if (result) {
            log.debug("更新评论成功: userId={}, commentId={}", userId, commentId);
        }
        return result;
    }

    /**
     * 删除评论
     */
    public Boolean deleteComment(Long commentId) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }

        CommentRecord commentRecord = this.getById(commentId);
        if (commentRecord == null || !commentRecord.getUserId().equals(userId)) {
            return false;
        }

        commentRecord.setDeleteFlag(true);
        boolean result = this.updateById(commentRecord);
        if (result) {
            log.debug("删除评论成功: userId={}, commentId={}", userId, commentId);
        }
        return result;
    }

    /**
     * 获取用户评论记录列表
     */
    public List<CommentRecordVO> getUserCommentRecords(Integer limit, PageDomain<CommentRecordVO> page) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }

        if (limit == null || limit <= 0) {
            limit = 20;
        }

        LambdaQueryWrapper<CommentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentRecord::getUserId, userId)
                .eq(CommentRecord::getDeleteFlag, false)
                .orderByDesc(CommentRecord::getCommentTime);

        IPage<CommentRecord> pageResult = this.page(new Page<>(page.getPageNum(), page.getPageSize()), wrapper);
        page.setTotal(pageResult.getTotal());
        
        return pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取目标的评论列表
     */
    public List<CommentRecordVO> getTargetComments(String targetType, Long targetId, PageDomain<CommentRecordVO> page) {
        LambdaQueryWrapper<CommentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentRecord::getTargetType, targetType)
                .eq(CommentRecord::getTargetId, targetId)
                .eq(CommentRecord::getDeleteFlag, false)
                .orderByDesc(CommentRecord::getCommentTime);

        IPage<CommentRecord> pageResult = this.page(new Page<>(page.getPageNum(), page.getPageSize()), wrapper);
        page.setTotal(pageResult.getTotal());
        
        return pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 统计用户评论总数
     */
    public Long countUserComments(Long userId) {
        return this.count(new LambdaQueryWrapper<CommentRecord>()
                .eq(CommentRecord::getUserId, userId)
                .eq(CommentRecord::getDeleteFlag, false));
    }

    /**
     * 统计用户今日评论数
     */
    public Long countUserTodayComments(Long userId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        return this.count(new LambdaQueryWrapper<CommentRecord>()
                .eq(CommentRecord::getUserId, userId)
                .eq(CommentRecord::getDeleteFlag, false)
                .between(CommentRecord::getCommentTime, todayStart, todayEnd));
    }

    /**
     * 统计用户本周评论数
     */
    public Long countUserThisWeekComments(Long userId) {
        LocalDateTime weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        
        return this.count(new LambdaQueryWrapper<CommentRecord>()
                .eq(CommentRecord::getUserId, userId)
                .eq(CommentRecord::getDeleteFlag, false)
                .between(CommentRecord::getCommentTime, weekStart, now));
    }

    /**
     * 转换为VO对象
     */
    private CommentRecordVO convertToVO(CommentRecord commentRecord) {
        CommentRecordVO vo = new CommentRecordVO();
        BeanUtils.copyProperties(commentRecord, vo);
        return vo;
    }
} 