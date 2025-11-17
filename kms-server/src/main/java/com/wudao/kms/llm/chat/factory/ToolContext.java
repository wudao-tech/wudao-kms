package com.wudao.kms.llm.chat.factory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具执行上下文
 * 用于在工具执行过程中传递上下文信息
 * 
 * @author Claude
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ToolContext {
    
    /**
     * 上下文数据存储
     */
    private Map<String, Object> context = new ConcurrentHashMap<>();
    
    /**
     * 助手UUID
     */
    private String assistantUuid;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 获取上下文值
     * 
     * @param key 键
     * @param <T> 值类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }
    
    /**
     * 设置上下文值
     * 
     * @param key 键
     * @param value 值
     * @return 当前对象
     */
    public ToolContext put(String key, Object value) {
        context.put(key, value);
        return this;
    }
    
    /**
     * 获取上下文值，如果不存在则返回默认值
     * 
     * @param key 键
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        Object value = context.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * 检查是否包含指定键
     * 
     * @param key 键
     * @return 是否包含
     */
    public boolean containsKey(String key) {
        return context.containsKey(key);
    }
    
    /**
     * 移除指定键的值
     * 
     * @param key 键
     * @return 被移除的值
     */
    public Object remove(String key) {
        return context.remove(key);
    }
    
    /**
     * 获取所有上下文参数
     * 
     * @return 上下文参数Map的副本
     */
    public Map<String, Object> getAllParams() {
        return new ConcurrentHashMap<>(context);
    }
}