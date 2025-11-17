package com.wudao.kms.agent.mcp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工具树形结构响应VO
 *
 * @author wudao
 * @date 2024-12-20
 */
@Data
@Schema(description = "工具树形结构响应VO")
public class ToolTreeResponse {

    /** 节点ID */
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "节点ID")
    private Long id;

    /** 节点名称 */
    @Schema(description = "节点名称")
    private String name;

    /** 显示名称 */
    @Schema(description = "显示名称") 
    private String displayName;

    /** 描述信息 */
    @Schema(description = "描述信息")
    private String description;

    /** 节点类型：service-服务，tool-工具 */
    @Schema(description = "节点类型：service-服务，tool-工具")
    private String nodeType;

    /** 工具类型：MCP、BUILTIN、API（仅工具节点有效） */
    @Schema(description = "工具类型：MCP、BUILTIN、API")
    private String toolType;

    /** 工具分组（仅工具节点有效） */
    @Schema(description = "工具分组")
    private String toolGroup;

    /** HTTP方法（仅工具节点有效） */
    @Schema(description = "HTTP方法")
    private String method;

    /** 接口路径（仅工具节点有效） */
    @Schema(description = "接口路径")
    private String endpointUrl;

    /** 参数结构定义（仅工具节点有效） */
    @Schema(description = "参数结构定义(JSON格式)")
    private String parametersSchema;

    /** 包含关键词（仅工具节点有效） */
    @Schema(description = "包含关键词")
    private String includeKeywords;

    /** 排除关键词（仅工具节点有效） */
    @Schema(description = "排除关键词")
    private String excludeKeywords;

    /** 状态 */
    @Schema(description = "启用状态")
    private Boolean status;

    /** 服务基础URL（仅服务节点有效） */
    @Schema(description = "服务基础URL")
    private String baseUrl;

    /** 认证类型（仅服务节点有效） */
    @Schema(description = "认证类型")
    private String authType;

    /** 是否预设 */
    @Schema(description = "是否预设")
    private Boolean isPreset;

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

    /** 子节点列表 */
    @Schema(description = "子节点列表")
    private List<ToolTreeResponse> children;

    /** 额外信息 */
    @Schema(description = "额外信息")
    private String extra;

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long userId;

    /** 工具密钥（仅工具节点有效） */
    @Schema(description = "工具密钥")
    private String toolKey;

    /** 类型代码 */
    @Schema(description = "类型代码")
    private Integer type;

    /** 删除标志 */
    @Schema(description = "删除标志")
    private Boolean isDeleted;

    /** 服务器主机（仅服务节点有效） */
    @Schema(description = "服务器主机")
    private String serverHost;

    /** API密钥（仅服务节点有效） */
    @Schema(description = "API密钥")
    private String apiKey;

    /** 认证方式（仅服务节点有效） */
    @Schema(description = "认证方式")
    private Integer authMethod;

    /** 认证类型字符串（仅服务节点有效） */
    @Schema(description = "认证类型字符串")
    private String authTypeStr;
} 