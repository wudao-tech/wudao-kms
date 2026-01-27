<template>
   <div style="width: 100%; height: 100%; padding: 20px;">
     <div class="model-opt-container" style="background-color: #fff; height: 100%; padding: 20px;">
        <div class="search-input-wrapper" style="margin-bottom: 10px;">
           <!-- <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
             <span style="font-weight: bold;">模型管理</span>
             
           </div> -->
           <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px;">
                <div style="display: flex; align-items: center;color: #6A6A6A;">
                    <span style="width: 100px;">全部类型：</span>
                    <div style="display: flex; align-items: center;">
                        <span style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.modelType === '' ? '#6b05a8' : '' }" @click="handleThemeClick('')">全部</span>
                        <span v-for="item in ai_model_type" style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.modelType === item.value ? '#6b05a8' : '' }" :key="item.value" @click="handleThemeClick(item.value)">{{ item.label }}</span>
                    </div>
                </div>
                <el-input v-model="queryParams.name" placeholder="搜索" style="width: 300px;" @input="getStoreList" />
           </div>
           <div style="display: flex; align-items: center; color: #6A6A6A;">
              <span style="width: 100px;">模型厂商：</span>   
              <div>
                <span style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.providerCode === '' ? '#6b05a8' : '' }" @click="handleProviderCode('')">全部</span>
                <span v-for="item in manufacturerList" style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.providerCode === item.providerCode ? '#6b05a8' : '' }" :key="item.providerCode" @click="handleProviderCode(item.providerCode)">{{ item.name }}</span>
              </div>
           </div>
        </div>
        <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px;">
            <span>模型列表</span>
            <el-button type="primary" @click="handleAddModel()">新增模型</el-button>
            <!-- <el-button type="primary" @click="handleAdd">新增厂商</el-button> -->
        </div>
        <el-table
            v-loading="loading" 
            :data="treeTableData" 
            border 
            style="flex: 1;"
            row-key="id"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
            default-expand-all
        >
            <el-table-column prop="name" label="名称" width="350">
                <template #default="{row}">
                    <!-- 厂商行 -->
                    <div v-if="row.isManufacturer" style="display: flex; align-items: center; gap: 30px; font-weight: 400;">
                        <span>{{ row.name }} ({{ row.providerCode }})</span>
                        <el-tag size="small">{{ row.modelCount }}个模型</el-tag>
                        <span v-if="row.status === '连接成功'"> <span style="width: 8px; height: 8px; background-color: #67C23A; border-radius: 50%; display: inline-block; margin-right: 4px;"></span>正常</span>
                        <span v-else><span style="width: 8px; height: 8px; background-color: #F56C6C; border-radius: 50%; display: inline-block; margin-right: 4px;"></span>异常</span>
                    </div>
                    <!-- 模型行 -->
                    <div v-else style="display: flex; align-items: center; gap: 8px;">
                        <img v-if="row.modelIcon" :src="row.modelIcon" alt="" style="width: 20px; height: 20px;">
                        <span>{{ row.name }}</span>
                    </div>
                </template>
            </el-table-column>
            <!-- <el-table-column prop="providerCode" label="模型Code" width="150">
            </el-table-column> -->
            <el-table-column prop="modelType" label="模型类型" width="170">
                <template #default="{row}">
                   <span v-if="!row.isManufacturer">{{ getModelType(row.modelType) }}</span>
                   <span v-else>-</span>
                </template>
            </el-table-column>
            <el-table-column prop="description" label="描述">
                <template #default="{row}">
                    <span v-if="!row.isManufacturer" class="description-cell">{{ row.description || '-' }}</span>
                    <span v-else>-</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
                <template #default="{row}">
                  <!-- 模型操作 -->
                  <template v-if="!row.isManufacturer">
                    <el-tooltip content="编辑" placement="top">
                      <el-button link icon="EditPen" @click.stop="handleAddModel(row)"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                      <el-button link icon="Delete" @click.stop="handleDelete(row)"></el-button>
                    </el-tooltip>
                  </template>
                  <!-- 厂商操作 -->
                  <template v-else>
                    <el-tooltip content="新增模型" placement="top">
                      <el-button link icon="Plus" size="small" @click.stop="handleAddModel(row)"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除厂商" placement="top">
                      <el-button link icon="Delete" size="small" :disabled="row.modelCount > 0 || (row.children && row.children.length > 0)" @click.stop="handleDeleteManufacturer(row)"></el-button>
                    </el-tooltip>
                    <el-tooltip content="配置" placement="top">
                      <el-button link icon="Setting" size="small" @click.stop="handleConfig(row)"></el-button>
                    </el-tooltip>
                    
                  </template>
                </template>
            </el-table-column>
        </el-table>
        <!-- 模型弹窗 -->
        <el-dialog v-model="addModelVisible" :title="addModelForm.id ? '编辑模型' : '新增模型'" @close="handleCancel(addModelFormRef)" width="700px">
            <el-form :model="addModelForm" ref="addModelFormRef" :rules="rules" label-width="100px" style="display: flex; flex-wrap: wrap;">
                <el-form-item label="模型厂商" prop="providerCode" style="width: 50%;">
                    <el-select v-model="addModelForm.providerCode" :disabled="addModelForm.id" @change="handleProviderCodeChange">
                        <el-option v-for="item in manufacturerList" :key="item.providerCode" :label="item.name" :value="item.providerCode" />
                        <el-option label="自定义" value="custom" />
                    </el-select>
                </el-form-item>

                <!-- 自定义厂商配置 -->
                <template v-if="addModelForm.providerCode === 'custom'">
                    <el-form-item label="接入协议" prop="manufacturerProviderCode"  style="width: 50%;">
                        <el-select v-model="addModelForm.manufacturerProviderCode" placeholder="请选择接入协议" @change="handleCustomManufacturerProviderCode">
                            <el-option v-for="item in filteredAiLlmModel" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="厂商名称" prop="manufacturerName"  style="width: 50%;" >
                        <el-input v-model="addModelForm.manufacturerName" placeholder="请输入厂商名称" />
                    </el-form-item>
                    <el-form-item 
                        v-if="customManufacturerType !== 'cloud' || addModelForm.manufacturerProviderCode === 'ollama'" 
                        :label="addModelForm.manufacturerProviderCode === 'ollama' ? 'Base URL' : 'Endpoint'" 
                        prop="endpoint"
                         style="width: 50%;"
                    >
                        <el-input v-model="addModelForm.endpoint" placeholder="请输入Endpoint" />
                    </el-form-item>
                    <el-form-item 
                        v-if="customManufacturerType !== 'cloud' && addModelForm.manufacturerProviderCode !== 'ollama'" 
                        label="Path" 
                        prop="path"
                        style="width: 50%;"
                    >
                        <el-input v-model="addModelForm.path" placeholder="请输入Completions-path" />
                    </el-form-item>
                    <el-form-item 
                        v-if="addModelForm.manufacturerProviderCode !== 'ollama'" 
                        label="API Key" 
                        prop="apiKey"
                        style="width: 50%;"
                        >
                        <el-input v-model="addModelForm.apiKey" placeholder="请输入API Key" />
                    </el-form-item>
                </template>

                <el-form-item label="模型名称" prop="name" style="width: 50%;">
                    <el-input v-model="addModelForm.name" />
                </el-form-item>
                <el-form-item label="模型Code" prop="model" style="width: 50%;">
                    <el-input v-model="addModelForm.model" />
                </el-form-item>
                <el-form-item label="模型类型" prop="modelType" style="width: 50%;">
                    <el-select v-model="addModelForm.modelType">
                        <el-option v-for="item in ai_model_type" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="最大Token" prop="maxTokens" style="width: 50%;">
                    <el-input-number v-model="addModelForm.maxTokens" style="width: 100%;" :min="1" :max="100000" :step="100" />
                </el-form-item>
                <el-form-item label="模型能力" style="width: 100%;">
                    <el-checkbox v-model="addModelForm.deepThinkingModel" label="深度思考" size="large" />
                    <el-checkbox v-model="addModelForm.multimodal" label="多模态" size="large" />
                    <el-checkbox v-model="addModelForm.webSearch" label="联网搜索" size="large" />
                </el-form-item>
                
                <el-form-item label="描述" prop="description" style="width: 100%;">
                    <el-input type="textarea" v-model="addModelForm.description" :autosize="{ minRows: 4, maxRows: 10 }"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
               
                <el-button @click="handleCancel(addModelFormRef)">取消</el-button>
                <el-button type="primary" @click="handleSubmit(addModelFormRef)">确定</el-button>
            </template>
        </el-dialog>

        <!-- 厂商弹窗 -->
        <el-dialog v-model="manufacturerDialogVisible" :title="manufacturerForm.id ? '厂商配置' : '厂商新增'" width="500px" @close="cancelManufacturerForm(manufacturerFormRef)">
            <el-form ref="manufacturerFormRef" :model="manufacturerForm" label-width="100px" label-position="left" :rules="manufacturerRules">
                <!-- 只有非预设厂商（非ollama、openai等）才显示接入协议 -->
                <el-form-item v-if="isPresetManufacturer(manufacturerForm.providerCode)" label="接入协议" prop="providerCode">
                    <el-select v-model="manufacturerForm.providerCode" :disabled="!!manufacturerForm.id" placeholder="请选择支持厂商" @change="handleManufacturerProviderCode">
                        <el-option v-for="item in filteredAiLlmModel" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="厂商名称" prop="name">
                    <el-input v-model="manufacturerForm.name" :disabled="!isPresetManufacturer(manufacturerForm.providerCode)" />
                </el-form-item>
                <el-form-item v-if="manufacturerForm.type !== 'cloud' || manufacturerForm.providerCode === 'ollama'" :label="manufacturerForm.providerCode === 'ollama' ? 'Base URL' : 'Endpoint'" prop="endpoint">
                    <el-input v-model="manufacturerForm.endpoint" />
                </el-form-item>
                <el-form-item v-if="manufacturerForm.type !== 'cloud' && manufacturerForm.providerCode !== 'ollama'" label="Path">
                    <el-input v-model="manufacturerForm.completionsPath" />
                </el-form-item>
                <el-form-item v-if="manufacturerForm.providerCode !== 'ollama'" label="API Key" prop="apiKey">
                    <el-input v-model="manufacturerForm.apiKey" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="testManufacturerFn(manufacturerFormRef)">测试</el-button>
                <el-button @click="cancelManufacturerForm(manufacturerFormRef)">取消</el-button>
                <el-button type="primary" @click="submitManufacturer(manufacturerFormRef)">确定</el-button>
            </template>
        </el-dialog>
     </div>
   </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import {  getModel } from '@/api/retrieve'
