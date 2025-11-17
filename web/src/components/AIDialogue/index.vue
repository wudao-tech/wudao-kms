<template>
    <div
    class="ai-dialogue"
   :style="assistant.bgImg ? { backgroundImage: `url(${assistant.bgImg})`, backgroundSize: 'cover', backgroundPosition: 'bottom' } : {}"
  >
        <div class="assistant-header-warp" v-if="assistant?.bgImg">
            <span style="font-size: 16px; font-weight: 700;"></span>
            <div style="display: flex; align-items: center; gap: 5px; cursor: pointer; color: #333;">
                <el-popover v-if="assistant?.type === 'workflow'" placement="bottom-end" :width="400" trigger="click">
                    <template #reference>
                        <el-button link style="color: #333;" @click="handleInputParams">
                            <el-icon style="color: #333;"><Operation /></el-icon>入参
                        </el-button>
                    </template>
                    <div>请填写输入参数值，填完后会将参数带入后续的对话中</div>
                    <div v-for="item in assistant.graphConfig" :key="item.paramName" style="margin: 10px 0;">
                       <div style="margin: 3px 0 6px 0;">
                        <span style="font-weight: 600; padding-left: 5px; font-size: 16px;">{{ item.paramName }}</span>
                        <el-tooltip :content="item.description" placement="top">
                            <el-icon style="color: #999;"><QuestionFilled /></el-icon>
                        </el-tooltip>
                        <span class="out-view-item-type">String</span>
                       </div>
                       <el-input v-model="item.value" placeholder="请输入参数值" />
                    </div>
                </el-popover>
                <el-button link style="color: #333" @click="handleMemory">
                    <svg-icon icon-class="jiyi1" style="font-size: 14px;"></svg-icon>
                    <span style="font-size: 14px;">记忆</span>
                </el-button>
            </div>
        </div>
        <div v-if="list.length > 0" style="flex: 1; padding: 20px; overflow-y: auto;">
            <bubble-list ref="bubbleListRef" :list="list" max-height="100%">
                <template #header="{ item }">
                    <Thinking
                        v-if="item.reasoning_content"
                        v-model="item.thinlCollapse"
                        :content="item.reasoning_content"
                        :status="item.thinkingStatus"
                        class="thinking-chain-warp"
                    />
                </template>
                <template #content="{ item }">
                    <!-- <Typewriter v-if="item.content && item.role === 'system'"  is-fog typing  :content="item.content" :is-markdown="true" /> -->
                    <!-- <XMarkdown v-if="item.content && item.role === 'system'" :markdown="item.content" class="markdown-body" :themes="{ light: 'github-light', dark: 'github-dark' }" default-theme-mode="dark" /> -->
                    <XMarkdown 
                        v-if="item.content && item.role === 'system'" 
                        :markdown="item.content" 
                        class="markdown-body" 
                        :themes="{ light: 'github-light', dark: 'github-dark' }" 
                        default-theme-mode="dark"
                        :html="true"
                        :linkify="true"
                    /> 
                    <!-- user 图片消息 -->
                    <div v-if="item.files && item.files.length > 0 && item.role === 'user'" class="user-file-content">
                        <div class="file-list-container">
                            <div v-for="(file, index) in item.files" :key="index" class="file-item">
                                <!-- 图片文件 -->
                                <div class="image-file">
                                    <img :src="file.url" :alt="file.name" class="file-image" />
                                    <div class="file-overlay">
                                        <span class="file-name">{{ file.name }}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- user 文本内容 -->
                    <div v-if="item.content && item.role === 'user'" class="user-content">
                        {{ item.content }}
                    </div>
                </template>
                <template #footer="{ item }">
                    <div class="footer-wrapper">
                        <div class="footer-container">
                            <el-tooltip content="重做" placement="top" v-if="item.role === 'system'">
                                <el-button link @click="handleRedo(item)">
                                <img :src="redo" alt="redo" style="width: 17px; height: 17px;">
                            </el-button>
                            </el-tooltip>
                            <el-tooltip content="复制" placement="top">
                                <el-button link @click="handleCopy(item)">
                                    <img :src="copy" alt="copy" style="width: 17px; height: 17px;">
                                </el-button>
                            </el-tooltip>
                            <!-- <el-tooltip content="点赞" placement="top" v-if="item.role === 'system'">
                                <el-button link>
                                    <img :src="up" alt="copy" style="width: 17px; height: 17px;">
                                </el-button>
                            </el-tooltip>
                            <el-tooltip content="点踩" placement="top" v-if="item.role === 'system'">
                                <el-button link>
                                    <img :src="down" alt="down" style="width: 17px; height: 17px;">
                                </el-button>
                            </el-tooltip> -->
                            <!-- <div v-if="item.role === 'user'">
                                <el-icon style="cursor: pointer;" @click="handlePrev(item)"><ArrowLeftBold /></el-icon>
                                <span>2/3</span>
                                <el-icon style="cursor: pointer;" @click="handleNext(item)"><ArrowRightBold /></el-icon>
                            </div> -->
                        </div>
                        <!-- 追问功能 -->
                        <div v-if="item.role === 'system' && item.followUpQuestions && item.followUpQuestions.length > 0" class="follow-up-questions">
                            <div v-for="(question, index) in item.followUpQuestions" :key="index" class="follow-up-item" @click="handleQuickCommand(question)">
                                {{ question }}
                            </div>
                        </div>
                    </div>
                </template>
            </bubble-list>
        </div>
        <div v-else style="flex: 1; padding: 20px;">
            <div class="assistant-header" style="margin-top: 40px;" v-if="assistant">
                <img :src="assistant.logo" style="width: 80px; height: 80px; border-radius: 8px; object-fit: cover;"  alt="">
                <span style="font-size: 20px; font-weight: 700; line-height: 36px;">{{ assistant.name }}</span>
            </div>
            <div class="assistant-desc" v-if="assistant?.guideWord">{{ assistant.guideWord }}</div>
            <div class="suggestions" v-if="assistant?.guideQuestions">
                <div class="suggestion-item" v-for="suggestion in assistant.guideQuestions" :key="suggestion" @click="handleSuggestion(suggestion)">
                    <div class="suggestion-content">
                        <div class="suggestion-title"><img src="@/assets/images/guide_icon.svg" alt="" style="width: 20px; height: 20px; margin-right: 10px;">{{ suggestion}}</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="chat-area">
            <!-- 快捷指令 -->
            <div style="display: flex; margin-bottom: 5px;" v-if="assistant?.quickCommands?.length > 0">
                <div v-for="(item, index ) in assistant.quickCommands" :key="index" class="quick-command-item" @click="handleQuickCommand(item.desc)">
                    <svg-icon :icon-class="item.icon" style="font-size: 14px; margin-right: 5px; margin-top: 2px; color: #333;" />
                    <span style="font-size: 14px;font-weight: 700; height: 22px; color: #838383;">{{ item.title }}</span>
                </div>
            </div>
            <Sender ref="senderRef" v-model="senderValue" variant="updown" :auto-size="{ minRows: 2, maxRows: 4 }" clearable allow-speech placeholder="请输入问题" @submit="handleSubmit">
                <template #header>
                    <div class="header-self-wrap">
                        <div class="file-list-container">
                            <div v-for="(item, index) in fileList" :key="index" class="file-item">
                                <!-- 图片文件 -->
                                <div class="image-file">
                                    <img :src="item.url" :alt="item.name" class="file-image" />
                                    <div class="file-overlay">
                                        <span class="file-name">{{ item.name }}</span>
                                    </div>
                                    <!-- 删除按钮在右上角 -->
                                    <el-button type="danger" size="small" circle @click="handleClose(item)" class="remove-btn">
                                        <el-icon><Close /></el-icon>
                                    </el-button>
                                </div>
                            </div>
                        </div>
                        <div v-if="fileList.length === 0" class="empty-state">
                            <div style="text-align: center; color: #999; font-size: 14px; padding: 20px;">
                                <div style="margin-bottom: 8px;">📷 仅支持图片格式</div>
                                <div style="font-size: 12px; color: #ccc;">支持 JPG、PNG、GIF、WebP 等图片格式</div>
                            </div>
                        </div>
                    </div>
                </template>
                <template #prefix>
                    <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                        <div class="online-btn" :style="{ color: enableSearch ? '#333' : '#999' }" v-if="assistant?.search" @click="enableSearch = !enableSearch">
                            已联网
                            <span v-if="enableSearch" style="width: 5px; height: 5px; background: #3DCD58; border-radius: 50%;"></span>
                            <span v-else style="width: 5px; height: 5px; background: #999; border-radius: 50%;"></span>
                        </div>
                        <div class="online-btn" :style="{ color: enableDeepChat ? '#333' : '#999' }" v-if="assistant?.deepThinkingModel" @click="enableDeepChat = !enableDeepChat">
                            深度思考
                            <span v-if="enableDeepChat" style="width: 5px; height: 5px; background: #3DCD58; border-radius: 50%;"></span>
                            <span v-else style="width: 5px; height: 5px; background: #999; border-radius: 50%;"></span>
                        </div>
                    </div>
                </template>
                <template #action-list>
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <el-upload
                            style="margin-top: 2px;"
                            v-if="assistant?.multimodal || assistant?.type === 'workflow'"
                            action="/s3/upload"
                            :http-request="fileUploadFn"
                            accept="image/*"
                            :before-upload="beforeUpload"
                        >
                            <el-button link>
                                <el-icon style="cursor: pointer; font-size: 20px;"><Paperclip /></el-icon>
                            </el-button>
                        </el-upload>
                        <!-- <el-button link>
                            <el-icon style="cursor: pointer; font-size: 20px;"><Microphone /></el-icon>
                        </el-button> -->
                        <el-button link style="color: #333;" @click="handleClear">
                            <i style="font-size: 18px;" class="iconfont se-dig-icon-emb-clear"></i>
                        </el-button>
                        <el-button 
                            v-if="!isStreaming"
                            round  
                            style="width: 60px;background: linear-gradient(90deg, rgb(154, 195, 255) 0%, rgb(187, 86, 254) 100%)" 
                            @click="handleSubmit"
                        >
                            <el-icon style="color: #fff; font-size: 22px;"><Promotion /></el-icon>
                        </el-button>
                        <el-button 
                            v-if="isStreaming"
                            round  
                            style="width: 60px;background: linear-gradient(90deg, rgb(154, 195, 255) 0%, rgb(187, 86, 254) 100%)" 
                            @click="terminateFn"
                        >
                            <el-icon style="color: #fff; font-size: 22px;"><VideoPause /></el-icon>
                        </el-button>
                    </div>
                </template>
            </Sender>
        </div>
        <el-dialog v-model="memoryVisible" title="" width="50%">
            <template #header>
                <div style="display: flex; gap: 10px; align-items: center; font-size: 20px; font-weight: 700;">
                    <span style="cursor: pointer;" :class="{ 'active-tab': active === 'variable' }" @click="() => { active = 'variable'; getTableData(); }">记忆变量</span>
                    <el-divider direction="vertical" />
                    <span style="cursor: pointer;" :class="{ 'active-tab': active === 'table' }" @click="() => { active = 'table'; getTableData(); }">记忆表</span>
                </div>
            </template>
            <div v-if="active === 'variable'" style="min-height: 200px;">
                <el-table :data="variableList" border>
                    <el-table-column prop="key" label="变量名称" />
                    <el-table-column prop="value" label="变量值" />
                    <el-table-column prop="tempMemory" label="记忆时常" >
                        <template #default="{ row }">
                            <span>{{ row.tempMemory === 'FOREVER' ? '永久' : '单次会话' }}</span>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div v-else  style="min-height: 200px;">
                <el-tabs v-model="memoryType" type="card" @tab-click="handleTabClick">
                    <!-- 表名称 -->
                    <el-tab-pane v-for="(item, index) in assistant?.memoryTable" :key="index" :label="item.tableName" :name="item.tableName"></el-tab-pane>
                </el-tabs>
                <!-- 表下面的data表 -->
                <el-table :data="currentTableInfo.data" v-if="currentTableInfo.fields.length > 0" border>
                    <el-table-column
                        v-for="field in currentTableInfo.fields"
                        :key="field.fieldName"
                        :prop="field.fieldName"
                        :label="field.fieldName"
                    />
                </el-table>
                <div v-else style="text-align: center; padding: 40px; color: #999;">
                    暂无数据
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { BubbleList, Sender, XMarkdown, Thinking, Typewriter } from 'vue-element-plus-x'
import { ElMessage } from 'element-plus'
import { Operation, QuestionFilled } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { fileUpload } from '@/api/ai/aiStore'
import robotAvatar from '@/assets/images/robot.png'
import copy from './img/copy.svg'
import down from './img/down.svg'
import up from './img/up.svg'
import redo from './img/redo.svg'
import axios from 'axios'

