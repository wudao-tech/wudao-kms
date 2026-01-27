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
                支持 PDF、TXT、DOC、DOCX、MD, CSV, PPTX, XLSX, PNG, MP3, WAV  等最多可上传 15 个文件，每个文件不超过 20MB，PDF 最多 500 页
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
              
              <el-table-column prop="parseResult" v-if="props.uploadType === 'file'" label="上传进度">
                <template #default="scope">
                  <div>
                    <el-progress 
                      :percentage="scope.row.progress || 0" 
                      :stroke-width="16"
                      striped
                      :format="(percentage) => `${percentage}%`"
                      :status="scope.row.parseResult === '上传失败' ? 'exception' : 
                               scope.row.parseResult === '合并失败' ? 'exception' : 
                               scope.row.progress === 100 ? 'success' : undefined"
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
                </div>  
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
        <div v-if="currentStep === 1" class="segment-step" style="display: flex;">
            <div class="segment-step-item">
              <div style="margin-bottom: 20px;">
                <div>
                  解析设置:
                  <el-tooltip
                    content="设置文件解析方式，默认为通用解析方案，复杂文件可选增强解析方式"
                    placement="top"
                  >
                    <el-icon style="color:#D8D8D8; font-size: 14px;"><questionFilled /></el-icon>
                  </el-tooltip>
                </div>
                <div style="border: 1px solid #EEEEEE; padding: 3px 20px; margin-top: 10px;">
                  <el-radio-group v-model="config.pdfIncrease">
                    <el-radio :value="false">通用文档解析</el-radio>
                    <el-radio :value="true">增强文档解析</el-radio>
                  </el-radio-group>
                </div>
              </div>

              <div style="margin-bottom: 20px;">
                <div>
                  智能总结:
                  <el-tooltip
                    content="智能生成文章摘要和标签"
                    placement="top"
                  >
                    <el-icon style="color:#D8D8D8; font-size: 14px;"><questionFilled /></el-icon>
                  </el-tooltip>
                </div>
                <div  style="border: 1px solid #EEEEEE; padding: 3px 20px; margin-top: 10px;">
                    <el-checkbox v-model="config.summary" label="智能摘要"/>
                    <el-checkbox v-model="config.tags" label="智能标签" />
                </div>
              </div>

              <div style="margin-bottom: 20px;">
                <div>
                  索引增强:
                  <el-tooltip
                    content="设置文件解析方式，默认为通用解析方案，复杂文件可选增强解析方式"
                    placement="top"
                  >
                    <el-icon style="color:#D8D8D8; font-size: 14px;"><questionFilled /></el-icon>
                  </el-tooltip>
                </div>
                <div  style="border: 1px solid #EEEEEE; padding: 3px 20px; margin-top: 10px;">
                    <el-checkbox v-model="config.vlFlag" label="生成图片索引"/>
                    <!-- <el-checkbox v-model="config.generateTitleIndex" label="生成标题索引" /> -->
                </div>
              </div>
              <div style="display: flex; line-height: 32px;">
                <div style="width: 120px; ">
                  问答提取:
                  <el-tooltip
                    content="根据文档分块内容，智能提取问答对，回答精度较好，但可能会丢失部分信息"
                    placement="top"
                  >
                    <el-icon style="color:#D8D8D8; font-size: 14px;"><questionFilled /></el-icon>
                  </el-tooltip>
                </div>
                <el-switch v-model="config.qaExtract" @change="handleQaExtractChange"></el-switch>
              </div>
              <el-input v-model="config.qaExtractPrompt" v-if="config.qaExtract" placeholder="请输入匹配难度" type="textarea" :autosize="{minRows: 5, maxRows: 10}"></el-input>
            </div>

            <div class="segment-step-item">
              <div>
                分段设置:
                <el-tooltip
                  content="设置文件内容分块条件，便于候选内容召回"
                  placement="top"
                >
                  <el-icon style="color:#D8D8D8; font-size: 14px;"><questionFilled /></el-icon>
                </el-tooltip>
              </div>
              <div style="margin-top: 10px;">
                <div class="segment-options" style="margin-bottom: 0;">
                  <el-radio-group v-model="config.segmentMethod" class="segment-radio-group">
                    <div class="radio-option">
                      <el-radio value="normal">
                        <div class="radio-content">
                          <div class="radio-title">默认分段</div>
                          <div class="radio-description">平台默认的分段方式</div>
                        </div>
                      </el-radio>
                    </div>
                    <div class="radio-option" style="border-bottom: none;">
                      <el-radio value="div">
                        <div class="radio-content">
                          <div class="radio-title">自定义分段</div>
                          <div class="radio-description">支持按需配置段落分割标则，查看操作指引</div>
                        </div>
                      </el-radio>
                    </div>
                  </el-radio-group>
                </div>
                <div  class="custom-segment-config">
                  <div>
                    <el-radio-group v-model="config.segmentType" style="margin-bottom: 10px;" :disabled="config.segmentMethod !== 'div'">
                      <el-radio value="paragraph">按段落分块</el-radio>
                      <el-radio value="contentSize">按长度分块</el-radio>
                      <el-radio value="symbol">按指定分隔符分块</el-radio>
                    </el-radio-group>
                    <div v-if="config.segmentType === 'paragraph'">
                      <div style="font-size: 14px;margin-bottom: 5px;">最大段落深度</div>
                      <el-input-number :max="8" :min="1" controls-position="right" :disabled="config.segmentMethod !== 'div'" style="width: 100%"  v-model="config.maxParagraph"></el-input-number>
                    </div>
                    <div v-if="config.segmentType === 'symbol'" class="config-section">
                      <div class="section-label" style="display: flex; margin-bottom: 5px;">
                        分隔符
                        <el-tooltip content="允许自定义分隔符进行分块，通常用于已处理好的数据，使用特定的分隔符来精准分块" placement="top">
                          <el-icon style="color:#D8D8D8; font-size: 14px;"><question-filled /></el-icon>
                        </el-tooltip>
                      </div>
                      <el-select
                          v-model="tagList"
                          multiple
                          filterable
                          allow-create
                          style="width: 100%"
                          default-first-option
                          :reserve-keyword="false"
                          :disabled="config.segmentMethod !== 'div'"
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
                      </div>
                    </div>
                    <div v-if="config.segmentType !== 'symbol'" class="config-section" style="line-height: 32px; margin: 10px 0;">
                      <div class="section-label" style="display: flex; width: 116px;">分块大小<el-tooltip content="分块大小" placement="top">
                          <el-icon style="color:#D8D8D8; font-size: 14px;"><question-filled /></el-icon>
                        </el-tooltip></div>
                        <el-input-number :max="1800" :min="50" controls-position="right" :disabled="config.segmentMethod !== 'div'" style="width: 100%"  v-model="config.customSegmentSize"></el-input-number>
                      <!-- <el-slider v-model="config.customSegmentSize" :disabled="config.segmentMethod !== 'div'" style="width: 100%" :marks="marks" :min="50" :max="1800" show-input /> -->
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
        </div>

        <!-- 分段编辑步骤 -->
        <div v-if="currentStep === 2" class="edit-step">
          <div class="edit-step-left">
            <div style="font-size: 16px;margin-bottom: 10px; font-weight: 700">文件列表</div>
            <!-- <div style="color: #999999;margin-bottom: 10px;">文档列表</div> -->
            <div>
              <div v-for="(item, index) in tempFileData.knowledgeFileUploadTempDTOList" :key="index"
              :class="{'step-active': index === currentFileId}" 
              style="height: 38px;line-height: 38px; padding: 0 10px;"
              @click="handleFileClick(item,index)"
              > 
                <el-tooltip :content="item.fileName" placement="right">
                  <span style="width: 100%; display: inline-block; cursor: pointer;" class="text-ellipsis" >{{ item.fileName }}</span>
                </el-tooltip>
              </div>
            </div>
          </div>

          <div class="edit-step-center">
            <div style="padding-left: 20px;  margin-bottom: 10px; font-size: 16px; font-weight: 700; padding-top: 10px;">原始文档预览</div>
            <!-- 文件预览容器 -->
            <div class="original-file" style="background: #f8f9fa; height: calc(100% - 41px);">
              <FilePreview 
                :file-url="originalFile" 
                :file-name="currentFile.fileName"
                placeholder="请选择文件"
                @download="viewOriginalFile"
              />
            </div>
          </div>

          <div class="edit-step-right" style="margin-left: 5px;">
           
            <div style="display: flex; justify-content: space-between; align-items: center; padding: 0 20px; padding-top: 10px;">
              <span style="margin-bottom: 10px; font-size: 16px; font-weight: 700;">解析后文档内容</span>
              <div>
                <span v-if="segmentCount > 0" style="font-size: 14px; color: #606266;">{{ segmentCount }} 段落</span>
               
              </div>
            </div>
            
            <div class="parsed-content-container" style="height: calc(100% - 41px); overflow-y: auto; background: #f8f9fa;" v-loading="sseLoading" element-loading-text="文件正在解析，请勿离开">
              <parseMd 
                v-if="sseCompleted"
                ref="parseMdRef"
                :knowledgeBaseId="tempFileData.knowledgeBaseId" 
                :knowledgeSpaceId="tempFileData.knowledgeSpaceId" 
                :fileMd5="currentFile.fileMd5"
                :sseCompleted="sseCompleted"
              />
              <div v-else style="display: flex; justify-content: center; align-items: center; height: 100%;">
                <el-button link icon="Pointer" style="font-size: 24px;" @click="sseAnalysis">预览</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="bottom-actions">
        <el-button 
          v-if="currentStep > 0" 
          :disabled="isSubmitting || (stepTwo && currentStep === 1) || sseLoading"
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
import { triggerRef, computed, watch } from 'vue'
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
import FilePreview from '@/components/FilePreview/index.vue'

