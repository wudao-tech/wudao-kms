package com.wudao.kms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提示词模板实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ai_prompt")
@Schema(description = "提示词模板")
public class Prompt extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "提示词名称")
    private String name;
    
    @Schema(description = "提示词类型：official-官方，personal-个人")
    private String type;
    
    @Schema(description = "提示词内容")
    private String content;
    
    @Schema(description = "提示词描述")
    private String description;
    
    @Schema(description = "提示词标签")
    private String tags;
    
    @Schema(description = "创建用户ID")
    private Long userId;
    
    @Schema(description = "是否公开：0-私有，1-公开")
    private Integer isPublic;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "是否删除：0-未删除，1-已删除")
    private Integer isDelete;
}