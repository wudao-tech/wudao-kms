package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户搜索历史实体
 */
@Data
@Accessors(chain = true)
@TableName(value = "search_history",autoResultMap = true)
@Schema(description = "用户搜索历史")
public class SearchHistory {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "搜索内容")
    private String content;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "文档ID列表")
    @TableField(value = "doc", typeHandler = JacksonTypeHandler.class)
    private List<KnowledgeFile> doc;
    @Schema(description = "搜索来源：test_search-测试搜索，vector_search-向量搜索")
    private String source;

    @Schema(description = "搜索历史绑定知识空间")
    private Long knowledgeSpaceId;
} 