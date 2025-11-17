import request from '@/utils/request';
import { TenantPkgForm, TenantPkgQuery, TenantPkgVO } from './types';
import { AxiosPromise } from 'axios';

// 查询租户套餐列表
export function listTenantPackage(query?: TenantPkgQuery): AxiosPromise<TenantPkgVO[]> {
  return request({
    url: '/api/tenant/package/list',
    method: 'get',
    params: query
  });
}

// 查询租户套餐下拉选列表
export function selectTenantPackage(): AxiosPromise<TenantPkgVO[]> {
  return request({
    url: '/api/tenant/package/selectList',
    method: 'get'
  });
}

// 查询租户套餐详细
export function getTenantPackage(packageId: string | number): AxiosPromise<TenantPkgVO> {
  return request({
    url: '/api/tenant/package?packageId=' + packageId,
    method: 'get'
  });
}

// 新增租户套餐
export function addTenantPackage(data: TenantPkgForm) {
  return request({
    url: '/api/tenant/package',
    method: 'post',
    data: data
  });
}

// 修改租户套餐
export function updateTenantPackage(data: TenantPkgForm) {
  return request({
    url: '/api/tenant/package',
    method: 'put',
    data: data
  });
}

// 租户套餐状态修改
export function changePackageStatus(packageId: number | string, status: number) {
  const data = {
    packageId,
    status
  };
  return request({
    url: '/api/tenant/package/changeStatus',
    method: 'put',
    data: data
  });
}

// 删除租户套餐
export function delTenantPackage(packageId: string | number | Array<string | number>) {
  return request({
    url: '/api/tenant/package?packageId=' + packageId,
    method: 'delete'
  });
}
