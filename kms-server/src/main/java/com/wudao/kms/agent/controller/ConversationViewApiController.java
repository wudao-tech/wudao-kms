package com.wudao.kms.agent.controller;

import com.wudao.kms.agent.dto.TurnBasedConversationView;
import com.wudao.kms.agent.service.MessageQualityService;
import com.wudao.kms.agent.service.TurnBasedConversationService;
import com.wudao.kms.common.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.wudao.kms.common.AjaxResult.error;
import static com.wudao.kms.common.AjaxResult.success;

/**
 * 对话视图控制器
 * 提供类似ChatGPT的对话结构查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/api/v2/conversation")
@Tag(name = "对话视图管理", description = "提供基于轮次的对话结构查询")
public class ConversationViewApiController {
    
    @Resource
    private TurnBasedConversationService conversationService;
    
    @Resource
    private MessageQualityService qualityService;
    
    /**
     * 获取会话的对话视图（类似ChatGPT的对话结构）
     */
    @GetMapping("/{sessionUuid}/turns")
    @Operation(summary = "获取对话轮次视图", description = "按轮次组织的对话结构，支持版本管理")
    public AjaxResult getConversationView(
            @Parameter(description = "会话UUID", required = true)
            @PathVariable String sessionUuid) {
        try {

            TurnBasedConversationView conversationView = conversationService.getConversationByTurns(sessionUuid);
            
            if (conversationView == null) {
                return error("会话不存在");
            }
            
            return success(conversationView);
            
        } catch (Exception e) {
            log.error("获取对话视图失败: sessionUuid={}", sessionUuid, e);
            return error("获取对话视图失败: " + e.getMessage());
        }
    }
    
    /**
     * 切换轮次中的活跃版本
     */
    @PostMapping("/{sessionUuid}/turns/{turnId}/switch-version")
    @Operation(summary = "切换活跃版本", description = "在指定轮次中切换用户消息或AI回复的活跃版本")
    public AjaxResult switchActiveVersion(
            @Parameter(description = "会话UUID", required = true)
            @PathVariable String sessionUuid,
            @Parameter(description = "轮次ID", required = true)
            @PathVariable String turnId,
            @Parameter(description = "消息类型: USER或ASSISTANT", required = true)
            @RequestParam String messageType,
            @Parameter(description = "目标节点ID", required = true)
            @RequestParam String nodeId) {
        try {
            log.info("切换活跃版本: sessionUuid={}, turnId={}, messageType={}, nodeId={}", 
                    sessionUuid, turnId, messageType, nodeId);
            
            conversationService.switchActiveVersion(sessionUuid, turnId, messageType, nodeId);
            
            return success("版本切换成功");
            
        } catch (Exception e) {
            log.error("切换活跃版本失败", e);
            return error("切换版本失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定轮次的详细信息
     */
    @GetMapping("/{sessionUuid}/turns/{turnId}")
    @Operation(summary = "获取轮次详情", description = "获取指定轮次的所有版本信息")
    public AjaxResult getTurnDetail(
            @Parameter(description = "会话UUID", required = true)
            @PathVariable String sessionUuid,
            @Parameter(description = "轮次ID", required = true)
            @PathVariable String turnId) {
        try {
            log.info("获取轮次详情: sessionUuid={}, turnId={}", sessionUuid, turnId);
            
            // 这里可以实现单个轮次的详细查询逻辑
            TurnBasedConversationView conversationView = conversationService.getConversationByTurns(sessionUuid);
            
            if (conversationView == null) {
                return error("会话不存在");
            }
            
            // 找到指定的轮次
            var turn = conversationView.getTurns().stream()
                    .filter(t -> t.getTurnId().equals(turnId))
                    .findFirst()
                    .orElse(null);
                    
            if (turn == null) {
                return error("轮次不存在");
            }
            
            return success(turn);
            
        } catch (Exception e) {
            log.error("获取轮次详情失败", e);
            return error("获取轮次详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取对话的简化视图（只返回活跃版本）
     */
    @GetMapping("/{sessionUuid}/active-path")
    @Operation(summary = "获取活跃对话路径", description = "只返回每个轮次的活跃版本，适合普通对话展示")
    public AjaxResult getActiveConversationPath(
            @Parameter(description = "会话UUID", required = true)
            @PathVariable String sessionUuid) {
        try {
            log.info("获取活跃对话路径: sessionUuid={}", sessionUuid);
            
            TurnBasedConversationView conversationView = conversationService.getConversationByTurns(sessionUuid);
            
            if (conversationView == null) {
                return error("会话不存在");
            }
            
            // 只返回活跃路径
            var activePath = conversationView.getTurns().stream()
                    .filter(turn -> turn.getActiveUserMessage() != null || turn.getActiveAssistantMessage() != null)
                    .map(turn -> {
                        // 创建简化的轮次信息，只包含活跃消息
                        return turn.toBuilder()
                                .userVersions(null)
                                .assistantVersions(null)
                                .build();
                    })
                    .collect(java.util.stream.Collectors.toList());
                    
            return success(activePath);
            
        } catch (Exception e) {
            log.error("获取活跃对话路径失败", e);
            return error("获取活跃路径失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为最佳回复
     */
    @PostMapping("/messages/{nodeId}/mark-best")
    @Operation(summary = "标记最佳回复", description = "将指定的AI回复标记为最佳回复")
    public AjaxResult markAsBestResponse(
            @Parameter(description = "节点ID", required = true)
            @PathVariable String nodeId,
            @Parameter(description = "是否清除同轮次其他最佳标记", required = false)
            @RequestParam(defaultValue = "true") Boolean clearOthers) {
        try {
            log.info("标记最佳回复: nodeId={}, clearOthers={}", nodeId, clearOthers);
            
            boolean success = qualityService.markAsBestResponse(nodeId, clearOthers);
            
            if (success) {
                return success("标记成功");
            } else {
                return error("标记失败");
            }
            
        } catch (Exception e) {
            log.error("标记最佳回复失败", e);
            return error("标记失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为错误回复
     */
    @PostMapping("/messages/{nodeId}/mark-error")
    @Operation(summary = "标记错误回复", description = "将指定的AI回复标记为错误回复")
    public AjaxResult markAsErrorResponse(
            @Parameter(description = "节点ID", required = true)
            @PathVariable String nodeId) {
        try {
            log.info("标记错误回复: nodeId={}", nodeId);
            
            boolean success = qualityService.markAsErrorResponse(nodeId);
            
            if (success) {
                return success("标记成功");
            } else {
                return error("标记失败");
            }
            
        } catch (Exception e) {
            log.error("标记错误回复失败", e);
            return error("标记失败: " + e.getMessage());
        }
    }
    
    /**
     * 清除消息的特殊标记
     */
    @PostMapping("/messages/{nodeId}/clear-marks")
    @Operation(summary = "清除特殊标记", description = "清除消息的最佳/错误标记")
    public AjaxResult clearSpecialMarks(
            @Parameter(description = "节点ID", required = true)
            @PathVariable String nodeId) {
        try {
            log.info("清除特殊标记: nodeId={}", nodeId);
            
            boolean success = qualityService.clearSpecialMarks(nodeId);
            
            if (success) {
                return success("清除成功");
            } else {
                return error("清除失败");
            }
            
        } catch (Exception e) {
            log.error("清除特殊标记失败", e);
            return error("清除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取轮次中的最佳回复
     */
    @GetMapping("/turns/{turnId}/best-response")
    @Operation(summary = "获取最佳回复", description = "获取指定轮次中的最佳回复")
    public AjaxResult getBestResponse(
            @Parameter(description = "轮次ID", required = true)
            @PathVariable String turnId) {
        try {
            log.info("获取最佳回复: turnId={}", turnId);
            
            var bestResponse = qualityService.getBestResponseInTurn(turnId);
            
            if (bestResponse != null) {
                return success(bestResponse);
            } else {
                return success("该轮次暂无最佳回复");
            }
            
        } catch (Exception e) {
            log.error("获取最佳回复失败", e);
            return error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取轮次中的错误回复列表
     */
    @GetMapping("/turns/{turnId}/error-responses")
    @Operation(summary = "获取错误回复列表", description = "获取指定轮次中的所有错误回复")
    public AjaxResult getErrorResponses(
            @Parameter(description = "轮次ID", required = true)
            @PathVariable String turnId) {
        try {
            log.info("获取错误回复列表: turnId={}", turnId);
            
            var errorResponses = qualityService.getErrorResponsesInTurn(turnId);
            
            return success(errorResponses);
            
        } catch (Exception e) {
            log.error("获取错误回复列表失败", e);
            return error("获取失败: " + e.getMessage());
        }
    }
}
