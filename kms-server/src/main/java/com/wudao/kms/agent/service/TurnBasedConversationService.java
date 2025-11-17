package com.wudao.kms.agent.service;

import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.domain.AiAssistantMessageTurn;
import com.wudao.kms.agent.domain.AssistantSession;
import com.wudao.kms.agent.dto.*;
import com.wudao.security.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于轮次的对话视图服务
 */
@Slf4j
@Service
public class TurnBasedConversationService {
    
    @Resource
    private MessageTurnService turnService;
    
    @Resource
    private MessageNodeService nodeService;
    
    @Resource
    private AssistantSessionService sessionService;
    
    /**
     * 获取基于轮次的对话视图
     * @param sessionUuid 会话UUID
     * @return 对话视图
     */
    public TurnBasedConversationView getConversationByTurns(String sessionUuid) {
        try {
            AssistantSession session = new AssistantSession();
            session.setSessionUuid(sessionUuid);
            
            // 2. 按轮次查询
            List<AiAssistantMessageTurn> turns = turnService.getTurnsBySession(sessionUuid);

            // 3. 为每个轮次构建版本信息
            List<ConversationTurn> conversationTurns = turns.stream()
                    .sorted(Comparator.comparing(AiAssistantMessageTurn::getTurnIndex))
                    .map(this::buildConversationTurn)
                    .collect(Collectors.toList());
                    
            // 4. 构建元数据
            ConversationMetadata metadata = buildConversationMetadata(session, conversationTurns);
            
            return TurnBasedConversationView.builder()
                    .sessionUuid(sessionUuid)
                    .title(session.getSessionName())
                    .createTime(session.getCreatedAt())
                    .updateTime(session.getUpdatedAt())
                    .turns(conversationTurns)
                    .metadata(metadata)
                    .totalTurns(conversationTurns.size())
                    .currentPath(buildCurrentPath(conversationTurns))
                    .build();
                    
        } catch (Exception e) {
            log.error("获取对话轮次视图失败: sessionUuid={}", sessionUuid, e);
            throw new RuntimeException("获取对话视图失败", e);
        }
    }
    
    /**
     * 构建单个轮次的完整信息
     * @param turn 轮次对象
     * @return 对话轮次
     */
    private ConversationTurn buildConversationTurn(AiAssistantMessageTurn turn) {
        try {
            log.debug("构建轮次信息: turnId={}, turnIndex={}", turn.getTurnId(), turn.getTurnIndex());
            
            // 获取该轮次的所有用户消息版本
            List<AiAssistantMessageNode> userNodes = getNodesByTurnAndType(turn.getTurnId(), "USER");
            log.debug("轮次{}找到{}个用户消息版本", turn.getTurnIndex(), userNodes.size());
            
            // 获取该轮次的所有AI回复版本（包括已删除的，因为要展示完整的版本关系）
            List<AiAssistantMessageNode> allAssistantNodes = getAllAssistantNodesByTurn(turn.getTurnId());
            log.debug("轮次{}找到{}个AI回复版本（包含历史版本）", turn.getTurnIndex(), allAssistantNodes.size());
            
            // 转换为TurnMessage并建立关联关系
            List<TurnMessage> userMessages = convertToTurnMessagesWithChildren(userNodes, allAssistantNodes);
            List<TurnMessage> assistantMessages = convertToTurnMessages(allAssistantNodes);
            
            // 找到活跃版本
            TurnMessage activeUser = findActiveMessage(userMessages);
            TurnMessage activeAssistant = findActiveMessage(assistantMessages);
            
            return ConversationTurn.builder()
                    .turnId(turn.getTurnId())
                    .turnIndex(turn.getTurnIndex())
                    .activeUserMessage(activeUser)
                    .activeAssistantMessage(activeAssistant)
                    .userVersions(userMessages)
                    .assistantVersions(assistantMessages)
                    .currentUserNodeId(turn.getActiveUserNodeId())
                    .currentAssistantNodeId(turn.getActiveAssistantNodeId())
                    .turnStatus(turn.getTurnStatus())
                    .userVersionsCount(userMessages.size())
                    .assistantVersionsCount(assistantMessages.size())
                    .build();
                    
        } catch (Exception e) {
            log.error("构建轮次信息失败: turnId={}", turn.getTurnId(), e);
            // 返回一个基本的轮次信息，避免整个查询失败
            return ConversationTurn.builder()
                    .turnId(turn.getTurnId())
                    .turnIndex(turn.getTurnIndex())
                    .turnStatus("ERROR")
                    .userVersionsCount(0)
                    .assistantVersionsCount(0)
                    .build();
        }
    }
    
