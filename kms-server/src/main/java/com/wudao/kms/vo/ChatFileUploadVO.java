package com.wudao.kms.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 聊天文件上传VO
 */
@Data
@Schema(description = "聊天文件上传VO")
public class ChatFileUploadVO {
    
    @Schema(description = "文件上传记录ID")
    private Long id;
    
    @Schema(description = "会话ID")
    private Long sessionId;
    
    @Schema(description = "原始文件名")
    private String originalFileName;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "文件分类")
    private String fileCategory;
    
    @Schema(description = "上传时间")
    private LocalDateTime createdAt;
} 