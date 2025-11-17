package com.wudao.kms.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wudao.kms.agent.domain.AiAssistantMessageTurn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI助手消息轮次Mapper接口
 */
@Mapper
public interface AiAssistantMessageTurnMapper extends BaseMapper<AiAssistantMessageTurn> {
    
    /**
     * 根据会话UUID获取所有轮次
     */
    @Select("SELECT * FROM ai_assistant_message_turn WHERE session_uuid = #{sessionUuid} AND del_flag = false ORDER BY turn_index ASC")
    List<AiAssistantMessageTurn> getTurnsBySessionUuid(@Param("sessionUuid") String sessionUuid);
    
    /**
     * 根据会话UUID和轮次序号获取轮次
     */
    @Select("SELECT * FROM ai_assistant_message_turn WHERE session_uuid = #{sessionUuid} AND turn_index = #{turnIndex} AND del_flag = false")
    AiAssistantMessageTurn getTurnByIndex(@Param("sessionUuid") String sessionUuid, @Param("turnIndex") Integer turnIndex);
    
    /**
     * 根据轮次ID获取轮次
     */
    @Select("SELECT * FROM ai_assistant_message_turn WHERE turn_id = #{turnId} AND del_flag = false")
    AiAssistantMessageTurn getTurnById(@Param("turnId") String turnId);
    
    /**
     * 获取会话的最大轮次序号
     */
    @Select("SELECT COALESCE(MAX(turn_index), -1) FROM ai_assistant_message_turn WHERE session_uuid = #{sessionUuid} AND del_flag = false")
    Integer getMaxTurnIndex(@Param("sessionUuid") String sessionUuid);
    
    /**
     * 更新轮次的活跃用户消息节点
     */
    @Update("UPDATE ai_assistant_message_turn SET active_user_node_id = #{nodeId}, update_time = CURRENT_TIMESTAMP WHERE turn_id = #{turnId}")
    int updateActiveUserNode(@Param("turnId") String turnId, @Param("nodeId") String nodeId);
    
    /**
     * 更新轮次的活跃AI回复节点
     */
    @Update("UPDATE ai_assistant_message_turn SET active_assistant_node_id = #{nodeId}, update_time = CURRENT_TIMESTAMP WHERE turn_id = #{turnId}")
    int updateActiveAssistantNode(@Param("turnId") String turnId, @Param("nodeId") String nodeId);
    
    /**
     * 更新用户消息版本计数
     */
    @Update("UPDATE ai_assistant_message_turn SET user_versions_count = #{count}, update_time = CURRENT_TIMESTAMP WHERE turn_id = #{turnId}")
    int updateUserVersionsCount(@Param("turnId") String turnId, @Param("count") Integer count);
    
    /**
     * 更新AI回复版本计数
     */
    @Update("UPDATE ai_assistant_message_turn SET assistant_versions_count = #{count}, update_time = CURRENT_TIMESTAMP WHERE turn_id = #{turnId}")
    int updateAssistantVersionsCount(@Param("turnId") String turnId, @Param("count") Integer count);
    
    /**
     * 更新轮次状态
     */
    @Update("UPDATE ai_assistant_message_turn SET turn_status = #{status}, update_time = CURRENT_TIMESTAMP WHERE turn_id = #{turnId}")
    int updateTurnStatus(@Param("turnId") String turnId, @Param("status") String status);
    
    /**
     * 获取会话的活跃对话路径（基于各轮次的活跃节点）
     */
    @Select("SELECT n.* FROM ai_assistant_message_turn t " +
            "JOIN ai_assistant_message_node n ON (n.node_id = t.active_user_node_id OR n.node_id = t.active_assistant_node_id) " +
            "WHERE t.session_uuid = #{sessionUuid} AND t.del_flag = false AND n.del_flag = false " +
            "ORDER BY t.turn_index ASC, n.message_type ASC")
    List<AiAssistantMessageTurn> getActiveConversationPath(@Param("sessionUuid") String sessionUuid);
}