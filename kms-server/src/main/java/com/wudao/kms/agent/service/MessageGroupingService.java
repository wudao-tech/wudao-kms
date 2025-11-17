package com.wudao.kms.agent.service;


import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.dto.GroupedMessageResponse;

import java.util.List;

/**
 * 消息分组服务
 * 将树状结构的消息按对话轮次分组，便于前端展示
 */
public interface MessageGroupingService {
    
    /**
     * 将消息树按对话轮次分组
     * @param sessionUuid 会话UUID
     * @return 分组后的消息结构
     */
    GroupedMessageResponse groupMessagesByTurns(String sessionUuid);
    
    /**
     * 将指定路径的消息按轮次分组
     * @param conversationPath 对话路径
     * @return 分组后的消息结构
     */
    GroupedMessageResponse groupPathByTurns(List<AiAssistantMessageNode> conversationPath);
    
    /**
     * 获取指定位置的所有用户消息版本
     * @param sessionUuid 会话UUID
     * @param turnIndex 轮次索引
     * @return 该轮次的所有用户消息版本
     */
    List<AiAssistantMessageNode> getUserMessageVersions(String sessionUuid, int turnIndex);
    
    /**
     * 获取指定用户消息的所有AI回复版本
     * @param userNodeId 用户消息节点ID
     * @return 所有AI回复版本
     */
    List<AiAssistantMessageNode> getAssistantMessageVersions(String userNodeId);
}