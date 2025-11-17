<template>
  <div :class="{ 'has-logo': showLogo }" :style="{ backgroundColor: bgColor }" v-if="leftMenu.length">
    <el-scrollbar :class="sideTheme" wrap-class="scrollbar-wrapper">
      <transition :enter-active-class="proxy?.animate.menuSearchAnimate.enter" mode="out-in">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :background-color="bgColor"
          :text-color="textColor"
          :unique-opened="true"
          :active-text-color="theme"
          :collapse-transition="false"
          mode="vertical"
        >
          <sidebar-item v-for="(r, index) in leftMenu" :key="r.path + index" :item="r" />
        </el-menu>
      </transition>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from 'vue-router'
import SidebarItem from './SidebarItem.vue';
import variables from '@/assets/styles/variables.module.scss';
import useAppStore from '@/store/modules/app';
import useSettingsStore from '@/store/modules/settings';
import usePermissionStore from '@/store/modules/permission';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

let leftMenu = ref<any[]>([]);


const route = useRoute();
const router = useRouter()
const appStore = useAppStore();
const settingsStore = useSettingsStore();
const {sidebarRouters, SIDEBAR_INDEX} = toRefs(usePermissionStore())

watch(route, () => {
  if (!SIDEBAR_INDEX.value.get(route.path)) return
  const [l1, l2, l3, l4] = SIDEBAR_INDEX.value.get(route.path)
  if (typeof l4 === 'number') {
    leftMenu.value = sidebarRouters.value[l1].children[l2].children[l3].children

  } else {
    leftMenu.value = []
  }
  appStore.sidebar.menus = leftMenu.value
}, {
  deep: true,
  immediate: true
})

const showLogo = computed(() => settingsStore.sidebarLogo);
const sideTheme = computed(() => settingsStore.sideTheme);
const theme = computed(() => settingsStore.theme);
const isCollapse = computed(() => !appStore.sidebar.opened);
const activeMenu = computed(() => {
  const { meta, path } = route;
  // if set path, the sidebar will highlight the path you set
  if (meta.activeMenu) {
    return meta.activeMenu;
  }
  return path;
});

const bgColor = computed(() => (sideTheme.value === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground));
const textColor = computed(() => (sideTheme.value === 'theme-dark' ? variables.menuColor : variables.menuLightColor));


</script>
