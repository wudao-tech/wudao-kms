<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="960" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>{{ data.type === 'add' ? '新增' : '编辑' }}功能</template>

    <el-form ref="formRef" :label-width="100" :model="itemData" :rules="rules">
      <el-form-item label="功能类型">
        <el-radio-group v-model="state.itemType" @change="state.switchItem" :disabled="['edit', 'copy'].includes(data.type)">
          <el-radio-button value="properties">属性</el-radio-button>
          <el-radio-button value="events">事件</el-radio-button>
          <el-radio-button value="services">服务</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model.trim="itemData.name" maxlength="30" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="标识符" prop="identifier">
        <el-input v-model.trim="itemData.identifier" maxlength="30" show-word-limit :disabled="data.type === 'edit'">
          <template #append>
            <el-button @click="state.createId" :disabled="data.type === 'edit'">生成</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="描述">
        <el-input type="textarea" v-model.trim="itemData.desc" maxlength="200" show-word-limit></el-input>
      </el-form-item>

      <el-form-item :label="ITEM_TYPE_OBJ.label" prop="type">
        <el-select v-model="itemData[state.propType[state.itemType]]">
          <el-option v-for="t in ITEM_TYPE_OBJ.list" :key="t[0]" :value="t[0]" :label="t[1]"></el-option>
        </el-select>
      </el-form-item>

      <template v-if="state.itemType === 'properties' && itemData.dataType">
        <el-form-item label="读写类型">
          <el-radio-group v-model="itemData.accessMode" prop="accessMode">
            <el-radio v-for="t in PROP_WRITEABLE" :key="t[0]" :value="t[0]">{{ t[1] }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数据类型">
          <el-select v-model="itemData.dataType.type" prop="dataType">
            <el-option v-for="t in PROP_DATA_TYPE" :key="t[0]" :value="t[0]" :label="t[1].label"></el-option>
          </el-select>
        </el-form-item>
        <template v-if="itemData.dataType?.type">
          <el-form-item label="最大值" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('max')">
            <el-input type="number" v-model="itemData.dataType.specs.max"></el-input>
          </el-form-item>
          <el-form-item label="最小值" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('min')">
            <el-input type="number" v-model="itemData.dataType.specs.min"></el-input>
          </el-form-item>
          <el-form-item label="单位" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('unit')">
            <el-input v-model="itemData.dataType.specs.unit"></el-input>
          </el-form-item>
          <el-form-item label="保留小数" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('step')">
            <el-input type="number" v-model="itemData.dataType.specs.step"></el-input>
          </el-form-item>
          <el-form-item label="数组大小" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('arraySize')">
            <el-input type="number" v-model="itemData.dataType.specs.arraySize"></el-input>
          </el-form-item>
          <el-form-item label="数组类型" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('itemType')">
            <el-select v-model="itemData.dataType.specs.itemType">
              <el-option v-for="t in PROP_DATA_ARRAY_TYPE" :key="t" :value="t" :label="PROP_DATA_TYPE.get(t).label"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="长度" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('length')">
            <el-input type="number" v-model="itemData.dataType.specs.length"></el-input>
          </el-form-item>
          <el-form-item label="文件大小" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('fileSize')">
            <el-input type="number" v-model="itemData.dataType.specs.fileSize"></el-input>
          </el-form-item>
          <el-form-item label="文件类型" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('fileType')">
            <el-input v-model="itemData.dataType.specs.fileType"></el-input>
          </el-form-item>
          <el-form-item label="结构体大小" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('structSize')">
            <el-input type="number" v-model="itemData.dataType.specs.structSize"></el-input>
          </el-form-item>
          <el-form-item label="0的值" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('bool0')" prop="bool0">
            <el-input v-model.trim="itemData.dataType.specs.bool0"></el-input>
          </el-form-item>
          <el-form-item label="1的值" v-if="PROP_DATA_TYPE.get(itemData.dataType.type).specs.includes('bool1')" prop="bool0">
            <el-input v-model.trim="itemData.dataType.specs.bool1"></el-input>
          </el-form-item>
        </template>

      </template>
      <template v-if="['services'].includes(state.itemType)">
        <el-form-item label="调用类型" prop="callType">
          <el-select v-model="itemData.callType">
            <el-option v-for="t in PROP_CALL_TYPE" :key="t[0]" :value="t[0]" :label="t[1]"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="输入参数">
          <el-table :data="itemData.inputData">
            <el-table-column label="参数名称" prop="name"></el-table-column>
            <el-table-column label="标识符" prop="identifier"></el-table-column>
            <el-table-column label="描述" prop="desc"></el-table-column>
            <el-table-column label="类型">
              <template #default="{ row}">
                {{ PROP_DATA_TYPE.get(row.dataType.type).label }}
              </template>
            </el-table-column>
            <el-table-column>
              <template #header>
                <el-select @change="(val) => state.checkProp(val, 'inputData')">
                  <el-option v-for="t in propList" :key="t.identifier" :value="t.identifier" :disabled="state.outputChecked.includes(t.identifier)">{{ t.name }}</el-option>
                </el-select>
              </template>
              <template #default="{ row}">
                <el-button type="danger" @click="state.checkDel(row.identifier, 'inputData')">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </template>
      <template v-if="['events', 'services'].includes(state.itemType)">
        <el-form-item label="输出参数">
          <el-table :data="itemData.outputData">
            <el-table-column label="参数名称" prop="name"></el-table-column>
            <el-table-column label="标识符" prop="identifier"></el-table-column>
            <el-table-column label="描述" prop="desc"></el-table-column>
            <el-table-column label="类型">
              <template #default="{ row}">
                {{ PROP_DATA_TYPE.get(row.dataType.type).label }}
              </template>
            </el-table-column>
            <el-table-column>
              <template #header>
                <el-select @change="(val) => state.checkProp(val, 'outputData')">
                  <el-option v-for="t in propList" :key="t.identifier" :value="t.identifier" :disabled="state.outputChecked.includes(t.identifier)">{{ t.name }}</el-option>
                </el-select>
              </template>
              <template #default="{ row}">
                <el-button type="danger" @click="state.checkDel(row.identifier, 'outputData')">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </template>
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
import { nanoid } from '@/utils'
import { PROP_TYPE, PROP_WRITEABLE, EVENT_TYPE, SERVICE_TYPE, PROP_DATA_TYPE, PROP_DATA_ARRAY_TYPE, PROP_CALL_TYPE } from '@/config/'
import { ApiEquipType } from '@/api/Business'
import { cloneDeep } from 'lodash-es'
import moment from 'moment'

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object || Array,
    required: true,
  },
})
const _l = inject('_l')
const emits = defineEmits(['update:modelValue', 'ok'])
const { proxy } = getCurrentInstance()

