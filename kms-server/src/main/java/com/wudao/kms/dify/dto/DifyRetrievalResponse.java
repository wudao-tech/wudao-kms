package com.wudao.kms.dify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dify外部知识库检索响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dify外部知识库检索响应")
public class DifyRetrievalResponse {

    @Schema(description = "检索结果记录列表")
    private List<DifyRecord> records;

    public static DifyRetrievalResponse of(List<DifyRecord> records) {
        return new DifyRetrievalResponse(records);
    }
}
