<template> 
  <div class="parse-md-container">
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <el-icon size="24" style="color: #409eff"><Loading /></el-icon>
      <span style="margin-left: 8px;">正在加载解析内容...</span>
    </div>
    
    <!-- SSE未完成时的提示（仅在fileMd5模式下显示） -->
    <div v-else-if="props.fileMd5 && !props.sseCompleted" class="sse-waiting">
      <el-icon size="48" style="color: #e6a23c"><Clock /></el-icon>
      <p style="color: #e6a23c; margin-top: 16px;">等待文件分段处理完成...</p>
    </div>
    
    <!-- MD内容显示 -->
    <div v-else-if="segments.length > 0" class="md-content">
      <!-- 段落列表 -->
      <div class="segments-list">
        <div 
          v-for="(segment, index) in segments" 
          :key="index"
          class="segment-item"
          :class="{ 'segment-active': currentSegmentIndex === index }"
        >
          <!-- 段落信息（框外） -->
          <div class="segment-header">
            <span class="segment-index">段落 {{ segment.segmentIndex || (index + 1) }}  <span style="font-size: 12px;color: #909399;">{{ segment.length }} 字符</span></span>
            <div class="segment-actions">
              <el-button v-if="editingIndex === index" link size="small" icon="Upload" @click="uploadImage(index)" >上传图片</el-button>
              <el-button
                v-if="editingIndex !== index"
                link
                size="small" 
                icon="Edit" 
                :disabled="!props.permission"
                @click="editSegment(index)"
                style="margin-left: 8px;"
              >
                编辑
              </el-button>
            </div>
          </div>
          
          <!-- 段落内容 -->
          <div class="segment-content" @click="selectSegment(index)">
            <!-- 编辑模式 -->
            <div v-if="editingIndex === index" class="edit-mode">
              <!-- Markdown 编辑器（暂时注释） -->
              <!--
              <div class="markdown-editor-container">
                <v-md-editor 
                  v-model="editingContent" 
                  height="300px"
                  :toolbar="markdownToolbar"
                  :upload-image="handleImageUpload"
                  placeholder="编辑段落内容..."
                />
              </div>
              -->
              
              <!-- 暂时使用文本域 -->
              <el-input
                v-model="editingContent"
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 10 }"
                placeholder="编辑段落内容..."
                class="edit-textarea"
                ref="textareaRef"
                @focus="recordCursorPosition"
                @click="recordCursorPosition"
                @keyup="recordCursorPosition"
              />                         
              <div class="edit-actions">
                <el-button size="small" @click="cancelEdit">取消</el-button>
                <el-button size="small" type="primary" @click="saveEdit">保存</el-button>
              </div>
            </div>
            
            <!-- 预览模式 -->
            <div v-else>
              <v-md-preview :text="segment.content" class="segment-markdown" />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 无内容时的占位 -->
    <div v-else class="no-content">
      <el-icon size="48" style="color: #c0c4cc"><Document /></el-icon>
      <p style="color: #909399; margin-top: 16px;">暂无解析内容</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, nextTick } from 'vue'
import { getToken } from '@/utils/auth'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import VMdEditor from '@kangc/v-md-editor'
import '@kangc/v-md-editor/lib/style/preview.css'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import '@kangc/v-md-editor/lib/style/codemirror-editor.css'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import Prism from 'prismjs'
import { getParsedFileSegment, uploadFileImage, updateParsedFileSegment, getParsedFileSegmentDetail, fileSegmentVector } from '@/api/base'

// 配置markdown预览器和编辑器
VMdPreview.use(vuepressTheme, {
  Prism,
})

VMdEditor.use(vuepressTheme, {
  Prism,
})

const props = defineProps({
    fileMd5: {
    type: String,
    default: ''
    },
    knowledgeBaseId: {
        type: Number,
        default: 0
    },
    knowledgeSpaceId: {
        type: Number,
        default: 0
    },
    sseCompleted: {
        type: Boolean,
        default: false
    },
    fileId: {
      type: Number,
      default: 0
    },
    permission: {
      type: Boolean,
      default: true
    }
})

