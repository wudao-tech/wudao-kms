import request from '@/utils/request';
import { PostForm, PostQuery, PostVO } from './types';
import { AxiosPromise } from 'axios';

// 查询岗位列表
export function listPost(query: PostQuery): AxiosPromise<PostVO[]> {
  return request({
    url: '/api/post/list',
    method: 'get',
    params: query
  });
}

// 查询岗位详细
export function getPost(postId: string | number): AxiosPromise<PostVO> {
  return request({
    url: '/api/post?postId=' + postId,
    method: 'get'
  });
}

// 获取岗位选择框列表
export function optionselect(deptId?: number | string, postIds?: (number | string)[]): AxiosPromise<PostVO[]> {
  return request({
    url: '/api/post/select',
    method: 'get',
    params: {
      postIds: postIds,
      deptId: deptId
    }
  });
}

// 新增岗位
export function addPost(data: PostForm) {
  return request({
    url: '/api/post',
    method: 'post',
    data: data
  });
}

// 修改岗位
export function updatePost(data: PostForm) {
  return request({
    url: '/api/post',
    method: 'put',
    data: data
  });
}

// 删除岗位
export function delPost(postId: string | number | (string | number)[]) {
  return request({
    url: '/api/post?postId=' + postId,
    method: 'delete'
  });
}
