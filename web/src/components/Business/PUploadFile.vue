<template>
  <el-upload ref="uploadRef" class="avatar-uploader" action="/file/upload" :show-file-list="false" :before-upload="beforeAvatarUpload" :http-request="fileUploadFn">
    <img v-if="fileUrl" :src="fileUrl" class="avatar" />
    <el-icon v-else class="avatar-uploader-icon">
      <Plus />
    </el-icon>

    <template #tip>
      <div class="el-upload__tip">
        {{ format.join('/') }} files with a size less than {{size}}
      </div>
    </template>
  </el-upload>
</template>
<script setup>
import { inject, getCurrentInstance } from 'vue'
import { fileUpload } from '@/api/menu'
import { alarmImageUrlConn } from '@/utils/theme'

const props = defineProps({
  modelValue: String | Array,
  format: {
    type: Array,
    default: () => {
      return ['jpg', 'png', 'jpeg']
    }
  },
  size: {
    type: String,
    default: '2M'
  }
})

const emits = defineEmits(['update:modelValue'])
const {proxy} = getCurrentInstance()
const _l = inject('_l')

const fileUrl = ref()
watch(() => props.modelValue, (val) => {
  fileUrl.value = val
}, {
  immediate: true
})

const UNIT = {
  B: 1, K: 1024, M: 1024 * 1024, G: 1024 * 1024 * 1024
}

const sizeNum = computed(() => {
  const num = +props.size.slice(0, -1)
  if (isNaN(num)) return 1024 * 1024 * 2
  const unit = UNIT[props.size.slice(-1).toUpperCase()] || 1
  return num * unit
})

const fileUploadFn = async (req) => {
  const file = req.file
  const format = file.type.split('/').slice(-1)[0]
  const { data } = await fileUpload(file, 'file', format)
  fileUrl.value = alarmImageUrlConn(data)
  emits('update:modelValue', fileUrl.value)
}
const beforeAvatarUpload = (rawFile) => {
  if (!props.format.includes(rawFile.type.split('/').slice(-1)[0])) {
    proxy.$modal.msgError('格式不符合要求')
    return false
  } else if (rawFile.size > sizeNum.value) {
    proxy.$modal.msgError(`文件大小不能超过${props.size}`)
    return false
  }
  return true
}


</script>
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

