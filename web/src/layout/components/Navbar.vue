<template>
  <div class="headerbar">
    <div class="dp-c" style="z-index: 10">
      <div class="headerbar_button_icon element dp-c" @click="checkMeun">
        <svg-icon v-if="!drawer" icon-class="menu_on" />
        <svg-icon v-else icon-class="menu_off" />
      </div>
      <svg-icon icon-class="logo" style="font-size: 30px; margin-left: 20px" @click="router.push('/home/index')" />
      <div style="font-size: 16px; color: #6b05a8; cursor: pointer; font-weight: 700;"  @click="router.push('/home/index') ">-知识库</div>
      <SidebarTab :header-tab-menu="headerTabMenu" @change-tab="onChangeTab" />
    </div>
    <div class="dp-c">
      <div style="padding: 0 20px;">
        <el-tooltip content="github" placement="bottom">
          <svg-icon icon-class="github" class="element ml20 fs20" style="font-size: 16px;" @click="openGithub"></svg-icon>
        </el-tooltip>
        <el-tooltip content="gitee" placement="bottom">
          <svg-icon icon-class="gitee" class="element ml20 fs20" style="font-size: 16px" @click="openGitee"></svg-icon>
        </el-tooltip>
      </div>
      <div style="display: flex; height: 100%; align-items: center; padding: 0 20px; border-left: 1px solid #f5f5f5; border-right: 1px solid #f5f5f5">
        <el-tooltip content="帮助文档" placement="bottom">
          <svg-icon icon-class="document" class="element ml20 fs20" style="font-size: 16px" @click="openHelp"></svg-icon>
        </el-tooltip>
        <el-tooltip content="通知" placement="bottom">
          <el-badge class="item"  is-dot :hidden="noticeTo.unreadCount === 0 &&  noticeTo.todo === 0">
            <svg-icon icon-class="notice" class="element ml20 fs20" style="font-size: 16px" @click="openNotice"></svg-icon>
          </el-badge>
        </el-tooltip>
        <el-tooltip content="语言" placement="right">
          <lang-select id="lang-select" />
        </el-tooltip>
      </div>
      <div style="display: flex; align-items: center;">
        <el-dropdown ref="drop" trigger="click" :hide-on-click="false">
          <img class="element" style="width: 38px; height: 38px; margin: 0 16px 0 20px; border-radius: 50%" :src="userStore.avatar" alt="" />
          <template #dropdown>
            <div style="width: 408px; font-size: 16px">
              <div class="drop_header dp-c">
                <!-- <svg-icon icon-class="platform_icon" style="font-size: 50px" /> -->
                <svg-icon icon-class="logo" style="font-size: 50px" />
                <span style="font-weight: bold; margin-top: 10px">物道智云</span>
              </div>
              <div style="padding: 0 26px">
                <div style="color: #999999; margin-top: 13px">个人</div>
                <div class="drop_user">
                  <img style="width: 42px; height: 42px; border-radius: 50%" :src="userStore.avatar" alt="" />
                  <div style="margin-left: 15px">{{ userInfo.username }}</div>
                  <!-- <div style="color: #999999">我自己</div> -->
                  <img
                    v-if="userInfo.id === 1"
                    style="margin-left: 15px; width: 72px; height: 20px"
                    src="../../assets/images/admin_icon.png"
                    alt=""
                  />
                </div>
              </div>
              <div class="drop_footer dp-c">
                <el-button link style="width: 50%" @click="handleSetting">个人设置</el-button>
                <div style="width: 1px; height: 22px; background: #c7c7c7"></div>
                <el-button link style="width: 50%" @click="logout">退出</el-button>
              </div>
            </div>
          </template>
        </el-dropdown>
        <!-- <svg-icon icon-class="user_icon" style="font-size: 38px; margin: 0 16px 0 20px" /> -->
      </div>
    </div>
    <el-dialog v-model="dialogVisible" width="760" class="set-dialog"  @close="cancel(infoFormRef)">
      <div style="display: flex; color: #000">
        <div style="padding: 22px 38px; background: #f5f5f5; width: 320px">
          <div>
            <span style="font-size: 18px">个人信息</span>
            <div class="dp-c" style="padding: 30px 0; flex-direction: column; border-bottom: 1px solid #d9d9d9">
              <img style="width: 84px" :src="userStore.avatar" alt="" />
              <!-- <userAvatar @updateAvatar="updateAvatar"  /> -->
              <span style="font-size: 18px; margin-top: 10px">{{userInfo.username || '-'}}</span>
            </div>
            <div style="margin-top: 30px">
              <div style="margin-left: 10px; display: flex; align-items: center; margin-bottom: 15px">
                <svg-icon icon-class="setId" style="font-size: 24px; margin-right: 16px"></svg-icon>
                <el-divider direction="vertical" />
                <span style="margin-left: 40px">{{ userInfo.id }}</span>
              </div>
              <div style="margin-left: 10px; display: flex; align-items: center; margin-bottom: 15px">
                <svg-icon icon-class="setphone" style="font-size: 22px; margin-right: 16px"></svg-icon>
                <el-divider direction="vertical" />
                <span style="margin-left: 40px">{{userInfo.phoneNumber || '-'}}</span>
              </div>
              <div style="margin-left: 10px; display: flex; align-items: center; margin-bottom: 15px">
                <svg-icon icon-class="setemail" style="font-size: 24px; margin-right: 16px"></svg-icon>
                <el-divider direction="vertical" />
                <span style="margin-left: 40px">{{ userInfo.email || '-'}}</span>
              </div>
              <div style="margin-left: 10px; display: flex; align-items: center; margin-bottom: 15px">
                <svg-icon icon-class="setdept" style="font-size: 24px; margin-right: 16px"></svg-icon>
                <el-divider direction="vertical" />
                <span style="margin-left: 40px">{{userInfo.roleGroup}}</span>
              </div>
              <!-- <div style="margin-left: 10px; display: flex; align-items: center">
                <svg-icon icon-class="setpost" style="font-size: 22px; margin-right: 16px"></svg-icon>
                <el-divider direction="vertical" />
                <span style="margin-left: 40px">{{userInfo.postGroup}}</span>
              </div> -->
            </div>
          </div>
        </div>
        <div style="padding: 0 36px; flex: 1;">
          <el-form :model="infoForm" label-width="80" :rules="rules" ref="infoFormRef" label-position="left">
            <el-tabs v-model="activeName">
              <el-tab-pane label="基本信息" name="first">
                <el-form-item label="用户昵称" style="margin-top: 20px" prop="nickname">
                  <el-input v-model="infoForm.nickname" style="width: 300px" placeholder="请输入用户昵称" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="infoForm.gender">
                    <el-radio :value="0">男</el-radio>
                    <el-radio :value="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-tab-pane>
              <!-- <el-tab-pane label="修改密码" name="second">
                <el-form-item label="旧密码" style="margin-top: 20px" prop="oldPassword">
                  <el-input v-model="infoForm.oldPassword" style="width: 300px" type="password" placeholder="请输入旧密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="infoForm.newPassword" style="width: 300px" type="password" placeholder="请输入新密码" />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="infoForm.confirmPassword" style="width: 300px" type="password" placeholder="请确认密码" />
                </el-form-item>
              </el-tab-pane> -->
            </el-tabs>
          </el-form>
        </div>
      </div>
      <template #footer>
        <el-button @click="cancel(infoFormRef)">关闭</el-button>
        <!-- <el-button type="primary" @click="submitPassword(infoFormRef)"> 保存 </el-button> -->
      </template>
    </el-dialog>
    <!-- meun弹出层 -->
    <el-drawer v-model="drawer" direction="ltr" :with-header="false" close-on-click-modal size="760px">
      <MenuAll @close_menu="closeMenu" @menu_fn="changeMenu" />
    </el-drawer>
    <Organization v-if="organ" :organ="organ" @close-organ="clos" />
    <NoticeDrawer 
      v-if="notice" 
      :notice="notice" 
      :noticeTo="noticeTo" 
      :userId="userStore.userId"
      @closeNotice="closeNotice" 
      @updateNoticeTo="updateNoticeTo" 
    />
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue';
import UserAvatar from '@/views/system/user/profile/userAvatar.vue';
import Organization from '@/components/Organization/index.vue';
import MenuAll from './menu.vue';
import SidebarTab from './sidebarTab.vue';
import useUserStore from '@/store/modules/user';
import { getTenantList } from '@/api/login';
import user, {  updateUserPwd, updateUserProfile } from '@/api/system/user';
import { dynamicTenant, userListTenant, userDynamicTenant } from '@/api/system/tenant';
import NoticeDrawer from '@/components/noticeDrawer/index.vue';
import { update } from 'lodash';
import { getNoticeTotal } from '@/api/system/notice';
import { getTodoList } from '@/api/base';

