package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 轮次内的消息
 * 代表用户消息或AI回复的一个版本
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "轮次内的消息")
public class TurnMessage {
    
    @Schema(description = "节点ID")
    private String nodeId;
    
    @Schema(description = "消息类型: USER-用户消息, ASSISTANT-助手回复")
    private String messageType;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "文本数据（AI回复的最终文本）")
    private String textData;
    
    @Schema(description = "思考数据（AI的思考过程）")
    private String reasoningData;
    
    @Schema(description = "是否为当前活跃版本")
    private Boolean isActive;
    
    @Schema(description = "是否为最佳回复")
    private Boolean isBestResponse;
    
    @Schema(description = "是否为错误回复")
    private Boolean isErrorResponse;
    
    @Schema(description = "在该轮次中的版本序号，从0开始")
    private Integer versionIndex;
    
    @Schema(description = "版本类型: ORIGINAL-原始, REGENERATED-重新生成, EDITED-编辑")
    private String versionType;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "Token使用情况")
    private TokenUsage tokenUsage;
    
    @Schema(description = "文件记录")
    private List<Map<String, Object>> fileRecords;
    
    @Schema(description = "工具调用记录")
    private List<Map<String, Object>> toolCallRecords;
    
    @Schema(description = "模型名称")
    private String modelName;
    
    @Schema(description = "模型参数配置")
    private Map<String, Object> modelParams;
    
    @Schema(description = "处理开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "处理结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "处理耗时(毫秒)")
    private Long processingTimeMs;
    
    @Schema(description = "消息状态: SUCCESS-成功, ERROR-失败, TIMEOUT-超时")
    private String processStatus;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @Schema(description = "扩展信息")
    private Map<String, Object> extendInfo;
    
    @Schema(description = "子节点ID列表（用于用户消息关联对应的AI回复版本）")
    private List<String> childrenNodeIds;
    
    /**
     * 检查是否为用户消息
     */
    public boolean isUserMessage() {
        return "USER".equals(messageType);
    }
    
    /**
     * 检查是否为AI回复
     */
    public boolean isAssistantMessage() {
        return "ASSISTANT".equals(messageType);
    }
    
    /**
     * 检查是否有思考内容
     */
    public boolean hasReasoningData() {
        return reasoningData != null && !reasoningData.trim().isEmpty();
    }
    
    /**
     * 检查是否有文件附件
     */
    public boolean hasFileAttachments() {
        return fileRecords != null && !fileRecords.isEmpty();
    }
    
    /**
     * 检查是否调用了工具
     */
    public boolean hasToolCalls() {
        return toolCallRecords != null && !toolCallRecords.isEmpty();
    }
    
    /**
     * 检查是否为最佳回复
     */
    public boolean isBestResponse() {
        return Boolean.TRUE.equals(this.isBestResponse);
    }
    
    /**
     * 检查是否为错误回复
     */
    public boolean isErrorResponse() {
        return Boolean.TRUE.equals(this.isErrorResponse);
    }
    
    /**
     * 检查是否有特殊标记
     */
    public boolean hasSpecialMark() {
        return isBestResponse() || isErrorResponse();
    }
    
    /**
     * 获取回复质量标识
     */
    public String getQualityMark() {
        if (isBestResponse()) {
            return "BEST";
        } else if (isErrorResponse()) {
            return "ERROR";
        } else {
            return "NORMAL";
        }
    }
}
