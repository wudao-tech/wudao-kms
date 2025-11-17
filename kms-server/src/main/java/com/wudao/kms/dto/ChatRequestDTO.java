package com.wudao.kms.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 聊天请求DTO
 */
@Data
@Schema(description = "聊天请求DTO")  
public class ChatRequestDTO {
    
    @Schema(description = "会话ID")
    private Long sessionId;
    
    @Schema(description = "问题内容")
    private String question;
    
    @Schema(description = "是否联网", defaultValue = "false")
    private Boolean networkFlag = false;
    
    @Schema(description = "是否深度思考", defaultValue = "false")
    private Boolean deepThinkFlag = false;
    
    @Schema(description = "文件列表")
    private List<String> fileLists;
    
    @Schema(description = "知识库ID列表")  
    private List<Long> knowledgeBaseIds;
} 