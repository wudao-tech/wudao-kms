package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("miner_u_task")
@Schema(description = "minerU任务表，用于存放对应文件的解析任务")
public class MinerUTask extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "文件UUID，关联原始文件")
    private String fileUuid;

    @Schema(description = "原始文件URL")
    private String originalFileUrl;

    @Schema(description = "原始文件名")
    private String originalFileName;

    @Schema(description = "解析类型(pdf, word, ppt等)")
    private String parseType;


    /**
     * 任务处理状态，完成:done，pending: 排队中，running: 正在解析，failed：解析失败，converting：格式转换中
     */
    @Schema(description = "任务处理状态")
    private String state;

    @Schema(description = "MinerU返回的任务ID")
    private String taskId;

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "完整结果压缩包URL")
    private String fullZipUrl;

    @Schema(description = "解析结果内容")
    private String resultContent;

    @Schema(description = "本地处理结果目录路径")
    private String localResultPath;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "已解析页数")
    private Integer extractedPages = 0;

    @Schema(description = "总页数")
    private Integer totalPages = 0;

    @Schema(description = "任务完成时间")
    private LocalDateTime completeTime;
}
