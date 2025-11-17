<template>
  <div class="tab-equip-conf">
    <el-row justify="space-between">
      <div class="u-title">基本配置</div>

      <el-button text @click="switchAble">
        <i class="iconfont se-dig-icon-cds-edit-batch"></i>
        <span style="margin-left: 4px;">编辑配置</span>
      </el-button>
    </el-row>
    <div class="form-wrap">
      <el-form>
        <el-row :gutter="16">
          <el-col :md="12">
            <el-form-item label="设备类型">
              <el-select></el-select>
            </el-form-item>
            <el-form-item label="所属网关">
              <el-select filterable remote remote-method remote-show-suffix></el-select>
            </el-form-item>
            <el-form-item label="接入类型">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="接入协议">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="联网方式">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="重要程度">
              <el-radio-group>
                <el-radio :value="1">一般</el-radio>
                <el-radio :value="2">重要</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="是否启用">
              <el-radio-group>
                <el-radio :value="1">是</el-radio>
                <el-radio :value="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="自定义标签">
              <div class="custom-tag-item" v-for="t in 3" :key="t">
                <el-input :value="t"></el-input>

                <el-button text><i class="iconfont se-dig-icon-a-Deleteshanchu"></i> </el-button>
              </div>
              <div>
                <el-button text style="color: #447DFB;">
                  + <span style="margin-left: 2px;">添加标签</span>
                </el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :md="12">
            <el-form-item label="制造厂商">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="出厂日期">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="序列号">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="设备规格">
              <el-input></el-input>
            </el-form-item>
            <el-form-item label="设备详情">
              <!-- <Editor :min-height="192"></Editor> -->
              <mavon-editor v-model="content" :ishljs="false" />
            </el-form-item>

          </el-col>
        </el-row> 
      </el-form>
    </div>
    <el-row class="btn-wrap" justify="end" v-show="editAble">
      <el-button @click="switchAble">取消</el-button>
      <el-button type="primary">确定</el-button>
    </el-row>
  </div>
</template>
<script setup>
import { inject } from 'vue'
import {mavonEditor} from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';

const content = ref('');

const _l = inject('_l')

const editAble = ref(false)

const switchAble = () => {
  editAble.value = !editAble.value
}

</script>
<style lang='scss' scoped>
.tab-equip-conf {
  height: 100%;
  overflow: hidden;
  background: #fff;
  display: flex;
  flex-direction: column;
  .form-wrap {
    flex: 1;
    overflow: auto;
  }
  .btn-wrap {
    height: 40px;
    margin-top: 12px;
  }
  .form-wrap {
    margin-top: 12px;
    overflow-x: hidden;
    .custom-tag-item {
      display: flex;
      width: 100%;
      flex-wrap: nowrap;
      & + .custom-tag-item {
        margin-top: 12px;
      }
    }
  }
}
</style>
