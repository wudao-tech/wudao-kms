<template>
  <div class="detail-container">
    <div v-if="detailType === 0" style="display: flex; height: 100%;">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <!-- <el-icon><ArrowLeftBold /></el-icon> -->
        <el-button link icon="Back" @click="goBack">返回</el-button>
        <span>{{displaySpaceName || '知识空间'}}</span>
      </div>
      
      <!-- 搜索框 -->
      <div class="sidebar-search">
        <el-input v-model="filterText" prefix-icon="Search" />
        <el-button type="primary" style="margin-left: 10px; height: 30px;" :disabled="permissionType === 3" icon="Plus" @click="handleAdd(0)" />
      </div>

      <!-- 树形导航 -->
      <div class="sidebar-tree">
        <el-tree
          ref="treeRef"
          :data="treeData"
          v-loading="treeLoading"
          :props="defaultProps"
          node-key="id"
          highlight-current
          :default-expand-all="true"
          :expand-on-click-node="false"
          class="filter-tree"
          :filter-node-method="filterNode"
          @node-click="handleNodeClick"
        >
          <template #default="{ data }">
            <div style="display: flex; align-items: center; justify-content: space-between; width: 100%;">
              <span class="tree-node">
                <el-icon class="tree-icon">
                  <!-- <Folder v-if="!data.isLeaf" />
                  <Document v-else /> -->
                  <Folder />
                </el-icon>
                <span class="tree-label">{{ data.name }}</span>
              </span>
              <el-dropdown @click.stop trigger="click">
                <el-button link icon="More" style="margin-right: 10px;" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item icon="Plus" :disabled="permissionType === 3" @click="handleAdd(1,data)">新增</el-dropdown-item>   
                    <el-dropdown-item icon="Edit" :disabled="permissionType === 3" @click="handleAdd(2,data)">修改</el-dropdown-item>   
                    <el-dropdown-item icon="Delete" :disabled="permissionType === 3" @click="handleDelete(data)">删除</el-dropdown-item>   
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-tree>
      </div>
    </div>

    <!-- 右侧主内容区 -->
    <div class="main-content">
      <!-- 顶部控制栏 -->
      <div class="content-header">
         <div v-for="(tab, index) in tabs" :key="index" :class="{ 'tab': true, 'tab-item': activeTab === index }" @click="handleTabChange(index)">{{ tab }}</div>
      </div>

      <!-- 数据表格 -->
      <div class="table-container" v-if="activeTab === 0">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
          <div class="header-left">
          <el-input 
            v-model="query.fileName" 
            placeholder="搜索文件" 
            prefix-icon="Search"
            clearable
            class="search-input"
          />
          <el-select v-model="query.fileType" placeholder="全部格式" clearable class="filter-select">
            <el-option value="doc" key="doc" label="doc"></el-option>
            <el-option value="pdf" key="pdf" label="pdf"></el-option>
            <el-option value="txt" key="txt" label="txt"></el-option>
            <el-option value="xlsx" key="xlsx" label="xlsx"></el-option>
            <el-option value="png" key="png" label="png"></el-option>
            <el-option value="jpg" key="jpg" label="jpg"></el-option>
            <el-option value="jpeg" key="jpeg" label="jpeg"></el-option>
            <el-option value="mp3" key="mp3" label="mp3"></el-option>
            <el-option value="mp4" key="mp4" label="mp4"></el-option>
            <el-option value="csv" key="csv" label="csv"></el-option>
            <el-option value="md" key="md" label="md"></el-option>
            <el-option value="wav" key="wav" label="wav"></el-option>
          </el-select>
          <el-select v-model="query.approveStatus" placeholder="请选择状态" style="width: 150px;" clearable>
              <el-option label="已采纳" value="agree" />
              <el-option label="审核中" value="checking" />
              <el-option label="未采纳" value="reject" />
          </el-select>
        </div>
        <div class="header-right">
          <el-button  @click="handleBatchPass('agree')" icon="CircleCheck" v-if="permissionType === 1" :disabled="selectedRows.length === 0 ">采纳</el-button>
          <el-button  @click="handleBatchPass('reject')" icon="CircleClose"  v-if="permissionType === 1" :disabled="selectedRows.length === 0 ">拒绝</el-button>
          <el-button @click="handleSearch" style="margin-right: 12px;" type="primary">搜索</el-button>
          <el-dropdown>
            <el-button>录入知识</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item  @click="handleUpload('file')"  :disabled="permissionType === 3">文件上传</el-dropdown-item>
                <el-dropdown-item @click="handleUpload('url')"  :disabled="permissionType === 3">URL读取</el-dropdown-item>
                <el-dropdown-item @click="handleUpload('online')"  :disabled="permissionType === 3">在线创建</el-dropdown-item>
                <!-- <el-dropdown-item @click="handleUpload('answer')"  :disabled="permissionType === 4">问答上传</el-dropdown-item> -->
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        </div>
        <el-table 
          :data="tableData" 
          stripe 
          border
           @selection-change="handleSelectionChange"
          style="width: 100%; margin-bottom: 10px;">
          <el-table-column type="selection" width="55" :selectable="checkSelectable" />
          <el-table-column prop="fileName" label="名称" show-overflow-tooltip />       
          <el-table-column prop="processingStatus" label="处理状态" />
          <el-table-column prop="createType" label="提交方式" >
            <template #default="scope">
              <span v-if="scope.row.createType === 'UPLOAD'">文件上传</span>
              <span v-else-if="scope.row.createType === 'URL'">URL上传</span>
              <span v-else-if="scope.row.createType === 'ONLINE'">在线创建</span>
            </template>
          </el-table-column>
          <el-table-column prop="" label="处理形式" >
            <template #default="{ row }">
               <el-progress 
                  v-if="row.progress < 100 && row.processingStatus === '处理中'"
                  :percentage="row.progress || 0" 
                  :stroke-width="10"
                  striped
                  :format="(percentage) => `${percentage}%`"
                  :status="row.progress === 100 ? 'success' : undefined"
                />
                <div v-else>
                  <span v-if="row.extraType === 'QA'">问答对提取完成</span>
                  <span v-else>分段提取完成</span>
                </div>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="大小">
            <template #default="scope">
              <span>{{ (scope.row.fileSize / 1024 / 1024).toFixed(2) }}MB</span>
            </template>
          </el-table-column>
          <el-table-column prop="" label="段落/字符">
            <template #default="scope">
              <span>{{ scope.row.pageCount }}段落 / {{ scope.row.wordCount }}字符</span>
            </template>
          </el-table-column>
          <el-table-column prop="updatedAt" label="审批状态" >
            <template #default="{row}">
              <span v-if="row.approveStatus === 'agree'">已采纳</span>
              <span v-else-if="row.approveStatus === 'reject'">未采纳</span>
              <span v-else>审核中</span>
            </template>
          </el-table-column>
          <el-table-column prop="updatedAt" label="最近更新时间" />
          
          <el-table-column label="操作" width="170">
            <template #default="scope">
              <el-tooltip content="查看" placement="top">
                <el-button link size="small" icon="Tickets" @click="handleView(scope.row)" />
              </el-tooltip>
              <el-tooltip content="采纳" placement="top">
                <el-button link v-if="scope.row.approveStatus === 'checking' && permissionType === 1" size="small" icon="CircleCheck" @click="handleAccept(scope.row, 'agree')" />
              </el-tooltip>
              <el-tooltip content="拒绝" placement="top">
                <el-button link v-if="scope.row.approveStatus === 'checking' && permissionType === 1" size="small" icon="CircleClose" @click="handleAccept(scope.row, 'reject')" />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link size="small" icon="Delete" :disabled="permissionType === 3" @click="handleDeleteFile(scope.row)" />
              </el-tooltip>
              <el-tooltip content="分段设置" placement="top">
                <el-button link size="small" icon="Operation" :disabled="permissionType === 3" @click="handleSegment(scope.row)" />
              </el-tooltip>
              <el-tooltip content="分享" placement="top">
                <el-button link size="small" icon="Share" v-if="scope.row.approveStatus === 'agree'" @click="handleShare(scope.row)"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          v-model:page="query.pageNum"
          v-model:limit="query.pageSize"
          :total="total"
          @pagination="getFileList"
        />
      </div>
      <QaPair v-else-if="activeTab === 1 && query.spaceId" :permissionType="permissionType" :spaceId="query.spaceId" />
      <HitTesting v-else-if="activeTab === 2 && query.spaceId" :knowFileList="tableData" :knowledgeBaseIds="query.spaceId" @goToKnowledgeSpace="handleGoToKnowledgeSpace" />
      <Thesaurus v-else-if="activeTab === 3 && query.spaceId" :permissionType="permissionType" :spaceId="query.spaceId" />
    </div>
    </div>

     <!-- 上传文件 -->
     <FileUpload v-else-if="detailType === 4" :uploadType="uploadType" @back="back" :id="props.id" :stepTwo="stepTwo" :onLineObj="onLineObj" :spaceId="query.spaceId" />

    <!-- 新增弹窗 -->
    <el-dialog v-model="dialogVisible" :title="title" width="500" @close="handleClose">
      <el-row style="display: flex; align-items: center; " v-if="spaceObj.parentId !== 0">
        <span style="width: 100px;">父级文件</span>
        <el-input v-model="parentName" style="width: 300px;" disabled />
      </el-row>
      <el-row style="display: flex; align-items: center; margin-top: 10px; ">
        <span style="width: 100px;">文件夹名称</span>
        <el-input v-model="spaceObj.name" style="width: 300px;" placeholder="请输入文件夹名称" />
      </el-row>
      <template #footer>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 问答对 -->
     <el-dialog v-model="answerDialogVisible" title="问答对" width="600px">
       <div class="answer-dialog-content">  
         <div class="upload-section">
           
         </div>
       </div>
       
       <template #footer>
         <el-button @click="answerDialogVisible = false">取消</el-button>
         <el-button type="primary" @click="handleAnswerUpload" :disabled="!selectedFile">
           确定上传
         </el-button>
       </template>
     </el-dialog>
   

  </div>
