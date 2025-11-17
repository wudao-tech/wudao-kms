package com.wudao.kms.vo;

import com.wudao.kms.entity.KnowledgeFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 知识库文件VO（包含关联查询结果）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "知识库文件VO")
public class KnowledgeFileVO extends KnowledgeFile {

    @Schema(description = "知识库名称")
    private String knowledgeBaseName;

    @Schema(description = "空间名称")
    private String spaceName;
} 