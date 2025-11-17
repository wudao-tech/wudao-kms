package com.wudao.kms.agent.mcp.factory;

import com.alibaba.fastjson.JSONObject;
import com.wudao.common.exception.ServiceException;
import com.wudao.kms.llm.chat.factory.ToolContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.function.Function;

/**
 * 工具工厂
 * 负责创建ToolCallback实例，封装工具创建的复杂逻辑
 * 
 * @author Claude
 */
@Slf4j
@Component("agentToolFactory")
public class ToolFactory {
    
    @Resource
    private ToolRegistry toolRegistry;
    
    /**
     * 根据工具名称创建ToolCallback，并使用自定义描述和上下文参数
     * 
     * @param toolName 工具名称  
     * @param customDescription 自定义描述，如果为null则使用工具默认描述
     * @param contextParams 上下文参数，会被设置到ToolContext中
     * @return ToolCallback实例，如果工具不存在则返回null
     */
    @SuppressWarnings("unchecked")
    public ToolCallback createToolCallback(String toolName, String customDescription, Map<String, Object> contextParams) {
        if (toolName == null || toolName.trim().isEmpty()) {
            log.warn("工具名称不能为空");
            return null;
        }
        
        ToolStrategy<?, ?> strategy = toolRegistry.getTool(toolName);
        if (strategy == null) {
            log.warn("未找到工具: {}", toolName);
            return null;
        }
        
        try {
            ToolMetadata<?, ?> metadata = strategy.getMetadata();
            String description = customDescription != null ? customDescription : metadata.getDescription();
            
            // 创建适配器函数，将ToolStrategy适配为Function
            Function<Object, Object> adapterFunction = (request) -> {
                try {
                    // 验证请求参数，使用原始泛型类型

                    ToolStrategy<Object, Object> typedStrategy = (ToolStrategy<Object, Object>) strategy;
//                    if (!typedStrategy.validateRequest(request)) {
//                        throw new IllegalArgumentException("请求参数验证失败");
//                    }
                    
                    // 创建工具上下文
                    ToolContext context = new ToolContext();
                    
                    // 如果有上下文参数，设置到ToolContext中
                    if (contextParams != null && !contextParams.isEmpty()) {
                        for (Map.Entry<String, Object> entry : contextParams.entrySet()) {
                            context.put(entry.getKey(), entry.getValue());
                        }
                        log.debug("为工具 {} 设置了 {} 个上下文参数", toolName, contextParams.size());
                    }
                    
                    // 执行工具逻辑
                    return typedStrategy.execute(request, context);
                    
                } catch (Exception e) {
                    log.error("执行工具 {} 时发生错误: {}", toolName, e.getMessage(), e);
                    throw new RuntimeException("工具执行失败: " + e.getMessage(), e);
                }
            };
            
            // 构建FunctionToolCallback
            FunctionToolCallback<Object, Object> toolCallback = FunctionToolCallback
                    .builder(toolName, adapterFunction)
                    .description(description)  
                    .inputType(metadata.getRequestType())
                    .build();
                    
            log.debug("成功创建工具回调: {} - {}", toolName, description);
            return toolCallback;
            
        } catch (Exception e) {
            log.error("创建工具回调失败: {} - {}", toolName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 直接执行工具
     * 
     * @param toolName 工具名称
     * @param inputParam 输入参数(JSON格式)
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public JSONObject executeTool(String toolName, JSONObject inputParam){
        if (toolName == null || toolName.trim().isEmpty()) {
            throw new ServiceException("工具名称不能为空");
        }
        
        ToolStrategy<?, ?> strategy = toolRegistry.getTool(toolName);
        if (strategy == null) {
            throw new ServiceException("没有找到该工具: " + toolName);
        }
        
        try {
            ToolMetadata<?, ?> strategyMetadata = strategy.getMetadata();
            Class<?> requestType = strategyMetadata.getRequestType();
            
            // 将JSONObject转换为对应的请求类型
            Object request;
            if (inputParam == null || inputParam.isEmpty()) {
                // 如果没有输入参数，尝试创建空对象
                request = requestType.getDeclaredConstructor().newInstance();
            } else {
                // 将JSONObject转换为请求类型对象
                request = inputParam.toJavaObject(requestType);
            }
            
            // 创建工具上下文
            ToolContext context = new ToolContext();
            
            // 执行工具逻辑
            ToolStrategy<Object, Object> typedStrategy = (ToolStrategy<Object, Object>) strategy;
            Object result = typedStrategy.execute(request, context);

            // 统一将result转换为JSON对象
            try {
                JSONObject jsonObject = convertToJsonObject(result);
                log.info("工具 {} 执行成功", toolName);
                return jsonObject;
            } catch (Exception e) {
                // 如果JSON转换失败，使用toString作为fallback
                log.warn("工具 {} 结果JSON转换失败，使用toString: {}", toolName, e.getMessage());
                JSONObject fallbackJson = new JSONObject();
                fallbackJson.put("result", result.toString());
                return fallbackJson;
            }
            
        } catch (Exception e) {
            log.error("执行工具 {} 时发生错误: {}", toolName, e.getMessage(), e);
            throw new ServiceException("工具执行失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查工具是否可用
     * 
     * @param toolName 工具名称
     * @return 是否可用
     */
    public boolean isToolAvailable(String toolName) {
        return toolRegistry.containsTool(toolName);
    }
    
    /**
     * 获取工具元数据
     * 
     * @param toolName 工具名称
     * @return 工具元数据，如果工具不存在则返回null
     */
    public ToolMetadata<?, ?> getToolMetadata(String toolName) {
        ToolStrategy<?, ?> strategy = toolRegistry.getTool(toolName);
        return strategy != null ? strategy.getMetadata() : null;
    }
    
    /**
     * 将对象转换为JSON对象，特别处理record类型
     * 
     * @param obj 要转换的对象
     * @return JSONObject对象
     */
    private JSONObject convertToJsonObject(Object obj) {
        if (obj == null) {
            return new JSONObject();
        }
        
        // 检查是否是record类型
        if (obj.getClass().isRecord()) {
            JSONObject recordJson = new JSONObject();
            RecordComponent[] components = obj.getClass().getRecordComponents();
            
            for (RecordComponent component : components) {
                try {
                    Method accessor = component.getAccessor();
                    Object value = accessor.invoke(obj);
                    recordJson.put(component.getName(), value);
                } catch (Exception e) {
                    log.warn("获取record字段 {} 失败: {}", component.getName(), e.getMessage());
                }
            }
            return recordJson;
        } else {
            // 对于非record类型，尝试使用Fastjson转换
            try {
                Object jsonObj = JSONObject.toJSON(obj);
                if (jsonObj instanceof JSONObject) {
                    return (JSONObject) jsonObj;
                } else {
                    // 如果不是JSONObject，包装一下
                    JSONObject wrapper = new JSONObject();
                    wrapper.put("result", jsonObj);
                    return wrapper;
                }
            } catch (Exception e) {
                // 如果转换失败，返回包装的字符串
                JSONObject fallback = new JSONObject();
                fallback.put("result", obj.toString());
                return fallback;
            }
        }
    }
}