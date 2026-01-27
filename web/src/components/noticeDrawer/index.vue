<template>
  <el-drawer v-model="drawer"  title=""  close-on-click-modal :with-header="false" size="400px"  @close="closeNotice">
    <div style="display: flex; flex-direction: column; height: 100%;">
      <div style="position: relative;">
        <el-tabs v-model="activeName" @tab-change="handleTabChange">
          <el-tab-pane label="" name="user">
            <template #label>
              <el-badge :value="userNoticeCount" :hidden="userNoticeCount === 0" class="badge-item" :max="99">
                <span style="font-weight: 600;">用户</span>
              </el-badge>
            </template>
          </el-tab-pane>
          <el-tab-pane label="" name="system">
            <template #label>
              <!-- <el-badge :value="systemNoticeCount" :hidden="systemNoticeCount === 0" class="badge-item" :max="99"> -->
                <span style="font-weight: 600;">系统</span>
              <!-- </el-badge> -->
            </template>
          </el-tab-pane>
        </el-tabs>
        <el-button size="small" style="position: absolute; right: 10px; top: 8px;" @click="clearAll">清除未读</el-button>
      </div>
      <!-- 用户通知列表 -->
      <div v-if="activeName === 'user'" ref="userScrollContainer" style="flex: 1; padding: 0 20; overflow-y: auto;" class="custom-scrollbar-container" @scroll="handleUserScroll">
        <div v-for="item in userMessageList" class="message-item" :key="item.id || item.title" @click="handleItemClick(item)">
          <div>
            <el-badge is-dot :hidden="item.readFlag">
              <img src="@/assets/images/user_img.png" style="height: 32px; width: 32px; border-radius: 50%; object-fit: cover;" alt="">
            </el-badge>
          </div>
          <div style="flex: 1; overflow: hidden;">
            <div style="font-size: 14px;">
              <div style="line-height: 22px;">{{ item.creatorName || item.title }}</div>
              <div class="time-text" style="color: #999999; line-height: 22px;">
                <span style="flex: 1;" class="text-ellipsis">{{ getTitle(item) }}</span>
                <span>{{ formatTime(item.createdAt || item.createTime) }}</span>
              </div>
              <div style="line-height: 22px;">
               {{ item.content }}
              </div>
            </div>
          </div>
          <div v-if="item.targetCover || item.cover">
            <img :src="item.targetCover || item.cover" style="height: 42px; width: 42px; border-radius: 4px; object-fit: cover;" alt="">
          </div>
        </div>
        <div v-if="userLoading" style="text-align: center; color: #999; padding: 10px 0;">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span style="margin-left: 8px;">加载中...</span>
        </div>
        <p v-if="userNoMore" style="text-align: center; color: #999; padding: 10px 0;">没有更多消息了</p>
      </div>
      
      <!-- 系统通知列表 -->
      <div v-if="activeName === 'system'" ref="systemScrollContainer" style="flex: 1; padding: 0 20; overflow-y: auto;" class="custom-scrollbar-container system-notice-container" @scroll="handleSystemScroll">
        <div v-for="item in systemMessageList" class="system-message-item" :key="item.id || item.title" @click="handleItemClick(item)">
          <div class="system-notice-icon">
            <el-icon style="font-size: 24px; color: white;"><Setting /></el-icon>
          </div>
          <div style="flex: 1; overflow: hidden;">
            <div style="font-size: 14px; display: flex; justify-content: space-between; align-items: center;">
              <span style="line-height: 22px; font-weight: 600;">{{ item.title }}</span>
              <span style="color: #999999;">{{ formatTime(item.createdAt) }}</span>
            </div>
            <div style="line-height: 22px; color: #999999;">
               {{ item.content }}
            </div>
          </div>
        </div>
        <div v-if="systemLoading" style="text-align: center; color: #999; padding: 10px 0;">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span style="margin-left: 8px;">加载中...</span>
        </div>
        <p v-if="systemNoMore" style="text-align: center; color: #999; padding: 10px 0;">没有更多消息了</p>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { getNoticeList, clearUnreadNotice, markRead, userReceive, messageRead } from '@/api/system/notice';
import moment from 'moment';
import { useRouter } from 'vue-router';
import { Loading, Bell } from '@element-plus/icons-vue';
const { proxy } = getCurrentInstance();
const router = useRouter();
const props = defineProps({
  notice: {
    type: Boolean,
    default: false
  },
  noticeTo: {
    type: Object,
    default: () => ({})
  },
  userId: {
    type: Number,
    default: 0
  }
});
const emit = defineEmits(['closeNotice', 'updateNoticeTo']);
const drawer = ref(false);
const activeName = ref('user');

// 用户通知相关状态
const userLoading = ref(false);
const userNoMore = ref(false);
const userTotal = ref(0);
const userMessageList = ref([]);
const userScrollContainer = ref(null);
const userQueryParams = reactive({
  pageNum: 1,
  pageSize: 12
});

// 系统通知相关状态
const systemLoading = ref(false);
const systemNoMore = ref(false);
const systemTotal = ref(0);
const systemMessageList = ref([]);
const systemScrollContainer = ref(null);
const systemQueryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  type: 2
});

