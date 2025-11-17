import request from '@/utils/request'

// 知识库列表
export const  knowledgeBaseList= (params) => {
  return request({
    method: 'get',
    url: '/api/knowledge-base/page',
    params
  })
}

// 知识库标签列表
export const knowledgeBaseTagList = () => {
  return request({
    method: 'get',
    url: '/api/knowledge-base/tags'
  })
}
// 创建知识库
export const createKnowledgeBase = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-base',
    data
  })
}

// 更新知识库
export const updateKnowledgeBase = (data) => {
  return request({
    method: 'put',
    url: '/api/knowledge-base',
    data
  })
}

// 删除知识库
export const deleteKnowledgeBase = (id) => {
  return request({
    method: 'delete',
    url: `/api/knowledge-base/${id}`
  })
}

// 知识库详情
export const knowledgeBaseDetail = (id) => {
  return request({
    method: 'get',
    url: `/api/knowledge-base/${id}`
  })
}



// 知识空间列表
export const knowledgeSpaceList = (id) => {
  return request({
    method: 'get',
    url: `/api/knowledge-space/tree-structure/${id}`
  })
}

// 新增知识空间
export const createKnowledgeSpace = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-space',
    data
  })
}

// 更新知识空间
export const updateKnowledgeSpace = (data) => {
  return request({
    method: 'put',
    url: '/api/knowledge-space',
    data  
  })
}

// 删除知识空间
export const deleteKnowledgeSpace = (id) => {
  return request({
    method: 'delete',
    url: `/api/knowledge-space/${id}`
  })
}

// 知识空间文件列表
export const knowledgeSpaceFileList = (params) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file/page`,
    params
  })
}

// 删除文件
export const deleteFile = (id) => {
  return request({
    method: 'delete',
    url: `/api/knowledge-file/${id}`
  })
}

// 文件详情
export const fileDetail = (id) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file/${id}`
  })
}

// 更新文件
export const updateFileContent = (data) => {
  return request({
    method: 'put',
    url: '/api/knowledge-file',
    data
  })
}


// 上传文件
export const uploadFile = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-file/upload',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

// 文件分片上传
export const uploadFileChunk = (data) => {
  return request({
    method: 'post',
    url: '/chunk/chunkUpload',
    data
  })
}

// 分片合并
export const mergeFileChunk = (data) => {
  return request({
    method: 'get',
    url: '/chunk/mergeFile',
    params: data
  })
}

// 获取临时文件
export const getTempFile = (id, spaceId, creatrType) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file/temp/${id}/${spaceId}/${creatrType}`,
    // params: {
    //   creatrType
    // }
  })
}

// 创建临时文件
export const createTempFile = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-file/temp',
    data
  })
}

// 删除临时文件
export const deleteTempFile = (knowledgeBaseId, spaceId, fileMd5) => {
  return request({
    method: 'delete',
    url: `/api/knowledge-file/temp/${knowledgeBaseId}/${spaceId}/${fileMd5}`
  })
}

// 分段编辑获取临时文件
export const getTempFileBySegment = (data) => {
  return request({
    method: 'post',
    url: `/api/knowledge-file-segment/process-segment`,
    data
  })
}

// 获取解析后文件的段落
export const getParsedFileSegment = (data) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file-segment/queryList`,
    params: data
  })
}

// 详情查看分段段落
export const getParsedFileSegmentDetail = (data) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file-segment/querySegmentByFileUuid`,
    params: data
  })
}

// 更新文件段落
export const updateParsedFileSegment = (data) => {
  return request({
    method: 'put',
    url: `/api/knowledge-file-segment`,
    data
  })
}

// 更新段落后保存向量
export const fileSegmentVector = (fileId) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file-segment/vector/${fileId}`
  })
}

// 权限申请
export const applyPermission = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-base-permission-application/submit',
    data
  })
}

// 知识库下的用户列表
export const knowledgeBaseUserList = (id) => {
  return request({
    method: 'get',
    url: `/api/knowledge-base-permission/list/${id}`
  })
}

// 编辑知识库下的用户权限
export const editKnowledgeBaseUserPermission = (data) => {
  return request({
    method: 'put',
    url: '/api/knowledge-base-permission/update',
    data
  })
}

// 删除识库下的用户权限
export const deleteKnowledgeBaseUserPermission = (data) => {
  return request({
    method: 'delete',
    url: '/api/knowledge-base-permission/remove',
    params: data
  })
}

// 新增知识库下的用户权限
export const addKnowledgeBaseUserPermission = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-base-permission/add',
    data
  })
}

// 知识库下的权限申请列表
export const knowledgeBasePermissionApplicationList = (id) => {
  return request({
    method: 'get',
    url: `/api/knowledge-base-permission-application/pending/${id}`
  })
}
// 通过申请
export const passApplication = (id, data) => {
  return request({
    method: 'post',
    url: `/api/knowledge-base-permission-application/approve/${id}`,
    data: {}
  })
}
// 拒绝申请
export const rejectApplication = (id, data) => {
  return request({
    method: 'post',
    url: `/api/knowledge-base-permission-application/reject/${id}`,
    data: {}
  })
}

// 待办
export const getTodoList = (adminUserId) => {
  return request({
    method: 'get',
    url: `/api/knowledge-base-permission-application/admin/${adminUserId}`
  })
}

// 上传完成返回
export const uploadFileReturn = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-file/batch-create',
    data
  })
}


// 命中测试查询
export const hitTestQuery = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-base/search',
    data
  })
}

// 查询所有知识库
export const queryAllKnowledgeBase = () => {
  return request({
    method: 'get',
    url: '/api/knowledge-base/list'
  })
}

// 根据文件夹查询所有文件
export const queryAllFileByFolder = (id) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file/space/${id}`
  })
}

// 词库列表
export const queryAllWordLibrary = (params) => {
  return request({
    method: 'get',
    url: '/api/system/dictionary/page',
    params
  })
}

// 新增词库
export const addWordLibrary = (data) => {
  return request({
    method: 'post',
    url: '/api/system/dictionary',
    data
  })
}

// 更新词库
export const updateWordLibrary = (data) => {
  return request({
    method: 'put',
    url: '/api/system/dictionary',
    data
  })
}

// 删除词库
export const deleteWordLibrary = (id) => {
  return request({
    method: 'delete',
    url: `/api/system/dictionary/${id}`
  })
}

// 导入词库
export const importWordLibrary = (data) => {
  return request({
    method: 'post',
    url: '/api/system/dictionary/import',
    data
  })
}

// 历史记录
export const historyRecord = (params) => {
  return request({
    method: 'get',
    url: '/api/search-history/list',
    params
  })
}

// 可删除记录
export const deleteHistoryRecord = (id) => {
  return request({
    method: 'delete',
    url: `/api/search-history/${id}`
  })
}

// 下发来的应用
export const getPublishApp = (data) => {
  return request({
    method: 'post',
    url: '/agent/list',
    data
  })
}

// 知识库查文件
export const queryAllFileByKnowledgeBase = (knowledgeBaseId) => {
  return request({
    method: 'get',
    url: `/api/knowledge-file/list/${knowledgeBaseId}`
  })
}

// url上传
export const urlBatchDownload = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-file/batch-download',
    data
  })
}

// 在线创建
export const onlineCreate = (data) => {
  return request({
    method: 'post',
    url: '/api/knowledge-file/create-online',
    data
  })
}

//上传普通图片
export const uploadFileImage = (data) => {
  return request({
    method: 'post',
    url: '/api/file-common/upload',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}
