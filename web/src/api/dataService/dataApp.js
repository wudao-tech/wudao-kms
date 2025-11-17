import request from '@/utils/request';

// 应用列表-不分页
export function getAppList(data) {
  return request({
    url: '/open/client/treeList',
    method: 'post',
    data: data
  });
}
// 应用列表-不分页
export function getAuthedAppList() {
  return request({
    url: '/open/client/authedTreeList',
    method: 'post'
  });
}

// 获取应用的类别列表
export function getListTags() {
  return request({
    url: '/open/client/listTags',
    method: 'get'
  });
}

//创建应用
export function createClient(data) {
  return request({
    url: '/open/client',
    method: 'post',
    data: data
  });
}

//修改应用
export function updateClient(data) {
  return request({
    url: '/open/client',
    method: 'put',
    data: data
  });
}
//删除应用
export function delClient(ids) {
  return request({
    url: `/open/client/${ids}`,
    method: 'delete'
  });
}

//获取全部可选API列表
export function allApiList() {
  return request({
    url: `/open/api/treeList`,
    method: 'POST',
    data: {}
  });
}

//获取y由管理权限的API列表
export function authedApiList() {
  return request({
    url: `/open/api/authedTreeList`,
    method: 'POST'
  });
}
//根据应用获取授权的接口一览
export function getApiListByClient(clientId) {
  return request({
    url: `/open/apiAuth/getListByClientId/${clientId}`,
    method: 'GET'
  });
}
// 导出接口文档
export function exportDocument(clientId) {
  return request({
    url: '/open/client/document/' + clientId,
    method: 'get'
  });
}
// 接口文档预览
export function previewDocument(clientId) {
  return request({
    url: '/open/client/document/preview/' + clientId,
    method: 'get'
  });
}

// 获取单个app详情
export function getInfo(data) {
  return request({
    url: '/open/client/' + data,
    method: 'GET'
  });
}
// 启用/停用
export function enableFlag(data) {
  return request({
    url: '/open/client/enableFlag',
    method: 'POST',
    data: data
  });
}