// 用户通知未读数量
const userNoticeCount = computed(() => {
  return (props.noticeTo.commentCount || 0) + (props.noticeTo.favoriteCount || 0) + (props.noticeTo.todo || 0);
});

// 系统通知未读数量
const systemNoticeCount = ref(3);

// 切换tab
const handleTabChange = (tab) => {
  if (tab === 'user') {
    // 用户通知：包括评论、收藏、待办
    userQueryParams.pageNum = 1;
    userMessageList.value = [];
    userNoMore.value = false;
    getUserNoticeList();
  } else if (tab === 'system') {
    // 系统通知
    systemQueryParams.pageNum = 1;
    systemMessageList.value = [];
    systemNoMore.value = false;
    getSystemNoticeList();
  }
};

// 获取用户通知列表（包括评论、收藏、待办）
const getUserNoticeList = async () => {
  if (userLoading.value) return;
  userLoading.value = true;
  try {
    const res = await getNoticeList(userQueryParams);
    console.log('用户通知res', res);
    if (userQueryParams.pageNum === 1) {
      // 第一页，直接替换
      userMessageList.value = res.data || [];
    } else {
      // 后续页，追加数据
      userMessageList.value = [...userMessageList.value, ...(res.data || [])];
    }
    userTotal.value = res.total || 0;
    
    // 判断是否还有更多数据
    if (userMessageList.value.length >= userTotal.value || (res.data && res.data.length < userQueryParams.pageSize)) {
      userNoMore.value = true;
    } else {
      userNoMore.value = false;
    }
  } catch (error) {
    console.error('获取用户通知列表失败:', error);
  } finally {
    userLoading.value = false;
  }
};

// 获取系统通知列表
const getSystemNoticeList = async () => {
  if (systemLoading.value) return;
  systemLoading.value = true;
  
  try {
    const res = await userReceive(systemQueryParams);
    console.log('系统通知res', res);
    const notices = res.data
    if (systemQueryParams.pageNum === 1) {
      // 第一页，直接替换
      systemMessageList.value = notices;
    } else {
      // 后续页，追加数据
      systemMessageList.value = [...systemMessageList.value, ...notices];
    }
    
    systemTotal.value = res.total || notices.length;
    
    // 判断是否还有更多数据
    if (systemMessageList.value.length >= systemTotal.value || (notices && notices.length < systemQueryParams.pageSize)) {
      systemNoMore.value = true;
    } else {
      systemNoMore.value = false;
    }
    
    // 更新未读数量
    const unreadCount = systemMessageList.value.filter(item => !item.readFlag).length;
    systemNoticeCount.value = unreadCount;
    
  } catch (error) {
    console.error('获取系统通知列表失败:', error);
  } finally {
    systemLoading.value = false;
  }
};

const getTitle = (item) => {
  // 系统通知
  if (activeName.value === 'system' || item.type === 'system') {
    return item.title || '系统通知';
  }
  
  // 用户通知
  if (item.behavior === 'COMMENT') {
    return `评论了${item.targetName}`;
  }
  if (item.behavior === 'FAVORITE') {
    if (item.jumpType === 'assistant') {
      return `收藏了知识专家【${item.targetName}】`;
    } else {
      return `收藏了文件【${item.targetName}】`;
    }
  }
  if (item.behavior === 'TODO' || item.permissionTypeText) {
    return `申请了${item.targetName || item.permissionTypeText}`;
  }
  
  return item.title || '';
}

const handleItemClick = async (item) => {
  // 系统通知
  if (activeName.value === 'system' || item.type === 'system') {
    // 如果未读，标记为已读
    if (!item.readFlag && item.id) {
      try {
        await markRead(item.id);
        item.readFlag = true;
        systemNoticeCount.value = Math.max(0, systemNoticeCount.value - 1);
      } catch (error) {
        console.error('标记系统通知已读失败:', error);
      }
    }
    // 系统通知暂不跳转，只显示详情
    // 可以根据实际需求添加跳转逻辑
    return;
  }
  
  // 用户通知
  if (!item.id) return;
  
  // 标记为已读
  await markRead(item.id);
  
  // 根据不同类型跳转
  if (item.behavior === 'COMMENT') {
    closeNotice();
    router.push({
      path: '/space/retrieve/detail',
      query: {
        id: item.targetId,
        fromComment: 'true'
      }
    });
  } else if (item.behavior === 'FAVORITE') {
    closeNotice();
    if (item.jumpType === 'assistant') {
      router.push({
        path: '/space/specialist/detail',
        query: {
          uuid: item.targetId
        }
      });
    } else {
      router.push({
        path: '/space/retrieve/detail',
        query: {
          id: item.targetId
        }
      });
    }
  } else if (item.behavior === 'TODO' || item.permissionTypeText) {
    // 待办通知，待开发具体跳转逻辑
    ElMessage.info('待办详情查看功能待开发');
  }
}
// 清空所有未读
const clearAll = () => {
  if (activeName.value === 'system') {
    // 系统通知的清除
    ElMessageBox.confirm('确定清除所有未读消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 标记所有系统通知为已读
      systemMessageList.value.forEach(item => {
        item.readFlag = true;
      });
      systemNoticeCount.value = 0;
      ElMessage.success('清除成功');
    });
  } else {
    // 用户通知的清除
    ElMessageBox.confirm('确定清除所有未读消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      clearUnreadNotice().then(res => {
        ElMessage.success('清除成功');
        getUserNoticeList();
        emit('updateNoticeTo');
      });
    });
  }
}

