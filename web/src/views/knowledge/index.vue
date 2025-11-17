<template>
  <div class="knowledge-space">
   <div v-if="type === 1" style="height: 100%; display: flex; flex-direction: column;">
       <!-- 页面头部 -->
    <div class="library-top">
      <span class="page-title"></span>
      <div class="top-controls">
        <el-input 
          v-model="queryParams.name" 
          prefix-icon="Search" 
          placeholder="搜索知识库" 
          class="search-input"
          clearable
        />
        <el-select v-model="tags" placeholder="全部标签" collapse-tags style="width: 150px;" collapse-tags-tooltip clearable multiple @change="handleTagChange" class="filter-select">
          <el-option v-for="tag in dynamicTags" :key="tag" :label="tag" :value="tag" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="全部状态"  clearable class="filter-select">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="getKnowledgeBaseList">搜索</el-button>
        <el-button type="primary" class="create-btn" @click="createKnowledge">创建知识库</el-button>
      </div>
    </div>

    <!-- 知识库列表 -->
    <div style="flex: 1;"  class="custom-scrollbar-container">
      <div class="library-grid" v-loading="loading">
        <div 
          v-for="(library, index) in libraries" 
          :key="index" 
          class="library-card"
        >
          <div class="card-header" style="cursor: pointer;" @click="handleLibraryClick(library)">
            <div class="library-info">
              <div class="library-icon">
                <!-- <el-icon size="24" color="#409eff"><Folder /></el-icon> -->
                 <img src="@/assets/images/library.svg" alt="icon" style="width: 24px;">
              </div>
              <div class="library-details">
                <div class="library-name">{{ library.name }} 
                  <el-tag v-if="library.knowledgePermissionType === 1" size="small" style="margin-left: 10px;">管理员</el-tag>
                  <el-tag v-if="library.knowledgePermissionType === 2" size="small" style="margin-left: 10px;">读写</el-tag>
                  <el-tag v-if="library.knowledgePermissionType === 3" size="small" style="margin-left: 10px;">只读</el-tag>
                </div>
                <div class="library-meta">
                  <span>文件内容：{{ library.actualFileCount }}个文件 / {{ library.segmentCount || 0 }}分段 / {{ library.wordCount || 0 }}字数</span>
                  <span class="file-size">文件大小：{{ (library.actualTotalSize / 1024 / 1024).toFixed(2) + 'MB' }}</span>
                </div>
              </div>
            </div>
            <div class="card-status">
              <!-- <el-tag 
                :type="library.status === 1 ? 'success' : 'warning'" 
              >
                {{ library.status === 1 ? '启用' : '禁用' }}
              </el-tag> -->
              <div @click.stop>
                <el-switch v-model="library.status" :active-value="1" :inactive-value="0" :disabled="library.knowledgePermissionType === 3" size="small" @change="handleStatusChange(library)" />
                <span v-if="library.status === 1" style="font-size: 14px; margin-left: 5px;">已启用</span>
                <span v-else style="font-size: 14px; margin-left: 5px;">禁用</span>
              </div>
            </div>
          </div>
          
          <div class="card-footer">
            <span class="update-time">{{ library.createdAt }} <span style="color: #999; font-size: 12px; margin-left: 20px;">{{ library.createdByName }}</span></span>
            <div class="card-actions">
              <!-- 上传文件下拉菜单 -->
              <el-button link icon="Upload" :disabled="library.knowledgePermissionType === 3" size="small" @click="fileUploadFn('file', library.id)">上传文件</el-button>
              <el-button link icon="Pointer" :disabled="library.knowledgePermissionType === 3" size="small" @click="fileUploadFn('url', library.id)">URL提取</el-button>
              <el-button link icon="ChatLineSquare" :disabled="library.knowledgePermissionType === 3" size="small" @click="fileUploadFn('online', library.id)">在线创建</el-button>
              <!-- 更多操作下拉菜单 -->
              <el-dropdown @command="handleMoreCommand">
                <el-button plain  icon="MoreFilled" size="small"> </el-button>
                <!-- <el-icon><MoreFilled/></el-icon> -->
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :disabled="library.knowledgePermissionType === 3" :command="{types: 'edit', library}">编辑</el-dropdown-item>
                    <!-- <el-dropdown-item :command="{types: 'status', library}"> {{ library.status === 1 ? '禁用' : '启用' }}</el-dropdown-item> -->
                    <el-dropdown-item v-if="library.knowledgePermissionType !== 1" :command="{types: 'permission', library}">申请权限</el-dropdown-item>
                    <el-dropdown-item v-if="library.knowledgePermissionType === 1" :command="{types: 'manage', library}">权限管理</el-dropdown-item>
                    <el-dropdown-item :command="{types: 'delete', library}" :disabled="library.knowledgePermissionType !== 1">删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
        
      </div>
      <pagination
          v-show="total > 10"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          :total="total"
          @pagination="getKnowledgeBaseList"
        />
    </div>
   </div>
   <Permission v-else-if="type === 2" @back="back" :id="detailId" :userList="userList" />
   <Detail v-else-if="type === 3" :id="detailId" @back="back" :permissionType="permissionType" :spaceName="spaceName" />
   <!-- 上传文件 -->
   <FileUpload v-else-if="type === 4" :uploadType="uploadType" @back="back" :id="detailId" :spaceId="spaceId" />

    <el-dialog v-model="dialogVisible" :title="modelForm.id ? '编辑知识库' : '创建知识库'" width="700px" @close="cancelForm(formRef)">
      <el-form ref="formRef" :model="modelForm" :rules="rules" label-width="120px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="modelForm.name" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="tagList"
            multiple
            filterable
            allow-create
            default-first-option
            :reserve-keyword="false"
            placeholder="请选择标签"
            @change="handleTag"
          >
            <el-option
              v-for="item in dynamicTags"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="索引模型" prop="embeddingModel">
          <template #label>
              <div style="display: flex;">
                  <span>索引模型</span>
                  <el-tooltip content="将自然语言转化为向量，用于语义检索。注意：索引模型不可混用，且不支持后续修改" placement="top" >
                  <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                  </el-tooltip>
              </div>
          </template>
          <el-select v-model="modelForm.embeddingModel" clearable  :disabled="modelForm.id !== ''">
              <el-option
                  v-for="item in modelList.filter(item => item.modelType === 'embedding_model')"
                  :key="item.model"
                  :label="item.name"
                  :value="item.model"
                  >
                  <div style="display: flex; align-items: center; gap: 8px;">
                     <img :src="item.modelIcon" style="width: 30px;" alt="">
                     <span>{{ item.name }}</span>
                  </div>
              </el-option>
              <template #label="{ label, value }">
                  <div style="display: flex; align-items: center; gap: 8px;">
                      <img :src="modelList.find(item => item.model === value)?.modelIcon" style="width: 24px;" alt="">
                      <span>{{ label }}</span>
                  </div>
               </template>
            </el-select>
        </el-form-item>
        <el-form-item label="图片理解模型" prop="multimodalModel">
          <template #label>
              <div style="display: flex;">
                  <span>图片理解模型</span>
                  <el-tooltip content="用户理解图片，视频，音频等文件，生成对应文本描述" placement="top" >
                  <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                  </el-tooltip>
              </div>
          </template>
          <el-select v-model="modelForm.multimodalModel" clearable>
              <el-option
                  v-for="item in modelList.filter(item => item.modelType === 'multimodal')"
                  :key="item.model"
                  :label="item.name"
                  :value="item.model"
                  >
                  <div style="display: flex; align-items: center; gap: 8px;">
                     <img :src="item.modelIcon" style="width: 30px;" alt="">
                     <span>{{ item.name }}</span>
                  </div>
              </el-option>
              <template #label="{ label, value }">
                  <div style="display: flex; align-items: center; gap: 8px;">
                      <img :src="modelList.find(item => item.model === value)?.modelIcon" style="width: 24px;" alt="">
                      <span>{{ label }}</span>
                  </div>
               </template>
            </el-select>
        </el-form-item>
        <el-form-item label="文本理解模型" prop="textModel">
          <template #label>
              <div style="display: flex;">
                  <span>文本理解模型</span>
                  <el-tooltip content="用户理解总结，生成文本内容" placement="top" >
                  <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                  </el-tooltip>
              </div>
          </template>
          <el-select v-model="modelForm.textModel" clearable>
              <el-option
                  v-for="item in modelList.filter(item => item.modelType === 'text_generation')"
                  :key="item.model"
                  :label="item.name"
                  :value="item.model"
                  >
                  <div style="display: flex; align-items: center; gap: 8px;">
                     <img :src="item.modelIcon" style="width: 30px;" alt="">
                     <span>{{ item.name }}</span>
                  </div>
              </el-option>
              <template #label="{ label, value }">
                  <div style="display: flex; align-items: center; gap: 8px;">
                      <img :src="modelList.find(item => item.model === value)?.modelIcon" style="width: 24px;" alt="">
                      <span>{{ label }}</span>
                  </div>
               </template>
            </el-select>
        </el-form-item>
        <el-form-item label="权限" prop="permissionType">
          <el-radio-group v-model="modelForm.permissionType" :disabled="modelForm.id !== '' && user.id !== modelForm.createdBy">
            <el-radio :value="1">公开</el-radio>
            <el-radio :value="2">私有</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="modelForm.description" type="textarea" :rows="3" />
        </el-form-item>
        
      </el-form>
      <template #footer>
        <div style="display: flex; justify-content: space-between">
          <div style="font-size: 12px;color: #999; text-align: center; margin-left: 100px;">提示: 请确保您的内容严格遵守相关法律法规,避免包含<br/>任何违法或侵权的内容，请谨慎上传可能涉及敏感信息的资料</div>
          <div>
            <el-button @click="cancelForm(formRef)">取消</el-button>
            <el-button type="primary" @click="submitForm(formRef)">确定</el-button>
          </div>
        </div>
        
      </template>
    </el-dialog>
    <el-dialog v-model="permissionDialogVisible" title="权限申请" width="30%" @close="cancel(permissionFormRef)">
       <el-form ref="permissionFormRef" :model="permissionForm" :rules="rules1" label-width="80px">
        <el-form-item label="权限" prop="targetPermissionType">
          <el-select v-model="permissionForm.targetPermissionType" placeholder="请选择权限">
            <!-- <el-option label="只读" :value="3"> </el-option> -->
            <el-option label="读写" :value="2"> </el-option>
            <el-option label="管理" :value="1"> </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="申请理由" prop="applicationReason">
          <el-input v-model="permissionForm.applicationReason" />
        </el-form-item>
       </el-form>
       <template #footer>
        <el-button @click="cancel(permissionFormRef)">取消</el-button>
        <el-button type="primary" @click="submit(permissionFormRef)">确定</el-button>
      </template>
    </el-dialog>

    <!-- 上传选择目录 -->
    <el-dialog v-model="uploadDialogVisible" title="上传选择目录" width="30%" @close="uploadDialogVisible = false">
      <div style="display: flex;align-items: center;">
        <span style="width: 100px;">目录选择</span>
        <el-tree-select
          v-model="spaceId"
          :props="defaultProps"
          :data="treeData"
          node-key="id"
          :render-after-expand="false"
        />
      </div>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="selectSpace">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>