const props = defineProps({
    // 每次新的对话就是一个新的uuid
    sessionUuid: {
        type: String,
        default: ''
    },
    // 选择的模型
    assistantId: {
        type: String,
        default: ''
    },
    assistant: {
        type: Object,
        default: () => {}
    }
})
const userId = ref('')
const emit = defineEmits(['update:list', 'new-chat'])

// 记录是否进入思考状态
let isThinking = false

const list = ref([])
const bubbleListRef = ref(null)
const senderValue = ref('')
const fileList = ref([])
const senderRef = ref(null)

// 记忆相关变量
const active = ref('variable')
const memoryType = ref('')
const memoryVisible = ref(false)
const variableList = ref([])
const questList = ref([])

const status = ref(null)
const abortController = ref(null) // 用于控制SSE连接
const isStreaming = ref(false) // 用于控制按钮显示状态
const enableDeepChat = ref(false) //是否启用深度思考
const enableSearch = ref(false) //是否启用联网

// 计算属性：动态获取当前记忆表的字段和数据
const currentTableInfo = computed(() => {
    if (memoryType.value && props.assistant?.memoryTable) {
        const currentTable = props.assistant.memoryTable.find(table => table.tableName === memoryType.value)
        if (currentTable) {
            return {
                fields: currentTable.tableFields || [],
                data: currentTable.tableData || []
            }
        }
    }
    return { fields: [], data: [] }
})