// 根据用户ID生成固定颜色
const generateAvatarColor = (userId) => {
    // 颜色组映射规则：1,5 -> 组0；2,4 -> 组1；3,6 -> 组2；0,7 -> 组3；8,9 -> 组4
    const colorGroups = {
        0: '#409EFF',  // 蓝色 - 对应ID末尾 1,5
        1: '#67C23A',  // 绿色 - 对应ID末尾 2,4
        2: '#E6A23C',  // 橙色 - 对应ID末尾 3,6
        3: '#F56C6C',  // 红色 - 对应ID末尾 0,7
        4: '#9C27B0'   // 紫色 - 对应ID末尾 8,9
    }
    
    // 将userId转换为字符串并获取最后一位
    const userIdStr = String(userId || '0')
    const lastDigit = parseInt(userIdStr[userIdStr.length - 1]) || 0
    
    // 根据最后一位数字确定颜色组
    let colorGroup
    if (lastDigit === 1 || lastDigit === 5) {
        colorGroup = 0
    } else if (lastDigit === 2 || lastDigit === 4) {
        colorGroup = 1
    } else if (lastDigit === 3 || lastDigit === 6) {
        colorGroup = 2
    } else if (lastDigit === 0 || lastDigit === 7) {
        colorGroup = 3
    } else { // 8, 9
        colorGroup = 4
    }
    
    return colorGroups[colorGroup]
}

// 生成头像文字
const generateAvatarText = (username) => {
    return username.substring(0, 2).toUpperCase()
}


// 处理用户通知滚动事件
const handleUserScroll = (e) => {
  const target = e.target;
  const scrollTop = target.scrollTop;
  const scrollHeight = target.scrollHeight;
  const clientHeight = target.clientHeight;
  
  // 当滚动到距离底部50px时触发加载
  if (scrollHeight - scrollTop - clientHeight < 50) {
    loadMoreUser();
  }
};

// 处理系统通知滚动事件
const handleSystemScroll = (e) => {
  const target = e.target;
  const scrollTop = target.scrollTop;
  const scrollHeight = target.scrollHeight;
  const clientHeight = target.clientHeight;
  
  // 当滚动到距离底部50px时触发加载
  if (scrollHeight - scrollTop - clientHeight < 50) {
    loadMoreSystem();
  }
};

// 加载更多用户通知
const loadMoreUser = () => {
  if (userNoMore.value || userLoading.value) return;
  userQueryParams.pageNum += 1;
  getUserNoticeList();
};

// 加载更多系统通知
const loadMoreSystem = () => {
  if (systemNoMore.value || systemLoading.value) return;
  systemQueryParams.pageNum += 1;
  getSystemNoticeList();
};

const closeNotice = () => {
  drawer.value = false;
  emit('closeNotice');
};

// 格式化时间
const formatTime = (time) => {
  const today = moment().startOf('day');
  const createdDate = moment(time);
  
  if (createdDate.isSame(today, 'day')) {
    // 如果是当天，显示时:分
    return createdDate.format('HH:mm');
  } else {
    // 不是当天，显示月/日
    return createdDate.format('MM-DD');
  }
};

watch(
  () => props.notice,
  async (newValue) => {
    drawer.value = newValue;
    if (newValue) {
      activeName.value = 'user'; // 默认显示用户通知
      userMessageList.value = [];
      userQueryParams.pageNum = 1;
      userNoMore.value = false;
      getUserNoticeList();
    }
  },
  { immediate: true }
);
</script>


<style scoped lang="scss">
// :deep(.el-tabs__nav) {
//   height: 50px;
// }
:deep(.el-tabs__nav-scroll) {   
  margin-left: 30px;
}
:deep(.el-tabs__header) {
  margin: 0;
}
.message-item {
  border-bottom: 1px solid #EFEFEF;
  padding: 20px 10px 10px 20px;
  display: flex;
  gap: 12px;
}
.message-item:hover {
  background: #FBF8FC;
  cursor: pointer;
}

.avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: inline-block;
  line-height: 32px;
  text-align: center;
  color: #fff;
}
.time-text {
  display: flex;
  gap: 5px;
  overflow: hidden;
}
.badge-item {
  :deep(.el-badge__content){
    font-size: 10px !important;
    padding: 4px;
    height: 14px;
  }
}

// 系统通知样式
.system-notice-container {
  .system-message-item {
    border-bottom: 1px solid #EFEFEF;
    padding: 20px 10px 10px 20px;
    display: flex;
    gap: 12px;
    align-items: flex-start;
    cursor: pointer;
    
    &:hover {
      background: #FBF8FC;
    }
    
    .system-notice-icon {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      background: #538FFF;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }
  }
}

</style>
