<template>
    <div class="ai-store">
        <img src="@/assets/images/retrieve_bg.png" class="retrieve-bg" alt="">
        <img src="@/assets/images/specialist_bg.png" class="retrieve-bg-2" alt="">
        <div class="gradient-text">工业智能的“知识中枢”</div>
        <!-- 分类区域 -->
        <div class="search-input-wrapper">
            <input 
                v-model="queryParams.name" 
                placeholder="请搜索" 
                @keyup.enter="getStoreList" 
                style="width: 760px" 
                clearable 
                @clear="getStoreList"  
                class="search-input"
            />
            <el-button @click="getStoreList" class="search-button">
                <img src="@/assets/icons/svg/search-btn.svg" alt="">
            </el-button>
        </div>
        <div style="display: flex;gap: 20px; width: 100%; position: relative; z-index: 99; margin: 20px 0;">
            <div 
                class="banner-item" 
                v-for="(item, index) in recommendList" 
                :key="item.name" 
                @click="handleCardClick(item)"
                :style="{ backgroundImage: `url(${bannerImg[index % bannerImg.length]})` }"
            >
                <div style="display: flex; align-items: center; gap: 10px; margin-top: 62px;">
                    <img :src="item.logo" style="width: 40px; height: 40px; border-radius: 4px;" alt="">
                    <div>
                        <div style="color: #fff; font-size: 14px; font-weight: bold;">{{ item.name }}</div>
                        <div class="item_tag" v-if="item.tags && item.tags.length > 0">{{ item.tags[0] }}</div>
                    </div>
                </div>
                <div style="font-size: 14px; color: #fff; margin-top: 3px;" class="text-ellipsis">{{ item.description }}</div>
            </div>
        </div>
        <div class="category-row">
            <span class="label">业务环节：</span>
            <div style="flex: 1;" class="dp-s">
                <div class="tags">
                    <span class="tag" :style="{ color: queryParams.subjectId === '' ? '#722ED1' : '#000' }" @click="handleThemeClick('')">全部</span>
                    <span class="tag" v-for="item in ai_shop_subject" :key="item.value" :style="{ color: queryParams.subjectId === item.value ? '#722ED1' : '#000' }" @click="handleThemeClick(item.value)">{{ item.label }}</span>
                </div>
            </div>
        </div>
        <div style="display: flex;align-items: center; width: 100%; justify-content: space-between; position: relative; z-index: 99;">
            <div style="display: flex; align-items: center;">
                <span style="margin-right: 16px;">全部来源：</span>
                <div class="tags">
                    <span class="tag"  :style="{ color: queryParams.source === '' ? '#722ED1' : '#000' }" @click="handleSourceType('')">全部</span>
                    <span v-for="option, in sortOptions" :key="option.value" :style="{ color: queryParams.source === option.value ? '#722ED1' : '#000' }" class="tag" @click="handleSourceType(option.value)"> {{ option.label }}</span>
                </div>
            </div>
            <div style="display: flex; gap: 10px;">
                <el-select v-model="queryParams.status" placeholder="请选择状态" style="width: 130px;" @change="getStoreList" clearable>
                    <el-option label="草稿" :value="0"></el-option>
                    <el-option label="已发布" :value="1"></el-option>
                </el-select>
                <el-select v-model="queryParams.permission" placeholder="请选择权限" style="width: 130px;" clearable @change="getStoreList">
                    <el-option label="公开" :value="1"></el-option>
                    <el-option label="私有" :value="2"></el-option>
                </el-select>
                <!-- <el-select v-model="queryParams." placeholder="排序方式" style="width: 150px;" clearable @change="getStoreList">
                    <el-option label="最新" :value="1"></el-option>
                    <el-option label="最热" :value="0"></el-option>
                </el-select> -->
                <el-button type="primary" icon="Plus" @click="handleCreateClick">知识专家</el-button>
            </div>
        </div>
        <div style="flex: 1; width: 100%; margin-top: 20px;" v-loading="loading">
            <!-- 卡片列表区域 -->
            <div class="card-list">
                <wd-card 
                v-for="(item, index) in cardList" 
                :key="index" 
                :icon="item.logo" 
                :name="item.name" 
                :createByName="item.createByName"
                :createdBy="item.createdBy"
                :description="item.description"
                :tag="item.tags && item.tags.length > 0 ? item.tags[0] : null"
                :status="item.status"
                @click="handleCardClick(item)"
                >
                    <template #opt>
                        <el-dropdown placement="bottom-end" trigger="click" :teleported="false" @click.stop @command='(cmd) => handleOptionClick(cmd, item)'>
                            <el-button style="background: #F2F6FF;width: 34px;" plain icon="MoreFilled" size="small" @click.stop></el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <!--  本人创建 && 未发布状态 -->
                                    <el-dropdown-item 
                                        command="edit" 
                                        icon="EditPen" 
                                        :disabled="item.createdBy !== userId || item.status === 1"
                                        @click.stop
                                    >
                                        编辑
                                    </el-dropdown-item>
                                    <!-- 未发布状态 && 创建本人 -->
                                    <el-dropdown-item 
                                        command="delete" 
                                        icon="Delete" 
                                        :disabled="item.status === 1 || item.createdBy !== userId"
                                        @click.stop
                                    >
                                        删除
                                    </el-dropdown-item>
                                    <!-- （公开状态 || 本人创建） && 未发布状态 -->
                                    <el-dropdown-item command="config" icon="SetUp" v-if="(item.permission === 1 || item.createdBy === userId) && item.status === 0" @click.stop>配置</el-dropdown-item>
                                    <!-- 公开状态 || 本人创建 -->
                                    <el-dropdown-item command="copy" icon="CopyDocument" v-if="item.permission === 1 || item.createdBy === userId" @click.stop>复制</el-dropdown-item>
                                    <!-- 未发布状态 && 创建本人 -->
                                    <el-dropdown-item command="expand" icon="Expand" v-if="item.status === 0 && item.createdBy === userId" @click.stop>发布</el-dropdown-item>
                                    <!-- 已发布状态 && 创建本人 -->
                                    <el-dropdown-item command="unExpand" icon="Expand" v-if="item.status === 1 && item.createdBy === userId" @click.stop>取消发布</el-dropdown-item>
                                    <!-- 官方 && 已发布状态 && 未推荐状态 -->
                                    <el-dropdown-item command="recommend" icon="Connection"  v-if="userId === 1 && !item.recommend && item.status === 1" @click.stop>设为推荐</el-dropdown-item>
                                    <!-- 官方  && 推荐状态 -->
                                    <el-dropdown-item command="unRecommend" icon="Connection"  v-if="userId === 1 && item.recommend === true" @click.stop>取消推荐</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </template>
                    <template #footer>
                        <!-- <span>{{ item.permission === 1 ? '公开' : '私有' }}</span> -->
                        <span style="display: flex; align-items: center; height: 20px;">
                            <svg t="1761191432058" class="icon" style="margin-right: 3px;" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="17552" width="16" height="16"><path d="M512 917.333333c-200.533333 0-362.666667-160-362.666667-358.4 0-34.133333 4.266667-66.133333 12.8-98.133333 6.4-23.466667 14.933333-46.933333 27.733333-66.133333 19.2-34.133333 34.133333-57.6 55.466667-98.133333 23.466667-42.666667 21.333333-96 21.333333-96s42.666667 44.8 59.733333 83.2c12.8 32 2.133333 59.733333 2.133333 59.733333s61.866667-36.266667 98.133333-89.6c2.133333-4.266667 4.266667-6.4 4.266667-8.533333l0-2.133333C441.6 234.666667 448 219.733333 448 192c0-29.866667-14.933333-59.733333-32-83.2l0-2.133333c0 0 76.8 27.733333 138.666667 85.333333 83.2 74.666667 128 170.666667 128 170.666667s2.133333-44.8 14.933333-76.8c17.066667-51.2 59.733333-83.2 59.733333-83.2s4.266667 51.2 21.333333 96c17.066667 42.666667 46.933333 78.933333 55.466667 98.133333 10.666667 21.333333 19.2 42.666667 27.733333 66.133333 8.533333 29.866667 12.8 64 12.8 98.133333C874.666667 757.333333 712.533333 917.333333 512 917.333333zM814.933333 458.666667c-10.666667-29.866667-36.266667-59.733333-64-108.8-8.533333-14.933333-19.2-59.733333-19.2-59.733333-6.4 17.066667-10.666667 55.466667-25.6 98.133333-10.666667 32-21.333333 53.333333-25.6 59.733333 0-4.266667-8.533333-29.866667-96-151.466667-40.533333-55.466667-85.333333-91.733333-98.133333-102.4C490.666667 192 490.666667 192 490.666667 192l0 2.133333C488.533333 192 486.4 192 486.4 192l0 27.733333c-6.4 25.6-25.6 70.4-83.2 130.133333-61.866667 64-89.6 61.866667-89.6 61.866667s-12.8-12.8-19.2-34.133333c-8.533333-34.133333-6.4-64-6.4-64s-23.466667 32-44.8 64c-19.2 27.733333-29.866667 44.8-40.533333 76.8C194.133333 486.4 192 522.666667 192 556.8 192 731.733333 334.933333 874.666667 512 874.666667c177.066667 0 320-142.933333 320-317.866667C832 522.666667 825.6 488.533333 814.933333 458.666667zM682.666667 448C682.666667 448 682.666667 448 682.666667 448 682.666667 448 682.666667 448 682.666667 448z" p-id="17553" fill="#898989"></path></svg>
                            {{ item.active || 0 }}
                        </span>
                        <span style="display: flex; align-items: center; height: 20px; cursor: pointer;" @click.stop="handleCollectClick(item)">
                            <el-icon v-if="item.collected" style="font-size: 20px;color: #FFA401; margin-right: 3px;"><StarFilled /></el-icon>
                            <el-icon v-else style="font-size: 16px; margin-right: 3px;color: #898989;"><Star /></el-icon>
                            {{ item.collect || 0 }}
                        </span>
                        <span style="display: flex; align-items: center; height: 20px;">
                            <el-icon style="font-size: 14px; margin-right: 3px;color: #898989;"><Calendar /></el-icon>
                            {{  moment(item.updatedAt).format('YYYY-MM-DD') }}
                        </span>
                    </template>
                </wd-card>
            </div>
            <div v-if="total === 0 && !loading" class="dp-cc">
                <img :src="notData" alt="notData" />
            </div>
            <pagination
              v-show="total > 8"
              v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize"
              :total="total"
              :pageSizes="[8, 24, 48, 96]"
              @pagination="getStoreList"
            />
        </div>
         <!-- 创建专家弹窗 -->
         <el-dialog v-model="showCreateDialog"  :title="createForm.id ? '编辑知识专家' : '创建知识专家'" width="700px" :close-on-click-modal="false" >
            <el-form :model="createForm" ref="createFormRef" :rules="rules" label-width="120px" class="create-form">
                <!-- 助手名称 -->
                <el-form-item label="知识专家名称" prop="name">
                    <el-input v-model="createForm.name" placeholder="请输入知识专家名称"/>
                </el-form-item>
                <!-- 助手头像 -->
                <el-form-item label="知识专家头像" prop="logo">
                    <div style="display: flex;align-items: center;">
                        <div style="position: relative; display: inline-block;">
                            <el-upload 
                                ref="uploadRef" 
                                class="avatar-uploader" 
                                action="/common/upload" 
                                :show-file-list="false" 
                                :before-upload="beforeUpload" 
                                :http-request="fileUploadFn"
                            >
                                <img v-if="createForm.logo" :src="createForm.logo" class="avatar" />
                                <el-icon v-else class="avatar-uploader-icon">
                                    <Plus />
                                </el-icon>
                            </el-upload>
                            <!-- 删除按钮 -->
                            <el-button 
                                v-if="createForm.logo" 
                                type="danger" 
                                size="small" 
                                circle 
                                @click="removeLogo"
                                style="position: absolute; top: -8px; right: -8px; z-index: 10;"
                                title="删除头像"
                            >
                                <el-icon><Delete /></el-icon>
                            </el-button>
                        </div>
                        <div style="display: flex;flex-direction: column; margin-left: 20px; height: 80px">
                            <div style="flex: 1;">
                                <el-button link icon="MagicStick"  style="font-size: 20px;color: #1568FF;" @click="handleRandomImage"></el-button>
                            </div>
                            <span style="line-height: 16px;">点击魔法棒随机生成或点图片上传</span>
                        </div>
                    </div>
                </el-form-item>              
                <el-form-item label="业务环节" prop="subjectId">
                    <el-select v-model="createForm.subjectId" clearable placeholder="请选择业务环节">
                        <el-option v-for="item in ai_shop_subject" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="标签" prop="tags">
                    <el-autocomplete
                        v-model="tagInput"
                        :fetch-suggestions="querySearch"
                        clearable
                        class="w-50"
                        placeholder="请选择标签"
                        @select="handleSelect"
                        @change="handleTagInput"
                    >
                        <template #default="{ item }">
                            <div class="value">{{ item }}</div>
                        </template>
                    </el-autocomplete>
                </el-form-item>
                <el-form-item label="访问权限" prop="permission">
                    <el-radio-group v-model="createForm.permission">
                        <el-radio :value="1" size="large">公开</el-radio>
                        <el-radio :value="2" size="large">私有</el-radio>
                    </el-radio-group>
                </el-form-item>
                  <!-- 助手描述 -->
                  <el-form-item label="知识专家描述" prop="description">
                    <el-input v-model="createForm.description"  type="textarea"  :rows="3"  placeholder="请详细描述知识专家功能" maxlength="200" show-word-limit/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="cancel(createFormRef)">取消</el-button>
                <el-button   type="primary" @click="handleCreateConfirm(createFormRef)"  :loading="createLoading"> 确认 </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>

