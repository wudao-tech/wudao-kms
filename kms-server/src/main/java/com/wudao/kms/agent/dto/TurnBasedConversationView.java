package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基于轮次的对话视图响应
 * 为前端提供类似ChatGPT的对话结构，但以Turn为组织单位
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "基于轮次的对话视图")
public class TurnBasedConversationView {
    
    @Schema(description = "会话UUID")
    private String sessionUuid;
    
    @Schema(description = "对话标题")
    private String title;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "对话轮次列表")
    private List<ConversationTurn> turns;
    
    @Schema(description = "对话元数据")
    private ConversationMetadata metadata;
    
    @Schema(description = "当前总轮次数")
    private Integer totalTurns;
    
    @Schema(description = "当前活跃路径")
    private String currentPath;
}
