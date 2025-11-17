import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { DeptForm, DeptQuery, DeptVO } from './types';

// 查询部门列表
export const listDept = (query?: DeptQuery) => {
  return request({
    url: '/api/dept/list',
    method: 'get',
    params: query
  });
};

// 查询部门列表（排除节点）
export const listDeptExcludeChild = (deptId: string | number): AxiosPromise<DeptVO[]> => {
  return request({
    url: '/api/dept/list/exclude?deptId=' + deptId,
    method: 'get'
  });
};

// 查询部门详细
export const getDept = (deptId: string | number): AxiosPromise<DeptVO> => {
  return request({
    url: '/api/dept?deptId=' + deptId,
    method: 'get'
  });
};

// 查询部门下拉树结构
export const treeselect = (): AxiosPromise<DeptVO[]> => {
  return request({
    url: '/api/dept/tree/select',
    method: 'get'
  });
};

// 新增部门
export const addDept = (data: DeptForm) => {
  return request({
    url: '/api/dept',
    method: 'post',
    data: data
  });
};

// 修改部门
export const updateDept = (data: DeptForm) => {
  return request({
    url: '/api/dept',
    method: 'put',
    data: data
  });
};

// 删除部门
export const delDept = (deptId: number | string) => {
  return request({
    url: '/api/dept?deptId=' + deptId,
    method: 'delete'
  });
};
