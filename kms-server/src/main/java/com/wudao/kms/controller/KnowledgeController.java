package com.wudao.kms.controller;

import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.SummaryInfoDTO;
import com.wudao.kms.service.VectorizationService;
import com.wudao.kms.service.WorkspaceService;
import com.wudao.kms.vo.LatestKnowledgeResponse;
import com.wudao.kms.vo.RecommendKnowledgeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 知识Controller
 */
@Tag(name = "知识管理", description = "知识相关接口")
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final WorkspaceService workspaceService;

    @Operation(summary = "获取最新知识", description = "获取最新知识列表")
    @GetMapping("/latest")
    public R<LatestKnowledgeResponse> getLatestKnowledge() {
        LatestKnowledgeResponse response = workspaceService.getLatestKnowledge();
        return R.ok(response);
    }

    @Operation(summary = "获取知识推荐", description = "获取推荐知识列表")
    @GetMapping("/recommend")
    public R<RecommendKnowledgeResponse> getRecommendKnowledge() {
        RecommendKnowledgeResponse response = workspaceService.getRecommendKnowledge();
        return R.ok(response);
    }

    @Operation(summary = "获取首页汇总信息", description = "获取首页统计数据和热搜、搜索历史")
    @GetMapping("/summary")
    public R<SummaryInfoDTO> getSummaryInfo() {
        SummaryInfoDTO response = workspaceService.getSummaryInfo();
        return R.ok(response);
    }
} 