import request from '@/utils/request';
import { ConfigForm, ConfigQuery, ConfigVO } from './types';
import { AxiosPromise } from 'axios';

// 查询参数列表
export function listConfig(query: ConfigQuery): AxiosPromise<ConfigVO[]> {
  return request({
    url: '/api/config/list',
    method: 'get',
    params: query
  });
}

// 查询参数详细
export function getConfig(code: string | number): AxiosPromise<ConfigVO> {
  return request({
    url: '/api/config?code=' + code,
    method: 'get'
  });
}

export function getConfigs(code: string | number): AxiosPromise<ConfigVO> {
  return request({
    url: '/dictData?code=' + code,
    method: 'get'
  });
}

// 根据参数键名查询参数值
export function getConfigKey(code: string): AxiosPromise<string> {
  return request({
    url: '/api/config?code=' + code,
    method: 'get'
  });
}

// 新增参数配置
export function addConfig(data: ConfigForm) {
  return request({
    url: '/api/config',
    method: 'post',
    data: data
  });
}
// 新增参数配置
export function addConfigEl(data: ConfigForm) {
  return request({
    url: '/api/config/value',
    method: 'get',
    params: data
  });
}

// 修改参数配置
export function updateConfig(data: ConfigForm) {
  return request({
    url: '/api/config',
    method: 'put',
    data: data
  });
}

// 修改参数配置
export function updateConfigByKey(key: string, value: any) {
  return request({
    url: '/api/config',
    method: 'put',
    data: {
      code: key,
      value: value
    }
  });
}

// 删除参数配置
export function delConfig(code: string | number | Array<string | number>) {
  return request({
    url: '/api/config?code=' + code,
    method: 'delete'
  });
}

// 刷新参数缓存
export function refreshCache() {
  return request({
    url: '/api/dictType/refreshCache',
    method: 'delete'
  });
}
