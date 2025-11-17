package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单个对话轮次
 * 包含用户消息和AI回复的所有版本
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "单个对话轮次")
public class ConversationTurn {
    
    @Schema(description = "轮次ID")
    private String turnId;
    
    @Schema(description = "轮次序号（从0开始）")
    private Integer turnIndex;
    
    @Schema(description = "当前活跃的用户消息")
    private TurnMessage activeUserMessage;
    
    @Schema(description = "当前活跃的AI回复")
    private TurnMessage activeAssistantMessage;
    
    @Schema(description = "用户消息的所有版本")
    private List<TurnMessage> userVersions;
    
    @Schema(description = "AI回复的所有版本")
    private List<TurnMessage> assistantVersions;
    
    @Schema(description = "当前选中的用户消息节点ID")
    private String currentUserNodeId;
    
    @Schema(description = "当前选中的AI回复节点ID")
    private String currentAssistantNodeId;
    
    @Schema(description = "轮次状态")
    private String turnStatus;
    
    @Schema(description = "用户消息版本数量")
    private Integer userVersionsCount;
    
    @Schema(description = "AI回复版本数量")
    private Integer assistantVersionsCount;
    
    /**
     * 检查是否有多个用户消息版本
     */
    public boolean hasMultipleUserVersions() {
        return userVersions != null && userVersions.size() > 1;
    }
    
    /**
     * 检查是否有多个AI回复版本
     */
    public boolean hasMultipleAssistantVersions() {
        return assistantVersions != null && assistantVersions.size() > 1;
    }
    
    /**
     * 检查该轮次是否完整（有用户消息和AI回复）
     */
    public boolean isComplete() {
        return activeUserMessage != null && activeAssistantMessage != null;
    }
}
