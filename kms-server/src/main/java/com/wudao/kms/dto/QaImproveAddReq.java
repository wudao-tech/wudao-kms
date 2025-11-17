package com.wudao.kms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class QaImproveAddReq {
    private List<Long> feedbackIds;
    private Long knowledgeId;
    private Long knowledgeSpaceId;
    @Schema(description = "删除标识，用于添加优化后自动删除")
    private Boolean delAfterUse = false;
}
