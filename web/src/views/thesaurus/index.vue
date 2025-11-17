<template>
    <div class="setting-content">
        <div class="setting-header">
            <div style="font-size: 16px;font-weight: 600; margin-bottom: 10px;">词库设置</div>
            <p style="color: #5A5A5A;">支持用户管理自定义词汇，以优化搜索匹配，内容审核，输入推荐等场景，通过配置词库，可以实现搜索精度提升，内容安全控制的等需求</p>
        </div>
        
        <div class="dictionary-tabs">
            <el-tabs v-model="thesaurusParams.dictType" @tab-change="tabThesaurusType">
                <el-tab-pane v-for="item in tabOption" :key="item.value" :label="item.label" :name="item.value">
                    <template #label>
                        <div style="display: flex;">
                            <span>{{ item.label }}</span>
                            <el-tooltip :content="item.description" placement="top">
                                <el-icon style="color:#D8D8D8; font-size: 12px; margin-left: 2px;"><question-filled /></el-icon>
                            </el-tooltip>
                        </div>
                    </template>
                </el-tab-pane>
            </el-tabs>
            <div style="display: flex;align-items: center;">
                <el-input v-model="thesaurusParams.dictKey" prefix-icon="Search" clearable :placeholder="`请输入${titleKey}`" style="width: 150px;"></el-input>
                <span v-if="thesaurusParams.dictType === 'SYNONYM' || thesaurusParams.dictType === 'CORRECTION'"> - </span>
                <el-input v-if="thesaurusParams.dictType === 'SYNONYM' || thesaurusParams.dictType === 'CORRECTION'" clearable v-model="thesaurusParams.dictValue"  :placeholder="`请输入${titleValue}`" style="width: 150px;"></el-input>
                <el-date-picker
                    v-model="time"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    style="margin-left: 10px;"
                    value-format="YYYY-MM-DD"
                    @change="handleTimeChange"
                    clearable
                />
                <el-button  type="primary" style="margin-left: 10px;" @click="thesaurusSearch">搜索</el-button>
                <el-button type="primary" style="margin-left: 10px;" icon="Plus" @click="dialogVisible = true">添加</el-button>
                <el-upload
                    ref="uploadRef"
                    style="display: inline-block; margin-left: 10px;"
                    :http-request="customUploadRequest"
                    :before-upload="beforeImportUpload"
                    :on-remove="handleRemoveFile"
                    :file-list="fileList"
                    :limit="1"
                    accept=".xlsx,.xls"
                    :show-file-list="false"
                >
                    <el-button plain :loading="importLoading" icon="Upload">导入</el-button>
                </el-upload>
                <el-button style="margin-left: 10px;" icon="Download" :loading="exportLoading" @click="exportWordLibraryFn">
                    导出
                </el-button>
            </div>
        </div>
        <el-table :data="dictionaryData" style="width: 100%; margin-top: 10px;" border>
            <el-table-column prop="dictKey" :label="titleKey">
            </el-table-column>
            <el-table-column prop="dictValue" v-if="thesaurusParams.dictType === 'SYNONYM' || thesaurusParams.dictType === 'CORRECTION'" :label="titleValue">
            </el-table-column>
            <el-table-column prop="updatedAt" label="更新时间">
            </el-table-column>
            <el-table-column label="操作" width="150">
                <template #default="scope">
                    <el-tooltip content="编辑" placement="top">
                        <el-button link  icon="EditPen" size="small" @click="handleEdit(scope.row)"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link  icon="Delete" size="small" @click="handleDelete(scope.row)"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <pagination
            v-show="total > 0"
            v-model:page="thesaurusParams.pageNum"
            v-model:limit="thesaurusParams.pageSize"
            :total="total"
            @pagination="thesaurusSearch"
        />

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%" @close="cancel">
            <div>
                <div style="display: flex;align-items: center;">
                    <span style="width: 120px;">{{ titleKey }} :</span>
                    <el-input v-model="form.dictKey" :type="form.dictType === 'CORRECTION' ? 'textarea' : 'input'" :rows="3"  :placeholder="`请输入${titleKey}`" style="width: 100%;"></el-input>
                </div>
                <div v-if="form.dictType === 'SYNONYM' || thesaurusParams.dictType === 'CORRECTION'" style="display: flex;align-items: center; margin-top: 10px;">
                    <span style="width: 120px;">{{ titleValue }} :</span>
                    <el-input v-model="form.dictValue" :placeholder="`请输入${titleValue}`" style="width: 100%;"></el-input>
                </div>
            </div>
            <template #footer>
                <el-button type="primary" @click="submit">确定</el-button>
            </template>
        </el-dialog>


    </div>
