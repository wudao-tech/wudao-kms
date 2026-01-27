package com.wudao.kms.llm.token.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.llm.token.domain.TokenUsageLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TokenUsageLogMapper extends MPJBaseMapper<TokenUsageLog> {

    @Select("SELECT COALESCE(SUM(total_tokens), 0) FROM token_usage_log " +
            "WHERE user_id = #{userId} AND status = 'SUCCESS' " +
            "AND created_at >= CURRENT_DATE AND created_at < CURRENT_DATE + INTERVAL '1 day'")
    Long getTodayTotalTokens(@Param("userId") Long userId);
}
