<template>
  <div class="file-upload-container">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="steps-container">
        <!-- 返回按钮 -->
         <div>
            <el-button link @click="goBack" icon="Back" style="font-size: 16px;"> 返回</el-button>
         </div>
         <!-- 步骤指示器 -->
        <el-steps :active="currentStep" align-center>
          <el-step>
            <template #icon>
              <div style="display: flex; flex-direction: column; position: relative;">
                <div class="step-icon" style="position: absolute; top: -60px; left: -12px;">
                  <img src="@/assets/knowledge/Email-push.svg" alt="">
                </div>
                <span :class="['step-number active']">1</span>
              </div>
            </template>
            <template #title>
              <span class="step-title">{{props.uploadType === 'file' ? '文件上传' : props.uploadType === 'url' ? 'URL填写' : '在线编辑'}}</span>
            </template>
          </el-step>
          
          <el-step>
            <template #icon>
              <div style="display: flex; flex-direction: column; position: relative;">
                <div class="step-icon" style="position: absolute; top: -60px; left: -12px;">
                  <img src="@/assets/knowledge/Dividing-line-one.svg" alt="">
                </div>
                <span  :class="['step-number', { active: currentStep >= 1 }]">2</span>
              </div>
            </template>
            <template #title>
              <span class="step-title">知识提取</span>
            </template>
          </el-step>
          
          <el-step>
            <template #icon>
              <div style="display: flex; flex-direction: column; position: relative;">
                <div class="step-icon" style="position: absolute; top: -60px; left: -12px;">
                  <img src="@/assets/knowledge/Auto-width-one.svg" alt="">
                </div>
                <span  :class="['step-number', { active: currentStep >= 2 }]">3</span>
              </div>
            </template>
            <template #title>
              <span class="step-title">分段编辑</span>
            </template>
          </el-step>
          
          <!-- <el-step>
            <template #icon>
              <div style="display: flex; flex-direction: column; position: relative;">
                <div class="step-icon" style="position: absolute; top: -60px; left: -12px;">
                  <img src="@/assets/knowledge/Message-sent.svg" alt="">
                </div>
                <span :class="['step-number', { active: currentStep >= 3 }]">4</span>
              </div>
            </template>
            <template #title>
              <span class="step-title">内容提交</span>
            </template>
          </el-step> -->
        </el-steps>
    <div>
            
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content-area">
        <!-- 文件上传步骤 -->
        <div v-if="currentStep === 0" class="upload-step">
          <div class="upload-area" v-if="props.uploadType === 'file'">
            <el-upload
              class="upload-dragger"
              drag
              :http-request="httpRequest"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :on-remove="handleRemove"
              :before-upload="beforeUpload"
              :show-file-list="false"
              multiple
            >
              <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
              <div class="el-upload__text">点击上传或拖拽文件到这里</div>
              <div class="upload-tip">
                支持 PDF、TXT、DOC、DOCX、MD, CSV, PPTX, XLSX, PNG, MP3, MP4 等最多可上传 15 个文件，每个文件不超过 100MB，PDF 最多 500 页
              </div>
            </el-upload>
          </div>

          <div v-if="props.uploadType === 'url'" class="url-step" style="margin-bottom: 30px;">
            <div class="url-input-container">
              <el-input class="url-input" v-model="urlRess" placeholder="请输入URL,多个url用逗号分割，一次最多可输入10个" type="textarea" :rows="5" />
              <el-button class="url-submit-btn" @click="urlSubmit" type="primary">提交</el-button>
            </div>
          </div>                    
          <!-- 已上传文件表格 -->
          <div  v-if="!isEdtor" class="uploaded-files-table">
            <div v-if="props.uploadType === 'online'" style="text-align: end; margin-bottom: 10px;">
              <el-button type="primary" @click="createNewDocument">新建文档</el-button>
            </div>
            <el-table :data="tempFileData.knowledgeFileUploadTempDTOList" style="width: 100%" border>
              <el-table-column prop="name" :label="props.uploadType === 'url' ? 'URL地址' : '文件名'" show-overflow-tooltip>
                <template #default="scope">
                  <div class="file-cell">
                    <img :src="getFileTypeFn(scope.row.fileName)" style="width: 16px" alt="">
                    <span>{{ scope.row.fileName }}</span>
                  </div>
                </template>
              </el-table-column>
              
              <el-table-column prop="parseResult" label="上传进度">
                <template #default="scope">
                  <div>
                    <el-progress 
                      :percentage="scope.row.progress || 0" 
                      :stroke-width="16"
                      :text-inside="true"
                      :format="(percentage) => `${percentage}%`"
                      :status="scope.row.parseResult === '上传失败' ? 'exception' : 
                               scope.row.parseResult === '合并失败' ? 'exception' : 'success'"
                    />
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="summary" label="文件大小">
                <template #default="scope">
                  <span>{{ (scope.row.fileSize / 1024 / 1024).toFixed(2) }}MB</span>
                </template>
              </el-table-column>      
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button 
                    v-if="props.uploadType === 'online'"
                    link 
                    size="small" 
                    icon="EditPen" 
                    @click="previewMarkdownInEditor(url + scope.row.localSourcePath.replace('uploadPath/', 'profile/'), scope.row.fileName, scope.row.fileMd5)"
                  >
                  编辑
                  </el-button>
                  <el-button 
                    link 
                    size="small" 
                    icon="Delete" 
                    @click="handleDelete(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 在线编辑器 -->
          <div v-else>
            <div style="display: flex; align-items: center; margin-bottom: 10px">
               <span style="width: 80px">文件名: </span>
               <el-input v-model="title" placeholder="请输入文件名" />
            </div>
            <div class="text-editor-container" v-loading="tableLoading">
                <!-- 编辑器工具栏 -->
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                  <Toolbar
                    style="border-bottom: 1px solid #ccc; flex: 1;"
                    :editor="editorRef"
                    :defaultConfig="toolbarConfig"
                    mode="default"
                  />
                  <!-- Markdown 源码查看按钮 -->
                  <!-- <el-button 
                    v-if="textContent"
                    size="small" 
                    @click="showMarkdownSource = !showMarkdownSource"
                  >
                    {{ showMarkdownSource ? '隐藏源码' : '查看源码' }}
                  </el-button> -->
                </div>
                
                <!-- Markdown 源码显示 -->
                <!-- <div v-if="showMarkdownSource" style="margin-bottom: 10px;">
                  <el-input
                    v-model="textContent"
                    type="textarea"
                    :rows="8"
                    placeholder="Markdown 源码"
                    readonly
                  />
                </div> -->
                
                <!-- 富文本编辑器 -->
                <Editor
                  v-if="!showMarkdownSource"
                  style="height: 400px; overflow-y: hidden;"
                  v-model="textContent"
                  :defaultConfig="editorConfig"
                  mode="default"
                  @onCreated="handleCreated"
                />
            </div>
            <!-- 在线编辑模式下隐藏提交按钮，改为在下一步中调用 -->
            <div style="display: flex; justify-content: flex-end; margin-top: 10px;">
              <el-button  @click="isEdtor = false">返回</el-button>
              <el-button  type="primary" @click="onLineSubmit">提交</el-button>
            </div>
          </div>
         
        </div>

        <!-- 分段设置步骤 -->
        <div v-if="currentStep === 1" class="segment-step">
          <div class="segment-options">
            <el-radio-group v-model="config.segmentMethod" class="segment-radio-group">
              <div class="radio-option">
                <el-radio value="normal" size="large">
                  <div class="radio-content">
                    <div class="radio-title">默认分段</div>
                    <div class="radio-description">平台默认的分段方式</div>
                  </div>
                </el-radio>
                
              </div>
              
              <div class="radio-option">
                <el-radio value="div" size="large">
                  <div class="radio-content">
                    <div class="radio-title">自定义分段</div>
                    <div class="radio-description">支持按需配置段落分割标则，查看操作指引</div>
                  </div>
                </el-radio>
              </div>
            </el-radio-group>
          </div>

          <!-- 自定义分段配置 -->
          <div style="margin-top: 20px" class="custom-segment-config">
            <div style=" padding: 20px 0;">
              <div class="config-section"  v-if="config.segmentMethod === 'div'">
                <div class="section-label" style="display: flex">
                  分段方式
                  <el-tooltip content="分段方式" placement="top">
                    <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
                  </el-tooltip>
                </div>
                <el-select
                    v-model="tagList"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    :reserve-keyword="false"
                    placeholder="请选择方式"
                    @change="handleTag"
                  >
                    <el-option
                      v-for="item in segmentMethods"
                      :key="item.value"
                      :label="item.name"
                      :value="item.value"
                    />
                </el-select>
                <div class="segment-methods" style="margin-top: 10px;">
                  <!-- <el-input-tag v-model="inputTags" draggable placeholder="可选择或输入多种分段符，支持按动态新排序，将按显示顺序处理分段" /> -->
                
                  <!-- <el-select
                    v-model="tagList"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    :reserve-keyword="false"
                    placeholder="请选择方式"
                    @change="handleTag"
                  >
                    <el-option
                      v-for="item in dynamicTags"
                      :key="item"
                      :label="item"
                      :value="item"
                    />
                  </el-select> -->
                </div>
              </div>

              <div class="config-section">
                <div class="section-label" style="display: flex">最大段落字符数<el-tooltip content="最大段落字符数" placement="top">
                    <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
                  </el-tooltip></div>
                <el-slider v-model="config.customSegmentSize" :marks="marks" :min="50" :max="1800" show-input />
              </div>

              <div class="config-section">
                <div class="section-label" style="display: flex">段落重叠字符数<el-tooltip content="段落重叠字符数" placement="top">
                    <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
                  </el-tooltip></div>
                <el-slider v-model="config.customSegmentOverlap"  :marks="marks1"  :min="1" :max="config.customSegmentSize" show-input />
              </div>
            </div>
          </div>
        </div>

        <!-- 分段编辑步骤 -->
        <div v-if="currentStep === 2" class="edit-step" v-loading="sseLoading">
          <div class="edit-step-left">
            <div style="font-size: 16px;margin-bottom: 10px; font-weight: 700">自动分段与清洗</div>
            <!-- <div style="color: #999999;margin-bottom: 10px;">文档列表</div> -->
            <div>
              <div v-for="(item, index) in tempFileData.knowledgeFileUploadTempDTOList" :key="index" class="text-ellipsis" 
              :class="{'step-active': index === currentFileId}" 
              style="height: 38px;line-height: 38px; padding: 0 10px; cursor: pointer;"
              @click="handleFileClick(item,index)"
              > {{ item.fileName }}</div>
            </div>
          </div>
          <div class="edit-step-center">
            <div style="padding-left: 20px;  margin-bottom: 10px; font-size: 16px; font-weight: 700;">原始文档预览</div>
            <!-- 文件预览容器 -->
            <div class="original-file" style="background: #f8f9fa; height: calc(100% - 30px);">
              <div v-if="originalFile" class="file-preview-container">
                <!-- 加载状态 -->
                <div v-if="isPreviewLoading" class="preview-loading">
                  <el-icon size="24" style="color: #409eff"><Loading /></el-icon>
                  <span style="margin-left: 8px;">正在加载预览...</span>
                </div>
                
                <template v-if="previewType">
                  <!-- PDF预览 -->
                  <div v-if="previewType === 'pdf'" class="pdf-preview">
                    <!-- PDF页面控制 -->
                    <div class="pdf-controls" v-if="totalPages > 0">
                      <div class="view-mode-switch">
                        <el-radio-group v-model="pdfViewMode" size="small">
                          <el-radio-button value="continuous">连续滚动</el-radio-button>
                          <el-radio-button value="paged">分页浏览</el-radio-button>
                        </el-radio-group>
                      </div>
                      
                      <div v-if="pdfViewMode === 'paged'" class="page-controls">
                        <el-button-group>
                          <el-button :disabled="currentPage <= 1" @click="currentPage--" icon="ArrowLeft">上一页</el-button>
                          <el-button disabled>{{ currentPage }} / {{ totalPages }}</el-button>
                          <el-button :disabled="currentPage >= totalPages" @click="currentPage++" icon="ArrowRight">下一页</el-button>
                        </el-button-group>
                      </div>
                      
                      <div v-else class="page-info">
                        <span>总共 {{ totalPages }} 页</span>
                      </div>
                    </div>
                    
                    <div class="pdf-content">
                      <vue-pdf-embed 
                        :source="originalFile"
                        :page="pdfViewMode === 'paged' ? currentPage : undefined"
                        annotation-layer
                        text-layer
                        @loading-failed="handlePreviewError"
                        @loaded="handlePdfLoaded"
                        @rendered="handlePdfRendered"
                      />
                    </div>
                  </div>
                  
                  <!-- DOCX预览 -->
                  <div v-else-if="previewType === 'docx'" class="docx-preview">
                    <div ref="docxPreviewContainer" class="preview-container docx-preview-container"></div>
                  </div>
                  
                  <!-- Markdown预览 -->
                  <v-md-preview 
                    v-else-if="previewType === 'md'" 
                    :text="markdownContent"
                    class="markdown-preview"
                  />
                  
                  <!-- 纯文本预览 -->
                  <div 
                    v-else-if="previewType === 'txt'" 
                    class="text-preview"
                  >
                    <pre>{{ textContent2 }}</pre>
                  </div>
                  
                  <!-- 图片预览 -->
                  <div 
                    v-else-if="previewType === 'image'" 
                    class="image-preview"
                  >
                    <img :src="originalFile" alt="预览图片" />
                  </div>
                  
                  <!-- 不支持的格式 -->
                  <div v-else class="unsupported-preview">
                    <el-icon size="48" style="color: #909399"><Document /></el-icon>
                    <p>暂不支持该文件格式的预览</p>
                    <p style="font-size: 12px; color: #909399;">支持格式：PDF、DOCX、MD、TXT、图片</p>
                  </div>
                </template>
              </div>
              <!-- 无文件时的占位 -->
              <div v-else class="no-file-preview">
                <el-icon size="48" style="color: #c0c4cc"><Document /></el-icon>
                <p style="color: #909399; margin-top: 16px;">请选择文件</p>
              </div>
            </div>
          </div>

          <div class="edit-step-right" style="margin-left: 5px;">
            <!-- <div style="padding: 0 20px 10px 20px; border-bottom: 1px solid #ddd; background: white;"> -->
              <div style="display: flex; justify-content: space-between; align-items: center; padding: 0 20px;">
                <span style="margin-bottom: 10px; font-size: 16px; font-weight: 700;">解析后文档内容</span>
                <div>
                  <el-button size="small" link @click="exportParsedContent">导出内容</el-button>
                  <el-button size="small" link @click="refreshParsedContent">刷新</el-button>
                </div>
              </div>
            <!-- </div> -->
            
            <div class="parsed-content-container" style="height: calc(100% - 30px); overflow-y: auto; background: #f8f9fa;">
              <!-- 解析后的段落列表 -->
              <div v-if="parsedSegments.length > 0" class="parsed-segments-list">
                <div 
                  v-for="(segment, index) in parsedSegments" 
                  :key="segment.index"
                  class="parsed-segment-item"
                  :class="{ 'segment-active': currentParsedSegmentIndex === index }"
                  @click="selectParsedSegment(index)"
                >
                  <div class="segment-header">
                    <div class="segment-info">
                      <span class="segment-index">段落 {{ segment.index + 1 }}</span>
                      <span class="segment-type" :class="`type-${segment.type}`">{{ getSegmentTypeLabel(segment.type) }}</span>
                      <span class="segment-length">{{ segment.length }} 字符</span>
                    </div>
                  </div>
                  
                  <!-- 段落内容 -->
                  <div class="segment-content">
                    <div v-if="segment.type === 'text'" class="text-content">
                      <pre style="white-space: pre-wrap; font-family: inherit; margin: 0;">{{ segment.content }}</pre>
                    </div>
                    <div v-else-if="segment.type === 'image'" class="image-content">
                      <img 
                        :src="extractImageSrc(segment.content)" 
                        :alt="extractImageAlt(segment.content)"
                        style="max-width: 100%; height: auto; border-radius: 4px;"
                        @error="handleImageError"
                      />
                      <p style="margin-top: 8px; font-size: 12px; color: #909399;">{{ extractImageAlt(segment.content) }}</p>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 无段落时的占位 -->
              <div v-else class="no-parsed-segments">
                <el-icon size="48" style="color: #c0c4cc"><Document /></el-icon>
                <p style="color: #909399; margin-top: 16px;">请选择文件</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="bottom-actions">
        <el-button 
          v-if="currentStep > 0" 
          :disabled="isSubmitting || (stepTwo && currentStep === 1)"
          @click="prevStep"
        >
          上一步
        </el-button>
        
        <el-button 
          v-if="currentStep < 2" 
          type="primary"
          :disabled="tempFileData.knowledgeFileUploadTempDTOList.length === 0 && props.uploadType !== 'online'" 
          @click="nextStep"
        >
          下一步
        </el-button>
        
        <el-button 
          v-if="currentStep === 2" 
          type="primary" 
          :disabled="isSubmitting || sseLoading"
          :loading="isSubmitting"
          @click="submitAll"
        >
          {{ sseLoading ? '处理中...' : isSubmitting ? '提交中...' : '提交' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { EventSourcePolyfill } from 'event-source-polyfill'
import { getToken } from '@/utils/auth';
import { triggerRef } from 'vue'
import SparkMD5 from 'spark-md5'
import {
  uploadFile,
  uploadFileChunk,
  mergeFileChunk,
  getTempFile, 
  createTempFile,
  getTempFileBySegment,
  uploadFileReturn, 
  deleteTempFile,
urlBatchDownload,
  onlineCreate,
  uploadFileImage
} from '@/api/base'
import { getFileTypeFn } from '@/utils'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { createEditor } from '@wangeditor/editor'

// 文件预览相关导入
import VuePdfEmbed from 'vue-pdf-embed'
import { renderAsync } from 'docx-preview'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import Prism from 'prismjs'

// 配置markdown编辑器
VMdPreview.use(vuepressTheme, {
  Prism,
})

const marks = {
  50: '50',
  1800: '1800'
}

const marks1 = {
  1: '1'
}
const tableLoading = ref(false)
const urlRess = ref('')  // url上传

const url = import.meta.env.VITE_APP_BASE_API
const originalFile = ref('') // 原始文件
const newOriginalFile = ref(null) // 解析后文件

const tagList = ref([])

const progressText = ref(0)

// 文件预览相关变量
const previewType = ref('') // 当前预览文件类型
const markdownContent = ref('') // markdown内容
const textContent2 = ref('') // 纯文本内容
const docxPreviewContainer = ref(null) // docx容器引用
const isPreviewLoading = ref(false) // 预览加载状态

// PDF预览相关变量
const currentPage = ref(1) // 当前页码
const totalPages = ref(0) // 总页数
const pdfViewMode = ref('continuous') // PDF查看模式：continuous(连续滚动) 或 paged(分页浏览)

// 解析后文件预览相关变量
const newPreviewType = ref('') // 解析后文件类型
const newMarkdownContent = ref('') // 解析后markdown内容
const newTextContent = ref('') // 解析后纯文本内容
const newDocxContainer = ref() // 解析后docx容器引用
const isNewPreviewLoading = ref(false) // 解析后文件预览加载状态

// 解析后段落数据相关变量
const parsedSegments = ref([]) // 解析后的段落数据
const currentParsedSegmentIndex = ref(-1) // 当前选中的解析后段落索引

// SSE连接实例
const eventSource = ref(null)

const props = defineProps({
    uploadType: {
        type: String,
        default: 'online'
    },
    id: {
        type: Number,
        default: 0
    },
    spaceId: {
        type: Number,
        default: 3
    },
    stepTwo: {
      type: Boolean,
      default: false
    },
    onLineObj: {
      type: Object,
      default: () => ({})
    }
})

// 当前步骤
const currentStep = ref(0)
const activeTab = ref('text')

const tempFileData = ref({
  knowledgeFileUploadTempDTOList: []
})  // 临时文件

const knowledgeFileUploadTempDTOList = reactive([])

const config = ref({
  segmentMethod: 'normal',
  splitMethod: '',
  customSegmentSize: 50,
  customSegmentOverlap: 1
})
// wangEditor 富文本编辑器相关
const editorRef = shallowRef()
const textContent = ref('')
const title = ref('')

const toolbarConfig = {
  // 工具栏配置
  toolbarKeys: [
    'headerSelect',
    'blockquote',
    '|',
    'bold',
    'italic',
    'underline',
    'through',
    'code',
    'sup',
    'sub',
    'clearStyle',
    '|',
    'color',
    'bgColor',
    '|',
    'fontSize',
    'fontFamily',
    'lineHeight',
    '|',
    'bulletedList',
    'numberedList',
    'todo',
    {
      key: 'group-justify',
      title: '对齐',
      iconSvg: '<svg viewBox="0 0 1024 1024"><path d="M768 793.6v102.4H51.2v-102.4h716.8z m204.8-230.4v102.4H51.2v-102.4h921.6z m-204.8-230.4v102.4H51.2v-102.4h716.8z m204.8-230.4v102.4H51.2v-102.4h921.6z"></path></svg>',
      menuKeys: ['justifyLeft', 'justifyRight', 'justifyCenter', 'justifyJustify']
    },
    '|',
    'emotion',
    'insertLink',
    {
      key: 'group-image',
      title: '图片',
      iconSvg: '<svg viewBox="0 0 1024 1024"><path d="M959.877 128l0.123 0.123v767.775l-0.123 0.122H64.102l-0.122-0.122V128.123l0.122-0.123h895.775zM960 64H64C28.795 64 0 92.795 0 128v768c0 35.205 28.795 64 64 64h896c35.205 0 64-28.795 64-64V128c0-35.205-28.795-64-64-64zM832 288.01c0 53.023-42.988 96.01-96.01 96.01s-96.01-42.987-96.01-96.01S682.967 192 735.99 192 832 234.988 832 288.01zM896 832H128V704l224.01-384 256 320h64l224.01-192V832z"></path></svg>',
      menuKeys: ['insertImage', 'uploadImage']
    },
    // {
    //   key: 'group-video',
    //   title: '视频',
    //   iconSvg: '<svg viewBox="0 0 1024 1024"><path d="M981.184 160.096C837.568 139.456 678.848 128 512 128S186.432 139.456 42.816 160.096C15.296 267.808 0 386.848 0 512s15.296 244.16 42.816 351.904C186.464 884.544 345.152 896 512 896s325.568-11.456 469.184-32.096C1008.704 756.192 1024 637.152 1024 512s-15.296-244.16-42.816-351.904zM384 704V320l320 192-320 192z"></path></svg>',
    //   menuKeys: ['insertVideo', 'uploadVideo']
    // },
    'insertTable',
    'codeBlock',
    'divider',
    '|',
    'undo',
    'redo',
    '|',
    'fullScreen'
  ]
}

const editorConfig = {
  placeholder: '请输入内容...',
  // 启用 Markdown 支持
  enableMarkdown: true,
  // 配置 Markdown 快捷键
  shortcuts: {
    'ctrl+b': 'bold',
    'ctrl+i': 'italic',
    'ctrl+u': 'underline',
    'ctrl+shift+`': 'code',
    'ctrl+shift+1': 'header1',
    'ctrl+shift+2': 'header2',
    'ctrl+shift+3': 'header3',
    'ctrl+shift+4': 'header4',
    'ctrl+shift+5': 'header5',
    'ctrl+shift+6': 'header6',
    'ctrl+shift+7': 'unorderedList',
    'ctrl+shift+8': 'orderedList',
    'ctrl+shift+9': 'blockquote',
    'ctrl+shift+0': 'codeBlock'
  },
  MENU_CONF: {
    // 配置上传图片
    uploadImage: {
      async customUpload(file, insertFn) {
        // file 是用户选择的图片文件
        const formData = new FormData()
        formData.append('file', file)

        // 调用你自己的上传接口
        const res = await uploadFileImage(formData)
        insertFn(res.data, '', res.data) // 插入图片
      }
    }
  }
}

// Univer 表格编辑器相关
const univerContainer = ref()
let univerInstance = null
let univerAPI = null

// 文件上传相关
const fileList = ref([])
const currentFileId = ref(null)

const sseLoading = ref(false)
const list = ref([])
const uploadedFiles = ref([])

// 分段设置相关
const segmentType = ref('custom')
const segmentMethods = ref([ {
  name: '双换行', 
  value: 'double_newline',
  type: false
}, {
  name: '换行',
  value: 'newline',
  type: false
}, {
  name: '空格',
  value: 'space',
  type: false
},
{
  name: '空字符',
  value: 'empty',
  type: false
}])
const submitSuccess = ref(false)
const isSubmitting = ref(false)
const inputVisible = ref(false)
const InputRef = ref(null)
const inputTags = ref([])
const customSegmentInput = ref('')
const maxCharacters = ref('')
const overlapCharacters = ref('')
const emit = defineEmits(['back'])

const currentFile = ref({})

const isEdtor = ref(false)
const showMarkdownSource = ref(false)
const fromFileMd5 = ref('')

const createNewDocument = () => {
  textContent.value = ''
  title.value = ''
  fromFileMd5.value = ''
  isEdtor.value = true
}

// 计算文件MD5
const calculateFileMD5 = (file) => {
  return new Promise((resolve, reject) => {
    const spark = new SparkMD5.ArrayBuffer()
    const fileReader = new FileReader()
    
    fileReader.onload = (e) => {
      spark.append(e.target.result)
      const md5 = spark.end()
      resolve(md5)
    }
    
    fileReader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    
    fileReader.readAsArrayBuffer(file)
  })
}

// 预览 Markdown 文件到编辑器
const previewMarkdownInEditor = async (fileUrl, val, fileMd5) => {
  try {
    const response = await fetch(fileUrl)
    if (!response.ok) {
      throw new Error(`HTTP错误! 状态: ${response.status}`)
    }
    title.value = val
    fromFileMd5.value = fileMd5
    const markdownText = await response.text()
    textContent.value = markdownText
    isEdtor.value = true
  } catch (error) {
    ElMessage.error('加载 Markdown 文件失败: ' + error.message)
  }
}

// 自定义上传方法 - 分片上传
const httpRequest = async (options) => {
  tableLoading.value = true
  const { file, onSuccess, onError, onProgress } = options
  
  try {
    const CHUNK_SIZE = 2 * 1024 * 1024; // 2MB 分片
    const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
    let uploadedChunks = 0;
    
    // 创建文件上传记录
    const fileRecord = {
      fileName: file.name,
      fileSize: file.size,
      parseResult: '计算文件MD5中...',
      summary: '',
      tags: '',
      id: file.uid || Date.now(),
      progress: 0,
      fileMd5: '',
      localSourcePath: '',
      createType: 'UPLOAD'
    }
    
    // 添加到临时文件列表
    tempFileData.value.knowledgeFileUploadTempDTOList.push(fileRecord)
    
    // 计算文件MD5
    const fileMd5 = await calculateFileMD5(file)
    console.log('文件MD5:', fileMd5)
    
    // 更新文件记录的MD5
    const fileIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.id === fileRecord.id);
    if (fileIndex !== -1) {
      tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex].fileMd5 = fileMd5;
      tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex].parseResult = '准备上传...';
    }
    
    // 分片上传
    let finalResponseData = null; // 保存最后一个分片的响应数据
    for (let i = 0; i < totalChunks; i++) {
      const start = i * CHUNK_SIZE;
      const end = Math.min(start + CHUNK_SIZE, file.size);
      const chunk = file.slice(start, end);
      const formData = new FormData();
      formData.append('file', chunk);
      formData.append('fileMd5', fileMd5);
      formData.append('chunkSize', chunk.size);
      formData.append('fileName', file.name);
      formData.append('chunkIndex', i);
      formData.append('chunkCount', totalChunks);
      try {       
        const response = await uploadFileChunk(formData)
        
        if (response.code === 'ok') {
          uploadedChunks++;
          
          // 计算进度
          const progress = Math.round((uploadedChunks / totalChunks) * 100);
          
          // 保存最后一个分片的响应数据
          if (progress === 100) {
            finalResponseData = response.data;
            // 所有分片上传完成，请求合并分片
            try {
              const mergeResponse = await mergeFileChunk({ fileMd5 })
              console.log('分片合并成功:', mergeResponse)
              
              // 替换原有的文件记录，而不是新增
              const fileIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.id === fileRecord.id);
              if (fileIndex !== -1) {
                tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex] = {
                  ...tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex],
                  fileMd5: fileMd5,
                  fileName: file.name,
                  fileSize: file.size,
                  parseResult: '上传完成',
                  localSourcePath: mergeResponse.data,
                  createType: 'UPLOAD',
                  fileType: file.type,
                  progress: 100
                }
              }
            } catch (mergeError) {
              console.error('分片合并失败:', mergeError)
              ElMessage.error('分片合并失败，请重试')
              // 合并失败时更新状态
              const fileIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.id === fileRecord.id);
              if (fileIndex !== -1) {
                tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex].parseResult = '合并失败';
              }
            }
          }
          
          // 更新文件记录 - 使用 Vue 的响应式更新
          const fileIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.id === fileRecord.id);
          if (fileIndex !== -1) {
            // 直接更新对象属性，reactive 会自动触发响应式更新
            tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex].progress = progress;
            tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex].parseResult = `上传中 ${progress}%`;
          }
    
          // 调用进度回调
          if (onProgress) {
            onProgress({
              percent: progress,
              loaded: uploadedChunks * CHUNK_SIZE,
              total: file.size
            });
          }
          
          console.log(`分片 ${i + 1}/${totalChunks} 上传成功，总进度: ${progress}%`)
        } else {
          throw new Error(response.message || '分片上传失败')
        }
      } catch (chunkError) {   
        // 更新文件状态为失败
        const fileIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.id === fileRecord.id);
        if (fileIndex !== -1) {
          tempFileData.value.knowledgeFileUploadTempDTOList[fileIndex].parseResult = '上传失败';
        }
        
        throw chunkError;
      }
    }
    
    // 所有分片上传完成后，处理最终响应数据
    if (finalResponseData) {
      console.log('分片上传完成', finalResponseData)
      // 更新临时文件数据
      tempFileData.value.knowledgeBaseId = props.id
      tempFileData.value.knowledgeSpaceId = props.spaceId
    }
    
    // 创建临时文件记录
    createTempFilefn();
    
    // // 调用成功回调
    // if (onSuccess) {
    //   onSuccess({
    //     code: 'ok',
    //     data: fileRecord
    //   }, file);
    // }
    
  } catch (error) { 
    // 调用错误回调
    if (onError) {
      onError(error, file);
    }
    ElMessage.error(`文件 ${file.name} 上传失败: ${error.message}`)
    tableLoading.value = false
  }
}

