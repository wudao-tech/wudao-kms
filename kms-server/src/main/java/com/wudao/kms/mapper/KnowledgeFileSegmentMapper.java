package com.wudao.kms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.entity.KnowledgeFileSegment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件分段Mapper接口
 */
@Mapper
public interface KnowledgeFileSegmentMapper extends MPJBaseMapper<KnowledgeFileSegment> {

    List<KnowledgeSearchResult> vectorSearch(@Param("knowledgeBaseIds") List<Long> knowledgeBaseIds,
                                             @Param("knowledgeSpaceIds") List<Long> knowledgeSpaceIds,
                                             @Param("documentIds") List<Long> documentIds,
                                             @Param("disableKnowledgeIds") List<Long> disableKnowledgeIds,
                                             @Param("queryVector") String queryVector,
                                             @Param("topK") Long topK,
                                             @Param("fileType") String fileType);

    /**
     * 采用pg的能力计算分数
     *
     * @param chunkId 分段id
     * @param vector  查询向量
     * @return 结果分 使用1-(<=>)计算余弦相似度
     */
    @Select("""
                SELECT 1 - (t.vector <=> #{vector}::vector) AS score
                FROM knowledge_file_segment t
                WHERE t.id = #{chunkId}
            """)
    Double calculateScore(@Param("chunkId") Long chunkId, @Param("vector") String vector);

    /**
     * 使用PostgreSQL的to_tsvector进行分词（使用chinese_zh中文分词器）
     *
     * @param text 需要分词的文本
     * @return 分词后的结果列表
     */
    @Select("SELECT unnest(tsvector_to_array(to_tsvector('chinese_zh', #{text}::text))) as token")
    @ResultType(String.class)
    List<String> tokenizeText(@Param("text") String text);

    /**
     * 使用PostgreSQL全文搜索功能进行分词检索
     * 使用XML方式实现，定义在KnowledgeFileSegmentMapper.xml中
     */
    List<KnowledgeSearchResult> fulltextSearch(@Param("tokenizedQuery") String tokenizedQuery,
                                               @Param("knowledgeBaseIds") List<Long> knowledgeBaseIds,
                                               @Param("knowledgeSpaceIds") List<Long> knowledgeSpaceIds,
                                               @Param("documentIds") List<Long> documentIds,
                                               @Param("topK") Long topK,
                                               @Param("fileType") String fileType);
} 