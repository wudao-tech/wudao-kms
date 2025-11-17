package com.wudao.kms.entity.chat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 聊天文件上传记录
 */
@Data
@TableName("chat_file_upload")
@Schema(description = "聊天文件上传记录")
public class ChatFileUpload extends BaseEntity {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "会话ID")
    private Long sessionId;
    
    @Schema(description = "原始文件名")
    private String originalFileName;
    
    @Schema(description = "存储文件名")
    private String storageFileName;
    
    @Schema(description = "文件路径")
    private String filePath;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "MIME类型")
    private String mimeType;
    
    @Schema(description = "文件分类：image-图片，document-文档")
    private String fileCategory;
    
    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;
} 