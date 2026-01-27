<template>
  <div class="api-key-container">
    <div class="api-key-header">
      <div class="header-content">
        <div style="flex: 1;">
            <div style="font-size: 16px;">API密钥管理</div>
            <div style="font-size: 14px; color: #979797; margin-top: 5px;">
              调用平台的开放API接口(如设备管理、数据查询等)时,必须携带有效的API密钥进行身份验证,以确保访问安全并被授权执行相应操作
            </div>
        </div>
        <el-button type="primary" @click="handleCreate">新建</el-button>
      </div>
    </div>

    <div class="api-key-table">
      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="remark" label="名称" width="200" />
        <el-table-column prop="secretKey" label="Api key" :show-overflow-tooltip="true">
          <template #default="scope">
            {{ scope.row.secretKey + '******' }}
          </template>
        </el-table-column>
        <el-table-column prop="expiryTime" label="过期时间" width="180" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="lastUsedAt" label="最后使用时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link icon="Edit" @click="handleEdit(scope.row)"></el-button>
            <el-button link icon="Delete" @click="handleDelete(scope.row)"></el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </div>

    <!-- 新建/编辑密钥弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑密钥' : '新建密钥'"
      width="500px"
      @close="handleCloseDialog"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="名称" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="过期时间" prop="expiryTime">
          <el-date-picker
            v-model="form.expiryTime"
            type="datetime"
            placeholder="请输入"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCloseDialog">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleConfirm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 显示新密钥弹窗 -->
    <el-dialog
      v-model="showKeyDialogVisible"
      width="600px"
      @close="handleCloseShowKeyDialog"
    >
      <span style="color: #979797; font-size: 14px;"><span style="color: #f56c6c; padding-right: 3px;">*</span>请保管好你的密钥,密钥不会再次展示~</span>
      <div style="background: #F8F8F8; padding: 20px; font-size: 16px; text-align: center;">
        {{ newApiKey }}
      </div>
      <template #footer>
        <el-button @click="handleCloseShowKeyDialog">取消</el-button>
        <el-button type="primary" @click="handleCopyKey">复制</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listApiKey, createApiKey, deleteApiKey,  updateApiKey } from '@/api/system/apiKey'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 60
})

// 新建/编辑密钥弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: '',
  remark: '',
  expiryTime: ''
})
const formRules = {
  remark: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ],
  expiryTime: [
    { required: true, message: '请选择过期时间', trigger: 'change' }
  ]
}

// 显示新密钥弹窗
const showKeyDialogVisible = ref(false)
const newApiKey = ref('')

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listApiKey(queryParams)
    tableData.value = res.data || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取API密钥列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 新建
const handleCreate = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.remark = row.remark || ''
  form.expiryTime = row.expiryTime || ''
  dialogVisible.value = true
}

// 确认（新建或编辑）
const handleConfirm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateApiKey(form)
          ElMessage.success('编辑成功')
          dialogVisible.value = false
          getList() // 刷新列表
        } else {
          // 新建模式
          const res = await createApiKey({
            remark: form.remark,
            expiryTime: form.expiryTime
          })
          newApiKey.value = res.data || ''
          dialogVisible.value = false
          showKeyDialogVisible.value = true
          getList() // 刷新列表
          ElMessage.success('创建成功')
        }
      } catch (error) {
        console.error(isEdit.value ? '编辑API密钥失败:' : '创建API密钥失败:', error)
        ElMessage.error(isEdit.value ? '编辑失败' : '创建失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 关闭弹窗
const handleCloseDialog = () => {
  dialogVisible.value = false
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.id = ''
  form.remark = ''
  form.expiryTime = ''
  isEdit.value = false
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该API密钥吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteApiKey(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除API密钥失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 复制密钥
const handleCopyKey = async () => {
  try {
    await navigator.clipboard.writeText(newApiKey.value)
    ElMessage.success('复制成功')
  } catch (error) {
    // 降级方案
    const textarea = document.createElement('textarea')
    textarea.value = newApiKey.value
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('复制成功')
  }
}

// 关闭显示密钥弹窗
const handleCloseShowKeyDialog = () => {
  showKeyDialogVisible.value = false
  newApiKey.value = ''
}


onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.api-key-container {
  height: 100%;
  background: #fff;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.api-key-header {
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
  background-image: url('@/assets/images/modelSquare_header.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  .header-content {
    display: flex;
    gap: 12px;
  }
}

.api-key-table {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.key-warning {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;

  span:first-child {
    color: #f56c6c;
    margin-right: 4px;
  }
}

:deep(.el-textarea__inner) {
  font-family: 'Courier New', monospace;
  font-size: 12px;
}
</style>
