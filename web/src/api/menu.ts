import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { RouteRecordRaw } from 'vue-router';

// 获取路由
export function getRouters(): AxiosPromise<RouteRecordRaw[]> {
  return request({
    url: '/api/user/profile/routers',
    method: 'get'
  });
}


// /api/forward/edge-ai/modelOps/ops/listOrigin
export const fileUpload = (file: any, type = 'file', format = 'png') => {
  const formData = new FormData()
  formData.append(type, file)
  formData.append('module', 'maintenance')
  formData.append('format', format)
  return request({
    method: 'POST',
    url: '/api/file/upload',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
