<template>
  <div class="knowledge-expert-page">
    <!-- 侧边栏 -->
    <aside class="sidebar" style="height: 100%;">
      <div class="sidebar-header">
        <div style="display: flex; align-items: center; gap: 20px;">
            <el-button icon="Back" @click="handleBack" style="width: 24px;" size="small"></el-button>
            <el-tooltip :content="formData.name" placement="top">
                <span class="text-ellipsis" style="max-width: 200px;">{{ formData.name }}</span>
            </el-tooltip>
        </div>
       <div style="display: flex; align-items: center; gap: 10px;">
            <el-button type="primary" icon="chat-dot-round" style="background: linear-gradient( 90deg, #9AC3FF 0%, #BB56FE 100%); border: none; width: 200px;" @click="handleNewChat">开启新对话</el-button>
            <el-button link @click="handleClearAllSession">
                <svg t="1569683368540" class="icon" viewBox="0 0 1024 1024" color="#FBFBFB" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9723" width="20" height="20" data-v-133a2e38=""><path d="M899.1 869.6l-53-305.6H864c14.4 0 26-11.6 26-26V346c0-14.4-11.6-26-26-26H618V138c0-14.4-11.6-26-26-26H432c-14.4 0-26 11.6-26 26v182H160c-14.4 0-26 11.6-26 26v192c0 14.4 11.6 26 26 26h17.9l-53 305.6c-0.3 1.5-0.4 3-0.4 4.4 0 14.4 11.6 26 26 26h723c1.5 0 3-0.1 4.4-0.4 14.2-2.4 23.7-15.9 21.2-30zM204 390h272V182h72v208h272v104H204V390z m468 440V674c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v156H416V674c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v156H202.8l45.1-260H776l45.1 260H672z" p-id="9724"></path></svg>
            </el-button>
        </div>
      </div>
      <div class="history-list">
        <div class="history-section" v-if="todayList.length > 0">
          <el-divider content-position="left">今天</el-divider>
          <div class="history-item" v-for="i in todayList" :key="'today'+i">
             <div class="item-content" v-if="!i.out" :class="{'checked': sessionUuid === i.uuid}" style="display: flex; align-items: center;">
                <span style="flex: 1; margin-right: 10px; cursor: pointer;" class="text-ellipsis" @click="handleDetail(i)">{{ i.sessionName }}</span>
                <span class="item-btn">
                    <el-button link icon="Edit" size="small" @click="handleEdit(i)" />
                    <el-button link icon="Delete" size="small" @click="handleDelete(i)" />
                </span>
             </div>
             <el-input v-else v-model="inputValue" @blur="handleBlur(i)" @keyup.enter="handleEnter(i)" ></el-input>
          </div>
          <div v-if="todayList.length === 0" style="color: #999; font-size: 14px; text-align: center; padding: 10px 0;">暂无对话</div>
        </div>
        <div class="history-section" v-if="historyList.length > 0">
            <el-divider content-position="left">七日内</el-divider>
          <div class="history-item" v-for="i in historyList" :key="'week'+i">
            <div v-if="!i.out" class="item-content" :class="{'checked': sessionUuid === i.uuid}" style="display: flex; align-items: center;">
                <span style="flex: 1; margin-right: 10px; cursor: pointer;" class="text-ellipsis" @click="handleDetail(i)">{{ i.sessionName }}</span>
                <span class="item-btn">
                    <el-button link icon="Edit" size="small" @click="handleEdit(i)" />
                    <el-button link icon="Delete" size="small" @click="handleDelete(i)" />
                </span>
            </div>
            <el-input v-else v-model="inputValue" @blur="handleBlur(i)" @keyup.enter="handleEnter(i)" ></el-input>
          </div>
        </div>
      </div>
    </aside>
     <!-- <AIDialogue 
         ref="aiDialogueRef" 
         style="padding: 20px 100px;" 
         :sessionUuid="sessionUuid" 
         :assistantId="formData.uuid"
         v-model:list="list"
         :assistant="formData"
         @new-chat="handleNewChat"
     /> -->
    <WdAgent 
        ref="aiDialogueRef" 
        :sessionUuid="sessionUuid" 
        :assistantId="formData.uuid"
        v-model:list="list"
        :assistant="formData"
        mode="aidialogue"
        :chatUrl="chatUrl"
        style="width: calc(100% - 280px);"
        @new-chat="handleNewChat"
    />

    <!-- 图片预览 -->
    <el-image-viewer
        v-if="showImageViewer"
        :url-list="[currentImageUrl]"
        :initial-index="0"
        @close="closeImageViewer"
        :teleported="true"
        :z-index="3000"
    />
  </div>
