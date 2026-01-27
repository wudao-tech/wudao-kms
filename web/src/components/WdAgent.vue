<template>
    <div class="agent-container">
        <div :class="['assistant-chat', { 'ai-dialogue': mode === 'aidialogue', 'has-quote-panel': mode === 'aidialogue' && showQuoteResults }]" :style="bgStyle">
        <div :class="['assistant-header-warp', { 'd-none': mode === 'aidialogue' && !assistant?.bgImg }]">
            <!-- <span style="font-size: 16px; font-weight: 700;">{{ getTitleText() }}</span> -->
            <span style="font-size: 16px; font-weight: 700;"></span>
            <div style="display: flex; align-items: center; gap: 5px; cursor: pointer; color: #333;">
                
                <el-popover v-if="assistant.type === 'workflow'" placement="bottom-end" :width="400" trigger="click">
                    <template #reference>
                        <el-button link size="small" style="color: #333" @click="handleInputParams">
                            <el-icon><Operation /></el-icon>入参
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
                <el-button 
                    v-if="hasMemoryData" 
                    link  
                    @click="handleMemory"
                >
                    <svg-icon icon-class="jiyi1" style="font-size: 14px; "></svg-icon>
                    <span style="font-size: 14px;color: #333">记忆</span>
                </el-button>
            </div>
        </div>
        <div v-if="list.length > 0" :style="{ flex: 1, padding: mode === 'aidialogue' ? '20px' : '10px 20px', overflow: mode === 'aidialogue' ? 'auto' : 'hidden' }">
           <bubble-list :list="list" max-height="100%">
                <template #header="{ item }">
                    <Thinking 
                        v-if="item.reasoning_content" 
                        v-model="item.thinlCollapse" 
                        :content="item.reasoning_content"
                        :status="item.thinkingStatus" 
                        class="thinking-chain-warp"
                        @click.prevent
                    />
                </template>
                <template #content="{ item }">
                    <div v-if="item.content && item.role === 'system'">
                        <XMarkdown :markdown="item.content" class="markdown-body" :html="true" :linkify="true" :themes="{ light: 'github-light', dark: 'github-dark' }" default-theme-mode="dark" />
                        <!-- 引用资料单独渲染 -->
                        <div v-if="item.quoteList && item.quoteList.length > 0" class="quote-reference" @click="showQuoteList(item.quoteList)">
                            文章引用：
                            <span v-for="(ext, index) in getFileTypeExtensions(item.quoteList)" :key="index" class="file-icon-wrapper">
                                <img :src="getFilePath(`file.${ext}`)" :style="{ marginLeft: index > 0 ? '-8px' : '4px', zIndex: 10 - index }" class="file-type-icon" :alt="ext">
                            </span>
                            {{ item.quoteList.length }} 篇文章
                        </div>
                        <!-- 安防特有功能 -->
                        <div v-if="app === 'security' && item.showAndonButton > 0 && !item.showAndonComponent && !item.andonCreated && !item.showPdfComponent">
                           <el-button v-if="item.showAndonButton === 1" type="primary" size="small" @click="handleShowAndonComponent(item)">Andon事件</el-button>
                           <el-button v-if="item.showAndonButton === 2" type="primary" size="small" @click="handleVideoMonitor(item)">视频监控</el-button>
                           <el-button v-if="item.showAndonButton === 3" type="primary" size="small" @click="handlePdfReport(item)">pdf报告</el-button>
                        </div>
                        <!-- 显示图片记录（使用 recordObj） -->
                        <div v-if="app === 'security' && item.role === 'system' && item.recordObj">
                            <slot name="image-list" :recordObj="item.recordObj">
                                <component v-if="imageListComponent" :is="imageListComponent" :recordObj="item.recordObj" />
                            </slot>
                        </div>
                    </div>
                    <!-- <Typewriter v-if="item.content && item.role === 'system'" is-fog typing :content="item.content" :is-markdown="true" /> -->
                    <!-- user 图片消息 -->
                    <div v-if="item.files && item.files.length > 0 && item.role === 'user'" class="user-file-content">
                        <div class="file-list-container">
                            <div v-for="(file, index) in item.files" :key="index" class="file-item">
                                <!-- 图片文件 -->
                                <div v-if="isImageFile(file.name)" class="image-file">
                                    <img :src="file.url" :alt="file.name" class="file-image" />
                                    <div class="file-overlay">
                                        <span class="file-name">{{ file.name }}</span>
                                    </div>
                                </div>
                                <!-- 其他文件 -->
                                <div v-else class="document-file">
                                    <div class="file-icon">
                                        <img :src="getFilePath(file.name)" style="width: 24px; height: 24px;" alt="">
                                    </div>
                                    <div class="file-info">
                                        <span class="file-name">{{ file.name }}</span>
                                        <span class="file-type">{{ getFileExtension(file.name) }}</span>
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
                        <!-- AIDialogue模式的操作按钮 -->
                        <div v-if="mode === 'aidialogue' && chatUrl && app !== 'security'" class="footer-container">
                            <el-tooltip content="点赞" placement="top" v-if="item.role === 'system'">
                                <el-button link @click="handleLike(item, 'agree')">
                                    <!-- 已点赞 -->
                                    <el-icon v-if="item.type == 'agree'"><i class="iconfont se-dig-icon-dianzan_kuai1" style="color: #81858C"></i></el-icon>
                                    <!-- 未点赞 -->
                                    <el-icon v-else><i class="iconfont se-dig-icon-dianzan" style="color: #81858C;"></i></el-icon>
                                </el-button>
                            </el-tooltip>
                            <el-tooltip content="点踩" placement="top" v-if="item.role === 'system'">
                                <el-button link @click="handleLike(item, 'disagree')">
                                    <!-- 已点踩 -->
                                    <el-icon v-if="item.type === 'disagree'"><i class="iconfont se-dig-icon-diancai_kuai-copy" style="color: #81858C;"></i></el-icon>
                                    <!-- 未点踩 -->
                                    <el-icon v-else><i class="iconfont se-dig-icon-diancai-copy" style="color: #81858C;"></i></el-icon>
                                </el-button>
                            </el-tooltip>
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
                        </div>
                        <!-- 安防功能 -->
                        <div v-if="mode === 'aidialogue' && chatUrl && app === 'security' && item.role === 'system'">
                            <!-- Andon 组件 -->
                            <div v-if="item.showAndonComponent && !item.andonCreated" class="andon-component-wrapper" style="margin-top: 10px;">
                                <slot name="andon-component" :onSuccess="() => handleAndonSuccess(item)">
                                    <component v-if="andonComponent" :is="andonComponent" :onSuccess="() => handleAndonSuccess(item)" />
                                </slot>
                            </div>
                            <!-- 创建成功提示 -->
                            <div v-if="item.andonCreated" class="andon-success-wrapper" style="margin-top: 10px; padding: 10px; background: #f0f9ff; border: 1px solid #bae6fd; border-radius: 4px; color: #333; display: flex; align-items: center;">
                                <el-icon style="margin-right: 5px; color: #10b981;"><CircleCheck /></el-icon>
                                <span>Andon事件创建成功</span>
                            </div>
                            <!-- 视频组件 -->
                            <div v-if="item.showMonitorComponent && item.monitorDeviceIds && item.monitorDeviceIds.length > 0" class="monitor-component-wrapper" style="margin-top: 10px;">
                                <slot name="monitor-component" :list="item.monitorDeviceIds">
                                    <component v-if="monitorComponent" :is="monitorComponent" :list="item.monitorDeviceIds" />
                                </slot>
                            </div>
                            <!-- pdf报告 -->
                            <div v-if="item.showPdfComponent" class="pdf-component-wrapper" style="margin-top: 10px;">
                                <slot name="pdf-preview" :content="item.content">
                                    <component v-if="pdfPreviewComponent" :is="pdfPreviewComponent" :content="item.content" />
                                </slot>
                            </div>
                        </div>
                        <!-- 追问功能 -->
                        <div v-if="item.role === 'system' && item.followUpQuestions && item.followUpQuestions.length > 0" :class="{ 'follow-up-questions': mode === 'aidialogue' }">
                           <div v-for="( i, index ) in item.followUpQuestions" :key="index" :class="mode === 'aidialogue' ? 'follow-up-item' : 'footer-item'" @click="handleQuickCommand(i)">
                            {{ i }}
                           </div>
                        </div>
                    </div>
                </template>
           </bubble-list>
        </div>
        <div v-else :style="{ flex: 1, padding: mode === 'aidialogue' ? '20px' : '10px 20px' }">
            <div class="assistant-header" style="margin-top: 40px;">
                <img :src="assistant.logo" style="width: 80px; height: 80px; border-radius: 8px; object-fit: cover;"  alt="">
                <span style="font-size: 20px; font-weight: 700; line-height: 36px;">{{ assistant.name }}</span>

            </div>
            <div class="assistant-desc" v-if="assistant.guideWord">{{ assistant.guideWord }}</div>
            <div class="suggestions">
                <div class="suggestion-item" v-for="suggestion in assistant.guideQuestions" :key="suggestion" @click="handleSuggestion(suggestion)">
                    <div class="suggestion-content">
                        <div class="suggestion-title"><img src="@/assets/images/guide_icon.svg" alt="" style="width: 20px; height: 20px; margin-right: 10px;">{{ suggestion}}</div>
                    </div>
                </div>
            </div>
        </div>
        <div :style="{ padding: mode === 'aidialogue' ? '20px 0 0 0' : '0 20px 10px 20px' }">
          <div :style="{ display: 'flex', marginBottom: '5px' }" v-if="assistant?.quickCommands?.length > 0 || mode === 'assistantchat'">
            <div v-for="(item, index ) in assistant.quickCommands" :key="index" class="quick-command-item" @click="handleQuickCommand(item.desc)">
                <svg-icon :icon-class="item.icon" style="font-size: 14px; margin-right: 5px; margin-top: 2px; color: #333;" />
                <span style="font-size: 14px;font-weight: 700; height: 22px; color: #838383;">{{ item.title }}</span>
            </div>
            <!-- <el-tag v-for="i in fileList" :key="i.url" closable @close="handleClose(i)" style="margin-right: 10px; background-color: #F9F9F9;">{{ i.name }}</el-tag> -->
          </div>
            <Sender ref="senderRef" v-model="senderValue" variant="updown" :auto-size="mode === 'aidialogue' ? { minRows: 2, maxRows: 4 } : { minRows: 2, maxRows: 2 }" clearable allow-speech placeholder="请输入问题" @submit="handleSubmit">
                <template #header>
                    <div class="header-self-wrap">
                        <div class="file-list-container">
                            <div v-for="(item, index) in fileList" :key="index" class="file-item">
                                <div v-if="isImageFile(item.name)" class="image-file">
                                    <img :src="item.url" :alt="item.name" class="file-image" />
                                    <div class="file-overlay">
                                        <span class="file-name">{{ item.name }}</span>
                                    </div>
                                    <el-button type="danger" size="small" circle @click="handleClose(item)" class="remove-btn">
                                        <el-icon><Close /></el-icon>
                                    </el-button>
                                </div>
                                <div v-else class="document-file">
                                    <div class="file-icon">
                                        <img :src="getFilePath(item.name)" style="width: 24px; height: 24px;" alt="">
                                    </div>
                                    <div class="file-info">
                                        <span class="file-name">{{ item.name }}</span>
                                        <span class="file-type">{{ getFileExtension(item.name) }}</span>
                                    </div>
                                    <el-button type="danger" size="small" circle @click="handleClose(item)" class="remove-btn">
                                        <el-icon><Close /></el-icon>
                                    </el-button>
                                </div>
                            </div>
                        </div>
                        <div v-if="fileList.length === 0" class="empty-state">
                            <div style="text-align: center; color: #999; font-size: 14px; padding: 20px;">
                                <div style="margin-bottom: 8px;">📎 支持多种文件格式</div>
                                <div style="font-size: 12px; color: #ccc;">支持图片、文档、PDF 等多种格式</div>
                            </div>
                        </div>
                    </div>
                </template>
                <template #prefix>
                    <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                        <div class="online-btn" :style="{ color: enableSearch ? '#333' : '#999' }" v-if="assistant.webSearch" @click="enableSearch = !enableSearch">
                            已联网
                            <span v-if="enableSearch" style="width: 5px; height: 5px; background: #3DCD58; border-radius: 50%;"></span>
                            <span v-else style="width: 5px; height: 5px; background: #999; border-radius: 50%;"></span>
                        </div>
                        <div class="online-btn"  :style="{ color: enableDeepChat ? '#333' : '#999' }" v-if="assistant.deepThinkingModel" @click="enableDeepChat = !enableDeepChat">
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
                            v-if="assistant.multimodal || assistant.type === 'workflow'"
                            action="/s3/upload"
                            :http-request="fileUploadFn"
                            accept="*/*"
                            :before-upload="beforeUpload"
                        >
                            <el-button link>
                                <el-icon style="cursor: pointer; font-size: 20px; color: #333;"><Paperclip /></el-icon>
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
            <div v-else style="min-height: 200px;">
                <el-tabs v-model="memoryType" type="card" @tab-click="handleTabClick">
                    <!-- 表名称 -->
                    <el-tab-pane v-for="(item, index) in assistant.memoryTable" :key="index" :label="item.tableName" :name="item.tableName"></el-tab-pane>
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
        
        <!-- 引用资料面板 -->
        <div class="content-right" :class="{ 'visible': showQuoteResults }">
            <div v-if="showQuoteResults" class="content-right-warp">
                <div style="display: flex;align-items: center;justify-content: space-between;">
                    <span style="border-left: 2px solid #6B05A8; padding-left: 10px;">引用资料</span>
                    <span style="cursor: pointer; display: flex;align-items: center;gap: 5px; color: #666666; font-size: 12px;" @click="showQuoteResults = false"><span>收起 </span><el-icon><ArrowRight /></el-icon></span>
                </div>
                <div style="flex: 1; overflow-y: auto; margin-top: 10px;">
                    <div v-for="result in searchResults" class="result-item" :key="result.filename" :style="{cursor: result.id ? 'pointer' : 'default'}" @click="goDetail(result.id)">
                        <div class="result-title">
                            <VMdPreview :text="result.highlight" class="result-markdown" />
                        </div>
                        <div style="color: #999; margin-top: 10px;" v-if="result.filename">
                            <span style="display: flex;align-items: center;gap: 5px;"><img :src="getFilePath(result.filename)" style="width: 16px;" alt=""> {{ result.filename }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
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
import { ref, watch, onMounted, onBeforeUnmount, computed, nextTick } from 'vue'
import { BubbleList, Sender, XMarkdown, Thinking, Typewriter } from 'vue-element-plus-x'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import Prism from 'prismjs'
import { ElMessage, ElMessageBox, ElImageViewer } from 'element-plus'
import robotAvatar from '@/assets/images/robot.png'
import { getToken } from '@/utils/auth'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { fileUpload } from '@/api/ai/aiStore'
import { recordQa } from '@/api/qa'
import { useRouter, useRoute } from 'vue-router'
import { nanoid } from '@/utils'
// AIDialogue模式需要的图片资源
import copy from '@/assets/images/copy.svg'
import redo from '@/assets/images/redo.svg'

// 配置markdown预览器
VMdPreview.use(vuepressTheme, {
    Prism,
})

// Router
const router = useRouter()
const route = useRoute()

// Props
const props = defineProps({
    assistant: {
        type: Object,
        default: () => ({})
    },
    // kms中的sse
    chatUrl: {
        type: String,
        default: ''
    },
    // AIDialogue模式专用props
    sessionUuid: {
        type: String,
        default: ''
    },
    assistantId: {
        type: String,
        default: ''
    },
    // 控制组件模式：'assistantchat' | 'aidialogue'
    mode: {
        type: String,
        default: 'assistantchat'
    },
    app: {
        type: String,
        default: ''
    },
    // 安防功能组件，可以通过 props 传入
    andonComponent: {
        type: [Object, Function],
        default: null
    },
    monitorComponent: {
        type: [Object, Function],
        default: null
    },
    // 图片列表组件
    imageListComponent: {
        type: [Object, Function],
        default: null
    },
    // PDF预览组件
    pdfPreviewComponent: {
        type: [Object, Function],
        default: null
    }
   
})

// Emits for AIDialogue mode
const emit = defineEmits(['update:list', 'new-chat'])
// Methods
const generateSessionUuid = () => {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}
const active = ref('variable')
const memoryType = ref('')
const senderRef = ref(null)

// 计算属性：动态获取当前记忆表的字段和数据
const currentTableInfo = computed(() => {
    if (memoryType.value && props.assistant.memoryTable) {
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

// 计算属性：判断是否有记忆数据
const hasMemoryData = computed(() => {
    const hasMemoryTable = props.assistant.memoryTable && props.assistant.memoryTable.length > 0
    const hasMemoryVars = props.assistant.memoryVars && props.assistant.memoryVars.length > 0
    return hasMemoryTable || hasMemoryVars
})

const enableDeepChat = ref(false) //是否启用深度思考
const enableSearch = ref(false) //是否启用联网
// Data
const senderValue = ref('')
const list = ref([])
const url = ref(import.meta.env.VITE_AIDOJO_CHAT_URL + '/agent/v2/api/chat')
const sessionUuid = ref(props.mode === 'aidialogue' && props.sessionUuid ? props.sessionUuid : generateSessionUuid())
const userId = ref('')
const localUuid = ref('')
const suggestions = ref([])
const fileList = ref([])
const isThinking = ref(false)
const memoryVisible = ref(false)
const variableList = ref([])

const messageUuid = ref('') // 当前消息的uuid用来记录点赞点踩

// 引用资料相关变量
const searchResults = ref([])
const showQuoteResults = ref(false)

// 图片预览相关
const showImageViewer = ref(false)
const currentImageUrl = ref('')

const questList = ref([])

const status = ref(null)

// 计算背景样式
const bgStyle = computed(() => {
    if (props.assistant.bgImg) {
        return {
            backgroundImage: `url(${props.assistant.bgImg})`,
            backgroundSize: 'cover',
            backgroundPosition: props.mode === 'aidialogue' ? 'bottom' : 'center'
        }
    }
    return {}
})
const abortController = ref(null) // 用于控制SSE连接
const isStreaming = ref(false) // 用于控制按钮显示状态

const handleSuggestion = (suggestion) => {
    senderValue.value = suggestion
    handleSubmit()
}

// 上传前验证文件类型
const beforeUpload = (file) => {
    // const isImage = file.type.startsWith('image/')
    // if (!isImage) {
    //     ElMessage.error('只能上传图片格式的文件！')
    //     return false
    // }
    // 检查文件大小（可选，比如限制为10MB）
    const isLt10M = file.size / 1024 / 1024 < 10
    if (!isLt10M) {
        ElMessage.error('图片大小不能超过 10MB!')
        return false
    }
    
    return true
}

const fileUploadFn = async (req) => {
    const file = req.file
    const name = file.name
    try {
        const format = file.type.split('/').slice(-1)[0] || file.name.split('.').pop()
        const res = await fileUpload(file, 'file', format)
        
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
    } catch (error) {
        ElMessage.error('文件上传失败，请重试')
    }
}

const handleClose = (i) => {
    fileList.value = fileList.value.filter(item => item.url !== i.url)
    
    // 如果文件列表为空，自动关闭header
    if (fileList.value.length === 0) {
        closeHeader()
    }
}

const handleMemory = () => {
    memoryVisible.value = true
    // 如果有记忆表，默认选择第一个
    if (props.assistant.memoryTable && props.assistant.memoryTable.length > 0) {
        memoryType.value = props.assistant.memoryTable[0].tableName
        updateTableFields()
    }
    // getTableData() 现在通过 watch 监听器自动调用
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
    isThinking.value = false
    
    // 生成新的会话ID
    sessionUuid.value = generateSessionUuid()
    
    // 重置状态
    status.value = null
    
    ElMessage.success('聊天内容已清空')
}

const handleTabClick = (tab) => {
    memoryType.value = tab.name
    updateTableFields()
    getTableData()
}

const updateTableFields = () => {
    if (memoryType.value && props.assistant.memoryTable) {
        const currentTable = props.assistant.memoryTable.find(table => table.tableName === memoryType.value)  
    }
}

const handleQuickCommand = (i) => {
  senderValue.value = i
  handleSubmit()
}

const closeHeader = () => {
    if (senderRef.value) {
        senderRef.value.closeHeader()
    }
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

// 从引用列表中提取文件类型扩展名数组
const getFileTypeExtensions = (quoteList) => {
    if (!quoteList || !Array.isArray(quoteList)) return []
    const extensions = [...new Set(quoteList.map(quote => {
        const extension = quote.filename ? quote.filename.toLowerCase().split('.').pop() : 'othe'
        return extension
    }))]
    return extensions
}

const getFilePath = (fileName) => {
  if (!fileName) return new URL('/src/assets/fileIcon/othe.svg', import.meta.url).href;
  
  // 获取文件扩展名
  const extension = fileName.toLowerCase().split('.').pop();
  
  // 定义支持的文件类型
  const supportedExtensions = ['jpg', 'html', 'doc', 'ppt', 'mp4', 'txt', 'pdf', 'xls', 'png', 'md', 'csv', 'xlsx', 'xlsx', 'docx', 'jpeg'];
  
  // 特殊处理一些扩展名
  let iconName = extension;
  // 检查是否有对应的图标
  if (supportedExtensions.includes(iconName)) {
    return new URL(`/src/assets/fileIcon/${iconName}.svg`, import.meta.url).href;
  } else {
    return new URL('/src/assets/fileIcon/othe.svg', import.meta.url).href;
  }
}
// 解析 [type:Play:[id1,id2,id3]] 格式，提取设备ID数组
const parsePlayDeviceIds = (text) => {
    if (!text) return []
    // 匹配 [type:Play:[id1,id2,id3]] 格式
    const match = text.match(/\[\s*type\s*:\s*Play\s*:\s*\[([^\]]+)\]\s*\]/i)
    if (match && match[1]) {
        // 分割逗号，去除空格，过滤空值
        return match[1].split(',').map(id => id.trim()).filter(id => id)
    }
    return []
}

// 将源标记转换为小图标
const transformSourceMarkers = (text) => {
    if (!text) return text
    // 针对安防模式，移除所有特殊标记，但在 SSE 处理中会解析它们
    return text
        .replace(/\[\s*type\s*:\s*doc[^\]]*\]/gi, props.app === 'security' ? '' : `![source-marker-doc](${getFilePath('doc.svg')})`)
        .replace(/\[\s*type\s*:\s*qa[^\]]*\]/gi, props.app === 'security' ? '' : `![source-marker-qa](${getFilePath('qa.svg')})`)
        .replace(/\[\s*type\s*:\s*Andon_Create\s*\]/gi, '') // 移除 Andon_Create 标记
        .replace(/\[\s*type\s*:\s*Play\s*:[^\]]*\]/gi, '') // 移除 Play 标记
        .replace(/\[\s*type\s*:\s*pdf\s*\]/gi, '') // 移除 PDF 标记
}

// 从 content 中提取 type: record 的 JSON 对象
const extractRecordJson = (content) => {
    if (!content) return { recordObj: null, cleanedContent: content }
    
    let recordObj = null
    let cleanedContent = content
    
    try {
        let searchContent = content
        try {
            if ((content.startsWith('"') && content.endsWith('"')) || content.startsWith('{')) {
                const parsed = JSON.parse(content)
                if (typeof parsed === 'string') {
                    searchContent = parsed
                }
            }
        } catch (e) {}
        
        let recordIndex = searchContent.indexOf('"type":"record"')
        if (recordIndex === -1) {
            recordIndex = searchContent.indexOf('\\"type\\":\\"record\\"')
        }
        if (recordIndex !== -1) {
            let jsonStartIndex = searchContent.lastIndexOf('{', recordIndex)
            if (jsonStartIndex === -1) {
                const escapedBraceIndex = searchContent.lastIndexOf('\\{', recordIndex)
                if (escapedBraceIndex !== -1) {
                    jsonStartIndex = escapedBraceIndex + 1
                }
            }
            
            if (jsonStartIndex === -1) {
                jsonStartIndex = Math.max(0, recordIndex - 10)
            }
            
            let braceCount = 0
            let jsonEndIndex = -1
            
            for (let i = jsonStartIndex; i < searchContent.length; i++) {
                const char = searchContent[i]
                const prevChar = i > 0 ? searchContent[i - 1] : ''
                
                if (char === '\\' && prevChar !== '\\') continue
                
                if (char === '{' && prevChar !== '\\') {
                    braceCount++
                } else if (char === '}' && prevChar !== '\\') {
                    braceCount--
                    if (braceCount === 0) {
                        jsonEndIndex = i + 1
                        break
                    }
                }
            }
            
            if (jsonEndIndex !== -1) {
                let jsonStr = searchContent.substring(jsonStartIndex, jsonEndIndex)
                if (jsonStr.includes('\\"')) {
                    jsonStr = jsonStr.replace(/\\(.)/g, '$1')
                }
                
                try {
                    const recordData = JSON.parse(jsonStr)
                    if (recordData.type === 'record') {
                        recordObj = {
                            startTime: recordData.startTime || '',
                            endTime: recordData.endTime || '',
                            imgDescr: recordData.imgDescr || ''
                        }                     
                        let originalStartIndex = -1
                        const escapedJsonStart = content.indexOf('{\\"type\\":\\"record\\"')
                        if (escapedJsonStart !== -1) {
                            originalStartIndex = escapedJsonStart
                        } else {
                            const normalJsonStart = content.indexOf('{"type":"record"')
                            if (normalJsonStart !== -1) {
                                originalStartIndex = normalJsonStart
                            }
                        }
                        
                        if (originalStartIndex !== -1) {
                            cleanedContent = content.substring(0, originalStartIndex).trim()
                        }
                    }
                } catch (parseError) {}
            }
        }
    } catch (error) {}
    
    return { recordObj, cleanedContent }
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
const handleSubmit = async () => {
    if (!senderValue.value.trim()) return
    
    // AIDialogue模式的新会话创建逻辑
    if (props.mode === 'aidialogue') {
        // 如果没有 sessionUuid，先创建新对话
        if (!props.sessionUuid) {
            // 触发新对话创建，并等待完成
            await new Promise((resolve) => {
                emit('new-chat', resolve)
            })
            // 创建完成后，继续执行对话流程
        }
    }

    // 检查是否已选择模型
    if (!props.assistant.modelName) {
        ElMessageBox.alert('请先选择模型再进行对话', '提示', {
            confirmButtonText: '确定',
            type: 'warning'
        })
        return
    }

    // 设置流状态
    isStreaming.value = true
    
    // 重置思考状态
    isThinking.value = false
    
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
    messageUuid.value = nanoid(10)

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
        followUpQuestions: [], // 追问内容
        chatUuid: messageUuid.value,
        type: null, // 点赞点踩状态
        quoteList: [], // 引用资料列表
        showAndonButton: 0, // 按钮类型：0=不显示，1=Andon事件，2=视频监控
        showAndonComponent: false, // 是否显示 Andon 组件
        andonCreated: false, // 是否已创建成功
        monitorDeviceIds: [], // 视频监控设备ID列表
        showMonitorComponent: false, // 是否显示视频监控组件
        showPdfComponent: false, // 是否显示 PDF 组件
        recordObj: null // 图片记录对象（用于 imgCard 组件）
    })

    try {
        // message 只传递文本内容，文件通过 file 字段单独传递
        const messageContent = userMessage.trim() || ''
        console.log('messageContent', messageContent)
        // 在这里判断是工作流（streamResponseFollw）还是智能配置（streamResponse）， 区分两个
        if (props.assistant.type === 'workflow') {
            await streamResponseFollw(messageContent, aiMessageIndex)
        } else {
            await streamResponse(messageContent, aiMessageIndex)
        }
    } catch (error) {
        // 更新AI消息为错误状态
        list.value[aiMessageIndex] = {
            ...list.value[aiMessageIndex],
            content: '抱歉，请求失败，请稍后重试',
            loading: false,
            typing: false
        }
    }
}

