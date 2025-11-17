package com.wudao.kms.agent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wudao.kms.agent.domain.Prompt;
import com.wudao.kms.agent.dto.*;
import com.wudao.kms.agent.service.PromptService;
import com.wudao.kms.common.AjaxResult;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * 提示词工程
 */
@RestController
@RequestMapping("/api/v2/prompt")
@Tag(name = "提示词管理", description = "提示词模板管理接口")
@RequiredArgsConstructor
@Validated
public class PromptController {
    
    private final PromptService promptService;

    /**
     * 保存Prompt模板 用到的参数有名称、类型（官方还是个人）、Prompt、最近编辑时间
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "保存提示词模板")
    public AjaxResult savePrompt(@Valid @RequestBody PromptCreateRequest request) {
        try {
            Prompt prompt = promptService.savePrompt(request);
            return AjaxResult.success("保存成功", prompt);
        } catch (Exception e) {
            return AjaxResult.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 可以根据名称搜索，支持分页
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "查询提示词列表")
    public AjaxResult list(@Valid @ModelAttribute PromptListRequest request) {
        try {
            Long userId = SecurityUtils.getUserId();
            boolean isAdmin = userId == 1L;
            IPage<Prompt> page = promptService.getPromptListWithPermission(request, userId, isAdmin);
            return AjaxResult.success(page);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取提示词详情
     * @param id 提示词ID
     * @return 提示词详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取提示词详情")
    public AjaxResult getById(@PathVariable Long id) {
        try {
            Prompt prompt = promptService.getPromptById(id);
            if (prompt == null) {
                return AjaxResult.error("提示词不存在");
            }
            return AjaxResult.success(prompt);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 编辑提示词
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping
    @Operation(summary = "更新提示词")
    public AjaxResult edit(@Valid @RequestBody PromptUpdateRequest request) {
        try {
            Prompt prompt = promptService.updatePrompt(request);
            return AjaxResult.success("更新成功", prompt);
        } catch (Exception e) {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除提示词
     * @param id 提示词ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除提示词")
    public AjaxResult delete(@PathVariable Long id) {
        try {
            boolean result = promptService.deletePrompt(id);
            if (result) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除提示词
     * @param request 批量删除请求
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除提示词")
    public AjaxResult batchDelete(@Valid @RequestBody PromptBatchDeleteRequest request) {
        try {
            boolean result = promptService.batchDeletePrompts(request.getIds());
            if (result) {
                return AjaxResult.success("批量删除成功");
            } else {
                return AjaxResult.error("批量删除失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 复制提示词
     * @param request 复制请求
     * @return 复制结果
     */
    @PostMapping("/copy")
    @Operation(summary = "复制提示词")
    public AjaxResult copy(@Valid @RequestBody PromptCopyRequest request) {
        try {
            Prompt prompt = promptService.copyPrompt(request);
            return AjaxResult.success("复制成功", prompt);
        } catch (Exception e) {
            return AjaxResult.error("复制失败：" + e.getMessage());
        }
    }

    /**
     * 按标签查询提示词
     * @param tags 标签列表（逗号分隔）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 查询结果
     */
    @GetMapping("/by-tags")
    @Operation(summary = "按标签查询提示词")
    public AjaxResult getByTags(@RequestParam String tags, 
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            IPage<Prompt> page = promptService.getPromptsByTags(tags, pageNum, pageSize);
            return AjaxResult.success(page);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 切换提示词状态
     * @param request 状态切换请求
     * @return 切换结果
     */
    @PutMapping("/status")
    @Operation(summary = "切换提示词状态")
    public AjaxResult updateStatus(@Valid @RequestBody PromptStatusRequest request) {
        try {
            boolean result = promptService.updatePromptStatus(request.getId(), request.getStatus());
            if (result) {
                return AjaxResult.success("状态更新成功");
            } else {
                return AjaxResult.error("状态更新失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 导出提示词
     * @param request 导出请求
     * @return 导出文件
     */
    @PostMapping("/export")
    @Operation(summary = "导出提示词")
    public ResponseEntity<byte[]> export(@Valid @RequestBody PromptExportRequest request) {
        try {
            byte[] data = promptService.exportPrompts(request);
            String filename = "prompts_" + System.currentTimeMillis() + "." + request.getFormat();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", filename);
            
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if ("json".equals(request.getFormat())) {
                mediaType = MediaType.APPLICATION_JSON;
            } else if ("csv".equals(request.getFormat())) {
                mediaType = MediaType.TEXT_PLAIN;
            } else if ("excel".equals(request.getFormat())) {
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }
            
            headers.setContentType(mediaType);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 导入提示词
     * @param file 上传的文件
     * @return 导入结果
     */
    @PostMapping("/import")
    @Operation(summary = "导入提示词")
    public AjaxResult importPrompts(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return AjaxResult.error("上传文件不能为空");
            }
            
            int count = promptService.importPrompts(file);
            return AjaxResult.success("导入成功，共导入 " + count + " 条提示词");
        } catch (Exception e) {
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }
}