import parseMd from './parseMd.vue'

const marks = {
  50: '50',
  1800: '1800'
}

const tableLoading = ref(false)
const urlRess = ref('')  // url上传

const url = import.meta.env.VITE_APP_BASE_API
const originalFile = ref('') // 原始文件URL

const tagList = ref([])

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


// 默认配置
const defaultConfig = {
  segmentMethod: 'normal',
  splitMethod: '',
  customSegmentSize: 800,
  customSegmentOverlap: 50,
  qaExtract: false,
  maxParagraph: 5,
  summary: true,
  tags: true,
  vlFlag: false,
  segmentType: 'paragraph',
  pdfIncrease: false,
  extraChangeFlag: true, // 检测pdfIncrease是否发生了更改
  qaExtractPrompt: '', // 初始为空，因为qaExtract默认为false
}

const config = ref({ ...defaultConfig })

// 存储上一次的pdfIncrease值，用于检测变化
const lastPdfIncrease = ref(null)

// 标记文件列表是否发生了变化（新增或删除）
const fileListChanged = ref(false)

// 批量上传时的防抖定时器
let createTempFileTimer = null

// 上传队列和状态管理
const uploadQueue = ref([]) // 待上传文件队列
const isUploading = ref(false) // 是否正在上传
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


// 文件上传相关
const fileList = ref([])
const currentFileId = ref(null)

const sseLoading = ref(false)
const sseCompleted = ref(false) // SSE是否已完成
const processedFileMd5List = ref([]) // 已处理过的文件MD5列表（缓存）

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
const isSubmitting = ref(false)
const emit = defineEmits(['back'])

const currentFile = ref({})

const isEdtor = ref(false)
const fromFileMd5 = ref('')

// parseMd 组件引用
const parseMdRef = ref(null)

// 段落数量
const segmentCount = ref(0)

