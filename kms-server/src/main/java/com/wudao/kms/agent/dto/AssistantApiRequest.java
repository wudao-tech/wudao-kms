package com.wudao.kms.agent.dto;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 助手api请求
 * @author wudao
 * @date 2025-07-08
 */
@Data
public class AssistantApiRequest {
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "消息")
    private String message;
    @Schema(description = "会话id")
    private String sessionUuid;
    @Schema(description = "助手id")
    private String assistantUuid;
    @Schema(description = "线程id")
    private String threadId;
    @Schema(description = "文件")
    private List<Media> file;
    @Schema(description = "恢复对象")
    private JSONObject resumParam;
    @Schema(description = "初始参数")
    private Map<String, Object> initialParams;
    @Schema(description = "是否启用搜索")
    private Boolean enableSearch = true; // 是否启用搜索
    @Schema(description = "是否启用深度聊天")
    private Boolean enableDeepChat = false; // 是否启用深度聊天
    
    // 重新生成相关参数
    @Schema(description = "是否为重新生成请求")
    private Boolean isRegenerate = false;
    @Schema(description = "要重新生成的消息ID（用于重新生成时指定哪条助手回复需要重新生成）- 兼容旧版本")
    private Long regenerateMessageId;
    @Schema(description = "要重新生成的节点ID（新版本树状结构使用）")
    private String regenerateNodeId;
    @Schema(description = "重新生成的原因描述")
    private String regenerateReason;
    @Schema(description = "编辑后的用户输入（用于编辑用户消息后重新生成）")
    private String editedMessage;
    @Schema(description = "编辑的节点ID（用于指定要编辑的用户消息节点）")
    private String editNodeId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Media{
        @Schema(description = "媒体类型")
        private String mediaType;
        @Schema(description = "媒体url")
        private String mediaUrl;
    }
}
