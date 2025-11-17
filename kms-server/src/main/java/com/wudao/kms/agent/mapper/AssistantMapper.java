package com.wudao.kms.agent.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.agent.domain.Assistant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * AI助手数据访问层
 */
@Mapper
public interface AssistantMapper extends MPJBaseMapper<Assistant> {
    /**
     * 根据创建人和类型查询助手数量
     *
     * @param createBy 创建人
     * @param type     助手类型 可以为空
     * @return 助手数量
     */
    @Select("SELECT COUNT(*) FROM ai_assistant WHERE create_by = #{createBy} " +
            "AND (#{type}::VARCHAR IS NULL OR type = #{type}::VARCHAR)")
    int queryCountByCreateByAndType(@Param("createBy") Long createBy, @Param("type") String type);

    /**
     * 根据创建人查询已发布助手数量
     *
     * @param createBy 创建人
     * @return 已发布助手数量
     */
    @Select("SELECT COUNT(*) FROM ai_assistant WHERE create_by = #{createBy} " +
            "AND status = 1")
    int queryPublishedCountByCreateBy(@Param("createBy") Long createBy);
} 