</template>

<script setup>
// 可根据实际需要引入element-plus组件
import { BubbleList, Sender } from 'vue-element-plus-x'
import { ElImageViewer } from 'element-plus'
import { updateAssistant, getAssistantDetail } from '@/api/knowledgeExpert'
import { getToken } from '@/utils/auth'
import robotAvatar from '@/assets/images/robot.png'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import moment from 'moment'
import AIDialogue from '@/components/AIDialogue/index.vue'
import WdAgent from '@/components/WdAgent.vue'
const chatUrl = ref(import.meta.env.VITE_APP_BASE_API + '/knowledge/chat')
import { 
    getSessionList, 
    createSession, 
    deleteSession, 
    editSessionName,
    clearAllSession,
    getSessionDetail
} from '@/api/retrieve'
import { useRoute, useRouter } from 'vue-router'
import { nextTick, onMounted, watch } from 'vue'

const bubbleListRef = ref(null)
const aiDialogueRef = ref(null)
const userId = ref('')
const val = ref(null)
const route = useRoute()
const router = useRouter()
const senderValue = ref('')
const inputValue = ref('')
const fileList = ref([])
const checkedId = ref('') // 点击的历史详情
const list = ref([])
const sessionUuid = ref('')
const todayList = ref([])
const historyList = ref([])
const modeData = ref({})
const formData = ref({})

// 图片预览相关
const showImageViewer = ref(false)
const currentImageUrl = ref('')

const fileUploadFn = (req) => {
    const file = req.file
    console.log('file1111', file)
    let name = file.name
    // 这里需要实现文件上传逻辑
}

const beforeAvatarUpload = (file) => {
    console.log('file', file)
}

const handleClose = (i) => {
    fileList.value = fileList.value.filter(item => item.url !== i.url)
}

const handleEdit = (i) => {
    i.out = true
    inputValue.value = i.sessionName
    // 使用 nextTick 确保 DOM 更新后再获取焦点
    nextTick(() => {
        const inputElement = document.querySelector('.history-item input')
        if (inputElement) {
            inputElement.focus()
        }
    })
}

const handleBack = () => {
    // router.push('/home/specialist')
    router.go(-1)
}

const handleBlur = (i) => {
    i.out = false
}

const handleEnter = (i) => {
    i.out = false
    let params = {
        sessionName: inputValue.value,
        id: i.id
    }
    editSessionName(params).then(res => {
        getHistoryList()
    })
}

const handleNewChat = (resolve) => {
    // 如果当前已有对话且对话内容为空，不再创建新对话
    if (sessionUuid.value && list.value.length === 0) {
        // 如果有 resolve 回调，调用它表示新对话已经就绪
        if (resolve && typeof resolve === 'function') {
            resolve()
        }
        return
    }

    createSession({
        userId: userId.value,
        sessionName: '新对话',
        agentUuid: formData.value.uuid
    }).then(res => {
        sessionUuid.value = res.data
        list.value = [] // 清空当前对话列表
        getHistoryList()
        
        // 等待下一帧再更新 AIDialogue 的 list
        nextTick(() => {
            if (aiDialogueRef.value) {
                aiDialogueRef.value.setList([])
            }
            // 如果有 resolve 回调，调用它表示新对话创建完成
            if (resolve && typeof resolve === 'function') {
                resolve()
            }
        })
    })
}

const generateSessionUuid = () => {
    return Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

const handleDelete = (i) => {
    ElMessageBox.confirm('确定删除该对话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteSession(i.id).then(res => {
            ElMessage.success('删除成功')
            getHistoryList()
        })
    })
}