</template>
<script setup>
import { computed, nextTick } from 'vue'
import FileUpload from './fileUpload.vue'
import Permission from './permission.vue'
import { nanoid } from '@/utils'
import { useRouter, useRoute } from 'vue-router'
import HitTesting from './hittesting.vue'
import Thesaurus from './thesaurus.vue'
import QaPair from './qapair.vue'
import { 
    knowledgeBaseDetail, 
    knowledgeSpaceList, 
    createKnowledgeSpace, 
    updateKnowledgeSpace, 
    deleteKnowledgeSpace,
    knowledgeSpaceFileList,
    deleteFile,
    createTempFile,
    updateFileContent,
    batchAcceptReject
} from '@/api/base'

const props = defineProps({
  id: {
    type: Number,
    default: 6
  },
  spaceName: {
    type: String,
    default: ''
  },
  permissionType: {
    type: Number,
    default: 0
  }
})

const tabs = computed(() => {
  return ['知识列表', '问答对', '命中测试', '词库设置']
})
const selectedRows = ref([])
const activeTab = ref(0)
const router = useRouter()
const route = useRoute()
const detailType = ref(0)
const uploadType = ref('file')
const stepTwo = ref(false)

// 显示知识空间名称，优先使用 props，如果为空则从路由参数读取
const displaySpaceName = computed(() => {
  return props.spaceName || route.query.name || '知识空间'
})