import { 
  knowledgeBaseList, 
  createKnowledgeBase, 
  knowledgeBaseTagList, 
  updateKnowledgeBase,
  deleteKnowledgeBase,
  applyPermission,
  knowledgeSpaceList
} from '@/api/base'
import {  getModel } from '@/api/retrieve'
import Permission from './components/permission.vue'
import Detail from './components/detail.vue'
import FileUpload from './components/fileUpload.vue'
import { listUser } from '@/api/system/user'
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()
const type = ref(1)  // 1: 知识首页，2: 权限管理，3: 知识空间，4: 上传文件
const detailId = ref(null)

const defaultProps = {
  children: 'children',
  label: 'name',
}
const tagList = ref([])
const treeData = ref([])
const spaceId = ref('')

const modelList = ref([])
// 响应式数据
const user = ref(null)
const proxy = getCurrentInstance()
const userList = ref([])
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: null,
  tags: '',
})
const spaceName = ref('')
const tags = ref([])
const loading = ref(false)
const search = ref('')
const selectedTag = ref('all')
const selectedStatus = ref('all')
const dialogVisible = ref(false)
const formRef = ref(null)
const modelForm = ref({
  name: '',
  tags: '',
  permissionType: 1,
  description: '',
  id: '',
  embeddingModel: '',
  multimodalModel: '',
  textModel: ''
})

