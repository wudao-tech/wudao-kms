<template>
    <div class="card-item" @click="handleCardClick">
        <div class="card-header">
            <div class="card-icon">
                <img :src="icon" alt="icon" style="width: 100%; border-radius: 4px; height: 100%;"/>
            </div>
            <div class="card-title">
                <div class="title-row">
                    <span class="text-ellipsis title-name">{{ name }}</span>
                    <div class="title-actions">
                        <span v-if="status !== undefined" class="status" :style="{ color: status === 1 ? '#6B05A8' : '#838383'}">{{ status === 1 ? '已发布' : '草稿' }}</span>
                        <slot name="opt"></slot>
                    </div>
                </div>
                <div v-if="tag || createByName" class="title-meta">
                       <span v-if="createByName" class="creator">@{{ createByName }} <svg-icon icon-class="vip" v-if="createdBy === 1"></svg-icon></span>
                       <el-tooltip v-if="tag && tag.length > 6" :content="tag" placement="top">
                        <span class="type type-ellipsis">{{ displayTag }}</span>
                       </el-tooltip>
                       <span v-else-if="tag" class="type">{{ tag }}</span>
                </div>
               
            </div>
        </div>
        <div style="height: 50px; border-bottom: 1px solid #EEEEEE;">
            <div class="card-content text-ellipsis-line-2">{{ description }}</div>
        </div>
        <div class="card-footer" @click.stop>
            <slot name="footer"></slot>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    name: {
        type: String,
        required: true
    },
    description: {
        type: String,
        required: false
    },
    tag: {
        type: String,
        required: false
    },
    icon: {
        type: String,
        required: true
    },
    status: {
        type: Number,
        required: false
    },
    createByName: {
        type: String,
        required: false
    },
    createdBy: {
        type: Number,
        required: false
    }
})

const emit = defineEmits(['click'])

// 计算显示标签文本：超过6个字时截断
const displayTag = computed(() => {
    if (!props.tag) return ''
    if (props.tag.length > 6) {
        return props.tag.substring(0, 6) + '...'
    }
    return props.tag
})

const handleCardClick = () => {
    emit('click')
}
</script>

<style lang="scss" scoped>
.card-item {
    width: calc(25% - 15px);
    background: #FFFFFF;
    border-radius: 8px;
    padding: 20px 20px 10px 20px;
    box-sizing: border-box;
    cursor: pointer;
    position: relative;
    height: 164px;
    .card-header {
        display: flex;
        gap: 15px;
        margin-bottom: 15px;
        height: 48px;
        .card-icon {
            height: 44px;
            width: 44px;
            min-width: 44px;
            border-radius: 4px;
            display: flex;
            align-items: center;
        }

        .card-title {
            flex: 1;
            min-width: 0;
            display: flex;
            flex-direction: column;
            
            .title-row {
                min-width: 0;
                flex: 1;
                display: flex;
                gap: 4px;
                align-items: center;
                
                .title-name {
                    flex: 1;
                    min-width: 0;
                    font-size: 14px;
                    font-weight: 700;
                }
                
                .title-actions {
                    flex-shrink: 0;
                    display: flex;
                    align-items: center;
                    gap: 2px;
                }
            }
            
            .title-meta { 
                display: flex;
                align-items: center;
                gap: 6px;
                margin-top: 4px;
                .creator {
                    font-size: 12px;
                    color: #999999;
                }
            }
            
            .type {
                background: #F2F4FD;
                color: #722ED1;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 12px;
                display: inline-block;
                
                &.type-ellipsis {
                    max-width: 120px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
            }
        }
    }

    .card-content {
        color: #B8B8B8;
        font-size: 14px;
    }

    .card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 7px;
        padding: 0 5px;
        color: #999;
        font-size: 14px;
        cursor: auto;
    }
}

.dp-s {
    display: flex;
    align-items: center;
}
.status {
    background: #F2F4FD;
    margin-top: 0px;
    margin-right: 5px; 
    font-weight: bold; 
    font-size: 12px;
    padding: 4px;
    border-radius: 4px;
    font-size: 12px;
    display: inline-block;
}
.text-ellipsis-line-2 {
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>