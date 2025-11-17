package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分组消息响应DTO
 * 按对话轮次组织消息，支持版本化显示
 */
@Data
@Schema(description = "分组消息响应")
public class GroupedMessageResponse {
    
    @Schema(description = "会话UUID")
    private String sessionUuid;
    
    @Schema(description = "对话轮次列表")
    private List<ConversationTurn> turns;
    
    @Schema(description = "当前活跃路径")
    private List<String> activePath;
    
    /**
     * 对话轮次
     */
    @Data
    @Schema(description = "对话轮次")
    public static class ConversationTurn {
        
        @Schema(description = "轮次索引")
        private int turnIndex;
        
        @Schema(description = "用户消息版本列表")
        private List<MessageVersion> userVersions;
        
        @Schema(description = "当前活跃的用户消息版本索引")
        private int activeUserVersionIndex;
        
        @Schema(description = "AI回复版本列表（对应当前活跃用户消息）")
        private List<MessageVersion> assistantVersions;
        
        @Schema(description = "当前活跃的AI回复版本索引")
        private int activeAssistantVersionIndex;
    }
    
    /**
     * 消息版本
     */
    @Data
    @Schema(description = "消息版本")
    public static class MessageVersion {
        
        @Schema(description = "节点ID")
        private String nodeId;
        
        @Schema(description = "版本索引")
        private int versionIndex;
        
        @Schema(description = "消息内容")
        private String content;
        
        @Schema(description = "思考数据")
        private String reasoningData;
        
        @Schema(description = "文本数据")
        private String textData;
        
        @Schema(description = "创建时间")
        private String createTime;
        
        @Schema(description = "是否为当前活跃版本")
        private boolean isActive;
        
        @Schema(description = "Token信息")
        private TokenInfo tokenInfo;
    }
    
    /**
     * Token信息
     */
    @Data
    @Schema(description = "Token信息")
    public static class TokenInfo {
        @Schema(description = "输入Token")
        private Integer inputTokens;
        
        @Schema(description = "输出Token")
        private Integer outputTokens;
        
        @Schema(description = "总Token")
        private Integer totalTokens;
        
        @Schema(description = "处理时间(毫秒)")
        private Long processingTimeMs;
    }
}