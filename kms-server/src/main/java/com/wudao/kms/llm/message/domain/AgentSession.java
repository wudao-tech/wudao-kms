package com.wudao.kms.llm.message.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "agent_session",autoResultMap = true)
public class AgentSession extends BaseEntity {
    private Long id;
    private String sessionName;
    private String uuid;
    private String agentUuid;
}
