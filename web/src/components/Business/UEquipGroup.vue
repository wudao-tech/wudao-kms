<template>
  <el-dialog ref="formDialogRef" :model-value="modelValue" width="640" append-to-body @close="state.cancel" :close-on-press-escape="false" :close-on-click-modal="false">
    <template #header>
      <div class="u-title">{{ title }}</div>
    </template>
    <el-form :model="equip" :rules="rules1" :label-width="90">
      <!--  -->
      <el-form-item label="唯一编码" v-if="['editGroup', 'editEquip'].includes(data.type)">
        <el-input v-model="equip.id" disabled></el-input>
      </el-form-item>
      <el-form-item label="上级分组" v-if="data.node && equip.groups[0] !== 0">
        <template v-if="['addGroup', 'editGroup'].includes(data.type)">
          <el-input disabled :model-value="equip.groupLabel"></el-input>
        </template>
        <template v-else-if="['addEquip', 'editEquip'].includes(data.type)">
          <el-tree-select v-model="equip.groups" :data="data.tree" :props="data.props" node-key="id" multiple :render-after-expand="false" show-checkbox check-strictly check-on-click-node collapse-tags :max-collapse-tags="4" clearable filterable style="width: 100%" />
        </template>

      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input v-model.trim="equip.code"></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model.trim="equip.name"></el-input>
      </el-form-item>
      <el-form-item label="图片" v-if="['addEquip', 'editEquip'].includes(data.type)">
        <el-upload ref="uploadRef" class="avatar-uploader" action="/api/file/upload" :show-file-list="false" :before-upload="beforeAvatarUpload" :http-request="fileUploadFn">
          <img v-if="equip.imgUrl" :src="equip.imgUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon">
            <Plus />
          </el-icon>

          <template #tip>
            <div class="el-upload__tip">
              jpg/png files with a size less than 2M
            </div>
          </template>
        </el-upload>
      </el-form-item>

    </el-form>
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
import { ApiGroup, ApiEquip } from '@/api/Business/'
import { fileUpload } from '@/api/menu'
import { alarmImageUrlConn } from '@/utils/theme'

const _l = inject('_l')

const props = defineProps({
  modelValue: Boolean,
  data: {
    type: Object
  }
})
const emits = defineEmits(['update:modelValue', 'update:group', 'update:equip'])

const TITLE_MAP = {
  addGroup: '新增分组',
  addEquip: '新增设备',
  editGroup: '编辑分组',
  editEquip: '编辑设备'
}
const title = computed(() => {
  const { type } = props.data
  return TITLE_MAP[type]
})

const state = reactive({
  async ok() {
    const { type, node } = props.data
    const { id, code, name } = equip
    let res
    switch (props.data.type) {
      case 'addGroup':
        res = await ApiGroup({
          method: 'post',
          data: {
            pid: node?.key || null,
            code,
            name
          }
        })
        break
      case 'editGroup':
        res = await ApiGroup({
          method: 'put',
          data: {
            id,
            code,
            name
          }
        })
        break
      case 'addEquip':
      case 'editEquip': {
        const { groups, id, code, name, imgUrl } = equip
        const method = id ? 'put' : 'post'
        res = await ApiEquip({
          method,
          data: {
            id: id || undefined,
            code,
            name,
            groups,
            imgUrl
          }
        })
      }
        break
    }
    if (res.code === 'ok') {
      if (['addGroup', 'editGroup'].includes(type)) {
        emits('update:group')
      } else if (['addEquip', 'editEquip'].includes(type)) {
        emits('update:equip')
      }
      state.cancel()
    }
  },
  cancel() {
    emits('update:modelValue', false)
  }
})



const userFormRef_b = ref()
const DEFAULT_ITEM = JSON.stringify({
  // 父级分组，分组的父级是字符串，设备的父级是数组
  groups: [],
  groupLabel: '',
  id: '',
  code: '',
  name: '',
  imgUrl: ''
})
const equip = reactive(JSON.parse(DEFAULT_ITEM))

watch(() => props.modelValue, (val) => {
  if (val) {
    Object.assign(equip, JSON.parse(DEFAULT_ITEM))
    const { type, node } = props.data
    switch (type) {
      case 'addEquip':
        if (node) {
          equip.groups = [node.data.id]
          // equip.groupLabel = node.data.name
        }
        break
      case 'editEquip':
        Object.assign(equip, {
          ...node
        })
        break
      case 'addGroup':
        Object.assign(equip, {
          groups: [node?.data.id || 0],
          groupLabel: node?.label || '',
          code: '',
          name: ''
        })
        break
      case 'editGroup':
        Object.assign(equip, {
          id: node.data.id,
          groups: [node.data.pid || 0],
          groupLabel: node.parent.label,
          code: node.data.code,
          name: node.data.name
        })
        break
    }
  } else {
    equip.groups = []
  }
})

const fileUploadFn = async (req) => {
  const file = req.file
  const format = file.type.split('/').slice(-1)[0]
  const { data } = await fileUpload(file, 'file', format)
  equip.imgUrl = alarmImageUrlConn(data)
  console.log('equip.imgUrl', equip.imgUrl)
}
const beforeAvatarUpload = (rawFile) => {
  if (!['jpg', 'png', 'jpeg'].includes(rawFile.type.split('/').slice(-1)[0])) {
    proxy.$modal.msgError('Avatar picture must be JPG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    proxy.$modal.msgError('Avatar picture size can not exceed 2MB!')
    return false
  }
  return true
}

const rules1 = reactive({
  code: [{ required: true, message: '请输入', trigger: 'blur' }],
  name: [{ required: true, message: '请输入', trigger: 'blur' }],
})

</script>
<style lang='scss' scoped>
</style>
<style lang="scss" scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.avatar-uploader .el-upload {
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  border: 1px #f5f5f5 solid;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  border: 1px dashed var(--el-border-color);
}
</style>
