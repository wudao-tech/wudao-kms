package com.wudao.kms.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天会话问答VO
 */
@Data
@Schema(description = "聊天会话问答VO")
public class ChatSessionQaVO {
    
    @Schema(description = "问答ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "会话ID")
    private Long sessionId;
    
    @Schema(description = "问题")
    private String question;
    
    @Schema(description = "答案")
    private String answer;
    
    @Schema(description = "文件列表")
    private List<String> fileLists;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "推荐的知识库列表")
    private List<String> recommendKnowledgeBases;
} 