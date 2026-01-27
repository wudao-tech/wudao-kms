<template>
  <div class="qa-page">
    <div class="qa-container">
      <!-- 标签页 -->
      <div class="tabs-container">
        <div class="tabs">
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'records' }"
            @click="changeTab('records')"
          >
            点赞
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'optimization' }"
            @click="changeTab('optimization')"
          >
            点踩
          </div>
        </div>
         <!-- 筛选和操作区域 -->
      <div class="filters-section">
        <div class="filters">
          <el-date-picker 
          v-model="createTime" 
          type="datetimerange" 
          range-separator="至" 
          start-placeholder="开始日期" 
          end-placeholder="结束日期" 
          style="width: 300px;" 
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="changeTime" 
          clearable 
          />
          
          <el-select v-model="queryParams.createdBy" placeholder="提问人" style="width: 150px;" filterable  @change="getRecordsList" clearable>
            <el-option v-for="item in createdByList" :key="item.id" :label="item.nickname" :value="item.id" />
          </el-select>
          <el-select v-model="queryParams.agentUuid" placeholder="请选择知识专家" style="width: 150px;" @change="getRecordsList" clearable>
            <el-option v-for="item in knowledgeList" :key="item.uuid" :label="item.name" :value="item.uuid" />
          </el-select>
          <el-select v-model="queryParams.adoptionStatus" placeholder="处理状态" style="width: 100px;" @change="getRecordsList" clearable>
            <el-option label="已添加" value="adopted" />
            <el-option label="未处理" value="unadopted" />
            <el-option label="未采纳" value="reject" />
          </el-select>
        </div>
        
        <div class="search-section">
           
          <el-input
            v-model="queryParams.userMessage"
            placeholder="请输入"
            clearable
            style="width: 150px;"
            @input="getRecordsList"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" style="margin-left: 0px;" :disabled="selection.length === 0 || selection.some(item => item.adoptionStatus !== 'unadopted')" @click="handleAddToQa">添加到问答库</el-button>
          <el-button plain @click="handleDelete(null)" :disabled="selection.length === 0">删除</el-button>
          <el-button type="primary" plain @click="handleExport">导出</el-button>
        </div>
      </div>
      </div>
      <!-- 数据表格 -->
      <div class="table-container">
        <el-table ref="tableRef" v-loading="loading" :data="tableData" style="width: 100%" border row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="userMessage" label="问题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="agent" label="回答" min-width="300">
            <template #default="{row}">
              <span class="text-ellipsis">{{ row.agent }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="agentUuid" label="知识专家" width="120" show-overflow-tooltip>
            <template #default="{row}">
              {{ knowledgeList.find(item => item.uuid === row.agentUuid)?.name }}
            </template>
          </el-table-column>
          <el-table-column prop="createdByName" label="提问人" width="120" />
          <el-table-column v-if="activeTab === 'optimization'" prop="" label="优化状态" width="120" >
            <template #default="{row}">
              <span>{{ row.optimizationFlag === true ? '已优化' : '未处理' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="adoptionStatus" label="处理状态" width="120" >
            <template #default="{row}">
              <span v-if="row.adoptionStatus === 'adopted'">已添加</span>
              <span v-else-if="row.adoptionStatus === 'reject'">未采纳</span>
              <span v-else>未处理</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提问时间" width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{row}">
              <el-tooltip content="查看" placement="top">
                 <el-button link  @click="handleView(row, false)" icon="View"></el-button>
              </el-tooltip>
              <el-tooltip content="优化" placement="top">
                <el-button link @click="handleView(row, true)" icon="MagicStick"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link @click="handleDelete(row)" icon="Delete"></el-button>
              </el-tooltip>
              <el-tooltip content="添加到问答库" placement="top">
                <el-button link @click="add(row)" v-if="(activeTab === 'records' || (activeTab === 'optimization' && row.optimizationFlag === true)) && row.adoptionStatus === 'unadopted'">
                   <svg-icon icon-class="anwer_plus" />
                </el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 40, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '优化' : '查看'" width="700" append-to-body @close="handleClose">
       <el-form :model="form" label-width="80px">
        <el-form-item label="问题">
          <el-input v-model="form.userMessage" />
        </el-form-item>
        <el-form-item label="回答">
          <el-input v-model="form.agent" type="textarea" :rows="10" />
        </el-form-item>
       </el-form>
       <template #footer v-if="isEdit">
          <el-button  type="primary" :disabled="!form.userMessage || !form.agent" @click="submitForm">确定</el-button>
        </template>
    </el-dialog>
    <!-- 添加到问答库 -->
    <el-dialog v-model="addToQaVisible" title="添加到问答库" width="450" append-to-body @close="handleClose">
       <!-- <div style="display: flex; align-items: center; margin-bottom: 20px;">
          <span style="font-size: 14px; font-weight: bold; width: 100px;">知识库:</span>
          <el-select v-model="knowledgeId" placeholder="请选择知识库" style="width: 250px;" @change="changeKnowledgeId" clearable>
            <el-option v-for="item in baseList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
         
       </div> -->
       <div style="display: flex; align-items: center; margin-bottom: 10px;">
        <span style="font-size: 14px; font-weight: bold; width: 100px;">知识目录:</span>
        <el-cascader
          v-model="spaceId"
          :options="checkSpaceList"
          :disabled="!knowledgeId"
          style="width: 250px;"
          :show-all-levels="false"
          placeholder="请选择知识目录"
          :props="props1"
          @change="changeKnowledgeSpaceId"
        />
       </div>
       <div style="display: flex; align-items: center;">
          <span style="display: flex; align-items: center; width: 100px;">
            <span style="font-size: 14px; font-weight: bold;">删除记录</span>
            <el-tooltip content="添加到问答库后，该问题是否删除" placement="top">
              <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
            </el-tooltip>
          </span>
          <el-switch v-model="delAfterUse" />
       </div>
       <template #footer>
          <el-button  type="primary" :disabled="spaceId === '' || spaceId.length === 0" @click="addToOptimization">确定</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, nextTick } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getQaTable, getQaRecord, addToQa,  editQaRecord, deleteQaImprove, batchAddToQa, deleteRecordQa } from '@/api/qa'
import { listUserByDeptId } from '@/api/system/user'
const { proxy } = getCurrentInstance()
import { getPublishApp, knowledgeBaseList, knowledgeSpaceList as knowledgeSpaceListApi } from '@/api/base'
import { getAssistantList, getAssistantDetail } from '@/api/knowledgeExpert'

// 响应式数据
const activeTab = ref('records')

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  startTime: '',
  endTime: '',
  createdBy: '',
  type: 'agree',
  userMessage: '',
  agentUuid: ''
})
const props1 = {
  checkStrictly: true,
  label: 'name',
  value: 'id',
  children: 'children'
}
const baseList= ref([]) // 知识管理
const knowledgeSpaceList = ref([]) // 知识目录
const checkSpaceList = ref([])
const searchKeyword = ref('')
const isEdit = ref('records')
const total = ref(500)
const qaTable = ref([])
const form = ref({})
const dialogVisible = ref(false)
const selection = ref([])
const loading = ref(false)
const tableRef = ref(null)
// 表格数据
const tableData = ref([])
const createdByList = ref([])
const createTime = ref([])
const addToQaVisible = ref(false)

