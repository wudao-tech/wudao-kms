package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 助手工具DTO
 */
@Data
@Schema(description = "助手工具DTO")
public class AssistantTool {
    
    @Schema(description = "组件ID")
    private String componentId;
    
    @Schema(description = "组件名称")
    private String name;
    
    @Schema(description = "组件类型")
    private String type;
}