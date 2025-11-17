package com.wudao.kms.agent.service;


import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.domain.AiAssistantMessageTurn;

import java.util.List;

/**
 * 消息轮次服务接口
 * 管理对话轮次，实现ChatGPT风格的版本管理
 */
public interface MessageTurnService {
    
    /**
     * 创建新的对话轮次
     * @param sessionUuid 会话UUID
     * @return 新创建的轮次
     */
    AiAssistantMessageTurn createNewTurn(String sessionUuid, Long userId);
    
    /**
     * 获取或创建指定轮次
     *
     * @param sessionUuid 会话UUID
     * @param turnIndex   轮次序号
     * @param userId
     * @return 轮次对象
     */
    AiAssistantMessageTurn getOrCreateTurn(String sessionUuid, Integer turnIndex, Long userId);
    
    /**
     * 根据轮次ID获取轮次
     * @param turnId 轮次ID
     * @return 轮次对象
     */
    AiAssistantMessageTurn getTurnById(String turnId);
    
    /**
     * 获取会话的所有轮次
     * @param sessionUuid 会话UUID
     * @return 轮次列表
     */
    List<AiAssistantMessageTurn> getTurnsBySession(String sessionUuid);
    
    /**
     * 将消息节点关联到轮次
     * @param turnId 轮次ID
     * @param messageNode 消息节点
     * @param isActive 是否设为该轮次的活跃版本
     */
    void addMessageToTurn(String turnId, AiAssistantMessageNode messageNode, boolean isActive);
    
    /**
     * 切换轮次中的活跃版本
     * @param turnId 轮次ID
     * @param messageType 消息类型 (USER/ASSISTANT)
     * @param nodeId 目标节点ID
     */
    void switchActiveVersion(String turnId, String messageType, String nodeId);
    
    /**
     * 获取轮次中指定类型的所有版本
     * @param turnId 轮次ID
     * @param messageType 消息类型
     * @return 消息节点列表
     */
    List<AiAssistantMessageNode> getVersionsByTurnAndType(String turnId, String messageType);
    
    /**
     * 获取会话的活跃对话路径
     * @param sessionUuid 会话UUID
     * @return 活跃消息节点列表
     */
    List<AiAssistantMessageNode> getActiveConversationPath(String sessionUuid);
    
    /**
     * 获取轮次的活跃消息节点
     * @param turnId 轮次ID
     * @return 活跃消息节点列表（用户消息 + AI回复）
     */
    List<AiAssistantMessageNode> getActiveTurnMessages(String turnId);
    
    /**
     * 更新轮次统计信息
     * @param turnId 轮次ID
     */
    void updateTurnStatistics(String turnId);
    
    /**
     * 清除轮次的活跃AI回复指针
     * @param turnId 轮次ID
     */
    void clearActiveAssistantNode(String turnId);
}