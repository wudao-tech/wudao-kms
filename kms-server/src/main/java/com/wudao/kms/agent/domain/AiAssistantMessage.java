package com.wudao.kms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AI助手消息实体类，支持版本化管理
 * 用于记录消息的基本信息，支持重新生成和编辑功能
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "ai_assistant_message_v2", autoResultMap = true)
@Schema(description = "AI助手消息实体类(版本化)")
public class AiAssistantMessage extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "会话ID")
    private String sessionId;
    
    @Schema(description = "助手UUID")
    private String assistantUuid;
    
    @Schema(description = "消息版本号，用于支持重新生成和编辑")
    private Long messageVersion;
    
    @Schema(description = "父消息ID，用于版本链管理")
    private Long parentMessageId;
    
    @Schema(description = "原始用户输入内容")
    private String originalContent;
    
    @Schema(description = "轮次索引（用于多轮对话）")
    private Integer roundIndex;
    
    @Schema(description = "消息状态: PENDING, COMPLETED, ERROR, REGENERATING, EDITING")
    private String status;
    
    @Schema(description = "是否为当前活跃版本")
    private Boolean isActiveVersion;
    
    @Schema(description = "版本类型: ORIGINAL-原始, REGENERATED-重新生成, EDITED-编辑")
    private String versionType;
    
    @Schema(description = "版本描述或编辑原因")
    private String versionDescription;
    
    @Schema(description = "相关文件列表")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> attachments;
    
    @Schema(description = "工具调用配置")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> enabledTools;
    
    @Schema(description = "是否删除")
    private Boolean delFlag = false;
}
