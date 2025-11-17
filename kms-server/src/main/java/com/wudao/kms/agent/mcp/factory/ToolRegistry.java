package com.wudao.kms.agent.mcp.factory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具注册器
 * 负责管理所有工具的注册、发现和获取
 * 
 * @author Claude
 */
@Slf4j
@Component
public class ToolRegistry implements ApplicationContextAware {
    
    /**
     * 工具存储Map，key为工具名称，value为工具策略实例
     */
    private final Map<String, ToolStrategy<?, ?>> tools = new ConcurrentHashMap<>();
    
    /**
     * Spring应用上下文
     */
    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    /**
     * 初始化方法，自动扫描并注册所有工具策略
     */
    @PostConstruct
    public void initialize() {
        log.info("开始初始化工具注册器...");
        
        // 自动扫描所有ToolStrategy实现类
        Map<String, ToolStrategy> strategyBeans = applicationContext.getBeansOfType(ToolStrategy.class);
        
        for (Map.Entry<String, ToolStrategy> entry : strategyBeans.entrySet()) {
            ToolStrategy<?, ?> strategy = entry.getValue();
            try {
                // 初始化工具
                strategy.initialize();
                
                // 注册工具
                ToolMetadata<?, ?> metadata = strategy.getMetadata();
                String toolName = metadata.getName();
                
                if (tools.containsKey(toolName)) {
                    log.warn("工具名称冲突: {} 已存在，将被覆盖", toolName);
                }
                tools.put(toolName, strategy);
                
            } catch (Exception e) {
                log.error("注册工具失败: {} - {}", entry.getKey(), e.getMessage(), e);
            }
        }
        
        log.info("工具注册器初始化完成，共注册 {} 个工具", tools.size());
    }
    
    /**
     * 手动注册工具策略
     * 
     * @param strategy 工具策略实例
     */
    public void register(ToolStrategy<?, ?> strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("工具策略不能为空");
        }
        
        try {
            strategy.initialize();
            ToolMetadata<?, ?> metadata = strategy.getMetadata();
            String toolName = metadata.getName();
            
            if (toolName == null || toolName.trim().isEmpty()) {
                throw new IllegalArgumentException("工具名称不能为空");
            }
            
            tools.put(toolName, strategy);
            log.info("手动注册工具成功: {} - {}", toolName, metadata.getDescription());
            
        } catch (Exception e) {
            log.error("手动注册工具失败: {}", e.getMessage(), e);
            throw new RuntimeException("注册工具失败", e);
        }
    }
    
    /**
     * 根据工具名称获取工具策略
     * 
     * @param toolName 工具名称
     * @return 工具策略实例，如果不存在则返回null
     */
    public ToolStrategy<?, ?> getTool(String toolName) {
        if (toolName == null || toolName.trim().isEmpty()) {
            return null;
        }
        return tools.get(toolName);
    }
    
    /**
     * 检查工具是否存在
     * 
     * @param toolName 工具名称
     * @return 是否存在
     */
    public boolean containsTool(String toolName) {
        return toolName != null && tools.containsKey(toolName);
    }
    
    /**
     * 获取所有已注册的工具名称
     * 
     * @return 工具名称集合
     */
    public Set<String> getAllToolNames() {
        return tools.keySet();
    }
    
    /**
     * 获取已注册工具的数量
     * 
     * @return 工具数量
     */
    public int getToolCount() {
        return tools.size();
    }
    
    /**
     * 注销指定工具
     * 
     * @param toolName 工具名称
     * @return 被注销的工具策略，如果不存在则返回null
     */
    public ToolStrategy<?, ?> unregister(String toolName) {
        if (toolName == null || toolName.trim().isEmpty()) {
            return null;
        }
        
        ToolStrategy<?, ?> removed = tools.remove(toolName);
        if (removed != null) {
            try {
                removed.destroy();
                log.info("成功注销工具: {}", toolName);
            } catch (Exception e) {
                log.error("注销工具时执行destroy方法失败: {} - {}", toolName, e.getMessage(), e);
            }
        }
        return removed;
    }
    
    /**
     * 清空所有工具
     */
    public void clear() {
        for (Map.Entry<String, ToolStrategy<?, ?>> entry : tools.entrySet()) {
            try {
                entry.getValue().destroy();
            } catch (Exception e) {
                log.error("清空工具时执行destroy方法失败: {} - {}", entry.getKey(), e.getMessage(), e);
            }
        }
        tools.clear();
        log.info("已清空所有工具");
    }
    
    /**
     * 销毁方法，清理所有资源
     */
    @PreDestroy
    public void destroy() {
        log.info("开始销毁工具注册器...");
        clear();
        log.info("工具注册器销毁完成");
    }
}