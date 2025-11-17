import request from '@/utils/request'

// qa调优
export const getQaTable = (params) => {
    return request.get('/api/qa/improve/queryList', {params})
}

// 问答记录
export const getQaRecord = (params) => {
    return request.get('/api/feedback/queryList', {params})
}

// 问答记录编辑-优化
export const editQaRecord = (data) => {
    return request.put('/api/feedback', data)
}
// 问答导出
export const exportQaRecord = (params) => {
    return request.get('/api/feedback/export', {params})
}

// 添加到问答对
export const addToQa = (data) => {
    return request.post('/api/qa/improve', data)
}

// 批量添加问答对
export const batchAddToQa = (data) => {
    return request.post('/api/qa/improve/batch/add', data)
}

// QA调优导出
export const exportQaImprove = (params) => {
    return request.get('/api/qa/improve/export', {params})
}

// 导入问答对
export const importQaImprove = (data) => {
    return request.post('/api/qa/improve/importData', 
       data,
       {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
       }
    )
}

// 编辑问答对
export const editQaImprove = (data) => {
    return request.put('/api/qa/improve', data)
}

//删除问答对
export const deleteQaImprove = (params) => {
    return request.get('/api/qa/improve/delete', {params})
}

// 采纳
export const acceptQaImprove = (params) => {
    return request.get('/api/qa/improve/batchPass', {params})
}


// 点赞-点踩记录问题
export const recordQa = (data) => {
    return request.post('/api/feedback', data)
}

// 点赞-点踩删除
export const deleteRecordQa = (params) => {
    return request.get('/api/feedback/delete', {params})
}