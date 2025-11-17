package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;

@Data
@Schema(description = "知识库测试查询DTO")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnowledgeTestDTO {
    @Schema(description = "查询内容")
    private String query;
    @Schema(description = "知识库ID")
    private List<Long> knowledgeIds;
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
    @Schema(description = "重排类型")
    private String rerankModel;
    @Schema(description = "最大字数")
    private Integer maxCount;
    @Schema(description = "入口类型 knowledge_test, search")
    private String entranceType;
    @Schema(description = "搜索来源：test_search-测试搜索，vector_search-向量搜索")
    private String source;
    @Schema(description = "模型")
    private String model;
    private float[] vector;
    private Boolean webSearch;
    private Boolean deepThinking;
    private Boolean multimodal;
    private String sessionUuid;
    private Long userId;
    private String chatUuid;
    private String agentUuid;
    private Integer rerankTopK;
    private Double rerankScore;
    private Double ragScore;
    private Integer ragTopK;
    private Boolean isRerank = false;
}
