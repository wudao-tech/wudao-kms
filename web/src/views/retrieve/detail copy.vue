<template>
  <div class="detail-container">
    <!-- 左侧基本信息 -->
    <div class="left-sidebar">
      <div class="info-card">
        <div class="basic-info">
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px;">
            <el-button icon="Back" link @click="goBack"> 返回</el-button>
            <span>基本信息</span>
          </div>
          
          <!-- 知识图谱图片 -->
          <div class="knowledge-graph">
            <img :src="detail.coverPath" />
            <!-- <img src="http://116.62.32.18:9000/kms-bucket/covers/2025/07/21/1ae12c02-a55f-4a5c-8bba-899ddb874306.png" /> -->
          </div>
          
          <!-- 信息列表 -->
          <div class="info-list">
            <div class="info-item">
              <span class="label">创建人</span>
              <span class="value">{{ getUserName(detail.createdBy) }}</span>
            </div>
            <div class="info-item">
              <span class="label">访问次数</span>
              <span class="value">{{ detail.viewCount || 0 }}</span>
            </div>
            <div class="info-item">
              <span class="label">更新时间</span>
              <span class="value">{{ moment(detail.updatedAt).format('YYYY-MM-DD') }}</span>
            </div>
          </div>
        </div>
        <div class="summary-card">
            <h3>摘要</h3>
            <!-- <p class="summary-text">
            {{ detail.summary || '暂无摘要' }}
            </p> -->
            <el-input type="textarea" v-model="detail.summary" style="width: 100%;" :rows="5" />
            <h3 style="margin-top: 10px;">标签</h3>
            <!-- 标签 -->
            <div class="tags">
              <!-- <el-tag type="primary" v-for="tag in tags" :key="tag">{{ tag }}</el-tag> -->
              <el-tag
                v-for="(tag, index) in tags"
                :key="tag"
                closable
                :disable-transitions="false"
                @close="handleCloseTag(index)"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
              />
              <el-button v-else size="small" @click="showInput">
                +添加
              </el-button>
            </div>
        </div>
        <el-button type="primary" size="small" style="position: absolute; right: 20px; bottom: 20px;" @click="handleUpdate">更新</el-button>
      </div>
    </div>
    
    <!-- 右侧内容区域 -->
    <div class="main-content"  :class="{ 'expanded': !assistantVisible }">
      <!-- 文档标题和操作 -->
      <div style="flex: 1; background: white; overflow: hidden;">
        <div class="document-header">
            <h2 class="document-title">{{ detail.fileName }}</h2>
            <div class="document-actions" style="align-items: center;">
            <el-icon v-if="!isFavorite" style="font-size: 21px; cursor: pointer;" @click="favoriteFn(true)"><star /></el-icon>
            <el-icon v-else style="font-size: 25px; cursor: pointer; color: #FFA401;" @click="favoriteFn(false)"><StarFilled /></el-icon>
            <svg  @click="downloadFile" style="width: 25px; height: 25px; cursor: pointer;" xmlns="http://www.w3.org/2000/svg" aria-hidden="true" role="img" class="iconify iconify--ic" width="32" height="32" preserveAspectRatio="xMidYMid meet" viewBox="0 0 24 24" data-v-133a2e38=""><path d="M18 15v3H6v-3H4v3c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2v-3h-2zm-1-4l-1.41-1.41L13 12.17V4h-2v8.17L8.41 9.59L7 11l5 5l5-5z"></path></svg>
            </div>
        </div>
        <div style="flex: 1;" class="original-file" v-loading="isPreviewLoading" element-loading-text="正在加载预览...">
          <div v-if="originalFile" class="file-preview-container">
            <div v-if="previewType === 'pdf'" class="pdf-preview">
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
            <div 
              v-else-if="previewType === 'docx'" 
              ref="docxPreviewContainer"
              class="docx-preview"
            ></div>
            
            <!-- Markdown预览 -->
            <VMdPreview 
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
          </div>
          
          <!-- 无文件时的占位 -->
          <div v-else class="no-file-preview">
            <el-icon size="48" style="color: #c0c4cc"><Folder /></el-icon>
            <p style="color: #909399; margin-top: 16px;">暂无文件预览</p>
          </div>
        </div>
     </div>
      
      <!-- 底部评论区域 -->
      <div class="comment-section">
        <div class="comment-input">
          <el-input
            v-model="commentText"
            type="textarea"
            :rows="3"
            placeholder="这里是一些文案评价"
            class="comment-textarea"
            style="height: 73px"
          />
          <el-button type="primary" size="small" class="submit-btn" @click="submitComment">
            <el-icon><EditPen /></el-icon>
            提交
          </el-button>
        </div>
      </div>
    </div>
    <!-- <div class="right-assistant" :class="{ 'collapsed': !assistantVisible }">
        <div class="assistant-toggle" @click="toggleAssistant">
          <el-icon>
            <ArrowLeft v-if="assistantVisible" />
            <ArrowRight v-else />
          </el-icon>
        </div>
        <div class="assistant-content">
           <Comment ref="commentRef" :targetId="route.query.id" />
        </div>
    </div> -->
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { StarFilled, FullScreen, Download, EditPen, Loading, Document, Folder, ArrowLeft, ArrowRight, Picture } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { listUser } from '@/api/system/user'
import { fileDetail, updateFileContent } from '@/api/base'
import { addFavorite, cancelFavorite, checkFavorite } from '@/api/retrieve'
import Comment from '@/components/comment/index.vue'
import { addComment } from '@/api/comment'

