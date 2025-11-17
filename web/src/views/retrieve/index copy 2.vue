<template>
  <div class="retrieve-container">
    <img src="@/assets/images/retrieve_bg.png" class="retrieve-bg" alt="">
    <img src="@/assets/images/specialist_bg.png" class="retrieve-bg-2" alt="">
    <div class="content-left"  :class="{ 'collapsed': !assistantVisible }">
      <div class="assistant-toggle1" @click="toggleAssistant">
        <el-icon>
          <ArrowLeft v-if="assistantVisible" />
          <ArrowRight v-else />
        </el-icon>
      </div>
      <el-button v-if="!assistantVisible" type="primary" icon="chat-dot-round" style="background: linear-gradient( 90deg, #9AC3FF 0%, #BB56FE 100%); border: none; width: 30px;" ></el-button>
      <div v-if="assistantVisible" style="display: flex; align-items: center; gap: 10px;">
          <el-button type="primary" icon="chat-dot-round" style="background: linear-gradient( 90deg, #9AC3FF 0%, #BB56FE 100%); border: none; width: 200px;" @click="handleNewChat">开启新对话</el-button>
          <el-button link @click="handleClearAllSession">
              <svg t="1569683368540" class="icon" viewBox="0 0 1024 1024" color="#FBFBFB" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9723" width="20" height="20" data-v-133a2e38=""><path d="M899.1 869.6l-53-305.6H864c14.4 0 26-11.6 26-26V346c0-14.4-11.6-26-26-26H618V138c0-14.4-11.6-26-26-26H432c-14.4 0-26 11.6-26 26v182H160c-14.4 0-26 11.6-26 26v192c0 14.4 11.6 26 26 26h17.9l-53 305.6c-0.3 1.5-0.4 3-0.4 4.4 0 14.4 11.6 26 26 26h723c1.5 0 3-0.1 4.4-0.4 14.2-2.4 23.7-15.9 21.2-30zM204 390h272V182h72v208h272v104H204V390z m468 440V674c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v156H416V674c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v156H202.8l45.1-260H776l45.1 260H672z" p-id="9724"></path></svg>
          </el-button>
      </div>
      <div v-if="assistantVisible" class="history-list">
        <div class="history-section">
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

    </div>
    <div class="content">
      <div v-if="list.length > 0" style="flex: 1; padding: 10px 20px; overflow: hidden;">
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
                    <XMarkdown v-if="item.content && item.role === 'system'" :markdown="item.content" class="markdown-body" :html="true" :linkify="true" :themes="{ light: 'github-light', dark: 'github-dark' }" default-theme-mode="dark" />
                    <!-- <Typewriter v-if="item.content && item.role === 'system'" is-fog typing :content="item.content" :is-markdown="true" /> -->
                    <!-- user 文件消息 -->
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
                                    <el-button type="danger" size="small" circle @click="handleClose(file)" class="remove-btn">
                                        <el-icon><Close /></el-icon>
                                    </el-button>
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
                    </div>
                  </div>
                </template>
          </bubble-list>
      </div>
      <div v-else style="padding: 20px">
        <div class="main-title">让工业知识会思考、会协作、会进化</div>
      </div>

      <div>
          <Sender ref="senderRef" v-model="senderValue" variant="updown" :auto-size="{ minRows: 3, maxRows: 3 }" clearable allow-speech placeholder="请输入问题" @submit="handleSubmit">
              <template #header>
                  <div class="header-self-wrap">
                      <div class="file-list-container">
                          <div v-for="(item, index) in fileList" :key="index" class="file-item">
                              <!-- 图片文件 -->
                              <div v-if="isImageFile(item.name)" class="image-file">
                                  <img :src="item.url" :alt="item.name" class="file-image" />
                                  <div class="file-overlay">
                                      <span class="file-name">{{ item.name }}</span>
                                  </div>
                                  <!-- 删除按钮在右上角 -->
                                  <el-button type="danger" size="small" circle @click="handleClose(item)" class="remove-btn">
                                      <el-icon><Close /></el-icon>
                                  </el-button>
                              </div>
                              <!-- 其他文件 -->
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
                      <el-select class="model-select" v-model="modelValue" placeholder="请选择模型" style="width: 170px;" @change="handleModelChange">
                        <template #label="{ label, value }">
                          <div style="display: flex; align-items: center; gap: 8px;">
                            <img :src="modelOptions.find(item => item.model === value).modelIcon" style="width: 24px; height: 24px;" alt="">
                            <span>{{ label }}</span>
                          </div>
                        </template>
                        <el-option v-for="item in modelOptions" :key="item.model" :label="item.name" :value="item.model">
                          <div style="display: flex; align-items: center; gap: 8px;">
                            <img :src="item.modelIcon" style="width: 24px; height: 24px;" alt="">
                            {{ item.name }}
                          </div>

                        </el-option>
                      </el-select>
                      <div class="online-btn"  :style="{ color: queryParams.knowledgeIds.length > 0 ? '#333' : '#999' }" @click="openSettings">
                        <span>{{ knowName || '知识库' }} <span v-if="queryParams.knowledgeIds.length > 0">({{ queryParams.knowledgeIds.length }})</span></span>
                        <!-- <span style="position: absolute; right: -4px; top: -4px; width: 14px; height: 14px; background: #F56C6C; border-radius: 50%; color: #fff; font-size: 12px; text-align: center; line-height: 14px;">3</span> -->
                      </div>
                      <div class="online-btn" :style="{ color: enableSearch ? '#333' : '#999' }" v-if="modelData.search" @click="enableSearch = !enableSearch">
                          已联网
                          <span v-if="enableSearch" style="width: 5px; height: 5px; background: #3DCD58; border-radius: 50%;"></span>
                          <span v-else style="width: 5px; height: 5px; background: #999; border-radius: 50%;"></span>
                      </div>
                      <div class="online-btn"  :style="{ color: enableDeepChat ? '#333' : '#999' }" v-if="modelData.deepThinkingModel" @click="enableDeepChat = !enableDeepChat">
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
                          v-if="modelData.multimodal"
                          action="/s3/upload"
                          :http-request="fileUploadFn"
                          :before-upload="beforeUpload"
                          accept="*/*"
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
                          :style="{
                            width: '60px',
                            background: (modelValue && queryParams.knowledgeIds.length > 0) 
                              ? 'linear-gradient(90deg, rgb(154, 195, 255) 0%, rgb(187, 86, 254) 100%)' 
                              : '#eee',
                            cursor: (modelValue && queryParams.knowledgeIds.length > 0) ? 'pointer' : 'not-allowed'
                          }"
                          :disabled="!modelValue || queryParams.knowledgeIds.length === 0"
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
      <!-- 知识列表 -->
      <div  v-if="list.length === 0" class="knowledge-section">
        <div class="knowledge-column">
          <div class="section-title">
            <span :class="{ active1: activek === 0 }" style="cursor: pointer;" @click="handleKnowledgeList(0)">最新知识</span>
            <span style="margin-left: 35px;cursor: pointer;" :class="{ active1: activek === 1 }"  @click="handleKnowledgeList(1)">最热知识</span>
          </div>
          <div v-if="latestKnowledge.length > 0" class="knowledge-list custom-scrollbar-container">
            <div v-for="(item, index) in latestKnowledge" :key="item.id" class="knowledge-item"  @click="goDetail(item.fileId)">
              <img v-if="index === 0 && activek === 1" style="width: 21px;" :src="hot_1" alt="">
              <img v-else-if="index === 1 && activek === 1" style="width: 21px;" :src="hot_2" alt="">
              <img v-else-if="index === 2 && activek === 1" style="width: 21px;" :src="hot_3" alt="">
              <span v-else-if="activek === 1 " style="width: 21px; font-weight: 800; color: #D8D8D8;">{{ index < 9 ? '0' + (index + 1) : index + 1}}</span>
              <img v-else :src="getFilePath(item.fileName)" style="width: 21px;" alt="">
              <div class="knowledge-content">
                <div class="knowledge-title text-ellipsis" style="cursor: pointer;">{{ item.fileName }}</div>
                <div class="knowledge-meta">{{ item.knowledgeBaseName }}</div>
              </div>
              <div class="knowledge-time">{{ item.createdTimeText }}</div>
            </div>
          </div>
          <div  class="knowledge-list" v-else style="display: flex; align-items: center; justify-content: center; flex-direction: column;">
            <img :src="notData" style="width: 150px;" alt="">
            <span style="color:rgba(0, 0, 0, 0.6)">暂无知识</span>
          </div>
        </div>
        
        <div class="knowledge-column">
          <div class="section-title">
            <span>知识推荐</span>
          </div>
          <div class="knowledge-list custom-scrollbar" v-if="recommendedKnowledge.length > 0">
            <div v-for="item in recommendedKnowledge" :key="item.id" class="knowledge-item"  @click="goDetail(item.fileId)">
              <img :src="getFilePath(item.fileName)" style="width: 21px;" alt="">
              <div class="knowledge-content">
                <div class="knowledge-title text-ellipsis" style="cursor: pointer;">{{ item.fileName }}</div>
                <div class="knowledge-meta">{{ item.knowledgeBaseName }}</div>
              </div>
              <div class="knowledge-time">{{ item.createdTimeText }}</div>
            </div>
          </div>
          <div  class="knowledge-list" v-else style="display: flex; align-items: center; justify-content: center; flex-direction: column;">
             <img :src="notData" style="width: 150px;" alt="">
             <span style="color:rgba(0, 0, 0, 0.6)">暂无知识推荐</span>
          </div>
        </div>
      </div>
    </div>
    <div class="content-right" :class="{ 'visible': showQuoteResults }">
      <div  v-if="showQuoteResults" class="content-right-warp">
         <div style="display: flex;align-items: center;justify-content: space-between;">
          <span style="border-left: 2px solid #6B05A8; padding-left: 10px;">引用资料</span>
          <span style="cursor: pointer; display: flex;align-items: center;gap: 5px; color: #666666; font-size: 12px;" @click="showQuoteResults = false"><span>收起 </span><el-icon><CaretRight /></el-icon></span>
          </div>
         <div style="flex: 1; overflow-y: auto; margin-top: 10px;">
             <div v-for="result in searchResults"  class="result-item" :key="result.filename" :style="{cursor:  result.id ? 'pointer' : 'default'}" @click="goDetail(result.id)">
               <div class="result-title">
                 <v-md-preview :text="result.highlight" class="result-markdown" />
          </div>
                 <div style="color: #999; margin-top: 10px;" v-if="result.filename">
                   <span style="display: flex;align-items: center;gap: 5px;"><img :src="getFilePath(result.filename)" style="width: 16px;" alt=""> {{ result.filename }}</span>
                 </div>
          </div>
          </div>
        </div>
      </div>
    <el-dialog v-model="showSettings" title="知识库设置" width="600" @close="cancel">
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              知识库
            </span>
            <el-select v-model="form.knowledgeIds" placeholder="请选择知识库" multiple>
              <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              最大召回分段
              <el-tooltip content="最大召回字数内，知识库查询结果中可以被召回的最大段落数" placement="top">
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-slider style="padding-left: 5px" v-model="form.maxSegmentCount" :max="20" :min="1" size="small" :marks="marks" class="max-recall-slider" />
            <span style="background: #F2F4FF; padding: 5px 10px; margin-left: 20px;">{{  form.maxSegmentCount }}</span>
          </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              结果重排
              <el-tooltip content="使用重拍模型对召回结果进行二次排序，增强召回效果" placement="top" >
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-select v-model="form.rerankModel" style="flex: 1;" class="rerank-select" clearable @visible-change="handleSelectVisible">
              <el-option v-for="item in rerank_model" :key="item.value" :label="item.label" :value="item.value">
                <div>{{ item.label }}</div>
                <div
                  style="color: var(--el-text-color-secondary);
                  font-size: 13px;"
                >
                  {{ item.remark }}
        </div>
              </el-option>
            </el-select>
      </div>
        <div class="segment-options">
          <el-radio-group v-model="form.searchType" class="segment-radio-group">
            <div class="radio-option">
              <el-radio value="fulltext" size="large">
                <div class="radio-content">
                  <div class="radio-title">全文检索</div>
                  <div class="radio-description">通过关键词精准匹配进行检索</div>
                </div>
              </el-radio>
            </div>
            <div class="radio-option">
              <el-radio value="hybrid" size="large">
                <div class="radio-content">
                  <div class="radio-title">混合检索</div>
                  <div class="radio-description">同时使用语义检索和全文检索进行查询，并对结果进行排序</div>
                </div>
              </el-radio>
            </div>
            <div class="radio-option">
              <el-radio value="semantic" size="large">
                <div class="radio-content">
                  <div class="radio-title">语义检索</div>
                  <div class="radio-description">基于向量化技术，通过分析查询的语义和上下文来提供更准确和相关的搜索结果</div>
                </div>
              </el-radio>
            </div>
            
            
            </el-radio-group>   
        </div>
        
        <template #footer>
            <el-button  @click="cancel">取消</el-button>
            <el-button type="primary" @click="confirm">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import moment from 'moment'