const handleClose = (file) => {
    fileList.value = fileList.value.filter(item => item.url !== file.url)
    
    // 如果文件列表为空，自动关闭header
    if (fileList.value.length === 0) {
        closeHeader()
    }
}

// 清空聊天内容，回到初始化状态
const handleClear = () => {
    // 清空聊天列表
    list.value = []
    
    // 清空输入框
    senderValue.value = ''
    
    // 清空文件列表
    fileList.value = []
    
    // 关闭文件上传区域
    closeHeader()
    
    // 重置流状态
    isStreaming.value = false
    
    // 终止当前连接（如果有的话）
    if (abortController.value) {
        abortController.value.abort()
        abortController.value = null
    }
    
    // 重置思考状态
    isThinking = false
    
    // 生成新的会话ID
    sessionUuid.value = generateSessionUuid()
    
    // 重置状态
    status.value = null
    
    ElMessage.success('聊天内容已清空')
}

//终止输出
const terminateFn = () => {
  if (abortController.value) {
    abortController.value.abort()
    abortController.value = null
    
    // 重置流状态
    isStreaming.value = false
    
    // 更新最后一条AI消息的状态
    const lastMessageIndex = list.value.length - 1
    if (lastMessageIndex >= 0 && list.value[lastMessageIndex].role === 'system') {
      list.value[lastMessageIndex] = {
        ...list.value[lastMessageIndex],
        loading: false,
        typing: false
      }
    }
    
    // 清空文件列表并关闭header
    fileList.value = []
    closeHeader()
    
    // ElMessage.info('输出已终止')
  }
}

const handleSuggestion = (suggestion) => {
    senderValue.value = suggestion
    handleSubmit()
}

// 上传前验证文件类型
const beforeUpload = (file) => {
    const isImage = file.type.startsWith('image/')
    if (!isImage) {
        ElMessage.error('只能上传图片格式的文件！')
        return false
    }
    
    // 检查文件大小（可选，比如限制为10MB）
    const isLt10M = file.size / 1024 / 1024 < 10
    if (!isLt10M) {
        ElMessage.error('图片大小不能超过 10MB!')
        return false
    }
    
    return true
}
const closeHeader = () => {
    if (senderRef.value) {
        senderRef.value.closeHeader()
    }
}

