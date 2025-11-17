<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="720" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>
      <div class="u-title">{{editType}}设备类型</div>
    </template>
    <el-form ref="typeForm" :label-width="80" :model="equipType" :rules="rules">
      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="类型名称" prop="name">
            <el-input v-model.trim="equipType.name"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="编码" prop="code">
            <el-input v-model.trim="equipType.code"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="接入模式" prop="accessMode">
            <el-select v-model="equipType.accessMode">
              <el-option v-for="t in ACCESS_MODE" :key="t[0]" :value="t[0]" :label="t[1]"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="接入类型" prop="accessType">
            <el-select v-model="equipType.accessType">
              <el-option v-for="t in ACCESS_TYPE" :key="t[0]" :value="t[0]" :label="t[1]"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="通信方式" prop="communicationModel">
            <el-select v-model="equipType.communicationModel">
              <el-option v-for="t in COMMUNICATION_MODEL" :key="t" :value="t">{{ t }}</el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="接入协议" prop="communicationProtocol">
            <el-select v-model="equipType.communicationProtocol">
              <el-option v-for="t in COMMUNICATION_PROTOCOL" :key="t" :value="t">{{ t }}</el-option>
            </el-select>
          </el-form-item>
        </el-col>

      </el-row>
      <el-form-item label="类型图片">
        <p-upload-file v-model="equipType.imgUrl"></p-upload-file>
      </el-form-item>
      <el-form-item label="描述">
        <el-input type="textarea" v-model="equipType.descr"></el-input>
      </el-form-item>
      <el-form-item label="标签">
        <el-row class="type-label-item" v-for="(t, i) in equipType.labels" :key="i">
          <el-input v-model.trim="equipType.labels[i]"></el-input>
          <el-button v-if="equipType.labels.length > 1" text><i class="iconfont se-dig-icon-a-Deleteshanchu" @click="state.delLabel(i)"></i> </el-button>
        </el-row>
      </el-form-item>
      <el-form-item>
        <el-button text style="color: #447DFB;" @click="state.addLabel">
          + <span style="margin-left: 2px;">添加标签</span>
        </el-button>
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
import { ACCESS_MODE, ACCESS_TYPE, COMMUNICATION_MODEL, COMMUNICATION_PROTOCOL } from '@/config/'
import { ApiEquipType } from '@/api/Business/'

const _l = inject('_l')

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object
  }
})
const emits = defineEmits(['update:modelValue', 'ok'])
const { proxy } = getCurrentInstance()

const editType = computed(() => {
  return props.data.id ? '编辑' : '新增'
})

const equipType = reactive({
  "code": undefined,
  "name": "",
  "descr": "",
  "imgUrl": "",
  "labels": [''],
  "accessMode": undefined,
  "accessType": undefined,
  "communicationModel": "",
  "communicationProtocol": ""
})

const typeForm = ref()
const state = reactive({
  loading: false,
  async ok() {
    const valid = await typeForm.value.validate()
    if (valid) {
      try {
        const params = { ...equipType }
        params.labels = params.labels.filter(t => !!t)
        state.loading = true
        const { code } = await ApiEquipType({
          method: props.data.id ? 'put' : 'post',
          data: params
        })
        if (code === 'ok') {
          proxy.$modal.msgSuccess(`${editType.value}成功`)
          emits('ok')
          emits('update:modelValue')
        }
      } finally {
        state.loading = false
      }
    }
  },
  cancel() {
    emits('update:modelValue', false)
  },
  addLabel() {
    equipType.labels.push('')
  },
  delLabel(index) {
    equipType.labels.splice(index, 1)
  }
})

watch(() => props.modelValue, (val) => {
  if (val) {
    Object.assign(equipType, props.data)
    !equipType.labels.length && state.addLabel()
  }
})

const rules = reactive({
  name: [{ required: true, message: '请输入', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入', trigger: 'blur' },
    {
      pattern: /[0-9A-z]+/
    }
  ],
  accessMode: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],
  accessType: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],
  communicationModel: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],
  communicationProtocol: [{ required: true, message: '请选择', trigger: ['blur', 'change'] }],

})

</script>
<style lang='scss' scoped>
.type-label-item {
  width: 100%;
  flex-wrap: nowrap;
  & + .type-label-item {
    margin-top: 12px;
  }
}
</style>
