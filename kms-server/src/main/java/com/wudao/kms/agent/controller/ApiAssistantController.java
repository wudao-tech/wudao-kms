package com.wudao.kms.agent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wudao.common.model.vo.R;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.dto.AssistantCreateRequest;
import com.wudao.kms.agent.dto.AssistantListRequest;
import com.wudao.kms.agent.dto.AssistantListResponse;
import com.wudao.kms.agent.dto.AssistantPublishRequest;
import com.wudao.kms.agent.dto.AssistantUpdateRequest;
import com.wudao.kms.agent.dto.ImprovePromptRequest;
import com.wudao.kms.agent.service.AssistantService;
import com.wudao.kms.common.AjaxResult;
import com.wudao.kms.service.FavoriteRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * AI助手控制器
 */
@Tag(name = "AI助手管理", description = "AI助手的创建、更新和提示词优化")
@RestController
@RequestMapping("/api/agent/v2/assistant")
@Slf4j
public class ApiAssistantController {
    
    @Autowired
    private AssistantService assistantService;
    @Resource
    private FavoriteRecordService favoriteRecordService;

    /**
     * 创建应用
     */
    @Operation(summary = "创建AI助手")
    @PostMapping
    public R<Assistant> createAssistant(@RequestBody AssistantCreateRequest request) {
        log.info("创建AI助手请求: name={}, prompt={}", request.getName(), request.getPrompt());
        
        try {
            Assistant assistant = assistantService.createAssistant(request);
            return R.ok(assistant);
        } catch (Exception e) {
            log.error("创建AI助手失败", e);
            return R.fail("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改应用
     */
    @Operation(summary = "更新AI助手")
    @PutMapping
    public R<Assistant> updateAssistant(@RequestBody AssistantUpdateRequest request) {
        try {
            Assistant assistant = assistantService.updateAssistant(request);
            return R.ok(assistant);
        } catch (Exception e) {
            log.error("更新AI助手失败", e);
            return R.fail("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 提示词优化 - 流式响应
     */
    @Operation(summary = "提示词优化")
    @PostMapping(value = "/optimizePrompt", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> optimizePrompt(@RequestBody @Validated ImprovePromptRequest request, HttpServletResponse response) {
        log.info("提示词优化请求: assistantUuid={}, prompt={}", request.getAssistantUuid(), request.getPrompt());
        response.addHeader("X-Accel-Buffering", "no");
        return assistantService.optimizePrompt(request);
    }
    
    /**
     * 获取助手详情
     */
    @Operation(summary = "获取AI助手详情")
    @GetMapping("/{uuid}")
    public AjaxResult getAssistant(@PathVariable String uuid) {
        log.info("获取AI助手详情: uuid={}", uuid);
        
        try {
            Assistant assistant = assistantService.getAssistantByUuid(uuid);
            if (assistant == null) {
                return AjaxResult.error("助手不存在");
            }
            return AjaxResult.success(assistant);
        } catch (Exception e) {
            log.error("获取AI助手详情失败", e);
            return AjaxResult.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询助手列表
     */
    @Operation(summary = "分页查询AI助手列表")
    @PostMapping("/list")
    public AjaxResult getAssistantList(@RequestBody AssistantListRequest request) {
        try {
            IPage<AssistantListResponse> page = assistantService.getAssistantList(request);
            return AjaxResult.success(page);
        } catch (Exception e) {
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除助手
     */
    @Operation(summary = "删除AI助手")
    @DeleteMapping("/{uuid}")
    public AjaxResult deleteAssistant(@PathVariable String uuid) {
        log.debug("删除AI助手: uuid={}", uuid);
        try {
            boolean success = assistantService.deleteAssistant(uuid);
            if (success) {
                // 根据targetId和type删除收藏
                favoriteRecordService.deleteFavorite("assistant", uuid);
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除AI助手失败", e);
            return AjaxResult.error("删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "发布助手")
    @PostMapping("/publish")
    public AjaxResult publishAssistant(@RequestBody AssistantPublishRequest request) {
        assistantService.publish(request);
        return AjaxResult.success();
    }

    @Operation(summary = "复制助手")
    @PostMapping("/copy/{uuid}")
    public AjaxResult copyAssistant(@PathVariable String uuid) {
        return AjaxResult.success(assistantService.copyAssistant(uuid));
    }
} 