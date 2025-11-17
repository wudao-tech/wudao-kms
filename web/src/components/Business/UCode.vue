<template>
  <div :id="codeId"></div>
</template>
<script setup>
import { nanoid } from '@/utils'
import { onMounted, nextTick, watch, defineProps, defineEmits } from 'vue'
import CodeFlask from 'codeflask/build/codeflask.module.js'

const props = defineProps({
  modelValue: String,
  bgColor: String,
  language: {
    type: String,
    default: 'javascript'
  },
  readonly: {
    type: [Boolean, String],
    default: false
  }
})
const emits = defineEmits(['update:modelValue'])

const codeId = `code-${nanoid()}`
let flask
let flashCode
watch(() => props.modelValue, () => {
  // console.log('props.modelValue', props.modelValue)
  if (props.modelValue !== flashCode) {
    flask.updateCode(props.modelValue || '')
  }
})

onMounted(() => {
  flask = new CodeFlask(`#${codeId}`, {
    language: props.language,
    lineNumbers: true,
    enableAutocorrect: true,
    readonly: !!props.readonly
  })
  flask.updateCode(props.modelValue || '')
  flask.onUpdate((code) => {
    flashCode = code
    emits('update:modelValue', code)
  })
  if (props.bgColor) {
    nextTick(() => {
      document.querySelector(`#${codeId} .codeflask`).style.backgroundColor = props.bgColor
      document.querySelector(`#${codeId} .codeflask__pre`).style.width = 'auto'

      // codeflask__flatten  
    })
  }
})
</script>
<style lang='scss' scoped>
</style>
