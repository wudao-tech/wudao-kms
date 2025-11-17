package com.wudao.kms.controller;

import com.wudao.common.model.vo.R;
import com.wudao.kms.entity.KnowledgeSpace;
import com.wudao.kms.service.KnowledgeSpaceService;
import com.wudao.kms.vo.KnowledgeSpaceTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库空间Controller
 */
@Tag(name = "知识库空间管理", description = "知识库空间相关接口")
@RestController
@RequestMapping("/api/knowledge-space")
@RequiredArgsConstructor
public class KnowledgeSpaceController {

    private final KnowledgeSpaceService knowledgeSpaceService;

    @Operation(summary = "创建空间")
    @PostMapping
    public R<Boolean> create(@RequestBody KnowledgeSpace space) {
        Boolean result = knowledgeSpaceService.createSpace(space);
        return R.ok(result);
    }

    @Operation(summary = "更新空间")
    @PutMapping
    public R<Boolean> update(@RequestBody KnowledgeSpace space) {
        Boolean result = knowledgeSpaceService.updateSpace(space);
        return R.ok(result);
    }

    @Operation(summary = "删除空间")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "空间ID") @PathVariable Long id) {
        Boolean result = knowledgeSpaceService.deleteSpace(id);
        return R.ok(result);
    }

    @Operation(summary = "根据ID查询空间")
    @GetMapping("/{id}")
    public R<KnowledgeSpace> getById(@Parameter(description = "空间ID") @PathVariable Long id) {
        KnowledgeSpace result = knowledgeSpaceService.getById(id);
        return R.ok(result);
    }

    @Operation(summary = "根据知识库ID查询空间树")
    @GetMapping("/tree/{knowledgeBaseId}")
    public R<List<KnowledgeSpace>> getSpaceTree(@Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId) {
        List<KnowledgeSpace> result = knowledgeSpaceService.listByKnowledgeBaseId(knowledgeBaseId);
        return R.ok(result);
    }

    @Operation(summary = "根据父级ID查询子空间")
    @GetMapping("/children/{parentId}")
    public R<List<KnowledgeSpace>> getChildren(@Parameter(description = "父级ID") @PathVariable Long parentId) {
        List<KnowledgeSpace> result = knowledgeSpaceService.listByParentId(parentId);
        return R.ok(result);
    }

    @Operation(summary = "移动空间")
    @PutMapping("/{spaceId}/move")
    public R<Boolean> moveSpace(
            @Parameter(description = "空间ID") @PathVariable Long spaceId,
            @Parameter(description = "新父级ID") @RequestParam Long newParentId) {
        Boolean result = knowledgeSpaceService.moveSpace(spaceId, newParentId);
        return R.ok(result);
    }

    @Operation(summary = "获取空间完整路径")
    @GetMapping("/{id}/path")
    public R<String> getFullPath(@Parameter(description = "空间ID") @PathVariable Long id) {
        String result = knowledgeSpaceService.getFullPath(id);
        return R.ok(result);
    }

    @Operation(summary = "根据路径查询空间")
    @GetMapping("/by-path")
    public R<KnowledgeSpace> getByPath(
            @Parameter(description = "知识库ID") @RequestParam Long knowledgeBaseId,
            @Parameter(description = "路径") @RequestParam String path) {
        KnowledgeSpace result = knowledgeSpaceService.getByPath(knowledgeBaseId, path);
        return R.ok(result);
    }

    @Operation(summary = "根据知识库ID查询空间树形结构（包含子节点）")
    @GetMapping("/tree-structure/{knowledgeBaseId}")
    public R<List<KnowledgeSpaceTreeVO>> getSpaceTreeStructure(
            @Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId) {
        List<KnowledgeSpaceTreeVO> result = knowledgeSpaceService.getSpaceTree(knowledgeBaseId);
        return R.ok(result);
    }
} 