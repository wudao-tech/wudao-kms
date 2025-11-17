<template>
  <div class="tab-equip-api">
    <el-row class="api-header" justify="space-between" align="middle">
      <el-radio-group v-model="query.category" @change="switchRuleTab" size="small">
        <el-radio-button value="record">调用记录</el-radio-button>
        <el-radio-button value="api">接口列表</el-radio-button>
      </el-radio-group>

      <el-row class="query-wrap">
        <el-button type="primary">新增</el-button>
      </el-row>
    </el-row>

    <el-row class="api-wrap">
      <el-table :data="activeTab.list">
        <el-table-column label="序号" prop="id"></el-table-column>
        <el-table-column label="节点名称" prop="name"></el-table-column>
      </el-table>

      <el-row justify="end" align="middle">
        <el-pagination @size-change="activeTab.change" @current-change="activeTab.change" :page-sizes="activeTab.sizes" v-model:current-page="activeTab.query.pageNum" v-model:page-size="activeTab.query.pageSize" :layout="activeTab.layout" :total="activeTab.total">
        </el-pagination>
      </el-row>
    </el-row>
  </div>
</template>
<script setup>
import { inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { TRIGGER_HISTORY } from '@/__mocks__/'

const route = useRoute()
const router = useRouter()
const _l = inject('_l')

const query = reactive({
  id: route.query.id,
  tab: 'api',
  category: route.query.category || 'record'
})


const switchRuleTab = (val) => {
  const q = val === 'record' ? record.query : api.query
  router.replace({
    path: route.path,
    query: {
      ...query,
      ...q,
      category: val
    }
  })
}
const record = reactive({
  query: {
    pageNum: 1,
    pageSize: 10
  },
  sizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  total: 100,
  list: [],
  async getList() {
    record.list = TRIGGER_HISTORY.rows
  },
  change() {
    router.replace({
      path: route.path,
      query: {
        ...query,
        ...record.query
      }
    })
  }
})

const api = reactive({
  query: {
    pageNum: 1,
    pageSize: 10
  },
  sizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  total: 100,
  list: [],
  async getList() {
    api.list = TRIGGER_HISTORY.rows
  },
  change() {
    router.replace({
      path: route.path,
      query: {
        ...query,
        ...api.query
      }
    })
  }
})

const activeTab = computed(() => {
  return query.category === 'record' ? record : api
})

watch(route, () => {
  if (route.query.tab !== 'api') return
  const { pageNum = 1, pageSize = 10, name = '', category = 'record' } = route.query
  query.category = category
  const r = category === 'record' ? record : api
  Object.assign(r.query, {
    pageNum: +pageNum,
    pageSize: +pageSize
  })
  console.log('check')
  r.getList()
}, {
  immediate: true
})
</script>
<style lang='scss' scoped>
.tab-equip-api {
  display: flex;
  height: 100%;
  padding: 10px 8px;
  flex-direction: column;
  background: #f9f9f9;
  overflow: hidden;
}
.api-wrap {
  margin-top: 12px;
  flex: 1;
  flex-direction: column;
  overflow: hidden;
  > .el-table {
    flex: 1;
  }
  > .el-row {
    padding-top: 10px;
  }
}
</style>
