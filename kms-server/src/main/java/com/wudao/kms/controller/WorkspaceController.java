package com.wudao.kms.controller;

import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.CommentQueryDTO;
import com.wudao.kms.dto.CommentRecordDTO;
import com.wudao.kms.dto.CommentUpdateDTO;
import com.wudao.kms.dto.FavoriteCheckDTO;
import com.wudao.kms.dto.FavoriteRecordDTO;
import com.wudao.kms.dto.FavoriteRemoveDTO;
import com.wudao.kms.dto.VisitRecordDTO;
import com.wudao.kms.service.CommentRecordService;
import com.wudao.kms.service.FavoriteRecordService;
import com.wudao.kms.service.MyKnowledgeService;
import com.wudao.kms.service.VisitRecordService;
import com.wudao.kms.service.WorkspaceService;
import com.wudao.kms.vo.CommentRecordVO;
import com.wudao.kms.vo.FavoriteRecordVO;
import com.wudao.kms.vo.MyKnowledgeVO;
import com.wudao.kms.vo.VisitRecordVO;
import com.wudao.kms.vo.WorkspaceStatsVO;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工作台Controller
 */
@Tag(name = "工作台管理", description = "工作台相关接口")
@RestController
@RequestMapping("/api/workspace")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final VisitRecordService visitRecordService;
    private final FavoriteRecordService favoriteRecordService;
    private final CommentRecordService commentRecordService;
    private final MyKnowledgeService myKnowledgeService;

    @Operation(summary = "获取工作台统计数据")
    @GetMapping("/stats")
    public R<WorkspaceStatsVO> getWorkspaceStats() {
        WorkspaceStatsVO stats = workspaceService.getWorkspaceStats();
        return R.ok(stats);
    }

    @Operation(summary = "获取我的知识列表")
    @GetMapping("/my-knowledge")
    public PageDomain<MyKnowledgeVO> getMyKnowledgeList(@ParameterObject PageDomain<MyKnowledgeVO> page) {
        List<MyKnowledgeVO> knowledgeList = myKnowledgeService.getMyKnowledgeList(page);
        page.setData(knowledgeList);
        return page;
    }

    // ======================= 访问记录相关接口 =======================

    @Operation(summary = "记录访问")
    @PostMapping("/visit/record")
    public R<Void> recordVisit(
            @RequestBody VisitRecordDTO visitRecordDTO,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        visitRecordService.recordVisit(visitRecordDTO.getTargetType(), visitRecordDTO.getTargetId(), 
                visitRecordDTO.getTargetName(), visitRecordDTO.getTargetUrl(), ipAddress, userAgent);
        return R.ok();
    }

    @Operation(summary = "获取访问记录列表")
    @GetMapping("/visit/records")
    public PageDomain<VisitRecordVO> getVisitRecords(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") Integer limit,
            @ParameterObject PageDomain<VisitRecordVO> page) {
        List<VisitRecordVO> records = visitRecordService.getUserVisitRecords(limit, page);
        page.setData(records);
        return page;
    }

    @Operation(summary = "获取访问总数")
    @GetMapping("/visit/count")
    public R<Long> getVisitCount() {
        Long count = visitRecordService.countUserVisits(SecurityUtils.getUserId());
        return R.ok(count);
    }

    @Operation(summary = "删除访问记录")
    @DeleteMapping("/visit/{id}")
    public R<Boolean> deleteVisitRecord(@Parameter(description = "访问记录ID") @PathVariable Long id) {
        Boolean result = visitRecordService.deleteVisitRecord(id);
        return R.ok(result);
    }

    // ======================= 收藏记录相关接口 =======================

    @Operation(summary = "添加收藏")
    @PostMapping("/favorite/add")
    public R<Boolean> addFavorite(@RequestBody FavoriteRecordDTO favoriteRecordDTO) {
        Boolean result = favoriteRecordService.addFavorite(favoriteRecordDTO.getTargetType(), 
                favoriteRecordDTO.getTargetId(), favoriteRecordDTO.getTargetName(), favoriteRecordDTO.getTargetUrl());
        return R.ok(result);
    }

    @Operation(summary = "取消收藏")
    @PostMapping("/favorite/remove")
    public R<Boolean> removeFavorite(@RequestBody FavoriteRemoveDTO favoriteRemoveDTO) {
        Boolean result = favoriteRecordService.removeFavorite(favoriteRemoveDTO.getTargetType(), favoriteRemoveDTO.getTargetId());
        return R.ok(result);
    }

    @Operation(summary = "检查是否已收藏")
    @PostMapping("/favorite/check")
    public R<Boolean> isFavorited(@RequestBody FavoriteCheckDTO favoriteCheckDTO) {
        Boolean result = favoriteRecordService.isFavorite(SecurityUtils.getUserId(),
                favoriteCheckDTO.getTargetType(), favoriteCheckDTO.getTargetId());
        return R.ok(result);
    }

    @Operation(summary = "获取收藏记录列表")
    @GetMapping("/favorite/records")
    public PageDomain<FavoriteRecordVO> getFavoriteRecords(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") Integer limit,
            @ParameterObject PageDomain<FavoriteRecordVO> page) {
        List<FavoriteRecordVO> records = favoriteRecordService.getUserFavoriteRecords(limit, page);
        page.setData(records);
        return page;
    }

    @Operation(summary = "获取收藏总数")
    @GetMapping("/favorite/count")
    public R<Long> getFavoriteCount() {
        Long count = favoriteRecordService.countUserFavorites(SecurityUtils.getUserId());
        return R.ok(count);
    }

    // ======================= 评论记录相关接口 =======================

    @Operation(summary = "添加评论")
    @PostMapping("/comment/add")
    public R<Boolean> addComment(@RequestBody CommentRecordDTO commentRecordDTO) {
        Boolean result = commentRecordService.addComment(commentRecordDTO.getTargetType(), 
                commentRecordDTO.getTargetId(), commentRecordDTO.getTargetName(), 
                commentRecordDTO.getCommentContent(), commentRecordDTO.getRating());
        return R.ok(result);
    }

    @Operation(summary = "更新评论")
    @PostMapping("/comment/update/{commentId}")
    public R<Boolean> updateComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @RequestBody CommentUpdateDTO commentUpdateDTO) {
        Boolean result = commentRecordService.updateComment(commentId, 
                commentUpdateDTO.getCommentContent(), commentUpdateDTO.getRating());
        return R.ok(result);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{commentId}")
    public R<Boolean> deleteComment(@Parameter(description = "评论ID") @PathVariable Long commentId) {
        Boolean result = commentRecordService.deleteComment(commentId);
        return R.ok(result);
    }

    @Operation(summary = "获取用户评论记录列表")
    @GetMapping("/comment/records")
    public PageDomain<CommentRecordVO> getUserCommentRecords(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") Integer limit,
            @ParameterObject PageDomain<CommentRecordVO> page) {
        List<CommentRecordVO> records = commentRecordService.getUserCommentRecords(limit, page);
        page.setData(records);
        return page;
    }

    @Operation(summary = "获取目标的评论列表")
    @PostMapping("/comment/target")
    public PageDomain<CommentRecordVO> getTargetComments(
            @RequestBody CommentQueryDTO commentQueryDTO,
            @ParameterObject PageDomain<CommentRecordVO> page) {
        List<CommentRecordVO> records = commentRecordService.getTargetComments(
                commentQueryDTO.getTargetType(), commentQueryDTO.getTargetId(), page);
        page.setData(records);
        return page;
    }

    @Operation(summary = "获取评论总数")
    @GetMapping("/comment/count")
    public R<Long> getCommentCount() {
        Long count = commentRecordService.countUserComments(SecurityUtils.getUserId());
        return R.ok(count);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
} 