const query = ref({
  pageNum: 1,
  pageSize: 10,
  // knowledgeBaseId: props.id,
  spaceId: '',
  fileName: '',
  fileType: '',
  status: ''
})
const total = ref(0)
// 侧边栏搜索
const filterText = ref('')
const treeRef = ref(null)
const treeLoading = ref(false)
const emit = defineEmits(['back'])

const onLineObj = ref({})

// 新增弹窗
const dialogVisible = ref(false)
const title = ref('新增文件夹')
const spaceObj = ref({
    name: '',
    parentId: 0,
    knowledgeBaseId: props.id
})
const parentName = ref('')

// 问答对弹窗
const answerDialogVisible = ref(false)
const uploadRef = ref(null)
const fileList = ref([])
const selectedFile = ref(null)

// 树形数据
const treeData = ref([])

const defaultProps = {
  children: 'children',
  label: 'name',
}

const handleSearch = () => {
  getFileList()
}

// 表格数据
const tableData = ref([])
// 自动刷新定时器
const refreshTimer = ref(null)

const handleClose = () => {
  dialogVisible.value = false
  spaceObj.value = { name: '', knowledgeBaseId: props.id, parentId: 0 }
  parentName.value = ''
}
const submit = () => {
  const api = spaceObj.value.id ? updateKnowledgeSpace : createKnowledgeSpace
  api(spaceObj.value).then(() => {
    ElMessage.success(spaceObj.value.id ? '修改成功' : '新增成功')
    handleClose()
    getTreeData()
  })
}

