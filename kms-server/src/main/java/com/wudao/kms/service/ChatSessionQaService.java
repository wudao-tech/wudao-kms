package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.kms.dto.ChatRequestDTO;
import com.wudao.kms.entity.chat.ChatSessionQa;
import com.wudao.kms.entity.chat.ChatFileUpload;
import com.wudao.kms.mapper.ChatSessionQaMapper;
import com.wudao.kms.vo.ChatSessionQaVO;
import com.wudao.security.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天会话问答Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSessionQaService extends MPJBaseServiceImpl<ChatSessionQaMapper, ChatSessionQa> {

    private final ChatFileUploadService chatFileUploadService;

    /**
     * 保存问答记录
     */
    public Long saveQa(Long sessionId, Long userId, ChatRequestDTO requestDTO, String answer) {
        ChatSessionQa qa = new ChatSessionQa();
        qa.setSessionId(sessionId);
        qa.setUserId(userId);
        qa.setQuestion(requestDTO.getQuestion());
        qa.setAnswer(answer);
        
        // 将文件上传ID列表转换为字符串存储
        if (requestDTO.getFileLists() != null && !requestDTO.getFileLists().isEmpty()) {
            // 这里假设fileLists现在包含的是文件上传记录的ID
            qa.setFileUploadIds(String.join(",", requestDTO.getFileLists()));
        }
        
        this.save(qa);
        log.info("保存问答记录成功，会话ID: {}, 问答ID: {}", sessionId, qa.getId());
        
        return qa.getId();
    }

    /**
     * 获取会话内容，从远到近
     */
    public List<ChatSessionQaVO> getSessionContent(Long sessionId, Long userId) {
        List<ChatSessionQa> qaList = this.list(new LambdaQueryWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getSessionId, sessionId)
                .eq(ChatSessionQa::getUserId, userId)
                .eq(ChatSessionQa::getDeleteFlag, false)
                .orderByAsc(ChatSessionQa::getCreatedAt));
        
        return qaList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 删除会话内容
     */
    public Boolean deleteSessionContent(Long qaId, Long userId) {
        // 验证问答记录是否属于当前用户
        ChatSessionQa qa = this.getOne(new LambdaQueryWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getId, qaId)  
                .eq(ChatSessionQa::getUserId, userId)
                .eq(ChatSessionQa::getDeleteFlag, false));
        
        if (qa == null) {
            throw new RuntimeException("问答记录不存在或无权限");
        }
        
        qa.setDeleteFlag(true);
        boolean success = this.updateById(qa);
        
        if (success) {
            log.info("删除问答记录成功，问答ID: {}, 用户ID: {}", qaId, userId);
        }
        
        return success;
    }

    /**
     * 清空会话内容
     */
    public Boolean clearSessionContent(Long sessionId, Long userId) {
        boolean success = this.update(new LambdaUpdateWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getSessionId, sessionId)
                .eq(ChatSessionQa::getUserId, userId)
                .set(ChatSessionQa::getDeleteFlag, true));
        
        if (success) {
            log.info("清空会话内容成功，会话ID: {}, 用户ID: {}", sessionId, userId);
        }
        
        return success;
    }

    /**
     * 根据会话ID删除所有问答记录
     */
    public void deleteBySessionId(Long sessionId, Long userId) {
        this.update(new LambdaUpdateWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getSessionId, sessionId)
                .eq(ChatSessionQa::getUserId, userId)
                .set(ChatSessionQa::getDeleteFlag, true));
    }

    /**
     * 获取上传文件记录
     */
    public List<String> getUploadFileRecord(String sessionId) {
        // 根据会话ID获取所有问答记录，然后获取关联的文件上传记录
        List<ChatSessionQa> qaList = this.list(new LambdaQueryWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getSessionId, sessionId)
                .eq(ChatSessionQa::getUserId, SecurityUtils.getUserId())
                .eq(ChatSessionQa::getDeleteFlag, false)
                .orderByAsc(ChatSessionQa::getCreatedAt));
        
        // 收集所有文件上传ID
        List<Long> allFileUploadIds = qaList.stream()
                .filter(item -> item.getFileUploadIds() != null && !item.getFileUploadIds().isEmpty())
                .flatMap(item -> chatFileUploadService.parseFileUploadIds(item.getFileUploadIds()).stream())
                .distinct()
                .collect(Collectors.toList());
        
        // 根据ID获取文件记录，返回文件URL
        List<ChatFileUpload> fileUploads = chatFileUploadService.getUploadFilesByIds(allFileUploadIds);
        return fileUploads.stream()
                .map(ChatFileUpload::getFileUrl)
                .collect(Collectors.toList());
    }

    /**
     * 获取最近的历史对话记录（最多十轮）
     */
    public List<ChatSessionQa> getRecentHistory(Long sessionId, Long userId, int maxRounds) {
        return this.list(new LambdaQueryWrapper<ChatSessionQa>()
                .eq(ChatSessionQa::getSessionId, sessionId)
                .eq(ChatSessionQa::getUserId, userId)
                .eq(ChatSessionQa::getDeleteFlag, false)
                .orderByDesc(ChatSessionQa::getCreatedAt)
                .last("LIMIT " + maxRounds))
                .stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())) // 重新按时间正序排列
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private ChatSessionQaVO convertToVO(ChatSessionQa qa) {
        ChatSessionQaVO vo = new ChatSessionQaVO();
        vo.setId(qa.getId());
        vo.setUserId(qa.getUserId());
        vo.setSessionId(qa.getSessionId());
        vo.setQuestion(qa.getQuestion());
        vo.setAnswer(qa.getAnswer());
        vo.setCreatedAt(qa.getCreatedAt());
        
        // 根据文件上传ID获取文件信息
        if (qa.getFileUploadIds() != null && !qa.getFileUploadIds().isEmpty()) {
            List<Long> fileUploadIds = chatFileUploadService.parseFileUploadIds(qa.getFileUploadIds());
            List<ChatFileUpload> fileUploads = chatFileUploadService.getUploadFilesByIds(fileUploadIds);
            List<String> fileUrls = fileUploads.stream()
                    .map(ChatFileUpload::getFileUrl)
                    .collect(Collectors.toList());
            vo.setFileLists(fileUrls);
        }
        
        // TODO: 设置推荐的知识库列表
        vo.setRecommendKnowledgeBases(List.of());
        
        return vo;
    }
} 