const isLoading = ref(false)
const mdContent = ref('')
const segments = ref([])
const currentSegmentIndex = ref(-1)

// 编辑相关状态
const editingIndex = ref(-1)
const editingContent = ref('')
const originalContent = ref('')
const cursorPosition = ref(0) // 记录光标位置
const textareaRef = ref(null) // 文本域引用

// 图片上传相关
const uploadAction = import.meta.env.VITE_APP_BASE_API + '/api/knowledge-file/upload-image'
const uploadHeaders = {
  'Authorization': 'Bearer ' + getToken()
}

// Markdown 编辑器工具栏配置
const markdownToolbar = [
  'bold',
  'italic', 
  'underline',
  'strikethrough',
  '|',
  'title',
  'sub',
  'sup',
  '|',
  'quote',
  'unorderedList',
  'orderedList',
  '|',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  '|',
  'undo',
  'redo',
  '|',
  'preview',
  'fullscreen'
]

// 计算总字符数（不包含符号）
const totalCharacters = computed(() => {
  return segments.value.reduce((total, segment) => {
    // 只计算中文字符、英文字母、数字，不计算标点符号和特殊字符
    const textContent = segment.content || ''
    const textOnly = textContent.replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '')
    return total + textOnly.length
  }, 0)
})

// 通过fileId加载段落数据
const loadMdContentByFileId = async (fileId) => {
  isLoading.value = true
  try {
    const response = await getParsedFileSegmentDetail({
      fileId: fileId
    })
    if (response.code === 'ok' && response.data) {
      // response.data 是个数组，每个段落是一个item
      const segmentsData = response.data
      
      segments.value = segmentsData.map((item, index) => {
        // 只计算中文字符、英文字母、数字，不计算标点符号和特殊字符
        const textOnly = (item.segmentContent || '').replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '')
        return {
          id: item.id,
          index: index,
          content: item.segmentContent || '',
          length: textOnly.length
        }
      })     
      // 设置mdContent以便模板正确显示
      mdContent.value = 'loaded'
    } else {
      segments.value = []
      mdContent.value = ''
    }
  } catch (error) {
    ElMessage.error('加载文档段落失败: ' + error.message)
    segments.value = []
    mdContent.value = ''
  } finally {
    isLoading.value = false
  }
} 

// 加载段落数据
const loadMdContent = async (fileMd5) => {
  isLoading.value = true
  try {
    const response = await getParsedFileSegment({
      fileMd5: props.fileMd5,
      knowledgeId: props.knowledgeBaseId,
      knowledgeSpaceId: props.knowledgeSpaceId
    })
    if (response.code === 'ok' && response.data) {
      // response.data 是个数组，每个段落是一个item
      const segmentsData = response.data
      // 将接口数据转换为组件需要的格式
      segments.value = segmentsData.map((item, index) => {
        const content = item.segmentContent || ''
        // 只计算中文字符、英文字母、数字，不计算标点符号和特殊字符
        const textOnly = content.replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '')
        return {
          ...item,
          content: content,
          length: textOnly.length
        }
      })     
      // 设置mdContent以便模板正确显示
      mdContent.value = 'loaded'
    } else {
      segments.value = []
      mdContent.value = ''
      ElMessage.warning('暂无段落数据')
    }
  } catch (error) {
    ElMessage.error('加载文档段落失败: ' + error.message)
    segments.value = []
    mdContent.value = ''
  } finally {
    isLoading.value = false
  }
}

// 监听文件URL变化和SSE完成状态
watch([() => props.fileMd5, () => props.sseCompleted], async ([newFileMd5, sseCompleted]) => {
  if (newFileMd5 && sseCompleted) {
    await loadMdContent(newFileMd5)
  } else {
    mdContent.value = ''
    segments.value = []
    currentSegmentIndex.value = -1
  }
}, { immediate: true })

// 监听 fileId 变化
// watch([() => props.fileId, ], async ([newFileId]) => {
//   console.log('newFileId', newFileId)
//   if (newFileId) {
//     await loadMdContentByFileId(newFileId)
//   } else {
//     mdContent.value = ''
//     segments.value = []
//     currentSegmentIndex.value = -1
//   }
// }, { immediate: true })

