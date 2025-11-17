import * as Vue from 'vue';
//2.新创建一个vue实例
let vue = {};
const StorageUtil = {
  // ==================sessionsTorage设置缓存================
  // 设置缓存
  sessionSet: function (name, data) {
    localStorage.removeItem(name);
    localStorage.setItem(name, JSON.stringify(data));
  },
  // 获取缓存
  sessionGet: function (name) {
    if (localStorage.getItem(name) != 'undefined') return JSON.parse(localStorage.getItem(name));
    return null;
  },
  // 清除缓存
  sessionRemove: function (name) {
    localStorage.removeItem(name);
  },
  // ==================localStorage设置缓存==================
  // 设置缓存
  localSet: function (name, data) {
    localStorage.removeItem(name);
    localStorage.setItem(name, JSON.stringify(data));
  },
  // 获取缓存
  localGet: function (name) {
    if (localStorage.getItem(name) != 'undefined') return JSON.parse(localStorage.getItem(name));
    return null;
  },
  // 清除缓存
  localRemove: function (name) {
    localStorage.removeItem(name);
  },
  //获取用户token
  getAccessToken() {
    let etlUserInfo;
    try {
      etlUserInfo = StorageUtil.sessionGet('etlUserInfo');
      if (!etlUserInfo) {
        vue.$message.closeAll();
        vue.$message.error('登录失效！！');
        vue.$router.push({
          name: 'Login'
        });
        return;
      }
      return etlUserInfo.accessToken;
    } catch (err) {
      vue.$message.closeAll();
      vue.$message.error('登录失效！！');
      vue.$router.push({
        name: 'Login'
      });
      return;
    }
  },
  getUserInfo() {
    let etlUserInfo;
    try {
      etlUserInfo = StorageUtil.sessionGet('etlUserInfo');
      if (!etlUserInfo) {
        vue.$message.closeAll();
        vue.$message.error('登录失效！！');
        vm.$router.push({
          name: 'Login'
        });
        return;
      }
      return etlUserInfo;
    } catch (err) {
      vue.$message.closeAll();
      vue.$message.error('登录失效！！');
      vm.$router.push({
        name: 'Login'
      });
    }
    return null;
  },
  clearUserInfo() {
    this.sessionRemove('etlUserInfo');
  }
};
export default StorageUtil;
