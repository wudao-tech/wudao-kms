package com.wudao.kms.controller;

import cn.hutool.core.collection.CollUtil;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.KnowledgeBaseQueryDTO;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.service.KnowledgeBaseService;
import com.wudao.kms.service.SearchHistoryService;
import com.wudao.kms.service.VectorizationService;
import com.wudao.kms.vo.KnowledgeBaseVO;
import com.wudao.security.utils.SecurityUtils;
import com.wudao.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 知识库Controller
 */
@Tag(name = "知识库管理", description = "知识库相关接口")
@RestController
@RequestMapping("/api/knowledge-base")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    private final VectorizationService vectorizationService;

    private final SearchHistoryService searchHistoryService;

    private final ISysUserService iSysUserService;

    @Operation(summary = "创建知识库")
    @PostMapping
    public R<Boolean> create(@RequestBody @Validated KnowledgeBase knowledgeBase) {
        Boolean result = knowledgeBaseService.createKnowledgeBase(knowledgeBase);
        return R.ok(result);
    }

    @Operation(summary = "更新知识库")
    @PutMapping
    public R<Boolean> update(@RequestBody KnowledgeBase knowledgeBase) {
        Boolean result = knowledgeBaseService.updateKnowledgeBase(knowledgeBase);
        return R.ok(result);
    }

    @Operation(summary = "删除知识库")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "知识库ID") @PathVariable Long id) {
        Boolean result = knowledgeBaseService.deleteKnowledgeBase(id);
        return R.ok(result);
    }

    @Operation(summary = "根据ID查询知识库")
    @GetMapping("/{id}")
    public R<KnowledgeBase> getById(@Parameter(description = "知识库ID") @PathVariable Long id) {
        KnowledgeBase result = knowledgeBaseService.getById(id);
        return R.ok(result);
    }

    @Operation(summary = "分页查询知识库（带权限信息）")
    @GetMapping("/page")
    public PageDomain<KnowledgeBaseVO> pageWithPermission(
            @ParameterObject KnowledgeBaseQueryDTO knowledgeBaseQueryDTO,
            @ParameterObject PageDomain<KnowledgeBaseVO> page) {
        List<KnowledgeBaseVO> knowledgeBases = knowledgeBaseService.listWithPermission(knowledgeBaseQueryDTO, SecurityUtils.getUserId(), page);
        page.setData(knowledgeBases);
        return page;
    }

    @Operation(summary = "查询所有知识库")
    @GetMapping("/list")
    public R<List<KnowledgeBase>> list() {
        List<KnowledgeBase> result = knowledgeBaseService.listAvailableKnowledgeBase();
        return R.ok(result);
    }

    @Operation(summary = "启用知识库")
    @PutMapping("/{id}/enable")
    public R<Boolean> enable(@Parameter(description = "知识库ID") @PathVariable Long id) {
        Boolean result = knowledgeBaseService.enableKnowledgeBase(id);
        return R.ok(result);
    }

    @Operation(summary = "禁用知识库")
    @PutMapping("/{id}/disable")
    public R<Boolean> disable(@Parameter(description = "知识库ID") @PathVariable Long id) {
        Boolean result = knowledgeBaseService.disableKnowledgeBase(id);
        return R.ok(result);
    }

    @Operation(summary = "根据名称搜索知识库")
    @GetMapping("/search")
    public R<List<KnowledgeBase>> search(@Parameter(description = "搜索关键词") @RequestParam String name) {
        SysUser sysUser = iSysUserService.getById(SecurityUtils.getUserId());
        List<KnowledgeBase> result = knowledgeBaseService.searchByName(name, sysUser.getUsername());
        return R.ok(result);
    }


    @Operation(summary = "获取知识库标签")
    @GetMapping("/tags")
    public R<List<String>> getKnowledgeBaseTags() {
        PageDomain<KnowledgeBaseVO> page = new PageDomain<>();
        page.setPageSize(100000);
        page.setPageNum(1);
        List<KnowledgeBaseVO> baseVOList = knowledgeBaseService.listWithPermission(new KnowledgeBaseQueryDTO(), SecurityUtils.getUserId(), page);
        List<String> tags = baseVOList.stream().map(KnowledgeBaseVO::getTags).toList();
        tags = tags.stream().flatMap(tag -> List.of(tag.split(",")).stream()).distinct().toList();
        return R.ok(tags);
    }

    @Operation(summary = "知识库检索")
    @PostMapping("/search")
    public R<List<KnowledgeSearchResult>> search(@RequestBody KnowledgeTestDTO knowledgeTestDTO) {
        // 保存搜索历史
        if (knowledgeTestDTO != null && knowledgeTestDTO.getQuery() != null) {
            searchHistoryService.saveSearchHistory(knowledgeTestDTO);
        }
        if (CollUtil.isEmpty(knowledgeTestDTO.getKnowledgeIds())) {
            // 获取到当前拥有知识库
            List<KnowledgeBase> knowledgeBases = knowledgeBaseService.listAvailableKnowledgeBase();
            if (CollUtil.isEmpty(knowledgeBases)) {
                return R.ok();
            }
            List<Long> knowledgeIds = knowledgeBases.stream().map(KnowledgeBase::getId).toList();
            knowledgeTestDTO.setKnowledgeIds(knowledgeIds);
        }
        return R.ok(vectorizationService.searchPro(knowledgeTestDTO));
    }
} 