const createTempFilefn = () => {
  createTempFile(tempFileData.value).then(res => {
    tableLoading.value = false
  }).catch(error => {
    tableLoading.value = false
  })
}
const handleUploadSuccess = (response, file) => {
  // ElMessage.success(`文件 ${file.name} 上传成功`)

}
const handleUploadError = (error, file) => {
  // ElMessage.error(`文件 ${file.name} 上传失败`)
}
const handleRemove = (file, fileList) => {
  // 从列表中移除对应文件
  list.value = list.value.filter(item => item.name !== file.name)
  ElMessage.success('文件已移除')
}

const handleDelete = async (row) => {
  try {
    const res = await deleteTempFile(props.id, props.spaceId, row.fileMd5)
    ElMessage.success('文件已删除')
    await getTempFileData(props.id, props.spaceId)
  } catch (error) {
    console.error('删除文件失败:', error)
    ElMessage.error('删除文件失败')
  }
}
const getTempFileData = async (newId, newSpaceId) => {
  try {
    let creatrType = ''
    if (props.uploadType === 'url') {
       creatrType = 'URL'
    } else if (props.uploadType === 'file') {  
      creatrType = 'UPLOAD'
    } else {
       creatrType = 'ONLINE'
    }
    const res = await getTempFile(newId, newSpaceId, creatrType)
    console.log('res', res)
    if (res.data) {
      tempFileData.value = res.data
      config.value = {
        segmentMethod: res.data.knowLedgeSegmentConfig?.segmentMethod || 'normal',
        splitMethod: res.data.knowLedgeSegmentConfig?.splitMethod || '',
        customSegmentSize: res.data.knowLedgeSegmentConfig?.customSegmentSize || 50,
        customSegmentOverlap: res.data.knowLedgeSegmentConfig?.customSegmentOverlap || 1
      }
      if (!props.stepTwo) {
        tempFileData.value.knowledgeFileUploadTempDTOList = res.data.knowledgeFileUploadTempDTOList.filter(item => !item.fileId).map(item => {
          item.progress = 100
          return item
        })
      }
    
      if (config.value.splitMethod !== '') {
        tagList.value =  config.value.splitMethod.split(',')
      }
      if (config.value.segmentMethod === '') {
        config.value.segmentMethod = 'normal'
      }
    } else {
      tempFileData.value = {
        knowledgeBaseId: props.id,
        // spaceId: props.spaceId,
        knowledgeSpaceId: props.spaceId,
        knowledgeFileUploadTempDTOList: []
      }
    }
    return res
  } catch (error) {
    console.error('获取临时文件数据失败:', error)
    tempFileData.value = {
      knowledgeBaseId: props.id,
      // spaceId: props.spaceId,
      knowledgeSpaceId: props.spaceId,
      knowledgeFileUploadTempDTOList: []
    }
    throw error
  }
}

