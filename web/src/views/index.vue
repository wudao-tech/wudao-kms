<template>
  <div class="workspace-page">
      <!-- 左侧内容 -->
      <div class="left-content"  :class="{ 'expanded': !assistantVisible }">
        <!-- 欢迎区域 -->
        <div class="welcome-section">
          <div>
            <div class="date-info">
            <span class="date">{{ d }}</span>
            <span class="day">{{ w }}</span>
            <span class="time">{{ h }}</span>
          </div>
          <div class="greeting">{{ user.username || '用户' }}，<span style="font-size: 14px;">欢迎回来</span></div>
          </div>
          <div style="font-size: 18px;color: #6C308F; font-weight: 600; font-style: italic;">物道AI知识库让工业会思考，会协作，会进化</div>
        </div>

        <!-- 快速开始 -->
        <div class="quick-start-section">
          <div v-if="!collapse" class="quick-cards">
            <div class="quick-card" @click="handleQuickCard('/home/specialist')">
               <img src="@/assets/images/index_svg_1.svg" style="margin-right: 10px; height: 28px;" alt="">
              <div>
                <div class="card-title">知识专家</div>
                <div class="card-desc text-ellipsis">专属知识问答助手，充分挖掘知识</div>
              </div>
            </div>
            <div class="quick-card" @click="handleQuickCard('/space/retrieve')">
              <img src="@/assets/images/index_svg_2.svg" style="margin-right: 10px; height: 28px;" alt="">
              <div>
                <div class="card-title">知识检索</div>
                <div class="card-desc text-ellipsis">智能检索，知识点不遗漏</div>
              </div>
            </div>
            <div class="quick-card" @click="handleQuickCard('/space/knowledge')">
              <img src="@/assets/images/index_svg_4.svg" style="margin-right: 10px; height: 28px; width: 28px;" alt="">
              <div>
                <div class="card-title">知识管理</div>
                <div class="card-desc text-ellipsis">知识准确提取，统一管理</div>
              </div>
            </div>
          </div>
        </div>
        
        <div style="display: flex; gap: 20px; margin-top: 20px; flex: 1; overflow: hidden;">
          <div style="width: 35%;background: white; display: flex; flex-direction: column;">
          <!-- 我的/公告 Tab模块 -->
            <div class="stats-section"> 
              <div class="stats-tabs">
                <div :class="{ 'tab-item': true, active: activeTab1 === 'visit' }"  @click="handleTab1('visit')">最近访问</div>
                <div  :class="{ 'tab-item': true, active: activeTab1 === 'collect' }" @click="handleTab1('collect')">收藏</div>
                <!-- <div  :class="{ 'tab-item': true, active: activeTab1 === 'notice' }" @click="handleTab1('notice')">待办({{ todoList.length }})</div> -->
              </div>
            </div>
            <div v-if="activityList.length > 0 && activeTab1 !== 'notice'" class="custom-scrollbar-container" style="flex: 1; padding: 0 20px;">
              <div class="activity-item" v-for="activity in activityList" :key="activity.id" @click="handleActivity(activity)"  style="gap: 0">
                  <!-- <img v-if="activeTab2 === 'collect' && index === 0" src="@/assets/images/hot_1.svg" style="width: 20px; margin-right: 10px;" alt="">
                  <img v-else-if="activeTab2 === 'collect' && index === 1" src="@/assets/images/hot_2.svg" style="width: 20px; margin-right: 10px;" alt="">
                  <img v-else-if="activeTab2 === 'collect' && index === 2" src="@/assets/images/hot_3.svg" style="width: 20px; margin-right: 10px;" alt=""> -->
                <img v-if="activity.targetType === 'assistant'" src="@/assets/images/knowlege_icon.svg" style="width: 20px; margin-right: 10px;" alt="">
                <img v-else :src="getFilePath(activity.targetName)" style="width: 20px; margin-right: 10px;" alt="">
                <div style="flex: 1; overflow: hidden;">
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                     <span style="flex: 1; margin-right: 10px; cursor: pointer;" class="text-ellipsis">  {{ activity.targetName }}</span>
                     <span>{{ activity.creatorName }}</span>           
                  </div>
                  <div style="display: flex; justify-content: space-between; color: #B4B4B4; align-items: center;">
                    <span v-if="activity.targetType === 'assistant'">知识专家</span>
                    <span v-else>知识文件</span>
                    <span>  {{  moment(activity.visitTime).format('MM-DD HH:mm') }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else-if="todoList.length > 0 && activeTab1 === 'notice'" class="custom-scrollbar-container" style="flex: 1; padding: 0 20px;">
              <div class="activity-item" style="gap: 0" v-for="activity in todoList" :key="activity.id">
                <img src="@/assets/images/role.svg" style="width: 20px; margin-right: 10px;" alt="">
                <div style="flex: 1; overflow: hidden;">
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                     <span style="flex: 1; margin-right: 10px; cursor: pointer;" class="text-ellipsis">{{ activity.knowledgeBaseName || '-' }}</span>
                     <span> {{ activity.applicantUserName }} </span>     
                  </div>
                  <div style="display: flex; justify-content: space-between; color: #B4B4B4; align-items: center;">
                    <span>{{ activity.permissionTypeText }} / {{ activity.applicationReason }}</span>
                    <span>
                       <el-button link style="color: #B4B4B4; font-weight: 400;" @click="approveFn(activity.id, 1)">通过</el-button>
                       <el-button link style="color: #B4B4B4; font-weight: 400;" @click="approveFn(activity.id, 2)">拒绝</el-button>
                     </span>  
                  </div>
                </div>
              </div>
            </div>
            <div v-else style="flex: 1;display: flex; justify-content: center; align-items: center;">
              <img :src="notData" style="width: 120px;" alt="">
            </div>
          </div>
          <div style="flex: 1; background: white; padding: 20px 20px 0 20px; display: flex; flex-direction: column; position: relative;">
            <!-- <div>我的知识</div>
            <div style="display: flex; gap: 20px; margin-top: 10px;">
              <div class="stats-card">
                <span>访问</span>
                <span style="margin-top: 5px; font-size: 18px; font-weight: 600;">{{ countAll.visitedByCount || 0 }}</span>
              </div>
              <div class="stats-card">
                <span>收藏</span>
                <span style="margin-top: 5px; font-size: 18px; font-weight: 600;">{{ countAll.favoriteByCount || 0 }}</span>
              </div>
              <div class="stats-card">
                <span>评论</span>
                <span style="margin-top: 5px; font-size: 18px; font-weight: 600;">{{ countAll.commentByCount || 0 }}</span>
              </div>
            </div> -->
            <div class="stats-tabs">
                <div style="margin-bottom: 5px;">最新知识({{ myList.length }})</div>
            </div>
            <div v-if="myList.length > 0" v-loading="loading" class="custom-scrollbar-container" style="flex: 1;">
              <div class="activity-item" v-for="activity in myList" :key="activity.id" @click="handleActivity(activity)"  style="gap: 0; padding: 10px 5px">
                <img :src="getFilePath(activity.targetName)" style="width: 20px; margin-right: 10px;" alt="">
                <div style="flex: 1;">
                  <div style="display: flex; justify-content: space-between;">
                     <span style="flex: 1; margin-right: 10px; cursor: pointer;" class="text-ellipsis">  {{ activity.targetName }}</span>
                     <span>{{ activity.createdBy }}</span>           
                  </div>
                  <div style="display: flex; justify-content: space-between; color: #B4B4B4">
                    <span>{{ activity.knowledgeName }} / {{activity.spaceName}}</span>
                    <span>  {{  moment(activity.visitTime).format('MM-DD HH:mm') }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div  v-else style="display: flex; justify-content: center; align-items: center; height: calc(100% - 66px);">
              <img :src="notData" style="width: 120px;" alt="">
            </div>
          </div>
        </div>

      </div>
      <!-- 右侧助手 -->
      <div class="right-assistant" :class="{ 'collapsed': !assistantVisible }">
        <div class="assistant-toggle" @click="toggleAssistant">
          <el-icon>
            <ArrowRight v-if="assistantVisible" />
            <ArrowLeft v-else />
          </el-icon>
        </div>
        <WdAgent v-show="assistantVisible" style="height: 100%;" :assistant="formData"  mode="assistantchat" :chatUrl="chatUrl" />
      </div>
      <transition name="xiaowu-fade">
        <img src="@/assets/images/xiaowu.svg" class="xiaowu-img" v-if="!assistantVisible" @click="toggleAssistant" style="position: absolute; bottom: 12px; right: 12px; cursor: pointer;" alt="">
      </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ArrowUp, Position } from '@element-plus/icons-vue'
import notData from '@/assets/images/notData.png'
import AssistantChat from '@/components/AssistantChat.vue'
import { getInfo } from '@/api/login'
import { addConfigEl } from '@/api/system/config'

import { getAssistantDetail } from '@/api/knowledgeExpert'
import WdAgent from '@/components/WdAgent.vue'
import { 
   getWorkspaceVisitRecord,
   getWorkspaceCollect,
   getWorkspaceMyKnowledge,
   myVisit
} from '@/api/retrieve'
import moment from 'moment'
import { useRouter, useRoute } from 'vue-router'
const chatUrl = ref(import.meta.env.VITE_APP_BASE_API + '/knowledge/chat')
const router = useRouter()
const route = useRoute()

const queryParams = ref({
  pageNum: 1,
  pageSize: 10
})
const queryParams1 = ref({
  pageNum: 1,
  pageSize: 20
})

const todoList = ref([]) // 待办

// Tab状态
const activeTab1 = ref('visit')
const activeTab2 = ref(1)
const user = ref({})
const h = ref('')
const w = ref('')
const d = ref('')
let intervalId

const countAll = ref({
  favoriteByCount: 0, // 收藏
  commentByCount: 0, // 评论
  visitedByCount: 0 // 访问
})

const assistantVisible = ref(true)


const collapse = ref(false)

// 活动列表数据
const activityList = ref([])

// 我的内容
const myList = ref([])

// 建议数据
const suggestions = ref([])

const loading = ref(false)

const handleTab1 = async (tab) => {
  activeTab1.value = tab
  if (tab === 'visit') {
    list3()
  } else if (tab === 'collect') {
    list2()
  }
}


const list1 = async() => {
  const res = await getWorkspaceMyKnowledge(queryParams1.value)
  myList.value = res.data.map(item => {
    return {
      ...item,
      targetName: item.fileName,
      visitTime: item.createdAt,
      knowledgeName: item.knowledgeBaseName,
      spaceName: item.knowledgeSpaceName,
      id: item.fileId,
      targetId: item.fileId,
    }
  })
}

const list2 = async() => {
  const res = await getWorkspaceCollect(queryParams.value)
  activityList.value = res.data.map((item, index) => {
    return {
      ...item,
      visitTime: item.createdAt,
      knowledgeName: item.knowledgeBaseName,
      spaceName: item.knowledgeSpaceName,
      id: item.fileId
    }
  })
}

const list3 = async() => {
  const res = await getWorkspaceVisitRecord({pageNum: 1, pageSize: 10})
  activityList.value = res.data || []
}


const handleActivity = (item) => {
  console.log('item', item)
  if (item.targetType === 'assistant') {
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
}

const handleQuickCard = (path) => {
    router.push({
      path: path
    })
  }

const toggleAssistant = () => {
  assistantVisible.value = !assistantVisible.value
}

const getFilePath = (fileName) => {
  if (!fileName) return new URL('../assets/fileIcon/othe.svg', import.meta.url).href;
  
  // 获取文件扩展名
  const extension = fileName.toLowerCase().split('.').pop();
  
  // 定义支持的文件类型
  const supportedExtensions = ['jpg', 'html', 'doc', 'ppt', 'mp4', 'txt', 'pdf', 'xls', 'png', 'md', 'csv', 'xlsx', 'xlsx', 'docx', 'jpeg'];
  
  // 特殊处理一些扩展名
  let iconName = extension;
  // 检查是否有对应的图标
  if (supportedExtensions.includes(iconName)) {
    return new URL(`../assets/fileIcon/${iconName}.svg`, import.meta.url).href;
  } else {
    return new URL('../assets/fileIcon/othe.svg', import.meta.url).href;
  }
}

const getMyVisit = async () => {
  const res = await myVisit()
  countAll.value = res.data
}



const formData = ref({})
const modelData = ref({})
const getConfigfn = async () => {
  const res = await addConfigEl({code: 'my.index.chat'})
  // agent配置详情
  //res.rows[0].configValue
  const res1 = await getAssistantDetail(res.data)
  formData.value = res1.data
}

onMounted(() => {
  user.value = JSON.parse(localStorage.getItem('userInfo'))
  intervalId = setInterval(() => {
    h.value = moment().format('HH:mm:ss');
    const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const date = new Date();
    w.value = days[date.getDay()];
    d.value = moment().format('YYYY/MM/DD');
  }, 0);
  list3()
  list1()
  getConfigfn()
  getMyVisit()
})
onUnmounted(() => {
  clearInterval(intervalId);
});
</script>

<style scoped lang="scss">
.workspace-page {
  height: 100%;
  background: #f5f7fa;
  display: flex;
  padding: 20px;
  position: relative;
  // gap: 20px;
  .top-nav {
    background: white;
    display: flex;
    padding: 0 20px;
    border-bottom: 1px solid #e4e7ed;
    
    .nav-item {
      padding: 16px 24px;
      color: #666;
      cursor: pointer;
      border-bottom: 2px solid transparent;
      
      &.active {
        color: #6b05a8;
        border-bottom-color: #6b05a8;
        font-weight: 500;
      }
    }
  }

  
  .left-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    .welcome-section {
      // background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      background-image: url('@/assets/images/index_top_bg.png');
      background-size: cover;
      background-position: right;
      color: #555555;
      padding: 20px 140px 20px 30px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      // padding-right: 144px;
      .date-info {
        display: flex;
        gap: 8px;
        align-items: center;
        margin-bottom: 12px;
        font-size: 14px;
      }
      
      .greeting {
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .quick-start-section {
      background: white;
      padding: 20px;
      
      .section-title {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 16px;
        font-weight: 600;    
        .collapse-icon {
          color: #666;
          cursor: pointer;
        }
      }
      
      .quick-cards {
        display: flex;
        gap: 12px;
        .quick-card {
          border: 1px solid #e4e7ed;
          flex: 1;
          border-radius: 8px;
          padding: 16px;
          cursor: pointer;
          transition: all 0.3s ease;
          display: flex;
          font-size: 16px;
          background: linear-gradient( 90deg, #E0F0FC 0%, #EFEAFA 100%);
          &.active {
            border-color: #6b05a8;
            background: #faf8ff;
          }
          
          &:hover {
            border-color: #6b05a8;
            transform: translateY(-2px);
          }
          
          .card-icon {
            font-size: 24px;
            margin-bottom: 8px;
            margin-right: 15px;
          }       
          .card-title {
            font-weight: 600;
            margin-bottom: 4px;
            color: #303133;
          }
          .card-desc {
            color: #909399;
            line-height: 1.4;
          }
        }
      }
    }
    
    .stats-section, .secondary-section {
      background: white;
      padding: 0 20px;
      
     
      
      .stats-content {
        .stats-cards {
          display: flex;
          gap: 40px;
          justify-content: space-between;
          margin-bottom: 24px;
          
          .stat-card {
            text-align: center;
            
            .stat-number {
              font-size: 18px;
              height: 48px;
              width: 48px;
              border-radius: 50%;
              background: #F9F9F9;
              text-align: center;
              line-height: 48px;
              font-weight: 700;
              color: #303133;
              margin-bottom: 10px;
            }
            
            .stat-label {
              font-size: 14px;
              color: #666;
            }
          }
        }
        
        .detail-stats {
          display: flex;
          gap: 40px;
          justify-content: space-between;
          .detail-item {
            display: flex;
            align-items: center;
            flex-direction: column;
            gap: 8px;
            width: 60px;
            .detail-number {
              font-size: 18px;
              font-weight: 600;
              color: #303133;
            }
            
            .detail-label {
              font-size: 14px;
              color: #666;
              margin-right: 4px;
              display: flex;
              align-items: center;
              gap: 4px;
            }
            
            .detail-dot {
              width: 8px;
              height: 8px;
              border-radius: 50%;
              
              &.green { background: #52c41a; }
              &.orange { background: #faad14; }
              &.red { background: #ff4d4f; }
            }
          }
        }
      }
      
      .notice-content, .secondary-content {
        padding: 20px;
        text-align: center;
        color: #999;
      }
    }
    
    .todo-section {
      background: white;
      padding: 20px;  
      .section-header {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
        color: #303133;
      }
      
      .todo-list {
        .todo-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          height: 40px;
          background: #F9F9F9;
          margin-bottom: 15px;
          padding: 0 15px;
          box-sizing: border-box;
          .todo-name {
            color: #303133;
            font-size: 14px;
          }
          &:hover {
            border: 1px solid #D1B6E1;
          }
          .todo-action {
            color: #6b05a8;
            font-size: 14px;
            cursor: pointer;
            
            &:hover {
              text-decoration: underline;
            }
          }
        }
      }
    }
  } 
  .activity-item {
    display: flex;
    gap: 16px;
    padding: 10px 0;
    border-bottom: 1px solid #f0f2f5;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    &:hover {
      color: #6b05a8;
      cursor: pointer;
    }
    
    .activity-number {
      // width: 24px;
      // height: 24px;
      // border-radius: 50%;
      // background: #6b05a8;
      // color: white;
      // display: flex;
      // align-items: center;
      // justify-content: center;
      // font-size: 14px;
      // font-weight: 600;
      // flex-shrink: 0;
    }
    .activity-title {
      font-size: 14px;
      color: #303133;
      display: flex;
      align-items: center;
      width: 60%;
      &:hover {
        color: #6b05a8;
        cursor: pointer;
      }
    }
    .status-text {
      color: #555555;
    }
    .activity-time {
      color: #555555;
    }
  }

}
.stats-tabs, .secondary-tabs {
        display: flex;
        gap: 24px;
        border-bottom: 1px solid #e4e7ed;
        
        .tab-item {
          padding: 12px 0;
          color: #666;
          cursor: pointer;
          border-bottom: 2px solid transparent;
          font-size: 14px;
          
          &.active {
            color: #6b05a8;
            border-bottom-color: #6b05a8;
            font-weight: 500;
          }
        }
      }
      :deep(.el-input__wrapper) {
        border: 1px solid #EBEDF7;
        box-shadow: none;
      }
  .expanded {
    margin-right: 0 !important;
  }
  .activity-header {
    display: flex;
    align-items: center;
    gap: 20px;
    background: #f5f5f5;
    height: 30px;
    font-size: 14px;
    padding: 0 10px;
    color: #909399;
  }
  .stats-card {
    flex: 1;
    height: 64px;
    background: linear-gradient( rgba(234,237,255,0.83) 0%, rgba(234,239,255,0.31) 100%);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  .xiaowu-img {
    transition: all 0.3s ease;
  }
  
  // 小物图片渐入渐出动效
  .xiaowu-fade-enter-active,
  .xiaowu-fade-leave-active {
    transition: all 0.25s ease-out;
  }
  
  .xiaowu-fade-enter-from {
    opacity: 0;
    transform: scale(0.6);
  }
  
  .xiaowu-fade-leave-to {
    opacity: 0;
    transform: scale(0.6);
  }
  
  .xiaowu-fade-enter-to,
  .xiaowu-fade-leave-from {
    opacity: 1;
    transform: scale(1);
  }
</style>