const knowledgeList = ref([]) // 知识专家
const knowledgeId = ref('')
const knowledgeSpaceId = ref('')
const spaceId = ref([])
const delAfterUse = ref(false)

const feedbackIds = ref([])

const assisData = ref({})

const changeTime  = (val) => {
  if (val) {
    queryParams.value.startTime = val[0]
    queryParams.value.endTime = val[1]
  } else {
    queryParams.value.startTime = ''
    queryParams.value.endTime = ''
  }
  if (activeTab.value === 'records') {
    getRecordsList()
  } else {
    getQaTableList()
  }
}

const changeKnowledgeSpaceId = (val) => {
  knowledgeSpaceId.value = val[val.length - 1]
}

// 方法
const addToOptimization = () => {
    let params = {
        feedbackIds: feedbackIds.value,
        knowledgeId: knowledgeId.value,
        knowledgeSpaceId: knowledgeSpaceId.value,
        delAfterUse: delAfterUse.value
    }
    console.log('params', params)
    batchAddToQa(params).then(res => {
        ElMessage.success('添加成功')
        addToQaVisible.value = false
        getRecordsList()
    })
}

const handleSelectionChange = (val) => {
  selection.value = val
  feedbackIds.value = val.map(item => item.id)
}

// 处理批量添加到问答库
const handleAddToQa = () => {
  // 如果选择了多个记录，检查它们是否来自同一个知识专家
  if (selection.value.length > 1) {
    const agentUuids = selection.value.map(item => item.agentUuid).filter(Boolean)
    // 检查是否有不同的知识专家
    const uniqueAgentUuids = [...new Set(agentUuids)]
    
    if (uniqueAgentUuids.length > 1) {
      // 如果来自不同的知识专家，提示并阻止打开弹窗
      ElMessage.warning('多个知识专家问答优化时请分开添加')
      return
    }
  }
  
  // 检查通过，打开弹窗
  addToQaVisible.value = true
}