// 监听 segments 变化
watch(() => {
  // 访问 segments ref 的值
  const segments = parseMdRef.value?.segments
  return segments?.value || segments
}, (segments) => {
  if (segments && Array.isArray(segments)) {
    segmentCount.value = segments.length
  } else {
    segmentCount.value = 0
  }
}, { deep: true, immediate: true })

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

// 处理上传队列中的下一个文件
const processNextUpload = async () => {
  if (uploadQueue.value.length === 0) {
    isUploading.value = false
    tableLoading.value = false
    return
  }
  
  const nextFile = uploadQueue.value.shift()
  if (nextFile) {
    try {
      await httpRequestInternal(nextFile)
    } catch (error) {
      // 即使当前文件上传失败，也继续处理队列中的下一个文件
      console.error('文件上传失败，继续处理队列:', error)
    }
    // 当前文件完成后（无论成功或失败），继续处理下一个
    await processNextUpload()
  }
}

// 自定义上传方法 - 分片上传（内部实现）
const httpRequestInternal = async (options) => {
  const { file, onSuccess, onError, onProgress, tempRecordId } = options
  
  try {
    const CHUNK_SIZE = 2 * 1024 * 1024; // 2MB 分片
    const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
    let uploadedChunks = 0;
    
    // 找到之前创建的临时记录
    const tempRecordIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.id === tempRecordId)
    if (tempRecordIndex === -1) {
      throw new Error('找不到文件记录')
    }
    
    // 从记录中获取已计算的MD5（在httpRequest中已经计算过了）
    let fileMd5 = tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].fileMd5
    
    // 如果记录中没有MD5（兜底情况），则计算
    if (!fileMd5) {
      // 更新状态为"计算MD5..."
      tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].parseResult = '计算MD5...'
      fileMd5 = await calculateFileMD5(file)
      // 更新MD5到记录中
      tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].fileMd5 = fileMd5
    }
    
    // 再次检查文件是否已上传过（作为兜底检查，但排除当前记录）
    const existingFile = tempFileData.value.knowledgeFileUploadTempDTOList.find(
      f => f.fileMd5 === fileMd5 && f.id !== tempRecordId && f.localSourcePath
    )
    if (existingFile) {
      // 如果检测到重复，从列表中移除当前记录
      tempFileData.value.knowledgeFileUploadTempDTOList.splice(tempRecordIndex, 1)
      
      ElMessage.warning(`文件 "${file.name}" 已上传过，请勿重复上传`)
      if (onError) {
        onError(new Error('文件已上传过'), file)
      }
      throw new Error('文件已上传过')
    }
    
    // 更新状态为"准备上传..."
    tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].parseResult = '准备上传...'
    
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
              // 更新状态为"合并中..."
              tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].parseResult = '合并中...'
              
              const mergeResponse = await mergeFileChunk({ fileMd5 })
              
              // 更新文件记录为完成状态
              tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex] = {
                ...tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex],
                fileMd5: fileMd5,
                fileName: file.name,
                fileSize: file.size,
                parseResult: '上传完成',
                localSourcePath: mergeResponse.data,
                createType: 'UPLOAD',
                fileType: file.type,
                progress: 100
              }
            } catch (mergeError) {
              ElMessage.error('分片合并失败，请重试')
              // 合并失败时更新状态
              tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].parseResult = '合并失败'
              tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].progress = 0
              throw mergeError
            }
          }
          
          // 更新进度
          tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].progress = progress
          tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].parseResult = progress === 100 ? '上传完成' : `上传中 ${progress}%`
    
          // 调用进度回调
          if (onProgress) {
            onProgress({
              percent: progress,
              loaded: uploadedChunks * CHUNK_SIZE,
              total: file.size
            });
          }
        } else {
          throw new Error(response.message || '分片上传失败')
        }
      } catch (chunkError) {   
        // 更新文件状态为失败
        tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].parseResult = '上传失败'
        tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].progress = 0
        
        throw chunkError;
      }
    }
    
    // 所有分片上传完成后，处理最终响应数据
    if (finalResponseData) {
      // 更新临时文件数据
      tempFileData.value.knowledgeBaseId = props.id
      tempFileData.value.knowledgeSpaceId = props.spaceId
    }
    
    // 批量上传时，使用防抖机制延迟调用 createTempFilefn
    // 避免每个文件完成后立即调用，导致覆盖其他正在上传的文件状态
    // 使用 nextTick 确保当前文件的 localSourcePath 已经更新到列表中
    nextTick(() => {
      // 清除之前的定时器
      if (createTempFileTimer) {
        clearTimeout(createTempFileTimer)
      }
      
      // 延迟 500ms 调用，如果在这期间有其他文件完成，会重新计时
      // 这样可以确保在批量上传时，所有文件都完成后再统一调用一次
      createTempFileTimer = setTimeout(() => {
        createTempFilefn()
        createTempFileTimer = null
      }, 500)
    })
    
    // // 调用成功回调
    // if (onSuccess) {
    //   onSuccess({
    //     code: 'ok',
    //     data: fileRecord
    //   }, file);
    // }
    
  } catch (error) {
    // 确保文件状态被标记为失败
    if (tempRecordId) {
      const tempRecordIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.id === tempRecordId)
      if (tempRecordIndex !== -1) {
        tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].parseResult = '上传失败'
        tempFileData.value.knowledgeFileUploadTempDTOList[tempRecordIndex].progress = 0
      }
    }
    
    // 调用错误回调
    if (onError) {
      onError(error, file);
    }
    
    // 只有在非重复文件的情况下才显示错误消息
    if (!error.message?.includes('已上传过') && !error.message?.includes('文件重复')) {
      ElMessage.error(`文件 ${file.name} 上传失败: ${error.message}`)
    }
    
    // 注意：不在这里设置 tableLoading.value = false，因为可能还有队列中的文件
    // 抛出错误，让 processNextUpload 捕获
    throw error
  }
}

