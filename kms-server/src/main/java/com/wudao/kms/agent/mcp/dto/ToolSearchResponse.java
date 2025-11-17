package com.wudao.kms.agent.mcp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工具搜索响应VO
 *
 * @author wudao
 * @date 2024-12-20
 */
@Data
@Schema(description = "工具搜索响应VO")
public class ToolSearchResponse {

    /** 工具ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "工具ID")
    private Long toolId;

    /** 工具名称 */
    @Schema(description = "工具唯一名称")
    private String toolName;

    /** 工具显示名称 */
    @Schema(description = "工具显示名称")
    private String displayName;

    /** 工具描述 */
    @Schema(description = "工具描述")
    private String description;

    /** 工具类型 */
    @Schema(description = "工具类型：MCP、BUILTIN、API")
    private String toolType;

    /** 工具分组 */
    @Schema(description = "工具分组")
    private String toolGroup;

    /** HTTP方法 */
    @Schema(description = "HTTP方法")
    private String method;

    /** 接口路径 */
    @Schema(description = "接口路径")
    private String endpointUrl;

    /** 参数结构定义 */
    @Schema(description = "参数结构定义(JSON格式)")
    private String parametersSchema;

    /** 包含关键词 */
    @Schema(description = "包含关键词")
    private String includeKeywords;

    /** 排除关键词 */
    @Schema(description = "排除关键词")
    private String excludeKeywords;

    /** 工具状态 */
    @Schema(description = "工具启用状态")
    private Boolean toolStatus;

    /** 服务ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "服务ID")
    private Long serviceId;

    /** 服务名称 */
    @Schema(description = "服务名称")
    private String serviceName;

    /** 服务显示名称 */
    @Schema(description = "服务显示名称")
    private String serviceDisplayName;

    /** 服务描述 */
    @Schema(description = "服务描述")
    private String serviceDescription;

    /** 服务状态 */
    @Schema(description = "服务启用状态")
    private Boolean serviceStatus;

    /** 服务基础URL */
    @Schema(description = "服务基础URL")
    private String baseUrl;

    /** 创建者 */
    @Schema(description = "创建者")
    private Long createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新者 */
    @Schema(description = "更新者")
    private Long updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
} 