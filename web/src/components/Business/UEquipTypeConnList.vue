<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="960" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>
      <div class="u-title">关联</div>
    </template>
    <el-row class="type-conn-equip-list">
      <div class="form-box" align="middle">
        <span>名称</span>
        <el-input v-model.trim="filterKey" style="width: 200px;" clearable></el-input>
        <el-button @click="state.search">筛选</el-button>
      </div>

      <el-table :data="state.list" v-loading="state.loading">
        <el-table-column label="设备名称" prop="name"></el-table-column>
        <el-table-column label="描述" prop="descr"></el-table-column>
        <el-table-column label="联网状态">
          <template #default="{ row}">
            {{ NETWORK_STATUS.get(row.online) }}
          </template>
        </el-table-column>
        <el-table-column label="运行状态">
          <template #default="{ row}">
            {{ RUNNING_STATUS.get(row.warned) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" >
          <template #default="{row}">
            <el-tooltip content="详情" placement="top">
              <el-button text @click="state.detail(row)"><i class="iconfont se-dig-icon-detail1"></i></el-button>
            </el-tooltip>

            <el-popconfirm title="确定解绑?" placement="top" @confirm="state.connCancel(row)">
              <template #reference >
                <el-button text title="解绑">
                  <i class="iconfont se-dig-icon-yueshupeizhi icon-conn"></i>
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-row justify="end" style="padding-top: 12px;">
        <el-pagination @size-change="state.getList" @current-change="state.getList" :page-sizes="state.sizes" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :layout="state.layout" :total="state.total">

        </el-pagination>
      </el-row>
    </el-row>
  </el-dialog>
</template>
<script setup>
import { inject, getCurrentInstance } from 'vue'
import { NETWORK_STATUS, RUNNING_STATUS } from '@/config/'
import { ApiEquip } from '@/api/Business'
import { useRouter } from 'vue-router'

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object || Array,
    required: true,
  },
})
const { proxy } = getCurrentInstance()
const router = useRouter()
const _l = inject('_l')
const emits = defineEmits(['update:modelValue', 'ok'])

const filterKey = ref()
const query = {
  pageNum: 1,
  pageSize: 10
}

const state = reactive({
  sizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  total: 0,
  loading: false,
  list: [],
  async getList() {
    try {
      state.loading = true
      const { code, rows, total } = await ApiEquip({
      params: {
          equipmentTypeId: props.data.id,
        name: filterKey.value,
        ...query
      }
    })
    if (code === 'ok') {
      state.list = rows || []
      state.total = total
    }
    } finally {
      state.loading = false
    }
    
  },
  cancel() {
    emits('update:modelValue', false)
  },
  search() {
    query.pageNum = 1
    state.getList()
  },
  detail(row) {
    router.push({
      name: 'EquipDetail',
      query: {
        id: row?.id || 'equipId'
      }
    })
  },
  async connCancel(row) {
    const { code } = await ApiEquip({
      method: 'put',
      data: {
        id: row.id,
        equipmentTypeId: null
      }
    })
    if (code === 'ok') {
      proxy.$modal.msgSuccess('解绑成功')
    }
  }
})

watch(() => props.modelValue, (val) => {
  if (val) {
    filterKey.value = ''
    state.list = []
    query.pageNum = 1
    state.getList()
  }
})

</script>
<style lang='scss' scoped>
.type-conn-equip-list {
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  display: flex;
  .form-box {
    display: flex;
    gap: 12px;
    align-items: center;
  }
  .el-table {
    margin-top: 12px;
    height: 60vh;
  }
  .icon-conn {
    transform: rotate(45deg);
  }
}
</style>