import { getManufacturerList, addModel, editModel, deleteModel, addManufacturer, editManufacturer, deleteManufacturer, testManufacturer } from '@/api/manufacturer'
const { proxy } = getCurrentInstance();
const { ai_model_type, ai_llm_model } = toRefs(proxy?.useDict('ai_model_type', 'ai_llm_model'));
import { useRouter } from 'vue-router'
const router = useRouter()
const loading = ref(false)
const queryParams = ref({
    pageSize: 30,
    pageNum: 1,
    modelType: '',
    providerCode: '',
    name: ''
})

const sequence = ['qwen', 'deepseek','moonshot', 'zhipu']

// 按照 sequence 顺序对厂商列表进行排序
const sortManufacturerList = (list) => {
    if (!Array.isArray(list)) return list
    return [...list].sort((a, b) => {
        const indexA = sequence.indexOf(a.providerCode)
        const indexB = sequence.indexOf(b.providerCode)
        
        // 如果都在 sequence 中，按照 sequence 的顺序排序
        if (indexA !== -1 && indexB !== -1) {
            return indexA - indexB
        }
        // 如果只有 a 在 sequence 中，a 排在前面
        if (indexA !== -1) {
            return -1
        }
        // 如果只有 b 在 sequence 中，b 排在前面
        if (indexB !== -1) {
            return 1
        }
        // 如果都不在 sequence 中，保持原有顺序
        return 0
    })
}

