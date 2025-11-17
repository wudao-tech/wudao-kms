<template>
  <div class="login">
    <div class="pos_left">
      <svg-icon icon-class="platform_icon1" style="font-size: 34px" />
      <div style="margin-left: 20px; color: #6b05a8">KMS</div>
    </div>
    <el-form ref="loginRef" :model="registerForm" :rules="loginRules" class="login-form">
      <div class="login_invite">
        Hi,
        <div>管理员邀请你加入"KMS-XXX组织"</div>
      </div>
      <div style="padding: 20px 56px 0 56px" v-if="isRegister">
        <div style="font-size: 28px; font-weight: 700; margin-bottom: 25px">注册</div>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" type="text" size="large" auto-complete="off" placeholder="账号">
            <template #prefix><span style="color: #333; font-size: 16px; width: 70px">用户名</span></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="loginForm.email" type="text" size="large" auto-complete="off" placeholder="邮箱">
            <template #prefix><span style="color: #333; font-size: 16px; width: 70px">邮箱</span></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" size="large" auto-complete="off" placeholder="密码" @keyup.enter="handleLogin">
            <template #prefix>
              <span style="color: #333; font-size: 16px; width: 70px">密码</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" size="large" auto-complete="off" placeholder="密码" @keyup.enter="handleLogin">
            <template #prefix>
              <span style="color: #333; font-size: 16px; width: 70px">确认密码</span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item style="width: 100%">
          <el-button :loading="loading" size="large" style="width: 100%; background: #6b05a8; color: #fff" @click.prevent="handleLogin">
            <span v-if="!loading">注册</span>
            <span v-else>登 录 中...</span>
          </el-button>
          <div style="font-size: 14px">
            登录即代表同意<span style="color: blue; margin: 0 3px">用户协议</span>和<span style="color: blue; margin: 0 3px">隐私政策</span>
          </div>
        </el-form-item>
      </div>
      <div style="padding: 20px 56px 0 56px" v-else>
        <el-form-item prop="username" style="margin-top: 62px">
          <el-input v-model="registerForm.username" type="text" size="large" auto-complete="off" placeholder="账号">
            <template #prefix><span style="color: #333; font-size: 16px; width: 70px">姓名</span></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" type="text" size="large" auto-complete="off" placeholder="邮箱">
            <template #prefix><span style="color: #333; font-size: 16px; width: 70px">邮箱</span></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" size="large" auto-complete="off" placeholder="电话" @keyup.enter="handleAdd">
            <template #prefix>
              <span style="color: #333; font-size: 16px; width: 70px">电话</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item style="width: 100%">
          <el-button :loading="loading" size="large" style="width: 100%; background: #6b05a8; color: #fff" @click.prevent="handleAdd">
            <span>立即加入</span>
          </el-button>
          <div style="font-size: 14px">
            加入即代表同意<span style="color: blue; margin: 0 3px">用户协议</span>和<span style="color: blue; margin: 0 3px">隐私政策</span>
          </div>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { getCodeImg, getTenantList, register } from '@/api/login';
import { authBinding } from '@/api/system/social/auth';
import { useUserStore } from '@/store/modules/user';
import { to } from 'await-to-js';

