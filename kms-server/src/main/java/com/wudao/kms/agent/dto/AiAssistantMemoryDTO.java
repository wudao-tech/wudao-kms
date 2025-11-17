package com.wudao.kms.agent.dto;

import com.wudao.kms.agent.enums.TempMemoryEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * ai助手创建记忆变量
 */
@Data
@Schema(description = "AI助手记忆变量")
public class AiAssistantMemoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "名称")
    private String key;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "默认值",nullable = true)
    private String value;
    @Schema(description = "临时记忆")
    private TempMemoryEnums tempMemory;
}
