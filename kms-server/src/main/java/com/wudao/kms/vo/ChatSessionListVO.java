package com.wudao.kms.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 聊天会话列表VO
 */
@Data
@Schema(description = "聊天会话列表VO")
public class ChatSessionListVO {
    
    @Schema(description = "今天的会话")
    private List<ChatSessionVO> todaySessions;
    
    @Schema(description = "七日内的会话")
    private List<ChatSessionVO> weekSessions;
} 