// 厂商
const manufacturerList = ref([])
const manuImg = [
    {
        name: 'deepseek',
        icon: 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/deepseek.png',
    },
    {
        name: 'qwen',
        icon: 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png',
    },
    {
        name: 'zhipu',
        icon: 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/zhipu.png',
    },
    {
        name: 'moonshot',
        icon: 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/moonshot.png',
    },
    {
        name: 'openai',
        icon: 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/openai.jpg',
    },
    {
        name: 'ollama',
        icon: 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/ollama.jpg',
    }
]
const addModelFormRef = ref(null)
const addModelForm = ref({
    name: '',
    modelType: '',
    deepThinkingModel: false,
    multimodal: false,
    webSearch: false,
    model: '',
    providerCode: '',
    maxTokens: 0,
    description: '',
    modelIcon: '',
    // 自定义厂商相关字段
    manufacturerProviderCode: '', // 接入协议（支持厂商）
    manufacturerName: '', // 厂商名称
    endpoint: '', // Endpoint
    path: '', // Path（Completions-path）
    apiKey: '' // API Key
})
// 自定义厂商类型
const customManufacturerType = ref('')

// 动态验证规则
const rules = computed(() => {
    const baseRules = {
        name: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
        modelType: [{ required: true, message: '请选择模型类型', trigger: 'blur' }],
        maxTokens: [{ required: true, message: '请输入最大token长度', trigger: 'blur' }],
        description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
        model: [{ required: true, message: '请输入模型code', trigger: 'blur' }],
        providerCode: [{ required: true, message: '请选择模型厂商', trigger: 'blur' }]
    }
    
    // 如果是自定义厂商，添加厂商相关验证
    if (addModelForm.value.providerCode === 'custom') {
        baseRules.manufacturerProviderCode = [{ required: true, message: '请选择接入协议', trigger: 'change' }]
        baseRules.manufacturerName = [{ required: true, message: '请输入厂商名称', trigger: 'blur' }]
        
        // 根据类型添加 endpoint 验证
        if (customManufacturerType.value !== 'cloud' || addModelForm.value.manufacturerProviderCode === 'ollama') {
            baseRules.endpoint = [{ required: true, message: '请输入Endpoint', trigger: 'blur' }]
        }
        
        // 如果不是 cloud 且不是 ollama，需要 path
        if (customManufacturerType.value !== 'cloud' && addModelForm.value.manufacturerProviderCode !== 'ollama') {
            baseRules.path = [{ required: true, message: '请输入Completions-path', trigger: 'blur' }]
        }
        
        // 如果不是 ollama，需要 API Key
        if (addModelForm.value.manufacturerProviderCode !== 'ollama') {
            baseRules.apiKey = [{ required: true, message: '请输入API Key', trigger: 'blur' }]
        }
    }
    
    return baseRules
})
const addModelVisible = ref(false)
const maxHeight = ref(500)