</template>

<script setup>
import { 
    queryAllWordLibrary, 
    addWordLibrary, 
    updateWordLibrary, 
    deleteWordLibrary
} from '@/api/base'

import { importWordLibrary, exportWordLibrary } from '@/api/retrieve'
import { ElLoading, ElMessage } from 'element-plus'
const { proxy } = getCurrentInstance()
const time = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('专有名词')

// 导入相关变量
const importLoading = ref(false)
const exportLoading = ref(false)
const fileList = ref([])
const uploadRef = ref()

const tabOption = ref([
    {
        label: '专有名词',
        value: 'PROPER_NOUN',
        titleKey: '专有名词',
        titleValue: '',
        description: '特定领域，品牌，技术术语或行业术语的集合'
    },
    {
        label: '同义词',
        value: 'SYNONYM',
        titleKey: '特征词',
        titleValue: '同义词',
        description: '同一概念的不同表达形式，例如“汽车/轿车” “AI/人工智能”'
    },
    {
        label: '纠错词',
        value: 'CORRECTION',
        titleKey: '纠错词',
        titleValue: '特征词',
        description: '修正用户输入中常见拼写错误或错误表达的词汇，例如“log/登录” “teh/the”'
    },
    {
        label: '敏感词',
        value: 'SENSITIVE',
        titleKey: '敏感词',
        titleValue: '',
        description: '需过滤或屏蔽的违规词汇，例如政治敏感词，欺诈用语'
    },
    // {
    //     label: '搜索联想词',
    //     value: 'SUGGESTION',
    //     titleKey: '搜索联想词',
    //     titleValue: '',
    //     description: '基于用户输入习惯或高频搜索记录生成的推荐词，例如“旅游 - 旅游攻略”'
    // }
])

const thesaurusParams = ref({
    dictKey: '',
    dictValue: '',
    dictType: 'PROPER_NOUN',
    startTime: '',
    endTime: '',
    pageNum: 1,
    pageSize: 10,
    spaceId: 0
})

const titleKey = ref('专有名词')
const titleValue = ref('')

const form = ref({
    dictKey: '',
    dictValue: '',
    dictType: '',
    spaceId: 0
})

// 词典数据
const dictionaryData = ref([])
const total = ref(0)

const handleTimeChange = (value) => {
    thesaurusParams.value.startTime = value[0]
    thesaurusParams.value.endTime = value[1]
}

const thesaurusSearch = () => {
    queryAllWordLibrary(thesaurusParams.value).then(res => {
        dictionaryData.value = res.data
        total.value = res.total
    })
}

const tabThesaurusType = (val) => {
    thesaurusParams.value.pageNum = 1
    thesaurusParams.value.pageSize = 10
    const item = tabOption.value.find(item => item.value === val)
    dialogTitle.value = item.label
    titleKey.value = item.titleKey
    titleValue.value = item.titleValue
    form.value.dictType = item.value
    thesaurusSearch()
}

const exportWordLibraryFn = async () => {
    let parmas = {
        ...thesaurusParams.value
    }
    delete parmas.pageNum
    delete parmas.pageSize
    try {
        // 显示按钮加载状态
        exportLoading.value = true
        proxy?.download(
            'api/system/dictionary/export',
            {
            ...parmas
            },
            `词库_${new Date().getTime()}.xlsx`
        );
    } catch (error) {
        ElMessage.error(`导出失败：${error.message || '未知错误'}`)
    } finally {
        // 关闭按钮加载状态
        exportLoading.value = false
    }
}

