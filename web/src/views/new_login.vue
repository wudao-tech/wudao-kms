<template>
  <div class="login">
    <div class="pos_left">
      <img src="@/assets/images/platform_icon1.png" alt="">
    </div>
    <div  style="display: flex; justify-content: center; align-items: center">
       <img src="../assets/images/login-poster.png" style="height: 650px; margin-right: 50px;margin-top: 25px" alt="">
      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <!-- <div>登录</div> -->
        <h3  class="title" style="margin-top: 40px; margin-left: 56px;" v-if="islogin === 1">欢迎使用</h3>
        <h3  class="title" style="margin-top: 40px; margin-left: 56px;" v-else-if="islogin === 2">欢迎注册</h3>
        <h3  class="title" style="margin-top: 20px; margin-left: 56px;" v-if="islogin !== 3">物道AI知识库</h3>
        <div v-if="islogin === 1" class="form-box">
          <div  style="margin-bottom: 20px; margin-top: 50px;">
            <!-- <span :class="{ actives: active === 1 }" class="element" style="margin-right: 32px" @click="changeTab(1)">验证码登录</span> -->
            <span :class="{ actives: active === 2 }" class="element" @click="changeTab(2)">密码登录</span>
          </div>
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" type="text" size="large" auto-complete="off" placeholder="账号" @blur="handleUserName">
              <template #prefix><span style="color: #333; font-size: 16px; width: 70px">账号</span></template>
            </el-input>
          </el-form-item>
          <el-form-item v-if="active === 2" prop="password">
            <el-input v-model="loginForm.password" type="password" size="large" auto-complete="off" placeholder="密码"  @keyup.enter="handleLogin">
              <template #prefix>
                <span style="color: #333; font-size: 16px; width: 70px">密码</span>
              </template>
              <template #suffix>
                <el-divider direction="vertical" /><el-button link style="font-size: 14px; width: 70px" @click="changeStatus(3)">忘记密码</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item v-if="active === 1" prop="code">
            <el-input v-model="loginForm.code" size="large" auto-complete="off" placeholder="验证码" @keyup.enter="handleLogin">
              <template #prefix><span style="color: #333; font-size: 16px; width: 70px">验证码</span></template>
              <template #suffix>
                <el-button link style="font-size: 14px; width: 70px" :disabled="isDisabled" @click="getYan">{{ btnText }}</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-row class="login-btn-box">
            <el-button :loading="loading" size="large" style="width: 100%; background: #6b05a8; color: #fff" @click.prevent="handleLogin">
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
            <div style="font-size: 14px;margin-top: 8px;display: flex; justify-content: space-between; align-items: center; width: 100%">
              <span>登录即代表同意<span style="color: blue; margin: 0 3px; cursor: pointer;" @click="openAgreement(1)">用户协议</span>和<span style="color: blue; margin: 0 3px; cursor: pointer;" @click="openAgreement(2)">隐私政策</span></span>
              <el-button link type="primary" style="font-size: 14px;"  @click="changeStatus(2)">前往注册</el-button>
            </div>
          </el-row>
        </div>

        <div v-else-if="islogin === 2" style="padding: 0 56px 0 56px">
          <el-form-item prop="username" style="margin-top: 62px">
            <el-input v-model="loginForm.username" type="text" size="large" auto-complete="off" placeholder="用户名大于5位">
              <template #prefix><span style="color: #333; font-size: 16px; width: 70px">用户名</span></template>
            </el-input>
          </el-form-item>
          <el-form-item  prop="password">
            <el-input v-model="loginForm.password" type="password" size="large" auto-complete="off" show-password placeholder="密码大于5位且只能输入数字字母下划线">
              <template #prefix>
                <span style="color: #333; font-size: 16px; width: 70px">密码</span>
              </template>
            </el-input>
          </el-form-item>
          <!-- <el-form-item  prop="aginpassword">
            <el-input v-model="loginForm.aginpassword" type="password" size="large" auto-complete="off" show-password placeholder="密码大于5位且只能输入数字字母下划线" >
              <template #prefix>
                <span style="color: #333; font-size: 16px; width: 70px">确认密码</span>
              </template>
            </el-input>
          </el-form-item> -->
          <el-form-item prop="phoneNumber">
            <el-input v-model="loginForm.phoneNumber" size="large" auto-complete="off" placeholder="手机号">
              <template #prefix>
                <span style="color: #333; font-size: 16px; width: 70px">手机号</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item  prop="smsCode">
            <el-input v-model="loginForm.smsCode" size="large" auto-complete="off" placeholder="验证码">
              <template #prefix><span style="color: #333; font-size: 16px; width: 70px">验证码</span></template>
              <template #suffix>
                <el-divider direction="vertical" /><el-button link style="font-size: 14px; width: 40px" :disabled="isDisabled" @click="getYan">{{ btnText }}</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button :loading="loading" size="large" style="width: 100%; background: #6b05a8; color: #fff" @click.prevent="handleLogin">
              <span>注册</span>
            </el-button>
            <div style="font-size: 14px; display: flex; justify-content: space-between; align-items: center; width: 100%">
              <span>注册即代表同意<span style="color: blue; margin: 0 3px; cursor: pointer;" @click="openAgreement(1)">用户协议</span>和<span style="color: blue; margin: 0 3px; cursor: pointer;" @click="openAgreement(2)">隐私政策</span></span>
              <el-button link type="primary" style="font-size: 14px;" @click="changeStatus(1)">前往登录</el-button>
            </div>
          </el-form-item>
        </div>
        <div v-else-if="islogin === 3" style="padding: 20px 56px 0 56px">
          <el-button link icon="Back"  @click="changeStatus(1)" style="margin-bottom: 100px">返回</el-button>
          <el-form-item prop="phoneNumber">
            <el-input v-model="loginForm.phoneNumber" size="large" auto-complete="off" placeholder="手机号">
              <template #prefix>
                <span style="color: #333; font-size: 16px; width: 70px">手机号</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item  prop="smsCode">
            <el-input v-model="loginForm.smsCode" size="large" auto-complete="off" placeholder="验证码">
              <template #prefix><span style="color: #333; font-size: 16px; width: 70px">验证码</span></template>
              <template #suffix>
                <el-divider direction="vertical" /><el-button link style="font-size: 14px; width: 40px" :disabled="isDisabled" @click="getYan">{{ btnText }}</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item  prop="password">
            <el-input v-model="loginForm.password" type="password" size="large" auto-complete="off" show-password placeholder="密码大于5位且只能输入数字字母下划线">
              <template #prefix>
                <span style="color: #333; font-size: 16px; width: 70px">新密码</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item  prop="aginpassword">
            <el-input v-model="loginForm.aginpassword" type="password" size="large" show-password auto-complete="off" placeholder="密码大于5位且只能输入数字字母下划线" >
              <template #prefix>
                <span style="color: #333; font-size: 16px; width: 70px">确认密码</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button :loading="loading" size="large" style="width: 100%; background: #6b05a8; color: #fff" @click.prevent="handleLogin">
              确定
            </el-button>
           
          </el-form-item>
        </div>
      </el-form>
    </div>

    <el-dialog v-model="dialogVisible" title="修改密码" width="500" :before-close="handleClose">
      <el-form ref="passwordRef" :model="wordForm" :rules="wordRules" label-width="100" label-position="left">
        <el-form-item prop="phone" label="手机号">
          <el-input v-model="wordForm.phone" auto-complete="off" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item prop="code" label="验证码">
          <el-input v-model="wordForm.code" auto-complete="off" placeholder="请输入验证码">
            <template #suffix>
              <span class="element" style="color: #333; font-size: 14px; width: 70px">发送验证码</span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password" label="新密码">
          <el-input v-model="wordForm.password" type="password" auto-complete="off" show-password placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item prop="aginpassword" label="确认密码">
          <el-input v-model="wordForm.aginpassword" type="password" auto-complete="off" show-password placeholder="请输入密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="updatePassword(passwordRef)"> 确认 </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="codeVisible" title="图片验证" width="300" @close="closeCode">
         <div style="display: flex; justify-content: center; align-items: center; flex-direction: column;">
            <img :src="codeImg" alt="">
            <el-input v-model="code" style="width: 150px; margin: 20px 0;" ></el-input>
            <el-button type="primary" @click="submitGetCode" style="width: 150px;">确定</el-button>
         </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { getImageCode, getSmsCode, newRegister, getEmailCode, getTenantInfo, inviteLinkInfo, submitTenant, wudaoCallback } from '@/api/login'