// 文件预览相关导入
import VuePdfEmbed from 'vue-pdf-embed'
import { renderAsync } from 'docx-preview'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import Prism from 'prismjs'
import moment from 'moment'
const target = 'knowledge'
// 配置markdown编辑器
// VuePress 主题默认支持 HTML 标签渲染，无需额外配置
VMdPreview.use(vuepressTheme, {
  Prism,
})

const url = import.meta.env.VITE_APP_BASE_API
const router = useRouter()
const route = useRoute()
const detail = ref({})
const userList = ref([])
const tags = ref([])
const inputVisible = ref(false)
const inputValue = ref('')

const isFavorite = ref(false)
// 评论文本
const commentText = ref('')
const originalFile = ref('')

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

const assistantVisible = ref(true)

// 评论组件引用
const commentRef = ref(null)

// 预览内容
const previewContent = ref('')

const goBack = () => {
  // 检查是否从搜索结果页面进入的详情页
  if (route.query.fromSearch === 'true') {
    console.log('返回到搜索结果页面，携带状态参数...')
    // 携带所有搜索状态参数返回到检索页面
    const searchState = {
      fromSearch: 'true',
      searchQuery: route.query.searchQuery,
      searchType: route.query.searchType,
      maxSegmentCount: route.query.maxSegmentCount,
      rerankModel: route.query.rerankModel,
      fileType: route.query.fileType,
      knowledgeBaseIds: route.query.knowledgeBaseIds,
      knowledgeId: route.query.knowledgeId,
      updateTime: route.query.updateTime,
      currentSort: route.query.currentSort,
      sortDirection: route.query.sortDirection
    }
    
    router.push({
      path: '/space/retrieve',
      query: searchState
    })
  } else {
    // 如果是从其他页面进入的，使用默认返回逻辑
    router.go(-1)
  }
}

const getUserName = (id) => {
  let user = userList.value.find(item => item.id === id)
  if (user) { 
    return user.nickname || user.username
  }
}

const handleCloseTag = (ind) => {
  tags.value.splice(ind, 1)
  detail.value.tags = tags.value.join(',')
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value.input.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    tags.value.push(inputValue.value)
    detail.value.tags = tags.value.join(',')
  }
  inputVisible.value = false
  inputValue.value = ''
}

const handleUpdate = () => {
  updateFileContent(detail.value).then(res => {
    ElMessage.success('更新成功')
  })
}

const submitComment = () => {
  console.log(commentText.value)
  if (commentText.value === '') {
    ElMessage.warning('请输入评论内容')
    return
  }
  let params = {
    targetId: route.query.id,
    content: commentText.value
  }
  addComment(target, params).then(res => {
    ElMessage.success('评论发表成功')
    commentText.value = ''
    if (commentRef.value) {
      commentRef.value.refreshComments()
    }
  })
}