// 厂商表单相关
const manufacturerDialogVisible = ref(false)
const manufacturerFormRef = ref(null)
const manufacturerForm = ref({
    providerCode: '',
    type: '',
    name: '',
    endpoint: '',
    completionsPath: '',
    apiKey: ''
})
// 动态验证规则（根据是否是预设厂商调整）
const manufacturerRules = computed(() => {
    const baseRules = {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
    }
    
    // 只有非预设厂商才需要验证接入协议
    if (!isPresetManufacturer(manufacturerForm.value.providerCode)) {
        baseRules.providerCode = [{ required: true, message: '请选择支持厂商', trigger: 'change' }]
    }
    
    // 根据类型添加其他验证
    if (manufacturerForm.value.type !== 'cloud' || manufacturerForm.value.providerCode === 'ollama') {
        baseRules.endpoint = [{ required: true, message: '请输入', trigger: 'blur' }]
    }
    
    if (manufacturerForm.value.type !== 'cloud' && manufacturerForm.value.providerCode !== 'ollama') {
        baseRules.completionsPath = [{ required: true, message: '请输入Completions-path', trigger: 'blur' }]
    }
    
    if (manufacturerForm.value.providerCode !== 'ollama') {
        baseRules.apiKey = [{ required: true, message: '请输入API key', trigger: 'blur' }]
    }
    
    return baseRules
})

// 过滤已存在的厂商
// openai 和 ollama 一直显示，不过滤；其他云厂商需要过滤已存在的
const filteredAiLlmModel = computed(() => {
    if (!ai_llm_model.value || !Array.isArray(ai_llm_model.value)) {
        return []
    }
    
    const existingProviderCodes = manufacturerList.value
        .map(item => item.providerCode)
        .filter(code => code)
    
    // 预设厂商列表（一直显示，不过滤）
    const presetManufacturers = ['ollama', 'openai']
    
    if (manufacturerForm.value.id) {
        // 编辑模式：当前编辑的厂商要显示，其他已存在的非预设厂商要过滤
        const currentProviderCode = manufacturerForm.value.providerCode
        const filteredCodes = existingProviderCodes.filter(code => code !== currentProviderCode)
        
        return ai_llm_model.value.filter(item => {
            // 预设厂商一直显示
            if (presetManufacturers.includes(item.value)) {
                return true
            }
            // 其他厂商过滤已存在的
            return !filteredCodes.includes(item.value)
        })
    } else {
        // 新增模式：预设厂商一直显示，其他已存在的厂商要过滤
        return ai_llm_model.value.filter(item => {
            // 预设厂商一直显示
            if (presetManufacturers.includes(item.value)) {
                return true
            }
            // 其他厂商过滤已存在的
            return !existingProviderCodes.includes(item.value)
        })
    }
})

// 标记是否从厂商节点新增模型
const isAddingFromManufacturer = ref(false)

const handleThemeClick = (value) => {
    queryParams.value.modelType = value
    queryParams.value.pageNum = 1
    getStoreList()
}

const handleDelete = (row) => {
   ElMessageBox.confirm('确定删除该模型吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
   }).then(() => {
    deleteModel({
        id: row.originalId || row.id
    }).then(res => {
        ElMessage.success('删除成功')
        getStoreList()
    }).catch(() => {
        ElMessage.error('删除失败')
    })
   })
}