watch(() => [props.id, props.spaceId, props.stepTwo, props.onLineObj, props.uploadType], async ([newId, newSpaceId, newStepTwo, newOnLineObj, newUploadType]) => {
  if (newStepTwo) {
    currentStep.value = 1
  } else {
    currentStep.value = 0
  }
  
  if (newUploadType === 'online' && newOnLineObj) {

    title.value = newOnLineObj.fileName || ''
    textContent.value = newOnLineObj.content || ''

  }
  
  submitSuccess.value = false
  await getTempFileData(newId, newSpaceId)
}, {immediate: true})

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
  
  // 清理Univer
  try {
    if (univerInstance) {
      univerInstance.dispose()
      univerInstance = null
      univerAPI = null
    }
  } catch (error) {
  }
  
  // 清理SSE连接
  if (eventSource.value) {
    try {
      eventSource.value.close()
      eventSource.value = null
    } catch (error) {
    }
  }
})

// 监听tab切换
watch(activeTab, (newTab) => {
  if (newTab === 'table') {
    // 添加延迟确保DOM已渲染

    // setTimeout(() => {
    //   initUniver()
    // }, 300)
  }
}, { immediate: true })

// 事件处理
const goBack = () => {
  emit('back')
}

// 处理步骤点击
const handleStepClick = (stepIndex) => {
  // 如果当前在第二步（currentStep === 1），禁止点击所有步骤
  if (currentStep.value === 1) {
    ElMessage.warning('当前正在处理文件分段，无法切换步骤')
    return
  }
  
  // 其他情况允许跳转
  if (stepIndex < currentStep.value) {
    currentStep.value = stepIndex
  }
}

