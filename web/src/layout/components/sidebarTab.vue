<template>
  <div class="menu_tab" v-if="!headerTabMenu.hidden">
    <div class="menu_one">{{ headerTabMenu.meta?.title }}</div>
    <div v-if="headerTabMenu.children?.length&&!headerTabMenu.hidden" style="margin-left: 42px; height: 100%">
      <template v-for="(item, index) in headerTabMenu.children" :key="index">
        <span v-if="!item.hidden" class="tab_item" :class="{ curActive: curIndex === index }" @click="changeTab(item)">{{ item.meta.title }}</span>
      </template>
    </div>
  </div>
</template>
<script setup>
import { ref, defineProps, watch, computed } from 'vue'
import { useUserStore } from '@/store/modules/user'
import usePermissionStore from '@/store/modules/permission'
import {findFirstChildPath} from '@/utils/permission'


// const props = defineProps({
//   headerTabMenu: {
//     type: Object,
//     default: () => {
//       return {}
//     }
//   }
// })

const userStore = useUserStore()
const { SIDEBAR_INDEX, sidebarRouters } = toRefs(usePermissionStore())
const router = useRouter()
const route = useRoute()

const headerTabMenu = ref([])

watch(route, () => {
  const resolved = router.resolve(route.path)

  if(!resolved) return
  
  // 优先使用 activeMenu 配置
  let targetPath = route.path
  const routeMeta = resolved.matched[resolved.matched.length-1]?.meta
  if (routeMeta?.activeMenu) {
    targetPath = routeMeta.activeMenu
  }
  
  let routePath = resolved.matched[resolved.matched.length-1].path
  
  // 如果有 activeMenu，尝试解析 activeMenu 对应的路由
  if (routeMeta?.activeMenu) {
    const activeResolved = router.resolve(routeMeta.activeMenu)
    if (activeResolved && activeResolved.matched.length > 0) {
      routePath = activeResolved.matched[activeResolved.matched.length-1].path
    }
  }
  
  if (!SIDEBAR_INDEX.value.get(routePath)) {
    headerTabMenu.value = []
    return
  }
  
  const [l1, l2, l3] = SIDEBAR_INDEX.value.get(routePath)

  if (typeof l3 === 'number') {
    headerTabMenu.value = sidebarRouters.value[l1].children[l2]
  }else if(typeof l2 === 'number'){
    headerTabMenu.value = sidebarRouters.value[l1]
  } else {
    headerTabMenu.value = []
  }
}, {
  deep: true,
  immediate: true
})

const curIndex = computed(() => {
  // 优先使用 activeMenu 配置
  let checkPath = route.path
  const resolved = router.resolve(route.path)
  const routeMeta = resolved?.matched[resolved.matched.length-1]?.meta
  
  if (routeMeta?.activeMenu) {
    const activeResolved = router.resolve(routeMeta.activeMenu)
    if (activeResolved && activeResolved.matched.length > 0) {
      checkPath = activeResolved.matched[activeResolved.matched.length-1].path
    }
  }
  
  if (!SIDEBAR_INDEX.value.get(checkPath)) return -1
  const [l1, l2, l3] = SIDEBAR_INDEX.value.get(checkPath)

  if(SIDEBAR_INDEX.value.get(checkPath)){
    if(typeof l3 === 'number') {
      return l3
    }else if(typeof l2 === 'number'){
      return l2
    }
  }else{
    return 0
  }
})
const emit = defineEmits(['change-tab'])

const changeTab = (item) => {
  router.push(findFirstChildPath(item))
};
</script>
<style scoped lang="scss">
.menu_tab {
  display: flex;
  height: 100%;
  align-items: center;
}
.menu_one {
  font-size: 16px;
  font-weight: bold;
  padding: 0 40px;
  border-right: 1px solid #979797;
}
.tab_item {
  margin-right: 65px;
  display: inline-block;
  height: 100%;
  line-height: 58px;
  font-size: 16px;
  cursor: pointer;
  box-sizing: content-box;
}
.curActive {
  border-bottom: 2px solid #6b05a8;
  color: #6b05a8;
}
</style>
