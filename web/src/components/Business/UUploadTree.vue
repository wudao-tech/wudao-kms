<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="50%" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>
      <el-row justify="space-between">
        <div class="u-title">导入工厂设备层级</div>
        <el-button type="primary">模板下载</el-button>
      </el-row>
    </template>
    <el-upload class="upload-demo" drag action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15">
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        拖拽上传，或 <em>点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          仅允许xlsx格式，且小于10M
        </div>
      </template>
    </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="state.ok">确 定</el-button>
        <el-button @click="state.cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { inject } from 'vue'
import { inviteLink, inviteLinkList } from '@/api/login'
import { add } from 'lodash'

const _l = inject('_l')

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object
  }
})
const emits = defineEmits(['update:modelValue'])

const state = reactive({
  async ok() {

  },
  cancel() {
    emits('update:modelValue', false)
  },

  del(index) {
    form1.email.splice(index, 1)
  },
  add() {
    form1.email.push('')
  }
})



</script>
<style lang='scss' scoped>
</style>
