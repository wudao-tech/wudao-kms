package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.wudao.kms.config.VectorTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QaImprove {
    private Long id;
    private String question;
    private String answer;
    @TableField(typeHandler = VectorTypeHandler.class)
    private float[] questionVector;
    private Long createBy;
    @TableField(exist = false)
    private String sourceName;
    private LocalDateTime createTime;
    private Long sourceId;
    @TableField(exist = false)
    private LocalDateTime startTime;
    @TableField(exist = false)
    private LocalDateTime endTime;
    private Long knowledgeId;
    private Long knowledgeSpaceId;
    @Schema(description = "审核状态 审核中reviewing/通过pass")
    private String reviewStatus;
    @Schema(description = "生成方式：upload/feedback/extra/manual")
    private String generateType;
    @TableField(exist = false)
    private String createByName;
    @Schema(description = "关联的反馈ID")
    private Long feedbackId;
}
