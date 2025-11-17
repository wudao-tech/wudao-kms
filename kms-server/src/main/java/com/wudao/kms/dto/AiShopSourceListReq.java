package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AiShopSourceListReq {
    private Integer pageNum;
    private Integer pageSize;
    private String name;
    private String source;
    private String subjectId;
    @Schema(description = "排序项列表")
    private List<OrderItem> orderBy;
    private String assistantType;
    @Schema (description = "来源类型 0自定义 1官方")
    private Integer sourceType;

    @Data
    @Schema(description = "排序项")
    public static class OrderItem{
        @Schema(description = "排序字段")
        private String field;
        @Schema(description = "排序方式：asc-升序，desc-降序")
        private String type;
    }
}
