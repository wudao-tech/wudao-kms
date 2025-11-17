import request from '@/utils/request'

// 创建助手
export function createAssistant(data) {
    return request({
        url: '/api/agent/v2/assistant',
        method: 'post',
        data: data,
    })
}

// 分页查询助手列表
export function getAssistantList(data) {
    return request({
        url: '/api/agent/v2/assistant/list',
        method: 'post',
        data: data,
    })
}

// 获取助手详情
export function getAssistantDetail(uuid) {
    return request({
        url: `/api/agent/v2/assistant/${uuid}`,
        method: 'get',
    })
}

// 更新助手
export function updateAssistant(data) {
    return request({
        url: '/api/agent/v2/assistant',
        method: 'put',
        data: data,
    })
}

// 删除助手
export function deleteAssistant(uuid) {
    return request({
        url: `/api/agent/v2/assistant/${uuid}`,
        method: 'delete',
    })
}

// 复制助手
export function copyAssistant(uuid) {
    return request({
        url: `/api/agent/v2/assistant/copy/${uuid}`,
        method: 'post',
    })
}

// 提示词优化（流式响应）
export function improvePrompt(data) {
    return request({
        url: '/api/agent/v2/assistant/improve_prompt',
        method: 'post',
        data: data,
        responseType: 'stream'
    })
}

// 助手聊天（流式接口）
export function chatWithAssistant(data) {
    return request({
        url: '/api/agent/v2/assistant/chat',
        method: 'post',
        data: data,
        responseType: 'stream'
    })
}

// 搜索知识库
export function searchKnowledgeBase(name) {
  return request({
    url: '/api/kms/knowledge/api/search',
    method: 'get',
    params: {
      name: name || ''
    }
  })
}


// 保存画布
export function saveCanvas(data, versionId) {
  return request({
    url: `/api/ai/workflow/${versionId}`,
    method: 'put',
    data: data,
  })
}

// 获取工作流版本
export function getWorkflowVersion(uuid) {
    return request({
        url: `/api/ai/workflow/${uuid}/currentVersion`,
        method: 'get',
    })
}

// 获取大模型
export function getModelList() {
  return request({
    url: `/api/llmmode/model/map`,
    method: 'get',
  })
}

// 下发
export function expandAssistant(data) {
    return request({
        url: `/api/agent/v2/assistant/publish`,
        method: 'post',
        data: data,
    })
}

// 追问问题
export function followUpQuestion(assistantUuid, sessionUuid) {
    return request({
        url: `/api/agent/v2/api/followup/${assistantUuid}/${sessionUuid}`,
        method: 'post',
        data: data,
    })
}

// 下架组件
export function takeDownComponent(uuid) {
    return request({
        url: `/agent/v2/assistant/downComponents/${uuid}`,
        method: 'get',
    })
}

// 工作流
export function workflowList() {
    return request({
        url: `/api/agent/workflowList`,
        method: 'get'
    })
}