import { BubbleList, Sender, XMarkdown, Thinking } from 'vue-element-plus-x'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import Prism from 'prismjs'
import { recordQa } from '@/api/qa'
import { nanoid } from '@/utils'

// 配置markdown预览器
VMdPreview.use(vuepressTheme, {
  Prism,
})

import { fileUpload } from '@/api/ai/aiStore'
import robotAvatar from '@/assets/images/robot.png'
const { proxy } = getCurrentInstance()
import { getToken } from '@/utils/auth'
import { fetchEventSource } from '@microsoft/fetch-event-source'
const { rerank_model } = toRefs(proxy?.useDict('rerank_model'));
import { 
  getKnowledgeRetrieveSummary, 
  getKnowledgeRetrieveRecommend, 
  getKnowledgeRetrieveLatest,
  getModel,
  createSession,
  getSessionList,
  deleteSession,
  editSessionName,
  getSessionDetail,
  clearAllSession
} from '@/api/retrieve'
import { queryAllKnowledgeBase } from '@/api/base/'
import notData from '@/assets/images/notData.png'
import hot_1 from '@/assets/images/hot_1.svg'
import hot_2 from '@/assets/images/hot_2.svg'
import hot_3 from '@/assets/images/hot_3.svg'

const router = useRouter()
const route = useRoute()
const knowName = ref('')
const messageUuid = ref('')
const searchResults = ref([])
const showQuoteResults = ref(false)
const currentQuoteList = ref([]) // 存储当前对话的引用资料

