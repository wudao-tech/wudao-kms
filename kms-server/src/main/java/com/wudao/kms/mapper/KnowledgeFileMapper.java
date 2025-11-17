package com.wudao.kms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.entity.KnowledgeFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识库文件Mapper接口
 */
@Mapper
public interface KnowledgeFileMapper extends MPJBaseMapper<KnowledgeFile> {
    /**
     * 调用PostgreSQL分词功能，返回分词后的tokens
     * 输入: "物道知识库"
     * 输出: ["物道", "知识库"]
     */
    // 使用to_tsvector将传入的内容进行分词
    @Select("SELECT unnest(string_to_array(lexemes, ' ')) as token " +
            "FROM ts_stat($$ " +
            "  SELECT to_tsvector('chinese_zh', #{text}) " +
            "$$)")
    List<String> tokenize(@Param("text") String text);
    // 进行全文搜索,使用ts_rank直接返回全文检索的分数
} 