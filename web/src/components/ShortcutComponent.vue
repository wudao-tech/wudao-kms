<template> 
    <el-dialog
        v-model="dialogVisible"
        :width="500"
        :before-close="handleClose"
        destroy-on-close
        class="component-dialog"
    >
        <template #header>
            <div class="dialog-header2">
                <span style="font-size: 16px;">快捷指令</span>
            </div>
        </template>
        <div>
            <el-form-item label="显示名称:" style="flex: 1;">
                <el-input v-model="form.title" placeholder="请输入名称" :maxlength="50" show-word-limit >
                    <template #prefix>
                        <el-dropdown placement="bottom-start">
                            <el-icon style="cursor: pointer;" v-if="form.icon === ''"><Edit /></el-icon>
                            <svg-icon  style="cursor: pointer;" v-else :icon-class="form.icon" />
                            <template #dropdown>
                             <div style="width: 350px; padding: 10px; display: flex; gap: 10px;">
                                <div class="icon-item" v-for="(item, index) in iconList" style="" @click="handleIconClick(item)" :key="index">
                                    <svg-icon color="var(--el-text-color-regular)" :key="item" :icon-class="item" />
                                </div>
                             </div>
                            </template>
                        </el-dropdown>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item label="指令内容:" style="flex: 1;">
                <el-input v-model="form.desc" show-word-limit type="textarea" :rows="3" placeholder="请输入描述" />
            </el-form-item>
        </div>
        <template #footer>
            <el-button @click="handleClose">取消</el-button>
            <el-button type="primary" @click="handleConfirm">确定</el-button>
        </template>
    </el-dialog>
</template> 
<script setup>

// Props
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    shortcutObj: {
        type: Object,
        default: () => ({})
    }
})

const dialogVisible = ref(false)
const emit = defineEmits(['update:visible', 'confirm'])
const form = ref({
    icon: '',
    title: '',
    desc: ''
})

// 监听 visible 
watch([() => props.visible, () => props.shortcutObj], ([newVisible, newShortcutObj], [oldVisible, oldShortcutObj]) => {
    dialogVisible.value = newVisible
    if (newShortcutObj && Object.keys(newShortcutObj).length > 0) {
        form.value = {
            icon: newShortcutObj.icon || '',
            title: newShortcutObj.title || '',
            desc: newShortcutObj.desc || ''
        }
    } else {
        // 重置为默认值
        form.value = {
            icon: '',
            title: '',
            desc: ''
        }
    }
}, { immediate: true, deep: true })

// 监听对话框显示状态
watch(dialogVisible, (newVal) => {
    emit('update:visible', newVal)
})

const iconList = ref(['rate', 'notice', 'fire', 'heart-off', 'setemail', 'select', 'question'])

const handleIconClick = (item) => {
    form.value.icon = item
}

// 关闭对话框
const handleClose = () => {
    dialogVisible.value = false
}


// 确认添加
const handleConfirm = () => {
    emit('confirm', {...form.value})
    handleClose()
}

</script>
<style scoped lang="scss">
 .dialog-header2 {
    background-image: url('@/assets/images/modelSquare_header.png');
    height: 58px;
    border-radius: 8px 8px 0 0;
    padding-left: 30px;
    line-height: 58px;
 }
 .icon-item {
    cursor: pointer;
    width: 10%;
    margin: 0 10px 10px 0;
    padding: 5px;
    display: flex;
    flex-direction: column;
    justify-items: center;
    align-items: center;
    border: 1px solid #ccc;
    &:hover {
      border-color: var(--el-color-primary);
    //   color: var(--el-color-primary);
      transition: all 0.2s;
      transform: scaleX(1.1);
    }
  }

</style>

<style lang="scss">

</style>