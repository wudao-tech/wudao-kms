import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { DictDataForm, DictDataQuery, DictDataVO } from './types';
// 根据字典类型查询字典数据信息
export function getDicts(dictType: string): AxiosPromise<DictDataVO[]> {
  return request({
    url: '/api/dictData/type?type=' + dictType,
    method: 'get'
  });
}

// 查询字典数据列表
export function listData(query: DictDataQuery): AxiosPromise<DictDataVO[]> {
  return request({
    url: '/api/dictData/list',
    method: 'get',
    params: query
  });
}

// 查询字典数据详细
export function getData(data): AxiosPromise<DictDataVO> {
  return request({
    url: '/api/dictData',
    method: 'get',
    params: data
  });
}

// 新增字典数据
export function addData(data: DictDataForm) {
  return request({
    url: '/api/dictData',
    method: 'post',
    data: data
  });
}

// 修改字典数据
export function updateData(data: DictDataForm) {
  return request({
    url: '/api/dictData',
    method: 'put',
    data: data
  });
}

// 删除字典数据
export function delData(data) {
  return request({
    url: '/api/dictData',
    method: 'delete',
    params: data
  });
}
