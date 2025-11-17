import { to } from 'await-to-js';
import { getToken, removeToken, setToken } from '@/utils/auth';
import { login as loginApi, logout as logoutApi, getInfo as getUserInfo, newRegister, wudaoCallback } from '@/api/login';
import { LoginData } from '@/api/types';
import defAva from '@/assets/images/user_img.png';
import store from '@/store';
import { getUserProfile } from '@/api/system/user'; 

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken());
  const name = ref('');
  const nickname = ref('');
  const userId = ref<string | number>('');
  const tenantId = ref<string>('');
  const avatar = ref('');
  const roles = ref<Array<string>>([]); // 用户角色编码集合 → 判断路由权限
  const permissions = ref<Array<string>>([]); // 用户权限编码集合 → 判断按钮权限

  /**
   * 登录
   * @param userInfo
   * @returns
   */
  const login = async (userInfo: LoginData) => {
    const [err, res] = await to(loginApi(userInfo));
    if (res) {
      // 从响应头中获取 token
      const accessToken = res.headers?.authorization;
      if (accessToken) {
        setToken(accessToken);
        token.value = accessToken;
      }
      
      // 返回原始数据，保持兼容性
      return Promise.resolve(res.data);
    }
    return Promise.reject(err);
  };
  const loginSSO = async (params) => {
    const [err, res] = await to(wudaoCallback(params));
    if (res) {
      console.log('res.headers', res.headers);
      // 从响应头中获取 token
      const accessToken = res.headers?.authorization;
      if (accessToken) {
        setToken(accessToken);
        token.value = accessToken;
      }
      
      // 返回原始数据，保持兼容性
      return Promise.resolve(res.data);
    }
    return Promise.reject(err);
  };

  const register = async (userInfo: LoginData) => {
    const [err, res] = await to(newRegister(userInfo));
    if (res) {
      // 从响应头中获取 token
      const accessToken = res.headers.authorization;
      if (accessToken) {
        setToken(accessToken);
        token.value = accessToken;
      }
      return Promise.resolve(res);
    }
    return Promise.reject(err);
  };

    // 获取用户信息
  const getInfo = async (): Promise<void> => {
    try {
      // 获取角色和权限信息
      const [err, res] = await to(getUserInfo());
      if (err) {
        console.error('获取用户角色信息失败:', err);
        throw err;
      }
      
      // 获取用户详细信息
      const res1 = await getUserProfile();
      localStorage.setItem('userInfo', JSON.stringify(res1.data));
      if (res && res1) {
        const data = res.data;
        const user = res1.data;
        
        // 设置角色和权限
        if (data.roles && data.roles.length > 0) {
          roles.value = data.roles;
          permissions.value = data.permissions;
        } else {
          roles.value = ['ROLE_DEFAULT'];
        }
        
        // 设置用户基本信息
        if (user) {
          const profile = user.avatar == '' || user.avatar == '/profile/avatar/admin.jpeg' ? defAva : user.avatar;
          console.log('profile', profile)
          name.value = user.username || '';
          nickname.value = user.nickname || '';
          avatar.value = profile;
          userId.value = user.id || '';
          tenantId.value = user.tenantId || '';
        } 
        
        return Promise.resolve();
      } else {
        throw new Error('获取用户信息失败：响应数据为空');
      }
    } catch (error) {
      console.error('获取用户信息过程中出错:', error);
      throw error;
    }
  };

  // 注销
  const logout = async (): Promise<void> => {
    await logoutApi();
    token.value = '';
    roles.value = [];
    permissions.value = [];
    removeToken();
  };

  const setAvatar = (value: string) => {
    avatar.value = value;
  };

  return {
    userId,
    tenantId,
    token,
    nickname,
    avatar,
    roles,
    permissions,
    login,
    register,
    getInfo,
    logout,
    setAvatar,
    loginSSO
  };
});

export default useUserStore;
// 非setup
export function useUserStoreHook() {
  return useUserStore(store);
}