const fileUploadFn = async (req) => {
    const file = req.file
    const name = file.name
    try {
        const format = file.type.split('/').slice(-1)[0] || file.name.split('.').pop()
        const res = await fileUpload(file, 'file', format)
        console.log('res', res)
        fileList.value.push({
            name: name,
            url: res.path, // 直接使用返回的路径
            size: file.size,
            type: file.type
        })   
         // 如果有文件上传，自动打开header显示
         if (senderRef.value) {
            senderRef.value.openHeader()
        }
        console.log('fileList', fileList.value)   
    } catch (error) {
        console.error('文件上传失败:', error)
        ElMessage.error('文件上传失败，请重试')
    }
}

const generateSessionUuid = () => {
    return Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}
// 上一步版本
const handlePrev = (item) => {
    console.log('prev', item)
}
// 下一步版本
const handleNext = (item) => {
    console.log('next', item)
}

const handleInputParams = () => {
    console.log('handleInputParams')
}

const handleMemory = () => {
    memoryVisible.value = true
    // 如果有记忆表，默认选择第一个
    if (props.assistant?.memoryTable && props.assistant.memoryTable.length > 0) {
        memoryType.value = props.assistant.memoryTable[0].tableName
        updateTableFields()
    }
    // getTableData() 现在通过 watch 监听器自动调用
}

const handleTabClick = (tab) => {
    memoryType.value = tab.name
    updateTableFields()
    getTableData()
}

const updateTableFields = () => {
    if (memoryType.value && props.assistant?.memoryTable) {
        const currentTable = props.assistant.memoryTable.find(table => table.tableName === memoryType.value)
    }
}

const handleQuickCommand = (item) => {
    senderValue.value = item
    handleSubmit()
}

// 判断是否为图片文件
const isImageFile = (fileName) => {
    const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg']
    const extension = fileName.split('.').pop().toLowerCase()
    return imageExtensions.includes(extension)
}

// 获取文件扩展名
const getFileExtension = (fileName) => {
    return fileName.split('.').pop().toUpperCase()
}

