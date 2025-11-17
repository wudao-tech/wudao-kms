import request from '@/utils/request';

export function propertyContent() {
  return request({
    url: '/custom/table/getUserCustomTables',
    method: 'get'
  });
}

export function propertyMyContent() {
  return request({
    url: '/custom/table/getUserCustomManageTables',
    method: 'get'
  });
}

// 待我审批
export function waitingMyApproval(query) {
  return request({
    url: '/custom/table/getPendingApproval',
    method: 'get',
    params: query
  });
}
// 我的申请
export function waitingMyApplyfor(query) {
  return request({
    url: '/custom/table/getMyRequest',
    method: 'get',
    params: query
  });
}
// 我的处理
export function waitingMyHandle(query) {
  return request({
    url: '/custom/table/getMyProcessed',
    method: 'get',
    params: query
  });
}

// 通过驳回
export function passThrough(query) {
  return request({
    url: '/custom/table/updateRlaCustomTableByRalId',
    method: 'get',
    params: query
  });
}

// 搜索-统计
export function searchStatistics(data) {
  return request({
    url: '/custom/table/statistics',
    method: 'post',
    data: data
  });
}

// 搜索-列表
export function searchList(query) {
  return request({
    url: `/custom/table/search`,
    method: 'get',
    params: query
  });
}

// 搜索-获取最热
export function recentlyHot() {
  return request({
    url: `/dataFactory/viewLog/getPopular`,
    method: 'get'
  });
}

// 搜索-获取最近访问
export function recentlyVisit() {
  return request({
    url: `/dataFactory/viewLog/getLatest`,
    method: 'get'
  });
}

// 搜索-获取最新
export function recentlyNew() {
  return request({
    url: `/dataFactory/viewLog/getNewest`,
    method: 'get'
  });
}