// 自定义上传方法 - 入口函数，负责队列管理
const httpRequest = async (options) => {
  const { file } = options
  console.log(123, file)
  
  // 先计算文件MD5，检查是否重复
  try {
    const fileMd5 = await calculateFileMD5(file)
    
    // 检查文件是否已上传过（通过MD5判断，且已有localSourcePath表示已上传成功）
    const existingFile = tempFileData.value.knowledgeFileUploadTempDTOList.find(
      f => f.fileMd5 === fileMd5 && f.localSourcePath
    )
    
    if (existingFile) {
      ElMessage.warning(`文件 "${file.name}" 已上传过，请勿重复上传`)
      if (options.onError) {
        options.onError(new Error('文件已上传过'), file)
      }
      return // 直接返回，不添加到列表
    }
    
    // 文件不重复，创建临时记录并添加到列表
    const tempFileRecord = {
      fileName: file.name,
      fileSize: file.size,
      parseResult: '等待上传',
      summary: '',
      tags: '',
      id: file.uid || `${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      progress: 0,
      fileMd5: fileMd5, // 已经计算好的MD5
      localSourcePath: '',
      createType: 'UPLOAD'
    }
    
    // 添加到列表显示
    tempFileData.value.knowledgeFileUploadTempDTOList.push(tempFileRecord)
    
    // 将文件和临时记录ID都存入队列
    uploadQueue.value.push({
      ...options,
      tempRecordId: tempFileRecord.id
    })
    
    // 如果当前没有在上传，开始处理队列
    if (!isUploading.value) {
      isUploading.value = true
      tableLoading.value = true
      await processNextUpload()
    }
    // 如果正在上传，文件已经在队列中，等待处理
  } catch (error) {
    // MD5计算失败或其他错误
    ElMessage.error(`文件 "${file.name}" 处理失败: ${error.message}`)
    if (options.onError) {
      options.onError(error, file)
    }
  }
}

const createTempFilefn = () => {
  createTempFile(tempFileData.value).then(res => {
    // 注意：不在这里设置 tableLoading.value = false
    // 因为可能还有队列中的文件正在上传，由 processNextUpload 统一管理
  }).catch(error => {
    // 错误时也不关闭 loading，让队列继续处理
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
    if (res.data) {
      // 保存旧的文件列表，用于检测变化
      const oldFileList = tempFileData.value.knowledgeFileUploadTempDTOList || []
      
      tempFileData.value = res.data
      
      // 获取新文件列表
      const newFileList = res.data.knowledgeFileUploadTempDTOList || []
      
      // 检测是否有文件被删除或新增
      const oldFileMd5Set = new Set(oldFileList.map(file => file.fileMd5))
      const newFileMd5Set = new Set(newFileList.map(file => file.fileMd5))
      const hasDeleted = oldFileList.some(file => !newFileMd5Set.has(file.fileMd5))
      const hasAdded = newFileList.some(file => !oldFileMd5Set.has(file.fileMd5))
      
      // 如果文件列表发生了变化（新增或删除），重置config到默认值
      if (hasDeleted || hasAdded || (oldFileList.length === 0 && newFileList.length > 0)) {
        fileListChanged.value = true
        config.value = { ...defaultConfig }
        lastPdfIncrease.value = null
        tagList.value = []
        // 清除本地存储
        clearConfigStorage()
      } else {
        fileListChanged.value = false
      }
      
      // 如果文件列表发生了变化（新增或删除），不使用后端配置，保持默认配置
      // 如果 onLineObj 中已经有 knowledgeSegmentConfig，则不覆盖（已经在 watch 中处理）
      // 否则使用后端返回的配置
      if (!fileListChanged.value && !props.onLineObj?.knowledgeSegmentConfig) {
        config.value = {
          ...config.value,
          ...res.data.knowLedgeSegmentConfig,
          // segmentMethod: res.data.knowLedgeSegmentConfig?.segmentMethod || 'normal',
          // splitMethod: res.data.knowLedgeSegmentConfig?.splitMethod || '',
          // customSegmentSize: res.data.knowLedgeSegmentConfig?.customSegmentSize || 800,
          // customSegmentOverlap: res.data.knowLedgeSegmentConfig?.customSegmentOverlap || 1
        }
        
        // 确保qaExtractPrompt与qaExtract状态一致
        if (!config.value.qaExtract) {
          config.value.qaExtractPrompt = ''
        }
        
        if (config.value.splitMethod !== '') {
          tagList.value =  config.value.splitMethod.split(',')
        }
        if (config.value.segmentMethod === '') {
          config.value.segmentMethod = 'normal'
        }
      }
      
      if (!props.stepTwo) {
        tempFileData.value.knowledgeFileUploadTempDTOList = res.data.knowledgeFileUploadTempDTOList.filter(item => !item.fileId).map(item => {
          item.progress = 100
          return item
        })
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
  
  // 如果 onLineObj 中包含 knowledgeSegmentConfig，优先使用它来初始化 config
  if (newOnLineObj && newOnLineObj.knowledgeSegmentConfig) {
    config.value = {
      ...config.value,
      ...newOnLineObj.knowledgeSegmentConfig
    }
    
    // 确保qaExtractPrompt与qaExtract状态一致
    if (!config.value.qaExtract) {
      config.value.qaExtractPrompt = ''
    }
    
    // 处理分隔符
    if (config.value.splitMethod) {
      tagList.value = config.value.splitMethod.split(',')
    }
    
    // 确保 segmentMethod 有默认值
    if (!config.value.segmentMethod) {
      config.value.segmentMethod = 'normal'
    }
  }
  
  await getTempFileData(newId, newSpaceId)
}, {immediate: true})

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
  
  // 清理上传队列
  uploadQueue.value = []
  isUploading.value = false
  
  // 清理批量上传的防抖定时器
  if (createTempFileTimer) {
    clearTimeout(createTempFileTimer)
    createTempFileTimer = null
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


// 事件处理
const goBack = () => {
  emit('back')
}


// 本地存储的 key，用于保存上次的配置
const getStorageKey = () => {
  return `fileUpload_lastConfig_${props.id}_${props.spaceId}`
}

// 保存配置到本地存储
const saveConfigToStorage = (config, fileList) => {
  // 只保存 defaultConfig 中定义的字段，避免后端返回的额外字段污染比较逻辑
  const configForCompare = {
    segmentMethod: config.segmentMethod,
    splitMethod: config.splitMethod,
    customSegmentSize: config.customSegmentSize,
    customSegmentOverlap: config.customSegmentOverlap,
    qaExtract: config.qaExtract,
    maxParagraph: config.maxParagraph,
    summary: config.summary,
    tags: config.tags,
    vlFlag: config.vlFlag,
    segmentType: config.segmentType,
    pdfIncrease: config.pdfIncrease,
    qaExtractPrompt: config.qaExtractPrompt
    // 注意：不保存 extraChangeFlag，因为这是计算出来的
  }
  
  const data = {
    config: configForCompare,
    fileList: fileList.map(file => ({
      fileName: file.fileName,
      fileMd5: file.fileMd5
    })),
    timestamp: Date.now()
  }
  const storageKey = getStorageKey()
  localStorage.setItem(storageKey, JSON.stringify(data))
}

// 从本地存储读取配置
const getConfigFromStorage = () => {
  try {
    const data = localStorage.getItem(getStorageKey())
    return data ? JSON.parse(data) : null
  } catch (error) {
    return null
  }
}

// 清除本地存储的配置
const clearConfigStorage = () => {
  const storageKey = getStorageKey()
  localStorage.removeItem(storageKey)
}

// 生成过滤后的分段配置
const getFilteredSegmentConfig = () => {
  const baseConfig = {
    segmentMethod: config.value.segmentMethod,
    customSegmentSize: config.value.customSegmentSize || 800,
    qaExtract: config.value.qaExtract,
    qaExtractPrompt: config.value.qaExtractPrompt,
    summary: config.value.summary,
    tags: config.value.tags,
    vlFlag: config.value.vlFlag,
    // generateTitleIndex: config.value.generateTitleIndex,
    pdfIncrease: config.value.pdfIncrease,
    extraChangeFlag: true
  }

  if (config.value.segmentMethod === 'normal') {
    // 默认分段模式：只保留基本字段
    return baseConfig
  } else if (config.value.segmentMethod === 'div') {
    // 自定义分段模式：根据segmentType添加相应字段
    const customConfig = { ...baseConfig }
    
    if (config.value.segmentType === 'paragraph') {
      // 按段落分块：添加maxParagraph
      customConfig.maxParagraph = config.value.maxParagraph
    } else if (config.value.segmentType === 'contentSize') {
      // 按长度分块：只保留customSegmentSize
      // 不需要额外字段
    } else if (config.value.segmentType === 'symbol') {
      // 按分隔符分块：添加splitMethod
      customConfig.splitMethod = config.value.splitMethod
    }
    
    return customConfig
  }
  
  return baseConfig
}

// 监听pdfIncrease变化（已移除extraChangeFlag相关逻辑，默认始终为true）

// 监听segmentMethod变化，实时过滤配置
watch(() => config.value.segmentMethod, (newMethod, oldMethod) => {
  if (oldMethod && newMethod !== oldMethod) {
    // segmentMethod发生变化时，重置相关字段
    if (newMethod === 'normal') {
      // 切换到默认分段：移除自定义分段相关字段
      config.value.segmentType = 'paragraph' // 重置为默认值
      config.value.maxParagraph = 5 // 重置为默认值
      config.value.splitMethod = '' // 清空分隔符
      config.value.customSegmentSize = config.value.customSegmentSize || 800 // 确保有默认值
    } else if (newMethod === 'div') {
      // 切换到自定义分段：根据当前segmentType设置默认值
      if (config.value.segmentType === 'paragraph') {
        config.value.maxParagraph = config.value.maxParagraph || 5
        config.value.customSegmentSize = config.value.customSegmentSize || 800
      } else if (config.value.segmentType === 'contentSize') {
        config.value.customSegmentSize = config.value.customSegmentSize || 800
      } else if (config.value.segmentType === 'symbol') {
        config.value.splitMethod = config.value.splitMethod || ''
        config.value.customSegmentSize = config.value.customSegmentSize || 800
      }
    }
  }
})

// 监听segmentType变化，重置相关字段
watch(() => config.value.segmentType, (newType, oldType) => {
  if (oldType && newType !== oldType && config.value.segmentMethod === 'div') {
    // 只在自定义分段模式下处理segmentType变化
    if (newType === 'paragraph') {
      // 按段落分块：重置分隔符和分块大小
      config.value.splitMethod = ''
      config.value.customSegmentSize = config.value.customSegmentSize || 800
      config.value.maxParagraph = config.value.maxParagraph || 5
    } else if (newType === 'contentSize') {
      // 按长度分块：重置分隔符和段落深度
      config.value.splitMethod = ''
      config.value.maxParagraph = null
      config.value.customSegmentSize = config.value.customSegmentSize || 800
    } else if (newType === 'symbol') {
      // 按分隔符分块：重置分块大小和段落深度
      config.value.customSegmentSize = config.value.customSegmentSize || 800
      config.value.maxParagraph = null
    }
  }
})

// 注意：不需要监听配置变化来清除本地存储
// 原因：
// 1. 文件列表变化时已经会清除配置（见下方的文件列表监听器）
// 2. nextStep 中会自动比较配置变化并判断是否需要重新处理
// 3. 过于激进的监听器会误清除刚保存的配置
// 
// 如果用户手动修改配置后再点击下一步：
// - nextStep 会检测到配置与本地存储不同
// - 判断需要重新处理并触发处理流程
// - 处理完成后保存新配置作为下次的基准
// 
// 注意：extraChangeFlag 始终为 true，不再进行判断

// 监听文件列表变化
watch(() => tempFileData.value.knowledgeFileUploadTempDTOList, (newFileList, oldFileList) => {
  if (oldFileList && oldFileList.length > 0) {
    // 获取旧文件列表的MD5集合
    const oldFileMd5Set = new Set(oldFileList.map(file => file.fileMd5))
    // 获取新文件列表的MD5集合
    const newFileMd5Set = new Set(newFileList.map(file => file.fileMd5))
    
    // 检测是否有文件被删除或新增
    const hasDeleted = oldFileList.some(file => !newFileMd5Set.has(file.fileMd5))
    const hasAdded = newFileList.some(file => !oldFileMd5Set.has(file.fileMd5))
    
    // 如果有文件被删除或新增，重置config到默认值
    if (hasDeleted || hasAdded) {
      // 标记文件列表已变化
      fileListChanged.value = true
      // 重置config到默认值
      config.value = { ...defaultConfig }
      // 重置lastPdfIncrease
      lastPdfIncrease.value = null
      // 清除本地存储
      clearConfigStorage()
      // 重置tagList
      tagList.value = []
    } else {
      // 文件没有新增或删除，只是其他属性变化（如segmentedDocPath等）
      // 不清除本地存储，因为配置本身没有变化
      // 重置标志
      fileListChanged.value = false
    }
  } else if (oldFileList && oldFileList.length === 0 && newFileList.length > 0) {
    // 从空列表到有文件，重置config
    fileListChanged.value = true
    config.value = { ...defaultConfig }
    lastPdfIncrease.value = null
    tagList.value = []
    // 清除本地存储
    clearConfigStorage()
  } else {
    // 重置标志
    fileListChanged.value = false
  }
}, { deep: true })

const nextStep = async () => {
  if (currentStep.value === 1) { 
    const savedData = getConfigFromStorage()
    const configForCompare = {
      segmentMethod: config.value.segmentMethod,
      splitMethod: config.value.splitMethod,
      customSegmentSize: config.value.customSegmentSize,
      customSegmentOverlap: config.value.customSegmentOverlap,
      qaExtract: config.value.qaExtract,
      maxParagraph: config.value.maxParagraph,
      summary: config.value.summary,
      tags: config.value.tags,
      vlFlag: config.value.vlFlag,
      segmentType: config.value.segmentType,
      pdfIncrease: config.value.pdfIncrease,
      qaExtractPrompt: config.value.qaExtractPrompt
    }
    const currentConfigStr = JSON.stringify(configForCompare)
    
    const currentFileList = tempFileData.value.knowledgeFileUploadTempDTOList.map(file => ({
      fileName: file.fileName,
      fileMd5: file.fileMd5
    }))
    const currentFileListStr = JSON.stringify(currentFileList)
    
    let configChanged = true
    if (savedData) {
      const savedConfigStr = JSON.stringify(savedData.config)
      const savedFileListStr = JSON.stringify(savedData.fileList)
      configChanged = savedConfigStr !== currentConfigStr || savedFileListStr !== currentFileListStr
    }
    
    config.value.extraChangeFlag = true
    
    const needReprocess = configChanged || 
                         !tempFileData.value.knowledgeFileUploadTempDTOList.some(file => file.segmentedDocPath)
    
    saveConfigToStorage(config.value, currentFileList)
    
    if (needReprocess) {
      tempFileData.value.knowLedgeSegmentConfig = getFilteredSegmentConfig()
      await createTempFile(tempFileData.value)
    }
  }
  
  if (currentStep.value < 2) {
    currentStep.value++
    
    if (currentStep.value === 2 && tempFileData.value.knowledgeFileUploadTempDTOList.length > 0) {
      if (currentFileId.value !== null && tempFileData.value.knowledgeFileUploadTempDTOList[currentFileId.value]) {
        const file = tempFileData.value.knowledgeFileUploadTempDTOList[currentFileId.value]
        nextTick(() => {
          handleFileClick(file, currentFileId.value)
        })
      } else if (!currentFile.value.fileName && tempFileData.value.knowledgeFileUploadTempDTOList[0]) {
        nextTick(() => {
          handleFileClick(tempFileData.value.knowledgeFileUploadTempDTOList[0], 0)
        })
      }
    }
  }
}

const sseAnalysis = async () => {
  if (!currentFile.value || !currentFile.value.fileMd5) {
    ElMessage.warning('请先选择文件')
    return
  }
  
  const fileMd5 = currentFile.value.fileMd5
  
  // 检查是否已经处理过这个文件
  if (processedFileMd5List.value.includes(fileMd5)) {
    // 如果已经处理过，直接显示 parseMd
    sseCompleted.value = true
    return
  }
  
  // extraChangeFlag 始终为 true
  config.value.extraChangeFlag = true
  
  // 每次点击预览都重新触发SSE，不检查配置变化
  // 使用过滤后的配置
  tempFileData.value.knowLedgeSegmentConfig = getFilteredSegmentConfig()
  let res = await createTempFile(tempFileData.value)
  
  if (res.code === 'ok') {
    try {
      sseLoading.value = true
      sseCompleted.value = false
      
      // 生成5位随机数作为userId
      const randomUserId = Math.floor(10000 + Math.random() * 90000)
      
      // 第一步：建立 SSE 通道
      eventSource.value = new EventSourcePolyfill(import.meta.env.VITE_APP_BASE_API + `/api/sse/connect?userId=${randomUserId}`, {
        headers: {
          'Authorization': 'Bearer ' + getToken(),
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache'
        }
      })
      
      // 第二步：订阅文件处理事件
      const subscribeResponse = await fetch(import.meta.env.VITE_APP_BASE_API + `/api/knowledge-file-segment/process_segment/${props.id}/${props.spaceId}/${randomUserId}/${fileMd5}`, {
        method: 'get',
        headers: {
          'Authorization': 'Bearer ' + getToken(),
          'Content-Type': 'application/json'
        }
      })
      
      // 监听消息事件
      eventSource.value.addEventListener('process-document', (event) => {
        let datas = JSON.parse(event.data)
        // 如果进度是100了就关闭sse连接
        if (datas.fileProgress === 100) {
          eventSource.value.close()
          // 关闭sse连接后，获取文件分段数据
          ElMessage.success('所有文件分段处理成功')
          sseLoading.value = false
          sseCompleted.value = true
          // 将 fileMd5 添加到已处理列表
          if (!processedFileMd5List.value.includes(fileMd5)) {
            processedFileMd5List.value.push(fileMd5)
          }
          // 获取最新数据
          getTempFileData(props.id, props.spaceId).then(() => {
            // 数据更新后，重新选择当前文件
            const currentIndex = tempFileData.value.knowledgeFileUploadTempDTOList.findIndex(f => f.fileMd5 === fileMd5)
            if (currentIndex !== -1) {
              nextTick(() => {
                // 从 SSE 回调中恢复当前文件时，不要重置 sseCompleted
                handleFileClick(tempFileData.value.knowledgeFileUploadTempDTOList[currentIndex], currentIndex, true)
              })
            }
          })
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
        sseCompleted.value = false
        if (eventSource.value) {
          eventSource.value.close()
          eventSource.value = null
        }
      })
      
      // 监听连接关闭事件
      eventSource.value.addEventListener('close', (event) => {
      })
      
    } catch (error) {
      ElMessage.error('无法建立文件处理连接')
      sseLoading.value = false
      sseCompleted.value = false
    }
  } else {
    sseLoading.value = false
    sseCompleted.value = false
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
    // 清空已处理文件MD5列表
    processedFileMd5List.value = []
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
  uploadFileReturn(tempFileData.value).then(res => {
    // 重置提交状态
    isSubmitting.value = false
    ElMessage.success('提交成功')
    goBack()
  }).catch(error => {
    // 失败时立即允许重新点击
    isSubmitting.value = false
    ElMessage.error('提交失败，请重试')
  })
}

// wangEditor 相关方法
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

const handleTag = (tag) => {
  config.value.splitMethod = tag.join(',')
}

// 处理问答提取开关变化
const handleQaExtractChange = (value) => {
  if (!value) {
    // 当关闭问答提取时，清空提示词内容
    config.value.qaExtractPrompt = ''
  } else {
    // 当开启问答提取时，设置默认提示词
    config.value.qaExtractPrompt = `一、问题生成：
     1、基于文本内容，提出一系列关键问题。问题应覆盖文本的核心概念、细节、逻辑和潜在含义。
     2、生成的问题总数不应超过50个。
     3、问题与文本的原始语言保持一致。
二、答案撰写：
     1、为每个问题提供详尽、完整的答案。
     2、核心要求：答案应尽可能基于和忠实于原文描述，可以引用原文的关键词句。在此基础之上，可以进行适当的解释性扩展，以确保答案的清晰性和自洽性。
     3、格式要求：答案可以充分利用Markdown元素来增强可读性和信息密度，例如：
         1)文字格式化（加粗、斜体）外部或内部链接
         2) 代码块
         3) 表格
         4) 数学公式
         5) 媒体链接（图片、视频等）`
  }
}

// 查看原始文档
const viewOriginalFile = () => {
  if (originalFile.value) {
    window.open(originalFile.value, '_blank')
  } else {
    ElMessage.warning('没有可查看的原始文档')
  }
}

const handleFileClick = (item, index, fromSse = false) => {
  try {   
    currentFileId.value = index
    currentFile.value = item
    // 构建文件URL
    const fileUrl = url + item.localSourcePath.replace('uploadPath/', 'profile/')
    originalFile.value = fileUrl
    if (!fromSse) {
      // 检查该文件是否已经处理过
      if (item.fileMd5 && processedFileMd5List.value.includes(item.fileMd5)) {
        // 如果已经处理过，直接显示解析后的内容
        sseCompleted.value = true
      } else {
        // 如果未处理过，显示预览按钮
        sseCompleted.value = false
      }
    }
  } catch (error) {
    console.error('文件点击处理错误:', error)
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
  if (['ppt', 'pptx'].includes(ext)) return 'ppt'
  if (['xls', 'xlsx'].includes(ext)) return 'excel'
  if (ext === 'csv') return 'csv'
  if (['md', 'markdown'].includes(ext)) return 'md'
  if (['txt', 'text'].includes(ext)) return 'txt'
  if (ext === 'log') return 'log'
  if (ext === 'svg') return 'svg'
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)) return 'image'
  if (ext === 'mp3') return 'audio'
  if (ext === 'wav') return 'audio'
  // if (['mp4', 'webm', 'ogg'].includes(ext)) return 'video'
  
  return 'unsupported'
}

// 加载文件内容
const loadFileContent = async (fileUrl, fileType) => {
  isPreviewLoading.value = true
  
  try {
    if (fileType === 'md' || fileType === 'txt' || fileType === 'log') {
      // 加载文本内容
      const response = await fetch(fileUrl)
      
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      
      const text = await response.text()
      
      if (fileType === 'md') {
        markdownContent.value = text
      } else if (fileType === 'log') {
        logContent.value = text
      } else {
        textContent2.value = text
      }
    } else if (fileType === 'svg') {
      // 加载SVG内容
      const response = await fetch(fileUrl)
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      svgContent.value = await response.text()
    } else if (fileType === 'csv') {
      // 加载CSV文件
      const response = await fetch(fileUrl)
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      const text = await response.text()
      
      // 解析CSV
      const lines = text.split('\n').filter(line => line.trim())
      if (lines.length > 0) {
        excelData.value.headers = lines[0].split(',').map(h => h.trim())
        excelData.value.rows = lines.slice(1).map(line => {
          const values = line.split(',')
          const row = {}
          excelData.value.headers.forEach((header, index) => {
            row[header] = values[index] ? values[index].trim() : ''
          })
          return row
        })
      }
    } else if (fileType === 'excel') {
      // 加载Excel文件
      const response = await fetch(fileUrl)
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      
      const arrayBuffer = await response.arrayBuffer()
      const workbook = XLSX.read(arrayBuffer, { type: 'array', cellDates: true })
      
      // 读取第一个工作表
      const firstSheetName = workbook.SheetNames[0]
      const worksheet = workbook.Sheets[firstSheetName]
      
      // 转换为JSON，raw: false 会将日期等格式化为字符串
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { 
        header: 1,
        raw: false,
        dateNF: 'yyyy-mm-dd hh:mm:ss'
      })
      
      if (jsonData.length > 0) {
        excelData.value.headers = jsonData[0].map((h, i) => h || `列${i + 1}`)
        excelData.value.rows = jsonData.slice(1).map(row => {
          const rowObj = {}
          excelData.value.headers.forEach((header, index) => {
            const value = row[index]
            // 如果值是日期对象，格式化为字符串
            if (value instanceof Date) {
              const year = value.getFullYear()
              const month = String(value.getMonth() + 1).padStart(2, '0')
              const day = String(value.getDate()).padStart(2, '0')
              const hours = String(value.getHours()).padStart(2, '0')
              const minutes = String(value.getMinutes()).padStart(2, '0')
              const seconds = String(value.getSeconds()).padStart(2, '0')
              rowObj[header] = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
            } else {
              rowObj[header] = value !== undefined ? value : ''
            }
          })
          return rowObj
        })
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
  // 允许的文件扩展名
  const allowedExtensions = ['pdf', 'txt', 'doc', 'docx', 'md', 'csv', 'pptx', 'xlsx', 'xls', 'png', 'jpg', 'jpeg', 'mp3', 'mp4', 'wav']
  
  // 允许的 MIME 类型
  const allowedMimeTypes = [
    'application/pdf', // PDF
    'text/plain', // TXT
    'application/msword', // DOC
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // DOCX
    'text/markdown', // MD
    'text/csv', // CSV
    'application/vnd.ms-excel', // XLS
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // XLSX
    'application/vnd.ms-powerpoint', // PPT
    'application/vnd.openxmlformats-officedocument.presentationml.presentation', // PPTX
    'image/png', // PNG
    'image/jpeg', // JPG/JPEG
    'image/jpg', // JPG
    'audio/mpeg', // MP3
    'audio/mp3', // MP3
    'video/mp4', // MP4
    'video/mpeg', // MP4
    'audio/wav', // WAV
  ]
  
  // 获取文件扩展名（转换为小写）
  const fileName = file.name || ''
  const fileExtension = fileName.split('.').pop()?.toLowerCase()
  
  // 检查文件扩展名
  const isAllowedExtension = fileExtension && allowedExtensions.includes(fileExtension)
  
  // 检查 MIME 类型（某些文件可能没有正确的 MIME type，所以扩展名检查是主要方式）
  const isAllowedMimeType = file.type && allowedMimeTypes.includes(file.type)
  
  // 如果扩展名或 MIME 类型都不在允许列表中，拒绝上传
  if (!isAllowedExtension && !isAllowedMimeType) {
    ElMessage.error('只支持 PDF、TXT、DOC、DOCX、MD、CSV、PPTX、XLSX、PNG、MP3、MP4 格式的文件！')
    return false
  }
  
  // 检查文件大小（单个文件最多20MB）
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isLt20M) {
    ElMessage.error('上传文件大小不能超过20MB！')
    return false
  }
  
  // 检查上传文件数量（最多15个文件）
  const currentFileCount = tempFileData.value.knowledgeFileUploadTempDTOList.length
  if (currentFileCount >= 15) {
    ElMessage.error('最多只能上传15个文件！')
    return false
  }

  return true
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
  const supportedFormats = ['pdf', 'txt', 'doc', 'docx', 'md', 'csv', 'pptx', 'xlsx', 'png', 'mp3', 'mp4', 'jpg', 'wav']
  
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

:deep(.el-loading-text) {
  font-size: 20px;
}

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

:deep(.el-loading-mask) {
  background: rgba(255,255,255,0.9) !important;
}
.w-e-full-screen-container {
  z-index: 999;
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
        width: 100% !important;
        margin: 0 auto;
        background: white;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        
        .document-container {
          margin: 0;
          padding: 20px;
          width: 100% !important;
          
          p {
            margin: 0;
            line-height: 1.5;
            white-space: normal !important;
            word-wrap: break-word !important;
          }
          
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

/* Markdown预览样式优化 */
:deep(.v-md-editor-preview) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  
  .center-text {
    text-align: center;
  }
  
  .large-text {
    font-size: 15px;
  }
}

.text-preview pre {
  white-space: pre-wrap;
  font-family: inherit;
  margin: 0;
  padding: 20px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.audio-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: white;
  
  .audio-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px;
    
    .audio-info {
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    
    audio {
      outline: none;
      
      &::-webkit-media-controls-panel {
        background-color: #f5f5f5;
      }
    }
  }
}

.video-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: #000;
  
  video {
    outline: none;
    border-radius: 4px;
  }
}

.svg-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: white;
  
  .svg-content {
    max-width: 100%;
    max-height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    
    :deep(svg) {
      max-width: 100%;
      max-height: 100%;
    }
  }
}

.log-preview {
  background: #1e1e1e;
  padding: 20px;
  overflow: auto;
  
  pre {
    color: #d4d4d4;
    font-family: 'Courier New', Courier, monospace;
    font-size: 13px;
    line-height: 1.6;
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
  }
}

.excel-preview {
  padding: 20px;
  background: white;
  overflow: auto;
  
  :deep(.el-table) {
    font-size: 13px;
    
    th {
      background: #f5f7fa;
      font-weight: 600;
    }
  }
}

.ppt-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  
  .ppt-notice {
    text-align: center;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    
    p {
      margin: 8px 0;
      color: #666;
      font-size: 16px;
      
      &:first-of-type {
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }
    }
  }
}

.no-file-preview, .unsupported-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  height: 100%;
  p {
    margin: 8px 0;
    text-align: center;
  }
}
</style>

