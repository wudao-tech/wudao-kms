import request from '@/utils/request';

// 公共层 || 应用层tree
export function logicLevelTree(query) {
  return request({
    url: '/data/store/logic/level/folderAndTable',
    method: 'get',
    params: query
  });
}
