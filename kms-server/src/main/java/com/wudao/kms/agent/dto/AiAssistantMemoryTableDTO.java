package com.wudao.kms.agent.dto;

import com.wudao.kms.agent.enums.MemoryTableFieldEnums;
import com.wudao.kms.agent.enums.TempMemoryEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class AiAssistantMemoryTableDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "表名")
    private String tableName;
    @Schema(description = "表描述")
    private String tableDescription;
    @Schema(description = "用户表数据记忆时常")
    private TempMemoryEnums tempMemory;
    private List<TableField> tableFields;

    @Data
    public static class TableField {
        @Schema(description = "字段名称")
        private String fieldName;
        @Schema(description = "字段描述")
        private String fieldDescription;
        @Schema(description = "字段类型")
        private MemoryTableFieldEnums fieldType;
        @Schema(description = "是否必参")
        private Boolean required = false;
    }
}