const permissionType = ref(null)

const rules = ref({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  embeddingModel: [{ required: true, message: '请选择索引模型', trigger: 'blur' }],
  multimodalModel: [{ required: true, message: '请选择图片理解模型', trigger: 'blur' }],
  textModel: [{ required: true, message: '请选择文本理解模型', trigger: 'blur' }]
})

const uploadDialogVisible = ref(false)

const permissionDialogVisible = ref(false)
const permissionFormRef = ref(null)
// 权限申请表单
const permissionForm = ref({
  applicantUserId: null,
  applicationReason: '',
  knowledgeBaseId: null,
  targetPermissionType: null // 3-只读，2-读写，1-管理
})
const rules1 = ref({
  targetPermissionType: [{ required: true, message: '请选择权限', trigger: 'change' }],
  applicationReason: [{ required: true, message: '请输入申请理由', trigger: 'blur' }]
})

const dynamicTags = ref([])

const uploadType = ref('file')

// 知识库数据
const libraries = ref([])
const total = ref(0)


const createKnowledge = () => {
  dialogVisible.value = true
}


const cancelForm = async (formEl) => {
  if (!formEl) return
  formEl.resetFields()
  dialogVisible.value = false
  modelForm.value = {
    name: '',
    tags: '',
    permissionType: 1,
    description: '',
    id: '',
    embeddingModel: '',
    multimodalModel: '',
    textModel: ''
  }
  tagList.value = []
}

