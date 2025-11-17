package com.wudao.kms.agent.service;

import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.dto.AssistantApiRequest;

import java.util.List;

/**
 * AI助手消息节点服务接口
 */
public interface MessageNodeService {
    
    /**
     * 创建用户消息节点
     * @param request 请求参数
     * @param parentNodeId 父节点ID（如果为null则创建根节点）
     * @return 创建的用户消息节点
     */
    AiAssistantMessageNode createUserMessageNode(AssistantApiRequest request, String parentNodeId);
    
    /**
     * 创建AI助手回复节点
     * @param request 请求参数
     * @param userNodeId 对应的用户消息节点ID
     * @param reasoningData 思考数据
     * @param textData 文本数据
     * @param inputTokens 输入Token
     * @param outputTokens 输出Token
     * @param startTimeMillis 开始时间
     * @param endTimeMillis 结束时间
     * @param processingTimeMs 处理时间
     * @param modelName 模型名称
     * @return 创建的AI回复节点
     */
    AiAssistantMessageNode createAssistantMessageNode(AssistantApiRequest request,
                                                    String userNodeId,
                                                    String reasoningData,
                                                    String textData,
                                                    Integer inputTokens,
                                                    Integer outputTokens,
                                                    Long startTimeMillis,
                                                    Long endTimeMillis,
                                                    Long processingTimeMs,
                                                    String modelName);
    
    /**
     * 获取会话的对话历史（从根节点到当前节点的路径）
     * @param sessionUuid 会话UUID
     * @return 对话历史节点列表
     */
    List<AiAssistantMessageNode> getConversationHistory(String sessionUuid);
    
    /**
     * 根据节点ID获取对话路径
     * @param nodeId 节点ID
     * @return 从根节点到指定节点的路径
     */
    List<AiAssistantMessageNode> getConversationPath(String nodeId);
    
    /**
     * 获取会话的根节点
     * @param sessionUuid 会话UUID
     * @return 根节点
     */
    AiAssistantMessageNode getRootNode(String sessionUuid);
    
    /**
     * 根据节点ID获取节点
     * @param nodeId 节点ID
     * @return 消息节点
     */
    AiAssistantMessageNode getNodeById(String nodeId);
    
    /**
     * 获取节点的所有子节点
     * @param nodeId 节点ID
     * @return 子节点列表
     */
    List<AiAssistantMessageNode> getChildrenNodes(String nodeId);
    
    /**
     * 为重新生成创建新的AI回复节点
     * @param request 请求参数
     * @param originalNodeId 原始节点ID
     * @param reasoningData 新的思考数据
     * @param textData 新的文本数据
     * @param inputTokens 输入Token
     * @param outputTokens 输出Token
     * @param startTimeMillis 开始时间
     * @param endTimeMillis 结束时间
     * @param processingTimeMs 处理时间
     * @param modelName 模型名称
     * @return 新的AI回复节点
     */
    AiAssistantMessageNode createRegeneratedAssistantNode(AssistantApiRequest request,
                                                         String originalNodeId,
                                                         String reasoningData,
                                                         String textData,
                                                         Integer inputTokens,
                                                         Integer outputTokens,
                                                         Long startTimeMillis,
                                                         Long endTimeMillis,
                                                         Long processingTimeMs,
                                                         String modelName);
    
    /**
     * 为编辑消息创建新的用户消息节点
     * @param request 请求参数
     * @param originalNodeId 原始用户消息节点ID
     * @return 新的用户消息节点
     */
    AiAssistantMessageNode createEditedUserMessageNode(AssistantApiRequest request, String originalNodeId);
    
    /**
     * 构建会话的完整消息树结构
     * @param sessionUuid 会话UUID
     * @return 消息树的根节点（包含完整的子节点关系）
     */
    AiAssistantMessageNode buildMessageTree(String sessionUuid);
    
    /**
     * 获取轮次的所有AI回复版本（包括已删除的）
     * @param turnId 轮次ID
     * @return AI回复版本列表
     */
    List<AiAssistantMessageNode> getAllAssistantNodesByTurn(String turnId);
}