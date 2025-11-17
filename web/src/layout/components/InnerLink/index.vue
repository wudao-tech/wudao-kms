<template>
  <div :style="'height:' + height" class="iframe-container">
    <iframe :id="iframeId" style="width: 100%; height: 100%; border: 0" :src="src"></iframe>
  </div>
</template>

<script setup lang="ts">
import { propTypes } from '@/utils/propTypes';

const props = defineProps({
  src: propTypes.string.def('/'),
  iframeId: propTypes.string.isRequired
});
const height = ref(document.documentElement.clientHeight - 94.5 + 'px');
</script>

<style scoped>
.iframe-container {
  border: none;
  outline: none;
  box-shadow: none;
}

.iframe-container iframe {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}
</style>
