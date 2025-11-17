package com.wudao.kms.vo;

import com.wudao.kms.entity.KnowledgeFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索历史VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "搜索历史VO")
public class SearchHistoryVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "搜索内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "文档列表")
    private List<KnowledgeFile> doc;
} 