// 存储上次的配置和文件列表，用于判断是否有变化
const lastConfig = ref(null)
const lastFileList = ref([])

// 监听配置变化，当用户主动修改配置时清除缓存
watch(() => config.value, (newConfig, oldConfig) => {
  if (oldConfig && lastConfig.value) {
    const newConfigStr = JSON.stringify(newConfig)
    if (lastConfig.value !== newConfigStr) {
      // 配置发生变化，清除缓存以便重新处理
      lastConfig.value = null
      lastFileList.value = []
    }
  }
}, { deep: true })

// 监听文件列表变化
watch(() => tempFileData.value.knowledgeFileUploadTempDTOList, (newFileList, oldFileList) => {
  if (oldFileList && lastFileList.value.length > 0) {
    const newFileListStr = JSON.stringify(newFileList.map(file => ({
      fileName: file.fileName,
      fileMd5: file.fileMd5
    })))
    const lastFileListStr = JSON.stringify(lastFileList.value)
    if (newFileListStr !== lastFileListStr) {
      // 文件列表发生变化，清除缓存以便重新处理
      lastConfig.value = null
      lastFileList.value = []
    }
  }
}, { deep: true })

const nextStep = async () => {
  
  if (currentStep.value === 1) { 
    // 检查配置和文件是否有变化
    const currentConfigStr = JSON.stringify(config.value)
    const currentFileList = tempFileData.value.knowledgeFileUploadTempDTOList.map(file => ({
      fileName: file.fileName,
      fileMd5: file.fileMd5
    }))
    const currentFileListStr = JSON.stringify(currentFileList)
    
    // 判断是否需要重新处理
    const needReprocess = !lastConfig.value || 
                         lastConfig.value !== currentConfigStr || 
                         JSON.stringify(lastFileList.value) !== currentFileListStr ||
                         !tempFileData.value.knowledgeFileUploadTempDTOList.some(file => file.segmentedDocPath)
    
    if (needReprocess) {
      tempFileData.value.knowLedgeSegmentConfig = config.value
      let res = await createTempFile(tempFileData.value)
      sseLoading.value = true
      
      if (res.code === 'ok') {
        try {
          // 使用EventSourcePolyfill发送POST请求进行SSE连接
          eventSource.value = new EventSourcePolyfill(url + `/api/knowledge-file-segment/process_segment/${props.id}/${props.spaceId}`, {
            headers: {
              'Authorization': 'Bearer ' + getToken(),
            }
          })
          // 监听消息事件
          eventSource.value.addEventListener('progress', (event) => {
            let datas =  JSON.parse(event.data)
            // 如果进度是100了就关闭sse连接
            if (datas.overallProgress === 100) {
              eventSource.value.close()
              // 关闭sse连接后，获取文件分段数据
              ElMessage.success('所有文件分段处理成功')
              sseLoading.value = false
              
              // 获取最新数据并自动选择第一个文件
              getTempFileData(props.id, props.spaceId).then(() => {
                console.log('SSE处理完成，文件列表:', tempFileData.value.knowledgeFileUploadTempDTOList.length)
                // 数据更新后，自动选择第一个文件进行预览
                if (tempFileData.value.knowledgeFileUploadTempDTOList.length > 0) {
                  console.log('自动选择第一个文件:', tempFileData.value.knowledgeFileUploadTempDTOList[0].fileName)
                  nextTick(() => {
                    handleFileClick(tempFileData.value.knowledgeFileUploadTempDTOList[0], 0)
                  })
                }
              })
              
              // 保存当前配置和文件列表
              lastConfig.value = currentConfigStr
              lastFileList.value = currentFileList
            }
          })
          // 监听连接打开事件
          eventSource.value.addEventListener('open', (event) => {
            // ElMessage.success('开始处理文件分段...')
          })
          // 监听错误事件
          eventSource.value.addEventListener('error', (event) => {
            ElMessage.error('文件处理连接失败')
            sseLoading.value = false
            eventSource.value.close()
          })
          // 监听连接关闭事件
          eventSource.value.addEventListener('close', (event) => {
          })
          
        } catch (error) {
          ElMessage.error('无法建立文件处理连接')
          sseLoading.value = false
        }
      } else {
        sseLoading.value = false
      }
    } else {
      // 没有变化，直接使用现有数据
      // ElMessage.info('配置和文件未发生变化，使用现有分段数据')
      // 保存当前配置和文件列表（如果还没有保存的话）
      if (!lastConfig.value) {
        lastConfig.value = currentConfigStr
        lastFileList.value = currentFileList
      }
    }
  }
  
  if (currentStep.value < 2) {
    currentStep.value++
    
    // 如果进入分段编辑步骤且已有处理过的文件，恢复之前的选择
    if (currentStep.value === 2 && tempFileData.value.knowledgeFileUploadTempDTOList.length > 0) {
      // 如果之前有选择的文件，恢复选择状态
      if (currentFileId.value !== null && tempFileData.value.knowledgeFileUploadTempDTOList[currentFileId.value]) {
        const file = tempFileData.value.knowledgeFileUploadTempDTOList[currentFileId.value]
        nextTick(() => {
          handleFileClick(file, currentFileId.value)
        })
      } else if (!currentFile.value.fileName && tempFileData.value.knowledgeFileUploadTempDTOList[0]) {
        // 如果没有之前的选择，默认选择第一个文件
        nextTick(() => {
          handleFileClick(tempFileData.value.knowledgeFileUploadTempDTOList[0], 0)
        })
      }
    }
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    const previousStep = currentStep.value
    currentStep.value--
    
    // 如果从分段编辑步骤（步骤2）返回到分段设置步骤（步骤1），暂时清空预览内容
    // 但保留选择状态，以便下次进入时能恢复
    if (previousStep === 2 && currentStep.value === 1) {
      // 暂时清空预览内容，但不清空选择状态
      originalFile.value = ''
      newOriginalFile.value = ''
      parsedSegments.value = []
      currentParsedSegmentIndex.value = -1
      previewType.value = ''
      markdownContent.value = ''
      textContent2.value = ''
      // 清空docx容器内容
      if (docxPreviewContainer.value) {
        docxPreviewContainer.value.innerHTML = ''
      }
      // 重置PDF相关状态
      currentPage.value = 1
      totalPages.value = 0
      pdfViewMode.value = 'continuous'
      isPreviewLoading.value = false
      // 注意：不清空 currentFileId 和 currentFile，保留选择状态
    }
  }
}

