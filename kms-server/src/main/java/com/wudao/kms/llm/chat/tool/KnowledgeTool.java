package com.wudao.kms.llm.chat.tool;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.service.AssistantService;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.dto.RagResponse;
import com.wudao.kms.llm.chat.factory.ToolContext;
import com.wudao.kms.service.VectorizationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 知识库工具类
 * 提供知识库搜索功能，用于MCP工具调用
 * 支持从ToolContext获取知识库配置参数
 */
@Slf4j
@Component
public class KnowledgeTool implements BiFunction<KnowledgeTool.Request, ToolContext, KnowledgeTool.Response> {

    /**
     * 知识库搜索请求
     */
    @JsonClassDescription("Knowledge search tool request")
    public record Request(
            @JsonProperty(required = true, value = "query") 
            @JsonPropertyDescription("搜索查询内容") 
            String query
    ) {}

    /**
     * 知识库搜索响应
     */
    public record Response(
            @JsonPropertyDescription("搜索是否成功") 
            boolean success,
            @JsonPropertyDescription("响应消息") 
            String message,
            @JsonPropertyDescription("搜索结果总数") 
            int totalCount,
            @JsonPropertyDescription("搜索结果列表") 
            List<RagResponse> results
    ) {
        /**
         * 创建成功响应
         */
        public static Response success(List<RagResponse> results) {
            return new Response(true, "查询成功", results.size(), results);
        }
        
        /**
         * 创建失败响应
         */
        public static Response error(String message) {
            return new Response(false, message, 0, List.of());
        }
    }

    @Override
    public Response apply(Request request, ToolContext context) {
        log.info("知识库工具被调用，查询: {}", request.query());

        String agentUuid = context.get("agentUuid");
        AssistantService assistantService = SpringUtil.getBean(AssistantService.class);
        Assistant assistant = assistantService.getAssistantByUuid(agentUuid);
        List<Long> knowledgeIds = Collections.singletonList(assistant.getKnowledgeList());

        // 参数验证
        if (request.query() == null || request.query().trim().isEmpty()) {
            return Response.error("查询内容不能为空");
        }
        
        // 从ToolContext获取知识库ID列表

        if (knowledgeIds == null || knowledgeIds.isEmpty()) {
            return Response.error("知识库ID列表不能为空");
        }
        
        // 从ToolContext获取搜索配置参数
        String knowledgeSearchType = context.getOrDefault("knowledgeSearchType", "semantic");
        Integer maxRecallCount = context.getOrDefault("maxRecallCount", 800);
        Integer maxSegmentCount = context.getOrDefault("maxSegmentCount", 5);
        String rerankModel = context.getOrDefault("rerankModel", null);
        List<Long> knowledgeBaseIds = context.getOrDefault("knowledgeBaseIds", null);
        List<Long> documentIds = context.getOrDefault("documentIds", null);
        
        log.info("搜索配置 - 知识库ID: {}, 搜索类型: {}, 最大召回字数: {}, 最大召回分段: {}, 重排模型: {}", 
                knowledgeIds, knowledgeSearchType, maxRecallCount, maxSegmentCount, rerankModel);
        
        // 使用KnowledgeExecute执行搜索
        KnowledgeTestDTO  knowledgeTestDTO = new KnowledgeTestDTO();
        knowledgeTestDTO.setSearchType(knowledgeSearchType);
        knowledgeTestDTO.setKnowledgeIds(knowledgeIds);
        knowledgeTestDTO.setKnowledgeBaseIds(knowledgeBaseIds);
        knowledgeTestDTO.setDocumentIds(documentIds);
        knowledgeTestDTO.setQuery(request.query());

        knowledgeTestDTO.setIsRerank(assistant.getIsRerank());
        knowledgeTestDTO.setRerankModel(assistant.getRerankModel());
        knowledgeTestDTO.setRerankTopK(assistant.getRerankTopK());
        knowledgeTestDTO.setRerankScore(assistant.getRerankScore());

        knowledgeTestDTO.setRagScore(assistant.getRagScore());
        knowledgeTestDTO.setRagTopK(assistant.getRagTopK());
        VectorizationService vectorizationService = SpringUtil.getBean(VectorizationService.class);
        List<KnowledgeSearchResult> searchResults = vectorizationService.searchPro(knowledgeTestDTO);

        List<RagResponse> responses = searchResults.stream().map(item -> {
            return RagResponse.builder()
                    .chunkId(item.getId())
                    .content(item.getContent())
                    .highlight(item.getHighlight())
                    .answerType(item.getAnswerType()).build();
        }).toList();
        return Response.success(responses);
    }
}