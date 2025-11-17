package com.wudao.kms.notification.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("notification")
@Schema(description = "通知模块")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "通知类型 系统/用户")
    private String type;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "发送人id，如果为系统为空")
    private Long senderId;

    @Schema(description = "接受人id")
    private Long receiverId;

    @Schema(description = "行为 LIKE、COMMENT、FAVORITE")
    private String behavior;

    @Schema(description = "DOCUMENT")
    private String jumpType;

    @Schema(description = "跳转URL")
    private String jumpUrl;

    @Schema(description = "链接ID,前端跳转使用")
    private String targetId;

    @Schema(description = "已读")
    private Boolean readFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
}