const add = async (row) => {
  addToQaVisible.value = true
  delAfterUse.value = false
  spaceId.value = []
  knowledgeId.value = ''
  feedbackIds.value = [row.id]
  let res = await getAssistantDetail(row.agentUuid)
  assisData.value = res.data
  // 设置知识库ID（可能是单个ID或数组）
  const knowledgeListId = Array.isArray(assisData.value.knowledgeList) 
    ? assisData.value.knowledgeList[0] 
    : assisData.value.knowledgeList
  knowledgeId.value = knowledgeListId || ''
  knowledgeSpaceList.value = assisData.value.knowledgeList
  getKnowledgeSpaceList()
}

// 在树中查找包含目标ID的节点路径
const findNodePath = (tree, targetId, path = []) => {
  for (const node of tree) {
    const currentPath = [...path, node]
    if (node.id === targetId) {
      return currentPath
    }
    if (node.children && node.children.length > 0) {
      const found = findNodePath(node.children, targetId, currentPath)
      if (found) {
        return found
      }
    }
  }
  return null
}

// 根据已有的空间ID数组，筛选出包含这些ID及其所有上级节点的树结构
const filterTreeBySpaceIds = (fullTree, spaceIds) => {
  if (!Array.isArray(fullTree) || !Array.isArray(spaceIds) || spaceIds.length === 0) {
    return []
  }
  
  // 用于存储所有需要的节点ID（包括目标ID和它们的父节点ID）
  const requiredIds = new Set()
  
  // 为每个目标ID查找完整路径
  spaceIds.forEach(targetId => {
    const path = findNodePath(fullTree, targetId)
    if (path) {
      // 将路径中的所有节点ID添加到requiredIds
      path.forEach(node => requiredIds.add(node.id))
    }
  })
  
  // 递归筛选树，只保留requiredIds中的节点及其必要的父节点
  const filterNode = (nodes) => {
    return nodes
      .map(node => {
        const nodeInRequired = requiredIds.has(node.id)
        const hasRequiredChildren = node.children && node.children.length > 0
          ? filterNode(node.children).length > 0
          : false
        
        // 如果节点本身在requiredIds中，或者有子节点在requiredIds中，则保留
        if (nodeInRequired || hasRequiredChildren) {
          const filteredNode = {
            ...node,
            children: node.children && node.children.length > 0
              ? filterNode(node.children)
              : []
          }
          return filteredNode
        }
        return null
      })
      .filter(node => node !== null)
  }
  
  return filterNode(fullTree)
}

const getKnowledgeSpaceList = () => {
  knowledgeSpaceListApi( knowledgeSpaceList.value).then(res => {
    const fullTree = res.data || []
    knowledgeSpaceList.value = fullTree
    
    // 获取当前知识专家下已有的空间ID（最后一层）
    const existingSpaceIds = assisData.value.knowledgeSpaceList || []
    
    // 如果已有选中的空间ID，筛选出包含这些ID及其上级的树结构
    // 但不在打开弹窗时自动回显，等用户自己选择
    if (existingSpaceIds.length > 0 && fullTree.length > 0) {
      checkSpaceList.value = filterTreeBySpaceIds(fullTree, existingSpaceIds)
    } else {
      // 如果没有已有的空间ID，显示完整的树结构供用户选择
      checkSpaceList.value = fullTree
    }
    // 确保spaceId为空，不回显，等用户选择
    spaceId.value = []
  })
}