const DEFAULT_ITEM = {
  properties: JSON.stringify({
    identifier: '',
    name: '',
    desc: '',
    type: '',
    accessMode: 'RW',
    dataType: {
      type: '',
      specs: {
        "max": "",
        "min": "",
        "unit": "",
        "step": "",
        "arraySize": "",
        "itemType": "",
        "length": "",
        "fileSize": "",
        "fileType": "",
        "structSize": "",
        "bool0": "",
        "bool1": ""
      }
    }
  }),
  events: JSON.stringify({
    "identifier": "",
    "name": "",
    "desc": "",
    "eventType": "",
    "outputData": []
  }),
  services: JSON.stringify({
    "identifier": "",
    "name": "",
    "desc": "",
    "serviceType": "",
    "callType": "",
    "inputData": [],
    "outputData": []
  })
}

const itemData = ref({})
const propList = ref([])
const formRef = ref()
const state = reactive({
  loading: false,
  propType: {
    properties: 'type',
    events: 'eventType',
    services: 'serviceType'
  },
  itemType: '',
  createId() {
    itemData.value.identifier = nanoid(4)
  },
  async ok() {
    /*
      1、rule判断
      2、全量数据合并
    */

    try {
      state.loading = true
      const valid = await formRef.value.validate()
      if (valid) {
        const r = cloneDeep(props.data.rootData)
        r[state.itemType] = r[state.itemType] || []
        itemData.value.updatedAt = moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
        if (props.data.type === 'edit') {
          const fi = r[state.itemType].findIndex(t => t.identifier === itemData.value.identifier)
          r[state.itemType].splice(fi, 1, itemData.value)
        } else {
          r[state.itemType].unshift(itemData.value)
        }
        ApiEquipType({
          method: 'put',
          data: {
            id: props.data.typeId,
            thingModelDraft: r,
            updatedAt: props.data.updatedAt || undefined
          }
        }).then(res => {
          if (res.code === 'ok') {
            if (res.data === 0) {
              proxy.$modal.notifyWarning('数据已被其他管理员更新过，请检查！')
            }
            emits('ok')
            state.cancel()
          }
        })
      }
    } finally {
      state.loading = false
    }
  },
  cancel() {
    emits('update:modelValue', false)
  },
  switchItem() {
    itemData.value = JSON.parse(DEFAULT_ITEM[state.itemType])
  },
  outputChecked: computed(() => {
    return (itemData.value.outputData || []).map(t => t.identifier)
  }),
  inputData: computed(() => {
    return (itemData.value.inputData || []).map(t => t.identifier)
  }),

  checkProp(val, key) {
    const current = propList.value.find(t => t.identifier === val)
    itemData.value[key].push(current)
  },
  checkDel(id, key) {
    const fi = itemData.value[key].findIndex(t => t.identifier === id)
    itemData.value[key].splice(fi, 1)
  }
})