const submitAll = () => {
  // 如果正在提交中，直接返回
  if (isSubmitting.value) {
    return
  }
  // 设置提交状态
  isSubmitting.value = true
  if (props.uploadType === 'online') {
    tempFileData.value.createType = 'ONLINE'
  } else if (props.uploadType === 'url') {
    tempFileData.value.createType = 'URL'
  } else {
    tempFileData.value.createType = 'UPLOAD'
  }
  // uploadFileReturn(tempFileData.value).then(res => {
  //   // 重置进度条
  //   ElMessage.success('提交成功')
  //   submitSuccess.value = true
  //   submitSuccess.value = true
  //   goBack()
  // }).catch(error => {
  //   // 失败时立即允许重新点击
  //   isSubmitting.value = false
  //   ElMessage.error('提交失败，请重试')
  // })

  // 不用等返回结果，直接跳
  uploadFileReturn(tempFileData.value)
  submitSuccess.value = true
  submitSuccess.value = true
  goBack()
}

// wangEditor 相关方法
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

// Univer 相关方法
const initUniver = () => {
  // 确保容器存在并且DOM已经渲染
  nextTick(() => {
    const container = document.getElementById('univer')
    if (!container) {
      console.error('Univer容器未找到')
      return
    }
    
    // 如果已经初始化过，先清理
    if (univerInstance) {
      univerInstance.dispose()
      univerInstance = null
      univerAPI = null
    }
    
    try {
      
      // 创建Univer实例
      const { univer, univerAPI: api } = createUniver({
        locale: LocaleType.ZH_CN,
        locales: {
          [LocaleType.ZH_CN]: merge(
            {},
            UniverPresetSheetsCoreZhCN,
          ),
        },
        theme: defaultTheme,
        presets: [
          UniverSheetsCorePreset({
            container: 'univer',
          }),
        ],
      })

      // 创建一个工作表
      api.createWorkbook({
        name: '在线表格编辑器',
        sheets: {
          sheet1: {
            id: 'sheet1',
            name: 'Sheet1',
            cellData: {
              0: {
                0: { v: '示例数据' },
                1: { v: '请开始编辑' },
              },
              1: {
                0: { v: 100 },
                1: { v: 200 },
              },
            },
            rowCount: 50,
            columnCount: 26,
          },
        },
        locale: LocaleType.ZH_CN,
        sheetOrder: ['sheet1'],
      })

      univerInstance = univer
      univerAPI = api
      
    } catch (error) {
      console.error('Univer 初始化失败:', error)
      ElMessage.error('表格编辑器初始化失败: ' + error.message)
    }
  })
}

