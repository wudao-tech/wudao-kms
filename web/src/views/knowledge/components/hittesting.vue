<template>
    <div class="test-content">
        <div style="display: flex; background: #fff; width: 100%;">
            <div style="width: 350px; border-right: 1px solid #D2D1D1; padding: 16px; display: flex; flex-direction: column;">
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px;">
                <el-select v-model="queryParams.documentIds" placeholder="请选择文件" clearable multiple collapse-tags collapse-tags-tooltip>
                    <el-option v-for="item in knowFileList" :key="item.id" :label="item.fileName" :value="item.id"></el-option>
                </el-select>
                <el-button plain icon="Setting" @click="handleSetting" style="margin-left: 5px;">检索配置</el-button>
            </div>
            <div class="search-area" style="position: relative;">
                <el-input
                    v-model="queryParams.query"
                    placeholder="请输入搜索内容"
                    type="textarea"
                    :rows="8"
                    ></el-input>
                    <el-button type="primary" style="position: absolute;right: 10px;bottom: 5px;" :disabled="!queryParams.query" size="small" @click="handleSearch">测试</el-button>
                </div>
                <el-divider content-position="left" style="color: #979797;">测试历史</el-divider>
                <div style="flex: 1;" class="custom-scrollbar-container">
                    <div class="history-item" :class="{ active: curId === item.id }" v-for="item in testHistory" :key="item.id" @click="handleHistoryClick(item)">
                        <span class="text-ellipsis" style="flex: 1; cursor: pointer;">{{ item.content }}</span>
                        <el-icon class="delete-icon" style="color: #999; cursor: pointer; margin: 0 10px;" @click.stop="handleDeleteHistory(item.id)"><Delete /></el-icon>
                    </div>
                </div>
            </div>
            <div style="width: calc(100% - 350px); padding: 10px; display: flex; flex-direction: column;">
                <div style="display: flex; gap: 20px;">
                    <div style="width: 300px;" >
                        <div style="margin-bottom: 10px;">测试文档</div>
                        <div class="custom-scrollbar-container" style="background: #F5F5F5; padding: 10px; height: 80px; overflow-y: auto;">
                        <div v-for="(item, index) in tarFileData" :key="index" class="text-ellipsis" style="line-height: 26px; color: #666666; font-size: 14px;">{{ item }}</div>
                        </div>
                    </div>
                    <div style="flex: 1;">
                        <div style="margin-bottom: 10px;">测试参数</div>
                        <div style="height: 80px; background: linear-gradient( 90deg, #E0F0FC 0%, #EFEAFA 100%); padding: 10px; display: flex; justify-content: space-around; align-items: center;">
                            <div style="flex-direction: column; display: flex; align-items: center;">
                                <span>搜索方式</span>
                                <span style="margin-top: 5px; color: #999; font-size: 14px; line-height: 20px;">{{ getType(queryParams.searchType) }}</span>
                            </div>
                            <div  style="flex-direction: column; display: flex; align-items: center;">
                                <span>最大召回分段</span>
                                <span style="margin-top: 5px; color: #999; font-size: 14px; line-height: 20px;">{{ queryParams.maxSegmentCount || 1 }}</span>
                            </div>
                            <div style="flex-direction: column; display: flex; align-items: center;">
                                <span>结果重排</span>
                                <span v-if="queryParams.rerankModel"  style="margin-top: 5px; color: #999; font-size: 14px; line-height: 20px;">{{ queryParams.rerankModel }}</span>
                                <span v-else  style="margin-top: 5px; color: #999; font-size: 14px; line-height: 20px;">-</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div style="margin: 20px 0 10px 0">测试结果</div>
                <div  v-loading="loading"  class="custom-scrollbar-container" style="flex: 1; background-color: #f5f5f5; padding: 10px; overflow: auto;">
                    <div class="result-card" v-for="result in testResults" :key="result.id" @click="handleResultClick(result)">
                        <div class="result-header">
                            <span style="margin-right: 10px;">{{ getType(result.search_type) }}</span>
                            <span v-if="result.rerank_score" style="margin-right: 10px;">结果重排: {{ result.rerank_score.toFixed(4) }}</span>
                            <span>相关度: {{ result.score.toFixed(4) }}</span>
                        </div>
                        <p class="result-content" v-html="result.highlight"></p>
                        <div class="result-footer">
                            <img v-if="result.answerType === 'QA'" src="@/assets/images/qa.png" style="width: 18px;" alt="">
                            <img v-else :src="getFileType(result.filename)" style="width: 18px;" alt="">
                            <span>{{ result.filename }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 检索配置弹窗组件 -->
        <RetrieveConfig 
            v-model:visible="showSettings" 
            :config="form" 
            @confirm="handleConfigConfirm"
        />
    </div>
</template>

<script setup>
import { 
    hitTestQuery, 
    historyRecord,
    deleteHistoryRecord
} from '@/api/base'
import notData from '@/assets/images/notData.png'
import RetrieveConfig from '@/components/RetrieveConfig.vue'
import { useRoute } from 'vue-router'
const { proxy } = getCurrentInstance()
const { rerank_model } = toRefs(proxy?.useDict('rerank_model'));
const route = useRoute()
const loading = ref(false)

const props = defineProps({
    knowFileList: {
        type: Array,
        default: () => []
    },
    knowledgeBaseIds: {
        type: Number,
        default: ''
    }
})

const form = ref({
    maxSegmentCount: 10,
    searchType: 'fulltext',
    rerankModel: 'qwen3-rerank'
})

const marks = {
  0: '0',
  20: '20'
}

const showSettings = ref(false)
const queryParams = ref({
    query: '',
    rerankModel: 'qwen3-rerank',
    knowledgeIds: [],
    searchType: 'fulltext',
    maxSegmentCount: 10,
    documentIds: [],
    knowledgeBaseIds: [],
    source: 'test_search',
    ragScore: 0.3,
    ragTopK: 10,
    rerankTopK: 10,
    rerankScore: 0.5
})
const tarFileData = ref([])
const curId = ref(null)
const typeData = [
    {label: '混合检索', value: 'hybrid'},
    {label: '语义检索', value: 'semantic'},
    {label: '全文检索', value: 'fulltext'}
]

// 测试历史记录
const testHistory = ref([])

// 测试结果
const testResults = ref([])

const getType = (val) => {
    return typeData.find(item => item.value === val)?.label
}
const getFileType = (fileName) => {
  if (!fileName) return new URL('/src/assets/fileIcon/othe.svg', import.meta.url).href;
  
  // 获取文件扩展名
  const extension = fileName.toLowerCase().split('.').pop();
  // 定义支持的文件类型
  const supportedExtensions = ['jpg', 'html', 'doc', 'ppt', 'mp4', 'txt', 'pdf', 'xls', 'png', 'md', 'csv', 'xlsx', 'xlsx', 'docx', 'jpeg'];
  
  // 特殊处理一些扩展名
  let iconName = extension;

  // 检查是否有对应的图标
  if (supportedExtensions.includes(iconName)) {
    return new URL(`/src/assets/fileIcon/${iconName}.svg`, import.meta.url).href;
  } else {
    return new URL('/src/assets/fileIcon/othe.svg', import.meta.url).href;
  }
}

const handleResultClick = (result) => {
    console.log('result', result)
}

const handleHistoryClick = (item) => {
    console.log('item', item)
    queryParams.value.query = item.content
    curId.value = item.id
    tarFileData.value = item.doc.map(item => item.fileName)
    queryParams.value.documentIds = item.doc.map(item => item.id)
    console.log('tarFileData', tarFileData.value)
    handleSearch(true) // 传入true表示从历史记录点击触发
}

const handleDeleteHistory = (id) => {
    ElMessageBox.confirm('确定删除该历史记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        deleteHistoryRecord(id).then(res => {
            handleHistory()
        })
    })
}

