import request from '@/utils/request';

// 获取应用授权的接口信息详细信息
export function getListByApiId(data) {
  return request({
    url: '/open/apiAuth/getListByApiId/' + data,
    method: 'GET'
  });
}
// 获取应用授权的接口信息详细信息
export function getListByClientId(data) {
  return request({
    url: '/open/apiAuth/getListByClientId/' + data,
    method: 'GET'
  });
}
