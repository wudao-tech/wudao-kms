<template>
  <el-drawer v-model="drawer"  title=""  close-on-click-modal :with-header="false" size="400px"  @close="closeNotice">
    <div style="display: flex; flex-direction: column; height: 100%;">
      <div style="position: relative;">
        <el-tabs v-model="activeName" @tab-change="handleTabChange">
          <el-tab-pane label="" name="comment">
            <template #label>
              <el-badge :value="noticeTo.commentCount" :hidden="noticeTo.commentCount === 0" class="badge-item" :max="99">
                <span style="font-weight: 600;">评论</span>
              </el-badge>
            </template>
          </el-tab-pane>
          <el-tab-pane label="收藏" name="collect">
            <template #label>
              <el-badge :value="noticeTo.favoriteCount" :hidden="noticeTo.favoriteCount === 0" class="badge-item" :max="99">
                <span style="font-weight: 600;">收藏</span>
              </el-badge>
            </template>
          </el-tab-pane>
          <el-tab-pane label="待办" name="todo">
            <template #label>
              <el-badge :value="noticeTo.todo" class="badge-item" :hidden="noticeTo.todo === 0" :max="99">
                <span style="font-weight: 600;">待办</span>
              </el-badge>
            </template>
          </el-tab-pane>
        </el-tabs>
        <el-button size="small" style="position: absolute; right: 10px; top: 8px;" @click="clearAll">清除未读</el-button>
      </div>
      <div ref="scrollContainer" style="flex: 1; padding: 0 20; overflow-y: auto;" class="custom-scrollbar-container" @scroll="handleScroll">
        <div v-for="item in messageList" class="message-item" :key="item.id || item.title" @click="handleItemClick(item)">
          <div>
            <el-badge is-dot :hidden="item.readFlag">
              <img src="@/assets/images/user_img.png" style="height: 32px; width: 32px; border-radius: 50%; object-fit: cover;" alt="">
            </el-badge>
          </div>
          <div style="flex: 1; overflow: hidden;">
            <div style="font-size: 14px;">
              <div style="line-height: 22px;">{{ item.creatorName }}</div>
              <div class="time-text" style="color: #999999; line-height: 22px;">
                <span style="flex: 1;" class="text-ellipsis">{{ getTitle(item) }}</span>
                <span>{{ formatTime(item.createdAt) }}</span>
              </div>
              <div v-if="activeName !== 'todo'" style="line-height: 22px;">
               {{ item.content }}
              </div>
              <div v-else style="text-align: right;">
                <el-button  style="color: #333; font-weight: 400;background: #F5F5F5;border: none;" size="small" @click="approveFn(item.id, 1)">通过</el-button>
                <el-button  style="color: #333; font-weight: 400;background: #F5F5F5;border: none;" size="small" @click="approveFn(item.id, 2)">拒绝</el-button>
              </div>
            </div>
            
          </div>
          <div v-if="activeName !== 'todo'">
            <img :src="item.targetCover" style="height: 42px; width: 42px; border-radius: 4px; object-fit: cover;" alt="">
          </div>
        </div>
        <div v-if="loading" style="text-align: center; color: #999; padding: 10px 0;">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span style="margin-left: 8px;">加载中...</span>
        </div>
        <p v-if="noMore" style="text-align: center; color: #999; padding: 10px 0;">没有更多消息了</p>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { getNoticeList, clearUnreadNotice, markRead, getNoticeTotal } from '@/api/system/notice';
import { getTodoList, passApplication, rejectApplication } from '@/api/base';
import moment from 'moment';
import { useRouter } from 'vue-router';
import { Loading } from '@element-plus/icons-vue';
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
const activeName = ref('comment');
const loading = ref(false);
const noMore = ref(false);
const total = ref(0);
const messageList = ref([]);
const scrollContainer = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 12,
  behavior: 'COMMENT'
});

