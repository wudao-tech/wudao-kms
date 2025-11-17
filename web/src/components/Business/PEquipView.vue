<template>
  <div class="tab-equip-view">
    <el-row class="property-wrap" :gutter="16">
      <el-col :lg="4" :md="6" :sm="8" v-for="t in 30" :key="t">
        <div class="property-item">
          <div class="val">属性值</div>
          <div class="label">属性名</div>
        </div>
      </el-col>
    </el-row>
    <el-row justify="end" style="padding-top: 12px;">
      <el-pagination @size-change="state.getList" @current-change="state.getList" :page-sizes="state.sizes" v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :layout="state.layout" :total="state.total">
      </el-pagination>
    </el-row>

    <div class="equip-statis">
      <el-row class="statis-title" justify="space-between">
        <div class="title">设备消息统计</div>
        <el-row class="legend">
          <div class="legend-item fail">写入失败</div>
          <div class="legend-item succ">写入成功</div>
        </el-row>
      </el-row>
      <div class="equip-graph" id="equip-record" v-if="state.statistics.length"></div>
      <el-empty v-else style="width: 100%;height:200px"></el-empty>
    </div>

    <div class="equip-statis">
      <el-row class="statis-title" justify="space-between">
        <div class="title">设备流量统计(KB)</div>
        <el-row class="legend">
          <div class="legend-item succ">总流量</div>
          <div class="legend-item fail">成功流量</div>
        </el-row>
      </el-row>
      <div class="equip-graph" id="equip-sizes" v-if="state.statistics.length"></div>
      <el-empty v-else style="width: 100%;height:200px"></el-empty>
    </div>

  </div>
</template>
<script setup>
import { inject } from 'vue'
import * as echarts from 'echarts'

import { EQUIP_STATIS } from '@/__mocks__'

const _l = inject('_l')

const query = reactive({
  pageNum: 1,
  pageSize: 10
})
const state = reactive({
  sizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  total: 100,
  statistics: [],
  getList() {
    state.statistics = EQUIP_STATIS.data

    state.lineInit()
  },
  lineInit() {
    let xAxis = [], succCounts = [], totalCounts = [], succSizes = [], totalSizes = []

    state.statistics.forEach(t => {
      const { connectEnd, successCount, totalCount, successSize, totalSize } = t
      xAxis.push(connectEnd)
      succCounts.push(successCount)
      totalCounts.push(totalCount)
      succSizes.push((successSize / 1024).toFixed(2))
      totalSizes.push((totalSize / 1024).toFixed(2))
    })
    nextTick(() => {
      state.lineDraw('equip-record', xAxis, [totalCounts, succCounts])

      state.lineDraw('equip-sizes', xAxis, [totalSizes, succSizes])
    })
  },
  lineDraw(el, xAxis, yAxis) {

    var chartDom = document.getElementById(el)
    var myChart = echarts.init(chartDom)
    var option
    option = state.lineOptions(xAxis, yAxis)
    option && myChart.setOption(option)
  },
  circleColors: ['#ff5a5a', '#61ddaa'],
  lineOptions(xAxis, yAxis) {
    return {
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          return `<div style="margin: 0px 0 0;line-height:1;"><div style="margin: 0px 0 0;line-height:1;"><div style="font-size:14px;color:#666;font-weight:400;line-height:1;">${params[0].axisValue}</div>

          ${params.map((t, i) => {
            return `<div style="margin: 10px 0 0;line-height:1;"><div style="margin: 0px 0 0;line-height:1;"><div style="margin: 0px 0 0;line-height:1;"><span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-color:${state.circleColors[i]};"></span><span style="float:right;margin-left:10px;font-size:14px;color:#666;font-weight:900">${t.data}</span><div style="clear:both"></div></div><div style="clear:both"></div></div>`
          }).join('')}`
        }
      },
      legend: null,
      toolbox: {
        feature: {
          saveAsImage: {}
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          data: xAxis
        }
      ],
      yAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          // name: legend[0],
          type: 'line',
          lineStyle: {
            color: '#f85a13'
          },
          areaStyle: {
            opacity: 0.8,
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 1,
                color: 'rgba(255,201,176,0.00)'
              },
              {
                offset: 0,
                color: '#f85a13'
              }
            ])
          },
          showSymbol: false,
          emphasis: {
            focus: 'series'
          },
          data: yAxis[0]
        },
        {
          // name: legend[1],
          type: 'line',
          lineStyle: {
            color: '#3dcd58'
          },
          areaStyle: {
            opacity: 0.8,
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 1,
                color: 'rgba(198,255,209,0.00)'
              },
              {
                offset: 0,
                color: '#3dcd58'
              }
            ])
          },
          showSymbol: false,
          emphasis: {
            focus: 'series'
          },
          data: yAxis[1]
        }
      ]
    }
  },
})

onMounted(() => {
  state.getList()
})
</script>
<style lang='scss' scoped>
.tab-equip-view {
  height: 100%;
  overflow: auto;
  padding: 10px;
  background: #f9f9f9;
  .property-wrap {
    max-height: 250px;
    overflow: auto;
    .property-item {
      height: 70px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      margin-bottom: 12px;
      background: #fff;
      border: 1px #d0e2f8 solid;
      border-radius: 2px;
      > .val {
        margin-bottom: 4px;
        font-weight: bold;
      }
      > .label {
        color: #999;
      }
    }
  }

  .equip-statis {
    margin-top: 16px;
    .statis-title {
      display: flex;
      .legend-item {
        position: relative;
        padding-left: 38px;
        color: #2C3542;
        &.succ:before {
          content: '';
          position: absolute;
          width: 22px;
          height: 1px;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          background: #61ddaa;
        }
        &.fail:before {
          content: '';
          position: absolute;
          width: 22px;
          height: 1px;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          background: #ff5a5a;
        }

        & + .legend-item {
          margin-left: 80px;
        }
      }
    }

    .equip-graph {
      height: 200px;
    }
  }
}
</style>
