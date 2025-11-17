package com.wudao.kms.agent.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "知识库搜索结果DTO")
public class KnowledgeSearchResult {
    @Schema(description = "段落ID")
    private String id;
    
    @Schema(description = "文档ID")
    @JsonProperty("document_id")
    private Long documentId;
    
    @Schema(description = "知识库ID")
    @JsonProperty("knowledge_base_id")
    private Long knowledgeBaseId;
    
    @Schema(description = "分段ID")
    @JsonProperty("chunk_id")
    private Long chunkId;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "摘要")
    private String summary;
    
    @Schema(description = "标签")
    private List<String> tags;
    
    @Schema(description = "S3文件URL")
    @JsonProperty("s3_url")
    private String s3Url;
    
    @Schema(description = "文件类型")
    @JsonProperty("file_type")
    private String fileType;
    
    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "重排分数")
    @JsonProperty("rerank_score")
    private Double rerankScore;
    
    @Schema(description = "相关度分数 1-10 1颗星 10-20 2颗星 20-40 3颗星 40-80 4颗星 >80 5颗星")
    private Double score;
    
    @Schema(description = "搜索类型")
    @JsonProperty("search_type")
    private String searchType;
    
    @Schema(description = "语义搜索分数")
    @JsonProperty("semantic_score")
    private Double semanticScore;
    
    @Schema(description = "全文搜索分数")
    @JsonProperty("fulltext_score")
    private Double fulltextScore;
    
    @Schema(description = "组合分数")
    @JsonProperty("combined_score")
    private Double combinedScore;

    @Schema(description = "知识空间ID")
    @JsonProperty("knowledge_space_id")
    private Long knowledgeSpaceId;
    
    @Schema(description = "高亮信息")
    private String highlight;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "图片信息")
    @JsonProperty("image_info")
    private SearchImg imageInfo;

    @Schema(description = "预览S3 URL")
    private String previewS3Url;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchImg{
        private String description;
        private String name;
        private String url;
    }
}
