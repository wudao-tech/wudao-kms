package com.wudao.kms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AI助手消息实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "ai_assistant_message", autoResultMap = true)
public class AssistantMessage extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话UUID
     */
    private String sessionUuid;
    
    /**
     * 助手UUID
     */
    private String assistantUuid;
    
    /**
     * 消息类型: USER, ASSISTANT
     */
    private String messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * AI回复内容
     */
    private String aiContent;
    
    /**
     * 轮次索引（用于多轮对话）
     */
    private Integer roundIndex;
    
    /**
     * 消息状态: PENDING, COMPLETED, ERROR
     */
    private String status;
    
    /**
     * 工具调用结果（JSON格式）
     */
    private String toolResults;
    
    /**
     * 相关文件列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> attachments;
    
    /**
     * 错误信息
     */
    private String errorMessage;

    @TableField(exist = false)
    private boolean like;
    @TableField(exist = false)
    private boolean dislike;
} 