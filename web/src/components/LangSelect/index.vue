<template>
  <el-dropdown trigger="click" @command="handleLanguageChange">
    <!-- <el-icon class="element ml20 fs20 mr20"><Orange /></el-icon> -->
    <svg-icon class="element ml20 fs20 mr20" style="font-size: 16px" icon-class="lang"></svg-icon>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item :disabled="appStore.language === 'zh_CN'" command="zh_CN"> 中文 </el-dropdown-item>
        <!-- <el-dropdown-item :disabled="appStore.language === 'en_US'" command="en_US"> English </el-dropdown-item> -->
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { useAppStore } from '@/store/modules/app';
import SvgIcon from '@/components/SvgIcon/index.vue';

const appStore = useAppStore();
const { locale } = useI18n();

const message: any = {
  zh_CN: '切换语言成功！',
  en_US: 'Switch Language Successful!'
};
const handleLanguageChange = (lang: any) => {
  locale.value = lang;
  appStore.changeLanguage(lang);
  ElMessage.success(message[lang] || '切换语言成功！');
};
</script>

<style lang="scss" scoped>
.lang-select--style {
  font-size: 18px;
  line-height: 50px;
}
.element {
  cursor: pointer;
}
.ml20 {
  margin-left: 20px;
}
.mr20 {
  margin-right: 20px;
}
.dp-c {
  display: flex;
  align-items: center;
}
.fs20 {
  font-size: 20px;
}
</style>