import { authBinding } from '@/api/system/social/auth'
import { useUserStore } from '@/store/modules/user'
import { to } from 'await-to-js'
import { forgetUserPwd } from '@/api/system/user'
import { getToken } from '@/utils/auth'

const userStore = useUserStore()
const router = useRouter()
const passwordRef = ref(null)
const wordForm = ref({
  phone: '',
  code: '',
  password: '',
  aginpassword: ''
})
const codeUrl = ref('')
const loading = ref(false)
// 登陆方式切换
const active = ref(2)




// 验证码
const codeImg = ref('')
const code = ref('')
const uuid = ref('')
const codeVisible = ref(false)


const redirect = ref(undefined)


const loginRef = ref()

// 密码弹窗
const dialogVisible = ref(false)
// 邀请的租户信息
const tenantInfo = ref({})

// 邀请链接携带的code
const linkCode = ref(null)
const wordRules = {
  phone: [{ required: true, trigger: 'blur', message: '请输入手机号' }],
  code: [{ required: true, trigger: 'blur', message: '请输入验证码' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  aginpassword: [{ required: true, trigger: 'blur', message: '请再次确认密码' }]
}

const loginForm = ref({
  username: '',
  password: '',
  phoneNumber: '',
  smsCode: '',
  aginpassword: '',
  useCookie: false,

})

const isDisabled = computed(() => {
  return !isPhone(loginForm.value.phoneNumber) || countdown.value > 0
})
const btnText = ref('发送')
const userType = ref(0)
const countdown = ref(0)
const islogin = ref(1)
const countdownTimer = ref(null)

const loginRules = {
  // tenantId: [{ required: true, trigger: 'blur', message: '请输入您的公司名称' }],
  username: [
    {
      required: true,
      trigger: 'blur',
      message: '请输入用户名'
    },
    {
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if  (value && (value.length < 4 || value.length > 15)) {
          callback(new Error('用户名长度必须在5-15位字符之间'))
        } else {
          callback()
        }
      }
    }
  ],
  password: [{ required: true, trigger: 'blur', 
    validator: (rule, value, callback) => {
      const isValid = /^[a-zA-Z0-9_]{6,}$/
      if (islogin.value === 1 ) { 
        if(value.length < 5) {
          callback(new Error('密码长度大于5位'))
        } else {
          callback()
        }
      }
      if (isValid.test(value)) {
          callback()
        } else {
          callback(new Error('密码长度大于5位且只能输入数字字母下划线'))
        }
     
    }  
  }],
  aginpassword: [{ 
    required: true, 
    trigger: 'blur', 
    validator: (rule, value, callback) => {
      if (value !== loginForm.value.password) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    } 
  }],
  smsCode: [{ required: true, trigger: 'blur', message: '请输入验证码' }],
  email: [
    {
      required: true,
      trigger: 'blur',
      validator: (rule, value, callback) => {
        const regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/
        if (regEmail.test(value)) {
          callback()
        } else {
          callback(new Error('请输入有效的邮箱'))
        }
      }
    }
  ],
  phoneNumber: [
    {
      required: true,
      trigger: 'blur',
      validator: (rule, value, callback) => {
        const phoneRegex = /^1[3-9]\d{9}$/
        if (phoneRegex.test(value)) {
          callback()
        } else {
          callback(new Error('请输入有效的手机号'))
        }
      }
    }
  ]
}

const isPhone = (phone) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

const isEmail = (email) => {
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  return emailRegex.test(email)
}

const handleUserName = () => {
  if (active.value === 1) {
    if (isPhone(loginForm.value.username) && !isEmail(loginForm.value.username)) userType.value = 1
    else if (!isPhone(loginForm.value.username) && isEmail(loginForm.value.username)) userType.value = 2
    else userType.value = 3
  }
}

const getYan = () => {
  codeVisible.value = true
  getImageCode().then((res) => {
    codeImg.value = 'data:image/png;base64,' + res.data.img
    uuid.value = res.data.uuid
  })
}

const submitGetCode = () => {
  console.log(loginForm.value)
  let params = {
    code: code.value,
    uuid: uuid.value,
    phoneNumber: loginForm.value.phoneNumber
  }
  getSmsCode(params).then((res) => {
    ElMessage.success('验证码已发送')
    closeCode()
    startCountdown()
  })
}

const startCountdown = () => {
  countdown.value = 60
  btnText.value = `${countdown.value}`
  
  countdownTimer.value = setInterval(() => {
    countdown.value--
    if (countdown.value > 0) {
      btnText.value = `${countdown.value}`
    } else {
      btnText.value = '发送'
      clearInterval(countdownTimer.value)
      countdownTimer.value = null
    }
  }, 1000)
}

const openAgreement = (type) => {
  const url = `${window.location.origin}/word?id=${type}`
  window.open(url, '_blank', 'width=800,height=600,scrollbars=yes,resizable=yes')
}

const closeCode = () => {
  code.value = ''
  uuid.value = ''
  codeImg.value = ''
  codeVisible.value = false
}

watch(
  () => router.currentRoute.value,
  (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect
  },
  { immediate: true }
)
// 忘记密码
const forgot = () => {
  dialogVisible.value = true
}
const handleClose = () => {
  dialogVisible.value = false
  wordForm.value = {
    phone: '',
    code: '',
    password: '',
    aginpassword: ''
  }
  passwordRef.value.resetFields()
}
const updatePassword = () => {
  passwordRef.value?.validate(async (valid, fields) => {
    if (valid) {
    }
  })
}

const changeTab = (val) => {
  active.value = val
}

const handleLogin = () => {
  loginRef.value?.validate(async (valid, fields) => {
    if (valid) {
      // 注册
      if (islogin.value === 2) {
        let params = {
          ...loginForm.value
        }
        delete params.aginpassword
        delete params.useCookie
        const [err, res] = await to(userStore.register(params))
        if (res.code === 'ok') {
          ElMessage.success('注册成功')
          islogin.value = 1
          loginForm.value = {
            username: '',
            password: '',
            phoneNumber: '',
            smsCode: '',
            aginpassword: '',
            useCookie: false,
          }
        }
      } else if (islogin.value === 1) {
        loading.value = true
        let params = {}
        if (active.value === 2) {
          params = {
            code: loginForm.value.code,
            uuid: loginForm.value.uuid,
            useCookie: loginForm.value.useCookie,
            pwd: {
              username: loginForm.value.username,
              password: loginForm.value.password
            }
          }
        } else {
        
        }
        const [err, res] = await to(userStore.login(params))
        if (res) {
          loading.value = false
            localStorage.removeItem('route_tab')
            const redirectUrl = '/'
            await router.push(redirectUrl)
          // }
        } else {
          loading.value = false
        }
      }   else {
         let params = {
           phoneNumber: loginForm.value.phoneNumber,
           code: loginForm.value.smsCode,
           password: loginForm.value.password
         } 
          let res= await forgetUserPwd(params)
          if (res.code === 'ok') {
            ElMessage.success('修改密码成功')
            islogin.value = 1
            loginForm.value = {
              username: '',
              password: '',
              phoneNumber: '',
              smsCode: '',
              aginpassword: '',
              useCookie: false
            }
          }
      }
    }
  })
}

const changeStatus = (val) => {
  islogin.value = val
  loginForm.value = {
    username: '',
    password: '',
    phoneNumber: '',
    smsCode: '',
    aginpassword: '',
    useCookie: false
  }
}

const join = () => {
  loginRef.value?.validate(async (valid, fields) => {
    if (valid) {
      let params = {
        name: loginForm.value.name,
        email: loginForm.value.email,
        phone: loginForm.value.phone,
        tenantId: tenantInfo.value.tenantId
      }
      loading.value = true
      submitTenant(params)
        .then((res) => {
          ElMessage.success('成功加入当前租户')
          loading.value = false
          localStorage.removeItem('route_tab')
          router.push('/')
        })
        .catch((err) => {
          loading.value = false
        })
    } else {
      loading.value = false
    }
  })
}

// 邀请加入的租户信息
const getTenantInfoMeg = (code) => {
  inviteLinkInfo(code).then((res) => {
    tenantInfo.value = res.data
  })
}
onMounted(async () => {
  // let params = router.currentRoute.value.query
  // if (params.source) {
  //     const [err, res] = await to(userStore.loginSSO(params))
  //     if (res) {
  //         localStorage.removeItem('route_tab')
  //         const redirectUrl = '/'
  //         await router.push(redirectUrl)
  //     }
  // }
});

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
  }
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
.login-header {
  height: 64px;
  padding: 0 12px 0 22px; 
  background: #fff;

  span {
    margin-left: 20px;
    color: #6b05a8;
    font-size: 24px;
  }
}
.form-wrap {
  flex: 1;
  overflow: auto;

  .poster {
    margin-right: 40px;
  }
}


.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 488px;
  height: 630px; 
  box-shadow: 0px 4px 20px 0px rgba(78, 29, 108, 0.12);
  .form-box {
    padding: 20px 56px 75px 56px
  }
  .title {
    margin: 0px auto 30px auto;
    color: #333333;
    font-size: 28px;
    font-weight: 700;
  }
  .form-title {
    padding: 24px 0 20px;
    font-size: 28px;
    line-height: 36px;
    font-weight: bold;
  }
  .form-text {
    font-size: 28px;
    font-weight: bold;
  }
  .form-tabs {
    margin: 66px 0 48px;
  }
  .login-btn-box {
    margin-top: 75px;
  }
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
</style>
