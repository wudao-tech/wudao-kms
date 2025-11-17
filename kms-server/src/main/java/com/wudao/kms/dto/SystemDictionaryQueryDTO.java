package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统词库查询DTO
 */
@Data
@Schema(description = "系统词库查询DTO")
public class SystemDictionaryQueryDTO {

    @Schema(description = "词库类型：PROPER_NOUN-专有名词，SYNONYM-同义词，CORRECTION-纠错词，SENSITIVE-敏感词，SUGGESTION-搜索联想词")
    private String dictType;

    @Schema(description = "词库键")
    private String dictKey;

    @Schema(description = "词库值")
    private String dictValue;

    @Schema(description = "状态：1-启用，0-禁用")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDate startTime;

    @Schema(description = "结束时间")
    private LocalDate endTime;

    @Schema(description = "空间id")
    private Long spaceId;
} 