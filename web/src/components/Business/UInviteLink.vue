<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" title="链接邀请" width="50%" style="height: 700px" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <el-tabs v-model="state.active">
      <el-tab-pane label="链接邀请" name="first">
        <div style="margin-bottom: 20px">
          <span style="color: #ff0000">Tips:</span>
          邀请链接有效期7天，到期后重新邀请，请及时通知成员加入
        </div>
        <div style="display: flex; margin-bottom: 20px">
          <el-input v-model="state.url" readonly />
          <el-button class="btn-copy" style="margin-left: 15px" link icon="CopyDocument" @click="copyToClipboard(state.url)">复制</el-button>
        </div>
        <el-button type="primary" @click="state.getUrl">重置链接</el-button>
      </el-tab-pane>
      <!-- <el-tab-pane label="邀请记录" name="second">
          <el-table :data="emailList" border :max-height="500">
            <el-table-column label="邀请人" prop="inviteName" width="120" />
            <el-table-column label="邀请链接" prop="url" />
            <el-table-column label="状态" prop="joinStatus" width="120">
              <template #default="scope">
                {{ scope.row.joinStatus === '1' ? '未加入' : '已加入' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" prop="userId" width="120">
              <template #default="scope">
                <el-button type="primary" link @click="copyToClipboard(scope.row.url)">复制链接</el-button>
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
import { inject, getCurrentInstance } from 'vue'
import { inviteLink, inviteLinkList } from '@/api/login'
import ClipboardJS from 'clipboard';

const _l = inject('_l')

const {proxy} = getCurrentInstance()
const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object
  }
})
const emits = defineEmits(['update:modelValue'])

const state = reactive({
  active: 'first',
  url: '',
  getUrl() {
    inviteLink([{}]).then((res) => {
      state.url = res.data[0].url || ''
    })
  },
  ok() {
    state.cancel()
   },
  cancel() {
    emits('update:modelValue', false)
  }
})

const clipboard = ref('')
const copyToClipboard = (url) => {
  clipboard.value = new ClipboardJS('.btn-copy', {
    text: () => url
  });
  clipboard.value.on('success', (e) => {
    proxy.$modal.msgSuccess('复制成功')
    clipboard.value.destroy();
  });
  clipboard.value.on('error', (e) => {
    proxy.$modal.msgSuccess('复制失败')
    clipboard.value.destroy();
  });
};

watch(() => props.modelValue, (val) => {
  val && state.getUrl()
})
</script>
<style lang='scss' scoped>
</style>