const handleSearch = (fromHistory = false) => {
    loading.value = true
    // 如果不是从历史记录点击触发的搜索，才重新设置tarFileData
    if (!fromHistory) {
        tarFileData.value = props.knowFileList.filter(item => queryParams.value.documentIds.includes(item.id)).map(item => item.fileName)
    } else {
        tarFileData.value = props.knowFileList.filter(item => queryParams.value.documentIds.includes(item.id)).map(item => item.fileName)
    }
    hitTestQuery(queryParams.value).then(res => {
        loading.value = false
        testResults.value = res.data
        testResults.value.forEach(item => {
            // 先将 <em> 标签替换为 <mark>，然后将所有 <mark> 标签替换为带样式的 <span>
            item.highlight = item.highlight
                .replace(/<em>/g, '<mark>')
                .replace(/<\/em>/g, '</mark>')
                .replace(/<mark>/g, '<span style="color: #447DFB;">')
                .replace(/<\/mark>/g, '</span>')
        })
        handleHistory()
    })
}

const handleSetting = () => {
    showSettings.value = true
    // 设置表单初始值
    form.value.maxSegmentCount = queryParams.value.maxSegmentCount
    form.value.searchType = queryParams.value.searchType
    form.value.rerankModel = queryParams.value.rerankModel
}

