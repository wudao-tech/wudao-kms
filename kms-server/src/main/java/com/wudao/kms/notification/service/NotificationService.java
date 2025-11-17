package com.wudao.kms.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.notification.domain.Notification;
import com.wudao.kms.notification.dto.NotificationResp;
import com.wudao.kms.notification.dto.UnReadCountDTO;
import com.wudao.kms.notification.mapper.NotificationMapper;
import com.wudao.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService extends MPJBaseServiceImpl<NotificationMapper, Notification> {

    /**
     * 分页查询通知列表（支持条件过滤）
     *
     * @param notification 查询条件
     * @param page         分页参数
     * @return 分页结果
     */
    public PageDomain<NotificationResp> queryList(Notification notification, PageDomain<NotificationResp> page) {
        MPJLambdaWrapper<Notification> wrapper = new MPJLambdaWrapper<>();

        // 接收者ID筛选（必填）
        if (notification.getReceiverId() != null) {
            wrapper.eq(Notification::getReceiverId, notification.getReceiverId());
        }

        // 通知类型筛选
        if (StringUtils.isNotBlank(notification.getType())) {
            wrapper.eq(Notification::getType, notification.getType());
        }

        // 行为类型筛选（LIKE、COMMENT、COLLECT等）
        if (StringUtils.isNotBlank(notification.getBehavior())) {
            wrapper.eq(Notification::getBehavior, notification.getBehavior());
        }

        // 已读/未读筛选
        if (notification.getReadFlag() != null) {
            wrapper.eq(Notification::getReadFlag, notification.getReadFlag());
        }

        // 跳转类型筛选（DOCUMENT等）
        if (StringUtils.isNotBlank(notification.getJumpType())) {
            wrapper.eq(Notification::getJumpType, notification.getJumpType());
        }

        // 标题模糊查询
        if (StringUtils.isNotBlank(notification.getTitle())) {
            wrapper.like(Notification::getTitle, notification.getTitle());
        }
        wrapper.selectAll(Notification.class);
        wrapper.selectAs(SysUser::getNickname, NotificationResp::getCreatorName);
        wrapper.selectAs(SysUser::getAvatar, NotificationResp::getCreatorCover);
        wrapper.select("case when t3.id is not null then t3.file_name else t2.name end as targetName");
        wrapper.select("case when t3.id is not null then t3.cover_path else t2.logo end as targetCover");
        // 按创建时间倒序排列
        wrapper.orderByDesc(Notification::getCreateTime);
        wrapper.leftJoin(SysUser.class, SysUser::getId, Notification::getSenderId);
//        wrapper.leftJoin(KnowledgeFile.class, KnowledgeFile::getId, Notification::getTargetId);
        wrapper.leftJoin("knowledge_file t3 on t3.id::varchar = t.target_id");
        wrapper.leftJoin(Assistant.class, Assistant::getUuid, Notification::getTargetId);

        // 执行分页查询
        IPage<NotificationResp> pageResult = this.selectJoinListPage(new Page<>(page.getPageNum(), page.getPageSize()), NotificationResp.class, wrapper);
        page.setRecords(pageResult.getRecords());
        page.setTotal(pageResult.getTotal());
        return page;
    }

    /**
     * 根据用户ID查询通知列表
     *
     * @param userId 用户ID
     * @return 通知列表
     */
    public List<Notification> listByUserId(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, userId)
                .orderByDesc(Notification::getCreateTime);
        return list(wrapper);
    }

    /**
     * 根据用户ID查询未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    public UnReadCountDTO countUnreadByUserId(Long userId) {
        // 使用原生SQL进行GROUP BY COUNT查询
        List<UnReadCountDTO> unReadCountDTOS = baseMapper.countUnreadGroupByBehavior(userId);

        UnReadCountDTO countDTO = new UnReadCountDTO();
        long commentCount = 0;
        long favoriteCount = 0;

        for (UnReadCountDTO item : unReadCountDTOS) {
            if ("comment".equalsIgnoreCase(item.getBehavior())) {
                commentCount = item.getUnreadCount();
            } else if ("favorite".equalsIgnoreCase(item.getBehavior())) {
                favoriteCount = item.getUnreadCount();
            }
        }

        countDTO.setCommentCount(commentCount);
        countDTO.setFavoriteCount(favoriteCount);
        countDTO.setUnreadCount(commentCount + favoriteCount);

        return countDTO;
    }

    /**
     * 创建通知
     *
     * @param notification 通知对象
     * @return 是否创建成功
     */
    public boolean createNotification(Notification notification) {
        if (notification.getCreateTime() == null) {
            notification.setCreateTime(LocalDateTime.now());
        }
        if (notification.getReadFlag() == null) {
            notification.setReadFlag(false);
        }
        return save(notification);
    }

    /**
     * 更新通知（标记为已读）
     *
     * @param id     通知ID
     * @param userId 用户ID
     * @return 是否更新成功
     */
    public boolean markAsRead(Long id, Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, id)
                .eq(Notification::getReceiverId, userId)
                .set(Notification::getReadFlag, true)
                .set(Notification::getReadTime, LocalDateTime.now());
        return update(wrapper);
    }

    /**
     * 根据类型批量已读
     * @param behavior 行为
     * @return
     */
    public boolean markAsRead(String behavior){
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getReceiverId, SecurityUtils.getUserId())
                .eq(Notification::getBehavior, behavior)
                .set(Notification::getReadFlag, true)
                .set(Notification::getReadTime, LocalDateTime.now());
        return update(wrapper);
    }

    /**
     * 删除通知（物理删除）
     *
     * @param id     通知ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public boolean deleteNotification(Long id, Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getId, id)
                .eq(Notification::getReceiverId, userId);
        return remove(wrapper);
    }

    /**
     * 清空用户的所有通知（物理删除）
     *
     * @param userId 用户ID
     * @return 是否清空成功
     */
    public boolean clearAllNotifications(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getReceiverId, userId);
        wrapper.set(Notification::getReadFlag, true);
        wrapper.set(Notification::getReadTime, LocalDateTime.now());
        return update(wrapper);
    }
}
