package com.wudao.kms.llm.chat.tool;

import com.wudao.kms.llm.chat.factory.ToolContext;
import com.wudao.kms.llm.chat.factory.ToolMetadata;
import com.wudao.kms.llm.chat.factory.ToolStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 知识库工具策略实现
 * 将WorkflowTool适配为ToolStrategy接口
 * 支持从ToolContext获取知识库配置参数
 *
 * @author Claude
 */
@Slf4j
@Component
public class WorkflowToolStrategy implements ToolStrategy<WorkflowTool.Request, WorkflowTool.Response> {

    /**
     * 内部使用的WorkflowTool实例（通过Spring注入）
     */
    private final WorkflowTool workflowTool;

    @Autowired
    public WorkflowToolStrategy(WorkflowTool workflowTool) {
        this.workflowTool = workflowTool;
    }
    
    @Override
    public ToolMetadata<WorkflowTool.Request, WorkflowTool.Response> getMetadata() {
        return ToolMetadata.<WorkflowTool.Request, WorkflowTool.Response>builder()
                .name("workflow_tool")
                .description("知识库搜索工具，可以从指定的知识库中搜索相关信息，支持语义检索、全文检索和混合检索")
                .requestType(WorkflowTool.Request.class)
                .responseType(WorkflowTool.Response.class)
                .category("knowledge")
                .requiresContext(true) // 需要ToolContext来获取知识库配置
                .version("1.0.0")
                .build();
    }
    
    @Override
    public WorkflowTool.Response execute(WorkflowTool.Request request, ToolContext context) {
        try {
            log.debug("执行知识库搜索工具: query={}", request.query());

            return workflowTool.apply(request, context);
            
        } catch (Exception e) {
            log.error("执行知识库搜索工具失败: {}", e.getMessage(), e);
            return WorkflowTool.Response.error("知识库搜索失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validateRequest(WorkflowTool.Request request) {
        if (request == null) {
            log.warn("知识库搜索工具请求参数不能为空");
            return false;
        }
        
        if (request.query() == null || request.query().trim().isEmpty()) {
            log.warn("知识库搜索工具查询内容不能为空");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void initialize() {
        log.info("知识库搜索工具策略初始化完成");
    }
    
    @Override
    public void destroy() {
        log.info("知识库搜索工具策略销毁完成");
    }
}