const handleTag = (tag) => {
  config.value.splitMethod = tag.join(',')
}

const handleFileClick = (item, index) => {
  try {   
    // 更新选中状态
    currentFileId.value = index
    currentFile.value = item
    
    // 构建文件URL
    const fileUrl = url + item.localSourcePath.replace('uploadPath/', 'profile/')
    
    originalFile.value = fileUrl

    // 解析后文件预览和段落数据加载
    if (item.segmentedDocPath) {
      const newFileUrlJson = url + item.segmentedDocPath.replace('uploadPath/', 'profile/')
      newOriginalFile.value = newFileUrlJson
      // 加载解析后的段落数据
      loadParsedSegmentsFromUrl(newFileUrlJson)
    } else {
      // 清空段落数据
      parsedSegments.value = []
      currentParsedSegmentIndex.value = -1
    }
    console.log('newFileUrlJson', item.fileName ,fileUrl)
    // 获取文件类型
    const fileType = getFileType(fileUrl)
    
    // 立即设置预览类型
    previewType.value = fileType
    // 等待DOM更新
    nextTick(() => {
    })
    
    // 清空之前的内容
    markdownContent.value = ''
    textContent2.value = ''
    if (docxPreviewContainer.value) {
      docxPreviewContainer.value.innerHTML = ''
    }
    
    // 重置PDF相关状态
    currentPage.value = 1
    totalPages.value = 0
    pdfViewMode.value = 'continuous'
    
    // 根据文件类型处理
    switch (fileType) {
      case 'pdf':
        isPreviewLoading.value = false
        break
      case 'image':
        isPreviewLoading.value = false
        break
      case 'md':
      case 'txt':
      case 'docx':
        loadFileContent(fileUrl, fileType)
        break
      default:
        isPreviewLoading.value = false
        break
    }
  } catch (error) {
    console.error('文件点击处理错误:', error)
    isPreviewLoading.value = false
    // ElMessage.error('文件预览失败')
  }
}

