import request from '@/utils/request'

// 制造商列表
export const getManufacturerList = (params) => {
    return request.get('/api/model/provider/queryList', {params})
}

// 新增制造商
export const addManufacturer = (data) => {
    return request.post('/api/model/provider', data)
}

// 编辑制造商
export const editManufacturer = (data) => {
    return request.put('/api/model/provider', data)
}

// 删除制造商
export const deleteManufacturer = (params) => {
    return request.delete('/api/model/provider', {params})
}

// 测试制造商
export const testManufacturer = (data) => {
    return request.post('/api/model/provider/refresh', data)
}

// 新增模型
export const addModel = (data) => {
    return request.post('/api/llm/model', data)
}

// 编辑模型
export const editModel = (data) => {
    return request.put('/api/llm/model', data)
}

// 删除模型
export const deleteModel = (params) => {
    return request.delete('/api/llm/model', {params})
}