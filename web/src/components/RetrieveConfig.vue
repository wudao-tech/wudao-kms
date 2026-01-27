<template> 
    <el-dialog
        v-model="dialogVisible"
        title="高级设置"
        width="600"
        :before-close="handleClose"
        class="component-dialog"
    >
        <div class="segment-options">
          <el-radio-group v-model="form.searchType" class="segment-radio-group">
            <div class="radio-option">
              <el-radio value="fulltext" size="large">
                <div class="radio-content">
                  <div class="radio-title">全文检索</div>
                  <div class="radio-description">通过关键词精准匹配进行检索</div>
                </div>
              </el-radio>
            </div>
            <div class="radio-option">
              <el-radio value="hybrid" size="large">
                <div class="radio-content">
                  <div class="radio-title">混合检索</div>
                  <div class="radio-description">同时使用语义检索和全文检索进行查询，并对结果进行排序</div>
                </div>
              </el-radio>
            </div>
            <div class="radio-option">
              <el-radio value="semantic" size="large">
                <div class="radio-content">
                  <div class="radio-title">语义检索</div>
                  <div class="radio-description">基于向量化技术，通过分析查询的语义和上下文来提供更准确和相关的搜索结果</div>
                </div>
              </el-radio>
            </div>
            </el-radio-group>   
        </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              最大召回分段
              <el-tooltip content="最大召回字数内，知识库查询结果中可以被召回的最大段落数" placement="top">
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-slider style="padding-left: 5px" v-model="form.maxSegmentCount" show-input :max="20" :min="1" size="small" class="max-recall-slider" />
            <!-- <span style="background: #F2F4FF; padding: 5px 10px; margin-left: 20px;">{{  form.maxSegmentCount }}</span> -->
        </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              检索分
              <el-tooltip content="根据检索分限制从知识库得到最相近的答案" placement="top" >
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-slider v-model="form.ragScore" :min="0" :max="1" :step="0.1" show-input size="small" />
        </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              top k
              <el-tooltip content="根据检索分排序，按照topK选出前几个结果" placement="top" >
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-slider v-model="form.ragTopK" :min="1" :max="20" :step="1" show-input size="small" />
        </div>

        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              结果重排
              <el-tooltip content="使用重排模型来进行二次排序,可增强综合排名" placement="top" >
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-select v-model="form.rerankModel" clearable>
              <el-option
                  v-for="item in modelOptions.filter(item => item.modelType === 'text_rerank')"
                  :key="item.model"
                  :label="item.name"
                  :value="item.model"
                  >
                  <div class="select-item-w" style="display: flex; align-items: center; gap: 8px;">
                     <img :src="item.modelIcon" style="width: 40px;" alt="">
                     <span>{{ item.name }}</span>
                  </div>
              </el-option>
              <template #label="{ label, value }">
                  <div style="display: flex; align-items: center; gap: 8px;">
                      <img :src="modelOptions.find(item => item.model === value)?.modelIcon" style="width: 24px;" alt="">
                      <span>{{ label }}</span>
                  </div>
               </template>
            </el-select>
        </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              重排分
              <el-tooltip content="增加重排分限制展示最相近的答案" placement="top" >
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-slider v-model="form.rerankScore" :min="0" :max="1" :step="0.1" show-input size="small" />
        </div>
        <div class="recall-title">
            <span  style="min-width: 120px; display: flex;margin-right: 10px;">
              重排top k
              <el-tooltip content="根据重排分排序从重排结果中选出前若干个的结果" placement="top" >
                <el-icon style="color:#D8D8D8; font-size: 12px;"><question-filled /></el-icon>
              </el-tooltip>
            </span>
            <el-slider v-model="form.rerankTopK" :min="1" :max="10" :step="1" show-input size="small" />
        </div>
        
        <template #footer>
            <el-button @click="handleClose">取消</el-button>
            <el-button type="primary" @click="handleConfirm">确定</el-button>
        </template>
    </el-dialog>
</template> 
<script setup>
import { ref, watch, getCurrentInstance, nextTick } from 'vue'
import { getModel } from '@/api/retrieve'
// Props
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    config: {
        type: Object,
        default: () => ({
            searchType: 'fulltext',
            maxSegmentCount: 10,
            rerankModel: 'qwen3-rerank',
            ragScore: 0.1,
            ragTopK: 10,
            rerankScore: 0.1,
            rerankTopK: 5
        })
    }
})

const { proxy } = getCurrentInstance()
const modelOptions = ref([])
const dialogVisible = ref(false)
const emit = defineEmits(['update:visible', 'confirm'])

const marks = {
  0: '0',
  20: '20'
}

const form = ref({
    searchType: 'fulltext',
    maxSegmentCount: 10,
    rerankModel: 'qwen3-rerank',
    ragScore: 0.1,
    ragTopK: 10,
    rerankScore: 0.1,
    rerankTopK: 10
})

const getModelList = async () => {
    let params = {
        pageNum: 1,
        pageSize: 1000,
    }
    const res = await getModel(params)
    modelOptions.value = res.data
}
// 监听 visible prop 变化
watch(() => props.visible, (newVal) => {
    dialogVisible.value = newVal
    if (newVal) {
        // 弹窗打开时初始化表单数据
        form.value = {
            ...form.value,
            ...props.config
        }
    }
    getModelList()
}, { immediate: true })

// 监听对话框显示状态
watch(dialogVisible, (newVal) => {
    emit('update:visible', newVal)
})

// 关闭对话框
const handleClose = () => {
    dialogVisible.value = false
}

// 确认配置
const handleConfirm = () => {
    emit('confirm', { ...form.value })
    handleClose()
}
</script>
<style scoped lang="scss">
.recall-title {
    font-size: 16px;
    margin-bottom: 20px;
    color: #333;
    display: flex;
    align-items: center;
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

:deep(.el-slider__button) {
    width: 15px;
    height: 15px;
}

.selectWidth {
    flex: 1;
}
:deep(.el-slider__runway) {
    height: 4px !important;
    .el-slider__bar {
        height: 4px;
    }
    .el-slider__button-wrapper {
        top: -17px;
        .el-slider__button {
            height: 14px;
            width: 14px;
        }
    }
}
</style>
<style lang="scss">
.component-dialog {
    border-radius: 8px !important;
    .dialog-header {
        background-image: url('@/assets/images/modelSquare_header.png');
        background-size: cover;
        height: 40px;
        line-height: 40px;
        padding-left: 20px;
        font-size: 16px;
        font-weight: 700;
        border-radius: 8px 8px 0 0;
    }
}

</style>