package com.wudao.kms.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wudao.kms.agent.domain.AiAssistantMessageNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI助手消息节点Mapper接口
 */
@Mapper
public interface AiAssistantMessageNodeMapper extends BaseMapper<AiAssistantMessageNode> {
    
    /**
     * 根据会话UUID获取所有消息节点
     */
    @Select("SELECT * FROM ai_assistant_message_node WHERE session_uuid = #{sessionUuid} AND del_flag = false ORDER BY create_time ASC")
    List<AiAssistantMessageNode> getNodesBySessionUuid(@Param("sessionUuid") String sessionUuid);
    
    /**
     * 根据父节点ID获取所有子节点
     */
    @Select("SELECT * FROM ai_assistant_message_node WHERE parent_node_id = #{parentNodeId} AND del_flag = false ORDER BY create_time ASC")
    List<AiAssistantMessageNode> getChildrenByParentId(@Param("parentNodeId") String parentNodeId);
    
    /**
     * 获取会话的根节点（父节点为null的节点）
     */
    @Select("SELECT * FROM ai_assistant_message_node WHERE session_uuid = #{sessionUuid} AND parent_node_id IS NULL AND del_flag = false LIMIT 1")
    AiAssistantMessageNode getRootNode(@Param("sessionUuid") String sessionUuid);
    
    /**
     * 根据节点ID获取节点
     */
    @Select("SELECT * FROM ai_assistant_message_node WHERE node_id = #{nodeId} AND del_flag = false")
    AiAssistantMessageNode getNodeById(@Param("nodeId") String nodeId);
    
    /**
     * 获取父节点的当前子节点列表（原生SQL确保JSONB正确解析）
     */
    @Select("SELECT children_node_ids::text as children_json FROM ai_assistant_message_node WHERE node_id = #{nodeId} AND del_flag = false")
    String getChildrenNodeIdsJson(@Param("nodeId") String nodeId);
    
    /**
     * 更新父节点的子节点列表
     */
    @Update("UPDATE ai_assistant_message_node SET children_node_ids = #{childrenNodeIds}::jsonb, update_time = CURRENT_TIMESTAMP WHERE node_id = #{nodeId}")
    int updateChildrenNodeIds(@Param("nodeId") String nodeId, @Param("childrenNodeIds") String childrenNodeIds);
    
    /**
     * 获取节点的所有祖先节点（向上遍历到根节点）
     */
    @Select("WITH RECURSIVE ancestors AS (" +
            "  SELECT node_id, parent_node_id, message_type, content, create_time, 1 as level " +
            "  FROM ai_assistant_message_node " +
            "  WHERE node_id = #{nodeId} AND del_flag = false " +
            "  UNION ALL " +
            "  SELECT n.node_id, n.parent_node_id, n.message_type, n.content, n.create_time, a.level + 1 " +
            "  FROM ai_assistant_message_node n " +
            "  INNER JOIN ancestors a ON n.node_id = a.parent_node_id " +
            "  WHERE n.del_flag = false " +
            ") " +
            "SELECT * FROM ai_assistant_message_node " +
            "WHERE node_id IN (SELECT node_id FROM ancestors) AND del_flag = false " +
            "ORDER BY create_time ASC")
    List<AiAssistantMessageNode> getAncestorNodes(@Param("nodeId") String nodeId);
    
    /**
     * 获取从根节点到指定节点的路径（对话历史）
     */
    @Select("WITH RECURSIVE path AS (" +
            "  SELECT node_id, parent_node_id, message_type, content, reasoning_data, text_data, " +
            "         input_tokens, output_tokens, model_name, create_time, 0 as level " +
            "  FROM ai_assistant_message_node " +
            "  WHERE node_id = #{nodeId} AND del_flag = false " +
            "  UNION ALL " +
            "  SELECT n.node_id, n.parent_node_id, n.message_type, n.content, n.reasoning_data, n.text_data, " +
            "         n.input_tokens, n.output_tokens, n.model_name, n.create_time, p.level + 1 " +
            "  FROM ai_assistant_message_node n " +
            "  INNER JOIN path p ON n.node_id = p.parent_node_id " +
            "  WHERE n.del_flag = false " +
            ") " +
            "SELECT * FROM ai_assistant_message_node " +
            "WHERE node_id IN (SELECT node_id FROM path) AND del_flag = false " +
            "ORDER BY create_time ASC")
    List<AiAssistantMessageNode> getConversationPath(@Param("nodeId") String nodeId);
    
    /**
     * 获取会话的活跃路径（基于is_active字段）
     */
    @Select("SELECT * FROM ai_assistant_message_node WHERE session_uuid = #{sessionUuid} AND is_active = true AND del_flag = false ORDER BY create_time ASC")
    List<AiAssistantMessageNode> getActivePathBySession(@Param("sessionUuid") String sessionUuid);
    
    /**
     * 更新节点的活跃状态
     */
    @Update("UPDATE ai_assistant_message_node SET is_active = #{isActive}, update_time = CURRENT_TIMESTAMP WHERE node_id = #{nodeId}")
    int updateNodeActiveStatus(@Param("nodeId") String nodeId, @Param("isActive") Boolean isActive);
    
    /**
     * 批量更新节点活跃状态
     */
    @Update("UPDATE ai_assistant_message_node SET is_active = #{isActive}, update_time = CURRENT_TIMESTAMP WHERE node_id IN #{nodeIds}")
    int batchUpdateNodeActiveStatus(@Param("nodeIds") List<String> nodeIds, @Param("isActive") Boolean isActive);
}