// 获取文件扩展名
const getFileExtension = (filename) => {
  return filename.toLowerCase().split('.').pop()
}

// 获取文件类型
const getFileType = (filename) => {
  const ext = getFileExtension(filename)
  
  if (ext === 'pdf') return 'pdf'
  if (['doc', 'docx'].includes(ext)) return 'docx'
  if (['md', 'markdown'].includes(ext)) return 'md'
  if (['txt', 'text'].includes(ext)) return 'txt'
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)) return 'image'
  
  return 'unsupported'
}

// 加载文件内容
const loadFileContent = async (fileUrl, fileType) => {
  isPreviewLoading.value = true
  
  try {
    if (fileType === 'md' || fileType === 'txt') {
      // 加载文本内容
      const response = await fetch(fileUrl)
      
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      
      const text = await response.text()
      
      if (fileType === 'md') {
        markdownContent.value = text
      } else {
        textContent2.value = text
      }
    } else if (fileType === 'docx') {
      // 加载DOCX文件
      await nextTick()
      
      if (docxPreviewContainer.value) {
        try {
          const response = await fetch(fileUrl)
          
          if (!response.ok) {
            throw new Error(`HTTP错误! 状态: ${response.status}`)
          }
          
          const blob = await response.blob()
          const arrayBuffer = await blob.arrayBuffer()
          
          // 清空容器
          docxPreviewContainer.value.innerHTML = ''
          
          // 渲染DOCX
          await renderAsync(arrayBuffer, docxPreviewContainer.value, undefined, {
            className: 'docx-wrapper',
            inWrapper: true,
            ignoreWidth: false,
            ignoreHeight: false,
            ignoreFonts: false,
            breakPages: true,
            ignoreLastRenderedPageBreak: true,
            experimental: false,
            trimXmlDeclaration: true,
            useBase64URL: false,
            useMathMLPolyfill: false,
            showChanges: false,
            debug: false
          })
        } catch (docxError) {
          console.error('DOCX渲染错误:', docxError)
          ElMessage.error('DOCX文件预览失败: ' + docxError.message)
        }
      } else {
        ElMessage.error('DOCX预览容器未找到')
      }
    }
  } catch (error) {
    ElMessage.error('文件预览加载失败: ' + error.message)
  } finally {
    isPreviewLoading.value = false
  }
}

// 预览错误处理
const handlePreviewError = (error) => {
  ElMessage.error('文件预览失败，请检查文件是否损坏')
  isPreviewLoading.value = false
  // 不要改变previewType，保持当前文件类型，让用户看到错误信息
}

// PDF加载成功处理，获取总页数
const handlePdfLoaded = (pdf) => {
  if (pdf && pdf.numPages) {
    totalPages.value = pdf.numPages
  }
  isPreviewLoading.value = false
}

// PDF渲染完成处理
const handlePdfRendered = (e) => {
  // PDF渲染完成
}

const beforeUpload = (file) => {
  const allowedTypes = ['application/pdf', 'text/plain', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/msword', 'text/markdown']
  const isAllowedType = allowedTypes.includes(file.type)
  const isLt100M = file.size / 1024 / 1024 < 100
 
  // if (!isAllowedType) {
  //   ElMessage.error('只支持 PDF、TXT、DOC、DOCX、MD 格式的文件！')
  //   return false
  // }
  if (!isLt100M) {
    ElMessage.error('上传文件大小不能超过 100MB！')
    return false
  }
  
  // 检查上传文件数量
  if (fileList.value.length >= 15) {
    ElMessage.error('最多只能上传15个文件！')
    return false
  }

  return true
}

// 解析后段落数据处理函数
const loadParsedSegments = (segmentsData) => {
  if (Array.isArray(segmentsData)) {
    parsedSegments.value = segmentsData
    currentParsedSegmentIndex.value = -1
  } else {
    parsedSegments.value = []
  }
}

// 从URL加载解析后段落数据
const loadParsedSegmentsFromUrl = async (jsonUrl) => {
  try {
    
    const response = await fetch(jsonUrl)
    if (!response.ok) {
      throw new Error(`HTTP错误! 状态: ${response.status}`)
    }
    
    // 先获取响应文本
    const text = await response.text()
    
    // 如果文本为空，直接返回空数组
    if (!text.trim()) {
      parsedSegments.value = []
      currentParsedSegmentIndex.value = -1
      return
    }
    
    // 尝试解析JSON
    try {
      const jsonData = JSON.parse(text)
      
      // 直接处理JSON数据，每个对象的content作为一个段落
      if (Array.isArray(jsonData)) {
        parsedSegments.value = jsonData.map((item, index) => ({
          index: index,
          content: item.content || '',
          type: item.type || 'text',
          length: (item.content || '').length
        }))
      } else {
        parsedSegments.value = []
      }
    } catch (jsonError) {
      throw new Error('服务器返回的数据不是有效的JSON格式')
    }
    
    currentParsedSegmentIndex.value = -1
  } catch (error) {
    ElMessage.error('加载文档段落数据失败: ' + error.message)
    // 清空段落数据
    parsedSegments.value = []
    currentParsedSegmentIndex.value = -1
  }
}

const selectParsedSegment = (index) => {
  currentParsedSegmentIndex.value = index
}

const getSegmentTypeLabel = (type) => {
  const typeLabels = {
    'text': '文本',
    'image': '图片',
    'table': '表格',
    'heading': '标题',
    'list': '列表'
  }
  return typeLabels[type] || '其他'
}

const extractImageSrc = (content) => {
  // 从HTML img标签中提取src属性
  const match = content.match(/src="([^"]+)"/)
  return match ? match[1] : ''
}

const extractImageAlt = (content) => {
  // 从HTML img标签中提取alt属性
  const match = content.match(/alt="([^"]+)"/)
  return match ? match[1] : '图片'
}

const handleImageError = (event) => {
  console.error('图片加载失败:', event.target.src)
  event.target.style.display = 'none'
}