const handleSubmit = async (formRef) => {
    if (!formRef) return
    await formRef.validate(async (valid) => {
        if (valid) {
            if (addModelForm.value.id) {
                // 编辑模型
                await editModel(addModelForm.value).then(async res => {
                    ElMessage.success('编辑成功')
                    // 重新获取厂商列表
                    const manufacturerRes = await getManufacturerList()
                    manufacturerList.value = sortManufacturerList(manufacturerRes.data)
                    getStoreList()
                    handleCancel(formRef)
                })
            } else {
                // 新增模型
                let modelData = { ...addModelForm.value }
                
                // 如果是自定义厂商，先创建厂商
                if (addModelForm.value.providerCode === 'custom') {
                    try {
                        // 构建厂商数据
                        const manufacturerData = {
                            providerCode: addModelForm.value.manufacturerProviderCode,
                            name: addModelForm.value.manufacturerName,
                            type: customManufacturerType.value,
                            endpoint: addModelForm.value.endpoint,
                            completionsPath: addModelForm.value.path || '',
                            apiKey: addModelForm.value.apiKey || ''
                        }
                        
                        // 创建厂商
                        const manufacturerRes = await addManufacturer(manufacturerData)
                        
                        if (manufacturerRes.code === 'ok') {
                            // 获取新创建的厂商的 providerCode（使用厂商名称作为标识）
                            const newManufacturerList = await getManufacturerList()
                            const newManufacturer = newManufacturerList.data.find(m => m.name === addModelForm.value.manufacturerName)
                            
                            if (newManufacturer) {
                                // 将模型的 providerCode 设置为新创建的厂商的 providerCode
                                modelData.providerCode = newManufacturer.providerCode
                                modelData.modelIcon = manuImg.find(item => item.name === newManufacturer.providerCode)?.icon || ''
                            }
                            
                            // 清理自定义厂商相关字段
                            delete modelData.manufacturerProviderCode
                            delete modelData.manufacturerName
                            delete modelData.endpoint
                            delete modelData.path
                            delete modelData.apiKey
                            
                            // 创建模型
                            await addModel(modelData).then(async res => {
                                ElMessage.success('新增成功')
                                // 重新获取厂商列表
                                const manufacturerRes = await getManufacturerList()
                                manufacturerList.value = sortManufacturerList(manufacturerRes.data)
                                getStoreList()
                                handleCancel(formRef)
                            })
                        } else {
                            ElMessage.error('创建厂商失败')
                        }
                    } catch (error) {
                        console.error('创建厂商失败:', error)
                        ElMessage.error('创建厂商失败，请重试')
                    }
                } else {
                    // 非自定义厂商，直接创建模型
                    await addModel(modelData).then(async res => {
                        ElMessage.success('新增成功')
                        // 重新获取厂商列表
                        const manufacturerRes = await getManufacturerList()
                        manufacturerList.value = sortManufacturerList(manufacturerRes.data)
                        getStoreList()
                        handleCancel(formRef)
                    })
                }
            }
        }
    })
}

const handleProviderCode = (value) => {
    queryParams.value.providerCode = value
    queryParams.value.pageNum = 1
    getStoreList()
}

const getModelType = (value) => {
    return ai_model_type.value.find(item => item.value === value)?.label
}

const getManufacturerName = (value) => {
    return manufacturerList.value.find(item => item.providerCode === value)?.name
}

const cardList = ref([])
const treeTableData = ref([])

// 构建树形表格数据
const buildTreeTableData = () => {
    const result = []
    
    // 如果筛选了特定厂商，只显示该厂商及其模型
    if (queryParams.value.providerCode) {
        const manufacturer = manufacturerList.value.find(m => m.providerCode === queryParams.value.providerCode)
        if (manufacturer) {
            const models = cardList.value.filter(model => model.providerCode === manufacturer.providerCode)
            
            const manufacturerNode = {
                ...manufacturer,
                id: `manufacturer-${manufacturer.providerCode}`,
                modelCount: models.length,
                isManufacturer: true,
                originalId: manufacturer.id,
                children: models.map(model => ({
                    ...model,
                    id: `model-${model.id}`,
                    originalId: model.id,
                    isManufacturer: false
                }))
            }
            result.push(manufacturerNode)
        }
    } else {
        // 没有筛选厂商时，显示所有厂商
        manufacturerList.value.forEach(manufacturer => {
            // 查找该厂商下的所有模型
            const models = cardList.value.filter(model => model.providerCode === manufacturer.providerCode)
            
            // 创建厂商节点（包含所有厂商信息）
            const manufacturerNode = {
                ...manufacturer,
                id: `manufacturer-${manufacturer.providerCode}`,
                modelCount: models.length,
                isManufacturer: true,
                originalId: manufacturer.id,
                children: models.map(model => ({
                    ...model,
                    id: `model-${model.id}`,
                    originalId: model.id,
                    isManufacturer: false
                }))
            }
            result.push(manufacturerNode)
        })
        
        // 添加没有厂商的模型（作为顶级节点）
        const modelsWithoutManufacturer = cardList.value.filter(model => 
            !model.providerCode || !manufacturerList.value.some(m => m.providerCode === model.providerCode)
        )
        
        modelsWithoutManufacturer.forEach(model => {
            result.push({
                ...model,
                id: `model-${model.id}`,
                originalId: model.id,
                isManufacturer: false
            })
        })
    }
    
    treeTableData.value = result
}

