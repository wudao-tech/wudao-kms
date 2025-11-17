package com.wudao.kms.agent.dto;

import com.alibaba.fastjson.JSONArray;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 记忆查询响应
 *
 * @author wudao
 * @date 2025-08-27
 */
@Data
@Schema(description = "记忆查询响应")
public class MemoryQueryResponse {

    @Schema(description = "记忆类型：variable(变量) 或 table(记忆表)")
    private String type;

    @Schema(description = "记忆变量数据，当type为variable时返回")
    private Map<String, Object> variableData;

    @Schema(description = "记忆表数据，当type为table时返回")
    private Map<String, TableMemoryData> tableData;

    @Data
    @Schema(description = "表记忆数据")
    public static class TableMemoryData {
        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "存储类型：永久 或 会话")
        private String storageType;

        @Schema(description = "记录数量")
        private Integer recordCount;

        @Schema(description = "表数据")
        private JSONArray data;
    }
}