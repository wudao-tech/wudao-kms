<template>
  <div class="menu_all">
    <div class="menu_left_bg"></div>
    <img src="@/assets/images/menu_left_bg.png" alt="" style="position: absolute; left: 0; bottom: 0; width: 160px; height: 378px;">
    <img src="@/assets/images/menu_right_bg.png" alt="" style="position: absolute; left: 160px; bottom: 0; width: calc(100% - 160px); height: 378px;">
    <div class="menu_all_right">
     
      <div style="height: 100%;">
        <div class="right_con">
          <el-icon class="menu_close element" @click="closeMenu"><Close /></el-icon>
          <div>
            <div v-for="item in sidebarRouters" :key="item.name" class="menu_div">
              <div class="menu_1">{{ item.meta.title }}</div>
              <div class="menu_content">
                <span v-for="i in item.children.filter(e => !e.hidden)" :key="i.title" class="menu_title">
                  <el-dropdown v-if="i.children && i.children.length > 0">
                    <span class="menu_title_h" style="font-size: 16px; height: 21px" @click="menuFn(i, 1)">
                      <svg-icon :icon-class="i.meta.icon" style="margin-right: 4px"></svg-icon>
                      {{ i.meta.title }}
                      <!-- <i class="iconfont se-dig-icon-star" style="margin-left: 30px; color: #000"></i> -->
                    </span>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item v-for="(e, index) in i.children.filter(t => !t.hidden)" style="background-color: #fff;" :key="index" @click="menuFn(e, 2)">{{ e.meta.title }}</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  <span v-else class="menu_title_h" @click="menuFn(i, 0)">
                    <svg-icon :icon-class="i.meta.icon" style="margin-right: 4px"></svg-icon>
                    {{ i.meta.title }}
                    <!-- <i class="iconfont se-dig-icon-star" style="margin-left: 30px; color: #000"></i> -->
                  </span>
                </span>
              </div>
            </div>
          </div>
          <!-- <div class="kongbai"></div> -->
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import usePermissionStore from '@/store/modules/permission';
import { findFirstChildPath } from '@/utils/permission';

const permissionStore = usePermissionStore();
const sidebarRouters = computed(() => {
  return permissionStore.getSidebarRoutes().filter((item) => item.hidden === false);
});
console.log('sidebarRouters', sidebarRouters);
const emit = defineEmits(['close_menu', 'menu_fn']);

const closeMenu = () => {
  emit('close_menu');
};

const menuFn = (routeItem, i) => {
  console.log('menuFn called with routeItem:', routeItem);
  console.log('Full sidebarRouters:', sidebarRouters.value);
  
  // 找到父路由
  let parentRoute = null;
  for (const parentItem of sidebarRouters.value) {
    if (parentItem.children && parentItem.children.includes(routeItem)) {
      parentRoute = parentItem;
      break;
    }
  }
  
  console.log('Found parent route:', parentRoute);
  
  let path;
  if (parentRoute) {
    // 传入父路径
    path = findFirstChildPath(routeItem, parentRoute.path);
  } else {
    // 如果没有找到父路由，直接使用
    path = findFirstChildPath(routeItem);
  }
  
  console.log('Final calculated path:', path);
  emit('menu_fn', path);
};
</script>
<style scoped lang="scss">
.element {
  cursor: pointer;
}
.menu_all {
  display: flex;
  height: 100%;
  position: relative;
  .menu_left_bg {
    width: 160px; 
    background: linear-gradient( 180deg, #EDF1FA 0%, #EDF0FF 100%); 
    position: absolute; 
    height: 100%;
    left: 0; 
    top: 0;
   
  }

  .menu_all_right {
    position: absolute;
    z-index: 99;
    width: 100%;
    font-size: 16px;
  }
  .right_con {
    height: 100%;
    display: flex;
    position: reactive;
    flex-direction: column;
  }
  .kongbai {
    flex: 1;
    // background: #fff;
    margin-left: 180px;
    background-image: url('@/assets/images/meun_bg.png');
    background-size: cover;
    background-position: bottom, right;
  }
  .menu_close {
    position: absolute;
    right: 20px;
    top: 15px;
    font-size: 18px;
    color: #867d7d;
  }
  .menu_div {
    border-bottom: 1px solid #eee;
    display: flex;
  }
  .menu_1 {
    font-weight: 700;
    width: 160px;
    padding-left: 35px;
    padding-top: 30px;
    color: #333;
  }
  .menu_content {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    color: #666666;
    padding: 30px 0 10px 80px;
  }
  .menu_title {
    width: 50%;
    margin-bottom: 15px;
  }
  .menu_title_h {
    cursor: pointer;
    display: flex;
    align-items: center;
    color: #333;
    .svg-icon {
      color: #898686;
    }
  }
  .menu_title_h:hover {
    color: #6b05a8;
    .svg-icon {
      color: #6b05a8;
    }
    .se-dig-icon-star {
      display: block;
    }
  }
}
.se-dig-icon-star {
  display: none;
}

.el-dropdown:hover {
  border: none !important;
}
:focus-visible {
  outline: none !important;
}
</style>
