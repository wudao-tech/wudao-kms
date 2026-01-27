<template>
    <div style="width: 100%; height: 100%; padding: 20px;">
        
        <div class="qa-pair-content">
            <div style="display: flex; align-items: center; justify-content: space-between;margin-bottom: 10px;">
                <div>
                    <el-input v-model="queryParams.question" placeholder="请输入问题" prefix-icon="Search"  style=" width: 200px; margin-right: 10px;" clearable @input="getQaPairList"></el-input>
                    <el-select v-model="queryParams.reviewStatus" placeholder="请选择状态" style="width: 150px; margin-right: 10px;" clearable @change="getQaPairList">
                        <el-option label="已采纳" value="pass" />
                        <el-option label="未采纳" value="reject" />
                        <el-option label="审核中" value="reviewing" />
                    </el-select>
                </div>
                <div>
                    <el-button  @click="handleBatchPass('pass')" icon="CircleCheck" v-if="permissionType === 1" :disabled="selectedRows.length === 0 ">采纳</el-button>
                    <el-button  @click="handleBatchPass('reject')" icon="CircleClose" v-if="permissionType === 1" :disabled="selectedRows.length === 0 ">拒绝</el-button>
                    <el-button  @click="handleBatchDelete" icon="Delete" :disabled="selectedRows.length === 0 || permissionType === 3">删除</el-button>
                    <el-button type="primary" @click="handleAdd" icon="Plus" :disabled="permissionType === 3">录入问答</el-button>
                </div>
            </div>
            <el-table v-loading="loading" :data="tableData" style="width: 100%;" border :max-height="maxHeight" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column type="index" width="55" label="序号" />
                <el-table-column prop="question" label="问题" show-overflow-tooltip width="300" />
                <el-table-column prop="answer" label="答案" >
                    <template #default="{ row }">
                        <span class="text-ellipsis">{{ row.answer }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="reviewStatus" label="状态" width="120" >
                    <template #default="{ row }">
                        <span v-if="row.reviewStatus === 'pass'">已采纳</span>
                        <span v-else-if="row.reviewStatus === 'reject'">未采纳</span>
                        <span v-else>审核中</span>
                    </template>
                </el-table-column>
                <el-table-column prop="generateType" label="生成方式" width="120" >
                    <template #default="{ row }">
                        <el-tooltip :content="row.generateType === 'feedback' ? '来自于QA优化的点赞，点踩中提交过来的' : row.generateType === 'extra' ? '在知识文件上传中，打开问答对提取功能时，由大模型生成的' : row.generateType === 'upload' || row.generateType === 'manual' ? '通过模板上传和创建得到的' : ''" placement="top">
                            <span v-if="row.generateType === 'feedback'">用户反馈</span>
                            <span v-else-if="row.generateType === 'extra'">智能提取</span>
                            <span v-else-if="row.generateType === 'upload' || row.generateType === 'manual'">手动创建</span>
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column prop="createByName" label="创建人" width="120" />
                <el-table-column prop="createTime" label="创建日期" width="180" />
                <el-table-column prop="" label="操作" width="140">
                    <template #default="{ row }">
                        <el-tooltip content="编辑" placement="top">
                            <el-button icon="EditPen" link :disabled="permissionType === 3" size="small" @click="handleEdit(row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="采纳" placement="top">
                            <el-button v-if="row.reviewStatus === 'reviewing' && permissionType === 1" icon="CircleCheck" link :disabled="permissionType === 3" size="small" @click="handleAccept(row, 'pass')"></el-button>
                        </el-tooltip>
                        <el-tooltip content="拒绝" placement="top">
                            <el-button v-if="row.reviewStatus === 'reviewing' && permissionType === 1" icon="CircleClose" link :disabled="permissionType === 3" size="small" @click="handleAccept(row, 'reject')"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="top">
                            <el-button icon="Delete" link :disabled="permissionType === 3" size="small" @click="handleDelete(row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <pagination
              v-show="total > 0"
              v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize"
              :total="total"
              @pagination="getQaPairList"
            />
        </div>
        <el-dialog v-model="dialogVisible" :title="form.id ? '编辑问答' : '录入问答'" width="600">
            <el-tabs v-if="!form.id" v-model="activeTab">
                <el-tab-pane label="输入问题" name="inputQuestion"></el-tab-pane>
                <el-tab-pane label="批量上传" name="batchUpload"></el-tab-pane>
            </el-tabs>
            <el-form v-if="activeTab === 'inputQuestion'" :model="form" label-width="100px" :rules="rules" ref="formRef" label-position="left">
                <el-form-item label="问题:" prop="question">
                    <el-input v-model="form.question" />
                </el-form-item>
                <el-form-item label="答案:" prop="answer">
                    <el-input v-model="form.answer" type="textarea" :rows="10" />
                </el-form-item>
            </el-form>
            <div v-if="activeTab === 'batchUpload'">
                <div style="display: flex; align-items: center;margin-bottom: 10px;">
                    <el-button type="primary" link style="height: 20px;" @click="downloadTemplate">
                    下载模板
                    </el-button>
                    <span style="margin-left: 10px; height: 20px; color: #898989;">(完成填写后再上传， 问题总数不超过500条)</span>
                </div>
                <el-upload
                    ref="uploadRef"
                    :auto-upload="false"
                    :on-change="handleFileChange"
                    :before-upload="beforeUpload"
                    :on-remove="removeFile"
                    accept=".xlsx,.xls"
                    drag
                    :limit="1"
                    :file-list="fileList"
                >
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                    将问答对文件拖到此处，或<em>点击上传</em>,只能上传xlsx/xls文件
                    </div>
                </el-upload>
            </div>

           <template #footer>
            <el-button @click="cancel(formRef)">取消</el-button>
            <el-button 
                type="primary" 
                @click="handleConfirm(formRef)"
                :disabled="activeTab === 'batchUpload' && !selectedFile"
                :loading="uploading"
            >
                确定
            </el-button>
           </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { getQaTable, getQaRecord, addToQa,  editQaImprove, deleteQaImprove, batchAddToQa, importQaImprove, acceptQaImprove } from '@/api/qa'
import { useRoute } from 'vue-router'
import templateFile from './answer.xlsx?url'
const route = useRoute()
const props = defineProps({
    spaceId: {
        type: Number,
        default: null
    },
    permissionType: {
        type: Number,
        default: 3
    }
})

const tableData = ref([])
const fileList = ref([])
const selectedFile = ref(null)
const activeTab = ref('batchUpload')
const maxHeight = ref(400)
const queryParams = ref({
    knowledgeSpaceId: props.spaceId,
    knowledgeId: Number(route.query.id),
    pageNum: 1,
    pageSize: 10,
    reviewStatus: '',
    question: ''
})
const loading = ref(false)
const total = ref(0)
const form = ref({
    question: '',
    answer: '',
    knowledgeSpaceId: props.spaceId,
    knowledgeId: Number(route.query.id)
})
const selectedRows = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const uploadRef = ref(null)
const uploading = ref(false)
const rules = ref({
    question: [
        { required: true, message: '请输入问题', trigger: 'blur' }
    ],
    answer: [
        { required: true, message: '请输入答案', trigger: 'blur' }
    ]
})

const getQaPairList = () => {
    loading.value = true
    getQaTable(queryParams.value).then(res => {
        tableData.value = res.data
        total.value = res.total
        loading.value = false
    }).finally(() => {
        loading.value = false
    })
}
const handleEdit = (row) => {
    dialogVisible.value = true
    activeTab.value = 'inputQuestion'
    form.value = {...row}
}
// 监听 spaceId 变化，重新初始化组件
watch(() => props.spaceId, (newVal) => {
    if (newVal) {
        // 清空之前的数据
        tableData.value = []
        form.value.knowledgeSpaceId = newVal
        queryParams.value.knowledgeSpaceId = newVal
        
        // 使用 nextTick 确保 DOM 已经渲染
        nextTick(() => {
            const element = document.querySelector('.qa-pair-content')
            if (element) {
                maxHeight.value = element.offsetHeight - 75
            }
        })
        
        getQaPairList()
    }
}, { immediate: true })

// 判断行是否可选（只有审核状态为 reviewing 的才可选）
const checkSelectable = (row) => {
    return row.reviewStatus === 'reviewing'
}

const handleSelectionChange = (selection) => {
    selectedRows.value = selection
}

const handleAdd = () => {
    dialogVisible.value = true
    activeTab.value = 'inputQuestion'
}

const handleBatchPass = (status) => {
    let ids = selectedRows.value.map(item => item.id)
    let params = {
        qaIds: ids.join(','),
        status: status
    }
    acceptQaImprove(params).then(res => {
        ElMessage.success(status === 'pass' ? '采纳成功' : '拒绝成功')
        selectedRows.value = []
        getQaPairList()
    })
}
const handleBatchDelete = () => {
    ElMessageBox.confirm('确定批量删除问答对吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        let ids = selectedRows.value.map(item => item.id)
        let params = {
            id: ids.join(',')
        }
        deleteQaImprove(params).then(res => {
            ElMessage.success('删除成功')
            getQaPairList()
        })
    })
}
const handleAccept = (row, status) => {
    let params = {
        qaIds: row.id,
        status: status
    }
    acceptQaImprove(params).then(res => {
        ElMessage.success(status === 'pass' ? '采纳成功' : '拒绝采纳')
        getQaPairList()
    })
}
// 下载问答对模板
const downloadTemplate = () => {
  try {
    // 使用 fetch 获取文件
    fetch(templateFile)
      .then(response => response.blob())
      .then(blob => {
        // 创建下载链接
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '问答对模板.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        // 释放 URL 对象
        window.URL.revokeObjectURL(url)
        ElMessage.success('模板下载成功')
      })
      .catch(error => {
        console.error('下载模板失败:', error)
        ElMessage.error('模板下载失败，请稍后重试')
      })
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('模板下载失败，请稍后重试')
  }
}


