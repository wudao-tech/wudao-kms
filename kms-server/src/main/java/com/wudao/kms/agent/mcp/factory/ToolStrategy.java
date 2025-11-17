package com.wudao.kms.agent.mcp.factory;

import com.wudao.kms.llm.chat.factory.ToolContext;

/**
 * 工具策略接口
 * 定义工具的统一执行接口和元数据获取方法
 * 
 * @param <REQ> 请求参数类型
 * @param <RESP> 响应结果类型
 * 
 * @author Claude
 */
public interface ToolStrategy<REQ, RESP> {
    
    /**
     * 获取工具元数据
     * 包含工具名称、描述、请求响应类型等信息
     * 
     * @return 工具元数据
     */
    ToolMetadata<REQ, RESP> getMetadata();
    
    /**
     * 执行工具逻辑
     * 
     * @param request 请求参数
     * @param context 执行上下文
     * @return 执行结果
     */
    RESP execute(REQ request, ToolContext context);
    
    /**
     * 工具初始化方法
     * 在工具注册时调用，用于执行初始化逻辑
     * 默认为空实现
     */
    default void initialize() {
        // 默认空实现
    }
    
    /**
     * 工具销毁方法
     * 在应用关闭时调用，用于清理资源
     * 默认为空实现
     */
    default void destroy() {
        // 默认空实现
    }
    
    /**
     * 验证请求参数
     * 在执行工具前调用，用于验证参数的合法性
     * 默认返回true（不验证）
     * 
     * @param request 请求参数
     * @return 验证结果
     */
    default boolean validateRequest(REQ request) {
        return true;
    }
}