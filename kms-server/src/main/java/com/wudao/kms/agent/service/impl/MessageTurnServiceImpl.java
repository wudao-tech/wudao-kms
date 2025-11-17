package com.wudao.kms.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.domain.AiAssistantMessageTurn;
import com.wudao.kms.agent.mapper.AiAssistantMessageNodeMapper;
import com.wudao.kms.agent.mapper.AiAssistantMessageTurnMapper;
import com.wudao.kms.agent.service.MessageTurnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 消息轮次服务实现类
 */
@Service
@Slf4j
public class MessageTurnServiceImpl extends ServiceImpl<AiAssistantMessageTurnMapper, AiAssistantMessageTurn>
        implements MessageTurnService {
    
    @Autowired
    private AiAssistantMessageTurnMapper turnMapper;
    
    @Autowired
    private AiAssistantMessageNodeMapper nodeMapper;
    
    @Override
    @Transactional
    public AiAssistantMessageTurn createNewTurn(String sessionUuid, Long userId) {
        try {
            // 获取下一个轮次序号
            Integer maxTurnIndex = turnMapper.getMaxTurnIndex(sessionUuid);
            Integer nextTurnIndex = maxTurnIndex + 1;
            
            // 创建新轮次
            AiAssistantMessageTurn turn = new AiAssistantMessageTurn();
            turn.setTurnId(UUID.randomUUID().toString());
            turn.setSessionUuid(sessionUuid);
            turn.setTurnIndex(nextTurnIndex);
            turn.setTurnStatus("ACTIVE");
            turn.setUserVersionsCount(0);
            turn.setAssistantVersionsCount(0);
            turn.setDelFlag(false);
            
            // 审计信息
            turn.setCreatedBy(userId);
            turn.setUpdatedBy(userId);
            turn.setCreatedAt(LocalDateTime.now());
            turn.setUpdatedAt(LocalDateTime.now());
            
            turnMapper.insert(turn);
            
            return turn;
            
        } catch (Exception e) {
            log.error("创建新轮次失败", e);
            throw new RuntimeException("创建新轮次失败", e);
        }
    }
    
    @Override
    public AiAssistantMessageTurn getOrCreateTurn(String sessionUuid, Integer turnIndex, Long userId) {
        // 先尝试获取现有轮次
        AiAssistantMessageTurn existingTurn = turnMapper.getTurnByIndex(sessionUuid, turnIndex);
        if (existingTurn != null) {
            return existingTurn;
        }
        
        // 如果不存在，创建新轮次
        return createNewTurn(sessionUuid,userId);
    }
    
    @Override
    public AiAssistantMessageTurn getTurnById(String turnId) {
        return turnMapper.getTurnById(turnId);
    }
    
    @Override
    public List<AiAssistantMessageTurn> getTurnsBySession(String sessionUuid) {
        return turnMapper.getTurnsBySessionUuid(sessionUuid);
    }
    
    @Override
    @Transactional
    public void addMessageToTurn(String turnId, AiAssistantMessageNode messageNode, boolean isActive) {
        try {
            AiAssistantMessageTurn turn = getTurnById(turnId);
            if (turn == null) {
                throw new RuntimeException("轮次不存在: " + turnId);
            }
            
            // 设置消息节点的轮次关联
            messageNode.setTurnId(turnId);
            
            // 计算版本序号
            List<AiAssistantMessageNode> existingVersions = getVersionsByTurnAndType(turnId, messageNode.getMessageType());
            messageNode.setVersionIndex(existingVersions.size());
            
            // 如果是活跃版本，更新轮次的活跃节点指针
            if (isActive) {
                if ("USER".equals(messageNode.getMessageType())) {
                    turnMapper.updateActiveUserNode(turnId, messageNode.getNodeId());
                } else if ("ASSISTANT".equals(messageNode.getMessageType())) {
                    turnMapper.updateActiveAssistantNode(turnId, messageNode.getNodeId());
                }
            }
            
            // 更新统计信息
            updateTurnStatistics(turnId);
            

        } catch (Exception e) {
            log.error("消息节点关联到轮次失败", e);
            throw new RuntimeException("消息节点关联到轮次失败", e);
        }
    }
    
    @Override
    @Transactional
    public void switchActiveVersion(String turnId, String messageType, String nodeId) {
        try {
            log.info("开始切换活跃版本 - turnId: {}, messageType: {}, nodeId: {}", 
                    turnId, messageType, nodeId);
            
            // 1. 更新轮次表中的活跃节点指针
            if ("USER".equals(messageType)) {
                turnMapper.updateActiveUserNode(turnId, nodeId);
            } else if ("ASSISTANT".equals(messageType)) {
                turnMapper.updateActiveAssistantNode(turnId, nodeId);
            } else {
                throw new IllegalArgumentException("不支持的消息类型: " + messageType);
            }
            
            // 2. 更新该轮次下同类型消息的isActive字段
            List<AiAssistantMessageNode> sameTypeNodes = getVersionsByTurnAndType(turnId, messageType);
            for (AiAssistantMessageNode node : sameTypeNodes) {
                boolean shouldBeActive = node.getNodeId().equals(nodeId);
                if (node.getIsActive() != shouldBeActive) {
                    log.debug("更新节点活跃状态: nodeId={}, isActive={}", node.getNodeId(), shouldBeActive);
                    nodeMapper.updateNodeActiveStatus(node.getNodeId(), shouldBeActive);
                }
            }
            
            log.info("切换活跃版本成功 - turnId: {}, messageType: {}, nodeId: {}", 
                    turnId, messageType, nodeId);
            
        } catch (Exception e) {
            log.error("切换活跃版本失败", e);
            throw new RuntimeException("切换活跃版本失败", e);
        }
    }
    
    @Override
    public List<AiAssistantMessageNode> getVersionsByTurnAndType(String turnId, String messageType) {
        QueryWrapper<AiAssistantMessageNode> wrapper = new QueryWrapper<>();
        wrapper.eq("turn_id", turnId)
               .eq("message_type", messageType)
               .eq("del_flag", false)
               .orderByAsc("version_index");
        
        return nodeMapper.selectList(wrapper);
    }
    
    @Override
    public List<AiAssistantMessageNode> getActiveConversationPath(String sessionUuid) {
        List<AiAssistantMessageNode> activePath = new ArrayList<>();
        
        try {
            // 获取所有轮次
            List<AiAssistantMessageTurn> turns = getTurnsBySession(sessionUuid);
            
            for (AiAssistantMessageTurn turn : turns) {
                // 添加活跃的用户消息
                if (turn.getActiveUserNodeId() != null) {
                    AiAssistantMessageNode userNode = nodeMapper.getNodeById(turn.getActiveUserNodeId());
                    if (userNode != null) {
                        activePath.add(userNode);
                    }
                }
                
                // 添加活跃的AI回复
                if (turn.getActiveAssistantNodeId() != null) {
                    AiAssistantMessageNode assistantNode = nodeMapper.getNodeById(turn.getActiveAssistantNodeId());
                    if (assistantNode != null) {
                        activePath.add(assistantNode);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("获取活跃对话路径失败", e);
        }
        
        return activePath;
    }
    
    @Override
    public List<AiAssistantMessageNode> getActiveTurnMessages(String turnId) {
        List<AiAssistantMessageNode> messages = new ArrayList<>();
        
        try {
            AiAssistantMessageTurn turn = getTurnById(turnId);
            if (turn == null) {
                return messages;
            }
            
            // 获取活跃的用户消息
            if (turn.getActiveUserNodeId() != null) {
                AiAssistantMessageNode userNode = nodeMapper.getNodeById(turn.getActiveUserNodeId());
                if (userNode != null) {
                    messages.add(userNode);
                }
            }
            
            // 获取活跃的AI回复
            if (turn.getActiveAssistantNodeId() != null) {
                AiAssistantMessageNode assistantNode = nodeMapper.getNodeById(turn.getActiveAssistantNodeId());
                if (assistantNode != null) {
                    messages.add(assistantNode);
                }
            }
            
        } catch (Exception e) {
            log.error("获取轮次活跃消息失败", e);
        }
        
        return messages;
    }
    
    @Override
    @Transactional
    public void updateTurnStatistics(String turnId) {
        try {
            // 统计用户消息版本数
            List<AiAssistantMessageNode> userVersions = getVersionsByTurnAndType(turnId, "USER");
            turnMapper.updateUserVersionsCount(turnId, userVersions.size());
            
            // 统计AI回复版本数
            List<AiAssistantMessageNode> assistantVersions = getVersionsByTurnAndType(turnId, "ASSISTANT");
            turnMapper.updateAssistantVersionsCount(turnId, assistantVersions.size());
            
            log.debug("更新轮次统计信息成功 - turnId: {}, userVersions: {}, assistantVersions: {}", 
                    turnId, userVersions.size(), assistantVersions.size());
            
        } catch (Exception e) {
            log.error("更新轮次统计信息失败", e);
        }
    }
    
    @Override
    @Transactional
    public void clearActiveAssistantNode(String turnId) {
        try {
            log.info("清除轮次的活跃AI回复指针: turnId={}", turnId);
            
            // 清除活跃AI回复指针
            turnMapper.updateActiveAssistantNode(turnId, null);
            
            // 重新计算该轮次的统计信息
            updateTurnStatistics(turnId);
            
            log.info("成功清除轮次的活跃AI回复指针: turnId={}", turnId);
            
        } catch (Exception e) {
            log.error("清除轮次活跃AI回复指针失败: turnId={}", turnId, e);
            throw new RuntimeException("清除轮次活跃AI回复指针失败", e);
        }
    }
}