const toggleAssistant = () => {
  assistantVisible.value = !assistantVisible.value
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
  console.log('fileType', fileType)
  try {
    if (fileType === 'md' || fileType === 'txt') {
      // 加载文本内容
      const response = await fetch(fileUrl)
      console.log('response', response)
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`)
      }
      
      const text = await response.text()
      console.log('text', text)
      if (fileType === 'md') {
        markdownContent.value = text
        console.log('Markdown content loaded:', text.substring(0, 100) + '...')
        console.log('Preview type:', previewType.value)
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
          ElMessage.error('DOCX文件预览失败: ' + docxError.message)
        }
      } else {
        console.error('DOCX容器未找到')
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

const downloadFile = () => {
  window.open(originalFile.value, '_blank')
}

// PDF加载成功处理，获取总页数
const handlePdfLoaded = (pdf) => {
  if (pdf && pdf.numPages) {
    totalPages.value = pdf.numPages
  }
  isPreviewLoading.value = false
}

// PDF渲染完成处理
const handlePdfRendered = () => {
  isPreviewLoading.value = false // PDF渲染完成时关闭loading
}

// PDF封面渲染完成处理
const handleCoverRendered = () => {
  isPreviewLoading.value = false // PDF封面渲染完成时关闭loading
}

// 加载预览内容
const loadPreviewContent = async () => {
  if (!originalFile.value) return
  
  isPreviewLoading.value = true // 开始加载时显示loading
  
  try {
    if (previewType.value === 'md' || previewType.value === 'txt') {
      const response = await fetch(originalFile.value)
      if (!response.ok) throw new Error(`HTTP错误! 状态: ${response.status}`)
      
      let text = await response.text()
      // 对于较长的文本,只显示前500个字符
      if (text.length > 500) {
        text = text.substring(0, 500) + '...'
      }
      previewContent.value = text
    } 
    else if (previewType.value === 'docx' && docxPreviewContainer.value) {
      // 确保DOM已经更新
      await nextTick()
      
      const response = await fetch(originalFile.value)
      if (!response.ok) throw new Error(`HTTP错误! 状态: ${response.status}`)
      
      const blob = await response.blob()
      const arrayBuffer = await blob.arrayBuffer()
      
      // 清空容器
      docxPreviewContainer.value.innerHTML = ''
      
      try {
        // 渲染DOCX预览
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
      } catch (renderError) {
        console.error('DOCX渲染错误:', renderError)
        ElMessage.error('文档渲染失败')
      }
    }
  } catch (error) {
    console.error('加载预览内容失败:', error)
    ElMessage.error('预览内容加载失败')
  } finally {
    isPreviewLoading.value = false // 无论成功失败都关闭loading
  }
}

const favoriteFn = async(type) => {
  if (type) {
    await addFavorite({
      targetType: 'knowledge_file',
      targetId: detail.value.id,
    })
    isFavorite.value = true
  } else {
    await cancelFavorite({
      targetType: 'knowledge_file',
      targetId: detail.value.id,
    })
    isFavorite.value = false
  }
}

onMounted(async () => {
  const res = await fileDetail(route.query.id)
  detail.value = res.data
  originalFile.value = url + detail.value.filePath.replace('uploadPath/', 'profile/')
  console.log(' originalFile.value', originalFile.value, detail.value.fileName)
  tags.value = detail.value.tags ? detail.value.tags.split(',') : []
  const resUser = await listUser()
  userList.value = resUser.data
  checkFavorite({
    targetType: 'knowledge_file',
    targetId: detail.value.id,
  }).then(res => {
    isFavorite.value = res.data
  })
  // 文件预览逻辑
  if (detail.value.fileName) {
    const fileType = getFileType(detail.value.filePath)
    previewType.value = fileType
    isPreviewLoading.value = true // 开始加载时显示loading
    
    // 确保DOM已经更新
    await nextTick()
    // 加载预览内容 - 使用正确的函数
    if (fileType === 'md' || fileType === 'txt' || fileType === 'docx') {
      await loadFileContent(originalFile.value, fileType)
    } else {
      isPreviewLoading.value = false
    }
  }
})
</script>

<style scoped lang="scss">
.detail-container {
  display: flex;
  height: 100%;
  background: #f5f5f5;
  padding: 20px;
}

.left-sidebar {
  width: 300px;
  margin-right: 20px;
  height: 100%;
  .info-card {
    background: white;
    padding: 20px;
    height: 100%;
    position: relative;
    .basic-info {
      h3 {
        margin: 0 0 20px 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
      
      .knowledge-graph {
        margin-bottom: 20px;
        text-align: center;
        
        .graph-image {
          width: 100%;
          height: 200px;
          background: #f0f0f0;
          overflow: hidden;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          .preview-container {
            width: 100%;
            height: 100%;
            overflow: hidden;
            background: white;
            padding: 10px;
            
            &.docx-preview-container {
              position: relative;
              padding: 0;
              
              :deep(.docx-wrapper) {
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                transform-origin: top left;
                transform: scale(0.4);
                width: 250%;  // 放大宽度以显示更多内容
                height: auto;
                background: white;
                
                .document-container {
                  margin: 0;
                  padding: 0;
                  background: white;
                  
                  p {
                    margin: 0;
                    line-height: 1.5;
                  }
                }
              }
            }
            
            &.text-preview {
              text-align: left;
              
              pre {
                margin: 0;
                font-size: 12px;
                line-height: 1.5;
                white-space: pre-wrap;
                word-break: break-all;
                max-height: 100%;
                overflow: hidden;
              }
            }
          }
          .preview-image {
            max-width: 100%;
            max-height: 100%;
            object-fit: contain;
          }
          .file-type-display {
            text-align: center;
            
            .file-icon {
              color: #909399;
              margin-bottom: 8px;
            }
            
            .file-ext {
              font-size: 24px;
              font-weight: bold;
              color: #606266;
              margin-bottom: 8px;
            }
            
            .file-name {
              font-size: 12px;
              color: #909399;
              max-width: 90%;
              margin: 0 auto;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
          
          :deep(.vue-pdf-embed) {
            height: 100%;
            
            canvas {
              width: 100% !important;
              height: 100% !important;
              object-fit: cover;
            }
          }
        }
      }
      
      .info-list {
        .info-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 0;
          
          .label {
            color: #666;
            font-size: 14px;
          }
          
          .value {
            color: #333;
            font-size: 14px;
            font-weight: 500;
          }
        }
      }
    }
  }
  
  .summary-card {
    margin-top: 10px;
    h3 {
      margin: 0 0 15px 0;
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
    
    .summary-text {
      color: #666;
      line-height: 1.6;
      margin-bottom: 15px;
      font-size: 14px;
      background: #F9F9F9;
      padding: 10px;
    }
    
    .tags {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;
    }
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column; 
  .document-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 30px;
    border-bottom: 1px solid #f0f0f0;
    
    .document-title {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: #333;
    }
    
    .document-actions {
      display: flex;
      gap: 10px;
    }
  }
  
  .pdf-viewer {
    flex: 1;
    padding: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #fafafa;
    
    .pdf-image {
      max-width: 100%;
      max-height: 100%;
      width: auto;
      height: auto;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      border-radius: 8px;
    }
  }
  
  .comment-section {
    padding: 20px;
    border-top: 1px solid #f0f0f0;
    background: white;
    margin-top: 10px;
    .comment-input {
      position: relative;
      
      .submit-btn {
        position: absolute;
        right: 12px;
        bottom: 12px;
        height: 32px;
        // min-width: 80px;
      }
    }
  }
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-image__error) {
  background: #f5f5f5;
  color: #999;
  font-size: 14px;
}
:deep(.el-textarea__inner) {
  box-shadow: none;
  background: #F9F9F9;
  border: none;
  padding-right: 100px;
}
.info-card {
  :deep(.el-textarea__inner) {
    padding-right: 0;
  }
}
/* 文件预览相关样式 */
.original-file {
  background: #fafafa;
  overflow: auto;
  height: calc(100% - 73px);
  .file-preview-container {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    .preview-loading {
      display: flex;
      align-items: center;
      color: #666;
    }
    
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
        // border-radius: 8px 8px 0 0;
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
        // padding: 10px;
        // background: #f5f5f5;
      }
    }
    
    .docx-preview,
    .markdown-preview,
    .text-preview,
    .image-preview {
      width: 100%;
      height: 100%;
      overflow: auto;
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
    
    
  }
}

/* Markdown预览样式优化 */
:deep(.v-md-editor-preview) {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  
  /* 确保嵌入的 HTML 样式能正常工作 */
  /* 内联样式优先级更高，markdown 中的 HTML 标签样式会正常显示 */
  
  /* 如果需要全局样式调整，可以在这里添加 */
  .center-text {
    text-align: center;
  }
  
  .large-text {
    font-size: 15px;
  }
}

/* DOCX预览样式优化 */
.docx-preview {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: white;
  overflow: auto;
  
  :deep(.docx-wrapper) {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 20px;
    overflow: auto;
    
    .document-container {
      margin: 0;
      padding: 0;
      
      p {
        margin: 0;
        line-height: 1.5;
      }
    }
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
.right-assistant {
    width: 420px;
    height: 100%;
    background: #fff;  
    position: relative;
    margin-left: 10px;
    transition: all 0.3s ease;
    .assistant-content {
      // padding: 24px;
      display: flex;
      flex-direction: column;
      height: 100%;
      gap: 20px; 
    }
    &.collapsed {
      width: 20px;   
      .assistant-content {
        display: none;
      }
    }
  }
  .assistant-toggle {
    position: absolute;
    left: -10px;
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
    z-index: 10;
    &::before {
      content: '';
      position: absolute;
      left: 0;
      border-width: 5px;
      border-style: solid;
      bottom: -10px;
      border-color: #fff #fff transparent transparent;
    }
    &::after {
      content: '';
      position: absolute;
      left: 0;
      border-width: 5px;
      border-style: solid;
      top: -10px;
      border-color: transparent #fff #fff transparent;
    }
  }
.expanded {
  margin-right: 0 !important;
}
</style>
