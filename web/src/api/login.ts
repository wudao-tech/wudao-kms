import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LoginData, LoginResult, VerifyCodeResult, TenantInfo } from './types';
import { UserInfo } from '@/api/system/user/types';
import { func } from 'vue-types';

// pc端固定客户端授权id
const clientId = import.meta.env.VITE_APP_CLIENT_ID;

/**
 * @param data {LoginData}
 * @returns
 */
export function login(data: LoginData): AxiosPromise<LoginResult> {
  console.log('data', data);
  const params = {
    ...data,
    // clientId: data.clientId || clientId,
    // grantType: data.grantType || 'password'
  };
  return request({
    url: '/api/auth/login',
    // headers: {
    //   isToken: false,
    //   // isEncrypt: true,
    //   repeatSubmit: false
    // },
    method: 'post',
    data: params,
    // 获取完整响应，包括 headers
    validateStatus: () => true,
    transformResponse: [(data, headers, status) => {
      return {
        data: data,
        headers: headers,
        status: status
      };
    }]
  });
}

// 注册方法
export function register(data: any) {
  const params = {
    ...data,
    clientId: clientId,
    grantType: 'password'
  };
  return request({
    url: '/api/auth/register',
    headers: {
      isToken: false,
      // isEncrypt: true,
      repeatSubmit: false
    },
    method: 'post',
    data: params
  });
}

/**
 * 注销
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'get'
  });
}

/**
 * 获取验证码
 */
export function getCodeImg(): AxiosPromise<VerifyCodeResult> {
  return request({
    url: '/api/auth/code',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  });
}

/**
 * 第三方登录
 */
export function callback(data: LoginData): AxiosPromise<any> {
  const LoginData = {
    ...data,
    clientId: clientId,
    grantType: 'social'
  };
  return request({
    url: '/api/auth/social/callback',
    method: 'post',
    data: LoginData
  });
}

// 获取用户详细信息
export function getInfo(): AxiosPromise<UserInfo> {
  return request({
    url: '/api/user/profile/info',
    method: 'get'
  });
}

// 获取租户列表
export function getTenantList(): AxiosPromise<TenantInfo> {
  return request({
    url: '/api/auth/tenant/list',
    headers: {
      isToken: false
    },
    method: 'get'
  });
}
// 生成邀请链接
export function inviteLink(data) {
  return request({
    url: '/api/invite',
    method: 'post',
    data
  });
}
// 邀请记录
export function inviteLinkList(query) {
  return request({
    url: '/api/invite/list',
    method: 'get',
    params: query
  });
}

// 手机验证码
export function getPhoneCode(code) {
  return request({
    url: '/api/resource/sms/code',
    method: 'get',
    params: {
      phonenumber: code
    }
  });
}

// 邮箱验证码
export function getEmailCode(code) {
  return request({
    url: '/api/resource/email/code',
    method: 'get',
    params: {
      email: code
    }
  });
}

// 租户用户关系
export function getTenantInfo(query) {
  return request({
    url: '/api/user/getTenantInfo',
    method: 'post',
    data: query
  });
}

// 邀请链接信息查询
export function inviteLinkInfo(code) {
  return request({
    url: `/api/invite/${code}`,
    method: 'get'
  });
}

// 确认邀请加入租户
export function submitTenant(query) {
  return request({
    url: '/api/auth/tenant_user_add',
    method: 'post',
    data: query
  });
}

// 获取图片验证码
export function getImageCode() {
  return request({
    url: '/api/public/captcha',
    method: 'get'
  });
}
// 短信验证
export function getSmsCode(data) {
  return request({
    url: '/api/public/sms/send',
    method: 'post',
    params: data
  });
}

// 新注册
export function newRegister(data) {
  return request({
    url: '/register',
    method: 'post',
    data
  });
}

// 物道单点登
export function wudaoLogin() {
  return request({
    url: '/api/oauth/authorize/wudao',
    method: 'get'
  });
}

// 物道单点登回调
export function wudaoCallback(data) {
  return request({
    url: '/api/oauth/login/wudao',
    method: 'post',
    params: data,
    validateStatus: () => true,
    transformResponse: [(data, headers, status) => {
      return {
        data: data,
        headers: headers,
        status: status
      };
    }]
  });
}