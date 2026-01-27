import request from '@/utils/request';

// 公共层 || 应用层tree
export function logicLevelTree(query) {
  return request({
    url: '/data/store/logic/level/folderAndTable',
    method: 'get',
    params: query
  });
}

// 获取 Token 使用额度
export function getTokenQuota() {
  return request({
    url: '/api/model/provider/token/quota',
    method: 'get'
  });
}
