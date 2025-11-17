package com.wudao.kms.agent.dto;

import lombok.Data;

import java.util.Map;

/**
 * 工作流恢复请求DTO
 */
@Data
public class WorkflowResumeRequest {
    
    /**
     * 用户输入
     */
    private String userInput;
    
    /**
     * 手机号（用于机票服务等场景）
     */
    private String phoneNumber;
    
    /**
     * 业务信息
     */
    private Map<String, Object> businessInfo;
    
    /**
     * 反馈消息
     */
    private String feedbackMessage;
    
    /**
     * 扩展参数
     */
    private Map<String, Object> additionalParams;
} 