// 切换tab
const handleTabChange = (tab) => {
  queryParams.pageNum = 1;
  messageList.value = [];
  noMore.value = false;
  if (tab === 'todo') {
    getTodoListFn()
  } else if (tab === 'collect') {
    queryParams.behavior = 'FAVORITE';
    getMessageList();
  } else {
    queryParams.behavior = 'COMMENT';
    getMessageList();
  }
  
  
};

const getTodoListFn = () => {
  loading.value = true;
  getTodoList(props.userId).then(res => {
    console.log('res', res);
    messageList.value = res.data.map(item => {
      return {
        ...item,
        readFlag: false,
        creatorName: item.applicantUserName,
        targetName: item.permissionTypeText,
      }
    })
    // 待办列表不支持分页，加载完就显示没有更多
    noMore.value = true;
  }).catch(error => {
    console.error('获取待办列表失败:', error);
  }).finally(() => {
    loading.value = false;
  });
}

const approveFn = (id, status) => {
  ElMessageBox.confirm(`确定要${status === 1 ? '通过' : '拒绝'}申请吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    if (status === 1) {
      passApplication(id).then(res => {
        ElMessage.success('通过成功')
        getTodoListFn()
        emit('updateNoticeTo');
      })
    } else {
      rejectApplication(id).then(res => {
        ElMessage.success('拒绝成功')
        getTodoListFn()
        emit('updateNoticeTo');
      })
    }
  })
}

const getTitle = (item) => {
  if (activeName.value === 'comment') {
    return `评论了${item.targetName}`;
  }
  if (activeName.value === 'todo') {
    return `申请了${item.targetName}`;
  }
  if (item.jumpType === 'assistant') {
    return `收藏了知识专家【${item.targetName}】`;
  } else {
    return `收藏了文件【${item.targetName}】`;
  }
}

const handleItemClick = async (item) => {
  if (activeName.value === 'comment') {
    await markRead(item.id);
    closeNotice()
    router.push({
      path: '/space/retrieve/detail',
      query: {
        id: item.targetId,
        fromComment: 'true'
      }
    })
  } else if (activeName.value === 'collect') {
    await markRead(item.id);
    closeNotice()
    if (item.jumpType === 'assistant') {
      router.push({
        path: '/space/specialist/detail',
        query: {
          uuid: item.targetId
        }
      })
    } else {
      router.push({
        path: '/space/retrieve/detail',
        query: {
          id: item.targetId
        }
      })
    }
  } else if (activeName.value === 'todo') {
     return
  }
}
// 清空所有未读
const clearAll = () => {
  ElMessageBox.confirm('确定清除所有未读消息吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    clearUnreadNotice().then(res => {
      ElMessage.success('清除成功');
      getMessageList();
      emit('updateNoticeTo');
    });
  });
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

// 获取消息列表
const getMessageList = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    const res = await getNoticeList(queryParams);
    console.log('res', res);
    if (queryParams.pageNum === 1) {
      // 第一页，直接替换
      messageList.value = res.data || [];
    } else {
      // 后续页，追加数据
      messageList.value = [...messageList.value, ...(res.data || [])];
    }
    total.value = res.total || 0;
    
    // 判断是否还有更多数据
    if (messageList.value.length >= total.value || (res.data && res.data.length < queryParams.pageSize)) {
      noMore.value = true;
    } else {
      noMore.value = false;
    }
  } catch (error) {
    console.error('获取消息列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 处理滚动事件
const handleScroll = (e) => {
  const target = e.target;
  const scrollTop = target.scrollTop;
  const scrollHeight = target.scrollHeight;
  const clientHeight = target.clientHeight;
  
  // 当滚动到距离底部50px时触发加载
  if (scrollHeight - scrollTop - clientHeight < 50) {
    loadMore();
  }
};

// 加载更多
const loadMore = () => {
  if (noMore.value || loading.value) return;
  if (activeName.value === 'todo') {
    // 待办列表不支持分页加载
    return;
  }
  queryParams.pageNum += 1;
  getMessageList();
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
      messageList.value = [];
      queryParams.pageNum = 1;
      noMore.value = false;
      getMessageList();
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

</style>
