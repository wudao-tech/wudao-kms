package com.wudao.kms.dto;

import com.wudao.kms.entity.KnowledgeBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KnowledgeBaseQueryDTO extends KnowledgeBase {

    @Schema(description = "用户ID，用于权限过滤")
    private Long userId;

    @Schema(description = "用户角色，用于权限过滤")
    private String userRole;

    @Schema(description = "我创建的")
    private Boolean onlyMe;
}
