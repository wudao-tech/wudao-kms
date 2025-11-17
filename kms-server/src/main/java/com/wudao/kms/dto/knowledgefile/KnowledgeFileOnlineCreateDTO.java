package com.wudao.kms.dto.knowledgefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 知识文件在线创建DTO
 */
@Data
@Schema(description = "知识文件在线创建请求")
public class KnowledgeFileOnlineCreateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "知识库ID", required = true)
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    @Schema(description = "空间ID", required = true)
    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @Schema(description = "文件名", required = true, example = "新建文档.txt")
    @NotEmpty(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;

    @Schema(description = "文件内容", required = true, example = "这是文档的内容...")
    @NotEmpty(message = "文件内容不能为空")
    @Size(max = 10485760, message = "文件内容不能超过10MB") // 10MB = 10 * 1024 * 1024
    private String fileContent;

    @Schema(description = "文件类型/扩展名", example = "txt")
    private String fileType;

    @Schema(description = "文件摘要（可选）", example = "这是一个重要的文档")
    @Size(max = 1000, message = "摘要长度不能超过1000个字符")
    private String summary;

    @Schema(description = "文件标签（可选，多个标签用逗号分隔）", example = "重要,文档,知识")
    @Size(max = 500, message = "标签长度不能超过500个字符")
    private String tags;
}