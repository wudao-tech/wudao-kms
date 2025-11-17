package com.wudao.kms.dto.knowledgefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 知识文件批量下载DTO
 */
@Data
@Schema(description = "知识文件批量下载请求")
public class KnowledgeFileBatchDownloadDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "知识库ID", required = true)
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    @Schema(description = "空间ID", required = true)
    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @Schema(description = "文件下载信息列表", required = true)
    @NotEmpty(message = "文件列表不能为空")
    @Size(max = 10, message = "单次最多支持下载10个文件")
    private List<FileDownloadInfo> fileList;

    /**
     * 文件下载信息
     */
    @Data
    @Schema(description = "文件下载信息")
    public static class FileDownloadInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @Schema(description = "文件URL", required = true, example = "https://example.com/file.pdf")
        @NotEmpty(message = "文件URL不能为空")
        private String fileUrl;

        @Schema(description = "文件名（可选，为空时从URL提取）", example = "document.pdf")
        private String fileName;

        @Schema(description = "文件描述（可选）", example = "重要文档")
        private String description;
    }
}