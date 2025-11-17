<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="910" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>
      <div class="u-title">物模型TSL</div>
    </template>

    <div class="tsl-desc">
      在工业物联网平台中，物模型是对工业设备、传感器、生产线等物理实体的数字化描述，是实现设备互联互通和智能化管理的核心技术。它通过属性、服务和事件三个维度来全面定义设备的功能。属性用于描述设备的运行状态和参数，例如温度、压力、开关状态等。这些属性可以是可读写的，也可以是只读的。服务则定义了设备可以执行的操作，如启动、停止、调整参数等，通常通过输入和输出参数来实现更复杂的业务逻辑。事件是设备主动上报的信息，如故障报警、状态变化等，可用于实时监控和故障处理。
    </div>

    <el-row>
      <el-tabs v-model="state.item" style="flex: 1">
        <el-tab-pane v-for="t in PROP_TAB_TYPE" :label="t[1].label" :name="t[0]" :key="t[0]"></el-tab-pane>
      </el-tabs>
    </el-row>

    <el-scrollbar class="code-wrap">
      <!-- <UCode v-model="state.jsonData" :readonly="true" bgColor="#f6f6f6"></UCode> -->
      <json-viewer :value="state.jsonData" :expand-depth="3"></json-viewer>
    </el-scrollbar>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="state.cancel">取 消</el-button>
        <el-button type="primary" @click="state.copy" class="btn-copy">复 制</el-button>
        
        <el-button type="primary" @click="state.export">导 出</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { inject } from 'vue'
import { PROP_TAB_TYPE } from '@/config/'
// import { UCode } from '@/components/Business/'
import JsonViewer from 'vue3-json-viewer'
import useCopyText from '@/hooks/useCopy.js'
import { exportJSON } from '@/plugins/download'

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object || Array,
    required: true,
  },
})
const _l = inject('_l')
const emits = defineEmits(['update:modelValue', 'ok'])
const { copyToClipboard } = useCopyText()

const state = reactive({
  loading: false,
  item: 'properties',
  jsonData: computed(() => {
    // return JSON.stringify(props.data.model[state.item], null, 2)
    return props.data.model[state.item]
  }),
  ok() { },
  cancel() {
    emits('update:modelValue', false)
  },
  copy() {
    copyToClipboard(JSON.stringify(props.data.model, null, 2))
  },
  export() {
    exportJSON(props.data.model, props.data.name)
  }
})
</script>
<style lang='scss' scoped>
.tsl-desc {
  padding: 10px 12px;
  background: #f5f5f5;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}
.code-wrap {
  position: relative;
  height: 400px;
  overflow: auto;
  background: #f6f6f6;

  .jv-container.jv-light {
    background: #f6f6f6;
  }
}
</style>