const handleTabChange = (index) => {
  activeTab.value = index
}

const handleGoToKnowledgeSpace = (data) => {
  // 切换tab
  activeTab.value = data.tab
}

const handleAccept = (data, status) => {
  let params = {
    id: data.id,
    approveStatus: status,
  }
  updateFileContent(params).then(res => {
    ElMessage.success('操作成功')
    getFileList()
  })
}

const handleDeleteFile = (data) => {
  ElMessageBox.confirm(`确定删除${data.fileName}文件吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteFile(data.id).then(() => {
      ElMessage.success('删除成功')
      getFileList()
    })
  })
}

// 递归查找树形数据中指定ID的节点（通用函数）
const findNodeById = (nodes, targetId) => {
  for (const node of nodes) {
    if (node.id === targetId) return node
    if (node.children?.length) {
      const found = findNodeById(node.children, targetId)
      if (found) return found
    }
  }
  return null
}

const handleAdd = (type, data) => {
  const isEdit = type === 2
  const parentId = type === 0 ? 0 : (type === 1 ? data.id : data.parentId)
  
  spaceObj.value = isEdit 
    ? { ...data } 
    : { name: '', parentId, knowledgeBaseId: props.id }
  
  parentName.value = isEdit 
    ? (data.parentId && data.parentId !== 0 ? findNodeById(treeData.value, data.parentId)?.name || '' : '')
    : (type === 1 ? data.name : '')
  
  dialogVisible.value = true
  title.value = isEdit ? '修改文件夹' : '新增文件夹'
}

const handleDelete = (data) => {
  ElMessageBox.confirm(`确定删除${data.name}吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteKnowledgeSpace(data.id).then(() => {
      ElMessage.success('删除成功')
      getTreeData()
    })
  })
}
// 事件处理
const handleNodeClick = (data) => {
  query.value.spaceId = data.id
  // spaceObj.value = data
  getFileList()
}

const handleView = (data) => {
  router.push({
    path: '/space/retrieve/detail',
    query: {
      id: data.id,
      knowledgeBaseId: data.knowledgeBaseId,
      type: 3
    }
  })
}

// 分享功能
const handleShare = (data) => {
  const url = import.meta.env.VITE_APP_BASE_API
  
  // 构建文件链接
  if (!data.filePath) {
    ElMessage.warning('文件链接未加载，请稍后再试')
    return
  }
  
  // 构建完整的分享 URL
  let shareUrl = url + data.filePath.replace('uploadPath/', 'profile/')
  
  // 如果 shareUrl 不是完整的 URL（不以 http:// 或 https:// 开头），则使用当前域名
  if (!shareUrl.startsWith('http://') && !shareUrl.startsWith('https://')) {
    shareUrl = window.location.origin + shareUrl
  }
  
  console.log('shareUrl', shareUrl)
  
  // 复制到剪贴板
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(shareUrl).then(() => {
      ElMessage.success('文件链接已复制到剪贴板')
    }).catch(() => {
      // 降级方案：使用传统方法复制
      copyToClipboard(shareUrl)
    })
  } else {
    // 降级方案：使用传统方法复制
    copyToClipboard(shareUrl)
  }
}

// 传统方法复制到剪贴板（降级方案）
const copyToClipboard = (text) => {
  const textArea = document.createElement('textarea')
  textArea.value = text
  textArea.style.position = 'fixed'
  textArea.style.left = '-999999px'
  textArea.style.top = '-999999px'
  document.body.appendChild(textArea)
  textArea.focus()
  textArea.select()
  
  try {
    const successful = document.execCommand('copy')
    if (successful) {
      ElMessage.success('文件链接已复制到剪贴板')
    } else {
      ElMessage.error('复制失败，请重试')
    }
  } catch (err) {
    console.error('复制失败:', err)
    ElMessage.error('复制失败，请重试')
  } finally {
    document.body.removeChild(textArea)
  }
}

