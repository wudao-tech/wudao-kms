<template>
  <div class="advanced-upload">
    <!-- 拖拽上传区域 -->
    <el-upload
      ref="uploadRef"
      class="upload-dragger"
      drag
      :http-request="customUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      :on-remove="handleRemove"
      :before-upload="beforeUpload"
      :show-file-list="false"
      :multiple="true"
      :accept="accept"
      :disabled="uploading"
    >
      <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
      <div class="el-upload__text">
        <span v-if="!uploading">点击上传或拖拽文件到这里</span>
        <span v-else>正在上传中，请稍候...</span>
      </div>
      <div class="upload-tip">
        支持 {{ acceptText }}，最多可上传 {{ maxFiles }} 个文件，每个文件不超过 {{ maxFileSize }}MB
      </div>
    </el-upload>

    <!-- 文件列表和进度条 -->
    <div v-if="fileList.length > 0" class="file-list">
      <div class="file-list-header">
        <span>上传文件列表 ({{ fileList.length }})</span>
        <el-button 
          v-if="!uploading" 
          type="primary" 
          size="small" 
          @click="startUpload"
          :disabled="fileList.length === 0"
        >
          开始上传
        </el-button>
        <el-button 
          v-if="uploading" 
          type="danger" 
          size="small" 
          @click="cancelUpload"
        >
          取消上传
        </el-button>
      </div>

      <div class="file-items">
        <div 
          v-for="(file, index) in fileList" 
          :key="file.uid" 
          class="file-item"
          :class="{ 'uploading': file.status === 'uploading', 'success': file.status === 'success', 'error': file.status === 'error' }"
        >
          <!-- 文件信息 -->
          <div class="file-info">
            <div class="file-icon">
              <img :src="getFileIcon(file.name)" alt="file" />
            </div>
            <div class="file-details">
              <div class="file-name">{{ file.name }}</div>
              <div class="file-size">{{ formatFileSize(file.size) }}</div>
            </div>
            <div class="file-status">
              <el-icon v-if="file.status === 'waiting'" class="status-icon waiting"><Clock /></el-icon>
              <el-icon v-if="file.status === 'uploading'" class="status-icon uploading"><Loading /></el-icon>
              <el-icon v-if="file.status === 'success'" class="status-icon success"><CircleCheck /></el-icon>
              <el-icon v-if="file.status === 'error'" class="status-icon error"><CircleClose /></el-icon>
            </div>
          </div>

          <!-- 进度条 -->
          <div v-if="file.status === 'uploading'" class="progress-container">
            <el-progress 
              :percentage="file.progress" 
              :status="file.progress === 100 ? 'success' : ''"
              :stroke-width="6"
              :show-text="false"
            />
            <div class="progress-text">
              {{ file.progress }}% ({{ formatFileSize(file.uploadedSize) }}/{{ formatFileSize(file.size) }})
            </div>
          </div>

          <!-- 错误信息 -->
          <div v-if="file.status === 'error'" class="error-message">
            {{ file.errorMessage }}
          </div>

          <!-- 操作按钮 -->
          <div class="file-actions">
            <el-button 
              v-if="file.status === 'waiting'" 
              size="small" 
              @click="removeFile(index)"
            >
              移除
            </el-button>
            <el-button 
              v-if="file.status === 'error'" 
              size="small" 
              type="primary"
              @click="retryUpload(index)"
            >
              重试
            </el-button>
          </div>
        </div>
      </div>

      <!-- 总体进度 -->
      <div v-if="uploading" class="overall-progress">
        <div class="progress-header">
          <span>总体进度</span>
          <span>{{ overallProgress }}%</span>
        </div>
        <el-progress 
          :percentage="overallProgress" 
          :stroke-width="8"
          color="#409eff"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Clock, Loading, CircleCheck, CircleClose } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  // 接受的文件类型
  accept: {
    type: String,
    default: '.pdf,.doc,.docx,.txt,.md,.csv,.pptx,.xlsx,.html,.png,.jpg,.jpeg,.gif,.bmp'
  },
  // 最大文件数量
  maxFiles: {
    type: Number,
    default: 15
  },
  // 单个文件最大大小 (MB)
  maxFileSize: {
    type: Number,
    default: 100
  },
  // 分片大小 (KB)
  chunkSize: {
    type: Number,
    default: 1024 // 1MB
  },
  // 上传接口
  uploadUrl: {
    type: String,
    default: '/api/upload'
  },
  // 额外的请求头
  headers: {
    type: Object,
    default: () => ({})
  },
  // 额外的表单数据
  data: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['upload-success', 'upload-error', 'upload-progress', 'file-remove'])

// 响应式数据
const uploadRef = ref(null)
const fileList = ref([])
const uploading = ref(false)
const abortController = ref(null)

// 计算属性
const acceptText = computed(() => {
  const types = props.accept.split(',').map(type => type.replace('.', '').toUpperCase())
  return types.join('、')
})

