package com.wudao.kms.agent.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MCP工具查询请求DTO
 *
 * @author wudao
 * @date 2024-12-20
 */
@Data
@Schema(description = "MCP工具查询请求DTO")
public class McpToolQueryRequest {

    /** 所属服务ID */
    @Schema(description = "所属服务ID")
    private Long serviceId;

    /** 工具名称（模糊查询） */
    @Schema(description = "工具名称（支持模糊查询）")
    private String toolName;

    /** 显示名称（模糊查询） */
    @Schema(description = "显示名称（支持模糊查询）")
    private String displayName;

    /** 工具类型 */
    @Schema(description = "工具类型：MCP、BUILTIN、API")
    private String toolType;

    /** 工具分组 */
    @Schema(description = "工具分组")
    private String group;

    /** 状态 */
    @Schema(description = "启用状态：true=启用，false=禁用")
    private Boolean status;

    /** 页码 */
    @Schema(description = "页码，从1开始")
    private Integer pageNum = 1;

    /** 页大小 */
    @Schema(description = "每页记录数")
    private Integer pageSize = 10;

    /** 排序字段 */
    @Schema(description = "排序字段")
    private String orderBy = "createTime";

    /** 排序方向 */
    @Schema(description = "排序方向：asc升序、desc降序")
    private String sortDirection = "desc";
} 