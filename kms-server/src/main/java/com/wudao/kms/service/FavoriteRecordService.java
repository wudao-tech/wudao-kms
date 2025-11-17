package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.mapper.AssistantMapper;
import com.wudao.kms.entity.*;
import com.wudao.kms.mapper.FavoriteRecordMapper;
import com.wudao.kms.mapper.KnowledgeFileMapper;
import com.wudao.kms.notification.domain.Notification;
import com.wudao.kms.notification.service.NotificationService;
import com.wudao.kms.vo.FavoriteRecordVO;
import com.wudao.security.utils.SecurityUtils;
import com.wudao.system.mapper.SysUserMapper;
import com.wudao.system.service.ISysNoticeService;
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
 * 收藏记录Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteRecordService extends MPJBaseServiceImpl<FavoriteRecordMapper, FavoriteRecord> {

    private final KnowledgeFileMapper knowledgeFileMapper;
    private final AssistantMapper assistantMapper;
    private final SysUserMapper sysUserMapper;
    private final NotificationService notificationService;

    /**
     * 添加收藏
     */
    public Boolean addFavorite(String targetType, String targetId, String targetName, String targetUrl) {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                log.warn("用户未登录，无法添加收藏");
                return false;
            }

            // 检查是否已经收藏
            if (isFavorite(userId, targetType, targetId)) {
                log.warn("用户已收藏该内容: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
                return false;
            }

            FavoriteRecord favoriteRecord = new FavoriteRecord()
                    .setUserId(userId)
                    .setTargetType(targetType)
                    .setTargetId(targetId)
                    .setTargetName(targetName)
                    .setTargetUrl(targetUrl)
                    .setFavoriteTime(LocalDateTime.now())
                    .setDeleteFlag(false);

            boolean result = this.save(favoriteRecord);
            if (result) {
                log.debug("添加收藏成功: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            }
            updateTargetCollect(targetType, targetId, "add");
            Long createBy = 0L;
            String name = "";
            if ("knowledge_file".equals(targetType)) {
                KnowledgeFile knowledgeFile = knowledgeFileMapper.selectById(targetId);
                createBy = knowledgeFile.getCreatedBy();
                name = knowledgeFile.getFileName();
            } else if ("assistant".equals(targetType)) {
                LambdaQueryWrapper<Assistant> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Assistant::getUuid, targetId);
                Assistant assistant = assistantMapper.selectOne(wrapper);
                createBy = assistant.getCreatedBy();
                name = assistant.getName();
            }
            // 增加通知
            Notification notification = Notification.builder()
                    .behavior("FAVORITE")
                    .type("USER")
                    .jumpType(targetType.toLowerCase())
                    .title(String.format("您的文章【%s】被收藏", name))
                    .senderId(SecurityUtils.getUserId())
                    .receiverId(createBy)
                    .createTime(LocalDateTime.now())
                    .targetId(targetId)
                    .build();
            notificationService.save(notification);
            return result;
        } catch (Exception e) {
            log.error("添加收藏失败: targetType={}, targetId={}", targetType, targetId, e);
            return false;
        }
    }

    /**
     * 更新目标收藏数据
     *
     * @param targetType 类型
     * @param targetId   值
     */
    private void updateTargetCollect(String targetType, String targetId, String operType) {
        switch (targetType) {
            case "assistant":
                LambdaQueryWrapper<Assistant> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Assistant::getUuid, targetId);
                Assistant assistant = assistantMapper.selectOne(wrapper);
                if (operType.equals("add")) {
                    assistant.setCollect(assistant.getCollect() + 1);
                    assistantMapper.updateById(assistant);
                } else if (operType.equals("delete")) {
                    assistant.setCollect(assistant.getCollect() - 1);
                    assistantMapper.updateById(assistant);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 取消收藏
     */
    public Boolean removeFavorite(String targetType, String targetId) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }

        FavoriteRecord favoriteRecord = this.getOne(new LambdaQueryWrapper<FavoriteRecord>()
                .eq(FavoriteRecord::getUserId, userId)
                .eq(FavoriteRecord::getTargetType, targetType)
                .eq(FavoriteRecord::getTargetId, targetId)
                .eq(FavoriteRecord::getDeleteFlag, false));

        if (favoriteRecord != null) {
            favoriteRecord.setDeleteFlag(true);
            boolean result = this.removeById(favoriteRecord);
            if (result) {
                log.debug("取消收藏成功: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            }
            updateTargetCollect(targetType, targetId, "delete");
            return result;
        }

        return false;
    }

    /**
     * 检查是否已收藏
     */
    public Boolean isFavorite(Long userId, String targetType, String targetId) {
        return this.count(new LambdaQueryWrapper<FavoriteRecord>()
                .eq(FavoriteRecord::getUserId, userId)
                .eq(FavoriteRecord::getTargetType, targetType)
                .eq(FavoriteRecord::getTargetId, targetId)
                .eq(FavoriteRecord::getDeleteFlag, false)) > 0;
    }

    public void deleteFavorite(String targetType, String targetId) {
        LambdaQueryWrapper<FavoriteRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteRecord::getTargetId, targetId);
        wrapper.eq(FavoriteRecord::getTargetType, targetType);
        this.remove(wrapper);
    }

    /**
     * 获取用户收藏记录列表
     */
    public List<FavoriteRecordVO> getUserFavoriteRecords(Integer limit, PageDomain<FavoriteRecordVO> page) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }

        // 使用MPJ直接关联收藏表和文件表/助手表，一次查询获取所有数据
        Page<FavoriteRecordVO> page1 = new Page<>(page.getCurrent(), page.getSize());
        MPJLambdaWrapper<FavoriteRecord> wrapper = new MPJLambdaWrapper<>();

        // 选择收藏记录的字段
        wrapper.selectAll(FavoriteRecord.class)
                // 使用COALESCE合并文件和助手的字段，优先使用非空值
                .selectAs("COALESCE(t2.file_name, t5.name)", FavoriteRecordVO::getTargetName)
                .selectAs("COALESCE(t2.created_by, t5.created_by)", FavoriteRecordVO::getCreatedBy)
                .selectAs("COALESCE(t2.created_at, t5.created_at)", FavoriteRecordVO::getCreatedAt)
                // 选择知识库和知识空间字段（仅knowledge_file类型有值）
                .selectAs("t3.id", FavoriteRecordVO::getKnowledgeBaseId)
                .selectAs("t3.name", FavoriteRecordVO::getKnowledgeBaseName)
                .selectAs("t4.id", FavoriteRecordVO::getKnowledgeSpaceId)
                .selectAs("t4.name", FavoriteRecordVO::getKnowledgeSpaceName);

        // 关联文件表（使用PostgreSQL的::varchar进行类型转换）
        wrapper.leftJoin("knowledge_file t2 on t2.id::varchar = t.target_id and t.target_type = 'knowledge_file' and t2.delete_flag = false")
                .leftJoin("knowledge_base t3 on t3.id = t2.knowledge_base_id and t3.delete_flag = false")
                .leftJoin("knowledge_space t4 on t4.id = t2.space_id and t4.delete_flag = false")
                // 关联助手表
                .leftJoin("ai_assistant t5 on t5.uuid = t.target_id and t.target_type = 'assistant'");

        // 查询条件
        wrapper.eq(FavoriteRecord::getCreatedBy, userId)
                .orderByDesc(FavoriteRecord::getFavoriteTime);

        Page<FavoriteRecordVO> resultPage = this.baseMapper.selectJoinPage(page1, FavoriteRecordVO.class, wrapper);

        List<Long> userIds = resultPage.getRecords().stream().map(item -> Long.parseLong(item.getCreatedBy())).toList();
        List<SysUser> sysUsers = sysUserMapper.selectByIds(userIds);
        Map<Long, SysUser> userMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getId, item -> item));

        resultPage.getRecords().forEach(item -> {
            if (userMap.containsKey(Long.parseLong(item.getCreatedBy()))) {
                item.setCreatorName(userMap.get(Long.parseLong(item.getCreatedBy())).getNickname());
            }
        });
        // 直接返回查询结果
        return resultPage.getRecords();
    }

    /**
     * 统计用户收藏总数
     */
    public Long countUserFavorites(Long userId) {
        return this.count(new LambdaQueryWrapper<FavoriteRecord>()
                .eq(FavoriteRecord::getUserId, userId)
                .eq(FavoriteRecord::getDeleteFlag, false));
    }

    /**
     * 统计用户今日收藏数
     */
    public Long countUserTodayFavorites(Long userId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        return this.count(new LambdaQueryWrapper<FavoriteRecord>()
                .eq(FavoriteRecord::getUserId, userId)
                .eq(FavoriteRecord::getDeleteFlag, false)
                .between(FavoriteRecord::getFavoriteTime, todayStart, todayEnd));
    }

    /**
     * 统计用户本周收藏数
     */
    public Long countUserThisWeekFavorites(Long userId) {
        LocalDateTime weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        return this.count(new LambdaQueryWrapper<FavoriteRecord>()
                .eq(FavoriteRecord::getUserId, userId)
                .eq(FavoriteRecord::getDeleteFlag, false)
                .between(FavoriteRecord::getFavoriteTime, weekStart, now));
    }

    /**
     * 转换为VO对象
     */
    private FavoriteRecordVO convertToVO(FavoriteRecord favoriteRecord) {
        FavoriteRecordVO vo = new FavoriteRecordVO();
        BeanUtils.copyProperties(favoriteRecord, vo);
        return vo;
    }

    public Long favoriteCountByFileIds() {
        MPJLambdaWrapper<FavoriteRecord> wrapper = new MPJLambdaWrapper<>();
        wrapper.leftJoin("knowledge_file t2 on t2.id::varchar = t.target_id and t2.delete_flag = false")
                .leftJoin("knowledge_base t3 on t3.id = t2.knowledge_base_id and t3.delete_flag = false");
        wrapper.select(FavoriteRecord::getTargetId)
                .eq(FavoriteRecord::getUserId, SecurityUtils.getUserId())
                .eq(FavoriteRecord::getTargetType, "knowledge_file")
                .eq(FavoriteRecord::getDeleteFlag, false);

        return this.count(wrapper);
    }
}