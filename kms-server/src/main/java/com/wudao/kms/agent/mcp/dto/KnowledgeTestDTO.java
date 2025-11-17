package com.wudao.kms.agent.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "知识库测试查询DTO")
public class KnowledgeTestDTO {
    @Schema(description = "查询内容")
    private String query;
    @Schema(description = "知识库ID")
    private List<Integer> knowledgeId;
    @Schema(description = "语义semantic 全文fulltext 混合hybrid")
    private String searchType;
    @Schema(description = "文档Id列表")
    private List<Long> documentIds;
    @Schema(description = "最大召回分段")
    private Integer maxSegmentCount;
    @Schema(description = "知识空间ID列表")
    private List<Long> knowledgeBaseIds;
    @Schema(description = "更新的时间, ONE_DAY,ONE_MONTHS.THREE_MONTHS,HALF_YEAR,ONE_YEAR,ALL")
    private String updateTime;
    @Schema(description = "文件类型，由后端传入md,txt,pdf等")
    private String fileType;
    @Schema(description = "重排模型名称")
    private String reRank;
}