const handleConfigConfirm = (config) => {
    queryParams.value = {
        ...queryParams.value,
        ...config
    }
    // 更新 form 的值以便下次打开时显示最新的配置
    form.value = { ...config }
}

const handleHistory = () => {
    let params = {
        knowledgeBaseIds: props.knowledgeBaseIds
    }
    historyRecord(params).then(res => {
        testHistory.value = res.data
    })
}

// 监听 knowledgeBaseIds 变化，重新初始化组件
watch(() => props.knowledgeBaseIds, (newVal) => {
    if (newVal) {
        // 清空之前的数据
        testResults.value = []
        testHistory.value = []
        tarFileData.value = []
        queryParams.value.documentIds = []
        queryParams.value.rerankModel = 'qwen3-rerank'
        queryParams.value.searchType = 'fulltext'
        queryParams.value.maxSegmentCount = 10
        queryParams.value.query = ''
        curId.value = null
        
        // 重新设置参数
        queryParams.value.knowledgeBaseIds = [newVal]
        handleHistory()
    }
}, { immediate: true })

onMounted(() => {
    queryParams.value.knowledgeIds = [ Number(route.query.id)]
    queryParams.value.knowledgeBaseIds = [props.knowledgeBaseIds]
    console.log(123, queryParams.value)
    handleHistory()
})
</script>

<style scoped lang="scss">
.test-content {
    height: calc(100% - 50px);
    display: flex;
    padding: 20px;
}
.history-item {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
    cursor: pointer;
    height: 40px;
    line-height: 40px;
    background: #f5f5f5;
    padding-left: 8px;
    border-radius: 2px;
    transition: all 0.3s ease;
    display: flex;
    align-items:center;
    .delete-icon {
        display: none;
    }
    &:hover {
        background: #F2F6FF;
        transform: translateY(-2px);
        .delete-icon {
            display: block;
        }
    }
}

.selected-files {
    margin-top: 10px;
    
    .file-tag {
        margin: 2px 4px 2px 0;
        transition: all 0.3s ease;
        max-width: 200px;
        
        &:hover {
            transform: scale(1.02);
        }
        
        .el-tag__content {
            width: 100%;
        }
    }
}

.test-main {
    flex: 1;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    
    .recall-settings {
        width: 70%;
        margin: 0 auto;
        .recall-title {
            font-size: 16px;
            margin-bottom: 20px;
            color: #333;
            display: flex;
            align-items: center;
        }
    }
    
    
}
.result-card {
        border-radius: 8px;
        padding: 10px 20px;
        background-color: #fff;
        margin-bottom: 10px;
        .result-header {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            font-size: 12px;
            color: #999;
        }
        
        .result-content {
            font-size: 14px;
            color: #666;
            line-height: 1.5;
            margin-bottom: 0;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .result-footer {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: 10px;
            // .file-icon {
            //     color: #52c41a;
            // }
            span {
            font-size: 14px;
            font-weight: 700;
        }
    }
}
:deep(.el-rate__icon) {
    font-size: 16px;
}
:deep(.el-slider__button) {
    width: 16px !important;
    height: 16px !important;
}
.segment-options {
       margin-bottom: 10px;
      .segment-radio-group {
        display: flex;
        flex-direction: column;
        gap: 10px;
       
        .radio-option {
          padding: 10px;
          transition: all 0.3s;
          background: #F2F6FF;
          width: 100%; 
          :deep(.el-radio) {
            width: 100%;
            
            .el-radio__label {
              width: 100%;
              padding-left: 20px;
            }
          }
          
          .radio-content {
            .radio-title {
              font-size: 16px;
              color: #303133;
              margin-bottom: 8px;
            }
            
            .radio-description {
              font-size: 14px;
              color: #909399;
            }
          }
        }
      }
    }
.recall-title {
    font-size: 16px;
    // font-weight: 600;
    margin-bottom: 20px;
    color: #333;
    display: flex;
    align-items: center;
}
:deep(.el-divider__text) {
    color: #979797;
}
.active {
    background: #F2F6FF;
}
</style>
<style>
</style>
