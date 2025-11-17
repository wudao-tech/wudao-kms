import { DeptVO } from './../dept/types';
import { RoleVO } from '@/api/system/role/types';
import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { UserForm, UserQuery, UserVO, UserInfoVO } from './types';
import { parseStrEmpty } from '@/utils/ruoyi';

/**
 * 查询用户列表
 * @param query
 */
export const listUser = (query: UserQuery): AxiosPromise<UserVO[]> => {
  return request({
    url: '/api/user/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询用户列表
 * @param query
 */
export const listUserTenant = (query: UserQuery): AxiosPromise<UserVO[]> => {
  return request({
    url: '/api/tenant/user/list',
    method: 'get',
    params: query
  });
};

/**
 * 通过用户ids查询用户
 * @param userIds
 */
export const optionSelect = (userIds: (number | string)[]): AxiosPromise<UserVO[]> => {
  return request({
    url: '/api/user/select?userIds=' + userIds,
    method: 'get'
  });
};

/**
 * 获取用户详情
 * @param id
 */
export const getUser = (id?: string | number): AxiosPromise<UserInfoVO> => {
  return request({
    url: `/api/user?userId=${id}`,
    method: 'get'
  });
};


/**
 * 新增用户
 */
export const addUser = (data: UserForm) => {
  return request({
    url: '/api/user',
    method: 'post',
    data: data
  });
};

/**
 * 新增用户
 */
export const addUserTenant = (data: UserForm) => {
  return request({
    url: '/api/tenant/user',
    method: 'post',
    data: data
  });
};

/**
 * 修改用户
 */
export const updateUser = (data: UserForm) => {
  return request({
    url: '/api/user',
    method: 'put',
    data: data
  });
};

/**
 * 修改用户
 */
export const updateUserTenant = (data: UserForm) => {
  return request({
    url: '/api/tenant/user',
    method: 'put',
    data: data
  });
};

/**
 * 删除用户
 * @param id 用户ID
 */
export const delUser = (id: Array<string | number> | string | number) => {
  return request({
    url: '/api/user?id=' + id,
    method: 'delete'
  });
};


/**
 * 用户密码重置
 * @param id 用户ID
 * @param password 密码
 */
export const resetUserPwd = (id: string | number, password: string) => {
  const data = {
    id,
    password
  };
  return request({
    url: '/api/user/password/reset',
    method: 'put',
    headers: {
      //   isEncrypt: true,
      repeatSubmit: false
    },
    data: data
  });
};

/**
 * 用户状态修改
 * @param id 用户ID
 * @param status 用户状态
 */
export const changeUserStatus = (id: number | string, status: number) => {
  const data = {
    id,
    status
  };
  return request({
    url: '/api/user/changeStatus',
    method: 'put',
    data: data
  });
};


/**
 * 查询用户个人信息
 */
export const getUserProfile = (): AxiosPromise<UserInfoVO> => {
  return request({
    url: '/api/user/profile',
    method: 'get'
  });
};

/**
 * 修改用户个人信息
 * @param data 用户信息
 */
export const updateUserProfile = (data: UserForm) => {
  return request({
    url: '/api/user/profile',
    method: 'put',
    data: data
  });
};

/**
 * 用户密码重置
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export const updateUserPwd = (data) => {
  return request({
    url: '/api/user/profile/updatePwd',
    method: 'put',
    headers: {
      // isEncrypt: true,
      repeatSubmit: false
    },
    params: data
  });
};

export const forgetUserPwd = (data) => {
  return request({
    url: '/api/auth/updatePwd',
    method: 'post',
    data: data
  });
}

/**
 * 用户头像上传
 * @param data 头像文件
 */
export const uploadAvatar = (data: FormData) => {
  return request({
    url: '/api/user/profile/avatar',
    method: 'post',
    data: data
  });
};

/**
 * 查询授权角色
 * @param id 用户ID
 */
export const getAuthRole = (userId: string | number): AxiosPromise<{ user: UserVO; roles: RoleVO[] }> => {
  return request({
    url: '/api/user/authRole?userId=' + userId,
    method: 'get'
  });
};


/**
 * 保存授权角色
 * @param data 用户ID
 */
export const updateAuthRole = (data: { userId: string; roleIds: string }) => {
  return request({
    url: '/api/user/authRole',
    method: 'put',
    params: data
  });
};


/**
 * 查询当前部门的所有用户信息
 * @param deptId
 */
export const listUserByDeptId = (query) => {
  return request({
    url: '/api/user/select',
    method: 'get',
    params: query
  });
};

/**
 * 查询部门下拉树结构
 */
export const deptTreeSelect = (): AxiosPromise<DeptVO[]> => {
  return request({
    // url: '/api/user/deptTree',
    url: '/api/dept/tree/select',
    method: 'get'
  });
};


export default {
  listUser,
  getUser,
  optionSelect,
  addUser,
  updateUser,
  delUser,
  resetUserPwd,
  changeUserStatus,
  getUserProfile,
  updateUserProfile,
  updateUserPwd,
  uploadAvatar,
  getAuthRole,
  updateAuthRole,
  deptTreeSelect,
  listUserByDeptId,
  listUserTenant,
  addUserTenant,
  updateUserTenant,
  forgetUserPwd
};
