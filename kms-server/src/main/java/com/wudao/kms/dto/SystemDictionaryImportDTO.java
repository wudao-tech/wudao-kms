package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 系统词库批量导入DTO
 */
@Data
@Schema(description = "系统词库批量导入DTO")
public class SystemDictionaryImportDTO {

    @Schema(description = "词库类型：PROPER_NOUN-专有名词，SYNONYM-同义词，CORRECTION-纠错词，SENSITIVE-敏感词，SUGGESTION-搜索联想词")
    private String dictType;

    @Schema(description = "词库数据列表")
    private List<DictItem> dictItems;

    private Long spaceId;

    @Data
    @Schema(description = "词库项")
    public static class DictItem {
        @Schema(description = "词库键")
        private String dictKey;

        @Schema(description = "词库值")
        private String dictValue;

        @Schema(description = "排序")
        private Integer sortOrder;
    }
} 