const submitForm = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      if (modelForm.value.id) {
        updateKnowledgeBase(modelForm.value).then(res => {
          ElMessage.success('编辑成功')
          cancelForm(formEl)
          getKnowledgeBaseList()
        })
      } else {
        createKnowledgeBase(modelForm.value).then(res => {
          ElMessage.success('创建成功')
          cancelForm(formEl)
          getKnowledgeBaseList()
        })
      }
    }
  })
}

const handleTagChange = (val) => {
  console.log('val', val)
  queryParams.value.tags = val.join(',')
}

const handleTag = (val) => {
  modelForm.value.tags = val.join(',')
}

const back = () => {
  // 返回首页时清理URL参数
  const cleanUrl = window.location.pathname;
  history.pushState({}, '', cleanUrl);
  type.value = 1
  getKnowledgeBaseList()
}

const cancel = async (formEl) => {
  if (!formEl) return
  formEl.resetFields()
  permissionDialogVisible.value = false
  permissionForm.value = {
    applicantUserId: null,
    applicationReason: '',
    knowledgeBaseId: null,
    targetPermissionType: null
  }
}

const submit = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
       applyPermission(permissionForm.value).then(res => {
        ElMessage.success('权限添加成功')
        cancel(formEl)
       })
    }
  })
}

const fileUploadFn = (val, id) => {
  uploadType.value = val
  detailId.value = Number(id) // 确保是数字类型
  spaceId.value = ''
  uploadDialogVisible.value = true
  knowledgeSpaceList(id).then(res => {
      treeData.value = res.data
  })
}

const selectSpace = () => {
 
  console.log('spaceId', spaceId.value)
  uploadDialogVisible.value = false
  type.value = 4
}
const handleLibraryClick = (row) => {
  // 跳转到detail时，带上type=3和id参数
  router.push({
    path: '/space/knowledge',
    query: { ...route.query, id: row.id, type: 3, name: row.name }
  })
  type.value = 3
  detailId.value = Number(row.id) // 确保是数字类型
  permissionType.value = row.knowledgePermissionType
  spaceName.value = row.name
}

const handleStatusChange = (library) => {
  updateKnowledgeBase({
    id: library.id,
    status: library.status
  }).then(res => {
    ElMessage.success('操作成功')
  })
}

