package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 访问记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("visit_record")
@Schema(description = "访问记录")
public class VisitRecord extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "访问目标类型：knowledge_base, knowledge_file, knowledge_space")
    private String targetType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "访问URL")
    private String targetUrl;

    @Schema(description = "访问时间")
    private LocalDateTime visitTime;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "用户代理信息")
    private String userAgent;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;
} 