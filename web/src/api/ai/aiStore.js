import { aidojoService } from '@/utils/request'


// ai商店列表
export function aiStoreList(data) {
    return aidojoService({
        url: '/ai/shop/list',
        method: 'post',
        data: data,
    })
}

// 下发应用
export function aiStoreExpand(data) {
    return aidojoService({
        url: '/ai/shop/publish',
        method: 'post',
        data: data
    })
}

export function fileUpload (file, type = 'file') {
    const formData = new FormData()
    formData.append(type, file)
    return aidojoService({
      method: 'POST',
      url: '/s3/uploadGetHttp',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' },
    })
}

export function commonFileUpload (file, type = 'file') {
    const formData = new FormData()
    formData.append(type, file)
    return aidojoService({
      method: 'POST',
      url: '/common/upload',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' },
    })
}

// 助手ID获取大模型
export function getAssistantModel(assistantUuid) {
    return aidojoService({
        url: `/llmmode/model/assistant/${assistantUuid}`,
        method: 'get'
    })
}

export function getSessionMessages(sessionUuid) {
    return aidojoService({
        url: `/v2/conversation/${sessionUuid}/turns`,
        method: 'get'
    })
}    

// 获取助手详情
export function getAssistantDetail(uuid) {
    return aidojoService({
        url: `/api/agent/v2/assistant/${uuid}`,
        method: 'get',
    })
}