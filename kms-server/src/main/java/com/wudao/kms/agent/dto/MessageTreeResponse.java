package com.wudao.kms.agent.dto;

import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 消息树响应DTO
 * 返回树状结构的消息数据
 */
@Data
@Schema(description = "消息树响应")
public class MessageTreeResponse {
    
    @Schema(description = "会话UUID")
    private String sessionUuid;
    
    @Schema(description = "当前活跃节点ID")
    private String currentNodeId;
    
    @Schema(description = "消息树的根节点")
    private MessageNodeDto rootNode;
    
    @Schema(description = "对话路径（从根节点到当前节点）")
    private List<MessageNodeDto> conversationPath;
    
    @Schema(description = "扩展信息")
    private Map<String, Object> extendInfo;
    
    /**
     * 消息节点DTO
     */
    @Data
    @Schema(description = "消息节点")
    public static class MessageNodeDto {
        
        @Schema(description = "节点ID")
        private String nodeId;
        
        @Schema(description = "父节点ID")
        private String parentNodeId;
        
        @Schema(description = "子节点列表")
        private List<MessageNodeDto> children;
        
        @Schema(description = "消息类型")
        private String messageType;
        
        @Schema(description = "消息内容")
        private String content;
        
        @Schema(description = "思考数据")
        private String reasoningData;
        
        @Schema(description = "文本数据")
        private String textData;
        
        @Schema(description = "输入Token数量")
        private Integer inputTokens;
        
        @Schema(description = "输出Token数量")
        private Integer outputTokens;
        
        @Schema(description = "模型名称")
        private String modelName;
        
        @Schema(description = "处理耗时(毫秒)")
        private Long processingTimeMs;
        
        @Schema(description = "创建时间")
        private String createTime;
        
        @Schema(description = "是否为叶子节点")
        private Boolean isLeaf;
        
        @Schema(description = "是否为当前活跃路径上的节点")
        private Boolean isInActivePath;
        
        /**
         * 从消息节点转换为DTO
         */
        public static MessageNodeDto fromNode(AiAssistantMessageNode node) {
            if (node == null) {
                return null;
            }
            
            MessageNodeDto dto = new MessageNodeDto();
            dto.setNodeId(node.getNodeId());
            dto.setParentNodeId(node.getParentNodeId());
            dto.setMessageType(node.getMessageType());
            dto.setContent(node.getContent());
            dto.setReasoningData(node.getReasoningData());
            dto.setTextData(node.getTextData());
            dto.setInputTokens(node.getInputTokens());
            dto.setOutputTokens(node.getOutputTokens());
            dto.setModelName(node.getModelName());
            dto.setProcessingTimeMs(node.getProcessingTimeMs());
            dto.setCreateTime(node.getCreatedAt() != null ? node.getCreatedAt().toString() : null);
            dto.setIsLeaf(node.isLeaf());
            
            return dto;
        }
    }
}