const handleView = (row, type) => {
  isEdit.value = type
  dialogVisible.value = true
  form.value = {...row }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    let params = {}
    if (row) {
      params.ids = row.id
    } else {
      params.ids = selection.value.map(item => item.id).join(',')
    }
    deleteRecordQa(params).then(res => {
      ElMessage.success('删除成功')
      getRecordsList()
    })
  })
}

const handleClose = () => {
  dialogVisible.value = false
  form.value = {}
  isEdit.value = 'records'
}

const changeTab = (tab) => {
  activeTab.value = tab
  searchKeyword.value = ''
  createTime.value = []
  queryParams.value.startTime = ''
  queryParams.value.endTime = ''
  queryParams.value.pageNum = 1
  queryParams.value.pageSize = 10
  queryParams.value.agentUuid = ''
  queryParams.type = tab === 'records' ? 'agree' : 'disagree'
  getRecordsList()
}

const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  getRecordsList()
}

const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  getRecordsList()
}

const getQaTableList = () => {
    let params = {
        userMessage: searchKeyword.value,
        pageNum: queryParams.value.pageNum,
        pageSize: queryParams.value.pageSize,
        startTime: queryParams.value.startTime,
        endTime: queryParams.value.endTime
    }
    loading.value = true
  getQaTable(params).then(res => {
    qaTable.value = res.data
    total.value = res.total
    loading.value = false
  })
}

const getRecordsList = () => {
  queryParams.value.type = activeTab.value === 'records' ? 'agree' : 'disagree'
  loading.value = true
  getQaRecord(queryParams.value).then(res => {
    tableData.value = res.data
    total.value = res.total
    loading.value = false
  })
}

const handleExport = () => {
  let params = {
    ...queryParams.value,
    ids: selection.value.map(item => item.id)
  }
  console.log('params', params)
    proxy?.download(
      'api/feedback/export',
      {...params},
      `问答记录_${new Date().getTime()}.csv`
    );
}

const submitForm = () => {
  editQaRecord(form.value).then(res => {
    ElMessage.success('编辑成功')
    dialogVisible.value = false
    getRecordsList()
  })
}
const getKnowledgeList = () => {
  let params = {
    pageNum: 1,
    pageSize: 1000,
  }
  getAssistantList(params).then(res => {
    knowledgeList.value = res.data.records
  })
}

const getBaseList = () => {
  let params = {
    pageNum: 1,
    pageSize: 1000
  }
  knowledgeBaseList(params).then(res => {
    baseList.value = res.data
  })
}
onMounted(() => {
//   getQaTableList()
  getRecordsList()
  listUserByDeptId().then(res => {
    createdByList.value = res.data
  })
  getKnowledgeList()
  getBaseList()
})
</script>

<style lang="scss" scoped>
.qa-page {
  width: 100%;
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
}

.qa-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tabs-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tabs {
  display: flex; 
  gap: 30px;
  .tab-item {
    padding: 5px 0;
    cursor: pointer;
    color: #606266;
    font-size: 16px;
    border-bottom: 2px solid transparent;
    transition: all 0.3s;
    
    &:hover {
      color: #722ed1;
    }
    
    &.active {
      color: #722ed1;
      border-bottom-color: #722ed1;
    }
  }
}

.filters-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  .filters {
    display: flex;
    gap: 10px;
  }
  
  .search-section {
    display: flex;
    align-items: center;
    gap: 10px;
  }
}

.table-container {
  flex: 1;
  overflow: hidden;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  .el-table__header {
    th {
      background-color: #fafafa;
      color: #606266;
      font-weight: 600;
    }
  }
}

:deep(.el-pagination) {
  .el-pagination__total {
    color: #606266;
  }
}
</style>