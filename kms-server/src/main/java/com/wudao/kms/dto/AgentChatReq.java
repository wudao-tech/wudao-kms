package com.wudao.kms.dto;

import com.wudao.kms.agent.domain.Assistant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AgentChatReq {
    private String agentUuid;
    private String query;
    private String sessionUuid;
    private Boolean deepThinking = false;
    private Boolean webSearch = false;
    private Long userId;
    private String chatUuid;
    @Schema(hidden = true)
    private Assistant assistant;
}