const handleSubmit = async () => {
    if (!senderValue.value.trim()) return
    
    // 如果没有 sessionUuid，先创建新对话
    if (!props.sessionUuid) {
        // 触发新对话创建，并等待完成
        await new Promise((resolve) => {
            emit('new-chat', resolve)
        })
        // 创建完成后，继续执行对话流程
    }
    
    // 设置流状态
    isStreaming.value = true
    
    // 重置思考状态
    isThinking = false
    
    const userMessage = senderValue.value
    
    // 如果有上传的文件，先添加文件消息
    if (fileList.value.length > 0) {
        // 添加文件消息
        list.value.push({
            key: Date.now(),
            role: 'user',
            placement: 'end',
            content: '', // 内容为空，使用自定义模板显示
            files: [...fileList.value], // 保存文件信息
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
    
    // 如果有文本输入，添加文本消息
    if (userMessage.trim()) {
        list.value.push({
            key: Date.now() + 1,
            role: 'user',   
            placement: 'end',
            content: userMessage,
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

    // 清空输入框
    senderValue.value = ''

    // 添加AI回复占位
    const aiMessageIndex = list.value.length
    list.value.push({
        key: Date.now() + 2,
        role: 'system',
        placement: 'start',
        content: '',
        reasoning_content: '', // 推理内容
        thinkingStatus: 'start', // 思考状态
        thinlCollapse: false, // 思考折叠状态
        loading: true,
        shape: 'corner',
        variant: 'filled',
        isMarkdown: true,
        typing: false,
        isFog: false,
        avatar: robotAvatar,
        avatarSize: '24px',
        avatarGap: '12px',
        followUpQuestions: [] // 追问内容
    })

    try {
        // message 只传递文本内容，文件通过 file 字段单独传递
        const messageContent = userMessage.trim() || ''
        
        // 在这里判断是工作流（streamResponseFollw）还是智能配置（streamResponse）， 区分两个
        console.log('props.assistant.type', props.assistant?.type)
        if (props.assistant?.type === 'workflow') {
            await streamResponseFollw(messageContent, aiMessageIndex)
        } else {
            await streamResponse(messageContent, aiMessageIndex)
        }
        emit('update:list', list.value)
    } catch (error) {
        // 更新AI消息为错误状态
        list.value[aiMessageIndex] = {
            ...list.value[aiMessageIndex],
            content: '抱歉，请求失败，请稍后重试',
            loading: false,
            typing: false
        }
        emit('update:list', list.value)
    }
}

const streamResponse = async (userMessage, messageIndex) => {
    const requestBody = {
        message: userMessage,
        sessionUuid: props.sessionUuid || generateSessionUuid(),
        userId: userId.value,
        assistantUuid: props.assistantId,
        enableDeepChat: enableDeepChat.value,
        enableSearch: enableSearch.value
    }
    if (fileList.value.length > 0) {
        requestBody.file = fileList.value.map(i => {
            return {
                mediaUrl: i.url,
                mediaType: i.name.split('.').pop()
            }
        })
    }
    
    // 创建新的AbortController
    abortController.value = new AbortController()
    
    try {
        await fetchEventSource(import.meta.env.VITE_AIDOJO_CHAT_URL + '/agent/v2/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`,
                'Cookie': `Authorization=${getToken()}`
            },
            body: JSON.stringify(requestBody),
            signal: abortController.value.signal, // 添加信号支持
            
            onopen: (response) => {
                if (!response.ok) {
                    throw new Error(`SSE连接失败: ${response.status} ${response.statusText}`)
                }
            },
            
            onmessage: (event) => {
                try {
                    const data = event.data
                    
                    // 安全检查：确保 messageIndex 有效且对应的消息存在
                    if (!list.value[messageIndex]) {
                        console.warn('messageIndex 无效或消息不存在:', messageIndex)
                        return
                    }
                    
                    if (data === '[DONE]') {
                        // 流结束
                        isStreaming.value = false
                        list.value[messageIndex] = {
                            ...list.value[messageIndex],
                            loading: false,
                            typing: false
                        }
                        
                        // 收到[DONE]时调用追问功能
                        if (props.assistant?.followup?.type === 'div') {
                            followUpQuestionFn(messageIndex)
                        }
                        
                        // 清空文件列表并关闭header
                        fileList.value = []
                        closeHeader()
                        emit('update:list', list.value)
                        return
                    }

                    // 尝试解析JSON数据
                    try {
                        const parsedData = JSON.parse(data)
                        
                        if (parsedData.outputType === 'reasoning') {
                            console.log(123, parsedData)
                            // 检查 content 是否为空
                            const content = parsedData.content || ''
                            if (!content && content !== 0) {
                                return
                            }
                            
                            // 开始思考链状态
                            if (!isThinking) {
                                isThinking = true
                                list.value[messageIndex].thinkingStatus = 'thinking'
                                list.value[messageIndex].loading = true
                                list.value[messageIndex].thinlCollapse = true
                                list.value[messageIndex].reasoning_content = ''
                            }
                            // 推理过程内容，累积到 reasoning_content
                            list.value[messageIndex].reasoning_content += content
                            
                        } else if (parsedData.outputType === 'text') {
                            // 检查 content 是否为空
                            const content = parsedData.content || ''
                            if (!content && content !== 0) {
                                return
                            }
                            
                            // 结束思考链状态
                            isThinking = false
                            list.value[messageIndex].thinkingStatus = 'end'
                            list.value[messageIndex].loading = false
                            
                            // 文本内容更新到主要内容中
                            list.value[messageIndex].content += content
                            list.value[messageIndex].typing = true
                            list.value[messageIndex].isFog = true
                        }
                        console.log(789, list.value)
                        // 无论哪种类型，都要触发更新
                        emit('update:list', list.value)
                        
                    } catch (e) {
                        // 如果不是JSON，当作普通文本处理
                        // 检查原始数据是否为空
                        if (!data && data !== 0) {
                            return
                        }
                        
                        list.value[messageIndex] = {
                            ...list.value[messageIndex],
                            content: list.value[messageIndex].content + data,
                            loading: false,
                            typing: true,
                            isFog: true
                        }
                        emit('update:list', list.value)
                    }
                } catch (error) {
                    console.error('处理SSE消息时出错:', error)
                }
            },
            
            onclose: () => {
                isStreaming.value = false
                
                // 安全检查：确保 messageIndex 有效且对应的消息存在
                if (list.value[messageIndex]) {
                    list.value[messageIndex] = {
                        ...list.value[messageIndex],
                        loading: false,
                        typing: false
                    }
                    
                    // SSE连接关闭时调用追问功能
                    if (props.assistant?.followup?.type === 'div') {
                        followUpQuestionFn(messageIndex)
                    }
                }
                
                fileList.value = []
                closeHeader()
                emit('update:list', list.value)
                
                // 清理AbortController
                abortController.value = null
            },
            
            onerror: (err) => {
                isStreaming.value = false
                // 如果是手动终止，不抛出错误
                if (err.name === 'AbortError') {
                    console.log('SSE连接已被手动终止')
                    return
                }
                throw err
            }
        })
    } catch (error) {
        console.error('SSE流处理错误:', error)
        throw error
    }
}

const streamResponseFollw = async (userMessage, messageIndex) => {
    // 将graphConfig数组转换为键值对格式
    const graphConfigParams = {}
    if (props.assistant?.graphConfig && Array.isArray(props.assistant.graphConfig)) {
        props.assistant.graphConfig.forEach(item => {
            if (item.paramName && item.value !== undefined) {
                graphConfigParams[item.paramName] = item.value
            }
        })
    }
    
    const requestBody = {
        sessionUuid: props.sessionUuid || generateSessionUuid(),
        userId: userId.value,
        assistantUuid: props.assistantId,
        enableDeepChat: enableDeepChat.value,
        enableSearch: enableSearch.value,
        
        threadId: props.sessionUuid || generateSessionUuid(),
        initialParams: {
          ...graphConfigParams, // 展开转换后的参数
          conversation_id: props.sessionUuid || generateSessionUuid(),
          input: userMessage,
          graphConfig: props.assistant?.graphConfig
        }
    }
    if (fileList.value.length > 0) {
        requestBody.initialParams.fileUrls = fileList.value.map((i) => i.url)
    }
    if (status.value === 'waiting_for_feedback') {
        requestBody.resumParam = {
        feed_back: userMessage,
        fileUrls: fileList.value.map(i => i.url),
        } 
    } else {
        delete requestBody.resumParam
    }
      
    try {
        await fetchEventSource( import.meta.env.VITE_AIDOJO_CHAT_URL + '/agent/v2/api/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`,
          },
          body: JSON.stringify(requestBody),
          
          onopen: (response) => {
            if (response.ok && response.status === 200) {
            } else {
              throw new Error(`SSE连接失败: ${response.status} ${response.statusText}`)
            }
          },
          
          onmessage: (event) => {
            try {
              const data = JSON.parse(event.data)
              console.log('data', data)
             
              // 安全检查：确保 messageIndex 有效且对应的消息存在
              if (!list.value[messageIndex]) {
                console.warn('messageIndex 无效或消息不存在:', messageIndex)
                return
              }
             
              const result = extractMessageFromSSEData(data)  
              if (data === '[DONE]') {
                // 流结束
                isStreaming.value = false
                list.value[messageIndex] = {
                  ...list.value[messageIndex],
                  loading: false,
                  typing: false
                }
                
                // 收到[DONE]时调用追问功能
                if (props.assistant?.followup?.type === 'div') {
                    followUpQuestionFn(messageIndex)
                }
                
                // 清空文件列表并关闭header
                fileList.value = []
                closeHeader()
                emit('update:list', list.value)
                return
              }

              if (data.node === '__END__') {
                status.value = null
              }
              // 如果有消息内容，更新AI回复
              if (result && result.message) {
                list.value[messageIndex] = {
                  ...list.value[messageIndex],
                  content: list.value[messageIndex].content + result.message,
                  loading: false,
                  typing: true,
                  isFog: true
                }
                emit('update:list', list.value)
              }
              
              // 处理status
              if (result && result.status) {
                status.value = result.status
                // 可以在这里添加对status的处理逻辑
              }
            } catch (error) {
              console.error('fetchEventSource 处理SSE消息时出错:', error)
            }
          },
          
          onclose: () => {
            // 流结束后，停止打字效果
            isStreaming.value = false
            
            // 安全检查：确保 messageIndex 有效且对应的消息存在
            if (list.value[messageIndex]) {
                list.value[messageIndex] = {
                    ...list.value[messageIndex],
                    loading: false,
                    typing: false
                }
                
                // SSE连接关闭时调用追问功能
                if (props.assistant?.followup?.type === 'div') {
                    followUpQuestionFn(messageIndex)
                }
            }
            
            fileList.value = []
            closeHeader()
            emit('update:list', list.value)
          },
          
          onerror: (err) => {
            isStreaming.value = false
            throw err
          }
        })
      } catch (error) {
        console.error('SSE流处理错误:', error)
        throw error
      }
}

const extractMessageFromSSEData = (data) => {
    try {
        // 解析原始数据（如果传入的是字符串）
        const jsonData = typeof data === 'string' ? JSON.parse(data) : data;
        
        // 获取node值和state对象
        const { node, state } = jsonData;
        
        // 检查state是否存在且为对象
        if (!state || typeof state !== 'object') {
          console.log('state不存在或不是对象:', state);
          return null;
        }
        
        // 遍历state的所有属性
        for (const [key, value] of Object.entries(state)) {
          // 检查是否是_output结尾的键
          if (key.endsWith('_output')) {
            // 去掉_output后缀后比较是否等于node值
            const baseKey = key.replace(/_output$/, '');
            if (baseKey === node) {
              try {
                // 尝试解析_output的值
                const outputValue = typeof value === 'string' ? JSON.parse(value) : value;
                
                // 检查是否包含message字段
                let obj = {
                  message: '',
                  status: null
                }
                if (outputValue && typeof outputValue === 'object' && 'message' in outputValue) {
                  obj.message = outputValue.message
                }
                
                // 检查是否包含status字段
                if (outputValue && typeof outputValue === 'object' && 'status' in outputValue) {
                  obj.status = outputValue.status
                }
                return obj
              } catch (e) {
                // JSON解析失败，跳过
                continue;
              }
            }
          }
        }
      } catch (e) {
        console.error('数据处理出错:', e);
      }
      
      return null; // 没有找到符合条件的message
}

// 重做
const handleRedo = (item) => {
    // 找到当前 AI 回答对应的用户问题
    const currentIndex = list.value.findIndex(msg => msg.key === item.key)
    if (currentIndex > 0) {
        // 找到前一条用户消息
        const userMessage = list.value[currentIndex - 1]
        if (userMessage && userMessage.role === 'user') {
            // 重新发送用户的问题
            senderValue.value = userMessage.content
            handleSubmit()
        }
    }
}
// 复制
const handleCopy = async (item) => {
    try {
        let contentToCopy = ''
        // 如果是系统消息（AI回答），复制主要内容
        if (item.role === 'system') {
            contentToCopy = item.content || ''
        } else if (item.role === 'user') {
            // 如果是用户消息，复制用户内容
            contentToCopy = item.content || ''
        }     
        if (contentToCopy) {
            // 使用现代的 Clipboard API
            if (navigator.clipboard) {
                await navigator.clipboard.writeText(contentToCopy)
            } else {
                // 降级方案：使用传统的 execCommand
                const textArea = document.createElement('textarea')
                textArea.value = contentToCopy
                textArea.style.position = 'fixed'
                textArea.style.opacity = '0'
                document.body.appendChild(textArea)
                textArea.focus()
                textArea.select()
                
                try {
                    document.execCommand('copy')
                } catch (err) {
                    console.error('复制失败:', err)
                    ElMessage.error('复制失败，请手动复制')
                } finally {
                    document.body.removeChild(textArea)
                }
            }
        }
    } catch (error) {
        ElMessage.error('复制失败，请手动复制')
    }
}

watch(() =>[ props.sessionUuid, props.assistantId], ([newSessionUuid, newAssistantId]) => {
 
}, { immediate: true })

// 监听 assistant 变化，初始化功能开关状态
watch(() => props.assistant, (newVal) => {
    if (newVal) {
        enableDeepChat.value = newVal.deepThinkingModel || false
        enableSearch.value = newVal.search || false
    }
}, { immediate: true, deep: true })
const getTableData = async () => {
    try {
        let params = {
            agentUuid: props.assistant?.uuid,
            sessionUuid: generateSessionUuid(),
            type: active.value === 'table' ? 'table' : 'variable',
            userId: userId.value
        }

        if (active.value === 'table' && memoryType.value) {
            params.tableName = memoryType.value
        }

        const response = await axios.get(import.meta.env.VITE_AIDOJO_CHAT_URL + '/agent/v2/api/getMemory', {
            params,
            headers: {
                'Content-Type': 'application/json'
            }
        })

        if (response.data && response.data.code === 'ok') {
            // 处理返回的数据
            if (active.value === 'variable') {
                // 更新记忆变量数据
                const variableData = response.data.data.variableData

                if (!variableData || Object.keys(variableData).length === 0) {
                    // 如果是空对象，使用 assistant.memoryVars
                    variableList.value = props.assistant?.memoryVars || []
                } else {
                    // 如果有值，将对象转换为数组并匹配 key
                    const updatedVariables = props.assistant?.memoryVars.map(item => {
                        // 如果后端返回的数据中有相同的 key，就使用后端的 value
                        if (variableData.hasOwnProperty(item.key)) {
                            return {
                                ...item,
                                value: variableData[item.key]
                            }
                        }
                        return item
                    })
                    variableList.value = updatedVariables
                }
            } else if (active.value === 'table') {
                // 更新记忆表数据
                const tableData = response.data.data.tableData

                if (tableData && Object.keys(tableData).length > 0) {
                    // 遍历每个表的数据
                    Object.keys(tableData).forEach(tableName => {
                        const tableInfo = tableData[tableName]
                        if (tableInfo && tableInfo.data && Array.isArray(tableInfo.data)) {
                            // 找到对应的记忆表配置
                            const memoryTableConfig = props.assistant?.memoryTable.find(table => table.tableName === tableName)
                            if (memoryTableConfig) {
                                // 更新该表的数据
                                memoryTableConfig.tableData = tableInfo.data
                            }
                        }
                    })
                } else {

                }
            }
        } else {
            console.error('获取记忆数据失败:', response.data?.msg || '未知错误')
        }
    } catch (error) {
        console.error('getTableData 请求失败:', error)
    }
}

// 追问
const followUpQuestionFn = async (messageIndex) => {
    if (props.assistant?.followup?.type === 'div') {
        try {
            const response = await axios.get(import.meta.env.VITE_AIDOJO_CHAT_URL + `/agent/v2/api/followup/${props.assistant?.uuid}/${generateSessionUuid()}`)
            if (response.data.code === 'ok' && response.data.data) {
                // 将追问内容存储到对应的消息中
                if (list.value[messageIndex]) {
                    list.value[messageIndex].followUpQuestions = response.data.data
                }

                // 同时更新全局的questList（用于最新的追问）
                questList.value = response.data.data
            }
        } catch (error) {
            console.error('追问请求失败:', error)
        }
    } else {
        console.log('追问功能未启用')
    }
}

// 暴露方法给父组件
defineExpose({
    setList: (newList) => {
        list.value = newList
    },
    scrollToBottom: () => {
        bubbleListRef.value?.scrollToBottom()
    },
    // 暴露流状态和终止方法
    isStreaming: isStreaming,
    terminateFn: terminateFn
})

onMounted(() => {
    console.log('内部', props.assistantId)
    userId.value = JSON.parse(localStorage.getItem('userInfo')).id
})
</script>

<style scoped lang="scss">
.page-title {
    text-align: center;
    margin-bottom: 32px;
    margin-top: 20%;
}

.assistant-title {
    font-weight: 600;
    margin-bottom: 4px;
    background: linear-gradient(270deg, #5C078F 0%, #AF50C5 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    color: transparent;
    font-size: 37px;
}

.subtitle {
    color: #676C90;
    font-size: 16px;
}

:deep(.el-upload-list) {
    display: none !important;
}

:deep(.el-sender) {
    background-color: #fff;
    border-radius: 8px;
    border: none !important;
    box-shadow: none;
}

:deep(.el-bubble-list) {
    .el-bubble-content {
        background-color: #fff !important;
        max-width: 100%;
    }
}

:deep(.el-bubble-end) {
    .el-bubble-avatar-placeholder {
        display: none;
    }
}

:deep(.markdown-body p) {
    margin-bottom: 0;
}
.online-btn {
    display: flex; 
    align-items: center; 
    gap: 4px; 
    background: #EEF1FE; 
    padding: 3px 10px; 
    border-radius: 4px; 
    color: #666666;
    margin-right: 10px;
    font-size: 14px;
    cursor: pointer;  
}
:deep(.el-sender-updown-wrap) {
    align-items: end;
}
:deep(.el-bubble-footer) {
    margin-top: 6px !important;
}

:deep(.el-bubble-header) {
    width: 100% !important;
    .content {
        pre{
           max-width: 100% !important;
        }
    }
    .el-thinking {
        .trigger {
            height: 35px;
        }
    }
}

.thinking-chain-warp {
    margin-bottom: 12px;
}

.ai-dialogue {
    flex: 1;
    padding: 20px 0;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;

    /* 默认背景 */
    background: linear-gradient(#ECEFFD 0%, #F2F3F8 100%);
}

.ai-dialogue.has-bg {
    background: none !important;
}

.assistant-header-warp {
    display: flex;
    height: 40px;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    // background: rgba(0,0,0,.2);
    // color: #fff;
}

.assistant-header {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
}

.assistant-desc {
    font-size: 14px;
    border-radius: 8px 8px 8px 8px;
    border: 1px solid #D5D8DD;
    padding: 16px;
    margin: 10px 0;
    background: #EEF0FF;
}

.suggestions {
    display: flex;
    flex-direction: column;
    flex-wrap: wrap;
    gap: 12px;
    flex: 1;
    overflow-y: auto;

    .suggestion-item {
        display: inline-flex;
        gap: 12px;
        padding: 10px 16px;
        background: #f8f9fa;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        width: fit-content;

        &:hover {
            background: #f5f5f5;
            transform: translateY(-2px);
        }

        .suggestion-content {
            .suggestion-title {
                font-size: 14px;
                color: #303133;
                display: flex;
            }
        }
    }
}

.quick-command-item {
    cursor: pointer;
    background: #F2F6FF;
    border-radius: 20px;
    padding: 5px 15px;
    border: 1px solid #E5E5E5;
    margin-right: 10px;
    &:hover {
        background: #fff;
        border-color: #5089FC;
    }
}

:deep(.el-bubble-header) {
    width: 100% !important;
    .content {
        pre{
           max-width: 100% !important;
        }
    }
    .el-thinking {
        .trigger {
            height: 35px;
            user-select: none;
            -webkit-user-drag: none;

            &:hover {
                cursor: pointer;
            }
        }
    }
}

.footer-item {
    padding: 6px 10px;
    background: #fff;
    border-radius: 8px;
    margin-bottom: 6px;
    transition: all 0.3s ease;
    display: table;
    &:hover {
        background: #f5f5f5;
        cursor: pointer;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
    }
}

.user-content {
    color: #303133;
    line-height: 1.6;
    word-wrap: break-word;
}

.user-file-content {
    .file-list-container {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;
    }
    
    .file-item {
        display: flex;
        align-items: center;
        gap: 8px;
    }
    
    .image-file {
        position: relative;
        border-radius: 8px;
        overflow: hidden;
        background: #f5f5f5;
        border: 1px solid #e5e5e5;
        
        .file-image {
            width: 120px;
            height: 90px;
            object-fit: cover;
            display: block;
        }
        
        .file-overlay {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
            color: white;
            padding: 8px;
            display: flex;
            justify-content: flex-start;
            align-items: center;
            
            .file-name {
                font-size: 12px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                max-width: 100px;
            }
        }
    }
}

.active-tab {
    color: #6b05a8 !important;
}

.out-view-item-type {
    font-size: 12px;
    margin-left: 10px;
    display: inline-block;
    background-color: #eee;
    border-radius: 6px;
    padding: 3px 6px;
    color: #666;
}

.header-self-wrap {
    display: flex;
    padding: 16px;
    max-height: 200px;
    overflow-y: auto;

    .file-list-container {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;
    }

    .file-item {
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .image-file {
        position: relative;
        border-radius: 8px;
        overflow: hidden;
        background: #f5f5f5;
        border: 1px solid #e5e5e5;

        .file-image {
            width: 80px;
            height: 60px;
            object-fit: cover;
            display: block;
        }

        .file-overlay {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
            color: white;
            padding: 8px;
            display: flex;
            justify-content: flex-start;
            align-items: center;

            .file-name {
                font-size: 12px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                max-width: 70px;
            }
        }

        .remove-btn {
            position: absolute;
            top: 4px;
            right: 4px;
            opacity: 0;
            transition: opacity 0.2s ease;
            background: rgba(0, 0, 0, 0.6) !important;
            border: none !important;

            &:hover {
                background: rgba(255, 0, 0, 0.8) !important;
            }
        }

        &:hover {
            .file-overlay {
                background: linear-gradient(transparent, rgba(0, 0, 0, 0.9));
            }

            .remove-btn {
                opacity: 1;
            }
        }
    }

    .document-file {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 12px;
        background: #f8f9fa;
        border: 1px solid #e5e5e5;
        border-radius: 8px;
        transition: all 0.2s ease;
        min-width: 200px;

        &:hover {
            background: #e9ecef;
            border-color: #dee2e6;

            .remove-btn {
                opacity: 1;
            }
        }

        .file-icon {
            font-size: 24px;
            color: #6c757d;
            flex-shrink: 0;
        }

        .file-info {
            flex: 1;
            min-width: 0;

            .file-name {
                display: block;
                font-size: 14px;
                font-weight: 500;
                color: #333;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                margin-bottom: 2px;
            }

            .file-type {
                font-size: 12px;
                color: #6c757d;
                background: #e9ecef;
                padding: 2px 6px;
                border-radius: 4px;
            }
        }

        .remove-btn {
            flex-shrink: 0;
            opacity: 0;
            transition: opacity 0.2s ease;

            &:hover {
                opacity: 1 !important;
                background: rgba(255, 0, 0, 0.1) !important;
            }
        }
    }

    .empty-state {
        text-align: center;
        color: #999;
        font-size: 14px;
        padding: 20px;
        font-style: italic;
    }
}

// 追问功能样式
.follow-up-questions {
    margin-top: 12px;
    
    .follow-up-item {
        padding: 6px 10px;
        background: #fff;
        border-radius: 8px;
        margin-bottom: 6px;
        transition: all 0.3s ease;
        display: table;
        cursor: pointer;
        
        &:hover {
            background: #f5f5f5;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
        }
    }
}
</style>