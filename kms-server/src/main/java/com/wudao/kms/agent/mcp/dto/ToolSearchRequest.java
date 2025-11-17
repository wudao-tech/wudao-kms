package com.wudao.kms.agent.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 工具搜索请求DTO
 *
 * @author wudao
 * @date 2024-12-20
 */
@Data
@Schema(description = "工具搜索请求DTO")
public class ToolSearchRequest {

    /** 服务名称（模糊搜索） */
    @Schema(description = "服务名称，支持模糊查询")
    private String serviceName;

    /** 工具名称（模糊搜索） */
    @Schema(description = "工具名称，支持模糊查询")
    private String toolName;

    /** 工具显示名称（模糊搜索） */
    @Schema(description = "工具显示名称，支持模糊查询")
    private String displayName;

    /** 工具描述（模糊搜索） */
    @Schema(description = "工具描述，支持模糊查询")
    private String description;

    /** 参数信息（模糊搜索） */
    @Schema(description = "参数信息，支持模糊查询")
    private String parameters;

    /** 工具类型 */
    @Schema(description = "工具类型：MCP、BUILTIN、API")
    private String toolType;

    /** 工具分组 */
    @Schema(description = "工具分组")
    private String toolGroup;

    /** 状态 */
    @Schema(description = "启用状态：true=启用，false=禁用")
    private Boolean status;

    /** 关键词搜索（综合搜索） */
    @Schema(description = "关键词搜索，会在工具名称、描述、参数等字段中搜索")
    private String keyword;

    /** 页码 */
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    /** 每页大小 */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    /** 排序字段 */
    @Schema(description = "排序字段", example = "updateTime")
    private String orderBy = "updateTime";

    /** 排序方向 */
    @Schema(description = "排序方向，asc或desc", example = "desc")
    private String sortDirection = "desc";
} 