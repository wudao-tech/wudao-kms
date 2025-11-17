<template>
  <div style="height: 100%; padding: 20px;">
    <div style="height: 100%; background-color: #fff; padding: 20px;">   
       <el-row style="display: flex; justify-content: space-between; align-items: center;">
        <el-button link icon="Back" @click="back">返回</el-button>
          <el-col :span="12" style="text-align: right;">
            <el-button link icon="Plus" @click="addPermission">新增</el-button>
          </el-col>
       </el-row>
       <el-tabs v-model="activeTab" @tab-change="handleTabChange">
           <el-tab-pane label="用户权限" name="user"> </el-tab-pane>
           <el-tab-pane label="权限申请" name="role"></el-tab-pane>
       </el-tabs>
       <el-table v-if="activeTab === 'user'" :data="tableData" style="width: 100%; margin-top: 10px;" border>
        <!-- <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="55" /> -->
        <el-table-column prop="userName" label="用户名称" />
        <el-table-column prop="permissionType" label="权限" >
          <template #default="scope">
            <span v-if="scope.row.permissionType === 1">管理</span>
            <span v-if="scope.row.permissionType === 2">读写</span>
            <span v-if="scope.row.permissionType === 3">只读</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column prop="action" label="操作" >
          <template #default="scope">
            <el-button link icon="Delete" v-if="scope.row.createdBy !== scope.row.userId" @click="deletePermission(scope.row)">删除</el-button>
            <el-button link icon="EditPen" v-if="scope.row.createdBy !== scope.row.userId" @click="editPermission(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
       </el-table>

       <el-table v-if="activeTab === 'role'" :data="roleData" style="width: 100%; margin-top: 10px;" border>
        <el-table-column prop="applicantUserName" label="用户名称" />
        <el-table-column prop="targetPermissionType" label="申请权限">
          <template #default="scope">
            <span v-if="scope.row.targetPermissionType === 1">管理</span>
            <span v-if="scope.row.targetPermissionType === 2">读写</span>
            <span v-if="scope.row.targetPermissionType === 3">只读</span>
          </template>
        </el-table-column>
        <el-table-column prop="applicationStatus" label="申请状态" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.applicationStatus === 0">待审批</span>
            <span v-if="scope.row.applicationStatus === 1">已通过</span>
            <span v-if="scope.row.applicationStatus === 2">已拒绝</span>
          </template>
        </el-table-column>
        <el-table-column prop="applicationReason" label="申请理由" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="申请时间" />
        <el-table-column prop="action" label="操作" width="120">
          <template #default="scope">
            <el-button link @click="passApplicationFn(scope.row.id)">通过</el-button>
            <el-button link @click="rejectApplicationFn(scope.row.id)">拒绝</el-button>
          </template>
        </el-table-column>
       </el-table>
    </div>

    <el-dialog v-model="permissionDialogVisible" :title="title" width="30%" @close="cancel(permissionFormRef)">
       <el-form ref="permissionFormRef" :model="permissionForm" :rules="rules1" label-width="80px">
        <el-form-item label="用户名称" prop="userId">
           <el-select v-model="permissionForm.userId" placeholder="请选择用户" @change="handleUserChange">
             <el-option v-for="item in userList" :key="item.id" :label="item.username" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限" prop="permissionType">
          <el-select v-model="permissionForm.permissionType" placeholder="请选择权限">
            <el-option label="只读" :value="3"> </el-option>
            <el-option label="读写" :value="2"> </el-option>
            <el-option label="管理" :value="1"> </el-option>
          </el-select>
        </el-form-item>
       </el-form>
       <template #footer>
        <el-button @click="cancel(permissionFormRef)">取消</el-button>
        <el-button type="primary" @click="submit(permissionFormRef)">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { 
  knowledgeBaseUserList, 
  editKnowledgeBaseUserPermission, 
  deleteKnowledgeBaseUserPermission, 
  addKnowledgeBaseUserPermission,
  knowledgeBasePermissionApplicationList,
  passApplication,
  rejectApplication
} from '@/api/base'

const props = defineProps({
  id: {
    type: Number,
    required: true
  },
  userList: {
    type: Array,
    default: () => []
  }
})
const proxy = getCurrentInstance()
const multipleTableRef = ref(null)
const tableData = ref([]) 
const emit = defineEmits(['back'])
const multipleSelection = ref([])
const title = ref('添加用户权限')
const permissionDialogVisible = ref(false)
const permissionFormRef = ref(null)
const permissionForm = ref({
  permissionType: null,
  knowledgeBaseId: props.id,
  userId: null,
  userName: ''
})
const activeTab = ref('user')
const rules1 = ref({
  permissionType: [{ required: true, message: '请选择权限', trigger: 'change' }],
  userId: [{ required: true, message: '请选择用户', trigger: 'blur' }]
})

const handleSelectionChange = (selection) => {
    multipleSelection.value = selection
}
const back = () => {
  emit('back')
}

const roleData = ref([{}])
const deletePermission = (row) => {
    ElMessageBox.confirm(
    `确定删除${row.userName}的权限吗？`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    deleteKnowledgeBaseUserPermission({
      userId: row.userId,
      knowledgeBaseId: props.id
    }).then(res => {
        ElMessage.success('删除成功')
        getKnowledgeBaseUserList(props.id)
    })
  })
}

const handleUserChange = (value) => {
  let user = props.userList.find(item => item.id === value)
  permissionForm.value.userName = user.username
}

const addPermission = () => {
  title.value = '添加用户权限'
  permissionDialogVisible.value = true
}
const editPermission = (row) => {
  title.value = '编辑用户权限'
  permissionDialogVisible.value = true
  permissionForm.value = {
    ...row
  }
}

const passApplicationFn = (id) => {
  passApplication(id).then(res => {
    ElMessage.success('通过申请')
    getKnowledgeBasePermissionApplicationList(props.id, {})
    getKnowledgeBaseUserList(props.id)
  })
}

const rejectApplicationFn = (id) => {
  rejectApplication(id).then(res => {
    ElMessage.success('拒绝申请')
    getKnowledgeBasePermissionApplicationList(props.id, {})
  })
}

const getKnowledgeBaseUserList = (id) => {
  knowledgeBaseUserList(id).then(res => {
    tableData.value = res.data
  })
}
const cancel = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
  permissionDialogVisible.value = false
  permissionForm.value = {
    permissionType: null,
    knowledgeBaseId: props.id,
    userId: null
  }
}

const submit = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      if (permissionForm.value.id) {
        editKnowledgeBaseUserPermission(permissionForm.value).then(res => {
          ElMessage.success('编辑成功')
          getKnowledgeBaseUserList(props.id)
          cancel(formEl)
        })
      } else {
        addKnowledgeBaseUserPermission(permissionForm.value).then(res => {
          ElMessage.success('添加成功')
          getKnowledgeBaseUserList(props.id)
          cancel(formEl)
        })
      }
    }
  })
}
const getKnowledgeBasePermissionApplicationList = (id) => {
  knowledgeBasePermissionApplicationList(id).then(res => {
    roleData.value = res.data
  })
}
watch(() => props.id, (newVal) => {
  console.log(newVal)
  getKnowledgeBaseUserList(newVal)
  // 审批列表
  getKnowledgeBasePermissionApplicationList(newVal)
}, {immediate: true})
</script>

<style scoped lang="scss">

</style>