// 文件选择处理
const handleFileChange = (file, fileList) => {
    selectedFile.value = file.raw
}

// 移除文件
const removeFile = () => {
    selectedFile.value = null
    fileList.value = []
    if (uploadRef.value) {
        uploadRef.value.clearFiles()
    }
}

const handleSelectChange = () => {
    getQaPairList()
}

// 上传前验证
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                  file.type === 'application/vnd.ms-excel'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    ElMessage.error('只能上传Excel文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB!')
    return false
  }
  return true
}
const handleConfirm = async (formEl) => {
    if (activeTab.value === 'inputQuestion') {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                if (form.value.id) {
                    editQaImprove(form.value).then(res => {
                        ElMessage.success('编辑成功')
                        cancel(formEl)
                        getQaPairList()
                    })
                } else {
                    addToQa(form.value).then(res => {
                        ElMessage.success('录入成功')
                        cancel(formEl)
                        getQaPairList()
                    })
                }
            }
        })
    } else if (activeTab.value === 'batchUpload') {
        // 批量上传模式
        if (!selectedFile.value) {
            ElMessage.warning('请先选择文件')
            return
        }
        
        uploading.value = true
        let formData = new FormData()
        formData.append('file', selectedFile.value)
        formData.append('knowledgeId', Number(route.query.id))
        formData.append('knowledgeSpaceId', props.spaceId)
        
        importQaImprove(formData).then(res => {
            ElMessage.success('批量上传成功')
            cancel(formEl)
            getQaPairList()
        }).catch(error => {
            ElMessage.error('上传失败：' + (error.message || '未知错误'))
        }).finally(() => {
            uploading.value = false
        })
    }
}
const cancel = async (formEl) => {
    if (formEl) {
        formEl.resetFields()
    }
    form.value = {
        question: '',
        answer: '',
        knowledgeSpaceId: props.spaceId,
        knowledgeId: Number(route.query.id)
    }
    // 清空文件选择
    selectedFile.value = null
    fileList.value = []
    uploading.value = false
    if (uploadRef.value) {
        uploadRef.value.clearFiles()
    }
    dialogVisible.value = false
    activeTab.value = 'inputQuestion'
}
const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该问答对吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        let params = {
            id: row.id
        }
        deleteQaImprove(params).then(res => {
            ElMessage.success('删除成功')
            getQaPairList()
        })
    })
}

// 组件挂载后设置表格高度
onMounted(() => {
    nextTick(() => {
        const element = document.querySelector('.qa-pair-content')
        if (element) {
            maxHeight.value = element.offsetHeight - 75
        }
    })
})
</script>

<style lang="scss" scoped>
.qa-pair-content {
    background-color: #fff;
    height: 100%;
    width: 100%;
    padding: 20px;
}
.el-icon--upload {
    font-size: 40px;
    color: #c0c4cc;
    line-height: 40px;
}
:deep(.el-upload-dragger) {
    padding: 20px;
}
</style>