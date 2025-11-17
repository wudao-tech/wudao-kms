<template>
  <!-- <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <side-bar v-if="!sidebar.hide" class="sidebar-container" />
    <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <navbar ref="navbarRef" @set-layout="setLayout" />
        <tags-view v-if="needTagsView" />
      </div>
      <app-main />
      <settings ref="settingRef" />
    </div>
  </div> -->

  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <div class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <navbar @CHANGE-ALL-MENU="changeAllMenu" @change-tab="onchangeTab" />
      </div>
      <div>
        <side-bar class="sidebar-container" :curIndex="curIndex" />
        <!-- <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" style="width: calc(100% - 200px); margin-left: 200px">
          <app-main />
          <settings ref="settingRef" />
        </div> -->
        <div :class="{ rightHide: isChild }">
          <app-main />
          <settings ref="settingRef" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SideBar from './components/Sidebar/index.vue';
import { AppMain, Navbar, Settings, TagsView } from './components';
import useAppStore from '@/store/modules/app';
import useSettingsStore from '@/store/modules/settings';
import { initWebSocket } from '@/utils/websocket';
import { initSSE } from "@/utils/sse";

const settingsStore = useSettingsStore();
const theme = computed(() => settingsStore.theme);
const sidebar = computed(() => useAppStore().sidebar);
const device = computed(() => useAppStore().device);
// const needTagsView = computed(() => settingsStore.tagsView);
const fixedHeader = computed(() => settingsStore.fixedHeader);
const classObj = computed(() => ({
  hideSidebar: !sidebar.value.opened,
  openSidebar: sidebar.value.opened,
  withoutAnimation: sidebar.value.withoutAnimation,
  mobile: device.value === 'mobile'
}));

const isChild = computed(() => {
  return !!sidebar.value?.menus.length
})
const curIndex = ref(0);

const flag = ref(true);

const { width } = useWindowSize();
const WIDTH = 992; // refer to Bootstrap's responsive design

watchEffect(() => {
  if (device.value === 'mobile') {
    useAppStore().closeSideBar({ withoutAnimation: false });
  }
  if (width.value - 1 < WIDTH) {
    useAppStore().toggleDevice('mobile');
    useAppStore().closeSideBar({ withoutAnimation: true });
  } else {
    useAppStore().toggleDevice('desktop');
  }
});

const navbarRef = ref<InstanceType<typeof Navbar>>();
const settingRef = ref<InstanceType<typeof Settings>>();

onMounted(() => {
  // nextTick(() => {
  //   navbarRef.value?.initTenantList();
  // });
});

onMounted(() => {
  let protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
  initWebSocket(protocol + window.location.host + import.meta.env.VITE_APP_BASE_API + '/resource/websocket');
});

onMounted(() => {
  // initSSE(import.meta.env.VITE_APP_BASE_API + '/resource/sse');
});

const changeAllMenu = (val) => {
  flag.value = val;
};
const onchangeTab = (val) => {
  // let selectMenu = JSON.parse(localStorage.getItem('route_tab'));
  // curIndex.value = val;
  // if (selectMenu.headerTabMenu.children && selectMenu.headerTabMenu.children[val].children) {
  //   isChild.value = true;
  // } else {
  //   isChild.value = false;
  // }
};
// const handleClickOutside = () => {
//   useAppStore().closeSideBar({ withoutAnimation: false });
// };

// const setLayout = () => {
//   settingRef.value?.openSetting();
// };
</script>

<style lang="scss" scoped>
@import '@/assets/styles/mixin.scss';
@import '@/assets/styles/variables.module.scss';

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$base-sidebar-width});
  transition: width 0.28s;
  background: $fixed-header-bg;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}
.rightHide {
  width: calc(100% - 200px);
  margin-left: 200px;
}
</style>
