package com.wudao.kms.agent.domain;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.kms.agent.enums.TempMemoryEnums;
import com.wudao.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "ai_assistant_memory", autoResultMap = true)
public class AiAssistantMemory extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,Object> keyValue;
    private String agentUuid;
    private String sessionUuid;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private TempMemoryEnums tempMemory;
    private String tableName;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray tableData;
}
