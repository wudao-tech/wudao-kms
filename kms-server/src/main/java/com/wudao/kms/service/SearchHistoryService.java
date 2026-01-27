package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.SearchHistory;
import com.wudao.kms.mapper.SearchHistoryMapper;
import com.wudao.kms.vo.SearchHistoryVO;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索历史Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchHistoryService extends ServiceImpl<SearchHistoryMapper, SearchHistory> {
    private final KnowledgeFileService knowledgeFileService;
    /**
     * 保存搜索记录
     * 
     * @param knowledgeTestDTO 搜索内容
     */
    public void saveSearchHistory(KnowledgeTestDTO knowledgeTestDTO) {
        String content = knowledgeTestDTO.getQuery();
        if (!StringUtils.hasText(content)) {
            return;
        }
        
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                log.warn("用户未登录，无法保存搜索历史");
                return;
            }

            // 创建新的搜索记录
            SearchHistory searchHistory = new SearchHistory()
                    .setContent(content.trim())
                    .setUserId(userId)
                    .setSource(knowledgeTestDTO.getSource())
                    .setCreateTime(LocalDateTime.now());
            if (CollUtil.isNotEmpty(knowledgeTestDTO.getDocumentIds())) {
                List<KnowledgeFile> knowledgeFiles = knowledgeFileService.listByIds(knowledgeTestDTO.getDocumentIds());
                searchHistory.setDoc(knowledgeFiles);
            }
            if (CollUtil.isNotEmpty(knowledgeTestDTO.getKnowledgeBaseIds())) {
                searchHistory.setKnowledgeSpaceId(knowledgeTestDTO.getKnowledgeBaseIds().getFirst());
            }
            
            this.save(searchHistory);
            log.debug("保存搜索历史成功: userId={}, content={}", userId, content);
            
        } catch (Exception e) {
            log.error("保存搜索历史失败: content={}", content, e);
        }
    }

    /**
     * 获取用户搜索历史
     * 
     * @param knowledgeSpaceId 知识空间id
     * @return 搜索历史列表
     */
    public List<SearchHistoryVO> getUserSearchHistory(Long knowledgeSpaceId) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }

        
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
                .eq(SearchHistory::getSource,"test_search") // 只查询测试搜索的历史
                .orderByDesc(SearchHistory::getCreateTime)
                .eq(SearchHistory::getKnowledgeSpaceId,knowledgeSpaceId)
                .last("LIMIT 15");
        
        List<SearchHistory> historyList = this.list(wrapper);
        
        return historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 删除用户搜索历史
     * 
     * @param id 搜索历史ID
     * @return 是否删除成功
     */
    public Boolean deleteSearchHistory(Long id) {
        if (id == null) {
            return false;
        }
        
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }
        
        // 只能删除自己的搜索历史
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getId, id)
                .eq(SearchHistory::getUserId, userId);
        
        return this.remove(wrapper);
    }

    /**
     * 清空用户搜索历史
     * 
     * @return 是否清空成功
     */
    public Boolean clearUserSearchHistory() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return false;
        }
        
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        
        return this.remove(wrapper);
    }

    /**
     * 获取用户搜索热词（去重的搜索内容，按频次排序）
     * 
     * @param limit 限制数量，默认10条
     * @return 热门搜索词列表
     */
    public List<String> getUserHotSearchWords(Integer limit) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return List.of();
        }
        
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        // 获取用户最近的搜索记录，然后按内容分组统计频次
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId)
                .orderByDesc(SearchHistory::getCreateTime)
                .last("LIMIT 100"); // 先取最近100条记录
        
        List<SearchHistory> recentHistory = this.list(wrapper);
        
        // 按内容分组并统计频次，然后按频次排序
        return recentHistory.stream()
                .collect(Collectors.groupingBy(SearchHistory::getContent, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO对象
     */
    private SearchHistoryVO convertToVO(SearchHistory searchHistory) {
        SearchHistoryVO vo = new SearchHistoryVO();
        BeanUtils.copyProperties(searchHistory, vo);
        return vo;
    }
} 