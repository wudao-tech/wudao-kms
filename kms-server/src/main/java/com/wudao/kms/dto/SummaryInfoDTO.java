package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页汇总信息DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "首页汇总信息DTO")
public class SummaryInfoDTO {

    @Schema(description = "文件总数")
    private Long totalFiles;

    @Schema(description = "今日新增文件数")
    private Long todayNewFiles;

    @Schema(description = "热搜内容列表")
    private List<HotSearchItem> hotSearchList;

    @Schema(description = "搜索历史列表")
    private List<String> historyList;

    @Data
    @Accessors(chain = true)
    @Schema(description = "热搜项")
    public static class HotSearchItem {
        @Schema(description = "序号")
        private Integer index;

        @Schema(description = "内容")
        private String content;

        @Schema(description = "搜索次数")
        private Long searchCount;
    }
} 