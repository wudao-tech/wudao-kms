package com.wudao.kms.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

/**
 * 聊天文件上传DTO
 */
@Data
@Schema(description = "聊天文件上传DTO")
public class ChatFileUploadDTO {
    
    @Schema(description = "会话ID")
    private Long sessionId;
    
    @Schema(description = "上传的文件")
    private MultipartFile file;
    
    @Schema(description = "文件类型", allowableValues = {"image", "document"})
    private String fileType;
} 