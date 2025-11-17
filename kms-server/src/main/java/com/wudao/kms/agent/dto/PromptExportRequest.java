package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 导出提示词请求DTO
 */
@Data
@Schema(description = "导出提示词请求")
public class PromptExportRequest {
    
    @Schema(description = "导出的提示词ID列表（为空则导出全部）")
    private List<Long> ids;
    
    @Schema(description = "导出格式：json, csv, excel")
    private String format = "json";
    
    @Schema(description = "提示词类型过滤：official-官方，personal-个人")
    private String type;
    
    @Schema(description = "是否公开过滤：0-私有，1-公开")
    private Integer isPublic;
}