// 解析MD内容为段落
const parseMdToSegments = (content) => {
  // 按双换行符分割段落
  const rawSegments = content.split(/\n\s*\n/).filter(segment => segment.trim())
  
  segments.value = rawSegments.map((segment, index) => ({
    index: index,
    content: segment.trim(),
    length: segment.trim().length
  }))
}

// 选择段落
const selectSegment = (index) => {
  if (editingIndex.value === -1) {
    currentSegmentIndex.value = index
  }
}

// 编辑段落
const editSegment = (index) => {
  editingIndex.value = index
  editingContent.value = segments.value[index].content
  originalContent.value = segments.value[index].content
}

// 记录光标位置
const recordCursorPosition = () => {
  // Element Plus 的 ref 可能是数组，取第一个元素
  const inputRef = Array.isArray(textareaRef.value) ? textareaRef.value[0] : textareaRef.value
  if (inputRef && inputRef.$refs?.textarea) {
    const textarea = inputRef.$refs.textarea
    cursorPosition.value = textarea.selectionStart || 0
  }
}

// 在指定位置插入图片
const insertImageAtPosition = (imageMarkdown) => {
  const currentContent = editingContent.value
  const position = cursorPosition.value
  // 如果没有光标位置或位置无效，插入到最后
  if (position === undefined || position === null || position < 0 || position > currentContent.length) {
    if (currentContent) {
      editingContent.value += '\n\n' + imageMarkdown
    } else {
      editingContent.value = imageMarkdown
    }
    return
  }
  
  // 从0截取到当前位置的所有内容
  const beforeCursor = currentContent.substring(0, position)
  const afterCursor = currentContent.substring(position)
  
  // 在光标位置插入图片 - 直接插入，不添加额外的换行
  editingContent.value = beforeCursor + imageMarkdown + afterCursor
  
  // 更新光标位置到插入内容之后
  const newPosition = position + imageMarkdown.length
  cursorPosition.value = newPosition
  
  // 设置文本域的光标位置
  nextTick(() => {
    // Element Plus 的 ref 可能是数组，取第一个元素
    const inputRef = Array.isArray(textareaRef.value) ? textareaRef.value[0] : textareaRef.value
    
    if (inputRef && inputRef.$refs?.textarea) {
      const textarea = inputRef.$refs.textarea
      textarea.focus()
      textarea.setSelectionRange(newPosition, newPosition)
    }
  })
}

// 保存编辑
const saveEdit = async () => {
  if (editingIndex.value !== -1) {
   
    await updateParsedFileSegment({
      id: segments.value[editingIndex.value].id,
      segmentContent: editingContent.value,
      vectorFlag: false
    })
    if (props.fileId) {
      await fileSegmentVector(props.fileId)
    }
    segments.value[editingIndex.value].content = editingContent.value
    // 只计算中文字符、英文字母、数字，不计算标点符号和特殊字符
    const textOnly = editingContent.value.replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '')
    segments.value[editingIndex.value].length = textOnly.length
    editingIndex.value = -1
    editingContent.value = ''
    originalContent.value = ''
    ElMessage.success('保存成功')

  }
}

// 取消编辑
const cancelEdit = () => {
  editingIndex.value = -1
  editingContent.value = ''
  originalContent.value = ''
}

