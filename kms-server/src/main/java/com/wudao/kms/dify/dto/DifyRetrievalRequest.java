package com.wudao.kms.dify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Dify外部知识库检索请求
 */
@Data
@Schema(description = "Dify外部知识库检索请求")
public class DifyRetrievalRequest {

    @NotBlank(message = "知识库ID不能为空")
    @JsonProperty("knowledge_id")
    @Schema(description = "知识库唯一标识")
    private String knowledgeId;

    @NotBlank(message = "查询内容不能为空")
    @Schema(description = "用户的搜索问题")
    private String query;

    @NotNull(message = "检索设置不能为空")
    @JsonProperty("retrieval_setting")
    @Schema(description = "检索配置")
    private DifyRetrievalSetting retrievalSetting;

    @JsonProperty("metadata_condition")
    @Schema(description = "元数据过滤条件（可选）")
    private DifyMetadataCondition metadataCondition;

    @Schema(description = "用户id，由token获取")
    private Long userId;
}