const overallProgress = computed(() => {
  if (fileList.value.length === 0) return 0
  const totalProgress = fileList.value.reduce((sum, file) => sum + (file.progress || 0), 0)
  return Math.round(totalProgress / fileList.value.length)
})

// 文件类型图标映射
const fileTypeIcons = {
  pdf: '/src/assets/fileIcon/pdf.svg',
  doc: '/src/assets/fileIcon/doc.svg',
  docx: '/src/assets/fileIcon/doc.svg',
  txt: '/src/assets/fileIcon/txt.svg',
  md: '/src/assets/fileIcon/md.svg',
  csv: '/src/assets/fileIcon/csv.svg',
  pptx: '/src/assets/fileIcon/ppt.svg',
  xlsx: '/src/assets/fileIcon/xlsx.svg',
  html: '/src/assets/fileIcon/html.svg',
  png: '/src/assets/fileIcon/png.svg',
  jpg: '/src/assets/fileIcon/jpg.svg',
  jpeg: '/src/assets/fileIcon/jpg.svg',
  gif: '/src/assets/fileIcon/gif.svg',
  bmp: '/src/assets/fileIcon/bmp.svg'
}

// 获取文件图标
const getFileIcon = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase()
  return fileTypeIcons[extension] || '/src/assets/fileIcon/file.svg'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 文件上传前验证
const beforeUpload = (file) => {
  // 检查文件大小
  const maxSize = props.maxFileSize * 1024 * 1024 // 转换为字节
  if (file.size > maxSize) {
    ElMessage.error(`文件 ${file.name} 超过最大大小限制 ${props.maxFileSize}MB`)
    return false
  }

  // 检查文件数量
  if (fileList.value.length >= props.maxFiles) {
    ElMessage.error(`最多只能上传 ${props.maxFiles} 个文件`)
    return false
  }

  // 检查文件类型
  const allowedTypes = props.accept.split(',').map(type => type.trim())
  const fileExtension = '.' + file.name.split('.').pop().toLowerCase()
  if (!allowedTypes.includes(fileExtension)) {
    ElMessage.error(`不支持的文件类型: ${fileExtension}`)
    return false
  }

  // 添加到文件列表
  const fileItem = {
    uid: file.uid,
    name: file.name,
    size: file.size,
    file: file,
    status: 'waiting',
    progress: 0,
    uploadedSize: 0,
    chunks: [],
    currentChunk: 0
  }

  fileList.value.push(fileItem)
  return false // 阻止默认上传
}

// 自定义上传方法
const customUpload = (options) => {
  // 这里不执行上传，只是添加到文件列表
  console.log('File added to list:', options.file)
}

// 开始上传
const startUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }

  uploading.value = true
  abortController.value = new AbortController()

  // 为每个文件准备分片
  for (const fileItem of fileList.value) {
    if (fileItem.status === 'waiting') {
      await prepareChunks(fileItem)
    }
  }

  // 开始上传所有文件
  const uploadPromises = fileList.value
    .filter(file => file.status === 'waiting')
    .map(file => uploadFile(file))

  try {
    await Promise.all(uploadPromises)
    ElMessage.success('所有文件上传完成')
  } catch (error) {
    if (error.name !== 'AbortError') {
      ElMessage.error('上传过程中出现错误')
    }
  } finally {
    uploading.value = false
    abortController.value = null
  }
}

// 准备文件分片
const prepareChunks = async (fileItem) => {
  const file = fileItem.file
  const chunkSize = props.chunkSize * 1024 // 转换为字节
  const chunks = []
  
  for (let i = 0; i < file.size; i += chunkSize) {
    const chunk = file.slice(i, i + chunkSize)
    chunks.push(chunk)
  }
  
  fileItem.chunks = chunks
  fileItem.totalChunks = chunks.length
  fileItem.currentChunk = 0
}

// 上传单个文件
const uploadFile = async (fileItem) => {
  fileItem.status = 'uploading'
  
  try {
    for (let i = 0; i < fileItem.chunks.length; i++) {
      if (abortController.value?.signal.aborted) {
        throw new Error('Upload cancelled')
      }

      await uploadChunk(fileItem, i)
      fileItem.currentChunk = i + 1
      fileItem.progress = Math.round(((i + 1) / fileItem.chunks.length) * 100)
      fileItem.uploadedSize = Math.round((fileItem.size * fileItem.progress) / 100)
      
      // 触发进度事件
      emit('upload-progress', {
        file: fileItem,
        progress: fileItem.progress
      })
    }
    
    fileItem.status = 'success'
    fileItem.progress = 100
    fileItem.uploadedSize = fileItem.size
    
    // 触发成功事件
    emit('upload-success', fileItem)
    
  } catch (error) {
    fileItem.status = 'error'
    fileItem.errorMessage = error.message || '上传失败'
    
    // 触发错误事件
    emit('upload-error', {
      file: fileItem,
      error: error
    })
    
    throw error
  }
}

