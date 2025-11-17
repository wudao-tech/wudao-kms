package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "助手快捷指令")
public class AssistantQuickCommandDTO {
    @Schema(description = "显示")
    private String title;
    @Schema(description = "描述")
    private String desc;
    @Schema(description = "icon")
    private String icon;
}
