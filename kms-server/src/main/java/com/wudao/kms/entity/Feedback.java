package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("ai_feedback")
public class Feedback{
    private Long id;
    @Schema(description = "反馈类型，agree disagree")
    private String type;
    private String userMessage;
    private String agent;
    @TableField(
            fill = FieldFill.INSERT
    )
    private Long createdBy;
    @TableField(
            fill = FieldFill.INSERT
    )
    private LocalDateTime createTime;
    @TableField(exist = false)
    private LocalDateTime startTime;
    @TableField(exist = false)
    private LocalDateTime endTime;
    @TableField(exist = false)
    private String createdByName;
    @Schema(description = "消息Uuid，用于更新反馈类型")
    private String chatUuid;
    @Schema(description = "反馈的时候采用uuid")
    private String agentUuid;
    @Schema(description = "采纳状态")
    private String adoptionStatus;
    @TableField(exist = false)
    private String agentName;
    @Schema(description = "优化状态，用于点踩才能添加到问答库，默认编辑自动变成true")
    private Boolean optimizationFlag = false;
    @TableField(exist = false)
    private List<Long> ids;
    @TableField(exist = false)
    @Schema(description = "QA审核状态（关联qa_improve）：reviewing/pass/reject")
    private String qaReviewStatus;
}
