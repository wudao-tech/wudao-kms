package com.wudao.kms.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wudao.kms.agent.domain.AssistantMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 助手消息Mapper接口
 */
@Mapper
public interface AssistantMessageMapper extends BaseMapper<AssistantMessage> {
    
    /**
     * 获取会话的历史消息（用于多轮对话）
     */
    @Select("SELECT * FROM ai_assistant_message " +
            "WHERE session_uuid = #{sessionUuid} " +
            "AND del_flag = false " +
            "AND status = 'COMPLETED' " +
            "AND id > COALESCE(#{lastMessageId}, 0) " +
            "ORDER BY round_index ASC, create_time ASC")
    List<AssistantMessage> getHistoryMessages(@Param("sessionUuid") String sessionUuid, 
                                            @Param("lastMessageId") Long lastMessageId);
    
    /**
     * 获取会话的最新消息列表
     */
    @Select("SELECT * FROM ai_assistant_message " +
            "WHERE session_uuid = #{sessionUuid} " +
            "AND del_flag = false " +
            "ORDER BY round_index DESC, create_time DESC " +
            "LIMIT #{limit}")
    List<AssistantMessage> getRecentMessages(@Param("sessionUuid") String sessionUuid, 
                                           @Param("limit") Integer limit);
} 