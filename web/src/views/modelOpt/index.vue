<template>
   <div style="width: 100%; height: 100%; padding: 20px;">
     <div class="model-opt-container" style="background-color: #fff; height: 100%; padding: 20px;">
        <!-- <div style="margin-bottom: 10px; display: flex; gap: 10px;">
            <el-input v-model="queryParams.name" placeholder="搜索" style="width: 300px;" @input="getStoreList" />
            <el-select v-model="queryParams.modelType" placeholder="模型类型" style="width: 150px;">
                <el-option v-for="item in ai_model_type" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-select v-model="queryParams.providerCode" placeholder="供应商" style="width: 150px;">
                <el-option v-for="item in ai_llm_model" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
        </div> -->
        <div class="search-input-wrapper" style="margin-bottom: 10px;">
           <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
             <span style="font-weight: bold;">模型管理</span>
             <el-input v-model="queryParams.name" placeholder="搜索" style="width: 300px;" @input="getStoreList" />
           </div>
           <div style="display: flex; align-items: center; margin-bottom: 10px; color: #6A6A6A;">
              <span style="width: 100px;">全部类型：</span>
              <div style="display: flex; align-items: center;">
                <span style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.modelType === '' ? '#6b05a8' : '' }" @click="handleThemeClick('')">全部</span>
                <span v-for="item in ai_model_type" style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.modelType === item.value ? '#6b05a8' : '' }" :key="item.value" @click="handleThemeClick(item.value)">{{ item.label }}</span>
              </div>
           </div>
           <div style="display: flex; align-items: center; color: #6A6A6A;">
              <span style="width: 100px;">供应商：</span>   
              <div>
                <span style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.providerCode === '' ? '#6b05a8' : '' }" @click="handleProviderCode('')">全部</span>
                <span v-for="item in ai_llm_model" style="padding: 0 10px; cursor: pointer;" :style="{ color: queryParams.providerCode === item.value ? '#6b05a8' : '' }" :key="item.value" @click="handleProviderCode(item.value)">{{ item.label }}</span>
              </div>
           </div>
        </div>
        <el-table
            v-loading="loading" 
            :data="cardList" 
            border 
            :max-height="maxHeight"
            @row-click="handleRowClick"
            style="cursor: pointer;"
        >
            <el-table-column prop="name" label="模型名称" width="250" >
                <template #default="{row}">
                    <div style="display: flex; align-items: center; gap: 5px;">
                        <img :src="row.modelIcon" alt="" style="width: 20px; height: 20px;">
                        <span>{{ row.name }}</span>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="modelType" label="模型类型" width="170">
                <template #default="scope">
                   <span>{{ getModelType(scope.row.modelType) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="description" label="描述">
                <template #default="scope">
                    <span class="description-cell">{{ scope.row.description || '-' }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="provider" label="模型厂商" width="200" />
            <!--
                  <el-tooltip
                    content="编辑"
                    placement="top"
                  >
                    <el-button link icon="EditPen" @click.stop="handleEdit(row)"></el-button>
                  </el-tooltip>
                  <el-tooltip
                    content="删除"
                    placement="top"
                  >
                    <el-button link icon="Delete" @click.stop="handleDelete(row)"></el-button>
                  </el-tooltip>
                </template>
            </el-table-column> -->
        </el-table>
        
        <!-- 模型详情弹窗 -->
        <el-dialog 
            v-model="dialogVisible" 
            title="模型详情" 
            width="700px"
        >
            <el-form :model="currentModel" label-width="100px">
                <el-form-item label="模型名称">
                    <el-input v-model="currentModel.name" />
                </el-form-item>
                <el-form-item label="模型类型">
                    <el-select v-model="currentModel.modelType">
                        <el-option v-for="item in ai_model_type" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="模型厂商">
                    <el-select v-model="currentModel.provider">
                        <el-option v-for="item in ai_llm_model" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <!-- <el-form-item label="API端点">
                    <el-input   v-model="currentModel.description"/>
                </el-form-item>
                <el-form-item label="API密钥">
                    <el-input   v-model="currentModel.description"/>
                </el-form-item>
                <el-form-item label="模型版本">
                    <el-input   v-model="currentModel.description"/>
                </el-form-item> -->
                <el-form-item label="描述">
                    <el-input 
                        v-model="currentModel.description" 
                        type="textarea" 
                        :autosize="{ minRows: 4, maxRows: 10 }"
                    />
                </el-form-item>
                <!-- <el-form-item label="激活模型">
                    <el-switch v-model="currentModel.status	" />
                </el-form-item> -->
            </el-form>
            <!-- <template #footer>
                <el-button type="primary" @click="handleSave">保存</el-button>
            </template> -->
        </el-dialog>
     </div>
   </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowDown, Star, View, User } from '@element-plus/icons-vue'
import { 
  getModel,
  deleteModel,
  editModel,
} from '@/api/retrieve'
import WdCard from '@/components/WdCard'
const { proxy } = getCurrentInstance();
const { ai_llm_model, ai_model_type } = toRefs(proxy?.useDict('ai_llm_model', 'ai_model_type'));
import notData from '@/assets/images/notData.png'
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
const maxHeight = ref(500)
const total = ref(0)

// 弹窗相关
const dialogVisible = ref(false)
const currentModel = ref({
    name: '',
    modelType: '',
    modelTypeLabel: '',
    description: ''
})

const handleThemeClick = (value) => {
    queryParams.value.modelType = value
    getStoreList()
}

const handleDelete = (row) => {
   ElMessageBox.confirm('确定删除该模型吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
   }).then(() => {
    deleteModel({
        id: row.id
    }).then(res => {
        ElMessage.success('删除成功')
        getStoreList()
    }).catch(() => {
        ElMessage.error('删除失败')
    })
   })
}

const handleEdit = (row) => {
    currentModel.value = row
    dialogVisible.value = true
}

const handleProviderCode = (value) => {
    queryParams.value.providerCode = value
    getStoreList()
}

const getModelType = (value) => {
    return ai_model_type.value.find(item => item.value === value)?.label
}

const cardList = ref([])

// 处理表格行点击
const handleRowClick = (row) => {
    currentModel.value = {
        name: row.name || '',
        modelType: row.modelType || '',
        modelTypeLabel: getModelType(row.modelType) || '',
        description: row.description || ''
    }
    dialogVisible.value = true
}

const getStoreList = () => {
    loading.value = true
    getModel(queryParams.value).then(res => {
        loading.value = false
        cardList.value = res.data
        total.value = res.total
    })
}

const isCeilingFixed = ref(false)

const handleScroll = () => {
    const scrollTop = document.querySelector('.ai-store').scrollTop
    const ceilingElement = document.querySelector('.ceiling')
    if (ceilingElement) {
        const ceilingOffset = ceilingElement.offsetTop
        isCeilingFixed.value = scrollTop >= ceilingOffset
    }
}

onMounted(() => {
    ai_llm_model.value.forEach(item => {
        item.checked = false
    })
    ai_model_type.value.forEach(item => {
        item.checked = false
    })
    console.log(document.querySelector('.model-opt-container').offsetHeight)
    maxHeight.value = document.querySelector('.model-opt-container').offsetHeight - 160
    getStoreList()

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
  :deep(.el-checkbox.el-checkbox--large .el-checkbox__inner) {
    width: 16px;
    height: 16px;
    border-radius: 0 0 14px 0;
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
</style>