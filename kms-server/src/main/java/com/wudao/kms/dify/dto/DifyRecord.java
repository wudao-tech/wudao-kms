package com.wudao.kms.dify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Dify检索结果记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dify检索结果记录")
public class DifyRecord {

    @Schema(description = "文本内容")
    private String content;

    @Schema(description = "相关性分数(0-1)")
    private Double score;

    @Schema(description = "文档标题")
    private String title;

    @Schema(description = "元数据信息")
    private Map<String, Object> metadata;
}
