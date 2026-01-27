import request from '@/utils/request';
import { AxiosPromise } from 'axios';

export interface ApiKeyQuery {
  pageNum?: number;
  pageSize?: number;
}

export interface ApiKeyVO {
  id?: string | number;
  name?: string;
  apiKey?: string;
  expireTime?: string;
  createdAt?: string;
  lastUsedTime?: string;
}

export interface ApiKeyForm {
  name?: string;
  expireTime?: string;
}

export interface CreateApiKeyResponse {
  apiKey: string;
}

/**
 * 查询API密钥列表
 */
export const listApiKey = (query: ApiKeyQuery): AxiosPromise<{ data: ApiKeyVO[]; total: number }> => {
  return request({
    url: '/api/api_key/list',
    method: 'get',
    params: query
  });
};

/**
 * 创建API密钥
 */
export const createApiKey = (data: ApiKeyForm): AxiosPromise<CreateApiKeyResponse> => {
  return request({
    url: '/api/api_key',
    method: 'post',
    data
  });
};

/**
 * 删除API密钥
 */
export const deleteApiKey = (id: string | number): AxiosPromise<void> => {
  return request({
    url: `/api/api_key`,
    method: 'delete',
    params: {
      id
    }
  });
};

/**
 * 编辑API密钥
 */
export const updateApiKey = (data: ApiKeyForm): AxiosPromise<void> => {
  return request({
    url: `/api/api_key`,
    method: 'put',
    data
  });
};

/**
 * 详情API密钥
 */
export const detailApiKey = (id: string | number): AxiosPromise<ApiKeyVO> => {
    return request({
      url: `/api/api_key`, 
      method: 'get',
      params: {
        id
      }
    });
  };

