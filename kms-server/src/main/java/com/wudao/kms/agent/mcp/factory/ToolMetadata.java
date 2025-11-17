package com.wudao.kms.agent.mcp.factory;

import lombok.Builder;
import lombok.Data;

/**
 * 工具元数据
 * 包含工具的基本信息和类型定义
 * 
 * @param <REQ> 请求类型
 * @param <RESP> 响应类型
 * 
 * @author Claude
 */
@Data
@Builder
public class ToolMetadata<REQ, RESP> {
    
    /**
     * 工具名称，用于标识工具
     */
    private String name;
    
    /**
     * 工具描述，用于AI模型理解工具功能
     */
    private String description;
    
    /**
     * 请求参数类型
     */
    private Class<REQ> requestType;
    
    /**
     * 响应结果类型
     */
    private Class<RESP> responseType;
    
    /**
     * 工具版本号
     */
    @Builder.Default
    private String version = "1.0.0";
    
    /**
     * 工具分类
     */
    private String category;
    
    /**
     * 是否需要上下文信息
     */
    @Builder.Default
    private boolean requiresContext = false;
}