const getStoreList = () => {
    loading.value = true
    getModel(queryParams.value).then(res => {
        loading.value = false
        cardList.value = res.data
        buildTreeTableData()
    })
}

const handleAddModel = (row) => {
    addModelVisible.value = true
    
    if (row && row.isManufacturer) {
        // 从厂商节点新增模型，自动填充 providerCode
        isAddingFromManufacturer.value = true
        addModelForm.value = {
            name: '',
            modelType: '',
            deepThinkingModel: false,
            multimodal: false,
            webSearch: false,
            model: '',
            providerCode: row.providerCode,
            maxTokens: 0,
            description: '',
            modelIcon: manuImg.find(item => item.name === row.providerCode)?.icon || '',
            // 自定义厂商相关字段
            manufacturerProviderCode: '',
            manufacturerName: '',
            endpoint: '',
            path: '',
            apiKey: ''
        }
    } else if (row && !row.isManufacturer) {
        // 编辑模型
        isAddingFromManufacturer.value = false
        const modelData = { ...row }
        modelData.id = row.originalId || row.id
        delete modelData.isManufacturer
        delete modelData.originalId
        // 确保包含自定义厂商字段
        addModelForm.value = {
            ...modelData,
            manufacturerProviderCode: '',
            manufacturerName: '',
            endpoint: '',
            path: '',
            apiKey: ''
        }
    } else {
        // 普通新增模型
        isAddingFromManufacturer.value = false
        addModelForm.value = {
            name: '',
            modelType: '',
            deepThinkingModel: false,
            multimodal: false,
            webSearch: false,
            model: '',
            providerCode: '',
            maxTokens: 0,
            description: '',
            modelIcon: '',
            // 自定义厂商相关字段
            manufacturerProviderCode: '',
            manufacturerName: '',
            endpoint: '',
            path: '',
            apiKey: ''
        }
    }
    customManufacturerType.value = ''
}

const handleProviderCodeChange = (value) => {
    if (addModelForm.value.id) {
        return 
    } else {
        addModelForm.value.modelIcon = manuImg.find(item => item.name === value)?.icon
    }
}

// 配置/编辑厂商
const handleConfig = (row) => {
    if (row.isManufacturer) {
        // 查找完整的厂商数据
        const manufacturer = manufacturerList.value.find(m => m.providerCode === row.providerCode)
        if (manufacturer) {
            manufacturerForm.value = { ...manufacturer }
            manufacturerDialogVisible.value = true
        }
    }
}

// 新增厂商
const handleAdd = () => {
    manufacturerDialogVisible.value = true
}

// 判断是否是预设厂商（ollama, openai等）
const isPresetManufacturer = (providerCode) => {
    // 预设厂商列表：ollama, openai 等
    const presetManufacturers = ['ollama', 'openai']
    return presetManufacturers.includes(providerCode)
}

// 厂商 providerCode 变化（厂商弹窗用）
const handleManufacturerProviderCode = (value) => {
    if (value === 'ollama' || value === 'openai') {
        manufacturerForm.value.type = 'other'
    } else {
        manufacturerForm.value.type = 'cloud'
    }
}

// 自定义厂商 providerCode 变化（模型弹窗用）
const handleCustomManufacturerProviderCode = (value) => {
    if (value === 'ollama' || value === 'openai') {
        customManufacturerType.value = 'other'
    } else {
        customManufacturerType.value = 'cloud'
    }
}

// 测试厂商连接（厂商弹窗用）
const testManufacturerFn = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            try {
                let res = await testManufacturer(manufacturerForm.value)
                if (res.code === 'ok') {
                    ElMessage.success('测试成功')
                } else {
                    ElMessage.error('测试失败')
                }
            } catch (error) {
                ElMessage.error('测试失败')
            }
        }
    })
}

