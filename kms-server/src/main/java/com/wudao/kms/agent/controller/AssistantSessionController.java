package com.wudao.kms.agent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wudao.kms.agent.domain.AssistantSession;
import com.wudao.kms.agent.dto.*;
import com.wudao.kms.agent.service.AssistantSessionService;
import com.wudao.kms.common.AjaxResult;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI助手会话控制器
 */
@Tag(name = "AI助手会话管理", description = "AI助手的会话创建、查询、聊天等功能")
@RestController
@RequestMapping("/agent/v2/session")
@Slf4j
public class AssistantSessionController {
    
    @Autowired
    private AssistantSessionService sessionService;
    
    /**
     * 创建会话
     */
    @Operation(summary = "创建AI助手会话")
    @PostMapping
    public AjaxResult createSession(@Valid @RequestBody AssistantSessionCreateRequest request) {
        Long currentUser = request.getCreateBy();
        log.info("创建AI助手会话: assistantUuid={}, sessionName={}, user={}", 
                request.getAssistantUuid(), request.getSessionName(), currentUser);
        
        try {
            AssistantSessionResponse response = sessionService.createSession(request);
            log.info("创建AI助手会话成功: sessionUuid={}, user={}", response.getSessionUuid(), currentUser);
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("创建AI助手会话失败: user={}", currentUser, e);
            return AjaxResult.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新会话
     */
    @Operation(summary = "更新AI助手会话")
    @PutMapping
    public AjaxResult updateSession(@Valid @RequestBody AssistantSession request) {
        try {
            sessionService.updateSession(request);
            return AjaxResult.success("更新成功");
        } catch (Exception e) {
            log.error("更新AI助手会话失败: sessionUuid={}", request.getSessionUuid(), e);
            return AjaxResult.error("更新失败: " + e.getMessage());
        }
    }


    /**
     * 分页查询会话列表
     */
    @Operation(summary = "分页查询AI助手会话列表")
    @PostMapping("/list")
    public AjaxResult getSessionList(@RequestBody AssistantSessionListRequest request) {
        try {
            IPage<AssistantSessionResponse> page = sessionService.getSessionList(request);
            return AjaxResult.success(page);
        } catch (Exception e) {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会话详情
     */
    @Operation(summary = "获取AI助手会话详情")
    @GetMapping("/{sessionUuid}")
    public AjaxResult getSessionDetail(@PathVariable @Parameter(description = "会话UUID") String sessionUuid) {
        try {
            AssistantSessionResponse response = sessionService.getSessionDetail(sessionUuid);
            return AjaxResult.success(response);
        } catch (Exception e) {
            return AjaxResult.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除会话
     */
    @Operation(summary = "删除AI助手会话")
    @DeleteMapping("/{sessionUuid}")
    public AjaxResult deleteSession(@PathVariable @Parameter(description = "会话UUID") String sessionUuid) {

        try {
            boolean success = sessionService.deleteSession(sessionUuid);
            if (success) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会话历史消息
     */
    @Operation(summary = "获取会话历史消息")
    @GetMapping("/{sessionUuid}/messages")
    public AjaxResult getSessionMessages(
            @PathVariable @Parameter(description = "会话UUID") String sessionUuid) {
        try {
            return AjaxResult.success(sessionService.getSessionMessages(sessionUuid));
        } catch (Exception e) {
            return AjaxResult.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 清空指定助手的会话信息
     */
    @Operation(summary = "清空助手会话信息")
    @DeleteMapping("/clear")
    public AjaxResult clearSessionsByAssistant(
            @RequestParam @Parameter(description = "助手UUID") String assistantUuid,
            @RequestParam(required = false) String createBy,
            @RequestParam(required = false) String sourceType) {

        try {
            sessionService.clearSessionByAssistant(assistantUuid, createBy, sourceType);
            return AjaxResult.success("清空成功");
        } catch (Exception e) {
            log.error("清空助手会话信息失败: assistantUuid={}, user={}", assistantUuid, createBy, e);
            return AjaxResult.error("清空失败: " + e.getMessage());
        }
    }
    
    /**
     * 结束多轮对话
     */
    @Operation(summary = "结束多轮对话")
    @PostMapping("/{sessionUuid}/stop")
    public AjaxResult stopSession(@PathVariable @Parameter(description = "会话UUID") String sessionUuid) {

        try {
            boolean success = sessionService.stopSession(sessionUuid);
            if (success) {
                return AjaxResult.success("结束成功");
            } else {
                return AjaxResult.error("结束失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("结束失败: " + e.getMessage());
        }
    }
} 