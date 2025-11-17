package com.wudao.kms.llm.message.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import com.wudao.kms.dto.KnowledgeSearchResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "agent_message", autoResultMap = true)
public class AgentMessage extends BaseEntity {
    private Long id;
    @Schema(description = "消息uuid")
    private String chatUuid;
    private String sessionUuid;
    private String userMessage;
    private String agent;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<KnowledgeSearchResult> quoteList;
    @Schema(description = "反馈类型 good bad")
    private String feedbackType;
}
