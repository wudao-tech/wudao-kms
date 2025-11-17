package com.wudao.kms.agent.dto;

import com.wudao.kms.llm.llmmode.domain.LLMModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "测试大模型的能力")
public class LLMModelTestReq {
    @Schema(description = "会话UUID,保留记忆")
    private String sessionUuid;
    @Schema(description = "提示词")
    private String prompt;
    @Schema(description = "文件列表")
    private List<String> files;
    @Schema(description = "大模型ID")
    private Long llmModelId;
    @Schema(description = "大模型信息")
    private LLMModel llmModel;
    @Schema(description = "是否启用搜索功能")
    private Boolean enableSearch = false;
    @Schema(description = "深度思考开关")
    private Boolean enableThinking = false;
    @Schema(description = "系统提示词")
    private String systemPrompt;
}
