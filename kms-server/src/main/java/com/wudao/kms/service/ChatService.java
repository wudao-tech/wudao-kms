package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.wudao.kms.dto.ChatFileUploadDTO;
import com.wudao.kms.dto.ChatRequestDTO;
import com.wudao.kms.entity.chat.ChatSession;
import com.wudao.oss.service.OssService;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionQaService chatSessionQaService;
    private final ChatFileUploadService chatFileUploadService;

    /**
     * 上传文件（图片和文件）
     */
    public Long uploadFile(ChatFileUploadDTO uploadDTO) {
        try {
            // 使用新的文件上传服务
            Long fileUploadId = chatFileUploadService.uploadFile(
                    uploadDTO.getFile(),
                    uploadDTO.getSessionId(),
                    uploadDTO.getFileType()
            );

            log.info("上传聊天文件成功，会话ID: {}, 文件上传记录ID: {}",
                    uploadDTO.getSessionId(), fileUploadId);

            return fileUploadId;

        } catch (Exception e) {
            log.error("上传聊天文件失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取上传文件记录
     */
    public List<String> getUploadFileRecord(String sessionId) {
        return chatSessionQaService.getUploadFileRecord(sessionId);
    }
} 