// 历史记录相关字段
const inputValue = ref('')
const sessionUuid = ref('')
const todayList = ref([])
const historyList = ref([])
const userId = ref(null)
const abortController = ref(null) // 用于控制SSE连接

// 聊天相关字段
const senderRef = ref(null)
const senderValue = ref('')
const list = ref([])
const fileList = ref([])
const enableDeepChat = ref(false) //是否启用深度思考
const enableSearch = ref(false) //是否启用联网
const isStreaming = ref(false) // 用于控制按钮显示状态
const isThinking = ref(false) // 思考状态
const options = ref([])
const modelData = ref({}) // 已选择模型
const assistantVisible = ref(false)

// 知识列表相关字段
const activek = ref(0)
const latestKnowledge = ref([])
const recommendedKnowledge = ref([])
const latest = ref({})
const modelOptions = ref([])

// 高级设置相关字段
const showSettings = ref(false)
const modelValue = ref('')
const form = ref({
  knowledgeIds: [],
  maxSegmentCount: 10,
  searchType: 'fulltext',
  rerankModel: 'qwen3-rerank'
})
const queryParams = ref({
  knowledgeIds: [],
  rerankModel: 'qwen3-rerank',
  maxSegmentCount: 10,
  searchType: 'fulltext',
  source: 'vector_search'
})
const marks = {
  0: '0',
  20: '20'
}
const toggleAssistant = () => {

  assistantVisible.value = !assistantVisible.value
}

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

          }
          console.log('param',params)
          recordQa(params).then(res => {
            // 使用 Vue 的响应式更新方式
            list.value[currentIndex].type = val
          })
        }
    }
 
}