// 图片上传前验证
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 处理图片上传成功（文本域模式）
const handleImageUpload = (response, segmentIndex) => {
  if (response.code === 'ok') {
    const imageUrl = response.data
    const imageMarkdown = `![图片](${imageUrl})`
    
    // 在编辑内容中插入图片
    if (editingContent.value) {
      editingContent.value += '\n\n' + imageMarkdown
    } else {
      editingContent.value = imageMarkdown
    }
    
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

const uploadImage = (index) => {
  // 创建隐藏的文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*' // 只允许选择图片文件
  input.style.display = 'none'
  
  // 添加到页面
  document.body.appendChild(input)
  
  // 触发文件选择
  input.click()
  
  // 监听文件选择
  input.onchange = async (event) => {
    const file = event.target.files[0]
    if (!file) {
      document.body.removeChild(input)
      return
    }
    
    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      ElMessage.error('只能上传图片文件')
      document.body.removeChild(input)
      return
    }
    
    // 验证文件大小（5MB）
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.error('图片大小不能超过 5MB')
      document.body.removeChild(input)
      return
    }
    
    try {
      // 显示上传中状态
      ElMessage.info('正在上传图片...')
      
      // 创建 FormData
      const formData = new FormData()
      formData.append('file', file)
      
      // 调用上传接口
      const response = await uploadFileImage(formData)
      
      if (response.code === 'ok') {
        const imageUrl = response.data
        const imageMarkdown = `![图片](${imageUrl})`
        
        // 在指定位置插入图片
        insertImageAtPosition(imageMarkdown)
        
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error('图片上传失败: ' + (response.message || '未知错误'))
      }
    } catch (error) {
      ElMessage.error('图片上传失败: ' + error.message)
    } finally {
      // 清理临时元素
      document.body.removeChild(input)
    }
  }
  
  // 监听取消选择
  input.oncancel = () => {
    document.body.removeChild(input)
  }
}

// 处理图片上传 - 适配 markdown 编辑器（暂时注释）
/*
const handleImageUploadForEditor = async (file, insertImage, files) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await fetch(uploadAction, {
      method: 'POST',
      headers: uploadHeaders,
      body: formData
    })
    
    const result = await response.json()
    
    if (result.code === 'ok') {
      const imageUrl = result.data
      insertImage({
        url: imageUrl,
        desc: '图片'
      })
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    console.error('图片上传错误:', error)
    ElMessage.error('图片上传失败')
  }
}
*/

// 暴露方法给父组件
defineExpose({
  segments,
  currentSegmentIndex,
  selectSegment,
  editSegment,
  saveEdit,
  cancelEdit
})
</script>

<style scoped lang="scss">
.parse-md-container {
  height: 100%;
  display: flex;
  width: 100%;
  width: calc(100% - 10px);
  flex-direction: column;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #409eff;
}

.sse-waiting {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  text-align: center;
}

.md-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content-header {
  padding: 16px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  
  h3 {
    margin: 0 0 8px 0;
    color: #303133;
    font-size: 16px;
  }
  
  .content-stats {
    display: flex;
    gap: 16px;
    font-size: 12px;
    color: #909399;
  }
}

.segments-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.segment-item {
  margin-bottom: 16px;
  padding: 12px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
  }
  
  &.segment-active {
    border-color: #409eff;
    background: #f0f9ff;
  }
}

.segment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px; 
  .segment-index {
    font-weight: 500;
    color: #303133;
  }
  
  .segment-actions {
    display: flex;
    align-items: center;
    
    .segment-length {
      color: #909399;
      font-size: 12px;
    }
  }
}

.segment-content {
  cursor: pointer;
  
  .edit-mode {
    // Markdown 编辑器样式（暂时注释）
    /*
    .markdown-editor-container {
      margin-bottom: 16px;
      border: 1px solid #e4e7ed;
      border-radius: 6px;
      overflow: hidden;
      
      :deep(.v-md-editor) {
        border: none;
        
        .v-md-editor__toolbar {
          background: #f8f9fa;
          border-bottom: 1px solid #e4e7ed;
        }
        
        .v-md-editor__main {
          background: white;
        }
        
        .v-md-editor__preview {
          background: #fafafa;
        }
      }
    }
    */
    
    .edit-textarea {
      margin-bottom: 12px;
    }
    
    .image-upload-section {
      margin-bottom: 12px;
      padding: 12px;
      background: #f8f9fa;
      border-radius: 6px;
      border: 1px dashed #d9d9d9;
      
      .upload-label {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }
      
      .image-uploader {
        margin-bottom: 8px;
      }
      
      .upload-tip {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .edit-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }
  }
  
  .segment-markdown {
    :deep(.v-md-editor-preview) {
      padding: 0;
      background: transparent;
    }
    
    :deep(.v-md-editor-preview-wrapper) {
      padding: 0;
    }
    
    :deep(h1, h2, h3, h4, h5, h6) {
      margin-top: 0;
      margin-bottom: 8px;
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

.no-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
}

</style>
