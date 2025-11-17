package com.wudao.kms.agent.dto;

import lombok.Data;

import java.util.Map;

/**
 * 工作流启动请求DTO
 */
@Data
public class WorkflowStartRequest {
    
    /**
     * 线程ID，用于会话管理
     */
    private String threadId;
    
    /**
     * 工作流类型（如：ticket_service, customer_support等）
     */
    private String workflowType;
    
    /**
     * 工作流配置JSON
     */
    private String workflowConfig;
    
    /**
     * 初始参数
     */
    private Map<String, Object> initialParams;
    
    /**
     * 用户输入
     */
    private String userInput;
    
    /**
     * 会话ID
     */
    private String conversationId;
} 