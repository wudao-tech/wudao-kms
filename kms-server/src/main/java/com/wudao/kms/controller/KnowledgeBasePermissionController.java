package com.wudao.kms.controller;

import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.KnowledgeBasePermissionDTO;
import com.wudao.kms.entity.KnowledgeBasePermission;
import com.wudao.kms.service.KnowledgeBasePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库权限Controller
 */
@Tag(name = "知识库权限管理", description = "知识库权限相关接口")
@RestController
@RequestMapping("/api/knowledge-base-permission")
@RequiredArgsConstructor
public class KnowledgeBasePermissionController {

    private final KnowledgeBasePermissionService knowledgeBasePermissionService;

    @Operation(summary = "根据知识库ID查询权限列表")
    @GetMapping("/list/{knowledgeBaseId}")
    public R<List<KnowledgeBasePermission>> listByKnowledgeBaseId(
            @Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId) {
        List<KnowledgeBasePermission> permissions = knowledgeBasePermissionService.listByKnowledgeBaseId(knowledgeBaseId);
        return R.ok(permissions);
    }

    @Operation(summary = "添加用户权限")
    @PostMapping("/add")
    public R<Boolean> addUserPermission(@RequestBody KnowledgeBasePermissionDTO permissionDTO) {
        Boolean result = knowledgeBasePermissionService.addUserPermission(
                permissionDTO.getKnowledgeBaseId(),
                permissionDTO.getUserId(),
                permissionDTO.getUserName(),
                permissionDTO.getUserEmail(),
                permissionDTO.getUserRole(),
                permissionDTO.getPermissionType()
        );
        return R.ok(result);
    }

    @Operation(summary = "删除用户权限")
    @DeleteMapping("/remove")
    public R<Boolean> removeUserPermission(
            @Parameter(description = "知识库ID") @RequestParam Long knowledgeBaseId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return knowledgeBasePermissionService.removeUserPermission(knowledgeBaseId, userId);
    }

    @Operation(summary = "更新用户权限")
    @PutMapping("/update")
    public R<Boolean> updateUserPermission(@RequestBody KnowledgeBasePermissionDTO permissionDTO) {
        Boolean result = knowledgeBasePermissionService.updateUserPermission(
                permissionDTO.getKnowledgeBaseId(),
                permissionDTO.getUserId(),
                permissionDTO.getPermissionType(),
                permissionDTO.getUserRole()
        );
        return R.ok(result);
    }

    @Operation(summary = "检查用户权限")
    @GetMapping("/check")
    public R<Boolean> checkPermission(
            @Parameter(description = "知识库ID") @RequestParam Long knowledgeBaseId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        Boolean hasPermission = knowledgeBasePermissionService.hasPermission(knowledgeBaseId, userId);
        return R.ok(hasPermission);
    }

    @Operation(summary = "获取用户权限类型")
    @GetMapping("/permission-type")
    public R<Integer> getUserPermissionType(
            @Parameter(description = "知识库ID") @RequestParam Long knowledgeBaseId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        Integer permissionType = knowledgeBasePermissionService.getUserPermissionType(knowledgeBaseId, userId);
        return R.ok(permissionType);
    }

    @Operation(summary = "根据用户ID获取有权限的知识库ID列表")
    @GetMapping("/knowledge-bases/{userId}")
    public R<List<Long>> getKnowledgeBaseIdsByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Long> knowledgeBaseIds = knowledgeBasePermissionService.getKnowledgeBaseIdsByUserId(userId);
        return R.ok(knowledgeBaseIds);
    }
} 