// import { getPublishApp } from '@/api/base'
import { addFavorite, cancelFavorite, checkFavorite } from '@/api/retrieve'
import { getAssistantList, createAssistant, updateAssistant,deleteAssistant, copyAssistant } from '@/api/knowledgeExpert'
import WdCard from '@/components/WdCard'
import { commonFileUpload } from '@/api/ai/aiStore'
import moment from 'moment'
const { proxy } = getCurrentInstance();
const { ai_shop_subject } = toRefs(proxy?.useDict('ai_shop_subject'));
import notData from '@/assets/images/notData.png'
import { useRouter } from 'vue-router'
import banner1 from './img/specialist_bnner_1.png'
import banner2 from './img/specialist_bnner_2.png'
import banner3 from './img/specialist_bnner_3.png'
import banner4 from './img/specialist_bnner_4.png'
const router = useRouter()
const loading = ref(false)
const queryParams = ref({
    name: '',
    pageNum: 1,
    pageSize: 8,
    status: '',
    subjectId: '',
    permission: '',
    sourceType: '',
    source: ''
   
})
const userId = ref('')
const bannerImg = ref([banner1, banner2, banner3, banner4])
const recommendList = ref([])
const total = ref(0)
const sortOptions = [
  { label: '官方', value: 'official' },
  { label: '我创建的', value: 'myself' },
  { label: '我收藏的', value: 'collected' }
]