const ITEM_TYPE_OBJ = computed(() => {
  const { itemType: t } = state
  return {
    label: t === 'properties' ? '属性类型' : t === 'events' ? '事件类型' : '服务类型',
    list: t === 'properties' ? PROP_TYPE : t === 'events' ? EVENT_TYPE : SERVICE_TYPE,
  }
})

watch(() => props.modelValue, (val) => {
  if (val) {
    const { item, rootData, itemData: iData, type } = props.data
    state.itemType = item
    propList.value = cloneDeep((rootData.properties || []).filter(t => ['LABEL', 'COLLECT'].includes(t.type)))
    itemData.value = {
      ...JSON.parse(DEFAULT_ITEM[item]),
      ...iData,
      identifier: type === 'edit' ? iData.identifier : ''
    }
  }
})

const rules = reactive({
  name: [{ required: true, message: '请输入', trigger: 'blur' }],
  identifier: [
    { required: true, message: '请输入', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (!props.data.rootData[state.itemType]) return callback()
        const isRepeat = props.data.rootData[state.itemType].findIndex(t => t.identifier === value) !== -1
        if (isRepeat && props.data.type !== 'edit') {
          callback(new Error('标识符重复'))
        } else {
          callback()
        }
      }
    }
  ],
  type: [
    {
      validator: (rule, value, callback) => {

        if (!itemData.value[state.propType[state.itemType]]) {
          callback(new Error('请输入'))
        } else {
          callback()
        }
      }
    }
  ],
  accessMode: [{ required: true, message: '请选择', trigger: 'blur' }],
  dataType: [{ required: true, message: '请选择', trigger: 'blur' }],
  itemType: [{ required: true, message: '请选择', trigger: 'blur' }],
  callType: [{ required: true, message: '请选择', trigger: 'blur' }],
  bool0: [
    {
      validator: (rule, value, callback) => {
        const { bool0, bool1 } = itemData.value.dataType.specs
        if (!bool0 && !bool1) {
          callback(new Error('bool0/bool1至少输入一项'))
        } else {
          callback()
        }
      }
    }
  ]
})
</script>
<style lang='scss' scoped>
</style>
