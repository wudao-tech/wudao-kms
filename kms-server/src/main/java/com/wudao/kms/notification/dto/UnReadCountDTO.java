package com.wudao.kms.notification.dto;

import lombok.Data;

@Data
public class UnReadCountDTO {
    private String behavior;
    private Long unreadCount;
    private Long favoriteCount = 0L;
    private Long commentCount = 0L;
}
