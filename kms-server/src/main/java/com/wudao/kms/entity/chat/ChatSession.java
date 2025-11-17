package com.wudao.kms.entity.chat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;

import lombok.Data;

/**
 * 聊天会话
 */
@Data
@TableName("chat_session")
public class ChatSession extends BaseEntity{
    private Long id;
    private Long userId;
    private String sessionId;
    private String sessionName;
    private Boolean deleteFlag;
}