const currentSort = ref('update_time')

// 创建专家弹窗相关
const showCreateDialog = ref(false)
const createLoading = ref(false)
const createFormRef = ref(null)
const uploadRef = ref(null)
const tagInput = ref('')
const createForm = ref({
    id: null,
    name: '',
    description: '',
    logo: 'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/c9caaf29-4af3-4196-87cb-fa0a1038b9ca.jpg',
    type: 'knowledge_expert',
    subjectId: '',
    tags: [],
    permission: 1
})

// 表单验证规则
const rules = ref({
    name: [{ required: true, message: '请输入Agent名称', trigger: 'blur' }],
    subjectId: [{ required: true, message: '请选择业务环节', trigger: 'blur' }],
    logo: [{ required: true, message: '请上传Agent头像', trigger: 'blur' }],
    type: [{ required: true, message: '请选择Agent类型', trigger: 'blur' }],
    description: [{ required: true, message: '请输入Agent描述', trigger: 'blur' }]
})

// 标签相关数据
const list = ref([
    {
        key: 'factory_construction',
        children: ['工厂数字化规划设计', '数字化基础设施建设', '数字孪生工厂构建']
    },
    {
        key: 'product_r_d',
        children: ['产品数字化设计', '产品虚拟化验证']
    },
    {
        key: 'process_design',
        children: ['工艺数字化设计', '制造工程优化']
    },
    {
        key: 'production_management',
        children: ['生产计划优化', '车间智能排产', '生产进度跟踪', '生产动态调度', '仓储智能管理', '物料精准配送', '危险作业自动化', '安全一体化管控', '能源智能管控', '碳资产安全生命周期管理', '污染在线管理', '网络协同制造']
    },
    {
        key: 'manufacturing',
        children: ['柔性产线快速换产', '工艺动态优化', '先进过程控制', '人机协同作业', '在线智能检测', '质量精准追溯', '质量分析与改进', '设备运行监控', '设备故障诊断与预测', '设备维修维护']
    },
    {
        key: 'operation_management',
        children: ['智能经营决策', '数智精益管理', '规模化定制', '产品精准营销']
    },
    {
        key: 'product_service',
        children: ['远程运维服务', '产品增值服务', '客户主动服务', '产品精准营销']
    },
    {
        key: 'supply_chain_management',
        children: ['供应链数字化管理', '采购计划优化协同', '供应链风险预警与调度', '供应链物流智能配送']
    }
])

