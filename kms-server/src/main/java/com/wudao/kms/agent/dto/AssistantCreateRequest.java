package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 创建助手请求DTO
 */
@Data
public class AssistantCreateRequest {
    private String name;
    private String prompt;
    private String logo;
    @Schema(description = "助手类型：properties-配置，workflow-工作流")
    private String type;
    @Schema(description = "助手描述")
    private String description;
    @Schema(description = "标签")
    private List<String> tags;
    @Schema(description = "权限：1-公开，2-私有")
    private Integer permission;
    @Schema(description = "来源：官方/自定义/收藏")
    private String source;
    @Schema(description = "业务环节")
    private String subjectId;
} 