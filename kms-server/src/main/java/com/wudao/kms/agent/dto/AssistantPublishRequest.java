package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "助手发布操作")
public class AssistantPublishRequest {
    @Schema(description = "助手ID")
    private String assistantUuid;
    @Schema(description = "操作类型")
    private String type;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "封面")
    private String cover;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "标签")
    private List<String> tags;
    @Schema(description = "是否官方")
    private Boolean isPublic;
    @Schema(description = "行业")
    private String tradeId;
    @Schema(description = "主题")
    private String subjectId;

}
