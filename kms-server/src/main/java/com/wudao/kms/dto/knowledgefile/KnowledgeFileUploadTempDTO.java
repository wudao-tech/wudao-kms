package com.wudao.kms.dto.knowledgefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "知识文件上传临时DTO")
public class KnowledgeFileUploadTempDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "文件ID")
    private Long fileId;
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "文件s3路径")
    private String s3Path;
    @Schema(description = "文件大小")
    private Long fileSize;
    @Schema(description = "文件类型")
    private String fileType;
    @Schema(description = "文件md5")
    private String fileMd5;
    @Schema(description = "解析结果")
    private String parseResult;
    @Schema(description = "摘要")
    private String summary;
    @Schema(description = "标签")
    private String tags;
    @Schema(description = "段落数量")
    private Integer segmentCount;
    @Schema(description = "内容数量")
    private Integer contentCount;
    @Schema(description = "分段处理状态")
    private String segmentStatus;
    @Schema(description = "分段后的文档路径")
    private String segmentedDocPath;
    @Schema(description = "本地source文件路径")
    private String localSourcePath;
    @Schema(description = "本地segment文件路径")
    private String localSegmentPath;
    @Schema(description = "分段配置")
    private KnowLedgeSegmentConfig segmentConfig;
    @Schema(description = "文件上传方式：UPLOAD上传 URL ONLINE在线")
    private String createType;
    @Schema(description = "解析后的md文件")
    private String extraMd;
}