// 上传单个分片
const uploadChunk = async (fileItem, chunkIndex) => {
  const chunk = fileItem.chunks[chunkIndex]
  const formData = new FormData()
  
  formData.append('file', chunk, fileItem.name)
  formData.append('chunkIndex', chunkIndex)
  formData.append('totalChunks', fileItem.chunks.length)
  formData.append('fileName', fileItem.name)
  formData.append('fileSize', fileItem.size)
  
  // 添加额外的表单数据
  Object.keys(props.data).forEach(key => {
    formData.append(key, props.data[key])
  })

  const response = await fetch(props.uploadUrl, {
    method: 'POST',
    headers: {
      ...props.headers
    },
    body: formData,
    signal: abortController.value?.signal
  })

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`)
  }

  const result = await response.json()
  
  // 检查服务器返回的错误
  if (result.code && result.code !== 200) {
    throw new Error(result.message || '上传失败')
  }

  return result
}

// 取消上传
const cancelUpload = () => {
  if (abortController.value) {
    abortController.value.abort()
  }
  uploading.value = false
  
  // 将正在上传的文件状态重置为等待
  fileList.value.forEach(file => {
    if (file.status === 'uploading') {
      file.status = 'waiting'
      file.progress = 0
      file.uploadedSize = 0
    }
  })
  
  ElMessage.info('上传已取消')
}

// 重试上传
const retryUpload = async (index) => {
  const fileItem = fileList.value[index]
  fileItem.status = 'waiting'
  fileItem.progress = 0
  fileItem.uploadedSize = 0
  fileItem.errorMessage = ''
  
  if (uploading.value) {
    await uploadFile(fileItem)
  }
}

// 移除文件
const removeFile = (index) => {
  const fileItem = fileList.value[index]
  fileList.value.splice(index, 1)
  emit('file-remove', fileItem)
}

// 处理上传成功
const handleUploadSuccess = (response, file) => {
  console.log('Upload success:', response, file)
}

// 处理上传错误
const handleUploadError = (error, file) => {
  console.log('Upload error:', error, file)
}

// 处理文件移除
const handleRemove = (file) => {
  console.log('File removed:', file)
}

// 暴露方法给父组件
defineExpose({
  startUpload,
  cancelUpload,
  clearFiles: () => {
    fileList.value = []
  },
  getFileList: () => fileList.value
})
</script>

<style scoped lang="scss">
.advanced-upload {
  .upload-dragger {
    border: 2px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;
    
    &:hover {
      border-color: #409eff;
    }
    
    &.is-dragover {
      border-color: #409eff;
      background-color: #f0f9ff;
    }
    
    .el-icon--upload {
      font-size: 67px;
      color: #c0c4cc;
      margin: 40px 0 16px;
      line-height: 50px;
    }
    
    .el-upload__text {
      color: #606266;
      font-size: 14px;
      text-align: center;
    }
    
    .upload-tip {
      font-size: 12px;
      color: #909399;
      text-align: center;
      margin-top: 8px;
    }
  }
  
  .file-list {
    margin-top: 20px;
    
    .file-list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding: 12px 16px;
      background-color: #f5f7fa;
      border-radius: 4px;
      font-weight: 500;
    }
    
    .file-items {
      .file-item {
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        margin-bottom: 12px;
        padding: 16px;
        transition: all 0.3s;
        
        &.uploading {
          border-color: #409eff;
          background-color: #f0f9ff;
        }
        
        &.success {
          border-color: #67c23a;
          background-color: #f0f9ff;
        }
        
        &.error {
          border-color: #f56c6c;
          background-color: #fef0f0;
        }
        
        .file-info {
          display: flex;
          align-items: center;
          margin-bottom: 12px;
          
          .file-icon {
            margin-right: 12px;
            
            img {
              width: 32px;
              height: 32px;
            }
          }
          
          .file-details {
            flex: 1;
            
            .file-name {
              font-weight: 500;
              margin-bottom: 4px;
              word-break: break-all;
            }
            
            .file-size {
              font-size: 12px;
              color: #909399;
            }
          }
          
          .file-status {
            .status-icon {
              font-size: 20px;
              
              &.waiting {
                color: #909399;
              }
              
              &.uploading {
                color: #409eff;
                animation: rotate 1s linear infinite;
              }
              
              &.success {
                color: #67c23a;
              }
              
              &.error {
                color: #f56c6c;
              }
            }
          }
        }
        
        .progress-container {
          margin-bottom: 12px;
          
          .progress-text {
            font-size: 12px;
            color: #606266;
            margin-top: 4px;
            text-align: center;
          }
        }
        
        .error-message {
          color: #f56c6c;
          font-size: 12px;
          margin-bottom: 12px;
        }
        
        .file-actions {
          text-align: right;
        }
      }
    }
    
    .overall-progress {
      margin-top: 20px;
      padding: 16px;
      background-color: #f5f7fa;
      border-radius: 4px;
      
      .progress-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        font-weight: 500;
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style> 