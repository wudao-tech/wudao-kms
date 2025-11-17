/** 查询保存的页面布局，即保存的显示类的信息 */
import request from '@/utils/request';

// 分页查询columnSetting列表
export function listColumnSetting(query) {
  return request({
    url: '/aps/columnSetting/list',
    method: 'get',
    params: query
  });
}
// 分页查询columnSetting列表
export function treeListColumnSetting(query) {
  return request({
    url: '/aps/columnSetting/treeList',
    method: 'get',
    params: query
  });
}

// 查询columnSetting详细
export function getColumnSetting(columnSettingId) {
  return request({
    url: '/aps/columnSetting/' + columnSettingId,
    method: 'get'
  });
}

// 新增columnSetting
export function addColumnSetting(data) {
  return request({
    url: '/aps/columnSetting',
    method: 'post',
    data: data
  });
}

// 修改columnSetting
export function updateColumnSetting(data) {
  return request({
    url: '/aps/columnSetting',
    method: 'put',
    data: data
  });
}

// 删除columnSetting
export function delColumnSetting(columnSettingId) {
  return request({
    url: '/aps/columnSetting/' + columnSettingId,
    method: 'delete'
  });
}

// 导出columnSetting
export function exportColumnSetting(query) {
  return request({
    url: '/aps/columnSetting/export',
    method: 'get',
    params: query
  });
}