const goBack = () => {
  emit('back')
}

const back = () => {
  detailType.value = 0
  stepTwo.value = false
  onLineObj.value = {}
  getFileList()
}

// 查找节点路径（从根到目标节点的所有节点ID）
const findNodePath = (nodes, targetId, path = []) => {
  for (const node of nodes) {
    const currentPath = [...path, node.id]
    if (node.id === targetId) return currentPath
    if (node.children?.length) {
      const found = findNodePath(node.children, targetId, currentPath)
      if (found) return found
    }
  }
  return null
}

// 展开树节点路径
const expandNodePath = (nodeIds) => {
  if (!treeRef.value?.store?.nodesMap) return
  nodeIds.forEach(nodeId => {
    const node = treeRef.value.store.nodesMap[nodeId]
    if (node?.childNodes?.length && !node.expanded) {
      node.expand()
    }
  })
}

const getTreeData = () => {
  treeLoading.value = true
  knowledgeSpaceList(props.id).then(res => {
    treeData.value = res.data
    
    // 检查路由参数中是否有 spaceId
    const routeSpaceId = route.query.spaceId
    const spaceIdNum = routeSpaceId ? (typeof routeSpaceId === 'string' ? parseInt(routeSpaceId) : routeSpaceId) : null
    const targetNode = spaceIdNum ? findNodeById(res.data, spaceIdNum) : null
    route.query.tab && handleTabChange(Number(route.query.tab))
    // 设置目标节点或默认第一个节点
    spaceObj.value = targetNode || res.data[0]
    query.value.spaceId = spaceObj.value.id
    
    // 设置树节点选中和展开
    nextTick(() => {
      nextTick(() => {
        if (!treeRef.value || !spaceObj.value) return
        
        // 展开到目标节点的路径
        if (targetNode && routeSpaceId) {
          const expandPath = findNodePath(res.data, spaceObj.value.id)
          if (expandPath) {
            expandNodePath(expandPath.slice(0, -1))
          }
        }
        
        // 设置选中节点
        treeRef.value.setCurrentKey(spaceObj.value.id)
      })
    })
    
    getFileList()
    treeLoading.value = false
  })
}

const handleBatchPass = (status) => {
  console.log('handleBatchPass')
  batchAcceptReject({
    fileId: selectedRows.value.map(item => item.id),
    status: status
  }).then(res => {
    ElMessage.success('操作成功')
    selectedRows.value = []
    getFileList()
  })
}

// 判断行是否可选（只有审批状态为 checking 的才可选）
const checkSelectable = (row) => {
    return row.approveStatus === 'checking'
}

const handleSelectionChange = (selection) => {
    selectedRows.value = selection
}

const handleSegment = (data) => {
  data.fileMd5 = data.fileMd5
  data.fileId = data.id
  data.localSourcePath = data.filePath
  data.parseResult = '解析完成'
  let editData = {
    knowledgeBaseId: props.id,
    knowledgeSpaceId: query.value.spaceId,
    knowledgeFileUploadTempDTOList: [data]
  }
  onLineObj.value = data
  if (data.createType === 'ONLINE') {
    uploadType.value = 'online'
  } else if (data.createType === 'URL') {
    uploadType.value = 'url'
  } else {
    uploadType.value = 'file'
  }
  
  createTempFile(editData).then(res => {
    detailType.value = 4
    stepTwo.value = true
  })
}
// 获取文件列表
const getFileList = () => {
  knowledgeSpaceFileList(query.value).then(res => {
    tableData.value = res.data.map(item => ({ ...item, progress: getProgress(item) }))
    total.value = res.total
    checkAndStartAutoRefresh()
  })
}

