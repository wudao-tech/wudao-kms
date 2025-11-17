<template>
  <div class="tab-equip-rule">
    <el-row class="rule-header" justify="space-between" align="middle">
      <el-radio-group v-model="query.category" @change="switchRuleTab" size="small">
        <el-radio-button value="record">事件记录</el-radio-button>
        <el-radio-button value="rule">触发规则</el-radio-button>
      </el-radio-group>

      <el-row class="query-wrap">
        <el-input v-model="filterText"></el-input>
        <el-button text><i class="iconfont se-dig-icon-emb-refresh"></i></el-button>
      </el-row>
    </el-row>

    <div class="record-wrap" v-if="query.category === 'record'">
      <el-scrollbar>
        <el-timeline>
          <el-timeline-item placement="top" v-for="t in record.list" :key="t.id" :timestamp="t.happenTime">
            <div class="record-item normal">
              <el-row justify="space-between">
                <el-row class="record-status">
                  <div class="icon-status normal warn">
                    <i class="iconfont se-dig-icon-emb-dianzan11"></i>
                    <i class="iconfont se-dig-icon-warn"></i>
                  </div>
                  <div class="record-msg">正常</div>
                  <el-row class="record-msg">
                    <div class="msg-item">告警：高</div>
                    <div class="msg-item">描述：温度</div>
                    <div class="msg-item">持续时间：10min</div>

                  </el-row>
                </el-row>
                <div>
                  <el-button type="primary" text bg>切片显示</el-button>
                </div>
              </el-row>
              <el-row class="equip-msg">
                <div class="msg-item">设备：T1</div>
                <div class="msg-item">参数：T1</div>
                <div class="msg-item">值：T1</div>
                <div class="msg-item">领域：T1</div>
              </el-row>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-scrollbar>
    </div>

    <el-row class="rule-wrap" v-else>
      <el-table :data="rule.list">
        <el-table-column label="序号" prop="id"></el-table-column>
        <el-table-column label="节点名称" prop="name"></el-table-column>
      </el-table>

      <el-row justify="end" align="middle">
        <el-pagination @size-change="rule.change" @current-change="rule.change" :page-sizes="rule.sizes" v-model:current-page="rule.query.pageNum" v-model:page-size="rule.query.pageSize" :layout="rule.layout" :total="rule.total">
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
  tab: 'rule',
  category: route.query.category || 'record'
})


const switchRuleTab = (val) => {
  const q = val === 'record' ? record.query : rule.query
  router.replace({
    path: route.path,
    query: {
      ...query,
      ...q,
      name: filterText.value,
      category: val
    }
  })
}
const filterText = ref()
const record = reactive({
  query: {
    pageNum: 1,
    pageSize: 10
  },
  list: [],
  async getList() {
    record.list.push(...TRIGGER_HISTORY.rows)
    console.log('record.list', record.list)
  }
})

const rule = reactive({
  query: {
    pageNum: 1,
    pageSize: 10
  },
  sizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  total: 100,
  list: [],
  async getList() {
    rule.list.push(...TRIGGER_HISTORY.rows)
  },
  change() {
    router.replace({
      path: route.path,
      query: {
        ...query,
        ...rule.query,
        name: filterText.value
      }
    })
  }
})


watch(route, () => {
  if (route.query.tab !== 'rule') return
  const { pageNum = 1, pageSize = 10, name = '', category = 'record' } = route.query
  query.category = category
  filterText.value = name
  const r = category === 'record' ? record : rule
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
.tab-equip-rule {
  display: flex;
  height: 100%;
  padding: 10px 8px;
  flex-direction: column;
  background: #f9f9f9;
  overflow: hidden;
}
.query-wrap {
  flex-wrap: nowrap;
}
.record-wrap {
  flex: 1;
  margin-top: 12px;
  overflow: hidden;
  .el-scrollbar {
    height: 100%;
  }
  .el-timeline {
    padding-left: 0;
  }
  .record-item {
    padding: 8px 16px 16px;
    background: #fff;
    &.normal {
      border-left: 2px #3dcd58 solid;
    }
    &.warn {
      border-left: 2px #f95a12 solid;
    }
    .record-status {
      .msg-item {
        & + .msg-item {
          margin-left: 35px;
        }
      }
    }
    .icon-status {
      width: 2em;
      &.normal {
        color: #3dcd58;
      }
      &.warn {
        color: #f95a12;
      }
    }
    .equip-msg {
      margin-top: 14px;
      padding-left: 2em;
      color: #979797;
      .msg-item {
        flex: 1 0 24%;
      }
    }
  }
}
.rule-wrap {
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
