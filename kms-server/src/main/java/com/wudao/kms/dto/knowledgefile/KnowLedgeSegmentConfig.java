package com.wudao.kms.dto.knowledgefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Data
public class KnowLedgeSegmentConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 分段方式
     * 可选值：none(不分段), sentence(按句子), paragraph(按段落), fixed_length(固定长度), custom(自定义)
     */
    private String splitMethod;
    
    /**
     * 最大长度
     * 当split_method为fixed_length时，指定每段的最大字符数
     * 当split_method为none时，通常设置为文本总长度
     */
    private Integer maxLength;
    
    /**
     * 重叠字符数
     * 段落间重叠的字符数，用于保持上下文连贯性
     */
    @JsonProperty("overlap")
    private Integer overlap = 0;
    
    // 兼容旧版本字段（可选）
    private String segmentMethod;
    private String customSegmentType;
    private Integer customSegmentSize;
    private Integer customSegmentOverlap;
    @Schema(description = "QA提取")
    private Boolean qaExtract = false;
    @Schema(description = "pdf增加处理")
    private Boolean pdfIncrease = false;
    private Boolean vlFlag = false;
    @Schema(description = "智能摘要")
    private Boolean summary;
    @Schema(description = "智能标签")
    private Boolean tags;
    @Schema(description = "最大段落深度")
    private Integer maxParagraph;
    @Schema(description = "分段类型")
    private String segmentType;
    @Schema(description = "解析变更")
    private Boolean extraChangeFlag = false;
    @Schema(description = "生成标题索引")
    private Boolean generateTitleIndex = false;
    @Schema(description = "QA解析的Prompt")
    private String qaExtractPrompt;
}