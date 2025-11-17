<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="720" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>
      <div class="u-title">同步</div>
    </template>

    <el-form>
      <el-form-item label="设备类型">
        <el-cascader v-model="state.checkedList" :options="state.list" :props="state.props" collapse-tags collapse-tags-tooltip :max-collapse-tags="3" clearable style="width: 100%;" />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="state.cancel">取 消</el-button>
        <el-button type="primary" :disabled="!state.checkedList.length" @click="state.ok" :loading="state.loading">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { inject } from 'vue'
import { ApiEquipType } from '@/api/Business'
import { PROP_TAB_TYPE } from '@/config'
import { nanoid } from '@/utils'
import { cloneDeep } from 'lodash-es'

const props = defineProps({
  modelValue: Boolean,
  /*
    {draft, typeId, updatedAt}
  */
  data: {
    type: Object || Array,
    required: true,
  },
})
const _l = inject('_l')
const emits = defineEmits(['update:modelValue', 'ok'])
// const items = ['properties', 'events', 'services']
const state = reactive({
  props: { multiple: true },
  loading: false,
  init() {
    PROP_TAB_TYPE.forEach((value, key) => {
      state.idSet[key] = new Set()
      state.itemMap[key] = new Map();
      (props.data.draft[key] || []).forEach(t => {
        state.idSet[key].add(t.identifier)
      })
    })
  },
  idSet: {},
  itemMap: {},
  list: [],
  async getList() {
    ApiEquipType({
      method: 'get',
      params: {
        pageNum: 1,
        pageSize: 9999
      }
    }).then(res => {
      if (res.code === 'ok') {
        // 遍历所有类型
        state.list = res.data.reduce((acc, t) => {
          if (t.id !== props.data.typeId) {
            const children = []
            // 将每个类型中的属性-事件-服务组合成类型的children
            PROP_TAB_TYPE.forEach((value, key) => {
              const propChildren = [];
              /*
                对可选择类型下的属性等的id进行判断唯一性，重复的id进行重新生成
                当一个属性id存在重复的时候，要把当前类型下的事件、服务进行循环修改
              */
              ; (t.thingModel[key] || []).forEach(k => {

                if (state.idSet[key].has(k.identifier)) {
                  k.oldId = k.identifier
                  k.identifier = createUnRepeatNanoid(state.idSet[key])

                  if (key === 'properties') {
                    ['events', 'services'].forEach(o => {
                      const n = t.thingModel[o]
                      n.forEach(m => {
                        if (m.inputData && m.inputData.length) {
                          let p = m.inputData?.find(n => n.identifier === k.oldId)
                          p && (p.identifier = k.identifier)
                        }
                        if (m.outputData && m.outputData.length) {
                          let p = m.outputData?.find(n => n.identifier === k.oldId)
                          p && (p.identifier = k.identifier)
                        }
                      })

                    })
                  }
                }
                state.idSet[key].add(k.identifier)

                state.itemMap[key].set(k.identifier, {
                  ...k,
                  _ITEM_TYPE: key
                })
                propChildren.push({
                  label: k.name,
                  value: k.identifier + '_' + key
                })
              })
              children.push({
                label: value.label,
                value: t.id + '-' + key,
                children: propChildren
              })
            })

            acc.push({
              label: t.name,
              value: t.id,
              children
            })
          }
          return acc
        }, [])

        console.log('state.list', state.list)
        console.log('state.itemMap', state.itemMap)
      }
    })
  },
  ok() {
    try {
      state.loading = false
      const { typeId, draft: d, updatedAt } = props.data
      const checked = state.checkedList.map(t => t.slice(-1)[0].split('_'))

      const draft = cloneDeep(d)
      checked.forEach(t => {
        const [identifier, key] = t
        draft[key].push(state.itemMap[key].get(identifier))
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
      state.loading = false
    }
  },
  cancel() {
    emits('update:modelValue', false)
  },
  checkedList: []
})

const createUnRepeatNanoid = (box) => {
  const id = nanoid(4)
  if (box.has(id)) {
    return createUnRepeatNanoid(box)
  }
  return id
}

watch(() => props.modelValue, (val) => {
  if (val) {
    Object.assign(state, {
      checkedList: [],
      itemMap: {},
      idSet: {}
    })
    state.init()
    state.getList()

  }
})

</script>
<style lang='scss' scoped>
</style>
