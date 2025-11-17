package com.wudao.kms.notification.controller;

import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.notification.domain.Notification;
import com.wudao.kms.notification.dto.NotificationResp;
import com.wudao.kms.notification.dto.UnReadCountDTO;
import com.wudao.kms.notification.service.NotificationService;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通知管理Controller
 */
@Tag(name = "通知管理", description = "通知相关接口")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "查询当前用户的通知列表（分页+条件查询）")
    @GetMapping("/list")
    public PageDomain<NotificationResp> list(
            @ParameterObject Notification notification,
            @ParameterObject PageDomain<NotificationResp> page) {
        Long userId = SecurityUtils.getUserId();
        // 确保只查询当前用户的通知
        notification.setReceiverId(userId);
        return notificationService.queryList(notification, page);
    }

    @Operation(summary = "查询当前用户的未读通知数量")
    @GetMapping("/unread/count")
    public R<UnReadCountDTO> countUnread() {
        Long userId = SecurityUtils.getUserId();
        return R.ok(notificationService.countUnreadByUserId(userId));
    }

    @Operation(summary = "创建通知")
    @PostMapping
    public R<Boolean> create(@RequestBody Notification notification) {
        boolean success = notificationService.createNotification(notification);
        return R.ok(success);
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public R<Boolean> markAsRead(
            @Parameter(description = "通知ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        boolean success = notificationService.markAsRead(id, userId);
        return R.ok(success);
    }

    @Operation(summary = "类型批量已读")
    @PutMapping("/read/{type}")
    public R<Boolean> markTypeAsRead(
            @Parameter(description = "通知ID") @PathVariable String type) {
        boolean success = notificationService.markAsRead(type);
        return R.ok(success);
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(
            @Parameter(description = "通知ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        boolean success = notificationService.deleteNotification(id, userId);
        return R.ok(success);
    }

    @Operation(summary = "清空所有通知")
    @DeleteMapping("/clear")
    public R<Boolean> clearAll() {
        Long userId = SecurityUtils.getUserId();
        boolean success = notificationService.clearAllNotifications(userId);
        return R.ok(success);
    }
}