    /**
     * 根据轮次ID和消息类型获取节点
     * @param turnId 轮次ID
     * @param messageType 消息类型
     * @return 节点列表
     */
    private List<AiAssistantMessageNode> getNodesByTurnAndType(String turnId, String messageType) {
        try {
            // 使用现有的 MessageTurnService 方法
            return turnService.getVersionsByTurnAndType(turnId, messageType);
        } catch (Exception e) {
            log.error("获取轮次消息失败: turnId={}, messageType={}", turnId, messageType, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取轮次的所有AI回复版本（包括已删除的）
     * @param turnId 轮次ID
     * @return AI回复版本列表
     */
    private List<AiAssistantMessageNode> getAllAssistantNodesByTurn(String turnId) {
        try {
            return nodeService.getAllAssistantNodesByTurn(turnId);
        } catch (Exception e) {
            log.error("获取轮次所有AI回复版本失败: turnId={}", turnId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 将AiAssistantMessageNode转换为TurnMessage
     * @param nodes 节点列表
     * @return TurnMessage列表
     */
    private List<TurnMessage> convertToTurnMessages(List<AiAssistantMessageNode> nodes) {
        return nodes.stream()
                .sorted(Comparator.comparing(AiAssistantMessageNode::getVersionIndex))
                .map(this::convertToTurnMessage)
                .collect(Collectors.toList());
    }
    
    /**
     * 将用户消息转换为TurnMessage并建立与AI回复的关联关系
     * @param userNodes 用户消息节点列表
     * @param allAssistantNodes 所有AI回复节点列表（包括历史版本）
     * @return 带有children_node_ids的用户消息列表
     */
    private List<TurnMessage> convertToTurnMessagesWithChildren(List<AiAssistantMessageNode> userNodes, List<AiAssistantMessageNode> allAssistantNodes) {
        // 按版本序号排序
        Map<String, List<String>> userNodeChildrenMap = new HashMap<>();
        
        // 为每个用户消息建立与AI回复的关联关系
        for (AiAssistantMessageNode userNode : userNodes) {
            List<String> childrenIds = new ArrayList<>();
            
            // 查找所有以该用户消息为父节点的AI回复
            for (AiAssistantMessageNode assistantNode : allAssistantNodes) {
                if (userNode.getNodeId().equals(assistantNode.getParentNodeId())) {
                    childrenIds.add(assistantNode.getNodeId());
                }
            }
            
            userNodeChildrenMap.put(userNode.getNodeId(), childrenIds);
        }
        
        // 转换为TurnMessage并设置children_node_ids
        return userNodes.stream()
                .sorted(Comparator.comparing(AiAssistantMessageNode::getVersionIndex))
                .map(node -> {
                    TurnMessage turnMessage = convertToTurnMessage(node);
                    // 设置子节点ID列表
                    turnMessage.setChildrenNodeIds(userNodeChildrenMap.getOrDefault(node.getNodeId(), new ArrayList<>()));
                    return turnMessage;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 将单个AiAssistantMessageNode转换为TurnMessage
     * @param node 消息节点
     * @return TurnMessage
     */
    private TurnMessage convertToTurnMessage(AiAssistantMessageNode node) {
        // 构建Token使用信息
        TokenUsage tokenUsage = TokenUsage.builder()
                .inputTokens(node.getInputTokens())
                .outputTokens(node.getOutputTokens())
                .totalTokens(node.getTotalTokens())
                .tokenCost(node.getTokenCost())
                .build();
                
        return TurnMessage.builder()
                .nodeId(node.getNodeId())
                .messageType(node.getMessageType())
                .content(node.getContent())
                .textData(node.getTextData())
                .reasoningData(node.getReasoningData())
                .isActive(node.getIsActive())
                .isBestResponse(node.getIsBestResponse())
                .isErrorResponse(node.getIsErrorResponse())
                .versionIndex(node.getVersionIndex())
                .versionType(determineVersionType(node))
                .createTime(node.getCreatedAt())
                .updateTime(node.getUpdatedAt())
                .tokenUsage(tokenUsage)
                .fileRecords(node.getFileRecords())
                .toolCallRecords(node.getToolCallRecords())
                .modelName(node.getModelName())
                .modelParams(node.getModelParams())
                .processingTimeMs(node.getProcessingTimeMs())
                .processStatus(node.getProcessStatus())
                .errorMessage(node.getErrorMessage())
                .extendInfo(node.getExtendInfo())
                .childrenNodeIds(node.getChildrenNodeIds())
                .build();
    }
    
    /**
     * 确定版本类型
     * @param node 消息节点
     * @return 版本类型
     */
    private String determineVersionType(AiAssistantMessageNode node) {
        // 可以根据业务逻辑来判断版本类型
        if (node.getVersionIndex() == 0) {
            return "ORIGINAL";
        }
        // 这里可以根据其他字段来判断是重新生成还是编辑
        // 比如根据 extendInfo 中的标记
        return "REGENERATED";
    }
    
    /**
     * 找到活跃的消息
     * @param messages 消息列表
     * @return 活跃消息
     */
    private TurnMessage findActiveMessage(List<TurnMessage> messages) {
        return messages.stream()
                .filter(msg -> Boolean.TRUE.equals(msg.getIsActive()))
                .findFirst()
                .orElse(messages.isEmpty() ? null : messages.get(0));
    }
    
    /**
     * 构建对话元数据
     * @param session 会话信息
     * @param turns 轮次列表
     * @return 对话元数据
     */
    private ConversationMetadata buildConversationMetadata(AssistantSession session, List<ConversationTurn> turns) {
        // 计算总消息数
        int totalMessages = turns.stream()
                .mapToInt(turn -> turn.getUserVersionsCount() + turn.getAssistantVersionsCount())
                .sum();
                
        // 计算总Token消耗
        TokenUsage totalTokenUsage = calculateTotalTokenUsage(turns);
        
        return ConversationMetadata.builder()
                .assistantUuid(session.getAssistantUuid())
                .assistantName(null) // AssistantSession中没有assistantName字段，需要单独查询
                .totalMessages(totalMessages)
                .totalTurns(turns.size())
                .totalTokenUsage(totalTokenUsage)
                .lastActiveTime(session.getUpdatedAt())
                .conversationStatus(session.getStatus() != null ? (session.getStatus() == 1 ? "ACTIVE" : "INACTIVE") : "UNKNOWN")
                .isArchived(false) // 可以根据实际情况设置
                .isStarred(false)  // 可以根据实际情况设置
                .build();
    }
    
    /**
     * 计算总Token消耗
     * @param turns 轮次列表
     * @return 总Token使用情况
     */
    private TokenUsage calculateTotalTokenUsage(List<ConversationTurn> turns) {
        int totalInput = 0;
        int totalOutput = 0;
        
        for (ConversationTurn turn : turns) {
            // 只计算活跃版本的Token消耗
            if (turn.getActiveUserMessage() != null && turn.getActiveUserMessage().getTokenUsage() != null) {
                TokenUsage userTokens = turn.getActiveUserMessage().getTokenUsage();
                totalInput += userTokens.getInputTokens() != null ? userTokens.getInputTokens() : 0;
                totalOutput += userTokens.getOutputTokens() != null ? userTokens.getOutputTokens() : 0;
            }
            
            if (turn.getActiveAssistantMessage() != null && turn.getActiveAssistantMessage().getTokenUsage() != null) {
                TokenUsage assistantTokens = turn.getActiveAssistantMessage().getTokenUsage();
                totalInput += assistantTokens.getInputTokens() != null ? assistantTokens.getInputTokens() : 0;
                totalOutput += assistantTokens.getOutputTokens() != null ? assistantTokens.getOutputTokens() : 0;
            }
        }
        
        return TokenUsage.builder()
                .inputTokens(totalInput)
                .outputTokens(totalOutput)
                .totalTokens(totalInput + totalOutput)
                .build();
    }
    
    /**
     * 构建当前路径
     * @param turns 轮次列表
     * @return 当前路径字符串
     */
    private String buildCurrentPath(List<ConversationTurn> turns) {
        return turns.stream()
                .filter(ConversationTurn::isComplete)
                .map(turn -> turn.getTurnIndex().toString())
                .collect(Collectors.joining(" -> ", "Turn: ", ""));
    }
    
    /**
     * 切换轮次中的活跃版本
     * @param sessionUuid 会话UUID
     * @param turnId 轮次ID
     * @param messageType 消息类型
     * @param nodeId 目标节点ID
     */
    public void switchActiveVersion(String sessionUuid, String turnId, String messageType, String nodeId) {
        try {
            log.info("切换活跃版本: sessionUuid={}, turnId={}, messageType={}, nodeId={}", 
                    sessionUuid, turnId, messageType, nodeId);
                    
            turnService.switchActiveVersion(turnId, messageType, nodeId);
            
            // 更新会话的当前节点
            sessionService.updateCurrentNode(sessionUuid, nodeId, SecurityUtils.getUserId());
            
            log.info("活跃版本切换成功");
            
        } catch (Exception e) {
            log.error("切换活跃版本失败", e);
            throw new RuntimeException("切换版本失败", e);
        }
    }
}
