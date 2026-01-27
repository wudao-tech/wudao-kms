package com.wudao.kms.dify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Dify检索设置
 */
@Data
@Schema(description = "Dify检索设置")
public class DifyRetrievalSetting {

    @JsonProperty("top_k")
    @Schema(description = "返回结果的最大数量", example = "5")
    private Integer topK = 5;

    @JsonProperty("score_threshold")
    @Schema(description = "相关性分数阈值(0-1)", example = "0.5")
    private Double scoreThreshold = 0.5;
}
