import request from '@/utils/request';

// 新增评论
export function addComment(target, data) {
  return request({
    url: `/api/comment/${target}`,
    method: 'post',
    data
  })
}

// 获取评论列表 
export function getCommentList(target, data) {
  return request({
    url: `/api/comment/${target}/list`,
    method: 'get',
    params: data
  })
}

// 删除回复
export function deleteReplys(target, data) {
  return request({
    url: `/api/comment/${target}/reply`,
    method: 'delete',
    params: data
  })
}

// 删除评论
export function deleteComments(target, data) {
  return request({
    url: `/api/comment/${target}`,
    method: 'delete',
    params: data
  })
}

// 回复列表
export function getReplyList(target, data) {
  return request({
    url: `/api/comment/${target}/reply/list`,
    method: 'get',
    params: data
  })
}

// 新增回复
export function addReply(target, data) {
  return request({
    url: `/api/comment/${target}/reply`,
    method: 'post',
    data
  })
}

