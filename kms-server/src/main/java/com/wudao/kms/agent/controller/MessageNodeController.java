package com.wudao.kms.agent.controller;

import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import com.wudao.kms.agent.dto.GroupedMessageResponse;
import com.wudao.kms.agent.dto.MessageTreeResponse;
import com.wudao.kms.agent.service.MessageGroupingService;
import com.wudao.kms.agent.service.MessageNodeService;
import com.wudao.kms.common.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.wudao.kms.common.AjaxResult.error;

/**
 * 消息节点控制器
 * 提供基于树状结构的消息管理API
 */
@Tag(name = "消息节点管理", description = "基于树状结构的消息节点API")
@RestController
@RequestMapping("/api/v2/message/node")
@Slf4j
public class MessageNodeController{
    
    @Autowired
    private MessageNodeService messageNodeService;
    
    @Autowired
    private MessageGroupingService messageGroupingService;
    
    
    /**
     * 获取会话的消息树结构
     */
    @Operation(summary = "获取消息树", description = "获取指定会话的完整消息树结构")
    @GetMapping("/tree/{sessionUuid}")
    public AjaxResult getMessageTree(
            @Parameter(description = "会话UUID") @PathVariable String sessionUuid) {
        try {
            AiAssistantMessageNode rootNode = messageNodeService.buildMessageTree(sessionUuid);
            
            if (rootNode == null) {
                return AjaxResult.success("会话暂无消息", null);
            }
            
            MessageTreeResponse response = new MessageTreeResponse();
            response.setSessionUuid(sessionUuid);
            response.setRootNode(MessageTreeResponse.MessageNodeDto.fromNode(rootNode));
            
            return AjaxResult.success(response);
            
        } catch (Exception e) {
            log.error("获取消息树失败", e);
            return error("获取消息树失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会话的对话历史路径
     */
    @Operation(summary = "获取对话历史", description = "获取从根节点到当前节点的对话路径")
    @GetMapping("/history/{sessionUuid}")
    public AjaxResult getConversationHistory(
            @Parameter(description = "会话UUID") @PathVariable String sessionUuid) {
        try {
            List<AiAssistantMessageNode> history = messageNodeService.getConversationHistory(sessionUuid);
            
            List<MessageTreeResponse.MessageNodeDto> historyDtos = history.stream()
                    .map(MessageTreeResponse.MessageNodeDto::fromNode)
                    .collect(Collectors.toList());
            
            MessageTreeResponse response = new MessageTreeResponse();
            response.setSessionUuid(sessionUuid);
            response.setConversationPath(historyDtos);
            
            return AjaxResult.success(response);
            
        } catch (Exception e) {
            log.error("获取对话历史失败", e);
            return error("获取对话历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定节点的详细信息
     */
    @Operation(summary = "获取节点详情", description = "根据节点ID获取节点的详细信息")
    @GetMapping("/detail/{nodeId}")
    public AjaxResult getNodeDetail(
            @Parameter(description = "节点ID") @PathVariable String nodeId) {
        try {
            AiAssistantMessageNode node = messageNodeService.getNodeById(nodeId);
            
            if (node == null) {
                return error("节点不存在");
            }

            return AjaxResult.success(MessageTreeResponse.MessageNodeDto.fromNode(node));
            
        } catch (Exception e) {
            log.error("获取节点详情失败", e);
            return error("获取节点详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取节点的子节点列表
     */
    @Operation(summary = "获取子节点", description = "获取指定节点的所有子节点")
    @GetMapping("/children/{nodeId}")
    public AjaxResult getChildrenNodes(
            @Parameter(description = "节点ID") @PathVariable String nodeId) {
        try {
            List<AiAssistantMessageNode> children = messageNodeService.getChildrenNodes(nodeId);
            
            List<MessageTreeResponse.MessageNodeDto> childrenDtos = children.stream()
                    .map(MessageTreeResponse.MessageNodeDto::fromNode)
                    .collect(Collectors.toList());
            
            return AjaxResult.success(childrenDtos);
            
        } catch (Exception e) {
            log.error("获取子节点失败", e);
            return error("获取子节点失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取从指定节点到根节点的路径
     */
    @Operation(summary = "获取节点路径", description = "获取从根节点到指定节点的完整路径")
    @GetMapping("/path/{nodeId}")
    public AjaxResult getNodePath(
            @Parameter(description = "节点ID") @PathVariable String nodeId) {
        try {
            List<AiAssistantMessageNode> path = messageNodeService.getConversationPath(nodeId);
            
            List<MessageTreeResponse.MessageNodeDto> pathDtos = path.stream()
                    .map(MessageTreeResponse.MessageNodeDto::fromNode)
                    .collect(Collectors.toList());
            
            return AjaxResult.success(pathDtos);
            
        } catch (Exception e) {
            log.error("获取节点路径失败", e);
            return error("获取节点路径失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取按轮次分组的对话历史（版本化显示）
     */
    @Operation(summary = "获取分组对话", description = "按对话轮次分组，支持用户消息和AI回复的版本化显示")
    @GetMapping("/grouped/{sessionUuid}")
    public AjaxResult getGroupedConversation(
            @Parameter(description = "会话UUID") @PathVariable String sessionUuid) {
        try {
            GroupedMessageResponse response = messageGroupingService.groupMessagesByTurns(sessionUuid);
            return AjaxResult.success(response);
            
        } catch (Exception e) {
            log.error("获取分组对话失败", e);
            return error("获取分组对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定轮次的用户消息版本
     */
    @Operation(summary = "获取用户消息版本", description = "获取指定对话轮次的所有用户消息版本")
    @GetMapping("/user-versions/{sessionUuid}/{turnIndex}")
    public AjaxResult getUserMessageVersions(
            @Parameter(description = "会话UUID") @PathVariable String sessionUuid,
            @Parameter(description = "轮次索引") @PathVariable int turnIndex) {
        try {
            List<AiAssistantMessageNode> versions = messageGroupingService.getUserMessageVersions(sessionUuid, turnIndex);
            
            List<MessageTreeResponse.MessageNodeDto> versionDtos = versions.stream()
                    .map(MessageTreeResponse.MessageNodeDto::fromNode)
                    .collect(Collectors.toList());
            
            return AjaxResult.success(versionDtos);
            
        } catch (Exception e) {
            log.error("获取用户消息版本失败", e);
            return error("获取用户消息版本失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定用户消息的AI回复版本
     */
    @Operation(summary = "获取AI回复版本", description = "获取指定用户消息的所有AI回复版本")
    @GetMapping("/assistant-versions/{userNodeId}")
    public AjaxResult getAssistantMessageVersions(
            @Parameter(description = "用户消息节点ID") @PathVariable String userNodeId) {
        try {
            List<AiAssistantMessageNode> versions = messageGroupingService.getAssistantMessageVersions(userNodeId);
            
            List<MessageTreeResponse.MessageNodeDto> versionDtos = versions.stream()
                    .map(MessageTreeResponse.MessageNodeDto::fromNode)
                    .collect(Collectors.toList());
            
            return AjaxResult.success(versionDtos);
            
        } catch (Exception e) {
            log.error("获取AI回复版本失败", e);
            return error("获取AI回复版本失败: " + e.getMessage());
        }
    }
    
}