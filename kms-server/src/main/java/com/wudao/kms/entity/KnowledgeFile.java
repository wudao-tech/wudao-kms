package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import com.wudao.kms.dto.knowledgefile.FileProcessProgressDTO;
import com.wudao.kms.dto.knowledgefile.KnowLedgeSegmentConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知识库文件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "knowledge_file",autoResultMap = true)
@Schema(description = "知识库文件")
public class KnowledgeFile extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "空间ID")
    private Long spaceId;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "文件内容（文本类型）")
    private String content;

    @Schema(description = "状态：1-待处理，2-处理中，3-已可用，4-处理失败")
    private Integer status;

    @Schema(description = "处理状态描述")
    private String processingStatus;

    @Schema(description = "字数统计")
    private Integer wordCount;

    @Schema(description = "页数统计")
    private Integer pageCount;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "标签，逗号分隔")
    private String tags;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "处理完成时间")
    private LocalDateTime processTime;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;

    @Schema(description = "分段后的文档路径")
    private String segmentedDocPath;

    @Schema(description = "分段后的文档MD5")
    private String fileMd5;

    @TableField(exist = false)
    private String createByNickName;

    @Schema(description = "封面")
    private String coverPath;

    @Schema(description = "浏览量")
    @TableField(exist = false)
    private Long viewCount;
    @Schema(description = "文件UUID")
    private String fileUuid;
    @Schema(description = "文件上传方式：UPLOAD上传 URL ONLINE在线")
    private String createType;
    @TableField(exist = false)
    private String s3Path;
    @Schema(description = "处理形式：分块处理/问答对提取")
    private String chunkType;
    @Schema(description = "oss路径")
    private String ossPath;
    @Schema(description = "解析后的md，用于生成总结和标签")
    @TableField(exist = false)
    private String extraMd;
    @Schema(description = "解析类型 SEGMENT分段/QA问答对")
    private String extraType = "SEGMENT";
    private String uuid;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private KnowLedgeSegmentConfig knowledgeSegmentConfig;
    @TableField(exist = false)
    private String previewS3Url;

    @Schema(description = "文件处理进度（JSON格式）")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileProcessProgressDTO processProgress;
} 