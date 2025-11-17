import request from '@/utils/request';

/**
 * 获取数仓分层类型
 * @returns 数据字典中对应的数仓分层类型
 */
// export const getDataStoreTypeAPI = async () => {
//   try {
//     const response = await request.get(`/api/dict/data/type/data_store`);
//     return response.data;
//   } catch (error) {
//     console.error('获取出错:', error);
//     throw error;
//   }
// };

export function getDataStoreTypeAPI() {
  return request({
    url: `/api/dict/data/type/data_store`,
    method: 'get'
  });
}

/**
 * 数据分层请求函数
 */

/**
 * 获取数据分层list
 * @returns 数仓分层的数据列表
 */
export const getDataStoreLayeredListAPI = async () => {
  try {
    const response = await request.get(`/data/store/data/level/list`);
    return response.data;
  } catch (error) {
    console.error('获取出错:', error);
    throw error;
  }
};

/**
 * 数仓分层保存更新
 * @param {string} params.serviceName - 业务名称
 * @param {integer} params.dictCode - 数据字典id
 * @param {integer} params.chargeUserId - 负责人id
 * @param {string} [params.remark] - 备注
 */
export const saveDataStoreLevelAPI = (param) => request.post('/data/store/data/level/save', param).then((response) => response.data);

/**
 * 删除数据分层
 * @param {string} id - 要删除的数据分层ID
 */
export const deleteDataStoreLevelAPI = async (id) => {
  try {
    const response = await request.post(`/data/store/data/level/del/${id}`);
    return response.data;
  } catch (error) {
    console.error('删除数据分层出错:', error);
    throw error;
  }
};

/**
 * 修改数据分层
 * @param {Object} params - 包含修改所需的所有参数
 * @param {integer} params.id - 数据分层的唯一标识
 * @param {string} params.serviceName - 业务名称
 * @param {integer} params.dictCode - 数据字典id
 * @param {integer} params.chargeUserId - 负责人id
 * @param {string} [params.remark] - 备注
 */
export const updateDataStoreLevelAPI = async (params) => {
  try {
    const response = await request.post('/data/store/data/level/update', params);
    return response.data;
  } catch (error) {
    console.error('修改数据分层出错:', error);
    throw error;
  }
};

/**
 * 查询数据分层数据
 * @param {number} id - 数据分层的唯一标识
 */
export const getDataLayerByIdAPI = async (id) => {
  try {
    const response = await request.get('/data/store/data/level/get', { params: { id } });
    return response.data;
  } catch (error) {
    console.error('查询数据分层数据出错:', error);
    throw error;
  }
};

/**
 * 获取逻辑层数据
 * @param {Object} params - 包含查询所需的参数
 * @param {integer} params.dictCode - 数据字典 ID，必填
 * @param {integer} [params.parentId] - 父级 ID，可选
 * @param {integer} [params.levelType] - 分类类型（0 数据域，1 业务过程），可选
 * @param {string} [params.search] - 搜索关键词，可选
 */
export const getLogicLayerListAPI = async (params) => {
  try {
    const response = await request.get('/data/store/logic/level/list', { params });
    return response.data;
  } catch (error) {
    console.error('获取逻辑层数据出错:', error);
    throw error;
  }
};

/**
 * 保存逻辑层数据
 * @param {Object} params - 包含保存所需的所有参数
 * @param {integer} [params.id] - 数据分层的唯一标识（如果是编辑）
 * @param {integer} [params.parentId] - 父级 ID（如果有）
 * @param {string} params.cnName - 中文名称
 * @param {string} params.enName - 英文名称
 * @param {integer} [params.dictCode] - 数据字典 ID
 * @param {integer} params.chargeUserId - 负责人 ID
 * @param {integer} params.levelType - 分类类型（0 数据域，1 业务过程）
 * @param {string} [params.remark] - 备注
 */
export const saveLogicLayerAPI = async (params) => {
  try {
    const response = await request.post('/data/store/logic/level/save', params);
    return response.data;
  } catch (error) {
    console.error('保存逻辑层数据出错:', error);
    throw error;
  }
};

/**
 * 获取逻辑层树状结构
 * @param {Object} params - 包含查询所需的参数
 * @param {integer} params.dictCode - 数据字典 ID，必填
 * @param {integer} [params.parentId] - 父级 ID，可选
 * @param {integer} [params.levelType] - 分类类型（0 数据域，1 业务过程），可选
 * @param {string} [params.search] - 搜索关键词，可选
 */
export const getLogicLayerTreeAPI = async (params) => {
  try {
    const response = await request.get('/data/store/logic/level/tree', { params });
    return response.data;
  } catch (error) {
    console.error('获取逻辑层树状结构出错:', error);
    throw error;
  }
};

/**
 * 删除逻辑层数据
 * @param {string} id - 逻辑层的唯一标识
 */
export const deleteLogicLayerAPI = async (id) => {
  try {
    const response = await request.post(`/data/store/logic/level/del/${id}`);
    return response.data;
  } catch (error) {
    console.error('删除逻辑层数据出错:', error);
    throw error;
  }
};

/**
 * 查询逻辑层数据
 * @param {number} id - 逻辑层的唯一标识
 */
export const getLogicLayerByIdAPI = async (id) => {
  try {
    const response = await request.get('/data/store/logic/level/get', { params: { id } });
    return response.data;
  } catch (error) {
    console.error('查询逻辑层数据出错:', error);
    throw error;
  }
};