const handleSourceType = (value) => {
    queryParams.value.source = value
    getStoreList()
}


const cardList = ref([])

const handleCardClick = (item) => {
    router.push({
        path: '/space/specialist/detail',
        query: {
            uuid: item.uuid
        }
    })
}

const getStoreList = () => {
    loading.value = true
    getAssistantList(queryParams.value).then(res => {
        cardList.value = res.data.records
        total.value = res.data.total
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}


const handleThemeClick = (value) => {
    queryParams.value.subjectId = value
    getStoreList()
}  

const changeSourceType = (val) => {
    if (val === undefined) {
        queryParams.value.sourceType = ''
    }
    getStoreList()
}

// 创建专家相关方法
const handleCreateClick = () => {
    showCreateDialog.value = true
    resetForm()
}

const resetForm = () => {
    createForm.value = {
        id: null,
        name: '',
        description: '',
        logo: 'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/c9caaf29-4af3-4196-87cb-fa0a1038b9ca.jpg',
        type: 'knowledge_expert',
        subjectId: '',
        tags: [],
        permission: 1
    }
    tagInput.value = ''
    if (createFormRef.value) {
        createFormRef.value.clearValidate()
    }
}

const handleCreateConfirm = async (formEl) => {
    if (!formEl) return
    
    await formEl.validate(async (valid) => {
        if (valid) {
            createLoading.value = true
            try {
                const params = {
                    ...createForm.value,
                    tags: createForm.value.tags
                }
                
                if (createForm.value.id) {
                    // 编辑模式
                    delete params.recommend
                    await updateAssistant(params)
                    ElMessage.success('编辑成功')
                    getStoreList()
                } else {
                    // 创建模式
                   let response = await createAssistant(params)
                    router.push({
                        path: '/space/specialist/config',
                        query: {
                            uuid: response.data.uuid
                        }
                    })
                    ElMessage.success('创建成功')
                }
                showCreateDialog.value = false
            } catch (error) {
                ElMessage.error(createForm.value.id ? '编辑失败' : '创建失败')
            } finally {
                createLoading.value = false
            }
        }
    })
}

const cancel = (formEl) => {
    showCreateDialog.value = false
    resetForm()
}

// 文件上传相关
const beforeUpload = (file) => {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
    }
    if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB!')
        return false
    }
    return true
}

