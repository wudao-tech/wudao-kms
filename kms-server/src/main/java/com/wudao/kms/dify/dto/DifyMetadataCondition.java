package com.wudao.kms.dify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Dify元数据过滤条件
 */
@Data
@Schema(description = "Dify元数据过滤条件")
public class DifyMetadataCondition {

    @JsonProperty("logical_operator")
    @Schema(description = "逻辑运算符: and/or", example = "and")
    private String logicalOperator = "and";

    @Schema(description = "过滤条件列表")
    private List<DifyCondition> conditions;

    @Data
    @Schema(description = "单个过滤条件")
    public static class DifyCondition {

        @Schema(description = "字段名称")
        private String name;

        @JsonProperty("comparison_operator")
        @Schema(description = "比较运算符: contains, not contains, start with, end with, is, is not, empty, not empty, =, !=, >, <, >=, <=, before, after")
        private String comparisonOperator;

        @Schema(description = "比较值")
        private Object value;
    }
}
