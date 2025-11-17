package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI助手列表查询请求
 */
@Data
@Schema(description = "AI助手列表查询请求")
public class AssistantListRequest {
    @Schema(description = "助手名称，支持模糊查询")
    private String name;
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
    @Schema(description = "助手类型： properties、workflow、agent", example = "properties")
    private String type;
    @Schema(description = "排序字段", example = "createTime")
    private String orderBy;
    @Schema(description = "排序方向，asc或desc", example = "desc")
    private String sortDirection;
    @Schema(description = "上下架筛选")
    private String shelvesStatus;
    private Integer status;
    @Schema(description = "只看我")
    private Boolean onlyMe = false;
    private String subjectId;
    private Integer permission;
    @Schema(description = "推荐")
    private Boolean recommend;
    @Schema(description = "来源 official官方/myself我创建/collected我收藏")
    private String source;
} 