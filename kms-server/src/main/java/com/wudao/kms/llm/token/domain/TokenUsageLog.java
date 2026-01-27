package com.wudao.kms.llm.token.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("token_usage_log")
@Schema(description = "Token使用记录")
public class TokenUsageLog extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "操作类型：CHAT/EMBEDDING/RERANK/ASR")
    private String operationType;

    @Schema(description = "模型名称")
    private String model;

    @Schema(description = "输入token数")
    private Integer inputTokens;

    @Schema(description = "输出token数")
    private Integer outputTokens;

    @Schema(description = "总token数")
    private Integer totalTokens;

    @Schema(description = "耗时（毫秒）")
    private Integer latencyMs;

    @Schema(description = "状态：SUCCESS/FAILED")
    private String status;

    @Schema(description = "错误信息")
    private String errorMessage;
}
