package com.wudao.kms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AI助手会话实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "ai_assistant_session", autoResultMap = true)
public class AssistantSession extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 助手UUID
     */
    private String assistantUuid;
    
    /**
     * 会话UUID
     */
    private String sessionUuid;
    
    /**
     * 会话名称
     */
    private String sessionName;
    
    /**
     * 会话状态 0-inactive 1-active
     */
    private Integer status;
    
    /**
     * 最后访问的消息ID（用于多轮对话）
     */
    private Long lastMessageId;
    
    /**
     * 当前活跃节点ID（用于树状对话结构）
     */
    private String currentNodeId;
    
    /**
     * 会话配置（JSON格式）
     */
    private String sessionConfig;
    
    /**
     * 删除标识
     */
    private Boolean delFlag;

    /**
     * 来源
     */
    private String sourceType;
}