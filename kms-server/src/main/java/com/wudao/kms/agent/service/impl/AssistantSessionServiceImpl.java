package com.wudao.kms.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.utils.StringUtils;
import com.wudao.kms.agent.domain.AiChatMemory;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.domain.AssistantMessage;
import com.wudao.kms.agent.domain.AssistantSession;
import com.wudao.kms.agent.dto.AssistantChatRequest;
import com.wudao.kms.agent.dto.AssistantSessionCreateRequest;
import com.wudao.kms.agent.dto.AssistantSessionListRequest;
import com.wudao.kms.agent.dto.AssistantSessionResponse;
import com.wudao.kms.agent.mapper.AiChatMemoryMapper;
import com.wudao.kms.agent.mapper.AssistantMapper;
import com.wudao.kms.agent.mapper.AssistantMessageMapper;
import com.wudao.kms.agent.mapper.AssistantSessionMapper;
import com.wudao.kms.agent.service.AssistantSessionService;
import com.wudao.security.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 助手会话服务实现类
 */
@Service
@Slf4j
public class AssistantSessionServiceImpl extends ServiceImpl<AssistantSessionMapper, AssistantSession>
        implements AssistantSessionService {
    
    @Autowired
    private AssistantSessionMapper sessionMapper;

    @Resource
    private AiChatMemoryMapper aiChatMemoryMapper;
    
    @Autowired
    private AssistantMessageMapper messageMapper;
    
    @Autowired
    private AssistantMapper assistantMapper;
    
    @Override
    @Transactional
    public AssistantSessionResponse createSession(AssistantSessionCreateRequest request) {
        // 验证助手是否存在
        Assistant assistant = assistantMapper.selectOne(
            new QueryWrapper<Assistant>().eq("uuid", request.getAssistantUuid())
        );
        if (assistant == null) {
            throw new RuntimeException("助手不存在");
        }
        
        // 创建会话
        AssistantSession session = new AssistantSession();
        session.setAssistantUuid(request.getAssistantUuid());
        session.setSessionUuid(UUID.randomUUID().toString().replace("-", ""));
        session.setSessionName(StrUtil.isNotBlank(request.getSessionName()) ? 
            request.getSessionName() : "与 " + assistant.getName() + " 的对话");
        session.setStatus(1); // 活跃状态
        session.setSessionConfig(request.getSessionConfig());
        session.setDelFlag(false);
        session.setSourceType(request.getSourceType());
        
        // 设置BaseEntity字段
        session.setCreatedBy(request.getCreateBy());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedBy(request.getCreateBy());
        session.setUpdatedAt(LocalDateTime.now());
        
        sessionMapper.insert(session);
        
        // 创建欢迎消息
        if (StrUtil.isNotBlank(assistant.getGuideWord())) {
            createWelcomeMessage(session.getSessionUuid(), assistant);
        }
        
        // 构建响应
        AssistantSessionResponse response = new AssistantSessionResponse();
        BeanUtils.copyProperties(session, response);
        response.setAssistantName(assistant.getName());
        response.setAssistantDesc(assistant.getDescription());
        response.setGuideWord(assistant.getGuideWord());
        response.setGuideQuestions(assistant.getGuideQuestions());
        response.setMessageCount(0);
        
        log.info("创建助手会话成功: sessionUuid={}, assistantUuid={}, user={}", 
                session.getSessionUuid(), assistant.getUuid(), SecurityUtils.getUserId());
        
        return response;
    }
    
    private void createWelcomeMessage(String sessionUuid, Assistant assistant) {
        AssistantMessage welcomeMessage = new AssistantMessage();
        welcomeMessage.setSessionUuid(sessionUuid);
        welcomeMessage.setAssistantUuid(assistant.getUuid());
        welcomeMessage.setMessageType("ASSISTANT");
        welcomeMessage.setContent("");
        welcomeMessage.setAiContent(assistant.getGuideWord());
        welcomeMessage.setRoundIndex(0);
        welcomeMessage.setStatus("COMPLETED");
        welcomeMessage.setCreatedBy(assistant.getCreatedBy());
        welcomeMessage.setCreatedAt(LocalDateTime.now());
        welcomeMessage.setUpdatedBy(assistant.getCreatedBy());
        welcomeMessage.setUpdatedAt(LocalDateTime.now());

        messageMapper.insert(welcomeMessage);
    }
    
    @Override
    public IPage<AssistantSessionResponse> getSessionList(AssistantSessionListRequest request) {
        Page<AssistantSession> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        QueryWrapper<AssistantSession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", false);
        
        // 权限校验：管理员可以看到所有，普通用户只能看到自己的
        if (SecurityUtils.getUserId() != 1L) {
            queryWrapper.eq("create_by", request.getCreateBy());
        }

        // 过滤条件
        if (StrUtil.isNotBlank(request.getAssistantUuid())) {
            queryWrapper.eq("assistant_uuid", request.getAssistantUuid());
        }
        if (StrUtil.isNotBlank(request.getSessionName())) {
            queryWrapper.like("session_name", request.getSessionName());
        }
        
        // 排序
        if (StrUtil.isNotBlank(request.getOrderBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSortDirection());
            queryWrapper.orderBy(true, isAsc, request.getOrderBy());
        } else {
            queryWrapper.orderByDesc("create_time");
        }
        
        IPage<AssistantSession> sessionPage = sessionMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        Page<AssistantSessionResponse> responsePage = new Page<>(request.getPageNum(), request.getPageSize());
        responsePage.setTotal(sessionPage.getTotal());
        
        List<AssistantSessionResponse> responseList = new ArrayList<>();
        for (AssistantSession session : sessionPage.getRecords()) {
            AssistantSessionResponse response = convertToResponse(session);
            responseList.add(response);
        }
        responsePage.setRecords(responseList);
        
        return responsePage;
    }
    
    private AssistantSessionResponse convertToResponse(AssistantSession session) {
        AssistantSessionResponse response = new AssistantSessionResponse();
        BeanUtils.copyProperties(session, response);
        
        // 获取助手信息
        Assistant assistant = assistantMapper.selectOne(
            new QueryWrapper<Assistant>().eq("uuid", session.getAssistantUuid())
        );
        if (assistant != null) {
            response.setAssistantName(assistant.getName());
            response.setAssistantDesc(assistant.getDescription());
            response.setGuideWord(assistant.getGuideWord());
            response.setGuideQuestions(assistant.getGuideQuestions());
        }
        
        // 获取消息统计
        QueryWrapper<AssistantMessage> messageQuery = new QueryWrapper<>();
        messageQuery.eq("session_uuid", session.getSessionUuid());
        messageQuery.eq("del_flag", false);
        int messageCount = messageMapper.selectCount(messageQuery).intValue();
        response.setMessageCount(messageCount);
        
        // 获取最后消息时间
        AssistantMessage lastMessage = messageMapper.selectOne(
            new QueryWrapper<AssistantMessage>()
                .eq("session_uuid", session.getSessionUuid())
                .eq("del_flag", false)
                .orderByDesc("create_time")
                .last("LIMIT 1")
        );
        if (lastMessage != null) {
            response.setLastMessageTime(lastMessage.getCreatedBy().toString());
        }
        
        return response;
    }
    
    @Override
    public AssistantSessionResponse getSessionDetail(String sessionUuid) {
        AssistantSession session = sessionMapper.selectOne(
            new QueryWrapper<AssistantSession>().eq("session_uuid", sessionUuid)
        );
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        
        // 权限校验
        if (SecurityUtils.getUserId() != 1L &&
            !session.getCreatedBy().equals(SecurityUtils.getUserId())) {
            throw new RuntimeException("无权限访问此会话");
        }
        
        return convertToResponse(session);
    }
    
    @Override
    public boolean deleteSession(String sessionUuid) {
        AssistantSession session = sessionMapper.selectOne(
            new QueryWrapper<AssistantSession>().eq("session_uuid", sessionUuid)
        );
        if (session == null) {
            return false;
        }
        
        // 软删除
        session.setDelFlag(true);
        session.setUpdatedBy(SecurityUtils.getUserId());
        session.setUpdatedAt(LocalDateTime.now());
        
        return sessionMapper.updateById(session) > 0;
    }
    
    @Override
    public List<AssistantMessage> getSessionMessages(String sessionUuid) {
        // 权限验证
        AssistantSession session = sessionMapper.selectOne(
            new QueryWrapper<AssistantSession>().eq("session_uuid", sessionUuid)
        );
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        LambdaQueryWrapper<AiChatMemory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMemory::getConversationId,sessionUuid);
        wrapper.orderByAsc(AiChatMemory::getId);
        List<AiChatMemory> chatMemories = aiChatMemoryMapper.selectList(wrapper);


        //转换消息格式
        List<AssistantMessage> assistantMessages = new ArrayList<>();
        for (AiChatMemory message : chatMemories) {
            AssistantMessage assistantMessage = new AssistantMessage();
            assistantMessage.setContent(message.getContent());
            assistantMessage.setMessageType(message.getType());
            assistantMessage.setId(message.getId());
            assistantMessage.setSessionUuid(sessionUuid);
            assistantMessages.add(assistantMessage);
        }

        if (CollUtil.isEmpty(assistantMessages)){
            return new ArrayList<>();
        }

        return assistantMessages;
    }
    
    private AssistantMessage createUserMessage(AssistantChatRequest request, AssistantSession session, Assistant assistant) {
        // 获取当前轮次
        QueryWrapper<AssistantMessage> roundQuery = new QueryWrapper<>();
        roundQuery.eq("session_uuid", request.getSessionUuid());
        roundQuery.eq("del_flag", false);
        roundQuery.orderByDesc("round_index");
        roundQuery.last("LIMIT 1");
        
        AssistantMessage lastMessage = messageMapper.selectOne(roundQuery);
        int nextRound = lastMessage != null ? lastMessage.getRoundIndex() + 1 : 1;
        
        AssistantMessage userMessage = new AssistantMessage();
        userMessage.setSessionUuid(request.getSessionUuid());
        userMessage.setAssistantUuid(assistant.getUuid());
        userMessage.setMessageType("USER");
        userMessage.setContent(request.getContent());
        userMessage.setRoundIndex(nextRound);
        userMessage.setStatus("PENDING");
        userMessage.setAttachments(request.getAttachments());
        userMessage.setCreatedBy(SecurityUtils.getUserId());
        userMessage.setCreatedAt(LocalDateTime.now());
        userMessage.setUpdatedBy(SecurityUtils.getUserId());
        userMessage.setUpdatedAt(LocalDateTime.now());

        messageMapper.insert(userMessage);
        return userMessage;
    }

    private String buildSystemPrompt(Assistant assistant) {
        StringBuilder prompt = new StringBuilder();
        if (StrUtil.isNotBlank(assistant.getSystemPrompt())) {
            prompt.append(assistant.getSystemPrompt());
        }
        if (StrUtil.isNotBlank(assistant.getPrompt())) {
            prompt.append("\n").append(assistant.getPrompt());
        }
        return prompt.toString();
    }

    
    @Override
    public boolean stopSession(String sessionUuid) {
        // 获取最后一条消息ID
        AssistantMessage lastMessage = messageMapper.selectOne(
            new QueryWrapper<AssistantMessage>()
                .eq("session_uuid", sessionUuid)
                .eq("del_flag", false)
                .orderByDesc("create_time")
                .last("LIMIT 1")
        );
        
        if (lastMessage != null) {
            // 更新会话的最后消息ID
            AssistantSession session = sessionMapper.selectOne(
                new QueryWrapper<AssistantSession>().eq("session_uuid", sessionUuid)
            );
            if (session != null) {
                session.setLastMessageId(lastMessage.getId());
                session.setUpdatedAt(LocalDateTime.now());
                return sessionMapper.updateById(session) > 0;
            }
        }
        
        return false;
    }

    @Override
    public AssistantSession getSessionByUuid(String sessionUuid) {
        try{
            AssistantSession session = sessionMapper.selectOne(
                new QueryWrapper<AssistantSession>().eq("session_uuid", sessionUuid).eq("del_flag", false)
            );
            return session;
        }catch (Exception e){
            log.error("获取会话失败: sessionUuid={}, error={}", sessionUuid, e.getMessage());
            throw new ServiceException("获取会话失败: " + e.getMessage());
        }
    }

    @Override
    public void updateSession(AssistantSession session) {
        if (session == null || session.getId() == null || StrUtil.isBlank(session.getSessionUuid())) {
            throw new ServiceException("会话信息不完整");
        }

        // 更新会话信息
        session.setUpdatedAt(LocalDateTime.now());

        sessionMapper.updateById(session);
    }

    @Override
    public void clearSessionByAssistant(String assistantUuid, String createBy, String sourceType) {
        try{
            LambdaQueryWrapper<AssistantSession> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AssistantSession::getAssistantUuid, assistantUuid)
                    .eq(StringUtils.isNotEmpty(createBy),AssistantSession::getCreatedBy, createBy)
                    .eq(StringUtils.isNotEmpty(sourceType), AssistantSession::getSourceType, sourceType)
                   .eq(AssistantSession::getDelFlag, false);
            sessionMapper.delete(wrapper);
        }catch (Exception e){
            log.error("清空助手会话失败: assistantUuid={}, error={}", assistantUuid, e.getMessage());
            throw new ServiceException("清空助手会话失败: " + e.getMessage());
        }
    }
    
    @Override
    public void updateCurrentNode(String sessionUuid, String currentNodeId, Long userId) {
        try {
            // 查找会话
            AssistantSession session = sessionMapper.selectOne(
                new QueryWrapper<AssistantSession>().eq("session_uuid", sessionUuid)
            );
            
            if (session == null) {
                log.warn("尝试更新不存在的会话节点: sessionUuid={}", sessionUuid);
                return;
            }
            
            // 权限校验 - 只有会话创建者可以更新
            if (!session.getCreatedBy().equals(userId)) {
                return;
            }
            
            // 更新当前节点ID
            session.setCurrentNodeId(currentNodeId);
            session.setUpdatedBy(userId);
            session.setUpdatedAt(LocalDateTime.now());
            
            sessionMapper.updateById(session);
            
            log.debug("更新会话当前节点成功: sessionUuid={}, currentNodeId={}", sessionUuid, currentNodeId);
            
        } catch (Exception e) {
            log.error("更新会话当前节点失败: sessionUuid={}, currentNodeId={}", sessionUuid, currentNodeId, e);
        }
    }
    
    @Override
    public String getCurrentNodeId(String sessionUuid) {
        try {
            // 查找会话
            AssistantSession session = sessionMapper.selectOne(
                new QueryWrapper<AssistantSession>().eq("session_uuid", sessionUuid)
            );
            
            if (session == null) {
                log.warn("尝试获取不存在的会话节点: sessionUuid={}", sessionUuid);
                return null;
            }
            
            return session.getCurrentNodeId();
            
        } catch (Exception e) {
            log.error("获取会话当前节点失败: sessionUuid={}", sessionUuid, e);
            return null;
        }
    }
} 