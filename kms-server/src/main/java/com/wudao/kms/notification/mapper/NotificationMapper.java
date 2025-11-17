package com.wudao.kms.notification.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.notification.domain.Notification;
import com.wudao.kms.notification.dto.UnReadCountDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface NotificationMapper extends MPJBaseMapper<Notification> {

    /**
     * 按行为类型分组统计未读通知数量
     *
     * @param userId 用户ID
     * @return 各行为类型的未读数量
     */
    @Select("SELECT behavior, COUNT(*) as unread_count " +
            "FROM notification " +
            "WHERE receiver_id = #{userId} AND read_flag = false " +
            "GROUP BY behavior")
    List<UnReadCountDTO> countUnreadGroupByBehavior(@Param("userId") Long userId);
}
