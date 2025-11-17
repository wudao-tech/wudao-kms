package com.wudao.kms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 知识库Mapper接口
 */
@Mapper
public interface KnowledgeBaseMapper extends MPJBaseMapper<KnowledgeBase> {
    // for-update更新segment_count和word_count

    @Update("UPDATE knowledge_base " +
            "SET segment_count = segment_count + #{segmentCount}, word_count = word_count + #{wordCount} " +
            "WHERE id = #{knowledgeBaseId}")
    void updateSegmentAndWordCountForUpdate(@Param("knowledgeBaseId") Long knowledgeBaseId,
                                           @Param("segmentCount") Integer segmentCount,
                                           @Param("wordCount") Integer wordCount);

    @Select("select id from knowledge_base where status = 0")
    List<Long> queryDisableKnowledge();
}