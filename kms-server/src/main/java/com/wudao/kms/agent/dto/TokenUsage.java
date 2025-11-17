package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Token使用情况
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token使用情况")
public class TokenUsage {
    
    @Schema(description = "输入Token数量")
    private Integer inputTokens;
    
    @Schema(description = "输出Token数量")
    private Integer outputTokens;
    
    @Schema(description = "总 Token数量")
    private Integer totalTokens;
    
    @Schema(description = "Token成本")
    private BigDecimal tokenCost;
    
    /**
     * 计算总Token数
     */
    public Integer calculateTotalTokens() {
        int input = inputTokens != null ? inputTokens : 0;
        int output = outputTokens != null ? outputTokens : 0;
        return input + output;
    }
    
    /**
     * 检查是否有Token消耗
     */
    public boolean hasTokenUsage() {
        return (inputTokens != null && inputTokens > 0) || 
               (outputTokens != null && outputTokens > 0);
    }
}
