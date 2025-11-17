import request from '@/utils/request';

// 文件夹及API列表
export function getApiFolderList() {
  return request({
    url: '/open/apiGroup/treeList',
    method: 'post',
    data: {}
  });
}

// 创建文件夹信息
export function createGroup(data) {
  return request({
    url: '/open/apiGroup',
    method: 'post',
    data: data
  });
}
//删除文件夹
export function delGroup(id) {
  return request({
    url: `/open/apiGroup/${id}`,
    method: 'delete'
  });
}
// 修改文件夹信息
export function updateGroup(data) {
  return request({
    url: '/open/apiGroup',
    method: 'PUT',
    data: data
  });
}

// 获取单个api详情
export function getInfo(data) {
  return request({
    url: '/open/api/' + data,
    method: 'GET'
  });
}
// 创建API
export function createApiInfo(data) {
  return request({
    url: '/open/api',
    method: 'post',
    data: data
  });
}
// 修改API
export function updateApiInfo(data) {
  return request({
    url: '/open/api',
    method: 'PUT',
    data: data
  });
}

// 删除API
export function deleteApiInfo(apiIds) {
  return request({
    url: `/open/api/${apiIds}`,
    method: 'DELETE'
  });
}

// API上线
export function updateApiOnline(data) {
  return request({
    url: '/open/api/online',
    method: 'POST',
    data: data
  });
}
// API下线
export function updateApiOffline(data) {
  return request({
    url: '/open/api/offline',
    method: 'POST',
    data: data
  });
}
// testApi
export function testApi(data) {
  return request({
    url: '/open/api/test',
    method: 'POST',
    data: data
  });
}
// listColumns
export function sqllistColumns(data) {
  return request({
    url: '/open/api/table/columns',
    method: 'POST',
    data: data
  });
}
// 获取数据库表的列和列的类型
export function listColumns(dbName, schema, tableName) {
  return request({
    url: '/open/api/table/columns/' + dbName + '/' + schema + '/' + tableName,
    method: 'POST'
  });
}
