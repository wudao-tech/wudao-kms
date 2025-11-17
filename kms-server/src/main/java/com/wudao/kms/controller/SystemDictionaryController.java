package com.wudao.kms.controller;

import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.SystemDictionaryImportDTO;
import com.wudao.kms.dto.SystemDictionaryQueryDTO;
import com.wudao.kms.entity.SystemDictionary;
import com.wudao.kms.service.SystemDictionaryService;
import com.wudao.kms.vo.SystemDictionaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
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
import java.util.Map;

/**
 * 系统词库Controller
 */
@Tag(name = "系统词库管理", description = "系统词库相关接口")
@RestController
@RequestMapping("/api/system/dictionary")
@RequiredArgsConstructor
public class SystemDictionaryController {

    private final SystemDictionaryService systemDictionaryService;

    @Operation(summary = "分页查询系统词库")
    @GetMapping("/page")
    public PageDomain<SystemDictionaryVO> page(
            @ParameterObject SystemDictionaryQueryDTO queryDTO,
            @ParameterObject PageDomain<SystemDictionaryVO> page) {
        return systemDictionaryService.page(queryDTO, page);
    }

    @Operation(summary = "查询系统词库列表")
    @GetMapping("/list")
    public R<List<SystemDictionaryVO>> list(@ParameterObject SystemDictionaryQueryDTO queryDTO) {
        List<SystemDictionaryVO> result = systemDictionaryService.list(queryDTO);
        return R.ok(result);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void export(@ParameterObject SystemDictionaryQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        systemDictionaryService.export(queryDTO, response);
    }

    @Operation(summary = "根据ID查询系统词库")
    @GetMapping("/{id}")
    public R<SystemDictionaryVO> getById(@Parameter(description = "词库ID") @PathVariable Long id) {
        SystemDictionaryVO result = systemDictionaryService.getById(id);
        return R.ok(result);
    }

    @Operation(summary = "创建系统词库")
    @PostMapping
    public R<Boolean> create(@RequestBody SystemDictionary dictionary) {
        Boolean result = systemDictionaryService.create(dictionary);
        return R.ok(result);
    }

    @Operation(summary = "更新系统词库")
    @PutMapping
    public R<Boolean> update(@RequestBody SystemDictionary dictionary) {
        Boolean result = systemDictionaryService.update(dictionary);
        return R.ok(result);
    }

    @Operation(summary = "删除系统词库")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "词库ID") @PathVariable Long id) {
        Boolean result = systemDictionaryService.delete(id);
        return R.ok(result);
    }

    @Operation(summary = "批量删除系统词库")
    @DeleteMapping("/batch")
    public R<Boolean> batchDelete(@RequestBody List<Long> ids) {
        Boolean result = systemDictionaryService.batchDelete(ids);
        return R.ok(result);
    }

    @Operation(summary = "批量导入系统词库")
    @PostMapping("/import")
    public R<Boolean> batchImport(@RequestBody SystemDictionaryImportDTO importDTO) {
        Boolean result = systemDictionaryService.batchImport(importDTO);
        return R.ok(result);
    }

    @Operation(summary = "根据类型查询词库")
    @GetMapping("/listByType")
    public R<List<SystemDictionaryVO>> listByType(
            @Parameter(description = "词库类型") @RequestParam String dictType) {
        List<SystemDictionaryVO> result = systemDictionaryService.listByType(dictType);
        return R.ok(result);
    }

    @Operation(summary = "获取所有词库类型")
    @GetMapping("/types")
    public R<List<Map<String, String>>> getDictTypes() {
        List<Map<String, String>> result = systemDictionaryService.getDictTypes();
        return R.ok(result);
    }

    @Operation(summary = "启用词库")
    @PutMapping("/{id}/enable")
    public R<Boolean> enable(@Parameter(description = "词库ID") @PathVariable Long id) {
        SystemDictionary dictionary = new SystemDictionary();
        dictionary.setId(id);
        dictionary.setStatus(1);
        Boolean result = systemDictionaryService.update(dictionary);
        return R.ok(result);
    }

    @Operation(summary = "禁用词库")
    @PutMapping("/{id}/disable")
    public R<Boolean> disable(@Parameter(description = "词库ID") @PathVariable Long id) {
        SystemDictionary dictionary = new SystemDictionary();
        dictionary.setId(id);
        dictionary.setStatus(0);
        Boolean result = systemDictionaryService.update(dictionary);
        return R.ok(result);
    }
} 