const getTableData = async () => {
    try {
        let params = {
            agentUuid: props.assistant.uuid,
            sessionUuid: sessionUuid.value,
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
                // 更新记忆变量数
                const variableData = response.data.data.variableData
                
                if (!variableData || Object.keys(variableData).length === 0) {
                    // 如果是空对象，使用 assistant.memoryVars
                    variableList.value = props.assistant.memoryVars || []
                } else {
                    // 如果有值，将对象转换为数组并匹配 key
                    const updatedVariables = props.assistant.memoryVars.map(item => {
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
                            const memoryTableConfig = props.assistant.memoryTable.find(table => table.tableName === tableName)
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
        }
    } catch (error) {
        console.error('getTableData 请求失败:', error)
    }
}

const streamResponse = async (userMessage, messageIndex) => {
    let requestBody = {}
    if (props.chatUrl && props.app !== 'security') {
        // kms
        requestBody = {
            query: userMessage, // 查询内容 - 放在后面确保不被覆盖
            agentUuid: props.assistant.uuid,
            sessionUuid: props.mode === 'aidialogue' ? (props.sessionUuid || sessionUuid.value) : sessionUuid.value, // 绑定的对话uuid
            userId: userId.value, // 用户id
            deepThinking: enableDeepChat.value, // 深度思考
            webSearch: enableSearch.value, // 联网
            chatUuid: messageUuid.value // 当前回答消息的id
        }
    } else {
        // ai上 或者安防 security
        requestBody = {
            message: userMessage,
            sessionUuid: props.mode === 'aidialogue' ? (props.sessionUuid || sessionUuid.value) : sessionUuid.value,
            userId: userId.value,
            assistantUuid: props.mode === 'aidialogue' ? props.assistantId : props.assistant.uuid,
            enableDeepChat: enableDeepChat.value,
            enableSearch: enableSearch.value
        }
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
    console.log('requestBody', requestBody)
    try {
        await fetchEventSource( props.chatUrl || import.meta.env.VITE_AIDOJO_CHAT_URL + '/agent/v2/api/chat', {
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
                    
                    if (data === '[DONE]') {
                        // 流结束，确保移除所有标记
                        if (list.value[messageIndex].content) {
                            // 在移除标记前，再次检查并解析设备ID（处理分片到达的情况）
                            const fullContent = list.value[messageIndex].content
                            if (fullContent.includes('[type:Play')) {
                                const deviceIds = parsePlayDeviceIds(fullContent)
                                if (deviceIds.length > 0) {
                                    list.value[messageIndex].monitorDeviceIds = deviceIds
                                }
                            }
                            
                            // 提取 type: record 的 JSON
                            const { recordObj, cleanedContent: recordCleanedContent } = extractRecordJson(fullContent)
                            if (recordObj) {
                                list.value[messageIndex].recordObj = recordObj
                            }
                            
                            // 使用提取后的清理内容
                            const finalCleanedContent = recordCleanedContent || fullContent
                            
                            // 在移除标记前，再次检查所有按钮类型（处理分片到达的情况）
                            if (finalCleanedContent.includes('[type:Andon_Create]')) {
                                list.value[messageIndex].showAndonButton = 1 // Andon事件
                            } else if (finalCleanedContent.includes('[type:Play')) {
                                list.value[messageIndex].showAndonButton = 2 // 视频监控
                            } else if (finalCleanedContent.includes('[type:Monitor') || finalCleanedContent.includes('[type:Video')) {
                                list.value[messageIndex].showAndonButton = 2 // 视频监控（兼容旧格式）
                            } else if (/\[\s*type\s*:\s*pdf\s*\]/i.test(finalCleanedContent)) {
                                list.value[messageIndex].showAndonButton = 3 // PDF报告
                            }
                            
                            list.value[messageIndex].content = transformSourceMarkers(finalCleanedContent)
                        }

                        // 流结束
                        isStreaming.value = false
                        list.value[messageIndex] = {
                            ...list.value[messageIndex],
                            loading: false,
                            typing: false
                        }
                        
                        // 收到[DONE]时调用追问功能
                        if (props.assistant.followup?.type === 'div') {
                            followUpQuestionFn(messageIndex)
                        }
                        
                        // 清空文件列表并关闭header
                        fileList.value = []
                        closeHeader()
                        
                        // AIDialogue模式需要触发事件
                        // if (props.mode === 'aidialogue') {
                        //     emit('update:list', list.value)
                        // }
                        return
                    }

                    // 尝试解析JSON数据
                    try {
                        const parsedData = JSON.parse(data)

                        // 处理错误响应
                        if (parsedData.outputType === 'error') {
                            isStreaming.value = false
                            list.value[messageIndex] = {
                                ...list.value[messageIndex],
                                content: parsedData.content || '请求出错，请稍后重试',
                                loading: false,
                                typing: false
                            }
                            // 清空文件列表并关闭header
                            fileList.value = []
                            closeHeader()
                            return
                        }

                        if (parsedData.outputType === 'reasoning') {
                            // 检查 content 是否为空
                            const content = parsedData.content || ''
                            if (!content && content !== 0) {
                                return
                            }
                            
                            // 开始思考链状态
                            if (!isThinking.value) {
                                isThinking.value = true
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
                            
                            // 如果 content 为空字符串，直接返回不处理
                            if (content === '') {
                                return
                            }
                            
                            // 结束思考链状态
                            isThinking.value = false
                            list.value[messageIndex].thinkingStatus = 'end'
                            list.value[messageIndex].loading = false
                            
                            // 文本内容更新到主要内容中
                            // AIDialogue模式需要清理HTML标签前的多余空格
                            // 实时输出时需要移除 [type:doc,chunkId:xxx] 标记
                            // 先累积内容，然后对整个累积内容进行标记移除，以处理分片到达的情况
                            if (props.mode === 'aidialogue') {
                                const cleanedContent = content.replace(/(\s+)(<[^>]+>)/g, '\n\n$2')
                                list.value[messageIndex].content += cleanedContent
                            } else {
                                list.value[messageIndex].content += content
                            }

                            // 检测是否包含标记，设置按钮类型
                            const fullContent = list.value[messageIndex].content
                            
                            // 提取 type: record 的 JSON
                            const { recordObj, cleanedContent: recordCleanedContent } = extractRecordJson(fullContent)
                            if (recordObj) {
                                list.value[messageIndex].recordObj = recordObj
                            }
                            
                            // 使用提取后的清理内容
                            const finalCleanedContent = recordCleanedContent || fullContent
                            
                            if (finalCleanedContent.includes('[type:Andon_Create]')) {
                                list.value[messageIndex].showAndonButton = 1 // Andon事件
                            } else if (finalCleanedContent.includes('[type:Play')) {
                                list.value[messageIndex].showAndonButton = 2 // 视频监控
                                // 解析设备ID列表
                                const deviceIds = parsePlayDeviceIds(finalCleanedContent)
                                if (deviceIds.length > 0) {
                                    list.value[messageIndex].monitorDeviceIds = deviceIds
                                }
                            } else if (finalCleanedContent.includes('[type:Monitor') || finalCleanedContent.includes('[type:Video')) {
                                list.value[messageIndex].showAndonButton = 2 // 视频监控（兼容旧格式）
                            } else if (/\[\s*type\s*:\s*pdf\s*\]/i.test(finalCleanedContent)) {
                                list.value[messageIndex].showAndonButton = 3 // PDF报告
                            }

                            // 对整个累积内容进行标记移除处理
                            list.value[messageIndex].content = transformSourceMarkers(finalCleanedContent)
                            list.value[messageIndex].typing = true
                            list.value[messageIndex].isFog = true

                           
                        } else if (parsedData.quoteList && props.mode === 'aidialogue') {
                            // 收集引用资料，存储到消息对象中
                            console.log('收到引用资料:', parsedData)
                            list.value[messageIndex].quoteList = parsedData.quoteList
                        }
                        
                    } catch (e) {
                        // 如果不是JSON，当作普通文本处理
                        // 检查原始数据是否为空
                        if (!data && data !== 0) {
                            return
                        }
                        
                        // 实时输出时需要移除标记
                        const appended = transformSourceMarkers(list.value[messageIndex].content + data)
                        list.value[messageIndex] = {
                            ...list.value[messageIndex],
                            content: appended,
                            loading: false,
                            typing: true,
                            isFog: true
                        }
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
                
                // 清空文件列表并关闭header
                fileList.value = []
                closeHeader()
                
                // AIDialogue模式需要触发事件
                // if (props.mode === 'aidialogue') {
                //     emit('update:list', list.value)
                // }
                
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

const handleInputParams = () => {
    console.log('handleInputParams')
}

// 获取标题文本
const getTitleText = () => {
    // 如果路由参数中有 status，显示 "预览与调试"
    if (route.query.status) {
        return `预览与调试`
    }
    // 否则只显示 assistant.name
    return props.assistant.name
}

const streamResponseFollw = async (userMessage, messageIndex) => {
    // 将graphConfig数组转换为键值对格式
    const graphConfigParams = {}
    if (props.assistant.graphConfig && Array.isArray(props.assistant.graphConfig)) {
        props.assistant.graphConfig.forEach(item => {
            if (item.paramName && item.value !== undefined) {
                graphConfigParams[item.paramName] = item.value
            }
        })
    }
    
    const requestBody = {
        sessionUuid: props.mode === 'aidialogue' ? (props.sessionUuid || sessionUuid.value) : sessionUuid.value,
        userId: userId.value,
        assistantUuid: props.mode === 'aidialogue' ? props.assistantId : props.assistant.uuid,
        enableDeepChat: enableDeepChat.value,
        enableSearch: enableSearch.value,
        threadId: props.mode === 'aidialogue' ? (props.sessionUuid || sessionUuid.value) : sessionUuid.value,
        initialParams: {
          ...graphConfigParams, // 展开转换后的参数
          conversation_id: props.mode === 'aidialogue' ? (props.sessionUuid || sessionUuid.value) : sessionUuid.value,
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
                if (props.assistant.followup?.type === 'div') {
                    followUpQuestionFn(messageIndex)
                }
                
                // 清空文件列表并关闭header
                fileList.value = []
                closeHeader()
                
                // AIDialogue模式需要触发事件
                if (props.mode === 'aidialogue') {
                    emit('update:list', list.value)
                }
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
            list.value[messageIndex] = {
              ...list.value[messageIndex],
              loading: false,
              typing: false
            }
            
            // SSE连接关闭时调用追问功能
            if (props.assistant.followup?.type === 'div') {
                followUpQuestionFn(messageIndex)
            }
            
            // 清空文件列表并关闭header
            fileList.value = []
            closeHeader()
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

// 追问
const followUpQuestionFn = async (messageIndex) => {
    if (props.assistant.followup?.type === 'div') {
        try {
            const response = await axios.get(import.meta.env.VITE_AIDOJO_CHAT_URL + `/agent/v2/api/followup/${props.assistant.uuid}/${sessionUuid.value}`)
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
    }
}


// AIDialogue模式专用的方法
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

// 点赞点踩
const handleLike = (item, val) => {
    console.log(item)
    const currentIndex = list.value.findIndex(msg => msg.key === item.key)
    if (currentIndex > 0) {
        // 找到前一条用户消息
        const userMessage = list.value[currentIndex - 1]
        if (userMessage && userMessage.role === 'user') {
            let params = {
                type: val,
                userMessage: userMessage.content,
                agent: item.content,
                chatUuid: item.chatUuid,
                agentUuid: props.assistant.uuid
            }
            console.log('param', params)
            recordQa(params).then(res => {
                // 使用 Vue 的响应式更新方式
                list.value[currentIndex].type = val
            })
        }
    }
}

// 引用资料相关方法
const showQuoteList = (quoteList) => {
    // 将引用资料显示在右侧面板
    searchResults.value = quoteList.map(item => ({
        highlight: item.highlight || item.content,
        filename: item.filename || '',
        id: item.document_id || ''
    }))
    // 显示右侧面板
    showQuoteResults.value = true
}


// 跳转到详情页
const goDetail = (id) => {
    let currentState = {
        id: id,
        fromSearch: true,
        sessionUuid: sessionUuid.value,
    }
    
    router.push({
        path: '/space/retrieve/detail',
        query: currentState
    })
}

// 显示 Andon 组件
const handleShowAndonComponent = (item) => {
    item.showAndonComponent = true
}

// Andon 创建成功回调
const handleAndonSuccess = (item) => {
    item.showAndonComponent = false
    item.andonCreated = true
}

// 视频监控处理
const handleVideoMonitor = (item) => {
    item.showMonitorComponent = true
}

// pdf报告
const handlePdfReport = (item) => {
    item.showPdfComponent = true
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
                ElMessage.success('复制成功')
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
                    ElMessage.success('复制成功')
                  
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

// 添加图片点击事件
const addImageClickEvents = () => {
    nextTick(() => {
        // 查找所有 markdown 内容中的图片
        const markdownImages = document.querySelectorAll('.markdown-body img, .el-bubble-content img, .elx-xmarkdown-provider img')
        
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

// Watch
watch(() => props.assistant, (newVal) => {
    console.log('newVal', newVal)
    suggestions.value = newVal.guideQuestions
    enableDeepChat.value = newVal.deepThinkingModel || false
    enableSearch.value = newVal.webSearch || false
    // 如果当前在记忆表模式且有记忆表数据，更新表格字段
    if (active.value === 'table' && newVal.memoryTable && newVal.memoryTable.length > 0) {
        if (!memoryType.value) {
            memoryType.value = newVal.memoryTable[0].tableName
        }
        updateTableFields()
    }
}, { immediate: true, deep: true })

// 监听 list 变化，为新内容添加图片点击事件并同步到父组件
watch(() => list.value, (newList) => {
    addImageClickEvents()
    emit('update:list', [...newList])
}, { deep: true })

// watch(() => props.modelData, (newVal) => {
// }, { immediate: true, deep: true })

// 监听记忆对话框的显示状态
watch(() => memoryVisible.value, (newVal) => {
    if (newVal) {
        getTableData()
    }
})

// 暴露方法给父组件 (AIDialogue模式需要)
defineExpose({
    setList: (newList) => {
        // 统一将来自详情页的系统消息内容做来源标记解析
        const parsed = Array.isArray(newList) ? newList.map(msg => {
            if (msg && msg.role === 'system' && typeof msg.content === 'string') {
                return {
                    ...msg,
                    content: transformSourceMarkers(msg.content)
                }
            }
            return msg
        }) : newList
        list.value = parsed
        console.log('newList', parsed)
        // 添加图片点击事件
        addImageClickEvents()
    },
    scrollToBottom: () => {
        // 这里可以添加滚动到底部的方法
    },
    // 暴露流状态和终止方法
    isStreaming: isStreaming,
    terminateFn: terminateFn
})

// 监听AIDialogue模式的props变化
watch(() =>[ props.sessionUuid, props.assistantId], ([newSessionUuid, newAssistantId]) => {
    // 可以在这里添加监听逻辑
}, { immediate: true })

// Lifecycle
onMounted(() => {
    console.log('onMounted', props.chatUrl, props.assistant)
    userId.value = JSON.parse(localStorage.getItem('userInfo')).id
})

// 组件卸载时清理
onBeforeUnmount(() => {
    // 组件卸载清理逻辑
})
</script>

<style lang="scss" scoped>
.assistant-chat {
   flex: 1;
   background: linear-gradient(#ECEFFD 0%, #F2F3F8 100%);
//    padding: 20px 20px 10px 20px;
   overflow: hidden;
   display: flex;
   flex-direction: column;
}

.assistant-header-warp {
    display: flex;
    height: 40px;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    // background: rgba(255, 255, 255, 0.2);
    // background: #fff;
    color: #fff;
}

:deep(.el-bubble-list) {
    .el-bubble-content {
        background-color: #fff !important;
        max-width: 100% !important;
        padding: 8px !important;
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

:deep(.el-bubble-end) {
    .el-bubble-avatar-placeholder {
        display: none;
    }
}

:deep(.el-sender) {
    background-color: #fff;
}

:deep(.el-sender:focus-within) {
    border-color: #dcdfe6 !important;
}

:deep(.el-upload-list) {
    display: none !important;
}

.online-btn {
    display: flex; 
    align-items: center; 
    gap: 4px; 
    background: #EEF1FE; 
    padding: 3px 10px; 
    border-radius: 4px; 
    color: #999;
    margin-right: 10px;
    font-size: 14px;
    cursor: pointer;  
}

:deep(.el-textarea__inner) {
    height: 24px !important;
}

:deep(.el-sender-updown-wrap) {
    align-items: end;
}

.thinking-chain-warp {
    margin-bottom: 12px;
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

:deep(.markdown-body p) {
    margin-bottom: 0;
    margin-top: 0;
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
    }
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
 
 .active-tab {
     color: #6b05a8 !important;

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
    
    // 删除按钮 - 默认隐藏，悬停时显示在右上角
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
.out-view-item-type {
    font-size: 12px;
    margin-left: 10px;
    display: inline-block;
    background-color: #eee;
    border-radius: 6px;
    padding: 3px 6px;
    color: #666;
}

// AIDialogue模式特有样式
.ai-dialogue {
    flex: 1;
    padding: 20px 100px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
    transition: padding 0.3s ease;

    /* 默认背景 */
    background: linear-gradient(#ECEFFD 0%, #F2F3F8 100%);
}

.ai-dialogue.has-quote-panel {
    padding: 20px;
}

.ai-dialogue.has-bg {
    background: none !important;
}

.footer-container {
    display: flex;
    align-items: center;
    gap: 10px;
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

// 调整边框和字体颜色
:deep(.el-bubble-footer) {
    margin-top: 6px !important;
}

.user-content {
    color: #303133;
    line-height: 1.6;
    word-wrap: break-word;
}

.active-tab {
    color: #6b05a8 !important;
}

// 添加缺失的深度样式选择器
:deep(.el-bubble-end) {
    .el-bubble-avatar-placeholder {
        display: none;
    }
}

:deep(.markdown-body p) {
    margin-bottom: 0;
}

:deep(.markdown-body) {
    img[alt="source-marker-doc"],
    img[alt="source-marker-qa"] {
        width: 12px; /* Adjust as needed */
        height: 12px; /* Adjust as needed */
        vertical-align: middle;
        margin-left: 4px;
        padding-bottom: 4px; /* Add padding-bottom */
        display: inline-block;
        box-sizing: content-box;
    }
}

/* 引用资料相关样式 */
.agent-container {
    display: flex;
    height: 100%;
    width: 100%;
    background: linear-gradient(#ECEFFD 0%, #F2F3F8 100%);
}

.assistant-chat {
    flex: 1;
    transition: all 0.3s ease;
}

.content-right {
    width: 0;
    opacity: 0;
    transition: all 0.3s ease;
    overflow: hidden;
    background: #fff;
    border-left: 1px solid #eee;
    
    &.visible {
        width: 350px;
        opacity: 1;
    }
    
    .content-right-warp {
        width: 350px;
        height: 100%;
        background: #fff;
        padding: 10px;
        display: flex;
        flex-direction: column;
    }
}

.result-item {
    display: flex;
    padding: 10px;
    background: #f9f9f9;
    margin-bottom: 10px;   
    flex-direction: column;
    font-size: 14px; 
    .result-title {
        color: #333;
        line-height: 1.4;
        overflow: hidden;
        
        .result-markdown {
            :deep(.v-md-editor-preview) {
                padding: 0;
                background: transparent;
            }
            
            :deep(.v-md-editor-preview-wrapper) {
                padding: 0;
            }
            
            :deep(.vuepress-markdown-body) {
                padding: 0;
                background: transparent;
            }
            
            :deep(h1, h2, h3, h4, h5, h6) {
                margin-top: 0;
                margin-bottom: 8px;
                font-size: 20px;
            }
            
            :deep(p) {
                margin: 0 0 8px 0;
            }
            
            :deep(ul, ol) {
                margin: 0 0 8px 0;
                padding-left: 20px;
            }
            
            :deep(blockquote) {
                margin: 0 0 8px 0;
                padding: 8px 12px;
                border-left: 4px solid #409eff;
                background: #f8f9fa;
            }
            
            :deep(code) {
                background: #f1f2f3;
                padding: 2px 4px;
                border-radius: 3px;
                font-size: 13px;
            }
            
            :deep(pre) {
                background: #f8f9fa;
                padding: 12px;
                border-radius: 6px;
                overflow-x: auto;
                margin: 0 0 8px 0;
            }
            
            // 限制图片最大宽度
            :deep(img) {
                max-width: 400px !important;
                height: auto;
                display: block;
                margin: 8px 0;
                border-radius: 4px;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            }
        }
    }
}

// 引用资料样式
.quote-reference {
    display: inline-flex;
    align-items: center;
    padding: 2px 6px;
    background: #e3f2fd;
    color: #1976d2;
    border-radius: 6px;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s ease;
    user-select: none;
    
    .file-icon-wrapper {
        display: inline-flex;
        align-items: center;
    }
    
    .file-type-icon {
        width: 14px;
        height: 14px;
        background: #fff;
        border-radius: 50%;
        vertical-align: middle;
        position: relative;
    }
    
    &:hover {
        background: #bbdefb;
        color: #0d47a1;
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(25, 118, 210, 0.2);
    }
}
:deep(.source-badge) {
    display: inline-block;
    width: 14px;
    height: 14px;
    vertical-align: text-bottom;
    margin-left: 5px;
}
:deep(.source-badge.source-doc) {
    background-image: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPScxNCcgaGVpZ2h0PScxNCcgdmlld0JveD0nMCAwIDI0IDI0JyBmaWxsPSdub25lJyBzdHJva2U9JyM2NjY2NjYnIHN0cm9rZS13aWR0aD0nMicgc3Ryb2tlLWxpbmVjYXA9J3JvdW5kJyBzdHJva2UtbGluZWpvaW49J3JvdW5kJz48cGF0aCBkPSdNMTQgMkg2YTIgMiAwIDAgMC0yIDJ2MTZhMiAyIDAgMCAwIDIgMmgxMmEyIDIgMCAwIDAgMi0yVjh6Jy8+PHBvbHlsaW5lIHBvaW50cz0nMTQgMiAxNCA4IDIwIDgnLz48bGluZSB4MT0nOCcgeTE9JzEzJyB4Mj0nMTYnIHkyPScxMycvPjxsaW5lIHgxPSc4JyB5MT0nMTcnIHgyPScxNicgeTI9JzE3Jy8+PC9zdmc+");
    background-repeat: no-repeat;
    background-position: center;
    background-size: contain;
}
:deep(.source-badge.source-qa) {
    background-image: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0naHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmcnIHdpZHRoPScxNCcgaGVpZ2h0PScxNCcgdmlld0JveD0nMCAwIDI0IDI0JyBmaWxsPSdub25lJyBzdHJva2U9JyM2NjY2NjYnIHN0cm9rZS13aWR0aD0nMicgc3Ryb2tlLWxpbmVjYXA9J3JvdW5kJyBzdHJva2UtbGluZWpvaW49J3JvdW5kJz48cGF0aCBkPSdNMjEgMTVhNCA0IDAgMCAxLTQgNEg3bC00IDRGN2E0IDQgMCAwIDEgNC00aDEwYTQgNCAwIDAgMSA0IDR6Jy8+PGxpbmUgeDE9JzgnIHkxPSc5JyB4Mj0nMTYnIHkyPSc5Jy8+PGxpbmUgeDE9JzgnIHkxPScxMycgeDI9JzE0JyB5Mj0nMTMnLz48L3N2Zz4=");
    background-repeat: no-repeat;
    background-position: center;
    background-size: contain;
}
.result-item {
     display: flex;
     padding: 10px;
     background: #f9f9f9;
     margin-bottom: 10px;   
     flex-direction: column;
     font-size: 14px; 
     .result-title {
       color: #333;
       line-height: 1.4;
       overflow: hidden;
       
       .result-markdown {
         :deep(.v-md-editor-preview) {
           padding: 0;
           background: transparent;
         }
         
         :deep(.v-md-editor-preview-wrapper) {
           padding: 0;
         }
         
         :deep(.vuepress-markdown-body) {
           padding: 0;
           background: transparent;
         }
         
         :deep(h1, h2, h3, h4, h5, h6) {
           margin-top: 0;
           margin-bottom: 8px;
           font-size: 20px;
         }
         
         :deep(p) {
           margin: 0 0 8px 0;
         }
         
         :deep(ul, ol) {
           margin: 0 0 8px 0;
           padding-left: 20px;
         }
         
         :deep(blockquote) {
           margin: 0 0 8px 0;
           padding: 8px 12px;
           border-left: 4px solid #409eff;
           background: #f8f9fa;
         }
         
         :deep(code) {
           background: #f1f2f3;
           padding: 2px 4px;
           border-radius: 3px;
           font-size: 13px;
         }
         
         :deep(pre) {
           background: #f8f9fa;
           padding: 12px;
           border-radius: 6px;
           overflow-x: auto;
           margin: 0 0 8px 0;
         }
         
         // 限制图片最大宽度
         :deep(img) {
           max-width: 400px !important;
           height: auto;
           display: block;
           margin: 8px 0;
           border-radius: 4px;
           box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
         }
       }
     }
   }
:deep(.elx-xmarkdown-provider) {
    img {
       max-width: 400px !important;
       cursor: pointer;
       transition: transform 0.2s ease;
       
       &:hover {
           transform: scale(1.02);
           box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
       }
    }
}

/* markdown 图片预览样式优化 */
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