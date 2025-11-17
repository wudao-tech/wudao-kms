package com.wudao.kms.agent.service.impl;

import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.domain.AiAssistantMessageTurn;
import com.wudao.kms.agent.dto.GroupedMessageResponse;
import com.wudao.kms.agent.service.MessageGroupingService;
import com.wudao.kms.agent.service.MessageNodeService;
import com.wudao.kms.agent.service.MessageTurnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息分组服务实现类
 * 将树状消息结构转换为版本化的轮次显示
 */
@Service
@Slf4j
public class MessageGroupingServiceImpl implements MessageGroupingService {
    
    @Autowired
    private MessageNodeService messageNodeService;
    
    @Autowired
    private MessageTurnService messageTurnService;

    @Override
    public GroupedMessageResponse groupMessagesByTurns(String sessionUuid) {
        try {
            // 直接使用Turn架构获取分组数据
            GroupedMessageResponse response = new GroupedMessageResponse();
            response.setSessionUuid(sessionUuid);
            
            // 获取所有轮次
            List<AiAssistantMessageTurn> turns = messageTurnService.getTurnsBySession(sessionUuid);
            List<GroupedMessageResponse.ConversationTurn> conversationTurns = new ArrayList<>();
            List<String> activePath = new ArrayList<>();
            
            for (AiAssistantMessageTurn turn : turns) {
                GroupedMessageResponse.ConversationTurn conversationTurn = new GroupedMessageResponse.ConversationTurn();
                conversationTurn.setTurnIndex(turn.getTurnIndex());
                
                // 获取用户消息版本
                List<AiAssistantMessageNode> userVersions = messageTurnService.getVersionsByTurnAndType(turn.getTurnId(), "USER");
                conversationTurn.setUserVersions(convertToMessageVersions(userVersions));
                
                // 找到活跃的用户消息版本索引
                String activeUserNodeId = turn.getActiveUserNodeId();
                int activeUserVersionIndex = findActiveVersionIndex(userVersions, activeUserNodeId);
                conversationTurn.setActiveUserVersionIndex(activeUserVersionIndex);
                
                if (activeUserNodeId != null) {
                    activePath.add(activeUserNodeId);
                }
                
                // 获取AI回复版本
                List<AiAssistantMessageNode> assistantVersions = messageTurnService.getVersionsByTurnAndType(turn.getTurnId(), "ASSISTANT");
                conversationTurn.setAssistantVersions(convertToMessageVersions(assistantVersions));
                
                // 找到活跃的AI回复版本索引
                String activeAssistantNodeId = turn.getActiveAssistantNodeId();
                int activeAssistantVersionIndex = findActiveVersionIndex(assistantVersions, activeAssistantNodeId);
                conversationTurn.setActiveAssistantVersionIndex(activeAssistantVersionIndex);
                
                if (activeAssistantNodeId != null) {
                    activePath.add(activeAssistantNodeId);
                }
                
                conversationTurns.add(conversationTurn);
            }
            
            response.setTurns(conversationTurns);
            response.setActivePath(activePath);
            return response;
            
        } catch (Exception e) {
            log.error("按轮次分组消息失败", e);
            return new GroupedMessageResponse();
        }
    }

    @Override
    public GroupedMessageResponse groupPathByTurns(List<AiAssistantMessageNode> conversationPath) {
        GroupedMessageResponse response = new GroupedMessageResponse();
        List<GroupedMessageResponse.ConversationTurn> turns = new ArrayList<>();
        List<String> activePath = new ArrayList<>();
        
        if (conversationPath == null || conversationPath.isEmpty()) {
            response.setTurns(turns);
            response.setActivePath(activePath);
            return response;
        }
        
        // 设置会话UUID
        response.setSessionUuid(conversationPath.get(0).getSessionUuid());
        
        // 按消息类型分组：USER -> ASSISTANT -> USER -> ASSISTANT...
        int turnIndex = 0;
        for (int i = 0; i < conversationPath.size(); ) {
            if (i < conversationPath.size() && "USER".equals(conversationPath.get(i).getMessageType())) {
                GroupedMessageResponse.ConversationTurn turn = new GroupedMessageResponse.ConversationTurn();
                turn.setTurnIndex(turnIndex);
                
                // 处理用户消息
                AiAssistantMessageNode userMessage = conversationPath.get(i);
                activePath.add(userMessage.getNodeId());
                
                // 获取该位置的所有用户消息版本（通过分析同一父节点下的USER类型消息）
                List<AiAssistantMessageNode> userVersions = getUserMessageVersionsAtPosition(userMessage);
                turn.setUserVersions(convertToMessageVersions(userVersions));
                turn.setActiveUserVersionIndex(findActiveVersionIndex(userVersions, userMessage.getNodeId()));
                
                i++; // 移动到下一个消息
                
                // 处理AI回复
                if (i < conversationPath.size() && "ASSISTANT".equals(conversationPath.get(i).getMessageType())) {
                    AiAssistantMessageNode assistantMessage = conversationPath.get(i);
                    activePath.add(assistantMessage.getNodeId());
                    
                    // 获取该用户消息的所有AI回复版本
                    List<AiAssistantMessageNode> assistantVersions = getAssistantMessageVersions(userMessage.getNodeId());
                    turn.setAssistantVersions(convertToMessageVersions(assistantVersions));
                    turn.setActiveAssistantVersionIndex(findActiveVersionIndex(assistantVersions, assistantMessage.getNodeId()));
                    
                    i++; // 移动到下一个消息
                }
                
                turns.add(turn);
                turnIndex++;
            } else {
                break; // 异常情况，跳出循环
            }
        }
        
        response.setTurns(turns);
        response.setActivePath(activePath);
        return response;
    }