// 全局方法，供 markdown 中的 onclick 调用
window.showQuoteListFromMarkdown = (element) => {
  const quoteListData = element.getAttribute('data-quote-list')
  if (quoteListData) {
    try {
      const quoteList = JSON.parse(decodeURIComponent(quoteListData))
      showQuoteList(quoteList)
    } catch (error) {
      console.error('解析引用资料失败:', error)
    }
  }
}
// 历史记录相关方法
const handleEdit = (i) => {
    i.out = true
    inputValue.value = i.sessionName
    // 清空路由参数
    router.replace({ query: {} })
    // 使用 nextTick 确保 DOM 更新后再获取焦点
    nextTick(() => {
        const inputElement = document.querySelector('.history-item input')
        if (inputElement) {
            inputElement.focus()
        }
    })
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



const handleDelete = (i) => {
    ElMessageBox.confirm('确定删除该对话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteSession(i.id).then(res => {
            ElMessage.success('删除成功')
            // 清空路由参数
            router.replace({ query: {} })
            getHistoryList()
        })
    })
}

const handleDetail = (i) => {
  // 如果当前有正在进行的对话，先断开SSE连接
  if (abortController.value) {
    abortController.value.abort()
    abortController.value = null
  }
  
  // 重置流状态
  isStreaming.value = false
  senderValue.value = ''
  // 关闭引用资料面板
  showQuoteResults.value = false
  
  // 清空文件列表
  fileList.value = []
  closeHeader()
  modelValue.value = i.model
  modelData.value = modelOptions.value.find(item => item.model === modelValue.value)
  modelData.value.search = true
  knowName.value = i.knowledgeIds.length > 0 ? options.value.find(item => i.knowledgeIds.includes(item.id))?.name : '知识库'
  enableSearch.value = i.webSearch
  enableDeepChat.value = i.deepThinking
  queryParams.value = {
    knowledgeIds: i.knowledgeIds,
    rerankModel: i.rerankModel,
    maxSegmentCount: i.maxSegmentCount,
    searchType: i.searchType
  }
  form.value = { ...form.value, ...queryParams.value }
  sessionUuid.value = i.uuid
  // 清空路由参数
  router.replace({ query: {} })
  getSessionDetail({
    sessionUuid: i.uuid
  }).then(res => {

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
        
        // 如果有引用资料，在内容后添加引用显示
        if (item.quoteList && item.quoteList.length > 0) {
          content += generateQuoteReference(item.quoteList)
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
          type: item.feedbackType
        })
      }
    })
    
    // 更新对话列表
    list.value = [...arr]
  })
}

