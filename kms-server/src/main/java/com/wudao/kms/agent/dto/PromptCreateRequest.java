package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 创建提示词请求DTO
 */
@Data
@Schema(description = "创建提示词请求")
public class PromptCreateRequest {
    
    @NotBlank(message = "提示词名称不能为空")
    @Schema(description = "提示词名称")
    private String name;
    
    @NotBlank(message = "提示词类型不能为空")
    @Schema(description = "提示词类型：official-官方，personal-个人")
    private String type;
    
    @NotBlank(message = "提示词内容不能为空")
    @Schema(description = "提示词内容")
    private String content;
    
    @Schema(description = "提示词描述")
    private String description;
    
    @Schema(description = "提示词标签")
    private String tags;
    
    @Schema(description = "是否公开：0-私有，1-公开")
    private Integer isPublic = 0;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status = 1;
    
    @Schema(description = "备注")
    private String remark;
}