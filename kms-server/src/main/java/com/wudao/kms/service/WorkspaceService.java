package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.comment.mapper.SysCommentMapper;
import com.wudao.comment.model.entity.SysComment;
import com.wudao.comment.service.SysCommentService;
import com.wudao.kms.dto.KnowledgeBaseQueryDTO;
import com.wudao.kms.dto.SummaryInfoDTO;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.SearchHistory;
import com.wudao.kms.entity.VisitRecord;
import com.wudao.kms.mapper.KnowledgeBaseMapper;
import com.wudao.kms.mapper.KnowledgeFileMapper;
import com.wudao.kms.mapper.SearchHistoryMapper;
import com.wudao.kms.vo.LatestKnowledgeResponse;
import com.wudao.kms.vo.RecommendKnowledgeResponse;
import com.wudao.kms.vo.WorkspaceStatsVO;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工作台Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final VisitRecordService visitRecordService;
    private final FavoriteRecordService favoriteRecordService;
    private final CommentRecordService commentRecordService;
    private final KnowledgeFileMapper knowledgeFileMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final VectorizationService vectorizationService;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBaseService knowledgeBaseService;
    private final SysCommentMapper sysCommentMapper;

    /**
     * 获取工作台统计数据
     */
    public WorkspaceStatsVO getWorkspaceStats() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            log.warn("用户未登录，无法获取工作台统计数据");
            return new WorkspaceStatsVO();
        }

        // 获取当前用户的所有文件ID
        LambdaQueryWrapper<KnowledgeFile> fileQuery = new LambdaQueryWrapper<KnowledgeFile>()
                .eq(KnowledgeFile::getCreatedBy, userId);
        List<String> fileIds = knowledgeFileMapper.selectList(fileQuery)
                .stream()
                .map(item -> item.getId().toString())
                .toList();
        Long visitedByCount = visitRecordService.visitCountByFileIds();
        Long favoriteByCount = favoriteRecordService.favoriteCountByFileIds();
        Long commentCount = 0L;
        if (CollUtil.isNotEmpty(fileIds)) {
            LambdaQueryWrapper<SysComment> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SysComment::getTargetId, fileIds);
            commentCount = sysCommentMapper.selectCount(wrapper);
        }

        return new WorkspaceStatsVO()
                .setVisitedByCount(visitedByCount)
                .setFavoriteByCount(favoriteByCount)
                .setCommentByCount(commentCount);
    }

    /**
     * 获取最新知识
     */
    public LatestKnowledgeResponse getLatestKnowledge() {
        List<LatestKnowledgeResponse.LatestKnowledgeItem> items = new ArrayList<>();
        KnowledgeBaseQueryDTO dto = new KnowledgeBaseQueryDTO();
        dto.setUserId(SecurityUtils.getUserId());
        List<Long> knowledgeList = knowledgeBaseService.queryPermissionKnowledgeList(SecurityUtils.getUserId());
        // 查询我拥有的知识库的权限
        if (CollUtil.isEmpty(knowledgeList)){
            return new LatestKnowledgeResponse();
        }
        
        // 查询最新的文件，限制数量
        LambdaQueryWrapper<KnowledgeFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(KnowledgeFile::getKnowledgeBaseId,knowledgeList);
        queryWrapper.eq(KnowledgeFile::getDeleteFlag,false);
        queryWrapper.orderByDesc(KnowledgeFile::getCreatedAt);
        queryWrapper.last("LIMIT 20");
        List<KnowledgeFile> files = knowledgeFileMapper.selectList(queryWrapper);
        // 根据访问次数分组，获取访问次数最多的文件
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");

        List<Long> knowledgeBaseIds = files.stream()
            .map(KnowledgeFile::getKnowledgeBaseId)
            .distinct()
            .toList();
        Map<Long, KnowledgeBase> knowledgeBaseMap = getKnowledgeBaseName(knowledgeBaseIds);
        for (KnowledgeFile file : files) {
            LatestKnowledgeResponse.LatestKnowledgeItem item = new LatestKnowledgeResponse.LatestKnowledgeItem()
                .setFileId(file.getId())
                .setFileName(file.getFileName())
                .setKnowledgeBaseName(knowledgeBaseMap.get(file.getKnowledgeBaseId()).getName())
                .setCreatedAt(file.getCreatedAt())
                .setCreatedTimeText(file.getCreatedAt().format(formatter));
            items.add(item);
        }
        LatestKnowledgeResponse latestKnowledgeResponse = new LatestKnowledgeResponse();
        latestKnowledgeResponse.setLatestKnowledge(items);
        List<LatestKnowledgeResponse.LatestKnowledgeItem> hotKnowledge = new ArrayList<>();

        // 根据访问次数分组，获取访问次数最多的文件
        MPJLambdaWrapper<VisitRecord> wrapper = new MPJLambdaWrapper<VisitRecord>()
                .select(VisitRecord::getTargetId)
                .leftJoin(KnowledgeFile.class, KnowledgeFile::getId, VisitRecord::getTargetId)
                .selectSum(VisitRecord::getTargetId, "visitCount")
                .groupBy(VisitRecord::getTargetId)
                .eq(KnowledgeFile::getDeleteFlag,false)
                .eq(VisitRecord::getTargetType, "knowledge_file")
                .orderByDesc("visitCount");
        List<VisitRecord> hotList = visitRecordService.selectJoinList(VisitRecord.class, wrapper);
        if (CollUtil.isNotEmpty(hotList)) {
            List<Long> knowledgeIds = hotList.stream().map(VisitRecord::getTargetId)
                    .distinct()
                    .toList();
            // 这里要按照拥有知识库权限来做
            LambdaQueryWrapper<KnowledgeFile> fileWrapper = new LambdaQueryWrapper<>();
            fileWrapper.in(KnowledgeFile::getId,knowledgeIds);
            fileWrapper.in(KnowledgeFile::getKnowledgeBaseId,knowledgeList);
            fileWrapper.eq(KnowledgeFile::getDeleteFlag,false);
            fileWrapper.last("limit 10");
            List<KnowledgeFile> knowledgeFiles = knowledgeFileMapper.selectList(fileWrapper);
            if (knowledgeFiles.isEmpty()){
                latestKnowledgeResponse.setHotKnowledge(new ArrayList<>());
                return latestKnowledgeResponse;
            }
            Map<Long, KnowledgeFile> fileMap = knowledgeFiles.stream().collect(Collectors.toMap(KnowledgeFile::getId, file -> file));
            //获取知识库id
            List<Long> knowledgeBaseIdList = knowledgeFiles.stream()
                    .map(KnowledgeFile::getKnowledgeBaseId)
                    .distinct()
                    .toList();
            Map<Long, KnowledgeBase> knowledgeBaseMapHot = getKnowledgeBaseName(knowledgeBaseIdList);
            knowledgeIds.forEach(item->{
                KnowledgeFile file = fileMap.get(item);
                if (file == null){
                    return;
                }
                LatestKnowledgeResponse.LatestKnowledgeItem hotItem = new LatestKnowledgeResponse.LatestKnowledgeItem()
                        .setFileId(file.getId())
                        .setFileName(file.getFileName())
                        .setKnowledgeBaseName(knowledgeBaseMapHot.get(file.getKnowledgeBaseId()).getName())
                        .setCreatedAt(file.getCreatedAt())
                        .setCreatedTimeText(file.getCreatedAt().format(formatter));
                hotKnowledge.add(hotItem);
            });
            latestKnowledgeResponse.setHotKnowledge(hotKnowledge);
        }
        return latestKnowledgeResponse;
    }

    /**
     * 获取知识推荐
     */
    public RecommendKnowledgeResponse getRecommendKnowledge() {
        RecommendKnowledgeResponse recommendKnowledgeResponse = vectorizationService.recommendKnowledgeBase();
        if (recommendKnowledgeResponse == null || recommendKnowledgeResponse.getRecommendKnowledge() == null){
            return new RecommendKnowledgeResponse();
        }
        List<Long> fileId = recommendKnowledgeResponse.getRecommendKnowledge().stream().map(RecommendKnowledgeResponse.RecommendKnowledgeItem::getFileId).toList();
        if (CollUtil.isEmpty(fileId)) {
            return recommendKnowledgeResponse; // 如果没有推荐的文件ID，直接返回空结果
        }
        // 根据id查询文件名
        List<KnowledgeFile> knowledgeFiles = knowledgeFileMapper.selectByIds(fileId);
        Map<Long, KnowledgeFile> fileMap = knowledgeFiles.stream().collect(Collectors.toMap(KnowledgeFile::getId, file -> file));
        // 获取知识库id
        List<Long> knowledgeBaseIdList = knowledgeFiles.stream()
                .map(KnowledgeFile::getKnowledgeBaseId)
                .distinct()
                .toList();
        Map<Long, KnowledgeBase> knowledgeBaseMap = getKnowledgeBaseName(knowledgeBaseIdList);
        recommendKnowledgeResponse.getRecommendKnowledge().forEach(item -> {
            KnowledgeFile file = fileMap.get(item.getFileId());
            if (file != null) {
                item.setFileName(file.getFileName());
                item.setKnowledgeBaseName(knowledgeBaseMap.get(file.getKnowledgeBaseId()).getName());
            }
        });
        return recommendKnowledgeResponse;
    }

    /**
     * 获取首页汇总信息
     */
    public SummaryInfoDTO getSummaryInfo() {
        Long userId = SecurityUtils.getUserId();
        
        // 文件总数和今日新增
        long totalFiles = knowledgeFileMapper.selectCount(new QueryWrapper<>());
        long todayNewFiles = knowledgeFileMapper.selectCount(
            new QueryWrapper<KnowledgeFile>()
                .ge("created_at", LocalDate.now().atStartOfDay())
        );

        // 热搜内容（基于搜索历史统计）
        List<SummaryInfoDTO.HotSearchItem> hotSearchList = new ArrayList<>();
        List<SearchHistory> searchHistories = searchHistoryMapper.selectList(
            new QueryWrapper<SearchHistory>()
                .select("content, COUNT(*) as search_count")
                .groupBy("content")
                .orderByDesc("search_count")
                .last("LIMIT 6")
        );
        
        for (int i = 0; i < searchHistories.size(); i++) {
            SearchHistory history = searchHistories.get(i);
            SummaryInfoDTO.HotSearchItem item = new SummaryInfoDTO.HotSearchItem()
                .setIndex(i + 1)
                .setContent(history.getContent())
                .setSearchCount(1L); // 这里需要实际的统计逻辑
            hotSearchList.add(item);
        }

        // 搜索历史（当前用户的）
        List<String> historyList = new ArrayList<>();
        if (userId != null) {
            List<SearchHistory> userSearchHistories = searchHistoryMapper.selectList(
                new QueryWrapper<SearchHistory>()
                    .eq("user_id", userId)
                        .eq("source","vector_search")
                    .orderByDesc("create_time")
                    .last("LIMIT 6")
            );
            historyList = userSearchHistories.stream()
                .map(SearchHistory::getContent)
                .distinct()
                .collect(Collectors.toList());
        }

        return new SummaryInfoDTO()
            .setTotalFiles(totalFiles)
            .setTodayNewFiles(todayNewFiles)
            .setHotSearchList(hotSearchList)
            .setHistoryList(historyList);
    }

    /**
     * 获取知识库名称
     */
    private Map<Long,KnowledgeBase> getKnowledgeBaseName(List<Long> knowledgeBaseId) {
        if (CollUtil.isEmpty(knowledgeBaseId)) {
            return Map.of();
        }
        List<KnowledgeBase> knowledgeBases = knowledgeBaseMapper.selectByIds(knowledgeBaseId);
        return knowledgeBases.stream().collect(Collectors.toMap(KnowledgeBase::getId, kb -> kb));
    }
} 