// 测试自定义厂商连接（模型弹窗用）
const testCustomManufacturer = async () => {
    if (!addModelFormRef.value) return
    
    // 验证自定义厂商必填字段
    const customRules = {
        manufacturerProviderCode: [{ required: true, message: '请选择接入协议', trigger: 'change' }],
        manufacturerName: [{ required: true, message: '请输入厂商名称', trigger: 'blur' }]
    }
    
    if (customManufacturerType.value !== 'cloud' || addModelForm.value.manufacturerProviderCode === 'ollama') {
        customRules.endpoint = [{ required: true, message: '请输入Endpoint', trigger: 'blur' }]
    }
    
    if (customManufacturerType.value !== 'cloud' && addModelForm.value.manufacturerProviderCode !== 'ollama') {
        customRules.path = [{ required: true, message: '请输入Completions-path', trigger: 'blur' }]
    }
    
    if (addModelForm.value.manufacturerProviderCode !== 'ollama') {
        customRules.apiKey = [{ required: true, message: '请输入API Key', trigger: 'blur' }]
    }
    
    // 临时验证
    const validateFields = ['manufacturerProviderCode', 'manufacturerName']
    if (customRules.endpoint) validateFields.push('endpoint')
    if (customRules.path) validateFields.push('path')
    if (customRules.apiKey) validateFields.push('apiKey')
    
    try {
        await addModelFormRef.value.validateField(validateFields)
        
        // 构建测试数据
        const testData = {
            providerCode: addModelForm.value.manufacturerProviderCode,
            name: addModelForm.value.manufacturerName,
            type: customManufacturerType.value,
            endpoint: addModelForm.value.endpoint,
            completionsPath: addModelForm.value.path || '',
            apiKey: addModelForm.value.apiKey || ''
        }
        
        const res = await testManufacturer(testData)
        if (res.code === 'ok') {
            ElMessage.success('测试成功')
        } else {
            ElMessage.error('测试失败')
        }
    } catch (error) {
        if (error && error.fields) {
            // 验证失败，不显示错误
            return
        }
        ElMessage.error('测试失败')
    }
}

// 提交厂商表单
const submitManufacturer = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            try {
                if (manufacturerForm.value.id) {
                    await editManufacturer(manufacturerForm.value)
                    ElMessage.success('编辑成功')
                } else {
                    await addManufacturer(manufacturerForm.value)
                    ElMessage.success('新增成功')
                }
                cancelManufacturerForm(formEl)
                // 重新获取厂商列表和模型列表
                const res = await getManufacturerList()
                manufacturerList.value = sortManufacturerList(res.data)
                getStoreList()
            } catch (error) {
                ElMessage.error(manufacturerForm.value.id ? '编辑失败' : '新增失败')
            }
        }
    })
}

// 取消厂商表单
const cancelManufacturerForm = (formEl) => {
    if (!formEl) return
    formEl.resetFields()
    manufacturerForm.value = {
        providerCode: '',
        type: '',
        name: '',
        endpoint: '',
        completionsPath: '',
        apiKey: ''
    }
    manufacturerDialogVisible.value = false
}

// 删除厂商
const handleDeleteManufacturer = (row) => {
    ElMessageBox.confirm('确定删除该厂商吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        try {
            await deleteManufacturer({ id: row.originalId || row.id })
            ElMessage.success('删除成功')
            // 重新获取厂商列表和模型列表
            const res = await getManufacturerList()
            manufacturerList.value = sortManufacturerList(res.data)
            getStoreList()
        } catch (error) {
            ElMessage.error('删除失败')
        }
    })
}

const handleCancel = (formRef) => {
    if (!formRef) return
    formRef.resetFields()
    addModelForm.value = {
        name: '',
        modelType: '',
        deepThinkingModel: false,
        multimodal: false,
        webSearch: false,
        model: '',
        providerCode: '',
        maxTokens: 0,
        description: '',
        modelIcon: '',
        // 自定义厂商相关字段
        manufacturerProviderCode: '',
        manufacturerName: '',
        endpoint: '',
        path: '',
        apiKey: ''
    }
    customManufacturerType.value = ''
    isAddingFromManufacturer.value = false
    addModelVisible.value = false
}
onMounted(() => {
    ai_model_type.value.forEach(item => {
        item.checked = false
    })
    console.log(document.querySelector('.model-opt-container').offsetHeight)
    maxHeight.value = document.querySelector('.model-opt-container').offsetHeight - 200
    
    // 先获取厂商列表，再获取模型列表，最后构建树形数据
    getManufacturerList().then(res => {
        manufacturerList.value = sortManufacturerList(res.data)
        console.log(123, manufacturerList.value)
        getStoreList()
    })
})

</script>