const exportParsedContent = () => {
  if (parsedSegments.value.length === 0) {
    ElMessage.warning('没有可导出的内容')
    return
  }
  
  // 生成导出内容
  let exportContent = ''
  parsedSegments.value.forEach((segment, index) => {
    exportContent += `段落 ${segment.index + 1} [${getSegmentTypeLabel(segment.type)}]\n`
    if (segment.type === 'text') {
      exportContent += segment.content + '\n\n'
    } else if (segment.type === 'image') {
      exportContent += `[图片: ${extractImageAlt(segment.content)}]\n\n`
    }
  })
  
  // 创建下载链接
  const blob = new Blob([exportContent], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `解析后文档内容_${new Date().toISOString().slice(0, 10)}.txt`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  
  ElMessage.success('内容导出成功')
}

const refreshParsedContent = () => {
  if (currentFile.value && currentFile.value.localSegmentPath) {
    // 重新加载解析后的数据
    ElMessage.info('正在刷新解析内容...')
    // 这里可以调用API重新获取数据，目前使用示例数据
    setTimeout(() => {
      loadParsedSegments(dataJson)
      ElMessage.success('内容刷新成功')
    }, 1000)
  } else {
    ElMessage.warning('没有可刷新的内容')
  }
}

// url上传

const urlSubmit = () => {
  if (urlRess.value.trim() === '') {
    ElMessage.warning('请输入URL')
    return
  }
  // 同时支持中文逗号和英文逗号分割
  let urlList = urlRess.value.split(/[,，]/)
  // 过滤掉空字符串和只包含空格的项
  urlList = urlList.filter(url => url.trim() !== '')
  
  if (urlList.length > 10) {
    ElMessage.warning('一次最多可输入10个URL')
    return
  }
  
  // 定义支持的文件格式
  const supportedFormats = ['pdf', 'txt', 'doc', 'docx', 'md', 'csv', 'pptx', 'xlsx', 'png', 'mp3', 'mp4', 'jpg']
  
  // 验证每个URL的文件格式
  for (let i = 0; i < urlList.length; i++) {
    const url = urlList[i].trim()
    const urlObj = new URL(url)
    const pathname = urlObj.pathname
    const extension = pathname.split('.').pop()?.toLowerCase()
    
    //if (!extension || !supportedFormats.includes(extension)) {
    //  ElMessage.error(`第${i + 1}个URL "${url}" 不属于文件格式`)
    //  return
    // }
  }
  
  let params = {
    fileList: urlList.map(i => {
      return {
        fileUrl: i.trim() // 去除每个URL首尾的空格
      }
    }),
    knowledgeBaseId: props.id,
    spaceId: props.spaceId
  }
  tableLoading.value = true
  urlBatchDownload(params).then(response => {
     if (response.code === 'ok') {
      urlRess.value = ''
      tempFileData.value.knowledgeBaseId = props.id
      tempFileData.value.knowledgeSpaceId = props.spaceId
      tempFileData.value.knowledgeFileUploadTempDTOList = tempFileData.value.knowledgeFileUploadTempDTOList.concat(response.data)
      createTempFilefn()
    } else {
      tableLoading.value = false
    }
  })
}

// 内部使用的在线提交方法，返回Promise用于async/await
const onLineSubmitInternal = async () => {
  // 添加安全检查，确保值存在
  let params = {
    knowledgeBaseId: props.id,
    spaceId: props.spaceId,
    fileName: title.value,
    fileContent: textContent.value,
    fileType: 'md',
    fromFileMd5: fromFileMd5.value
  }
  
  tableLoading.value = true
  
  try {
    const response = await onlineCreate(params)
    if (response.code === 'ok') {
      tempFileData.value.knowledgeBaseId = props.id
      tempFileData.value.knowledgeSpaceId = props.spaceId
      if ( fromFileMd5.value !== '') {
        // 替换文件fromFileMd5.value === response.data.fileMd5
        let ind = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(item => item.fileMd5 === fromFileMd5.value)
        if (ind !== -1) {
          tempFileData.value.knowledgeFileUploadTempDTOList[ind] = response.data
        } else {
          tempFileData.value.knowledgeFileUploadTempDTOList.push(response.data)
        }
      } else {
        tempFileData.value.knowledgeFileUploadTempDTOList.push(response.data)
      }
     
      // tempFileData.value.knowledgeFileUploadTempDTOList = [response.data]
      tableLoading.value = false
      isEdtor.value = false
      // ElMessage.success('在线文档创建成功')
      return response
    } else {
      tableLoading.value = false
      throw new Error(response.message || '提交失败')
    }
  } catch (error) {
    tableLoading.value = false
    ElMessage.error('提交失败: ' + (error.message || '未知错误'))
    throw error
  }
}

// 保留原有的onLineSubmit方法（如果需要的话）
const onLineSubmit = async () => {
  // 如果是在线编辑模式且当前在第一步（文件上传/编辑步骤），先提交在线内容
  if (!title.value || title.value.trim() === '') {
    ElMessage.warning('标题不能为空，请检查输入')
    return
  }
  if (!textContent.value || textContent.value.trim() === '') {
    ElMessage.warning('正文内容不能为空，请检查输入')
    return
  }
  await onLineSubmitInternal()
  createTempFilefn()
}

onMounted(() => {
  // 确保在线编辑模式下有初始值
  if (props.uploadType === 'online' && props.onLineObj) {
    title.value = props.onLineObj.fileName || ''
    textContent.value = props.onLineObj.content || ''
  }
})
</script>

<style scoped lang="scss">
@import '../knowledge.scss';

// 总体进度样式
.overall-progress {
  margin: 20px 0;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  
  .progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    font-weight: 500;
    color: #303133;
    
    .progress-info {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .file-count {
        font-size: 12px;
        color: #909399;
        font-weight: normal;
      }
    }
  }
}

// :deep(.el-progress-bar__outer) {
//   background: white;
//   border: 1px solid #A2BFFF;
// }

// 步骤数字样式
.step-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  background: #C0C4CC;
  color: white;
  transition: all 0.3s ease;
  
  &.active {
    background: #447DFB;
    color: white;
  }
}

// 步骤图标样式
.step-icon {
  img {
    width: 24px;
    height: 24px;
  }
}
:deep(.el-step__head.is-finish) {
  border-color:#447DFB;
}
.submit-step {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

// PDF预览样式
:deep(.pdf-preview) {
  width: 100%;
  height: 100%;
  overflow: auto;
  display: flex;
  flex-direction: column;
  
  .pdf-controls {
    padding: 12px 16px;
    background: white;
    border-bottom: 1px solid #e4e7ed;
    border-radius: 8px 8px 0 0;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .view-mode-switch {
      flex: 1;
    }
    
    .page-controls {
      flex: 1;
      text-align: center;
    }
    
    .page-info {
      flex: 1;
      text-align: right;
      color: #666;
      font-size: 14px;
    }
  }
  
  .pdf-content {
    flex: 1;
    overflow: auto;
    padding: 10px;
    background: #f5f5f5;
  }
}

/* PDF预览样式优化 */
:deep(.vue-pdf-embed) {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  
  .pdf-page {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-radius: 4px;
    margin-bottom: 20px;
  }
}

/* DOCX预览样式优化 */
.docx-preview {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  background: white;
  
  .preview-container {
    padding: 20px;
    min-height: 100%;
    width: 100%;
    
    &.docx-preview-container {
      :deep(.docx-wrapper) {
        width: 100% !important; /* 强制宽度100% */
        margin: 0 auto;
        background: white;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        
        .document-container {
          margin: 0;
          padding: 20px;
          width: 100% !important; /* 强制宽度100% */
          
          p {
            margin: 0;
            line-height: 1.5;
            white-space: normal !important; /* 允许文本换行 */
            word-wrap: break-word !important; /* 允许长单词换行 */
          }
          
          /* 确保所有内容都适应容器宽度 */
          table, img, div {
            max-width: 100% !important;
            width: auto !important;
            height: auto !important;
          }
        }
      }
    }
  }
}
:deep(.el-loading-mask) {
  background: rgba(255,255,255,0.9) !important;
}
.w-e-full-screen-container {
  z-index: 999;
}
</style>

