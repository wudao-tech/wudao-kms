package com.wudao.kms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.entity.QaImprove;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QaImproveMapper extends MPJBaseMapper<QaImprove> {
    @Select("""
            <script>
            select
                'QA' as contentType,
                create_time as createTime,
                answer as content,
                1 - (question_vector &lt;=&gt; #{queryVector}::vector) AS score
                from qa_improve
                order by score desc
            </script>
    """)
    List<KnowledgeSearchResult> qaVector(String queryVector);

    @Select("""
            <script>
            select
                'QA' as answerType,
                create_time as createTime,
                answer as content,
                1 - (question_vector &lt;=&gt; #{queryVector}::vector) AS score,
               base.name as knowledgeBaseName,
               space.name as knowledgeSpaceName,
               base.id as knowledgeBaseId,
               qa_improve.question as qaQuestion,
               space.id as knowledgeSpaceId,
               u.nickname as createBy
                from qa_improve
                left join knowledge_base base on base.id = qa_improve.knowledge_id
                left join knowledge_space space on space.id = qa_improve.knowledge_space_id
                left join sys_user u on u.id = qa_improve.create_by
                where review_status = 'pass'
                <if test="knowledgeBaseIds != null and knowledgeBaseIds.size() &gt; 0">
                  AND knowledge_id IN
                  <foreach collection="knowledgeBaseIds" item="id" open="(" separator="," close=")">
                    #{id}
                  </foreach>
                </if>
                <if test="knowledgeSpaceIds != null and knowledgeSpaceIds.size() &gt; 0">
                  AND knowledge_space_id IN
                  <foreach collection="knowledgeSpaceIds" item="id" open="(" separator="," close=")">
                    #{id}
                  </foreach>
                </if>
                order by score desc
            </script>
    """)
    List<KnowledgeSearchResult> qaVectorByCondition(String queryVector, List<Long> knowledgeBaseIds, List<Long> knowledgeSpaceIds);
}
