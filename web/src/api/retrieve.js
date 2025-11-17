import request from '@/utils/request'

// 知识检索汇总
export const getKnowledgeRetrieveSummary = () => {
  return request.get('/api/knowledge/summary')
}

// 知识检索推荐
export const getKnowledgeRetrieveRecommend = () => {
  return request.get('/api/knowledge/recommend')
}
// 最新知识
export const getKnowledgeRetrieveLatest = () => {
  return request.get('/api/knowledge/latest')
}

// 搜索词库
export const getKnowledgeRetrieveSearch = (params) => {
  return request.get('api/system/dictionary/list', {
    params
  })
}

// 评论
export const addComment = (data) => {
  return request.post('/api/workspace/comment/add', data)
}


// 工作台 收藏记录
export const getWorkspaceCollect = (params) => {
  return request.get('/api/workspace/favorite/records', {
    params
  })
}

// 收藏总数
export const getWorkspaceCollectCount = () => {
  return request.get('/api/workspace/favorite/count')
}

// 评论总数
export const getWorkspaceCommentCount = () => {
  return request.get('/api/workspace/comment/count')
} 

// 访问总数
export const getWorkspaceVisitCount = () => {
  return request.get('/api/workspace/visit/count')
}

// 访问记录
export const getWorkspaceVisitRecord = (params) => {
  return request.get('/api/workspace/visit/records', {
    params
  })
}

// 我的知识
export const getWorkspaceMyKnowledge = ( params) => {
  return request.get(`/api/workspace/my-knowledge`, {
    params
  })
}

// 添加收藏
export const addFavorite = (data) => {
  return request.post('/api/workspace/favorite/add', data)
}
// 取消收藏
export const cancelFavorite = (data) => {
  return request.post('/api/workspace/favorite/remove', data)
}

// 检查是否收藏
export const checkFavorite = (data) => {
  return request.post('/api/workspace/favorite/check', data)
}

// 我的知识访问存量
export const myVisit = () => {
  return request.get('/api/workspace/stats')
}

// 导入
// export const importWordLibrary = (dictType, data) => {
//   return request.post(`/api/system/dictionary/importDictionary/${dictType}`, data, {
//     headers: {
//       'Content-Type': 'multipart/form-data'
//     }
//   })
// }

export const importWordLibrary = (dictType, data) => {
  return request({
    method: 'post',
    url: `/api/system/dictionary/importDictionary/${dictType}`,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

// 导出
export const exportWordLibrary = (dictType) => {
  return request.get(`/api/system/dictionary/exportDictionary/${dictType}`, {
    responseType: 'blob'
  })
}

// 模型接口
export const getModel = (params) => {
  return request.get('/api/llm/model/queryList', {
    params
  })
}

// 创建新对话
export const createSession = (data) => {
  return request.post('/api/agent/session', data)
}

// 获取对话历史列表
export const getSessionList = (params) => {
  return request.get('/api/agent/session/queryList', {
    params
  })
}

// 删除历史对话
export const deleteSession = (sessionUuid) => {
  return request.delete(`/api/agent/session/${sessionUuid}`)
}

// 编辑对话名称
export const editSessionName = (data) => {
  return request.put('/api/agent/session', data)
}

// 当前对话记录详情
export const getSessionDetail = (params) => {
  return request.get(`/api/agent/message/queryList`, {
    params
  })
}

// 清空所有对话
export const clearAllSession = () => {
  return request.delete(`/api/agent/session/clean`)
}

// 删除模型
export const deleteModel = (params) => {
  return request.delete(`/api/llm/model`, {
    params
  })
}
// 编辑模型
export const editModel = (data) => {
  return request.put('/api/llm/model', data)
}

// 测试模型
export const testModel = (data) => {
  return request.post('/api/llm/model/test', data)
}