package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.kms.entity.chat.ChatFileUpload;
import com.wudao.kms.mapper.ChatFileUploadMapper;
import com.wudao.oss.service.OssService;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天文件上传Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatFileUploadService extends MPJBaseServiceImpl<ChatFileUploadMapper, ChatFileUpload> {

    private final OssService ossService;

    /**
     * 上传文件并保存记录
     */
    public Long uploadFile(MultipartFile file, Long sessionId, String fileCategory) {
        try {
            Long userId = SecurityUtils.getUserId();
            String originalFileName = file.getOriginalFilename();
            
            // 上传到S3
            String storagePath = "chat/" + sessionId;
            ossService.putObject((FileInputStream) file.getInputStream(), storagePath);
            String fileUrl = ossService.getHttpUrl(storagePath);

                    // 保存上传记录
            ChatFileUpload uploadRecord = new ChatFileUpload();
            uploadRecord.setUserId(userId);
            uploadRecord.setSessionId(sessionId);
            uploadRecord.setOriginalFileName(originalFileName);
            uploadRecord.setStorageFileName(extractFileNameFromUrl(fileUrl));
            uploadRecord.setFilePath(fileUrl);
            uploadRecord.setFileUrl(fileUrl);
            uploadRecord.setFileSize(file.getSize());
            uploadRecord.setFileType(getFileExtension(originalFileName));
            uploadRecord.setMimeType(file.getContentType());
            uploadRecord.setFileCategory(fileCategory);
            uploadRecord.setDeleteFlag(false);
            
            this.save(uploadRecord);
            
            log.info("上传聊天文件成功，会话ID: {}, 文件名: {}, 记录ID: {}", 
                    sessionId, originalFileName, uploadRecord.getId());
            
            return uploadRecord.getId();
            
        } catch (Exception e) {
            log.error("上传聊天文件失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 根据会话ID获取上传文件记录
     */
    public List<ChatFileUpload> getUploadFilesBySessionId(Long sessionId) {
        Long userId = SecurityUtils.getUserId();
        return this.list(new LambdaQueryWrapper<ChatFileUpload>()
                .eq(ChatFileUpload::getSessionId, sessionId)
                .eq(ChatFileUpload::getUserId, userId)
                .eq(ChatFileUpload::getDeleteFlag, false)
                .orderByDesc(ChatFileUpload::getCreatedAt));
    }

    /**
     * 根据ID列表获取文件记录
     */
    public List<ChatFileUpload> getUploadFilesByIds(List<Long> fileUploadIds) {
        if (fileUploadIds == null || fileUploadIds.isEmpty()) {
            return List.of();
        }
        
        return this.list(new LambdaQueryWrapper<ChatFileUpload>()
                .in(ChatFileUpload::getId, fileUploadIds)
                .eq(ChatFileUpload::getDeleteFlag, false));
    }

    /**
     * 从文件上传ID字符串解析ID列表
     */
    public List<Long> parseFileUploadIds(String fileUploadIds) {
        if (fileUploadIds == null || fileUploadIds.trim().isEmpty()) {
            return List.of();
        }
        
        return Arrays.stream(fileUploadIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    /**
     * 将文件上传ID列表转换为字符串
     */
    public String formatFileUploadIds(List<Long> fileUploadIds) {
        if (fileUploadIds == null || fileUploadIds.isEmpty()) {
            return "";
        }
        
        return fileUploadIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * 删除文件记录
     */
    public Boolean deleteUploadFile(Long fileUploadId) {
        Long userId = SecurityUtils.getUserId();
        
        ChatFileUpload uploadFile = this.getOne(new LambdaQueryWrapper<ChatFileUpload>()
                .eq(ChatFileUpload::getId, fileUploadId)
                .eq(ChatFileUpload::getUserId, userId)
                .eq(ChatFileUpload::getDeleteFlag, false));
        
        if (uploadFile == null) {
            throw new RuntimeException("文件记录不存在或无权限");
        }
        
        // 软删除
        uploadFile.setDeleteFlag(true);
        boolean success = this.updateById(uploadFile);
        
        if (success) {
            // TODO: 可以选择同时删除S3中的文件
            log.info("删除文件记录成功，记录ID: {}, 用户ID: {}", fileUploadId, userId);
        }
        
        return success;
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null) {
            return "";
        }
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        return lastSlashIndex >= 0 ? fileUrl.substring(lastSlashIndex + 1) : fileUrl;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex >= 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }
} 