<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="720" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>版本发布</template>

    <el-form ref="formRef" :model="release" :rules="rules">
      <el-form-item label="描述" prop="description">
        <el-input type="textarea" v-model="release.description" maxlength="50" show-word-limit></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="state.cancel">取 消</el-button>
        <el-button type="primary" @click="state.ok" :loading="state.loading">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { inject, getCurrentInstance } from 'vue'
import {ApiEquipTypeRelease} from '@/api/Business/'

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object || Array,
    required: true,
  },
});
const {proxy} = getCurrentInstance()
const _l = inject('_l')
const emits = defineEmits(['update:modelValue', 'ok']);

const release = reactive({
  productKey: '',
  version: '',
  description: ''
})
const formRef = ref()
const state = reactive({
  loading: false,
  async ok() {
    const valid = await formRef.value.validate()
    if (valid) {
      try {
        state.loading = true
        ApiEquipTypeRelease(release).then(res => {
          if (res.code === 'ok') {
            proxy.$modal.msgSuccess('成功发布')
            emits('ok')
            state.cancel()
          }
        })
      } finally {
        state.loading = false
      }
    }
   },
  cancel() {
    emits('update:modelValue', false)
  }
})

watch(() => props.modelValue, (val) => {
  if (val) {
    Object.assign(release, {
      productKey: props.data.productKey,
      version: props.data.version,
      description: ''
    })
  }
})

const rules = reactive({
  description: [{required: true, message: '请输入', trigger: 'blur'}]
})

</script>
<style lang='scss' scoped>

</style>
