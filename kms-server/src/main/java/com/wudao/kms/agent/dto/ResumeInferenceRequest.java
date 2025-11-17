package com.wudao.kms.agent.dto;

import lombok.Data;

/**
 * 恢复推理流程请求DTO
 */
@Data
public class ResumeInferenceRequest {
    
    /**
     * 模型UUID（首次传输时必填，后续可选）
     */
    private String modelUuid;
    
    /**
     * 图片地址（本地路径或远程URL）
     */
    private String imageUrl;
    
    /**
     * 其他扩展参数
     */
    private java.util.Map<String, Object> additionalParams;
}