// 计算进度
const getProgress = (item) => {
  // 检查必要的数据是否存在
  if (!item || !item.knowledgeSegmentConfig || !item.processProgress) {
    return 0
  }
  // 都是各类型总数
  let qaCount = 0 
  let vlCount = 0
  let vectorCount = 0
  // 所有总数
  let allCount = 0
  // 计算进度数量
  let progressCount = 0
  
  const config = item.knowledgeSegmentConfig || {}
  const processProgress = item.processProgress || {}
  const pageCount = item.pageCount || 0
  
  if (config.qaExtract) {
    qaCount = pageCount
  } else {
    vectorCount = pageCount
  }
  if (config.vlFlag) vlCount = pageCount
  allCount = qaCount + vlCount + vectorCount
  
  if (allCount === 0) {
    return 0
  }
  
  progressCount = (processProgress.qaFinish || 0) + (processProgress.vlFinish || 0) + (processProgress.vectorFinish || 0)
  return (progressCount / allCount * 100).toFixed(2)
}

// 检查并启动自动刷新
const checkAndStartAutoRefresh = () => {
  // 清除之前的定时器
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
    refreshTimer.value = null
  }
  
  // 检查是否有未完成的文件
  const hasUnfinishedFiles = tableData.value.some(item => 
    item.processingStatus === '处理中'
  )
  
  if (hasUnfinishedFiles) {
    // 启动自动刷新
    refreshTimer.value = setInterval(() => {
      getFileList()
    }, 1500) // 每3秒刷新一次
  }
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
    refreshTimer.value = null
  }
}

const handleUpload = (type) => {
  if (type === 'answer') {
    answerDialogVisible.value = true
    return
  }
  uploadType.value = type
  detailType.value = 4
}



// 处理问答对上传
const handleAnswerUpload = () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  
  const formData = new FormData()
  formData.append('file', selectedFile.value)
  formData.append('knowledgeBaseId', props.id)
  formData.append('spaceId', query.value.spaceId)
  formData.append('createType', 'ANSWER')
  
  // 这里调用上传API
  ElMessage.success('问答对上传功能开发中...')
  answerDialogVisible.value = false
  
  // 重置状态
  selectedFile.value = null
  fileList.value = []
  uploadRef.value?.clearFiles()
}

const filterNode = (value, data) => {
  if (!value) return true;
  return data.name.includes(value);
};
watch(filterText, (val) => {
  treeRef.value ? treeRef.value.filter(val) : '';
});
watch(() => props.id, (newVal) => {
    getTreeData()
}, {immediate: true})

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  stopAutoRefresh()
})
</script>
<style scoped lang="scss">
.detail-container {
  height: 100%;
  background-color: #f5f5f5;
}

// 左侧侧边栏样式
.sidebar {
  width: 280px;
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  
  .sidebar-header {
    height: 50px;
    padding: 0 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e4e7ed;
    font-weight: 600;
  }
  
  .sidebar-search {
    display: flex;
    padding: 10px;
  }
  
  .sidebar-tree {
    flex: 1;
    overflow-y: auto;
    
    .filter-tree {
      background: transparent;
      
      :deep(.el-tree-node__content) {
        height: 36px;
        
        &:hover {
          background-color: #f5f7fa;
        }
      }
      
      .tree-node {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .tree-icon {
          color: #409eff;
        }
        
        .tree-label {
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }
}

// 右侧主内容区样式
.main-content {
  width: calc(100% - 280px);
  display: flex;
  flex-direction: column;
  
  .content-header {
    height: 50px;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 50px;
  }
  .tab {
    height: 100%;
    line-height: 50px;
    cursor: pointer;
  }
  .tab-item {
    border-bottom: 2px solid #6b05a8;
    color: #6b05a8;
  }
  .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .search-input {
        width: 200px;
      }
      
      .filter-select {
        width: 120px;
      }
    }
  .table-container {
    flex: 1;
    background-color: #fff;
    margin: 20px;
    padding: 20px;
  }
}

// 问答对弹窗样式
.answer-dialog-content {
  .template-section {
    text-align: center;
    padding: 20px 0;
    
    h4 {
      margin: 0 0 10px 0;
      color: #303133;
      font-size: 16px;
    }
    
    p {
      margin: 0 0 20px 0;
      color: #606266;
      font-size: 14px;
    }
  }
  
  .upload-section {
    h4 {
      margin: 0 0 15px 0;
      color: #303133;
      font-size: 16px;
    }
  }
}

// 响应式处理
@media (max-width: 1200px) {
  .sidebar {
    width: 240px;
  }
}
</style>

