package com.wudao.kms.entity.chat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;

import lombok.Data;

/**
 * 聊天会话问答
 */
@Data
@TableName("chat_session_qa")
public class ChatSessionQa extends BaseEntity{
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 会话ID
     */
    private Long sessionId;
    /**
     * 问题
     */
    private String question;
    /**
     * 答案
     */
    private String answer;
    /**
     * 文件上传记录ID列表，逗号分隔
     */
    private String fileUploadIds;
    /**
     * 删除标记：false-未删除，true-已删除
     */
    private Boolean deleteFlag;
}
