<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" title="邮箱邀请" width="50%" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <el-tabs v-model="state.active">
      <el-tab-pane label="成员邮箱" name="first">
        <div style="margin-bottom: 20px">
          <span style="color: #ff0000">Tips:</span>
          邀请链接有效期7天，到期后重新邀请，请及时通知成员加入
        </div>
        <el-form label-position="left" label-width="100px">
          <el-form-item label="邮箱" prop="email">
            <el-scrollbar height="400px" style="width: 100%;">
              <el-form ref="userFormRef_b" :model="form1">
                <el-form-item v-for="(item, index) in form1.email" :key="index" style="margin-bottom: 15px;" :prop="`email[${index}]`" :rules="rules1.email">
                  <el-row style="width: 100%;" justify="space-between">
                    <el-input v-model="form1.email[index]" placeholder="请输入邮箱" style="flex: 1" />
                    <el-button v-if="form1.email.length > 1" style="margin-left: 20px" link icon="Delete" @click="state.del(index)"></el-button>
                  </el-row>
                </el-form-item>
                <!-- <div  style="display: flex; width: 100%; margin-bottom: 15px">
                  
                </div> -->
              </el-form>
              <el-button link icon="Plus" type="primary" @click="state.add">添加</el-button>
            </el-scrollbar>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <!-- <el-tab-pane label="邀请记录" name="second">
          <el-table :data="emailList" border>
            <el-table-column label="邀请人" prop="userId" />
            <el-table-column label="邀请邮箱" prop="userId" />
            <el-table-column label="状态" prop="userId" />
            <el-table-column label="加入时间" prop="userId" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button link>复制链接</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane> -->
    </el-tabs>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="state.ok">确 定</el-button>
        <el-button @click="state.cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { inject } from 'vue'
import { inviteLink, inviteLinkList } from '@/api/login'
import { add } from 'lodash'

const _l = inject('_l')

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object
  }
})
const emits = defineEmits(['update:modelValue'])

const state = reactive({
  active: 'first',
  async ok() {
    const valid = await userFormRef_b.value.validate()
    console.log('valid', valid)
    // state.cancel()
  },
  cancel() {
    emits('update:modelValue', false)
  },

  del(index) {
    form1.email.splice(index, 1)
  },
  add() {
    form1.email.push('')
  }
})

const userFormRef_b = ref()
const form1 = reactive({
  email: ['']
})

const rules1 = reactive({
  email: [{ type: 'email', required: true, message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
})

</script>
<style lang='scss' scoped>
</style>
