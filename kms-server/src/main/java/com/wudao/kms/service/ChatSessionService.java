package com.wudao.kms.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.kms.dto.ChatSessionDTO;
import com.wudao.kms.entity.chat.ChatSession;
import com.wudao.kms.entity.chat.ChatSessionQa;
import com.wudao.kms.mapper.ChatSessionMapper;
import com.wudao.kms.vo.ChatSessionListVO;
import com.wudao.kms.vo.ChatSessionVO;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天会话Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionService extends MPJBaseServiceImpl<ChatSessionMapper, ChatSession> {

    private final ChatSessionQaService chatSessionQaService;

    /**
     * 创建新会话
     */
    public Long createNewSession() {
        Long userId = SecurityUtils.getUserId();
        
        ChatSession chatSession = new ChatSession();
        chatSession.setUserId(userId);
        chatSession.setSessionId(IdUtil.simpleUUID());
        chatSession.setSessionName("新会话");
        
        this.save(chatSession);
        log.info("创建新会话成功，用户ID: {}, 会话ID: {}", userId, chatSession.getId());
        
        return chatSession.getId();
    }

    /**
     * 获取会话列表，区分今天和七日内
     */
    public ChatSessionListVO getSessionList() {
        Long userId = SecurityUtils.getUserId();
        
        // 获取今天的开始和结束时间
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX);
        
        // 获取七日前的开始时间
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7).with(LocalTime.MIN);
        
        // 查询用户的所有会话
        List<ChatSession> allSessions = this.list(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleteFlag, false)
                .orderByDesc(ChatSession::getUpdatedAt));
        
        // 转换为VO并分组
        ChatSessionListVO result = new ChatSessionListVO();
        
        List<ChatSessionVO> todaySessions = allSessions.stream()
                .filter(session -> session.getCreatedAt().isAfter(todayStart) && session.getCreatedAt().isBefore(todayEnd))
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        List<ChatSessionVO> weekSessions = allSessions.stream()
                .filter(session -> session.getCreatedAt().isAfter(weekStart) && session.getCreatedAt().isBefore(todayStart))
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        result.setTodaySessions(todaySessions);
        result.setWeekSessions(weekSessions);
        
        return result;
    }

    /**
     * 删除会话
     */
    public Boolean deleteSession(Long sessionId) {
        Long userId = SecurityUtils.getUserId();
        
        // 验证会话是否属于当前用户
        ChatSession session = this.getOne(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getId, sessionId)
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleteFlag, false));
        
        if (session == null) {
            throw new RuntimeException("会话不存在或无权限");
        }
        
        // 软删除会话
        session.setDeleteFlag(true);
        boolean success = this.updateById(session);
        
        if (success) {
            // 同时删除会话下的所有问答记录
            chatSessionQaService.deleteBySessionId(sessionId, userId);
            log.info("删除会话成功，会话ID: {}, 用户ID: {}", sessionId, userId);
        }
        
        return success;
    }

    /**
     * 编辑会话名称
     */
    public Boolean editSessionName(Long sessionId, String sessionName) {
        Long userId = SecurityUtils.getUserId();
        
        // 验证会话是否属于当前用户
        ChatSession session = this.getOne(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getId, sessionId)
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleteFlag, false));
        
        if (session == null) {
            throw new RuntimeException("会话不存在或无权限");
        }
        
        session.setSessionName(sessionName);
        boolean success = this.updateById(session);
        
        if (success) {
            log.info("编辑会话名称成功，会话ID: {}, 新名称: {}", sessionId, sessionName);
        }
        
        return success;
    }

    /**
     * 根据ID获取会话
     */
    public ChatSession getSessionById(Long sessionId, Long userId) {
        return this.getOne(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getId, sessionId)
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getDeleteFlag, false));
    }

    /**
     * 转换为VO
     */
    private ChatSessionVO convertToVO(ChatSession chatSession) {
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(chatSession.getId());
        vo.setSessionId(chatSession.getSessionId());
        vo.setSessionName(chatSession.getSessionName());
        vo.setUserId(chatSession.getUserId());
        vo.setCreatedAt(chatSession.getCreatedAt());
        vo.setUpdatedAt(chatSession.getUpdatedAt());
        
        // 获取最后一条消息和消息数量
        List<ChatSessionQa> qaList = chatSessionQaService.list(new LambdaQueryWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getSessionId, chatSession.getId())
                .eq(ChatSessionQa::getDeleteFlag, false)
                .orderByDesc(ChatSessionQa::getCreatedAt)
                .last("LIMIT 1"));
        
        if (!qaList.isEmpty()) {
            vo.setLastMessage(qaList.get(0).getQuestion());
        }
        
        Long messageCount = chatSessionQaService.count(new LambdaQueryWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getSessionId, chatSession.getId())
                .eq(ChatSessionQa::getDeleteFlag, false));
        
        vo.setMessageCount(messageCount.intValue());
        
        return vo;
    }
} 