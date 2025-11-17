<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="720" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>
      <div class="u-title">导入</div>
    </template>
    <el-upload ref="uploadRef" class="avatar-uploader" action="/file/upload" :show-file-list="false" :before-upload="beforeAvatarUpload" :http-request="fileUploadFn">

      <el-scrollbar class="code-wrap" v-if="state.jsonData" @click.stop>
        <json-viewer :value="state.jsonData" :expand-depth="6"></json-viewer>
      </el-scrollbar>
      <el-row v-else class="upload-init-box">
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text" style="margin-top: 12px;">
          拖拽上传，或 <em style="color: #3D6FEE;">点击上传</em>
        </div>
      </el-row>
      <template #tip>
        <div class="el-upload__tip" style="text-align: right;color: #979797;">
          只支持JSON文件
        </div>
      </template>
    </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="state.cancel">取 消</el-button>
        <el-button type="primary" :disabled="!state.jsonData" @click="state.ok" :loading="state.loading">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { inject, getCurrentInstance } from 'vue'
import JsonViewer from 'vue3-json-viewer'
import { ApiEquipType } from '@/api/Business'
import { nanoid } from '@/utils'
import { cloneDeep } from 'lodash-es'

/*
  data: {
    draft: 草稿内容
    typeId: 类型ID
  }
*/
const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object || Array,
    required: true,
  },
})
const _l = inject('_l')
const { proxy } = getCurrentInstance()
const emits = defineEmits(['update:modelValue', 'ok'])

const state = reactive({
  loading: false,
  jsonData: '',
  ok() {
    try {
      state.loading = true
      const p = ['properties', 'events', 'services']
      const { typeId, updatedAt } = props.data
      let draft = cloneDeep(props.data.draft)
      p.forEach(t => {
        draft[t] = draft[t] || []
        const box = new Set(draft[t].map(k => k.identifier))
        const arrs = (state.jsonData[t] || []).map(k => {
          const identifier = box.has(k.identifier) ? createUnRepeatNanoid(box) : k.identifier

          box.add(identifier)

          if (k.inputData && k.inputData) {
            k.inputData.forEach(m => {
              const p = draft.properties.find(n => n.oId === m.identifier)
              if (p) {
                m.identifier = p.identifier
              } else {
                draft.properties.push({
                  ...m,
                  oId: m.identifier
                })
              }
            })
          }

          if (k.outputData && k.outputData) {
            k.outputData.forEach(m => {
              const p = draft.properties.find(n => n.oId === m.identifier)
              // 输出参数在参数中是否存在
              if (!!p) {
                // 已存在
                m.identifier = p.identifier
              } else {
                draft.properties.push({
                  ...m,
                  oId: m.identifier
                })
              }
            })
          }

          return {
            ...k,
            oId: k.identifier,
            identifier
          }
        })
        draft[t] = draft[t].concat(arrs || [])
      })
      ApiEquipType({
        method: 'put',
        data: {
          id: typeId,
          thingModelDraft: draft,
          updatedAt
        }
      }).then(res => {
        if (res.code === 'ok') {
          if (res.data === 0) {
            return proxy.$modal.notifyWarning('数据已被其他管理员更新过，请检查！')
          }
          emits('ok')
          state.cancel()
        }
      })
    } finally {
      state.loading = true
    }
  },
  cancel() {
    emits('update:modelValue', false)
  }
})


const createUnRepeatNanoid = (box) => {
  const id = nanoid(4)
  if (box.has(id)) {
    return createUnRepeatNanoid(box)
  }
  return id
}

const beforeAvatarUpload = (rawFile) => {
  console.log('rawFile', rawFile)
  if (rawFile.type !== 'application/json') {
    return proxy.$modal.msgWarning('只支持JSON格式文件')
  }
  const fileReader = new FileReader()
  fileReader.onload = function (e) {
    state.jsonData = JSON.parse(e.target.result)
  }
  fileReader.readAsText(rawFile)
}

const fileUploadFn = () => {
  return false
}

watch(() => props.modelValue, (val) => {
  console.log('props.data', props.data)
  if (val) {
    state.jsonData = undefined
    state.loading = false
  }
})

</script>
<style lang='scss' scoped>
.avatar-uploader :deep(.el-upload) {
  width: 100%;
}
.upload-init-box {
  height: 200px;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  .el-icon--upload {
    font-size: 2em;
  }
}
.code-wrap {
  position: relative;
  width: 100%;
  height: 400px;
  overflow: auto;
  background: #f6f6f6;

  .jv-container.jv-light {
    background: #f6f6f6;
  }
}
</style>
