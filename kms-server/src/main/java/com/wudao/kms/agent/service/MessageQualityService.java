package com.wudao.kms.agent.service;

import com.wudao.kms.agent.domain.AiAssistantMessageNode;

/**
 * 消息质量管理服务接口
 */
public interface MessageQualityService {
    
    /**
     * 标记消息为最佳回复
     * @param nodeId 节点ID
     * @param clearOthers 是否清除同轮次其他消息的最佳标记
     * @return 是否成功
     */
    boolean markAsBestResponse(String nodeId, boolean clearOthers);
    
    /**
     * 标记消息为错误回复
     * @param nodeId 节点ID
     * @return 是否成功
     */
    boolean markAsErrorResponse(String nodeId);
    
    /**
     * 清除消息的特殊标记
     * @param nodeId 节点ID
     * @return 是否成功
     */
    boolean clearSpecialMarks(String nodeId);
    
    /**
     * 获取轮次中的最佳回复
     * @param turnId 轮次ID
     * @return 最佳回复节点，如果没有则返回null
     */
    AiAssistantMessageNode getBestResponseInTurn(String turnId);
    
    /**
     * 获取轮次中的错误回复列表
     * @param turnId 轮次ID
     * @return 错误回复节点列表
     */
    java.util.List<AiAssistantMessageNode> getErrorResponsesInTurn(String turnId);
    
    /**
     * 批量清除轮次中其他消息的最佳标记
     * @param turnId 轮次ID
     * @param excludeNodeId 要排除的节点ID
     * @return 清除的消息数量
     */
    int clearOtherBestMarksInTurn(String turnId, String excludeNodeId);
}
