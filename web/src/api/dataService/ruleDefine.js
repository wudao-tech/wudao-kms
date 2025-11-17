import request from '@/utils/request';

// 获取数据库接口
export function listDb() {
  return request({
    url: '/rule/dbList',
    method: 'get'
  });
}

// 获取数据库schema
export function listSchema(dbName) {
  return request({
    url: '/rule/schema/' + dbName,
    method: 'get'
  });
}

// 获取数据库schema下的表
export function listTables(dbName, schema) {
  return request({
    url: '/rule/tables/' + dbName + '/' + schema,
    method: 'get'
  });
}

// 获取数据库表的列和列的类型
export function listColumns(dbName, schema, tableName) {
  return request({
    url: '/rule/tableColumns/' + dbName + '/' + schema + '/' + tableName,
    method: 'get'
  });
}
// 获取数据库表的列和列的类型
export function sqllistColumns(data) {
  return request({
    url: '/rule/tableColumns',
    method: 'post',
    data: data
  });
}

// 添加规则
export function addRule(data) {
  return request({
    url: '/rule/addRule',
    method: 'post',
    data
  });
}
// 查询接收组
export function listReceiverGroup(query) {
  return request({
    url: '/notify/group/list',
    method: 'get',
    data: query
  });
}
// 执行一次接口
export function executeTest(query) {
  return request({
    url: '/rule/execute',
    method: 'post',
    data: query
  });
}
// 查询规则list
export function listRule(params) {
  return request({
    url: '/rule/list',
    method: 'get',
    params
  });
}
// 查询获取单个规则详情
export function getRuleInfo(id) {
  return request({
    url: '/rule/getRule/' + id,
    method: 'get'
  });
}
// 更新规则
export function updateRule(data) {
  return request({
    url: '/rule/editRule',
    method: 'put',
    data
  });
}
// 更新规则状态
export function updateRuleStatus(data) {
  return request({
    url: '/rule/editRuleStatus',
    method: 'put',
    data
  });
}
// 删除规则
export function deleteRule(id) {
  return request({
    url: '/rule/deleteRule/' + id,
    method: 'delete'
  });
}
// 获取 debug 数据 数据概览 tab
export function debugRule(id, debugRule, page, pageSize) {
  return request({
    url: `/rule/debug/${debugRule}/${id}/?page=${page}&pageSize=${pageSize}`,
    method: 'get'
  });
}
// ********************************新增SQL组件接口
//转换数据预览
export const transStepDataPreviewApi = (data) => {
  return request({
    url: '/rule/sql/data/preview',
    method: 'post',
    data: data
  });
};
export const transStepDataPreviewsqlApi = (data) => {
  return request({
    url: '/rule/tableColumns',
    method: 'post',
    data: data
  });
};
// 规则库复制
// 删除规则
export function copyRule(id) {
  return request({
    url: '/rule/copyRule/' + id,
    method: 'post'
  });
}
// DISS接口
export function getDissplant() {
  return request({
    url: '/rule/diss/plant',
    method: 'get'
  });
}
export function getDisskpi(id) {
  return request({
    url: '/rule/diss/kpi/' + id,
    method: 'get'
  });
}
export function getDissarea(id) {
  return request({
    url: '/rule/diss/area/' + id,
    method: 'get'
  });
}
export function getDissline(id) {
  return request({
    url: '/rule/diss/line/' + id,
    method: 'get'
  });
}
export function getDissuser(id) {
  return request({
    url: '/rule/diss/user/' + id,
    method: 'get'
  });
}
export function getDisssim(id) {
  return request({
    url: '/rule/diss/sim/' + id,
    method: 'get'
  });
}

export function getLineByArea(id, area) {
  return request({
    url: `/rule/diss/line/${id}/${area}`,
    method: 'get'
  });
}
