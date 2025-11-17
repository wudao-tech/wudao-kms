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
          <!-- <el-button type="primary" @click="handlePreview">预览</el-button> -->
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
                style="width: 80px;"
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
        <el-button type="primary" size="small" :disabled="!permission" style="position: absolute; right: 20px; bottom: 20px;" @click="handleUpdate">更新</el-button>
      </div>
    </div>
    
    <!-- 右侧内容区域 -->
    <div class="main-content"  :class="{ 'expanded': !assistantVisible }">
      <!-- 文档标题和操作 -->
      <div style="flex: 1; background: white; overflow: hidden;">
        <div class="document-header">
            <h2 class="document-title">{{ detail.fileName }}</h2>
            <div class="document-actions" style="align-items: center;">
              <!-- <el-button size="small" link style="margin-right: 20px;" @click="viewOriginalFile">查看原始文档</el-button> -->
              <el-tooltip :content="isFavorite ? '取消收藏' : '收藏'" placement="top">
                <el-icon v-if="!isFavorite" style="font-size: 21px; cursor: pointer;" @click="favoriteFn(true)"><star /></el-icon>
                <el-icon v-else style="font-size: 25px; cursor: pointer; color: #FFA401;" @click="favoriteFn(false)"><StarFilled /></el-icon>
              </el-tooltip>
              <!-- <svg  @click="downloadFile" style="width: 25px; height: 25px; cursor: pointer;" xmlns="http://www.w3.org/2000/svg" aria-hidden="true" role="img" class="iconify iconify--ic" width="32" height="32" preserveAspectRatio="xMidYMid meet" viewBox="0 0 24 24" data-v-133a2e38=""><path d="M18 15v3H6v-3H4v3c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2v-3h-2zm-1-4l-1.41-1.41L13 12.17V4h-2v8.17L8.41 9.59L7 11l5 5l5-5z"></path></svg> -->
            </div>
        </div>
        <div style="flex: 1;" class="original-file">
          <FilePreview 
            :file-url="originalFile" 
            :file-name="detail.fileName"
            placeholder="暂无文件预览"
            @download="downloadFile"
          />
        </div>
     </div>
      
      <!-- 底部评论区域 -->
       <div class="comment-section custom-scrollbar-container">
         <div class="comment-input">
           <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px;">
             <span>发表评论</span>
             <span style="cursor: pointer;" v-if="commentVisible" @click="commentVisible = !commentVisible">收起评论<el-icon style="margin-left: 5px;"><ArrowUpBold /></el-icon></span>
             <span style="cursor: pointer;" v-else @click="commentVisible = !commentVisible">展开评论<el-icon style="margin-left: 5px;"><ArrowDownBold style="padding-top: 2px;" /></el-icon></span>
           </div>
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
             评论
           </el-button>
         </div>
         <transition name="comment-accordion">
           <div v-if="commentVisible" class="comment-content">
             <Comment ref="commentRef" :targetId="route.query.id" />
           </div>
         </transition>
       </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { StarFilled } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { listUser } from '@/api/system/user'
import { fileDetail, updateFileContent } from '@/api/base'
import { addFavorite, cancelFavorite, checkFavorite } from '@/api/retrieve'
import Comment from '@/components/comment/index.vue'
import { addComment } from '@/api/comment'
import moment from 'moment'
import FilePreview from '@/components/FilePreview/index.vue'

const target = 'knowledge'

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
// 当前人员权限
const permission = ref(false)

const assistantVisible = ref(true)

// 评论组件引用
const commentRef = ref(null)
const commentVisible = ref(false)


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

// Base64 编码函数（支持中文和特殊字符）
const base64Encode = (str) => {
  try {
    // 使用 TextEncoder 将字符串转为 UTF-8 字节数组（推荐方式）
    if (typeof TextEncoder !== 'undefined') {
      const encoder = new TextEncoder()
      const bytes = encoder.encode(str)
      const binary = String.fromCharCode.apply(null, Array.from(bytes))
      return btoa(binary)
    }
    
    // 降级方案：使用 encodeURIComponent + 手动转换（兼容旧浏览器）
    // 先将特殊字符和中文进行 URL 编码，再转换为字节
    const encoded = encodeURIComponent(str)
    const bytes = []
    for (let i = 0; i < encoded.length; i++) {
      const char = encoded[i]
      if (char === '%') {
        // 解析 %XX 格式的编码
        const hex = encoded.substring(i + 1, i + 3)
        bytes.push(parseInt(hex, 16))
        i += 2
      } else {
        bytes.push(char.charCodeAt(0))
      }
    }
    const binary = String.fromCharCode.apply(null, bytes)
    return btoa(binary)
  } catch (e) {
    // 最终降级：直接使用 btoa（仅支持 ASCII）
    console.warn('Base64编码使用降级方案，可能不支持中文:', e)
    try {
      return btoa(str)
    } catch (e2) {
      console.error('Base64编码失败:', e2)
      return str
    }
  }
}

const handlePreview = () => {
  // 使用实际的文件 URL
  const fileUrl = 'http://192.168.3.40:8012/uploads/merged/人工智能研发运营体系（MLOps）实践指南-信通院2023.pdf'
  
  // 预览服务器地址（根据实际情况修改）
  const previewServer = 'http://192.168.3.40:8012'
  const encodedUrl = base64Encode(fileUrl)
  const previewUrl = `${previewServer}/onlinePreview?url=${encodeURIComponent(encodedUrl)}`
  
  window.open(previewUrl, '_blank')
}

const downloadFile = () => {
  window.open(originalFile.value, '_blank')
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
  let userId = JSON.parse(localStorage.getItem('userInfo')).id
  if (userId === detail.value.createdBy) {
    permission.value = true
  } else {
    permission.value = false
  }
  checkFavorite({
    targetType: 'knowledge_file',
    targetId: detail.value.id,
  }).then(res => {
    isFavorite.value = res.data
  })
  
  // 如果是从评论通知跳转过来的，自动展开评论并加载评论列表
  if (route.query.fromComment === 'true') {
    commentVisible.value = true
    // 等待组件渲染完成后加载评论列表
    nextTick(() => {
      if (commentRef.value) {
        commentRef.value.refreshComments()
      }
    })
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
  width: calc(100% - 320px);
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
      height: 25px;
    }
  }
  
  
  .comment-section {
    padding: 20px 10px 20px 10px;
    border-top: 1px solid #f0f0f0;
    background: white;
    margin-top: 10px;
      max-height: 350px;
      overflow-y: auto;
      .comment-input {
        position: relative;
        
        .submit-btn {
          position: absolute;
          right: 12px;
          bottom: 12px;
          height: 32px;
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
  height: calc(100% - 56px);
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

/* 评论手风琴动效 */
.comment-accordion-enter-active,
.comment-accordion-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.comment-accordion-enter-from {
  opacity: 0;
  max-height: 0;
  transform: translateY(-10px);
}

.comment-accordion-leave-to {
  opacity: 0;
  max-height: 0;
  transform: translateY(-10px);
}

.comment-accordion-enter-to,
.comment-accordion-leave-from {
  opacity: 1;
  max-height: 350px;
  transform: translateY(0);
}

.comment-content {
  border-top: 1px solid #f0f0f0;
  margin-top: 10px;
  padding-top: 10px;
}

</style>
