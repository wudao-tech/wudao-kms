package com.wudao.kms.agent.dto;

import com.wudao.kms.agent.enums.QueryFollowUpTypeEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "助手追问dto")
public class AssistantQueryFollowUpDTO {
    @Schema(description = "类型")
    private QueryFollowUpTypeEnums type;
    @Schema(description = "模型")
    private String model;
    @Schema(description = "会话数")
    private Integer dialogNumber;
    @Schema(description = "最大输出token")
    private Integer maxOutputToken;
    @Schema(description = "追问prompt")
    private String flowPrompt;
}
