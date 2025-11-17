import { aidojoService } from '@/utils/request'

// 创建会话
export function createSession(data) {
    return aidojoService({
        url: '/agent/v2/session',
        method: 'post',
        data: data,
    })
}

// 分页查询会话列表
export function getSessionList(data) {
    return aidojoService({
        url: '/agent/v2/session/list',
        method: 'post',
        data: data,
    })
}

export function getAssistantDetail(uuid) {
    return aidojoService({
        url: `/api/agent/v2/assistant/${uuid}`,
        method: 'get',
    })
}

// 获取会话详情
export function getSessionDetail(sessionUuid) {
    return aidojoService({
        url: `/agent/v2/session/${sessionUuid}`,
        method: 'get',
    })
}

// 删除会话
export function deleteSession(sessionUuid) {
    return aidojoService({
        url: `/agent/v2/session/${sessionUuid}`,
        method: 'delete',
    })
}

// 清空所有会话
export function clearAllSession(params) {
    return aidojoService({
        url: `/agent/v2/session/clear`,
        method: 'delete',
        params: params
    })
}

// 更新会话名称
export function updateSessionName(data) {
    return aidojoService({
        url: `/agent/v2/session`,
        method: 'put',
        data: data,
    })
}

// 获取会话历史消息
export function getSessionMessages(sessionUuid) {
    return aidojoService({
        url: `/agent/v2/session/${sessionUuid}/messages`,
        method: 'get'
    })
}

// 结束会话
export function stopSession(sessionUuid) {
    return aidojoService({
        url: `/agent/v2/session/${sessionUuid}/stop`,
        method: 'post',
    })
}

// 流式聊天接口 - 使用fetch而不是axios
export function chatWithSession(data) {
    // 这个接口返回流式数据，需要特殊处理
    return {
        url: '/agent/v2/session/chat',
        method: 'post',
        data: data,
        responseType: 'stream'
    }
} 