const userStore = useUserStore();
const router = useRouter();
const passwordRef = ref(null);
const isRegister = ref(true);
const wordForm = ref({
  phone: '',
  code: '',
  password: '',
  aginpassword: ''
});
const wordRules = {
  phone: [{ required: true, trigger: 'blur', message: '请输入手机号' }],
  code: [{ required: true, trigger: 'blur', message: '请输入验证码' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  aginpassword: [{ required: true, trigger: 'blur', message: '请再次确认密码' }]
};

const registerForm = ref({
  // tenantId: '',
  username: '',
  password: '',
  rememberMe: false,
  code: '',
  uuid: ''
});

const loginRules = {
  // tenantId: [{ required: true, trigger: 'blur', message: '请输入您的公司名称' }],
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  code: [{ required: true, trigger: 'blur', message: '请输入验证码' }]
};

const codeUrl = ref('');
const loading = ref(false);
// 登陆方式切换
const active = ref(2);
// 是否是邀请
const invite = ref(false);
const redirect = ref(undefined);
// 验证码开关
const captchaEnabled = ref(true);
// 租户开关
const tenantEnabled = ref(true);

const loginRef = ref();
// 租户列表
const tenantList = ref([]);
// 密码弹窗
const dialogVisible = ref(false);

watch(
  () => router.currentRoute.value,
  (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
  },
  { immediate: true }
);
// 忘记密码
const forgot = () => {
  dialogVisible.value = true;
};
const handleClose = () => {
  dialogVisible.value = false;
  wordForm.value = {
    phone: '',
    code: '',
    password: '',
    aginpassword: ''
  };
  passwordRef.value.resetFields();
};
const updatePassword = () => {
  passwordRef.value?.validate(async (valid, fields) => {
    if (valid) {
    }
  });
};

const changeTab = (val) => {
  active.value = val;
};

const handleLogin = () => {
  // loginRef.value?.validate(async (valid, fields) => {
  //   if (valid) {
  //     loading.value = true;
  //     isRegister.value = false;
  //   } else {
  //     console.log('error submit!', fields);
  //   }
  // });
  isRegister.value = false;
};

const handleAdd = () => {
  loginRef.value?.validate(async (valid, fields) => {
    if (valid) {
    } else {
      console.log('error submit!', fields);
    }
  });
};

/**
 * 获取验证码
 */
const getCode = async () => {
  const res = await getCodeImg();
  const { data } = res;
  captchaEnabled.value = data.captchaEnabled === undefined ? true : data.captchaEnabled;
  if (captchaEnabled.value) {
    codeUrl.value = 'data:image/gif;base64,' + data.img;
    loginForm.value.uuid = data.uuid;
  }
};

const getLoginData = () => {
  // const tenantId = localStorage.getItem('tenantId');
  const username = localStorage.getItem('username');
  const password = localStorage.getItem('password');
  const rememberMe = localStorage.getItem('rememberMe');
  loginForm.value = {
    // tenantId: tenantId === null ? String(loginForm.value.tenantId) : tenantId,
    username: username === null ? String(loginForm.value.username) : username,
    password: password === null ? String(loginForm.value.password) : String(password),
    rememberMe: rememberMe === null ? false : Boolean(rememberMe)
  };
};

/**
 * 获取租户列表
 */
const initTenantList = async () => {
  const { data } = await getTenantList();
  tenantEnabled.value = data.tenantEnabled === undefined ? true : data.tenantEnabled;
  if (tenantEnabled.value) {
    tenantList.value = data.voList;
    if (tenantList.value != null && tenantList.value.length !== 0) {
      loginForm.value.tenantId = tenantList.value[0].tenantId;
    }
  }
};

onMounted(() => {
  getCode();
  // initTenantList();
  getLoginData();
});
</script>

<style lang="scss" scoped>
.element {
  cursor: pointer;
}

.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url('../assets/images/login_back.png');
  background-size: cover;
  position: relative;
  font-size: 16px;
}

.pos_left {
  position: absolute;
  left: 33px;
  top: 24px;
  display: flex;
  align-items: center;
  font-size: 24px;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #333333;
  font-size: 28px;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 488px;
  height: 630px;
  box-shadow: 0px 4px 20px 0px rgba(78, 29, 108, 0.12);
  .el-input {
    height: 42px;

    input {
      height: 42px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}
.actives {
  color: #6b05a8;
  font-weight: bold;
  border-bottom: 2px solid #6b05a8;
  padding-bottom: 5px;
}
.login_invite {
  height: 95px;
  width: 100%;
  line-height: 30px;
  padding: 20px 56px;
  background-image: url('../assets/images/invite_back.png');
}

.login-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.login-code-img {
  height: 40px;
  padding-left: 12px;
}
// :deep(.el-input__prefix-inner) {
//   > span {
//     text-align: left;
//   }
// }
</style>