const fileUploadFn = async (options) => {
    const { file, onSuccess, onError } = options
    try {  
        const response = await commonFileUpload(file)
        if (response.code === 200) {
            createForm.value.logo = response.url
            onSuccess(response)
        } else {
            onError(new Error(response.message || '上传失败'))
        }
    } catch (error) {
        onError(error)
    }
}

const removeLogo = () => {
    createForm.value.logo = ''
}

const handleRandomImage = () => {
    // 随机生成头像的逻辑
    const randomImages = [
        'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/c9caaf29-4af3-4196-87cb-fa0a1038b9ca.jpg',
        'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/2294556a-5e94-4480-a852-9b96743c05be.jpg',
        'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/6c657fa1-7d29-4ceb-adae-64f54af530be.jpg',
        'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/b0674217-519d-4443-a57e-6697e3ca1cf4.jpeg',
        'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/bd4b0a09-1d7c-42e9-80e1-da3b064d9adc.jpg',
        'https://aidojo.wudao-tech.com/prod-api/profile/upload/2025/07/19/9f23705a-aae1-49d1-a7b7-a5c7ca539217.jpg'
    ]
    const randomIndex = Math.floor(Math.random() * randomImages.length)
    createForm.value.logo = randomImages[randomIndex]
}

// 标签相关方法
const querySearch = (queryString, callback) => {
    if (createForm.value.subjectId) {
        let foundItem = list.value.find(item => item.key === createForm.value.subjectId)
        if (foundItem) {
            let data = foundItem.children
            const results = queryString
                ? data.filter(item => item.includes(queryString))
                : data
            callback(results)
        } else {
            callback([])
        }
    } else {
        callback([])
    }
}

