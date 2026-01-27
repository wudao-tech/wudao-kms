import request from '@/utils/request';
import { NoticeForm, NoticeQuery, NoticeVO } from './types';
import { AxiosPromise } from 'axios';
// 查询公告列表
export function listNotice(query: NoticeQuery): AxiosPromise<NoticeVO[]> {
  return request({
    url: '/api/notice/list',
    method: 'get',
    params: query
  });
}

// 用户接收消息
export function userReceive(query) {
  return request({
    url: '/api/user/notice/list',
    method: 'get',
    params: query
  });
}

// 消息已读
export function messageRead(data: any) {
  return request({
    url: '/api/user/notice/read',
    method: 'put',
    data: data
  });
}
// 查询公告详细
export function getNotice(noticeId: string | number): AxiosPromise<NoticeVO> {
  return request({
    url: '/api/notice?noticeId=' + noticeId,
    method: 'get'
  });
}

// 新增公告
export function addNotice(data: NoticeForm) {
  return request({
    url: '/api/notice',
    method: 'post',
    data: data
  });
}

// 修改公告
export function updateNotice(data: NoticeForm) {
  return request({
    url: '/api/notice',
    method: 'put',
    data: data
  });
}

// 删除公告
export function delNotice(noticeId: string | number | Array<string | number>) {
  return request({
    url: '/api/notice?noticeId=' + noticeId,
    method: 'delete'
  });
}

// 通知列表
export function getNoticeList(query) {
  return request({
    url: '/api/notification/list',
    method: 'get',
    params: query
  });
}

// 通知所有数量 
export function getNoticeTotal() {
  return request({
    url: '/api/notification/unread/count',
    method: 'get'
  });
}

// 标记已读
export function markRead(id) {
  return request({
    url: `/api/notification/${id}/read`,
    method: 'put'
  });
}

// 清除未读消息
export function clearUnreadNotice() {
  return request({
    url: '/api/notification/clear',
    method: 'delete'
  });
}
