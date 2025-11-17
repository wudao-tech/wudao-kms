package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 提示词列表查询请求DTO
 */
@Data
@Schema(description = "提示词列表查询请求")
public class PromptListRequest {
    
    @Schema(description = "页码")
    private Integer pageNum = 1;
    
    @Schema(description = "每页大小")
    private Integer pageSize = 10;
    
    @Schema(description = "提示词名称（模糊查询）")
    private String name;
    
    @Schema(description = "提示词类型：official-官方，personal-个人")
    private String type;
    
    @Schema(description = "是否公开：0-私有，1-公开")
    private Integer isPublic;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "创建用户ID")
    private Long userId;
}