    @Override
    public List<AiAssistantMessageNode> getUserMessageVersions(String sessionUuid, int turnIndex) {
        try {
            // 使用Turn架构直接获取指定轮次的用户消息版本
            List<AiAssistantMessageTurn> turns = messageTurnService.getTurnsBySession(sessionUuid);
            
            for (AiAssistantMessageTurn turn : turns) {
                if (turn.getTurnIndex().equals(turnIndex)) {
                    return messageTurnService.getVersionsByTurnAndType(turn.getTurnId(), "USER");
                }
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取用户消息版本失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<AiAssistantMessageNode> getAssistantMessageVersions(String userNodeId) {
        try {
            return messageNodeService.getChildrenNodes(userNodeId)
                    .stream()
                    .filter(node -> "ASSISTANT".equals(node.getMessageType()))
                    .sorted(Comparator.comparing(AiAssistantMessageNode::getCreatedAt))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取AI回复版本失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取指定位置的所有用户消息版本
     * 这需要分析树结构，找到同一"对话轮次"的所有用户消息
     */
    private List<AiAssistantMessageNode> getUserMessageVersionsAtPosition(AiAssistantMessageNode currentUserMessage) {
        List<AiAssistantMessageNode> versions = new ArrayList<>();
        versions.add(currentUserMessage);
        
        // 如果这是一个编辑后的消息，需要找到原始消息和其他版本
        // 通过分析父节点的所有子节点来找到"兄弟"用户消息
        String parentNodeId = currentUserMessage.getParentNodeId();
        if (parentNodeId != null) {
            try {
                List<AiAssistantMessageNode> siblings = messageNodeService.getChildrenNodes(parentNodeId);
                for (AiAssistantMessageNode sibling : siblings) {
                    if ("USER".equals(sibling.getMessageType()) && 
                        !sibling.getNodeId().equals(currentUserMessage.getNodeId())) {
                        versions.add(sibling);
                    }
                }
            } catch (Exception e) {
                log.warn("获取兄弟用户消息失败", e);
            }
        }
        
        // 按创建时间排序
        versions.sort(Comparator.comparing(AiAssistantMessageNode::getCreatedAt));
        return versions;
    }
    
    /**
     * 将消息节点转换为版本DTO
     */
    private List<GroupedMessageResponse.MessageVersion> convertToMessageVersions(List<AiAssistantMessageNode> nodes) {
        List<GroupedMessageResponse.MessageVersion> versions = new ArrayList<>();
        
        for (int i = 0; i < nodes.size(); i++) {
            AiAssistantMessageNode node = nodes.get(i);
            GroupedMessageResponse.MessageVersion version = new GroupedMessageResponse.MessageVersion();
            
            version.setNodeId(node.getNodeId());
            version.setVersionIndex(i);
            version.setContent(node.getContent());
            version.setReasoningData(node.getReasoningData());
            version.setTextData(node.getTextData());
            version.setCreateTime(node.getCreatedAt() != null ? node.getCreatedAt().toString() : null);
            
            // Token信息
            GroupedMessageResponse.TokenInfo tokenInfo = new GroupedMessageResponse.TokenInfo();
            tokenInfo.setInputTokens(node.getInputTokens());
            tokenInfo.setOutputTokens(node.getOutputTokens());
            tokenInfo.setTotalTokens(node.getTotalTokens());
            tokenInfo.setProcessingTimeMs(node.getProcessingTimeMs());
            version.setTokenInfo(tokenInfo);
            
            versions.add(version);
        }
        
        return versions;
    }
    
    /**
     * 找到活跃版本的索引
     */
    private int findActiveVersionIndex(List<AiAssistantMessageNode> versions, String activeNodeId) {
        for (int i = 0; i < versions.size(); i++) {
            if (versions.get(i).getNodeId().equals(activeNodeId)) {
                return i;
            }
        }
        return 0; // 默认第一个
    }
}