const handleDetail = (i) => {
    // 如果当前有正在进行的对话，先断开SSE连接
    if (aiDialogueRef.value && aiDialogueRef.value.isStreaming) {
        aiDialogueRef.value.terminateFn()
    }
    
    checkedId.value = i.uuid
    sessionUuid.value = i.uuid
    getSessionDetail({
        sessionUuid: i.uuid
    }).then(res => {
        console.log('res', res)
        let arr = []
        
        // 处理返回的对话数据
        res.data.forEach(item => {
            // 添加用户消息
            if (item.userMessage) {
                arr.push({
                    key: Date.now() + Math.random(),
                    role: 'user',   
                    placement: 'end',
                    content: item.userMessage,
                    loading: false,
                    shape: 'corner',
                    variant: 'outlined',
                    isMarkdown: false,
                    typing: false,
                    avatar: '',
                    avatarSize: '24px',
                    avatarGap: '12px'
                })
            }
            
            // 添加AI回复
            if (item.agent) {
                let content = item.agent
                content = '上传模型弹窗用于上传外部训练的AI模型，其界面包含以下必填字段：\n\n- **模型名称**：文本输入，用于设置模型的显示名称\n- **模型类型**：下拉选择，用于选择模型分类\n- **文件上传**：文件选择，用于上传模型文件\n- **描述**：多行文本，用于填写模型的详细说明信息\n\n弹窗界面如下图所示：\n\n![](https://oss-kms-static-uat-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/wudao/kms/mineru-images/2025-12-14/55b586e7-f0cf-47ea-8ca4-452dd0512c08.jpg)[type:doc,chunkId:40877]'
                // 如果有引用资料，在内容后添加引用显示
                if (item.quoteList && item.quoteList.length > 0) {
                    // 使用WdAgent组件的方法生成引用资料HTML
                    if (aiDialogueRef.value && aiDialogueRef.value.generateQuoteReference) {
                        content += aiDialogueRef.value.generateQuoteReference(item.quoteList)
                    }
                }
                
                arr.push({
                    key: Date.now() + Math.random() + 1,
                    role: 'system',
                    placement: 'start',
                    content: content,
                    reasoning_content: '', // 推理内容
                    thinkingStatus: 'end', // 思考状态
                    thinlCollapse: false, // 思考折叠状态
                    loading: false,
                    shape: 'corner',
                    variant: 'filled',
                    isMarkdown: true,
                    typing: false,
                    isFog: false,
                    avatar: robotAvatar,
                    avatarSize: '24px',
                    avatarGap: '12px',
                    chatUuid: item.chatUuid,
                    type: item.feedbackType,
                    quoteList: item.quoteList
                })
            }
        })
        
        list.value = [...arr]      
        // 等待下一帧再更新 AIDialogue 的 list
        nextTick(() => {
            if (aiDialogueRef.value) {
                aiDialogueRef.value.setList([...arr])
            }
            // 添加图片点击事件
            addImageClickEvents()
        })
    })
}

// 添加图片点击事件
const addImageClickEvents = () => {
    nextTick(() => {
        // 查找所有 markdown 内容中的图片
        const markdownImages = document.querySelectorAll('.markdown-body img, .el-bubble-content img')
        
        markdownImages.forEach((img) => {
            // 移除旧的事件监听器（如果有）
            const newImg = img.cloneNode(true)
            img.parentNode.replaceChild(newImg, img)
            
            // 添加样式使图片可点击
            newImg.style.cursor = 'pointer'
            newImg.style.transition = 'transform 0.2s'
            
            // 鼠标悬停效果
            newImg.addEventListener('mouseenter', () => {
                newImg.style.transform = 'scale(1.02)'
            })
            newImg.addEventListener('mouseleave', () => {
                newImg.style.transform = 'scale(1)'
            })
            
            // 点击图片打开预览
            newImg.addEventListener('click', (e) => {
                e.preventDefault()
                e.stopPropagation()
                currentImageUrl.value = newImg.src
                showImageViewer.value = true
            })
        })
    })
}

// 关闭图片预览
const closeImageViewer = () => {
    showImageViewer.value = false
}

