package com.wudao.kms.agent.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.domain.AiAssistantMessageTurn;
import com.wudao.kms.agent.dto.AssistantApiRequest;
import com.wudao.kms.agent.mapper.AiAssistantMessageNodeMapper;
import com.wudao.kms.agent.service.AssistantSessionService;
import com.wudao.kms.agent.service.MessageNodeService;
import com.wudao.kms.agent.service.MessageTurnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI助手消息节点服务实现类
 */
@Service
@Slf4j
public class MessageNodeServiceImpl extends ServiceImpl<AiAssistantMessageNodeMapper, AiAssistantMessageNode>
        implements MessageNodeService {

    @Autowired
    private AiAssistantMessageNodeMapper messageNodeMapper;

    @Autowired
    private AssistantSessionService assistantSessionService;
    
    @Autowired
    private MessageTurnService messageTurnService;

    @Override
    @Transactional
    public AiAssistantMessageNode createUserMessageNode(AssistantApiRequest request, String parentNodeId) {
        try {
            // 1. 创建新的对话轮次（用户消息标志着新轮次的开始）
            AiAssistantMessageTurn currentTurn = messageTurnService.createNewTurn(request.getSessionUuid(),request.getUserId());
            
            // 2. 创建用户消息节点
            AiAssistantMessageNode node = new AiAssistantMessageNode();

            // 生成唯一节点ID
            node.setNodeId(UUID.randomUUID().toString());
            node.setParentNodeId(parentNodeId);
            node.setSessionUuid(request.getSessionUuid());
            node.setTurnId(currentTurn.getTurnId()); // 关联到轮次
            node.setMessageType("USER");
            node.setVersionIndex(0); // 新创建的用户消息是该轮次的第0个版本
            node.setContent(request.getMessage());
            node.setTextData(request.getMessage());

            // 用户消息没有Token消耗
            node.setInputTokens(0);
            node.setOutputTokens(0);
            node.setTotalTokens(0);
            node.setTokenCost(BigDecimal.ZERO);

            // 时间信息
            LocalDateTime now = LocalDateTime.now();
            node.setProcessingTimeMs(0L);
            node.setProcessStatus("SUCCESS");

            // 来源信息
            node.setSourceType("API");

            // 基础审计字段
            Long currentUser = request.getUserId();
            node.setCreatedBy(currentUser);
            node.setCreatedAt(now);
            node.setUpdatedBy(currentUser);
            node.setUpdatedAt(now);
            node.setDelFlag(false);
            node.setIsActive(true); // 新创建的节点默认为活跃

            // 保存节点
            messageNodeMapper.insert(node);

            // 3. 将消息节点关联到轮次，并设为活跃版本
            messageTurnService.addMessageToTurn(currentTurn.getTurnId(), node, true);

            // 如果有父节点，更新父节点的子节点列表
            if (parentNodeId != null) {
                updateParentChildrenList(parentNodeId, node.getNodeId());
            }
            return node;

        } catch (Exception e) {
            log.error("创建用户消息节点失败", e);
            throw new RuntimeException("创建用户消息节点失败", e);
        }
    }

    @Override
    @Transactional
    public AiAssistantMessageNode createAssistantMessageNode(AssistantApiRequest request,
                                                             String userNodeId,
                                                             String reasoningData,
                                                             String textData,
                                                             Integer inputTokens,
                                                             Integer outputTokens,
                                                             Long startTimeMillis,
                                                             Long endTimeMillis,
                                                             Long processingTimeMs,
                                                             String modelName) {
        // 在公共方法中获取用户ID，避免在内部方法中获取
        Long currentUserId = request.getUserId();
        return createAssistantMessageNodeInternal(request, userNodeId, reasoningData, textData,
                inputTokens, outputTokens, startTimeMillis, endTimeMillis, processingTimeMs, modelName, false, currentUserId);
    }
    
    /**
     * 创建AI助手回复节点的内部方法
     * @param isRegenerate 是否为重新生成（重新生成的节点应该成为活跃版本）
     */
    @Transactional
    private AiAssistantMessageNode createAssistantMessageNodeInternal(AssistantApiRequest request,
                                                                      String userNodeId,
                                                                      String reasoningData,
                                                                      String textData,
                                                                      Integer inputTokens,
                                                                      Integer outputTokens,
                                                                      Long startTimeMillis,
                                                                      Long endTimeMillis,
                                                                      Long processingTimeMs,
                                                                      String modelName,
                                                                      boolean isRegenerate,
                                                                      Long userId) {
        try {
            // 1. 获取用户消息节点，确定所属轮次
            AiAssistantMessageNode userNode = getNodeById(userNodeId);
            if (userNode == null) {
                throw new RuntimeException("找不到用户消息节点: " + userNodeId);
            }
            
            String turnId = userNode.getTurnId();
            if (turnId == null) {
                throw new RuntimeException("用户消息节点未关联轮次: " + userNodeId);
            }
            
            // 2. 获取该轮次在ASSISTANT类型中的版本序号
            List<AiAssistantMessageNode> existingAssistantVersions = messageTurnService.getVersionsByTurnAndType(turnId, "ASSISTANT");
            Integer versionIndex = existingAssistantVersions.size(); // 新版本的序号
            
            // 3. 创建AI回复节点
            AiAssistantMessageNode node = new AiAssistantMessageNode();

            // 生成唯一节点ID
            node.setNodeId(UUID.randomUUID().toString());
            node.setParentNodeId(userNodeId);
            node.setSessionUuid(request.getSessionUuid());
            node.setTurnId(turnId); // 关联到同一轮次
            node.setMessageType("ASSISTANT");
            node.setVersionIndex(versionIndex); // 设置版本序号
            node.setContent(textData != null ? textData : reasoningData);

            // 思考数据和文本数据
            node.setReasoningData(reasoningData);
            node.setTextData(textData);

            // Token信息
            node.setInputTokens(inputTokens != null ? inputTokens : 0);
            node.setOutputTokens(outputTokens != null ? outputTokens : 0);
            node.setTotalTokens((inputTokens != null ? inputTokens : 0) + (outputTokens != null ? outputTokens : 0));

            // 模型信息
            node.setModelName(modelName);

            node.setProcessingTimeMs(processingTimeMs != null ? processingTimeMs : 0L);
            node.setProcessStatus("SUCCESS");

            // 来源信息
            node.setSourceType("API");

            // 基础审计字段
            node.setCreatedBy(userId);
            node.setCreatedAt(LocalDateTime.now());
            node.setUpdatedBy(userId);
            node.setUpdatedAt(LocalDateTime.now());
            node.setDelFlag(false);
            node.setIsActive(true); // 新创建的节点默认为活跃

            // 保存节点
            messageNodeMapper.insert(node);

            // 4. 将AI回复关联到轮次，判断是否设为活跃版本
            // 重新生成的节点应该成为活跃版本，否则只有第一个版本默认为活跃
            boolean isActiveVersion = isRegenerate || (versionIndex == 0);
            messageTurnService.addMessageToTurn(turnId, node, isActiveVersion);
            
            // 如果是重新生成，需要将其他版本设为非活跃，并更新会话的当前节点
            if (isRegenerate) {
                log.info("重新生成节点，切换活跃版本: nodeId={}, turnId={}", node.getNodeId(), turnId);
                messageTurnService.switchActiveVersion(turnId, "ASSISTANT", node.getNodeId());
                assistantSessionService.updateCurrentNode(request.getSessionUuid(), node.getNodeId(),request.getUserId());
            }

            // 更新父节点（用户消息）的子节点列表
            updateParentChildrenList(userNodeId, node.getNodeId());

            // 更新会话的当前节点ID（保持向后兼容）
            assistantSessionService.updateCurrentNode(request.getSessionUuid(), node.getNodeId(),request.getUserId());

            return node;

        } catch (Exception e) {
            log.error("创建AI助手消息节点失败", e);
            throw new RuntimeException("创建AI助手消息节点失败", e);
        }
    }

    @Override
    public List<AiAssistantMessageNode> getConversationHistory(String sessionUuid) {
        try {
            // 通过轮次管理获取活跃对话路径
            return messageTurnService.getActiveConversationPath(sessionUuid);

        } catch (Exception e) {
            log.error("获取对话历史失败，sessionUuid: {}", sessionUuid, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<AiAssistantMessageNode> getConversationPath(String nodeId) {
        return messageNodeMapper.getConversationPath(nodeId);
    }

    @Override
    public AiAssistantMessageNode getRootNode(String sessionUuid) {
        return messageNodeMapper.getRootNode(sessionUuid);
    }

    @Override
    public AiAssistantMessageNode getNodeById(String nodeId) {
        return messageNodeMapper.getNodeById(nodeId);
    }

    @Override
    public List<AiAssistantMessageNode> getChildrenNodes(String nodeId) {
        return messageNodeMapper.getChildrenByParentId(nodeId);
    }

    @Override
    @Transactional
    public AiAssistantMessageNode createRegeneratedAssistantNode(AssistantApiRequest request,
                                                                 String originalNodeId,
                                                                 String reasoningData,
                                                                 String textData,
                                                                 Integer inputTokens,
                                                                 Integer outputTokens,
                                                                 Long startTimeMillis,
                                                                 Long endTimeMillis,
                                                                 Long processingTimeMs,
                                                                 String modelName) {
        try {
            log.info("创建重新生成的AI回复节点: originalNodeId={}, sessionUuid={}", originalNodeId, request.getSessionUuid());
            
            // 获取原始节点
            AiAssistantMessageNode originalNode = getNodeById(originalNodeId);
            if (originalNode == null) {
                throw new RuntimeException("找不到原始节点: " + originalNodeId);
            }
            
            // 验证会话UUID一致性
            if (!originalNode.getSessionUuid().equals(request.getSessionUuid())) {
                log.warn("重新生成请求的会话UUID与原始节点不匹配 - 请求: {}, 原始: {}", 
                        request.getSessionUuid(), originalNode.getSessionUuid());
            }

            // 获取当前用户ID
            Long currentUserId = request.getUserId();

            // 创建新的重新生成节点，使用特殊的重新生成逻辑
            AiAssistantMessageNode regeneratedNode = createAssistantMessageNodeInternal(
                    request, originalNode.getParentNodeId(),
                    reasoningData, textData, inputTokens, outputTokens,
                    startTimeMillis, endTimeMillis, processingTimeMs, modelName,
                    true, currentUserId); // 标记为重新生成，应该成为活跃节点

            log.info("重新生成的AI回复节点创建成功: nodeId={}, turnId={}, 已设为活跃版本", 
                    regeneratedNode.getNodeId(), regeneratedNode.getTurnId());
            
            return regeneratedNode;

        } catch (Exception e) {
            log.error("创建重新生成的AI回复节点失败", e);
            throw new RuntimeException("创建重新生成的AI回复节点失败", e);
        }
    }

    @Override
    @Transactional
    public AiAssistantMessageNode createEditedUserMessageNode(AssistantApiRequest request, String originalNodeId) {
        try {
            // 1. 获取原始节点
            AiAssistantMessageNode originalNode = getNodeById(originalNodeId);
            if (originalNode == null) {
                throw new RuntimeException("找不到原始节点: " + originalNodeId);
            }

            String turnId = originalNode.getTurnId();
            if (turnId == null) {
                throw new RuntimeException("原始用户消息节点未关联轮次: " + originalNodeId);
            }

            // 2. 获取该轮次在USER类型中的版本序号
            List<AiAssistantMessageNode> existingUserVersions = messageTurnService.getVersionsByTurnAndType(turnId, "USER");
            Integer versionIndex = existingUserVersions.size(); // 新版本的序号

            // 3. 创建编辑后的用户消息节点
            AiAssistantMessageNode node = new AiAssistantMessageNode();

            // 生成唯一节点ID
            node.setNodeId(UUID.randomUUID().toString());
            node.setParentNodeId(originalNode.getParentNodeId()); // 与原始节点相同的父节点
            node.setSessionUuid(request.getSessionUuid());
            node.setTurnId(turnId); // 关联到同一轮次
            node.setMessageType("USER");
            node.setVersionIndex(versionIndex); // 设置版本序号
            node.setContent(request.getMessage());
            node.setTextData(request.getMessage());

            // 用户消息没有Token消耗
            node.setInputTokens(0);
            node.setOutputTokens(0);
            node.setTotalTokens(0);
            node.setTokenCost(BigDecimal.ZERO);

            // 时间信息
            LocalDateTime now = LocalDateTime.now();
            node.setProcessingTimeMs(0L);
            node.setProcessStatus("SUCCESS");

            // 来源信息
            node.setSourceType("API");

            // 基础审计字段
            Long currentUser = request.getUserId();
            node.setCreatedBy(currentUser);
            node.setCreatedAt(now);
            node.setUpdatedBy(currentUser);
            node.setUpdatedAt(now);
            node.setDelFlag(false);
            node.setIsActive(true);

            // 保存节点
            messageNodeMapper.insert(node);

            // 4. 将编辑后的用户消息关联到轮次，并设为活跃版本
            messageTurnService.addMessageToTurn(turnId, node, true);
            
            // 5. 切换该轮次的活跃用户消息版本
            messageTurnService.switchActiveVersion(turnId, "USER", node.getNodeId());
            
            // 6. 更新会话的当前节点
            assistantSessionService.updateCurrentNode(request.getSessionUuid(), node.getNodeId(), currentUser);
            
            // 7. 清除该轮次原有的AI回复（因为用户消息已变更，原回复不再适用）
            clearAssistantResponsesInTurn(turnId);

            // 更新父节点的子节点列表
            if (originalNode.getParentNodeId() != null) {
                updateParentChildrenList(originalNode.getParentNodeId(), node.getNodeId());
            }

            log.info("创建编辑后的用户消息节点成功 - nodeId: {}, originalNodeId: {}, turnId: {}, versionIndex: {}",
                    node.getNodeId(), originalNodeId, turnId, versionIndex);

            return node;

        } catch (Exception e) {
            log.error("创建编辑后的用户消息节点失败", e);
            throw new RuntimeException("创建编辑后的用户消息节点失败", e);
        }
    }
    
    /**
     * 清除轮次中的AI回复（当用户消息被编辑后，原回复不再适用）
     * @param turnId 轮次ID
     */
    @Transactional
    private void clearAssistantResponsesInTurn(String turnId) {
        try {
            log.info("清除轮次中的AI回复: turnId={}", turnId);
            
            // 获取该轮次的所有AI回复
            List<AiAssistantMessageNode> assistantNodes = messageTurnService.getVersionsByTurnAndType(turnId, "ASSISTANT");
            
            if (!assistantNodes.isEmpty()) {
                // 将所有AI回复标记为删除
                for (AiAssistantMessageNode assistantNode : assistantNodes) {
                    assistantNode.setDelFlag(true);
                    assistantNode.setIsActive(false);
                    messageNodeMapper.updateById(assistantNode);
                    log.debug("标记AI回复为删除: nodeId={}", assistantNode.getNodeId());
                }
                
                // 清除轮次的活跃AI回复指针
                messageTurnService.clearActiveAssistantNode(turnId);
                
                log.info("成功清除轮次中的{}个AI回复", assistantNodes.size());
            } else {
                log.debug("轮次中没有AI回复需要清除: turnId={}", turnId);
            }
            
        } catch (Exception e) {
            log.error("清除轮次AI回复失败: turnId={}", turnId, e);
        }
    }

    @Override
    public AiAssistantMessageNode buildMessageTree(String sessionUuid) {
        try {
            // 获取根节点
            AiAssistantMessageNode rootNode = getRootNode(sessionUuid);
            if (rootNode == null) {
                return null;
            }

            // 递归构建完整的树结构
            buildChildrenRecursively(rootNode);

            return rootNode;

        } catch (Exception e) {
            log.error("构建消息树失败，sessionUuid: {}", sessionUuid, e);
            return null;
        }
    }

    /**
     * 递归构建子节点
     */
    private void buildChildrenRecursively(AiAssistantMessageNode node) {
        List<AiAssistantMessageNode> children = getChildrenNodes(node.getNodeId());

        if (!children.isEmpty()) {
            // 为了在前端展示，我们可以将子节点存储在extendInfo中
            Map<String, Object> extendInfo = node.getExtendInfo();
            if (extendInfo == null) {
                extendInfo = new HashMap<>();
                node.setExtendInfo(extendInfo);
            }
            extendInfo.put("children", children);

            // 递归处理每个子节点
            for (AiAssistantMessageNode child : children) {
                buildChildrenRecursively(child);
            }
        }
    }

    @Override
    public List<AiAssistantMessageNode> getAllAssistantNodesByTurn(String turnId) {
        try {
            QueryWrapper<AiAssistantMessageNode> wrapper = new QueryWrapper<>();
            wrapper.eq("turn_id", turnId)
                   .eq("message_type", "ASSISTANT")
                   // 不过滤del_flag，获取包括已删除的版本
                   .orderByAsc("version_index");
            
            List<AiAssistantMessageNode> allVersions = messageNodeMapper.selectList(wrapper);
            log.debug("获取轮次所有AI回复版本（包括已删除）: turnId={}, 版本数={}", turnId, allVersions.size());
            
            return allVersions;
            
        } catch (Exception e) {
            log.error("获取轮次所有AI回复版本失败: turnId={}", turnId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 更新父节点的子节点列表
     */
    private void updateParentChildrenList(String parentNodeId, String childNodeId) {
        try {
            // 先直接获取当前的子节点列表JSON，确保不丢失已有数据
            String currentChildrenJson = messageNodeMapper.getChildrenNodeIdsJson(parentNodeId);
            log.debug("从数据库获取的当前子节点JSON - parentNodeId: {}, children_json: {}", 
                    parentNodeId, currentChildrenJson);
            
            // 解析现有的子节点列表
            List<String> currentChildren = new ArrayList<>();
            if (currentChildrenJson != null && !currentChildrenJson.trim().isEmpty() && !"null".equals(currentChildrenJson)) {
                try {
                    currentChildren = JSON.parseArray(currentChildrenJson, String.class);
                    if (currentChildren == null) {
                        currentChildren = new ArrayList<>();
                    }
                } catch (Exception e) {
                    log.warn("解析子节点JSON失败，使用空列表 - JSON: {}", currentChildrenJson, e);
                    currentChildren = new ArrayList<>();
                }
            }
            
            log.debug("解析后的当前子节点列表 - parentNodeId: {}, children: {}", parentNodeId, currentChildren);
            
            // 检查是否需要添加新的子节点
            if (!currentChildren.contains(childNodeId)) {
                currentChildren.add(childNodeId);
                
                // 将更新后的子节点列表序列化为JSON
                String updatedChildrenJson = JSON.toJSONString(currentChildren);
                log.debug("准备更新子节点列表 - parentNodeId: {}, newJSON: {}", parentNodeId, updatedChildrenJson);
                
                int updateResult = messageNodeMapper.updateChildrenNodeIds(parentNodeId, updatedChildrenJson);
                
                if (updateResult > 0) {
                    log.info("更新父节点子节点列表成功 - parentNodeId: {}, childNodeId: {}, 最终子节点: {}", 
                            parentNodeId, childNodeId, currentChildren);
                } else {
                    log.warn("更新父节点子节点列表失败，受影响行数为0");
                }
            } else {
                log.debug("子节点ID已存在，跳过添加 - parentNodeId: {}, childNodeId: {}", parentNodeId, childNodeId);
            }
        } catch (Exception e) {
            log.error("更新父节点子节点列表失败", e);
            // 如果更新失败，尝试直接通过MyBatis-Plus更新
            try {
                AiAssistantMessageNode parentNode = messageNodeMapper.getNodeById(parentNodeId);
                if (parentNode != null) {
                    if (parentNode.getChildrenNodeIds() == null) {
                        parentNode.setChildrenNodeIds(new ArrayList<>());
                    }
                    if (!parentNode.getChildrenNodeIds().contains(childNodeId)) {
                        parentNode.addChildNodeId(childNodeId);
                        
                        AiAssistantMessageNode updateNode = new AiAssistantMessageNode();
                        updateNode.setNodeId(parentNodeId);
                        updateNode.setChildrenNodeIds(parentNode.getChildrenNodeIds());
                        updateNode.setUpdatedAt(LocalDateTime.now());
                        
                        int result = messageNodeMapper.updateById(updateNode);
                        if (result > 0) {
                            log.debug("通过MyBatis-Plus更新父节点子节点列表成功");
                        }
                    }
                }
            } catch (Exception e2) {
                log.error("通过MyBatis-Plus更新也失败", e2);
            }
        }
    }
}