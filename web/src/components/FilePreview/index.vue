<template>
  <div class="file-preview-wrapper" v-loading="isLoading" element-loading-text="正在加载预览...">
    <div v-if="fileUrl" class="file-preview-container">
      <!-- PDF预览 -->
      <div v-if="currentFileType === 'pdf'" class="pdf-preview">
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
            :source="fileUrl"
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
      <div v-else-if="currentFileType === 'docx'" class="docx-preview">
        <div ref="docxPreviewContainer" class="preview-container docx-preview-container"></div>
      </div>
      
      <!-- Markdown预览 -->
      <VMdPreview 
        v-else-if="currentFileType === 'md'" 
        :text="markdownContent"
        class="markdown-preview"
      />
      
      <!-- 纯文本预览 -->
      <div v-else-if="currentFileType === 'txt'" class="text-preview">
        <pre>{{ textContent }}</pre>
      </div>
      
      <!-- 图片预览 -->
      <div v-else-if="currentFileType === 'image'" class="image-preview">
        <img :src="fileUrl" alt="预览图片" />
      </div>
      
      <!-- 音频预览 -->
      <div v-else-if="currentFileType === 'audio'" class="audio-preview">
        <div class="audio-container">
          <div class="audio-info">
            <el-icon size="48" style="color: #409eff"><Headset /></el-icon>
            <p style="margin-top: 16px; font-size: 16px; color: #333;">{{ fileName }}</p>
          </div>
          <audio :src="fileUrl" controls style="width: 100%; max-width: 500px; margin-top: 20px;"></audio>
        </div>
      </div>
      
      <!-- 视频预览 -->
      <div v-else-if="currentFileType === 'video'" class="video-preview">
        <video :src="fileUrl" controls style="max-width: 100%; max-height: 100%;"></video>
      </div>
      
      <!-- SVG预览 -->
      <div v-else-if="currentFileType === 'svg'" class="svg-preview">
        <div v-html="svgContent" class="svg-content"></div>
      </div>
      
      <!-- LOG文件预览 -->
      <div v-else-if="currentFileType === 'log'" class="log-preview">
        <pre>{{ logContent }}</pre>
      </div>
      
      <!-- CSV/Excel预览 -->
      <div v-else-if="currentFileType === 'csv' || currentFileType === 'excel'" class="excel-preview">
        <el-table 
          :data="excelData.rows" 
          border
          stripe
          height="100%"
          style="width: 100%"
          :max-height="600"
        >
          <el-table-column
            v-for="(header, index) in excelData.headers"
            :key="index"
            :prop="header"
            :label="header"
            min-width="120"
            show-overflow-tooltip
          />
        </el-table>
      </div>
      
      <!-- PPT预览 -->
      <div v-else-if="currentFileType === 'ppt'" class="ppt-preview">
        <div class="ppt-notice">
          <el-icon size="48" style="color: #909399"><Document /></el-icon>
          <p>PPT文件预览</p>
          <p style="font-size: 14px; color: #666; margin-top: 10px;">{{ fileName }}</p>
          <el-button type="primary" style="margin-top: 20px;" @click="handleDownload">下载查看</el-button>
        </div>
      </div>
      
      <!-- 不支持的格式 -->
      <div v-else class="unsupported-preview">
        <el-icon size="48" style="color: #909399"><Document /></el-icon>
        <p>暂不支持该文件格式的预览</p>
        <p style="font-size: 12px; color: #909399;">支持格式：PDF、DOCX、PPT、EXCEL、CSV、MD、TXT、LOG、SVG、图片、音频(MP3、WAV)、视频(MP4)</p>
      </div>
    </div>
    
    <!-- 无文件时的占位 -->
    <div v-else class="no-file-preview">
      <el-icon size="48" style="color: #c0c4cc"><Folder /></el-icon>
      <p style="color: #909399; margin-top: 16px;">{{ placeholder || '暂无文件预览' }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import VuePdfEmbed from 'vue-pdf-embed'
import { renderAsync } from 'docx-preview'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import Prism from 'prismjs'
import * as XLSX from 'xlsx'

// 配置markdown编辑器
VMdPreview.use(vuepressTheme, {
  Prism,
})

const props = defineProps({
  fileUrl: {
    type: String,
    default: ''
  },
  fileName: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['download'])

// 预览相关变量
const currentFileType = ref('')
const isLoading = ref(false)
const markdownContent = ref('')
const textContent = ref('')
const svgContent = ref('')
const logContent = ref('')
const docxPreviewContainer = ref(null)

// Excel/CSV预览相关变量
const excelData = ref({
  headers: [],
  rows: []
})

// PDF预览相关变量
const currentPage = ref(1)
const totalPages = ref(0)
const pdfViewMode = ref('continuous')

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
  if (['mp3', 'wav'].includes(ext)) return 'audio'
  if (['mp4', 'webm', 'ogg'].includes(ext)) return 'video'
  
  return 'unsupported'
}

// 加载文件内容
const loadFileContent = async (fileUrl, fileType) => {
  isLoading.value = true
  
  try {
    if (fileType === 'md' || fileType === 'txt' || fileType === 'log') {
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
        textContent.value = text
      }
    } else if (fileType === 'svg') {
      const response = await fetch(fileUrl)
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      svgContent.value = await response.text()
    } else if (fileType === 'csv') {
      const response = await fetch(fileUrl)
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      const text = await response.text()
      
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
      const response = await fetch(fileUrl)
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      
      const arrayBuffer = await response.arrayBuffer()
      const workbook = XLSX.read(arrayBuffer, { type: 'array', cellDates: true })
      
      const firstSheetName = workbook.SheetNames[0]
      const worksheet = workbook.Sheets[firstSheetName]
      
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
      await nextTick()
      
      if (docxPreviewContainer.value) {
        try {
          const response = await fetch(fileUrl)
          if (!response.ok) {
            throw new Error(`HTTP错误! 状态: ${response.status}`)
          }
          
          const blob = await response.blob()
          const arrayBuffer = await blob.arrayBuffer()
          
          docxPreviewContainer.value.innerHTML = ''
          
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
    console.error('文件预览加载失败:', error)
    ElMessage.error('文件预览加载失败: ' + error.message)
  } finally {
    isLoading.value = false
  }
}

// PDF相关处理
const handlePreviewError = (error) => {
  console.error('PDF预览失败:', error)
  ElMessage.error('文件预览失败，请检查文件是否损坏')
  isLoading.value = false
}

const handlePdfLoaded = (pdf) => {
  if (pdf && pdf.numPages) {
    totalPages.value = pdf.numPages
  }
  isLoading.value = false
}

const handlePdfRendered = () => {
  isLoading.value = false
}

// 下载处理
const handleDownload = () => {
  emit('download', props.fileUrl)
}

// 重置状态
const resetState = () => {
  markdownContent.value = ''
  textContent.value = ''
  svgContent.value = ''
  logContent.value = ''
  excelData.value = { headers: [], rows: [] }
  currentPage.value = 1
  totalPages.value = 0
  pdfViewMode.value = 'continuous'
  if (docxPreviewContainer.value) {
    docxPreviewContainer.value.innerHTML = ''
  }
}

// 监听文件URL变化
watch(() => props.fileUrl, async (newUrl) => {
  if (!newUrl) {
    currentFileType.value = ''
    resetState()
    return
  }
  
  const fileType = getFileType(props.fileName || newUrl)
  currentFileType.value = fileType
  
  resetState()
  
  // 需要加载内容的文件类型
  if (['md', 'txt', 'log', 'docx', 'csv', 'excel', 'svg'].includes(fileType)) {
    await nextTick()
    await loadFileContent(newUrl, fileType)
  }
}, { immediate: true })

// 清理Blob URL
onBeforeUnmount(() => {
  // 如果有需要清理的资源
})
</script>

<style scoped lang="scss">
.file-preview-wrapper {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.file-preview-container {
  width: 100%;
  height: 100%;
  overflow: auto;
}

// PDF预览样式
.pdf-preview {
  width: 100%;
  height: 100%;
  overflow: auto;
  display: flex;
  flex-direction: column;
  
  .pdf-controls {
    padding: 12px 16px;
    background: white;
    border-bottom: 1px solid #e4e7ed;
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

// DOCX预览样式
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

// Markdown预览样式
:deep(.v-md-editor-preview) {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

// 文本预览样式
.text-preview {
  width: 100%;
  height: 100%;
  overflow: auto;
  
  pre {
    white-space: pre-wrap;
    font-family: inherit;
    margin: 0;
    padding: 20px;
    background: white;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

// 图片预览样式
.image-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  
  img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

// 音频预览样式
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
    width: 100%;
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

// 视频预览样式
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

// SVG预览样式
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

// LOG预览样式
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

// Excel预览样式
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

// PPT预览样式
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

// 不支持的格式和无文件样式
.unsupported-preview,
.no-file-preview {
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