const handleClearAllSession = () => {
    ElMessageBox.confirm('确定清空所有会话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        clearAllSession().then(res => {
            ElMessage.success('清空成功')
            getHistoryList()
        })
    })
}

const getDetail = () => {
    getAssistantDetail(route.query.uuid).then(res => {
        if (res.data && Object.keys(res.data).length > 0) {
            formData.value = res.data
            console.log(formData.value, '123')
            getHistoryList()
        }
    })
}

const getHistoryList = () => {
    let params = {
        agentUuid: formData.value.uuid
    }
    return getSessionList(params).then(res => {
        todayList.value = res.data.filter(i => moment(i.createTime).isSame(moment(), 'day'))
        historyList.value = res.data.filter(i => !moment(i.createTime).isSame(moment(), 'day'))
        todayList.value.forEach( item => {
            item.out = false
        })
        historyList.value.forEach( item => {
            item.out = false
        })
    })
}

onMounted(() => {
    getDetail()
    userId.value = JSON.parse(localStorage.getItem('userInfo')).id
})

// 监听 list 变化，为新内容添加图片点击事件
watch(() => list.value, () => {
    addImageClickEvents()
}, { deep: true })
</script>

<style scoped lang="scss">
.knowledge-expert-page {
  display: flex;
  height: 100%;
  background: #f6f7fb;
  position: relative;
}
.sidebar {
  width: 280px;
  background: #fff;
  border-right: 1px solid #eee;
  padding: 10px 0 0 0;
  display: flex;
  flex-direction: column;
}
.sidebar-header {
  padding: 0 20px;
  font-weight: bold;
  font-size: 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.new-chat-btn {
  margin-top: 12px;
}
.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px;
}
.section-title {
  font-size: 14px;
  color: #888;
  margin: 16px 0 8px 0;
}
.history-item {
  cursor: pointer;
  
}
.item-content {
    padding: 8px 5px;
    display: flex;
    cursor: pointer;
    align-items: center;
    border-radius: 8px 8px 8px 8px;
    font-size: 14px;
  color: #333;
    .item-btn {
        display: none;
    }
}
.item-content:hover {
    background: #F9F9F9;
    .item-btn {
        display: block;
    }
}
.checked {
    background: #F9F9F9; 
}
.main-content {
  flex: 1;
  padding: 20px 100px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(#ECEFFD 0%, #F2F3F8 100%);
}
.page-title {
  text-align: center;
  margin-bottom: 32px;
  margin-top: 20%;
}
.page-title h2 {
  color: #7c3aed;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}
.subtitle {
  color: #676C90;
  font-size: 16px;
}

.recommend-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 20px;
}
.recommend-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  width: calc(50% - 10px);
  transition: all 0.3s ease;
  cursor: pointer;
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.03);
  }
}
.recommend-title {
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}
.recommend-desc {
  font-size: 14px;
  color: #676C90;
}
.assistant-title {
    font-weight: 600;
    margin-bottom: 4px;
    background: linear-gradient(270.0000000000106deg, #5C078F 0%, #AF50C5 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
    font-size: 37px;
}

.text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

:deep(.el-upload-list) {
    display: none !important;
}
:deep(.el-select__wrapper) {
    box-shadow: none;
}
:deep(.el-select__wrapper:hover) {
  box-shadow: none;
}
</style>
<style lang="scss">
.el-sender {
    background-color: #fff;
    border-radius: 8px 8px 8px 8px;
    // border: 1px solid #EBEDF7;
    border: none !important;
    box-shadow: none;
}
.el-bubble-list {
    .el-bubble-content {
        background-color: #fff !important;
        max-width: 100%;
    }
}
.el-bubble-end {
    .el-bubble-avatar-placeholder {
        display: none;
    }
}
.markdown-body p {
    margin-bottom: 0;
}

/* 图片预览样式优化 */
:deep(.markdown-body img),
:deep(.el-bubble-content img) {
    cursor: pointer;
    transition: all 0.2s ease;
    border-radius: 4px;
}

:deep(.markdown-body img:hover),
:deep(.el-bubble-content img:hover) {
    transform: scale(1.02);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>