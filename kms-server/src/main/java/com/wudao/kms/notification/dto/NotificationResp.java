package com.wudao.kms.notification.dto;

import com.wudao.kms.notification.domain.Notification;
import lombok.Data;

@Data
public class NotificationResp extends Notification {
    private String creatorName;
    private String creatorCover;
    private String targetCover;
    private String targetName;
}