<style scoped lang="scss">
:root {
    color-scheme: light;
}
.search-input-wrapper {
    background-image: url('@/assets/images/modelSquare_header.png');
    background-size: cover;
    padding: 10px 20px;
    width: 100%;
}
.model-opt-container {
    display: flex;
    flex-direction: column;
}
.ai-store {
    position: relative;
    .retrieve-bg {
        position: absolute;
        left: 130px;
        top: 0;
    }
    .retrieve-bg-2 {
        position: absolute;
        right: 0;
        top: 0;
        height: 35%;
    }
    align-items: center;
    // overflow-y: auto;
    background: #F5F7FA !important;
    height: 100%;
    width: 100%;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    .gradient-text {
        font-size: 34px;
        padding-top: 30px;
        font-weight: bold;
        text-align: center;
        background: linear-gradient(270.0000000000106deg, #5C078F 0%, #AF50C5 100%);
        -webkit-background-clip: text;
        color: transparent;
        z-index: 99;
    }
    .category-section {
        margin-bottom: 20px;
        z-index: 99;
        .category-row {
            display: flex;
            align-items: center;
            
        }
        .category-row1 {
            display: flex;
            align-items: center;
            
        }
        .label {
                margin-right: 16px;
                min-width: 40px;
            }
            .tags {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;

                .tag {
                    padding: 6px 16px;
                    border-radius: 4px;
                    cursor: pointer;
                    transition: all 0.3s;
                    &:hover {
                        color: #722ED1;
                    }
                }
            }
        .filter-section {
            padding-top: 16px;
            margin-bottom: 16px;
            display: flex;
            .filter-group {
                display: flex;
                margin-bottom: 12px;

                .filter-item {
                    cursor: pointer;
                    margin-right: 30px;
                    &.active {
                        color: #722ED1;
                    }
                    &:hover {
                        color: #722ED1;
                    }
                }
            }
        }
    }

    .card-list {
        display: flex;
        gap: 20px;
        flex-wrap: wrap;
    }

    .sticky-wrapper {
        width: 80%;
        z-index: 100;
        background: transparent;
        margin-top: 10px;
        box-sizing: border-box;

        &.is-fixed {
            position: sticky;
            top: 0;
            background: #F5F7FA;
            padding: 10px 0;
            width: 100%; // 与父容器padding: 0 10%对应
            margin-left: auto;
            margin-right: auto;
            .category-row {
                            width: 80% !important;
                            margin-left: auto;
                            margin-right: auto;
                        }
            .category-row1 {
                width: 80% !important;
                margin-left: auto;
                margin-right: auto;
            }
            .ceiling {
                width: 80% !important;
                margin-left: auto;
                margin-right: auto;
            }
        }

        .category-row {
            width: 100%;
            z-index: 99;
            display: flex;
            align-items: center;
            box-sizing: border-box;          
        }
        .category-row1 {
            width: 100%;
            z-index: 99;
            display: flex;
            align-items: center;
            box-sizing: border-box;           
        }
        .label {
                margin-right: 16px;
                min-width: 40px;
            }

            .tags {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;

                .tag {
                    padding: 6px 16px;
                    border-radius: 4px;
                    cursor: pointer;
                    transition: all 0.3s;
                    &:hover {
                        color: #722ED1;
                    }
                }
            }

        .ceiling {
            display: flex;
            align-items: center;
            width: 100%;
            justify-content: space-between;
            box-sizing: border-box;
        }
    }
}
.sort-options {
    display: flex;
    gap: 45px;
    
    .sort-option {
        display: flex;
        align-items: center;
        cursor: pointer;
        font-size: 16px;
        transition: color 0.3s ease;
    }
}
.caret-wrapper{
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    height: 14px;
    width: 24px;
    vertical-align: middle;
    cursor: pointer;
    position: relative;
    overflow: initial;
    .sort-caret {
        width: 0px;
        height: 0px;
        position: absolute;
        left: 7px;
        border-width: 5px;
        border-style: solid;
        border-color: transparent;
        border-image: initial;
        transition: all 0.3s;
    }
    .sort-caret.ascending {
        top: -5px;
        border-bottom-color: #a8abb2;
        &:hover, &.active {
            border-bottom-color: #409eff;
        }
    }
    .sort-caret.descending {
        bottom: -3px;
        border-top-color: #a8abb2;
        &:hover, &.active {
            border-top-color: #409eff;
        }
    }
    .sort-caret.ascending.active {
        border-bottom-color: #409eff;
    }
    .sort-caret.descending.active {
        border-top-color: #409eff;
    }
} 
  :deep(.el-checkbox) {
    height: 20px;
  }
  :deep(.el-loading-mask) {
    background-color: transparent;
  }
  .category-row {
     width: 100%;
     z-index: 99;
    display: flex;
    align-items: center;

    .label {
        margin-right: 16px;
        min-width: 40px;
    }

    .tags {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;

        .tag {
            padding: 6px 16px;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s;
            &:hover {
                color: #722ED1;
            }
        }
    }
}
.ceiling {
    // transition: all 0.3s ease;
    background: transparent;
    padding: 10px 0;
    z-index: 100;
    
    &.ceiling-fixed {
        position: sticky;
        top: 0;
        background: #fff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin: 0 -10%;
        padding: 10px 10%;
    }
}
.stat {
    display: flex;
    align-items: center;
    gap: 5px;
}

.description-cell {
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

// 树形表格样式优化
:deep(.el-table) {
    .el-table__expand-icon {
        color: #409eff;
    }
    .cell {
        display: flex;
        align-items: center;
    }
    .el-table__row {
        &.el-table__row--level-0 {
            background-color: #f5f7fa;
            font-weight: 600;
        }
    }
    
    .el-table__placeholder {
        display: none;
    }
}
</style>