const handleNewChat = () => {
  createSession({
        userId: userId.value,
        sessionName: '新对话',  
    }).then(res => {
      sessionUuid.value = res.data
        list.value = [] // 清空当前对话列表
        // 清空文件列表
        fileList.value = []
        senderValue.value = ''
        closeHeader()
        // 重置模型和知识库设置
        modelValue.value = ''
        modelData.value = {}
        knowName.value = '知识库'
        enableSearch.value = false
        enableDeepChat.value = false
        queryParams.value = {
          knowledgeIds: [],
          rerankModel: 'qwen3-rerank',
          maxSegmentCount: 10,
          searchType: 'fulltext',
          source: 'vector_search'
        }
        getHistoryList()
        // 关闭引用资料面板
        showQuoteResults.value = false
        // 清空路由参数
        router.replace({ query: {} })
    })
}

const getHistoryList = () => {
  return getSessionList().then(res => {
    todayList.value = res.data.filter(i => moment(i.createTime).isSame(moment(), 'day'))
    historyList.value = res.data.filter(i => !moment(i.createTime).isSame(moment(), 'day'))
  })
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

// 聊天相关方法
// 上传前验证文件类型
const beforeUpload = (file) => {
    // 检查文件大小（可选，比如限制为10MB）
    const isLt10M = file.size / 1024 / 1024 < 10
    if (!isLt10M) {
        ElMessage.error('文件大小不能超过 10MB!')
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

const closeHeader = () => {
    if (senderRef.value) {
        senderRef.value.closeHeader()
    }
}

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
    sessionUuid.value = ''
    
    ElMessage.success('聊天内容已清空')
}

const handleModelChange = (value) => {
  modelData.value = modelOptions.value.find(item => item.model === value)
  modelData.value.search = true
}



const handleSubmit = async () => {
    if (!senderValue.value.trim()) return
    if (!modelValue.value) {
    return
  }
    if (!queryParams.value.knowledgeIds.length) {
      return
    }
    
    // 关闭引用资料面板
    showQuoteResults.value = false
    // 清空路由参数
    router.replace({ query: {} })
    
    // 清空引用资料
    currentQuoteList.value = []
    
    // 如果没有sessionUuid，先创建新对话
    if (!sessionUuid.value) {
    const { data } = await createSession({
        userId: userId.value,
        sessionName: '新对话',
      })
      sessionUuid.value = data
      // 清空文件列表
      fileList.value = []
      closeHeader()
      getHistoryList()
    }
    
    // 设置流状态
    isStreaming.value = true
    
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
        type: null,
        chatUuid: messageUuid.value
    })

    await streamResponse(userMessage, aiMessageIndex)
   
}

