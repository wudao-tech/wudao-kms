package com.wudao.kms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI助手消息轮次表 
 * 管理对话轮次，实现ChatGPT风格的版本管理：Session -> Turn -> Message Version
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ai_assistant_message_turn")
@Schema(description = "AI助手消息轮次表")
public class AiAssistantMessageTurn extends BaseEntity {
    
    @TableId(type = IdType.INPUT)
    @Schema(description = "轮次ID (UUID)")
    private String turnId;
    
    @Schema(description = "会话UUID")
    private String sessionUuid;
    
    @Schema(description = "轮次序号（从0开始）")
    private Integer turnIndex;
    
    @Schema(description = "当前活跃的用户消息节点ID")
    private String activeUserNodeId;
    
    @Schema(description = "当前活跃的AI回复节点ID")
    private String activeAssistantNodeId;
    
    @Schema(description = "用户消息版本数量")
    private Integer userVersionsCount = 0;
    
    @Schema(description = "AI回复版本数量")
    private Integer assistantVersionsCount = 0;
    
    @Schema(description = "轮次状态: ACTIVE-活跃, COMPLETED-完成, ARCHIVED-归档")
    private String turnStatus = "ACTIVE";
    
    @Schema(description = "是否删除")
    private Boolean delFlag = false;
    
    /**
     * 检查是否有用户消息版本
     */
    public boolean hasUserVersions() {
        return userVersionsCount != null && userVersionsCount > 0;
    }
    
    /**
     * 检查是否有AI回复版本
     */
    public boolean hasAssistantVersions() {
        return assistantVersionsCount != null && assistantVersionsCount > 0;
    }
    
    /**
     * 检查该轮次是否完成（有用户消息和AI回复）
     */
    public boolean isCompleted() {
        return hasUserVersions() && hasAssistantVersions();
    }
    
    /**
     * 增加用户消息版本计数
     */
    public void incrementUserVersions() {
        this.userVersionsCount = (this.userVersionsCount == null ? 0 : this.userVersionsCount) + 1;
    }
    
    /**
     * 增加AI回复版本计数
     */
    public void incrementAssistantVersions() {
        this.assistantVersionsCount = (this.assistantVersionsCount == null ? 0 : this.assistantVersionsCount) + 1;
    }
}