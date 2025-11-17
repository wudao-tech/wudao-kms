package com.wudao.kms.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wudao.kms.agent.domain.AssistantMessage;
import com.wudao.kms.agent.domain.AssistantSession;
import com.wudao.kms.agent.dto.AssistantSessionCreateRequest;
import com.wudao.kms.agent.dto.AssistantSessionListRequest;
import com.wudao.kms.agent.dto.AssistantSessionResponse;

import java.util.List;

/**
 * 助手会话服务接口
 */
public interface AssistantSessionService {
    
    /**
     * 创建会话
     * @param request 创建请求
     * @return 会话响应
     */
    AssistantSessionResponse createSession(AssistantSessionCreateRequest request);
    
    /**
     * 分页查询会话列表
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<AssistantSessionResponse> getSessionList(AssistantSessionListRequest request);
    
    /**
     * 获取会话详情
     * @param sessionUuid 会话UUID
     * @return 会话详情
     */
    AssistantSessionResponse getSessionDetail(String sessionUuid);
    
    /**
     * 删除会话
     * @param sessionUuid 会话UUID
     * @return 是否成功
     */
    boolean deleteSession(String sessionUuid);
    
    /**
     * 获取会话历史消息
     * @param sessionUuid 会话UUID
     * @return 消息列表
     */
    List<AssistantMessage> getSessionMessages(String sessionUuid);

    /**
     * 结束多轮对话
     * @param sessionUuid 会话UUID
     * @return 是否成功
     */
    boolean stopSession(String sessionUuid);

    /**
     * 获取会话详情
     * @param sessionUuid 会话UUID
     * @return 会话详情
     */
    AssistantSession getSessionByUuid(String sessionUuid);

    /**
     * 更新会话
     * @param session 会话对象
     */
    void updateSession(AssistantSession session);

    /**
     * 根据助手清空会话
     */
    void clearSessionByAssistant(String assistantUuid,String createBy,String sourceType);
    
    /**
     * 更新会话的当前节点ID
     * @param sessionUuid 会话UUID
     * @param currentNodeId 当前节点ID
     */
    void updateCurrentNode(String sessionUuid, String currentNodeId, Long userId);
    
    /**
     * 获取会话的当前节点ID
     * @param sessionUuid 会话UUID
     * @return 当前节点ID
     */
    String getCurrentNodeId(String sessionUuid);
} 