const handleEdit = (row) => {
    dialogVisible.value = true
    form.value = row
}

const handleDelete = (row) => {
    deleteWordLibrary(row.id).then(res => {
        ElMessage.success('删除成功')
        thesaurusSearch()
    })
}

const submit = () => {
    if (form.value.id) {
        updateWordLibrary(form.value).then(res => {
            ElMessage.success('更新成功')
            cancel()
            thesaurusSearch()
        })
    } else {
        form.value.dictType = thesaurusParams.value.dictType
        addWordLibrary(form.value).then(res => {
            ElMessage.success('添加成功')
            cancel()
            thesaurusSearch()
        })
    }
}

const cancel = () => {
    dialogVisible.value = false
    form.value = {
        dictKey: '',
        dictValue: '',
        dictType: thesaurusParams.value.dictType,
        spaceId: 0
    }
}   
// 上传前检查
const beforeImportUpload = (file) => {
    console.log('beforeImportUpload 被触发', file)
    console.log('文件类型:', file.type)
    console.log('文件大小:', file.size)
    
    const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                    file.type === 'application/vnd.ms-excel'
    const isLt10M = file.size / 1024 / 1024 < 10

    console.log('是否为Excel文件:', isExcel)
    console.log('是否小于10MB:', isLt10M)

    if (!isExcel) {
        ElMessage.error('只能上传 Excel 文件!')
        return false
    }
    if (!isLt10M) {
        ElMessage.error('上传文件大小不能超过 10MB!')
        return false
    }
    
    console.log('文件验证通过')
    return true
}

// 自定义上传请求
const customUploadRequest = async (options) => {
    const { file, onSuccess, onError, onProgress } = options
    console.log('file 对象:', file)  
    try {
        // 显示加载状态
        importLoading.value = true
        
        // 创建 FormData
        const formData = new FormData()
        formData.append('file', file)
        const dictType = String(thesaurusParams.value.dictType)
        const response = await importWordLibrary(dictType, formData)
        console.log('导入接口响应:', response)
        
        if (response.code === 'ok') {
            ElMessage.success('词库数据导入成功')
            // 刷新词库列表
            thesaurusSearch()        
            fileList.value = []
        }

    } catch (error) {
        ElMessage.error(`导入失败：${error.message || '未知错误'}`)
        fileList.value = []
        // 调用错误回调
    } finally {
        importLoading.value = false
    }
}

// 移除文件
const handleRemoveFile = (file, fileList) => {
    console.log('handleRemoveFile 被触发', file, fileList)
    // 从文件列表中移除
    const index = fileList.value.findIndex(item => item.uid === file.uid)
    if (index !== -1) {
        fileList.value.splice(index, 1)
    }
}



onMounted(() => {
    thesaurusSearch()
})
</script>

<style scoped lang="scss">
.setting-content {
    height: 100%;
    background-color: #fff;
    padding: 20px;
    overflow-y: auto;
    
    .setting-header {
        background-image: url('@/assets/knowledge/ci_bg.png');
        background-size: cover;
        color: #333333;
        padding: 20px;
        border-radius: 8px;
        margin-bottom: 10px;
        
        h2 {
            margin: 0 0 10px 0;
            font-size: 24px;
            font-weight: 600;
        }
        
        p {
            margin: 0;
            font-size: 14px;
            opacity: 0.9;
        }
    }
    
    .dictionary-tabs {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}

:deep(.el-tabs__header) {
    margin: 0 !important;
}

:deep(.el-tabs__nav-wrap::after) {
    display: none;
}

:deep(.el-tabs__nav-wrap) {
    border-bottom: none;
}

:deep(.el-tabs__item) {
    height: 60px !important;
    font-size: 16px;
}


</style>
