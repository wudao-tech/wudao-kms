<template>
  <el-config-provider :locale="appStore.locale" :size="appStore.size">
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
import useSettingsStore from '@/store/modules/settings';
import { handleThemeStyle } from '@/utils/theme';
import useAppStore from '@/store/modules/app';

const appStore = useAppStore();

onMounted(() => {
  nextTick(() => {
    // 初始化主题样式
    handleThemeStyle(useSettingsStore().theme);
  });
});

provide('_l', appStore._l)
</script>
<style>
#nprogress .bar {
  background: #6b05a8 !important; /* 自定义颜色 */
}
</style>
