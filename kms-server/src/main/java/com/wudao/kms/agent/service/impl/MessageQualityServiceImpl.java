package com.wudao.kms.agent.service.impl;

import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.service.MessageNodeService;
import com.wudao.kms.agent.service.MessageQualityService;
import com.wudao.kms.agent.service.MessageTurnService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息质量管理服务实现
 */
@Slf4j
@Service
public class MessageQualityServiceImpl implements MessageQualityService {
    
    @Resource
    private MessageNodeService nodeService;
    
    @Resource
    private MessageTurnService turnService;
    
    @Override
    @Transactional
    public boolean markAsBestResponse(String nodeId, boolean clearOthers) {
        try {
            log.info("标记最佳回复: nodeId={}, clearOthers={}", nodeId, clearOthers);
            
            AiAssistantMessageNode node = nodeService.getNodeById(nodeId);
            if (node == null) {
                log.warn("节点不存在: nodeId={}", nodeId);
                return false;
            }
            
            // 只有AI回复才能标记为最佳回复
            if (!"ASSISTANT".equals(node.getMessageType())) {
                log.warn("只有AI回复才能标记为最佳回复: nodeId={}, messageType={}", nodeId, node.getMessageType());
                return false;
            }
            
            // 如果需要清除同轮次其他最佳标记
            if (clearOthers && node.getTurnId() != null) {
                int clearedCount = clearOtherBestMarksInTurn(node.getTurnId(), nodeId);
                log.info("清除了{}个同轮次的最佳标记", clearedCount);
            }
            
            // 标记为最佳回复
            node.markAsBestResponse();
            // 这里需要保存到数据库，假设有一个update方法
            // nodeService.updateNode(node);
            
            log.info("成功标记为最佳回复: nodeId={}", nodeId);
            return true;
            
        } catch (Exception e) {
            log.error("标记最佳回复失败: nodeId={}", nodeId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean markAsErrorResponse(String nodeId) {
        try {
            log.info("标记错误回复: nodeId={}", nodeId);
            
            AiAssistantMessageNode node = nodeService.getNodeById(nodeId);
            if (node == null) {
                log.warn("节点不存在: nodeId={}", nodeId);
                return false;
            }
            
            // 只有AI回复才能标记为错误回复
            if (!"ASSISTANT".equals(node.getMessageType())) {
                log.warn("只有AI回复才能标记为错误回复: nodeId={}, messageType={}", nodeId, node.getMessageType());
                return false;
            }
            
            // 标记为错误回复
            node.markAsErrorResponse();
            // 这里需要保存到数据库
            // nodeService.updateNode(node);
            
            log.info("成功标记为错误回复: nodeId={}", nodeId);
            return true;
            
        } catch (Exception e) {
            log.error("标记错误回复失败: nodeId={}", nodeId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean clearSpecialMarks(String nodeId) {
        try {
            log.info("清除特殊标记: nodeId={}", nodeId);
            
            AiAssistantMessageNode node = nodeService.getNodeById(nodeId);
            if (node == null) {
                log.warn("节点不存在: nodeId={}", nodeId);
                return false;
            }
            
            // 清除特殊标记
            node.clearSpecialMarks();
            // 这里需要保存到数据库
            // nodeService.updateNode(node);
            
            log.info("成功清除特殊标记: nodeId={}", nodeId);
            return true;
            
        } catch (Exception e) {
            log.error("清除特殊标记失败: nodeId={}", nodeId, e);
            return false;
        }
    }
    
    @Override
    public AiAssistantMessageNode getBestResponseInTurn(String turnId) {
        try {
            log.debug("获取轮次最佳回复: turnId={}", turnId);
            
            List<AiAssistantMessageNode> assistantNodes = turnService.getVersionsByTurnAndType(turnId, "ASSISTANT");
            
            return assistantNodes.stream()
                    .filter(AiAssistantMessageNode::isBestResponse)
                    .findFirst()
                    .orElse(null);
                    
        } catch (Exception e) {
            log.error("获取轮次最佳回复失败: turnId={}", turnId, e);
            return null;
        }
    }
    
    @Override
    public List<AiAssistantMessageNode> getErrorResponsesInTurn(String turnId) {
        try {
            log.debug("获取轮次错误回复列表: turnId={}", turnId);
            
            List<AiAssistantMessageNode> assistantNodes = turnService.getVersionsByTurnAndType(turnId, "ASSISTANT");
            
            return assistantNodes.stream()
                    .filter(AiAssistantMessageNode::isErrorResponse)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("获取轮次错误回复列表失败: turnId={}", turnId, e);
            return java.util.Collections.emptyList();
        }
    }
    
    @Override
    @Transactional
    public int clearOtherBestMarksInTurn(String turnId, String excludeNodeId) {
        try {
            log.debug("清除轮次中其他最佳标记: turnId={}, excludeNodeId={}", turnId, excludeNodeId);
            
            List<AiAssistantMessageNode> assistantNodes = turnService.getVersionsByTurnAndType(turnId, "ASSISTANT");
            
            int clearedCount = 0;
            for (AiAssistantMessageNode node : assistantNodes) {
                if (!node.getNodeId().equals(excludeNodeId) && node.isBestResponse()) {
                    node.setIsBestResponse(false);
                    // 这里需要保存到数据库
                    // nodeService.updateNode(node);
                    clearedCount++;
                }
            }
            
            log.debug("清除了{}个最佳标记", clearedCount);
            return clearedCount;
            
        } catch (Exception e) {
            log.error("清除轮次最佳标记失败: turnId={}", turnId, e);
            return 0;
        }
    }
}
