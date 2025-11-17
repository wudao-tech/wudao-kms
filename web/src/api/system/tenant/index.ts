import request from '@/utils/request';
import { TenantForm, TenantQuery, TenantVO } from './types';
import { AxiosPromise } from 'axios';

// 查询租户列表
export function listTenant(query: TenantQuery): AxiosPromise<TenantVO[]> {
  return request({
    url: '/api/tenant/list',
    method: 'get',
    params: query
  });
}

// 普通查询租户列表
export function userListTenant() {
  return request({
    url: '/api/user/profile/tenants',
    method: 'get'
  });
}

// 查询租户详细
export function getTenant(id: string | number): AxiosPromise<TenantVO> {
  return request({
    url: '/api/tenant/' + id,
    method: 'get'
  });
}

// 新增租户
export function addTenant(data: TenantForm) {
  return request({
    url: '/api/tenant',
    method: 'post',
    headers: {
      // isEncrypt: true,
      repeatSubmit: false
    },
    data: data
  });
}

// 修改租户
export function updateTenant(data: TenantForm) {
  return request({
    url: '/api/tenant',
    method: 'put',
    data: data
  });
}

// 租户状态修改
export function changeTenantStatus(id: string | number, tenantId: string | number, status: number) {
  const data = {
    id,
    tenantId,
    status
  };
  return request({
    url: '/api/tenant/changeStatus',
    method: 'put',
    data: data
  });
}

// 删除租户
export function delTenant(id: string | number | Array<string | number>) {
  return request({
    url: '/api/tenant/' + id,
    method: 'delete'
  });
}

// 平台动态切换租户
export function dynamicTenant(tenantId: string | number) {
  return request({
    url: '/api/tenant/dynamic?tenantId=' + tenantId,
    method: 'get'
  });
}

// 普通用户动态切换租户
export function userDynamicTenant(tenantId: string | number) {
  return request({
    url: '/auth/switch_tenant?tenantId=' + tenantId,
    method: 'get'
    // data: {
    //   tenantId
    // }
  });
}

// 清除动态租户
export function dynamicClear() {
  return request({
    url: '/api/tenant/dynamic/clear',
    method: 'get'
  });
}

// 同步租户套餐
export function syncTenantPackage(tenantId: string | number, packageId: string | number) {
  const data = {
    tenantId,
    packageId
  };
  return request({
    url: '/api/tenant/syncTenantPackage',
    method: 'get',
    params: data
  });
}
