<template>
    <div class="pdf-container">
        <div class="pdf-controls">
            <el-button-group>
                <el-button :disabled="currentPage <= 1" @click="currentPage--" icon="ArrowLeft">上一页</el-button>
                <el-button disabled>{{ currentPage }} / {{ totalPages }}</el-button>
                <el-button :disabled="currentPage >= totalPages" @click="currentPage++" icon="ArrowRight">下一页</el-button>
            </el-button-group>
            
            <div class="zoom-controls">
                <el-button @click="zoomOut" icon="ZoomOut" :disabled="scale <= 0.5">缩小</el-button>
                <span class="zoom-level">{{ Math.round(scale * 100) }}%</span>
                <el-button @click="zoomIn" icon="ZoomIn" :disabled="scale >= 3">放大</el-button>
                <el-button @click="resetZoom" icon="Refresh">重置</el-button>
            </div>
        </div>
        
        <div class="pdf-content">
            <vue-pdf-embed 
                :source="pdfSource"
                :page="currentPage"
                annotation-layer
                text-layer
                @loaded="handlePdfLoaded"
                @loading-failed="handlePdfError"
            />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import VuePdfEmbed from 'vue-pdf-embed'
import privacy from '@/assets/word/privacy.pdf'
import user from '@/assets/word/user.pdf'
import { useRoute } from 'vue-router'
const route = useRoute()

// PDF相关状态
const pdfSource = ref(route.query.id === '1' ? user : privacy) // 当前显示的PDF源
const currentPage = ref(1) // 当前页码
const totalPages = ref(0) // 总页数
const scale = ref(1) // 缩放比例

// PDF加载成功处理
const handlePdfLoaded = (pdf) => {
    if (pdf && pdf.numPages) {
        totalPages.value = pdf.numPages
    }
}

// PDF加载失败处理
const handlePdfError = (error) => {
    ElMessage.error('PDF文件加载失败')
}

// 切换PDF文件的方法
const switchToPrivacy = () => {
    pdfSource.value = privacy
    currentPage.value = 1
    totalPages.value = 0
}

const switchToUser = () => {
    pdfSource.value = user
    currentPage.value = 1
    totalPages.value = 0
}

// 缩放相关方法
const zoomIn = () => {
    if (scale.value < 3) {
        scale.value = Math.min(3, scale.value + 0.25)
    }
}

const zoomOut = () => {
    if (scale.value > 0.5) {
        scale.value = Math.max(0.5, scale.value - 0.25)
    }
}

const resetZoom = () => {
    scale.value = 1
}
</script>

<style scoped>
.pdf-container {
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    background: #f5f5f5;
}

.pdf-controls {
    padding: 16px;
    background: white;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.zoom-controls {
    display: flex;
    align-items: center;
    gap: 8px;
}

.zoom-level {
    min-width: 60px;
    text-align: center;
    font-weight: 500;
    color: #606266;
}

.pdf-content {
    flex: 1;
    overflow: auto;
    padding: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 0; /* 确保flex子元素能够正确收缩 */
}

/* PDF组件样式优化 */
:deep(.vue-pdf-embed) {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    height: 100%;
}

:deep(.pdf-page) {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-radius: 4px;
    margin-bottom: 20px;
    max-width: 100%;
    max-height: calc(100vh - 120px); /* 减去控制栏和padding的高度 */
    width: auto;
    height: auto;
    object-fit: contain;
}

/* 确保PDF页面能够适应屏幕 */
:deep(.vue-pdf-embed canvas) {
    max-width: 100%;
    max-height: calc(100vh - 120px);
    width: auto !important;
    height: auto !important;
    transform: scale(v-bind(scale));
    transform-origin: center center;
    transition: transform 0.3s ease;
}
</style>