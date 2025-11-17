package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 对话元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话元数据")
public class ConversationMetadata {
    
    @Schema(description = "助手UUID")
    private String assistantUuid;
    
    @Schema(description = "助手名称")
    private String assistantName;
    
    @Schema(description = "默认模型")
    private String defaultModelSlug;
    
    @Schema(description = "总消息数")
    private Integer totalMessages;
    
    @Schema(description = "总轮次数")
    private Integer totalTurns;
    
    @Schema(description = "总Token消耗")
    private TokenUsage totalTokenUsage;
    
    @Schema(description = "最后活动时间")
    private LocalDateTime lastActiveTime;
    
    @Schema(description = "会话状态")
    private String conversationStatus;
    
    @Schema(description = "是否归档")
    private Boolean isArchived;
    
    @Schema(description = "是否星标")
    private Boolean isStarred;
    
    @Schema(description = "扩展信息")
    private Map<String, Object> extendInfo;
}
