<template>
  <el-row class="tab-equip-conn" :gutter="16">
    <el-col :md="12">
      <div class="conn-wrap">
        <div class="conn-title">MQTT协议接入方式</div>

        <el-form :label-width="96">
          <el-form-item label="MQTT主机：">
            <el-row class="conn-item">
              <span>10.177.38.27</span>
              <el-button class="btn-copy" link @click="copyToClipboard('10.177.38.27')">
                &lt; 点击复制 &gt;
                <i class="iconfont se-dig-icon-emb-canvas-copy"></i>
              </el-button>
            </el-row>
          </el-form-item>
          <el-form-item label="MQTT主机：">
            <el-row class="conn-item">
              <span>10.177.38.28</span>
              <el-button class="btn-copy" link @click="copyToClipboard('10.177.38.28')">
                &lt; 点击复制 &gt;
                <i class="iconfont se-dig-icon-emb-canvas-copy"></i>
              </el-button>
            </el-row>
          </el-form-item>
          <el-form-item label="MQTT主机：">
            <el-row class="conn-item">
              <span>10.177.38.29</span>
              <el-button class="btn-copy" link @click="copyToClipboard('10.177.38.29')">
                &lt; 点击复制 &gt;
                <i class="iconfont se-dig-icon-emb-canvas-copy"></i>
              </el-button>
            </el-row>
          </el-form-item>

        </el-form>
      </div>
    </el-col>
    <el-col :md="12">
      <el-row class="code-wrap">
        <div class="code-header"><b>内容示例：</b></div>
        <div class="code-box">
          <UCode v-model="jsonData" :readonly="true" bgColor="#f6f6f6"></UCode>
        </div>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup>
import { inject } from 'vue'
import { UCode } from '@/components/Business/'
import useCopyText from '@/hooks/useCopy.js'

const _l = inject('_l')
const { copyToClipboard } = useCopyText()

const jsonData = JSON.stringify({
  test: 1,
  test2: {
    'test2-1': {
      'test2-1-1': 2
    }
  }
}, '', 2)

</script>
<style lang='scss' scoped>
.tab-equip-conn {
  height: 100%;
  overflow: auto;
}
.conn-wrap {
  height: 100%;
  padding: 20px;
  background: #f9f9f9;
  :deep(.el-form-item__label) {
    font-weight: normal;
  }
  .conn-title {
    margin-bottom: 20px;
    font-weight: bold;
  }
}
.conn-item {
  .el-button {
    color: #447dfb;
    i {
      margin-left: 8px;
      color: #5a5a5a;
    }
  }
}
.code-wrap {
  flex-direction: column;
  height: 100%;
  background: #f9f9f9;
  .code-header {
    padding: 0 14px;
    line-height: 40px;
  }
  .code-box {
    position: relative;
    flex: 1;
  }
}
</style>
