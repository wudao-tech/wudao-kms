import request from '@/utils/request';

/**
 * 获取列表数据
 * @param db
 * @param table
 * @param pageNum
 * @param pageSize
 * @param body
 * @returns {AxiosPromise}
 * ?dbId=${db}&table=${table}&pageNum=${pageNum}&pageSize=${pageSize}
 */
export function getPage(body) {
  return request({
    url: `/api/custom/table/getPage`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: body
  });
}

/**
 * 获取用户列表
 * @param userName
 * @returns {AxiosPromise}
 */
export function getUserList(deptId) {
  return request({
    url: `/api/custom/table/getUserList?deptId=${deptId}`,
    method: 'get'
  });
}

/**
 * 获取部门列表
 * @returns {AxiosPromise}
 */
export function getDeptList() {
  return request({
    url: `/api/custom/table/getDeptList`,
    method: 'get'
  });
}

/**
 * 添加数据
 * @param body
 * @returns {AxiosPromise}
 */
export function insert(body) {
  return request({
    url: `/api/custom/table/insert`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: body
  });
}

/**
 * 根据表查询用户
 * @param body
 * @returns {AxiosPromise}
 */
export function getUserByTableId(body) {
  return request({
    url: `/api/custom/table/getUserByTableId`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: body
  });
}

/**
 * 删除用户
 * @param body
 * @returns {AxiosPromise}
 */
export function updateRlaCustomTableByRalId(ralId, status) {
  return request({
    url: `/api/custom/table/updateRlaCustomTableByRalId?ralId=${ralId}&status=${status}`,
    method: 'get'
  });
}

/**
 * 获取数据层次
 * @returns {AxiosPromise}
 */
export function dbTopic() {
  return request({
    url: '/api/dbsource/dbTopic',
    method: 'get'
  });
}

/**
 * 同步配置
 * @returns {AxiosPromise}
 */
// export function syncCustomTable() {
//     return request({
//         url: '/api/dbsource/syncCustomTable',
//         method: 'post',
//     });
// }
export function syncCustomTable() {
  return request({
    url: '/api/dbsource/syncDbTable',
    method: 'post'
  });
}

/**
 * 设置主题域
 * @param {*} body
 * @returns
 */
export function tableTopicSetup(body) {
  return request({
    url: `/api/custom/table/tableTopicSetup`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: body
  });
}

/**
 * 获取用户角色列表
 * @returns {AxiosPromise}
 */
export function roleUsers() {
  return request({
    url: '/api/custom/table/roleUsers',
    method: 'get'
  });
}
