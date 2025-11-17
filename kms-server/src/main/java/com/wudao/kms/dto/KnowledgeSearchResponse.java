package com.wudao.kms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "知识库搜索响应DTO")
public class KnowledgeSearchResponse {
    @Schema(description = "查询内容")
    private String query;
    
    @Schema(description = "搜索类型")
    @JsonProperty("search_type")
    private String searchType;
    
    @Schema(description = "温度参数")
    private Double temperature;
    
    @Schema(description = "总结果数")
    @JsonProperty("total_results")
    private Integer totalResults;
    
    @Schema(description = "搜索结果列表")
    private List<KnowledgeSearchResult> results;
    
    @Schema(description = "搜索耗时(毫秒)")
    @JsonProperty("took_ms")
    private Long tookMs;

    @Schema(description = "结果重排功能")
    @JsonProperty("rerank_enabled")
    private Boolean rerankEnabled;

    @Schema(description = "重排耗时")
    @JsonProperty("rerank_took_ms")
    private Long rerankTookMs;
} 