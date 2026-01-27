<template>
    <div style="height: 100%;">
        <div style="height: 44px; background-color: #fff; display: flex; align-items: center; justify-content: end;gap: 20px; padding: 0 20px;">
           <el-input v-model="queryParams.name" style="width: 200px;" placeholder="请输入制造商名称" @input="handleInput" />
           <el-button type="primary" @click="handleAdd">新增</el-button>
        </div>
        <div style="height: calc(100% - 60px); padding: 20px 10px 20px 20px;" class="custom-scrollbar-container">
           <div class="card-list">
                <wd-card
                    v-for="(item, index) in tableData"
                    :key="index" 
                    :name="item.name"
                    :description="item.desc"
                    :status="item.status === '连接成功' ? 3 : 4"
                    :icon="getIcon(item.providerCode)"
                    :tag="item.modelCount + '个模型'"
                    :createByName="item.createByName"
                >
                <template #opt>
                            <el-dropdown placement="bottom-end" trigger="click" :teleported="false" @click.stop @command='(cmd) => handleOptionClick(cmd, item)'>
                                <el-button style="background: #F2F6FF;width: 34px;" plain icon="MoreFilled" size="small" @click.stop></el-button>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item command="edit" icon="SetUp" @click.stop>编辑</el-dropdown-item>
                                        <el-dropdown-item command="del" icon="Delete"  @click.stop>删除</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </template>
                        <template #footer>
                            <div style=" width: 100%;">
                                <span style="display: flex; align-items: center; justify-content: end; height: 20px;">
                                    <el-icon style="font-size: 14px; margin-right: 3px;color: #898989;"><Calendar /></el-icon>
                                    {{  moment(item.time).format('YYYY-MM-DD') }}
                                </span>
                            </div>
                        </template>
                </wd-card>
           </div>
        </div>
        <el-dialog v-model="dialogVisible" :title="form.id ? '编辑制造商' : '新增制造商'" width="500px"  @close="cancel(formRef)">
            <el-form ref="formRef" :model="form" label-width="150px" label-position="left"  :rules="rules">
                <el-form-item label="支持厂商" prop="providerCode">
                    <el-select v-model="form.providerCode" :disabled="!!form.id" placeholder="请选择支持厂商" @change="handleProviderCode">
                        <el-option v-for="item in filteredAiLlmModel" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="名称" prop="name">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item v-if="form.type !== 'cloud' || form.providerCode === 'ollama'" :label="form.providerCode === 'ollama' ? 'Base URL' : 'endpoint'" prop="endpoint">
                    <el-input v-model="form.endpoint" />
                </el-form-item>
                <el-form-item v-if="form.type !== 'cloud' && form.providerCode !== 'ollama'" label="Completions-path">
                    <el-input v-model="form.completionsPath" />
                </el-form-item>
                <el-form-item v-if="form.providerCode !== 'ollama'" label="API key" prop="apiKey">
                    <el-input v-model="form.apiKey" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="testManufacturerFn(formRef)">测试</el-button>
                <el-button @click="cancel(formRef)">取消</el-button>
                <el-button type="primary" @click="submitTenant(formRef)">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>
<script setup>
import WdCard from '@/components/WdCard'
const { proxy } = getCurrentInstance();
const { ai_llm_model } = toRefs(proxy?.useDict('ai_llm_model'));
import moment from 'moment'
import { getManufacturerList, addManufacturer, editManufacturer, deleteManufacturer, testManufacturer } from '@/api/manufacturer'
const queryParams = ref({           
    name: ''
})
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({
    providerCode: '',
    type: '',
    name: '',
    endpoint: '',
    completionsPath: '',
    apiKey: ''
})
const rules = ref({
    name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
    providerCode: [{ required: true, message: '请选择支持厂商', trigger: 'change' }],
    endpoint: [{ required: true, message: '请输入', trigger: 'blur' }],
    completionsPath: [{ required: true, message: '请输入Completions-path', trigger: 'blur' }],
    apiKey: [{ required: true, message: '请输入API key', trigger: 'blur' }]
})

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
const tableData = ref([])

// 过滤已存在的厂商，新增时排除所有已存在的，编辑时排除除当前项外的已存在的
const filteredAiLlmModel = computed(() => {
    if (!ai_llm_model.value || !Array.isArray(ai_llm_model.value)) {
        return []
    }
    
    // 获取列表中已存在的 providerCode
    const existingProviderCodes = tableData.value
        .map(item => item.providerCode)
        .filter(code => code) // 过滤空值
    
    // 如果是编辑模式（有 id），排除当前编辑项的 providerCode
    if (form.value.id) {
        const currentProviderCode = form.value.providerCode
        const filteredCodes = existingProviderCodes.filter(code => code !== currentProviderCode)
        return ai_llm_model.value.filter(item => !filteredCodes.includes(item.value))
    } else {
        // 新增模式，排除所有已存在的 providerCode
        return ai_llm_model.value.filter(item => !existingProviderCodes.includes(item.value))
    }
})

const getIcon = (providerCode) => {
    return manuImg.find(item => item.name === providerCode)?.icon || ''
}
const handleOptionClick = (cmd, item) => {
    console.log('item', item)
    if (cmd === 'edit') {
        handleEdit(item)
    } else if (cmd === 'del') {
        handleDelete(item)
    }
}
const handleInput = (value) => {
    getList()
}

const cancel = (formEl) => {
    console.log('111111111111')
    if (!formEl) return
    formEl.resetFields()
    form.value = {
        providerCode: '',
        type: '',
        name: '',
        endpoint: '',
        completionsPath: '',
        apiKey: ''
    }
    dialogVisible.value = false
}

const testManufacturerFn = async (formEl) => {
    console.log('formEl', form.value)
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
           let res = await testManufacturer(form.value)
           if (res.code === 'ok') {
            ElMessage.success('测试成功')
           } else {
            ElMessage.error('测试失败')
           }
        }
    })
}

const submitTenant = async (formEl) => {
    console.log('formEl', form.value)
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            console.log('form.value', form.value)
            if (form.value.id) {
               let res = await editManufacturer(form.value)
               if (res.code === 'ok') {
                ElMessage.success('编辑成功')
                getList()
                cancel(formEl)
               }
            } else {
                let res = await addManufacturer(form.value)
                if (res.code === 'ok') {
                    proxy?.$modal.msgSuccess('新增成功')
                    getList()
                    cancel(formEl)
                }
            }
        }
    })
}

const handleAdd = () => {
    dialogVisible.value = true
}

const handleProviderCode = (value) => {
    console.log('value', value)
    if (value === 'ollama' || value === 'openai') {
        form.value.type = 'other'
    } else {
        form.value.type = 'cloud'
    }
}

const handleEdit = (item) => {
    form.value = { ...item }
    dialogVisible.value = true
}

const handleDelete = (item) => {
    ElMessageBox.confirm(`确定删除${item.name}吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        deleteManufacturer({id: item.id}).then(res => {
            ElMessage.success('删除成功')
            getList()
        })
    })
}

const getList = () => {
    getManufacturerList(queryParams.value).then(res => {
        tableData.value = res.data
    })
}

onMounted(() => {
    getList()
})
</script>
<style lang="scss" scoped>
.card-list {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
}
</style>