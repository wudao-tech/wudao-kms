package com.wudao.kms.agent.dto;

import com.wudao.kms.agent.domain.AssistantSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 助手会话响应DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "助手会话响应")
public class AssistantSessionResponse extends AssistantSession {
    
    @Schema(description = "助手名称")
    private String assistantName;
    
    @Schema(description = "助手描述")
    private String assistantDesc;
    
    @Schema(description = "助手引导词")
    private String guideWord;
    
    @Schema(description = "助手引导问题列表")
    private List<String> guideQuestions;
    
    @Schema(description = "最近消息数量")
    private Integer messageCount;
    
    @Schema(description = "最后消息时间")
    private String lastMessageTime;
} 