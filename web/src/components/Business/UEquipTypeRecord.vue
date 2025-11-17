<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="720" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>版本管理</template>
    <el-table :data="data.record">
      <el-table-column label="版本" prop="profile.version"></el-table-column>
      <el-table-column label="描述" prop="profile.description"></el-table-column>
      <el-table-column label="操作">
        <template #default="{ row}">
          <el-button type="primary" @click="state.reverse(row)">恢复</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>
<script setup>
import { inject } from 'vue'
import { cloneDeep } from 'lodash-es';

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object || Array,
    required: true,
  },
});
const _l = inject('_l')
const emits = defineEmits(['update:modelValue', 'ok']);

const state = reactive({
  loading: false,
  reverse(row) {
    emits('ok', cloneDeep(row))
    emits('update:modelValue', false)
  },
  cancel() {
    emits('update:modelValue', false)
  }
})
</script>
<style lang='scss' scoped>

</style>