const streamResponse = async (userMessage, messageIndex) => { 
 
  const requestBody = {
      ...form.value, // 知识库内容
      query: userMessage, // 查询内容 - 放在后面确保不被覆盖
      model: modelValue.value, // 模型
      sessionUuid: sessionUuid.value, // 绑定的对话uuid
      userId: userId.value, // 用户id
      deepThinking: enableDeepChat.value, // 深度思考
      webSearch: enableSearch.value, // 联网
      multimodal: modelData.value.multimodal, // 多模态文件上传
      chatUuid: messageUuid.value // 当前回答消息的id
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
        await fetchEventSource(import.meta.env.VITE_APP_BASE_API + '/knowledge/chat', {
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
                    

                     // 尝试解析JSON数据
                     try {
                         const parsedData = JSON.parse(data)
                        //  console.log('解析的数据:', parsedData)
                         
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
                             list.value[messageIndex].content += content
                             list.value[messageIndex].typing = true
                             list.value[messageIndex].isFog = true

                             
                         } else if (parsedData.quoteList) {
                             // 收集引用资料，不立即显示
                             console.log('收到引用资料:', parsedData)
                             currentQuoteList.value = parsedData.quoteList
                         }
                        
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
                    }
                } catch (error) {
                    console.error('处理SSE消息时出错:', error)
                }
            },
            
            onclose: () => {
                isStreaming.value = false
                
                // 如果有引用资料，添加到AI回复末尾
                console.log('SSE连接关闭，检查引用资料:', currentQuoteList.value)
                if (currentQuoteList.value && Array.isArray(currentQuoteList.value) && currentQuoteList.value.length > 0) {
                    let currentContent = list.value[messageIndex].content
                    const quoteReference = generateQuoteReference(currentQuoteList.value)
                    
                    // 更新AI消息内容，添加引用资料
                    list.value[messageIndex].content = currentContent + quoteReference
                    console.log('引用资料已添加到消息:', quoteReference)
                    
                    // 清空引用资料
                    currentQuoteList.value = []
                }
                
                list.value[messageIndex] = {
                    ...list.value[messageIndex],
                    loading: false,
                    typing: false
                }        
                // 清空文件列表并关闭header
                fileList.value = []
                closeHeader()
                
                // 清理AbortController
                abortController.value = null
            },
            
            onerror: (err) => {
                isStreaming.value = false
                // 如果是手动终止，不抛出错误
                if (err.name === 'AbortError') {

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

const openSettings = () => {
  showSettings.value = true
  form.value = {
    ...form.value,
    ... queryParams.value
  }
  
  // 等下拉菜单渲染后再应用样式
  nextTick(() => {
    setTimeout(() => {
      const dropdownItems = document.querySelectorAll('.el-select-dropdown__item')
      dropdownItems.forEach(item => {
        // 检查是否包含两个 div 子元素（label 和 remark）
        const divs = item.querySelectorAll('div')
        if (divs.length >= 2) {
          item.style.height = '70px'
          item.style.lineHeight = '1.4'
          item.style.padding = '8px 20px'
          item.style.whiteSpace = 'normal'
          item.style.display = 'flex'
          item.style.flexDirection = 'column'
          item.style.justifyContent = 'center'
        }
      })
    }, 100)
  })
}

const terminateFn = () => {
    // 终止SSE连接
    if (abortController.value) {
        abortController.value.abort()
        abortController.value = null
         // 终止输出
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
    }
    
   
}


// 知识列表相关方法
const handleKnowledgeList = (index) => {
  activek.value = index
  if (index === 0) {
    latestKnowledge.value = latest.value.latestKnowledge.slice(0, 10)
  } else {
    latestKnowledge.value = latest.value.hotKnowledge.slice(0, 10)
  }
}

const getFilePath = (fileName) => {
  if (!fileName) return new URL('../../assets/fileIcon/othe.svg', import.meta.url).href;
  
  // 获取文件扩展名
  const extension = fileName.toLowerCase().split('.').pop();
  
  // 定义支持的文件类型
  const supportedExtensions = ['jpg', 'html', 'doc', 'ppt', 'mp4', 'txt', 'pdf', 'xls', 'png', 'md', 'csv', 'xlsx', 'xlsx', 'docx', 'jpeg'];
  
  // 特殊处理一些扩展名
  let iconName = extension;
  // 检查是否有对应的图标
  if (supportedExtensions.includes(iconName)) {
    return new URL(`../../assets/fileIcon/${iconName}.svg`, import.meta.url).href;
  } else {
    return new URL('../../assets/fileIcon/othe.svg', import.meta.url).href;
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

// 生成文件类型图标HTML
const generateFileTypeIcons = (quoteList) => {
  const fileTypes = [...new Set(quoteList.map(quote => {
    const extension = quote.filename ? quote.filename.toLowerCase().split('.').pop() : 'othe'
    return extension
  }))]
  
  return fileTypes.map((ext, index) => {
    const iconPath = getFilePath(`file.${ext}`)
    const marginLeft = index > 0 ? '-8px' : '4px'
    return `<img src="${iconPath}" style="width: 14px; height: 14px; margin-left: ${marginLeft}; background: #fff; vertical-align: middle; position: relative; z-index: ${10 - index};" alt="${ext}">`
  }).join('')
}

// 生成引用资料HTML
const generateQuoteReference = (quoteList) => {
  const iconsHtml = generateFileTypeIcons(quoteList)
  return `\n\n<div class="quote-reference" data-quote-list='${encodeURIComponent(JSON.stringify(quoteList))}' onclick="window.showQuoteListFromMarkdown(this)">文章引用：${iconsHtml} ${quoteList.length} 篇文章</div>`
}

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

const getKnowledgeList = async () => {
  try {
    const resRecommend = await getKnowledgeRetrieveRecommend()
    recommendedKnowledge.value = resRecommend.data?.recommendKnowledge?.slice(0, 10) || []
      
    const resLatest = await getKnowledgeRetrieveLatest()
    latest.value = resLatest.data
    latestKnowledge.value = latest.value?.latestKnowledge?.slice(0, 10) || []
  } catch (error) {
    console.error('获取知识列表失败:', error)
  }
}

// 高级设置相关方法
const cancel = () => {
  showSettings.value = false
}

const confirm = () => {
  showSettings.value = false
    queryParams.value = {
    ...queryParams.value,
    ...form.value
  }
  if (form.value.knowledgeIds.length > 0) {
    knowName.value = options.value.find(item => form.value.knowledgeIds.includes(item.id))?.name
  } else {
    knowName.value = '知识库'
  }
}

const handleSelectVisible = (visible) => {
  if (visible) {
    // 下拉菜单展开时应用样式
    nextTick(() => {
      setTimeout(() => {
        const dropdownItems = document.querySelectorAll('.el-select-dropdown__item')
        dropdownItems.forEach(item => {
          // 检查是否包含两个 div 子元素（label 和 remark）
          const divs = item.querySelectorAll('div')
          if (divs.length >= 2) {
            item.style.height = '70px'
            item.style.lineHeight = '1.4'
            item.style.padding = '8px 20px'
            item.style.whiteSpace = 'normal'
            item.style.display = 'flex'
            item.style.flexDirection = 'column'
            item.style.justifyContent = 'center'
          }
        })
      }, 50)
    })
  }
}

const restoreSearchState = () => {
  // 检查是否从详情页返回
  if (route.query.fromSearch === 'true' && route.query.sessionUuid) {
    // 找到对应的对话项并重新点击
    const allSessions = [...todayList.value, ...historyList.value]
    const targetSession = allSessions.find(session => session.uuid === route.query.sessionUuid)
    assistantVisible.value = true
    if (targetSession) {
      // 重新执行 handleDetail，恢复点击效果
      handleDetail(targetSession)
    }
  }
}

// 模拟数据
onMounted(() => {
    getHistoryList().then(() => {
      // 历史列表加载完成后再恢复搜索状态
      restoreSearchState()
    })
  queryAllKnowledgeBase().then(res => {
        options.value = res.data
    })
    getModel().then(res => {
        modelOptions.value = res.data
    })
    userId.value = JSON.parse(localStorage.getItem('userInfo')).id
    // 获取知识列表数据
    getKnowledgeList()
})
</script>
<style scoped lang="scss">

.retrieve-container {
  height: 100%;
  background: linear-gradient(#ECEFFD 100%, #F2F3F8 100%);
  position: relative;
  display: flex;
  .retrieve-bg {
    position: absolute;
    transform: translate(-50%, 0);
    left: 50%;
    top: 0;
  }
}
.retrieve-bg-2 {
  position: absolute;
  right: 0;
  top: 0;
  height: 35%;
}
 .content-left {
    width: 280px;
    height: 100%;
    background: #fff;
    padding: 10px 20px;
    display: flex;
    flex-direction: column;
    transition: all 0.3s ease;
    position: relative;
    &.collapsed {
      width: 40px;
      margin-right: 100px; 
      padding: 10px 5px;  
    }
 }

 .assistant-toggle1 {
  position: absolute;
  right: -10px;
  top: 50%;
  transform: translateY(-50%);
  width: 10px;
  height: 40px;
  background: white;
  box-shadow: 4px 0 8px #070c141f;
      display: flex;
      align-items: center;
  justify-content: center;
        cursor: pointer;
  z-index: 1000;
  &::before {
    content: '';
    position: absolute;
    left: 0;
    border-width: 5px;
    border-style: solid;
    bottom: -10px;
    border-color:  #fff  transparent transparent #fff ;
  }
  &::after {
    content: '';
    position: absolute;
    left: 0;
    border-width: 5px;
    border-style: solid;
    top: -10px;
    border-color:  transparent transparent #fff #fff;
  }
}


 /* 历史记录相关样式 */
 .history-list {
    flex: 1;
    overflow-y: auto;
 }

 .history-section {
   margin-bottom: 20px;
 }

 .history-item {
        cursor: pointer;
   margin-bottom: 8px;
    }
    
 .item-content {
   padding: 8px 5px;
            display: flex;
            cursor: pointer;
            align-items: center;
   border-radius: 8px;
            font-size: 14px;
   color: #333;
   transition: all 0.2s ease;
   
   .item-btn {
     display: none;
   }
   
        &:hover {
     background: #F9F9F9;
     
     .item-btn {
       display: block;
     }
   }
 }

 .checked {
   background: #F9F9F9; 
 }

 .text-ellipsis {
              overflow: hidden;
   text-overflow: ellipsis;
   white-space: nowrap;
 }

 .content { 
      flex: 1;
          padding: 10px 20px;
      overflow: hidden;
    display: flex;
    flex-direction: column;
   z-index: 999;
   position: relative;
    .main-title {
      margin: 0 auto;
      font-weight: bold;
       font-size: 37px;
      background: linear-gradient(to right, #5C078F, #AF50C5 );
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
      color: transparent;
      text-align: center;
   }
 }

 /* 聊天相关样式 */
 :deep(.el-bubble-list) {
     .el-bubble-content {
         background-color: #fff !important;
         max-width: 100% !important;
         padding: 7px 10px;
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
     padding: 5px 10px; 
     border-radius: 4px; 
     color: #999;
     margin-right: 10px;
     font-size: 14px;
            cursor: pointer;
      }

//  :deep(.el-textarea__inner) {
//      height: 24px !important;
//  }

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

        &:hover {
            background: #e9ecef;
            border-color: #dee2e6;

            .remove-btn {
                opacity: 1;
            }
        }

        .file-icon {
            flex-shrink: 0;
          display: flex;
          align-items: center;
            justify-content: center;
            
            img {
                width: 24px;
                height: 24px;
                object-fit: contain;
            }
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
       flex-shrink: 0;
        display: flex;
        align-items: center;
       justify-content: center;
       
       img {
         width: 24px;
         height: 24px;
         object-fit: contain;
       }
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
      
/* 知识列表相关样式 */
.knowledge-section {
      display: flex;
      justify-content: center;
  gap: 20px;
  margin: 20px auto;
  width: 100%;
          flex: 1;
  overflow: hidden;
          
  .knowledge-column {
    flex: 1;
    .section-title {
            font-size: 16px;
            margin-bottom: 10px;
    }
    
    .knowledge-list {
      padding: 10px 20px;
          border-radius: 8px;
      background: #fff;
      height: calc(100% - 35px);
      max-height: 500px;
      overflow-y: auto;
      
      .knowledge-item {
            display: flex;
      align-items: center;
      gap: 10px;
        padding: 10px 0;
            border-bottom: 1px solid #eee;
            cursor: pointer;
        
        &:last-child {
          border-bottom: none;
        }
            
            &:hover {
          .knowledge-title {
            color: #6B05A8 !important;
          }
        }
        
        .knowledge-content {
          flex: 1;
          overflow: hidden;
          
          .knowledge-title {
            font-size: 0.9rem;
            color: #333;
            margin-bottom: 5px;
          }
          
          .knowledge-meta {
            font-size: 0.8rem;
            color: #666;
          }
        }
        
        .knowledge-time {
          font-size: 0.8rem;
        color: #999;
      }
      }
    }
  }
}

  .active1 {
    color: #6B05A8;
    font-weight: 600;
  }
  
.custom-scrollbar-container,
.custom-scrollbar {
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
    
    &:hover {
      background: #a8a8a8;
    }
  }
}

/* 高级设置相关样式 */
.recall-title {
  font-size: 16px;
  margin-bottom: 20px;
  color: #333;
  display: flex;
  align-items: center;
}

  .segment-options {
       margin-bottom: 10px;
  
      .segment-radio-group {
        display: flex;
        flex-direction: column;
        gap: 10px;
       
        .radio-option {
          padding: 10px;
          transition: all 0.3s;
          background: #F2F6FF;
          width: 100%; 
      
          :deep(.el-radio) {
            width: 100%;
            
            .el-radio__label {
              width: 100%;
              padding-left: 20px;
            }
          }
          
          .radio-content {
            .radio-title {
              font-size: 16px;
              color: #303133;
              margin-bottom: 8px;
            }
            
            .radio-description {
              font-size: 14px;
              color: #909399;
            }
          }
        }
      }
    }

:deep(.el-slider__button) {
  width: 15px;
  height: 15px;
}

 .content-right {
   height: 100%;
   position: relative;
   z-index: 999;
   margin-left: 100px;
   width: 0;
   opacity: 0;
   transition: all 0.3s ease;
   overflow: hidden;
   
   &.visible {
     margin-left: 0;
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
.model-select {
  border-radius: 20px;
  :deep(.el-select__wrapper) {
    background: #EEF1FE !important;
    border-radius: 20px;
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

:deep(.quote-reference) {
   display: inline-block;
   padding: 4px 8px;
   background: #e3f2fd;
   color: #1976d2;
   border-radius: 4px;
   font-size: 12px;
   cursor: pointer;
   margin-top: 8px;
   transition: all 0.2s ease;
   &:hover {
     background: #bbdefb;
     color: #0d47a1;
   }
  }
  :deep(.el-bubble-footer) {
    margin-top: 0px !important;
}
</style> 