const handleSelect = (item) => {
    tagInput.value = item
    createForm.value.tags = [item]
}

// 处理标签手动输入
const handleTagInput = (value) => {
    if (value && value.trim()) {
        createForm.value.tags = [value.trim()]
    } else {
        createForm.value.tags = []
    }
}

const removeTag = (index) => {
    createForm.value.tags.splice(index, 1)
}

const handleCollectClick = (item) => {
    if (item.collected) {
        cancelFavorite({
            targetType: 'assistant',
            targetId: item.uuid,
        }).then(res => {
            ElMessage.success('取消收藏成功')
            item.collected = false
            item.collect --
        })
    } else {
        addFavorite({
            targetType: 'assistant',
            targetId: item.uuid,
        }).then(res => {
            ElMessage.success('收藏成功')
            item.collected = true
            item.collect ++
        })
    }
}

// 编辑专家
const handleEditSpecialist = (item) => {
    createForm.value = {...item}
    tagInput.value = item.tags && item.tags.length > 0 ? item.tags[0] : ''
    showCreateDialog.value = true
}

// 删除专家
const handleDeleteSpecialist = (item) => {
    ElMessageBox.confirm(`确定删除${item.name}吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        deleteAssistant(item.uuid).then(res => {
            if(res.code === 200) {
                ElMessage.success('删除成功')
                getStoreList()
            }
        })
    })
}

// 复制专家
const handleCopySpecialist = (item) => {
    copyAssistant(item.uuid).then(res => {
        if(res.code === 200) {
            ElMessage.success('复制成功')
            router.push({
                path: '/space/specialist/config',
                query: {
                    uuid: res.data.uuid
                }
            })
        }
    })
}

// 发布/取消发布
const handlePublishSpecialist = (item, status) => {
    const params = {
        uuid: item.uuid,
        status: status
    }
    updateAssistant(params).then(res => {
        if(res.code === 'ok') {
            ElMessage.success(status === 1 ? '发布成功' : '取消发布成功')
            getStoreList()
        }
    }).catch(() => {
        ElMessage.error(status === 1 ? '发布失败' : '取消发布失败')
    })
}

// 设为推荐
const handleRecommendSpecialist = (item, type) => {
    const params = {
        uuid: item.uuid,
        recommend: type
    }
    updateAssistant(params).then(res => {
        if(res.code === 'ok') {
            ElMessage.success(type ? '设为推荐成功' : '取消推荐成功')
            getStoreList()
            getfilterList()
        }
    }).catch(() => {
        ElMessage.error(type ? '设为推荐失败' : '取消推荐失败')
    })
}


// 操作菜单点击处理
const handleOptionClick = (command, item) => {
    switch (command) {
        case 'edit':
            handleEditSpecialist(item)
            break
        case 'delete':
            handleDeleteSpecialist(item)
            break
        case 'copy':
            handleCopySpecialist(item)
            break
        case 'expand':
            handlePublishSpecialist(item, 1)
            break
        case 'config':
            router.push({
                path: '/space/specialist/config',
                query: {
                    uuid: item.uuid
                }
            })
            break
        case 'recommend': 
           ElMessageBox.confirm(`确定把${item.name}设为推荐吗？`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
           }).then(() => {
               handleRecommendSpecialist(item, true)
           })
            break
        case 'unRecommend': 
        ElMessageBox.confirm(`确定取消${item.name}的推荐吗？`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
           }).then(() => {
               handleRecommendSpecialist(item, false)
           })
            break
        case 'unExpand':
            handlePublishSpecialist(item, 0)
            break
    }
}

// 点击上传按钮
const handleUploadClick = () => {
    if (uploadRef.value) {
        uploadRef.value.$el.querySelector('input[type="file"]').click()
    }
}

const getfilterList = () => {
    let params = {
        recommend: true
    }
    getAssistantList(params).then(res => {
       console.log('res1', res)
       recommendList.value = res.data.records
    })
}

onMounted(() => {
    userId.value = JSON.parse(localStorage.getItem('userInfo')).id
    getStoreList()
    getfilterList() // 推荐列表
})
</script>

<style scoped lang="scss">
.ai-store {
    position: relative;
    overflow-y: auto;
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
    padding: 0 10%;
    background: #F5F7FA !important;
    height: 100%;
    display: flex;
    flex-direction: column;
    .gradient-text {
        font-size: 34px;
        padding-top: 20px;
        font-weight: bold;
        text-align: center;
        background: linear-gradient(270.0000000000106deg, #5C078F 0%, #AF50C5 100%);
        background-clip: text;
        -webkit-background-clip: text;
        color: transparent;
        z-index: 99;
    }
    .card-list {
        display: flex;
        gap: 20px;
        flex-wrap: wrap;
    }
}
  :deep(.el-checkbox.el-checkbox--large .el-checkbox__inner) {
    width: 16px;
    height: 16px;
    border-radius: 0 0 14px 0;
  }
  :deep(.el-checkbox) {
    height: 20px;
  }
.search-input-wrapper {
    position: relative;
    width: 760px;
    margin-top: 20px;

.search-input {
    width: 100%;
    padding: 0 20px;
    border: none;
    font-size: 1rem;
    height: 60px;
    border-radius: 8px;
    box-shadow: 0px 4px 8px 0px rgba(199,207,223,0.3);
    &:focus {
    outline: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }
}

.search-button {
    position: absolute;
    right: 10px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    font-size: 1.2rem;
    cursor: pointer;
}
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

.banner-item {
    width: calc(25% - 15px);
    height: 145px;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    border-radius: 4px 4px 4px 4px;
    padding: 10px 20px;
    position: relative;
    cursor: pointer;
    transition: all 0.3s ease;
    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(107, 5, 168, 0.3);
    }
}
:deep(.el-upload-list__item) {
    width: 80px;
    height: 80px;
}
:deep(.el-upload--picture-card) {
    width: 80px;
    height: 80px;
}

.avatar-uploader .avatar {
  width: 80px;
  height: 80px;
  display: block;
}
.avatar-uploader .el-upload {
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  border: 1px #f5f5f5 solid;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
  border: 1px dashed var(--el-border-color);
}
.item_tag {
    font-size: 12px;
    color: #6B05A8;
    background: #F2F4FD;
    padding: 2px 6px;
    margin-top: 3px;
    border-radius: 4px;
    display: inline-block;
}
</style>