// 处理更多操作下拉菜单点击
const handleMoreCommand = (command, row) => {
  const { types, library } = command
  switch (types) {
    case 'edit':
      tagList.value = library.tags.split(',')
      modelForm.value = { ...library }
      dialogVisible.value = true
      break
    case 'test':
      // 处理命中测试
      router.push({
        path: '/space/hittesting',
        query: {
          id: library.id
        }
      })
      break
    case 'permission':
      // 处理申请权限
      permissionDialogVisible.value = true
      permissionForm.value.knowledgeBaseId = library.id
      applicantUserId.value = user.value.id
      break
    case 'manage':
      // 处理权限管理
      type.value = 2
      detailId.value = Number(library.id) // 确保是数字类型
      break
    // case 'status':
    //   // 处理启用禁用
    //   ElMessageBox.confirm(
    //   `确定${library.status === 1 ? '禁用' : '启用'}吗？`,
    //   {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //     type: 'warning',
    //   }
    // ).then(() => {
    //     updateKnowledgeBase({
    //       id: library.id,
    //       status: library.status === 1 ? 0 : 1
    //     }).then(res => {
    //       ElMessage.success('操作成功')
    //       getKnowledgeBaseList()
    //     })
    //   })
    //   break
    case 'delete':
    ElMessageBox.confirm(
      `确定删除吗${library.name}？`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(() => {
        deleteKnowledgeBase(library.id).then(res => {
          ElMessage.success('删除成功')
          getKnowledgeBaseList()
        })
      })
      break
  }
}
const getKnowledgeBaseList = async () => {
  loading.value = true
  const res = await knowledgeBaseList(queryParams.value)
  libraries.value = res.data
  total.value = res.total
  loading.value = false
}
const getKnowledgeBaseTagList = async () => {
  const res = await knowledgeBaseTagList()
  dynamicTags.value = res.data
  // typeTags.value = res.data.map(item => ({
  //   label: item,
  //   type: false
  // }))
}

const getUser = () => {
  listUser({pageNum: 1, pageSize: 1000}).then(res => {
    if (res && res.code === 'ok') {
      userList.value = res.data
    }
    
  })
}
onMounted(async () => {
  // 根据URL参数自动设置type和detailId
  const { id, type: urlType, name } = route.query
  if (id && urlType == 3) {
    type.value = 3
    detailId.value = Number(id) // 转换为数字类型
    // 从路由参数读取知识库名称，刷新后保持显示
    if (name) {
      spaceName.value = name
    }
  } else {
    type.value = 1
  }
  user.value = JSON.parse(localStorage.getItem('userInfo'))
  await getKnowledgeBaseList()
  // 如果从路由参数进入，尝试从知识库列表中获取权限类型
  if (id && urlType == 3) {
    const library = libraries.value.find(lib => lib.id === Number(id))
    if (library) {
      permissionType.value = library.knowledgePermissionType
      // 如果路由参数中没有name，使用知识库列表中的name
      if (!name && library.name) {
        spaceName.value = library.name
      }
    }
  }
  getKnowledgeBaseTagList()
  getUser()
  let params = {
    pageNum: 1,
    pageSize: 1000,
  }
  getModel(params).then(res => {
    modelList.value = res.data
    console.log('modelList', modelList.value)
  })
})
</script>

<style lang="scss" scoped>
:deep(.el-loading-mask) {
  background: none;
}
.knowledge-space {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

// 顶部导航栏样式
.top-nav {
  width: 100%;
  height: 60px;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .nav-menu {
    .nav-menu-items {
      border-bottom: none;
      
      :deep(.el-menu-item) {
        height: 60px;
        line-height: 60px;
        padding: 0 20px;
        
        &.is-active {
          color: #7c3aed;
          border-bottom-color: #7c3aed;
        }
      }
    }
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .nav-icon {
      font-size: 20px;
      color: #606266;
      cursor: pointer;
      
      &:hover {
        color: #409eff;
      }
    }
  }
}

.library-top {
  width: 100%;
  height: 60px;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }

  .top-controls {
    display: flex;
    align-items: center;
    gap: 12px;

    .search-input {
      width: 240px;
    }

    .filter-select {
      width: 120px;
    }
  }
}

// 知识库网格布局
.library-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 20px;
  min-height: 200px;
  justify-content: space-between;
}

// 知识库卡片样式
.library-card {
  background-color: #fff;
  width: calc(50% - 10px);
  border-radius: 8px;
  padding: 20px 20px 10px 20px;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .library-info {
      display: flex;
      gap: 12px;
      flex: 1;

      .library-icon {
        width: 40px;
        height: 40px;
        background: #EAF5FF;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .library-details {
        flex: 1;

        .library-name {
          font-size: 16px;
          // font-weight: 600;
          color: #303133;
          margin-bottom: 8px;
          display: flex;
          align-content: center;
        }

        .library-meta {
          font-size: 14px;
          color: #909399;
          line-height: 1.4;

          .file-size {
            margin-left: 12px;
          }
        }
      }
    }

    .card-status {
      :deep(.el-tag) {
        &.el-tag--success {
          background: #E7F7EA;
          color: #3DCD58;
          // border-color: #a7f3d0;
        }
        
        &.el-tag--warning {
          background-color: #fffbeb;
          color: #d97706;
          // border-color: #fde68a;
        }
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #EEEEEE;
    padding-top: 10px;
    .update-time {
      font-size: 14px;
      color: #909399;
    }

    .card-actions {
      display: flex;
      gap: 8px;
    }
  }
}
</style>