const { proxy } = getCurrentInstance();
const router = useRouter()

const dialogVisible = ref(false);
const drawer = ref(false);
const notice = ref(false);
const userStore = useUserStore();
// 顶部显示的菜单
const headerTabMenu = ref({});
const userInfo = ref({});
const activeName = ref('first');
const infoForm = ref({
  nickname: '',
  gender: 0,
  avatar: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 通知
const noticeTo = ref({
  unreadCount: 0, // 未读
  commentCount: 0,  // 评论
  favoriteCount: 2, // 收藏
  todo: 0 // 待办
});

const infoFormRef = ref(null)
const rules = computed(() => {
  if (activeName.value === 'first') {
    // 基本信息标签页，只验证昵称
    return {
      nickname: [{ required: true, trigger: 'blur', message: '请输入您的昵称' }]
    };
  } else {
    // 修改密码标签页，验证所有密码字段
    return {
      oldPassword: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
      newPassword: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
      confirmPassword: [{ 
        required: true, 
        trigger: 'blur', 
        validator: (rule, value, callback) => {
          if (value !== infoForm.value.newPassword) {
            callback(new Error('两次密码不一致'))
          } else {
            callback()
          }
        } 
      }]
    };
  }
});

// 切换目录或路由
const curTab = ref({});
const tenantId = ref(null);
const emit = defineEmits(['CHANGE-ALL-MENU', 'change-tab']);
// 切换菜单
const changeMenu = (path) => {
  console.log('Changing menu to path:', path);
  
  closeMenu();
  
  router.push(path).catch((error) => {
    console.error('Navigation failed:', error);
  });
};

const cancel = async (formEl) => {
  if (!formEl) return
  await formEl.resetFields();
  dialogVisible.value = false;
  infoForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const submitPassword = async (formEl) => {
  if (!formEl) return
  
  try {
    const valid = await formEl.validate();
    if (valid) {
      if (activeName.value === 'first') {
        // 修改基本信息
        let params = {
          ...userInfo.value,
          avatar: infoForm.value.avatar,
          nickname: infoForm.value.nickname,
          gender: infoForm.value.gender,
        };
        console.log('修改基本信息参数:', params);
        
        const res = await updateUserProfile(params);
        console.log('修改基本信息结果:', res);
        ElMessage.success('基本信息修改成功');
        cancel(formEl);
        
        // 更新本地存储的用户信息
        userInfo.value = { ...userInfo.value, ...params };
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value));
        
      } else {
        // 修改密码
        let params = {
          oldPassword: infoForm.value.oldPassword,
          newPassword: infoForm.value.newPassword
        };
        console.log('修改密码参数:', params);
        
        const res = await updateUserPwd(params);
        console.log('修改密码结果:', res);
        ElMessage.success('密码修改成功');
        cancel(formEl);
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error);
    ElMessage.error('请检查输入信息');
  }
}

watch(drawer, (newValue) => {
  emit('CHANGE-ALL-MENU', !newValue);
});
const organ = ref(false);
const createOrgan = () => {
  organ.value = true;
};
const clos = () => {
  organ.value = false;
};

const updateAvatar = (img) => {
  console.log('img', img)
  infoForm.value.avatar = img
}

const logout = async () => {
  await ElMessageBox.confirm('确定退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });
  await userStore.logout();
   // 设置tenantId cookie为0，domain为.wudao-tech.com
  document.cookie = 'tenantId=0; path=/; domain=.wudao-tech.com';
  location.href = import.meta.env.VITE_REDIRECT_URL + 'login?redirect_uri=' + import.meta.env.VITE_PLATFORM_URL;
};

const openGithub = () => {
  window.open('https://github.com/wudao-tech/wudao-kms');
}

const openGitee = () => {
  window.open('https://gitee.com/wudao-tech/wudao-kms');
}

const openHelp = () => {
  window.open('https://wudaotech.feishu.cn/wiki/VpBQwzzQYiepO3k7hmEcdlH0n5g');
}

const openNotice = () => {
  notice.value = true;
};

const closeNotice = () => {
  notice.value = false;
  updateNoticeTo()
};

const handleSetting = () => {
  dialogVisible.value = true;
}

/** 租户列表 */
// const initTenantList = async () => {
//   if (userInfo.value.user.tenantId === '000000') {
//     const { data } = await getTenantList();
//     organizationList.value = data.voList;
//   } else {
//     const { data } = await userListTenant();
//     organizationList.value = data;
//   }
// };

const organizationList = ref([]); // 组织

const checkMeun = () => {
  drawer.value = !drawer.value;
};
const onChangeTab = (val) => {
  emit('change-tab', val);
};

const changeCompany = async (id) => {
  await userDynamicTenant(id);
  // localStorage.removeItem('route_tab');
  proxy?.$router.push('/');
  setTimeout(() => {
    window.location.reload();
  }, 500);
  // proxy?.$tab.refreshPage();
};
const getTodoListFn = async () => {
  const res = await getTodoList(userStore.userId);
  noticeTo.value.todo = res.data.length;
}

const updateNoticeTo = async () => {
  await getNoticeTotal().then(res => {
    noticeTo.value.unreadCount = res.data.unreadCount;
    noticeTo.value.commentCount = res.data.commentCount;
    noticeTo.value.favoriteCount = res.data.favoriteCount;
  });
  await getTodoListFn()
}

const drop = ref(null);
onMounted(() => {
  // getUserInfo();
  const storedUserInfo = localStorage.getItem('userInfo');
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo);
    // 初始化表单数据
    infoForm.value = {
      nickname: userInfo.value.nickname || '',
      gender: userInfo.value.gender || 0,
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  }
  updateNoticeTo()
});



const closeMenu = () => {
  drawer.value = false;
};
</script>
<style scoped lang="scss">
.headerbar {
  font-size: 14px;
  height: 56px;
  width: 100%;
  box-shadow: 0px 1px 2px 0px rgba(0, 0, 0, 0.12);
  display: flex;
  background-color: #fff;
  justify-content: space-between;
  .headerbar_button_icon {
    width: 56px;
    height: 56px;
    background-color: #6b05a8;
    justify-content: center;
    font-size: 26px;
  }
}
:deep(.el-overlay) {
  margin-top: 57px;
  height: calc(100% - 57px);
  .el-drawer__body {
    padding: 0;
  }
}
.drop_header {
  width: 100%;
  height: 130px;
  background-image: url('../../assets/images/drop_back.png');
  flex-direction: column;
  justify-content: center;
}
.drop_user {
  margin-top: 27px;
  padding-bottom: 25px;
  border-bottom: 1px solid #33333333;
  display: flex;
  align-items: center;
}
.drop_footer {
  width: 100%;
  display: flex;
  height: 60px;
  font-size: 16px;
  align-items: center;
  background: #f9f9f9;
}
.hoveit {
  padding: 10px 26px;
}
.hoveit:hover {
  background-color: #f8f8fa;
}
.wai-select {
  :deep(.el-select__wrapper) {
    box-shadow: none;
  }
  :deep(.el-select__wrapper.is-hovering:not(.is-focused)) {
    box-shadow: none !important;
  }
}

// 修复图标点击时的黑框问题
:deep(.svg-icon) {
  outline: none;
  
  &:focus {
    outline: none;
    border: none;
    box-shadow: none;
  }
  
  &:focus-visible {
    outline: none;
    border: none;
    box-shadow: none;
  }
}

:deep(.element) {
  outline: none;
  
  &:focus {
    outline: none;
    border: none;
    box-shadow: none;
  }
  
  &:focus-visible {
    outline: none;
    border: none;
    box-shadow: none;
  }
}
:deep(.el-drawer) {
  width: 760px;
}
</style>
<style lang="scss">

</style>
