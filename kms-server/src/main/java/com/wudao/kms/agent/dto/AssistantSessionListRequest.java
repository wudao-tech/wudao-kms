package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 助手会话列表查询请求DTO
 */
@Data
@Schema(description = "助手会话列表查询请求")
public class AssistantSessionListRequest {
    
    @Schema(description = "助手UUID")
    private String assistantUuid;
    
    @Schema(description = "会话名称，支持模糊查询")
    private String sessionName;
    
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "排序字段", example = "createTime")
    private String orderBy;
    
    @Schema(description = "排序方向，asc或desc", example = "desc")
    private String sortDirection = "desc";

    @Schema(description = "创建人", example = "admin")
    private String createBy; // 创建人

    private String sourceType; // 来源类型，如 web、mobile 等
} 