/**
 * 修改逻辑层数据
 * @param {Object} data - 包含修改逻辑层所需的所有数据
 */
export const updateLogicLayerAPI = async (data) => {
  try {
    const response = await request.post('/data/store/logic/level/update', data, {
      headers: {
        'Authorization': `Bearer {{token}}`, // 假设使用 Bearer token 认证
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  } catch (error) {
    console.error('修改逻辑层数据出错:', error);
    throw error;
  }
};

/**
 * 保存业务分层
 * @param {Object} params - 请求参数
 * @param {number} [params.id] - 业务分层 ID
 * @param {string} params.cnName - 中文名称
 * @param {string} params.enName - 英文名称
 * @param {number} [params.chargeUserId] - 负责人 ID
 * @param {string} [params.remark] - 备注
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const saveBusinessLayerAPI = async (params) => {
  try {
    const response = await request.post('/data/store/business/classify/save', params);
    return response.data;
  } catch (error) {
    console.error('保存业务分层出错:', error);
    throw error;
  }
};

/**
 * 保存关联数据域/数据集市
 * @param {Object} params - 请求参数
 * @param {number} params.classifyId - 分类 ID
 * @param {Array<number>} params.logicLevelIds - 逻辑层级 ID 数组
 * @param {number} params.dictCode - 数据字典 ID
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const saveRelationDataStoreAPI = async (params) => {
  try {
    const response = await request.post('/data/store/business/relation/save', params);
    return response.data;
  } catch (error) {
    console.error('保存关联数据域/数据集市出错:', error);
    throw error;
  }
};

/**
 * 删除业务分层
 * @param {number} id - 业务分层的 ID
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const deleteBusinessLayerAPI = async (id) => {
  try {
    const response = await request.post(`/data/store/business/classify/del/${id}`);
    return response.data;
  } catch (error) {
    console.error('删除业务分层出错:', error);
    throw error;
  }
};

/**
 * 删除关联数据域/数据集市
 * @param {number} id - 要删除的关联数据域/数据集市的 ID
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const deleteRelationDataStoreAPI = async (id) => {
  try {
    const response = await request.post(`/data/store/business/relation/del/${id}`);
    return response.data;
  } catch (error) {
    console.error('删除关联数据域/数据集市出错:', error);
    throw error;
  }
};

/**
 * 修改业务分层
 * @param {Object} params - 请求参数
 * @param {number} [params.id] - 业务分层的 ID
 * @param {string} params.cnName - 中文名称
 * @param {string} params.enName - 英文名称
 * @param {number} [params.chargeUserId] - 负责人 ID
 * @param {string} [params.remark] - 备注
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const updateBusinessLayerAPI = async (params) => {
  try {
    const response = await request.post('/data/store/business/classify/update', params);
    return response.data;
  } catch (error) {
    console.error('修改业务分层出错:', error);
    throw error;
  }
};

/**
 * 删除业务分类
 * @param {*} id 业务分类的唯一标识
 * @returns
 */
export const deleteBusinessClassify = async (id) => {
  try {
    const response = await request.post(`/data/store/business/classify/del/${id}`);

    return response.data;
  } catch (error) {
    console.error('Error deleting business classify:', error);
    throw error;
  }
};

/**
 * 获取关联数据域/数据集市
 * @param {Object} params - 请求参数
 * @param {number} params.dictCode - 数据字典代码
 * @param {number} params.classifyId - 业务分类 ID
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const getRelationDataStoresAPI = async (params) => {
  try {
    const response = await request.get('/data/store/business/relation/list', { params });
    return response.data;
  } catch (error) {
    console.error('获取关联数据域/数据集市出错:', error);
    throw error;
  }
};

/**
 * 查询业务分层
 * @param {number} id - 业务分层的 ID
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const getBusinessLayerAPI = async (id) => {
  try {
    const response = await request.get('/data/store/business/classify/get', { params: { id } });
    return response.data;
  } catch (error) {
    console.error('查询业务分层出错:', error);
    throw error;
  }
};

/**
 * 获取业务分类列表
 * @param {Object} [params={}] - 请求参数
 * @param {string} [params.search] - 搜索关键词
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
export const getBusinessClassificationsAPI = async (params = {}) => {
  try {
    const response = await request.get('/data/store/business/classify/list', {
      params: {
        ...params
      }
    });
    return response.data;
  } catch (error) {
    console.error('获取业务分类列表出错:', error);
    throw error;
  }
};

/**
 * 获取业务分类逻辑层
 * @param {Object} params - 请求参数
 * @param {number} params.dictCode - 数据字典代码
 * @param {number} params.classifyId - 业务分类 ID
 * @param {number} params.levelType - 分类类型（0 数据域，1 业务过程），可选
 * @returns {Promise<Object>} - 返回的 Promise 对象
 */
// export const fetchBusinessClassifyLevels = async (params) => {
//   try {
//     const response = await request.get('/data/store/logic/level/listBusinessClassify', { params });
//     return response.data;
//   } catch (error) {
//     console.error('获取业务分类逻辑层失败:', error);
//     throw error;
//   }
// };

export function fetchBusinessClassifyLevels(params) {
  return request({
    url: `/data/store/logic/level/listBusinessClassify`,
    method: 'get',
    params
  });
}

export function modelTree(params) {
  return request({
    url: '/data/store/logic/level/serviceLevelTree',
    method: 'get',
    params
  });
}

export function modelTreeTable(params) {
  return request({
    url: '/data/store/logic/level/tables',
    method: 'get',
    params
  });
}
