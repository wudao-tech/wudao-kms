package com.wudao.kms.controller;

import com.wudao.common.model.vo.R;
import com.wudao.kms.service.SearchHistoryService;
import com.wudao.kms.vo.SearchHistoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索历史Controller
 */
@Tag(name = "搜索历史管理", description = "用户搜索历史相关接口")
@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @Operation(summary = "获取用户搜索历史")
    @GetMapping("/list")
    public R<List<SearchHistoryVO>> getUserSearchHistory(
            @RequestParam("knowledgeBaseIds") Long knowledgeSpaceId) {
        List<SearchHistoryVO> result = searchHistoryService.getUserSearchHistory(knowledgeSpaceId);
        return R.ok(result);
    }

    @Operation(summary = "删除搜索历史记录")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteSearchHistory(
            @Parameter(description = "搜索历史ID") @PathVariable Long id) {
        Boolean result = searchHistoryService.deleteSearchHistory(id);
        return R.ok(result);
    }

    @Operation(summary = "清空用户搜索历史")
    @DeleteMapping("/clear")
    public R<Boolean> clearUserSearchHistory() {
        Boolean result = searchHistoryService.clearUserSearchHistory();
        return R.ok(result);
    }

    @Operation(summary = "获取用户搜索热词")
    @GetMapping("/hot-words")
    public R<List<String>> getUserHotSearchWords(
            @Parameter(description = "限制数量，默认10条") @RequestParam(defaultValue = "10") Integer limit) {
        List<String> result = searchHistoryService.getUserHotSearchWords(limit);
        return R.ok(result);
    }
} 