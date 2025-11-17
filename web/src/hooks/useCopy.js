import ClipboardJS from 'clipboard'
import {getCurrentInstance} from 'vue'

export default function () {
  const clipboard = ref('')
  const {proxy} = getCurrentInstance()
  const copyToClipboard = (url) => {
    clipboard.value = new ClipboardJS('.btn-copy', {
      text: () => url
    })
    clipboard.value.on('success', (e) => {
      proxy.$modal.msgSuccess('复制成功')
      clipboard.value.destroy()
    })
    clipboard.value.on('error', (e) => {
      proxy.$modal.msgSuccess('复制失败')
      clipboard.value.destroy()
    })
  }

  return {
    copyToClipboard
  }
}
