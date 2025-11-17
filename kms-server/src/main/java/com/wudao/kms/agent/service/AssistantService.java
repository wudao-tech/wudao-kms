package com.wudao.kms.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseService;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.dto.AssistantCreateRequest;
import com.wudao.kms.agent.dto.AssistantListRequest;
import com.wudao.kms.agent.dto.AssistantListResponse;
import com.wudao.kms.agent.dto.AssistantPublishRequest;
import com.wudao.kms.agent.dto.AssistantUpdateRequest;
import com.wudao.kms.agent.dto.ImprovePromptRequest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * AI助手服务接口
 */
public interface AssistantService extends MPJBaseService<Assistant> {
    
    /**
     * 创建助手
     * @param request 创建请求
     * @return 创建的助手
     */
    Assistant createAssistant(AssistantCreateRequest request);
    
    /**
     * 更新助手
     * @param request 更新请求
     * @return 更新后的助手
     */
    Assistant updateAssistant(AssistantUpdateRequest request);
    
    /**
     * 提示词优化 - 流式响应
     * @param request 优化请求
     * @return 流式消息
     */
    Flux<ServerSentEvent<String>> optimizePrompt(ImprovePromptRequest request);
    
    /**
     * 根据UUID获取助手
     * @param uuid 助手UUID
     * @return 助手信息
     */
    Assistant getAssistantByUuid(String uuid);
    
    /**
     * 分页查询助手列表
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<AssistantListResponse> getAssistantList(AssistantListRequest request);
    
    /**
     * 删除助手
     * @param uuid 助手UUID
     * @return 是否删除成功
     */
    boolean deleteAssistant(String uuid);

    /**
     * 发布/撤回操作
     * @param request 助手id
     */
    void publish(AssistantPublishRequest request);

    /**
     * 复制助手
     * @param uuid 助手UUID
     */
    Assistant copyAssistant(String uuid);
} 
