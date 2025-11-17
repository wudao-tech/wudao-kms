import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MenuQuery, MenuVO, MenuForm, MenuTreeOption, RoleMenuTree } from './types';

// 查询菜单列表
export const listMenu = (query?: MenuQuery): AxiosPromise<MenuVO[]> => {
  return request({
    url: '/api/menu/tree',
    method: 'get',
    params: query
  });
};

// 查询菜单详细
export const getMenu = (menuId: string | number): AxiosPromise<MenuVO> => {
  return request({
    url: '/api/menu?menuId=' + menuId,
    method: 'get'
  });
};

// 查询菜单下拉树结构
export const treeselect = (): AxiosPromise<MenuTreeOption[]> => {
  return request({
    url: '/api/menu/tree/select',
    method: 'get'
  });
};

// 根据角色ID查询菜单下拉树结构
export const roleMenuTreeselect = (roleId: string | number): AxiosPromise<RoleMenuTree> => {
  return request({
    url: '/api/menu/roleMenuTreeselect?roleId=' + roleId,
    method: 'get'
  });
};

// 根据角色ID查询菜单下拉树结构
export const tenantPackageMenuTreeselect = (packageId: string | number): AxiosPromise<RoleMenuTree> => {
  return request({
    url: '/api/menu/tenantPackageMenuTreeselect?packageId=' + packageId,
    method: 'get'
  });
};

// 新增菜单
export const addMenu = (data: MenuForm) => {
  return request({
    url: '/api/menu',
    method: 'post',
    data: data
  });
};

// 修改菜单
export const updateMenu = (data: MenuForm) => {
  return request({
    url: '/api/menu',
    method: 'put',
    data: data
  });
};

// 删除菜单
export const delMenu = (menuId: string | number) => {
  return request({
    url: '/api/menu?menuId=' + menuId,
    method: 'delete'
  });
};
