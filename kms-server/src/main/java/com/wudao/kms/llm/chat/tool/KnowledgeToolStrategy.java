package com.wudao.kms.llm.chat.tool;

import com.wudao.kms.llm.chat.factory.ToolContext;
import com.wudao.kms.llm.chat.factory.ToolMetadata;
import com.wudao.kms.llm.chat.factory.ToolStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 知识库工具策略实现
 * 将KnowledgeTool适配为ToolStrategy接口
 * 支持从ToolContext获取知识库配置参数
 * 
 * @author Claude
 */
@Slf4j
@Component
public class KnowledgeToolStrategy implements ToolStrategy<KnowledgeTool.Request, KnowledgeTool.Response> {

    /**
     * 内部使用的KnowledgeTool实例
     */
    private final KnowledgeTool knowledgeTool = new KnowledgeTool();
    
    @Override
    public ToolMetadata<KnowledgeTool.Request, KnowledgeTool.Response> getMetadata() {
        return ToolMetadata.<KnowledgeTool.Request, KnowledgeTool.Response>builder()
                .name("knowledge_search_tool")
                .description("知识库搜索工具，可以从指定的知识库中搜索相关信息，支持语义检索、全文检索和混合检索")
                .requestType(KnowledgeTool.Request.class)
                .responseType(KnowledgeTool.Response.class)
                .category("knowledge")
                .requiresContext(true) // 需要ToolContext来获取知识库配置
                .version("1.0.0")
                .build();
    }
    
    @Override
    public KnowledgeTool.Response execute(KnowledgeTool.Request request, ToolContext context) {
        try {
            log.debug("执行知识库搜索工具: query={}", request.query());
            
            // 验证ToolContext中是否包含必要的知识库配置
            if (!validateContext(context)) {
                return KnowledgeTool.Response.error("ToolContext中缺少必要的知识库配置信息");
            }
            
            // 执行原有工具逻辑
            KnowledgeTool.Response response = knowledgeTool.apply(request, context);
            
            log.debug("知识库搜索工具执行完成: success={}, totalCount={}", 
                    response.success(), response.totalCount());
            return response;
            
        } catch (Exception e) {
            log.error("执行知识库搜索工具失败: {}", e.getMessage(), e);
            return KnowledgeTool.Response.error("知识库搜索失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validateRequest(KnowledgeTool.Request request) {
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
    
    /**
     * 验证ToolContext中是否包含必要的知识库配置
     * 
     * @param context 工具执行上下文
     * @return 验证结果
     */
    private boolean validateContext(ToolContext context) {
        if (context == null) {
            log.warn("ToolContext不能为空");
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