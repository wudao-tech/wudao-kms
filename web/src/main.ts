import { createApp } from 'vue';
// global css
import 'virtual:uno.css';
import '@/assets/styles/index.scss';
import 'element-plus/theme-chalk/index.css'
import '@/assets/fonts/se/iconfont.css';

// 主题初始化 - 防止Mac系统深色模式自动触发
(() => {
  const storedTheme = localStorage.getItem('useDarkKey');
  if (!storedTheme) {
    // 如果没有存储的主题偏好，强制设置为明亮模式
    localStorage.setItem('useDarkKey', 'light');
    // 确保HTML元素没有dark类
    document.documentElement.classList.remove('dark');
  }
})();

// App、router、store
import App from './App.vue';
import store from './store';
import router from './router';

// 自定义指令
import directive from './directive';

// 注册插件
import plugins from './plugins/index'; // plugins

// 高亮组件
// import 'highlight.js/styles/a11y-light.css';
import 'highlight.js/styles/atom-one-dark.css';
import 'highlight.js/lib/common';
import HighLight from '@highlightjs/vue-plugin';

// svg图标
import 'virtual:svg-icons-register';
import ElementIcons from '@/plugins/svgicon';

import VueSplit from 'vue3-split-panel';

// permission control
import './permission';

// 国际化
import i18n from '@/lang/index';

// vxeTable
import VXETable from 'vxe-table';
import 'vxe-table/lib/style.css';
VXETable.config({
  zIndex: 999999
});

// 修改 el-dialog 默认点击遮照为不关闭
import { ElDialog } from 'element-plus';
ElDialog.props.closeOnClickModal.default = false;

const app = createApp(App);
app.use(VueSplit);

app.use(HighLight);
app.use(ElementIcons);
app.use(router);
app.use(store);
app.use(i18n);
app.use(VXETable);
app.use(plugins);
// 自定义指令
directive(app);

// 强制设置语言为中文
import useAppStore from '@/store/modules/app';
const appStore = useAppStore();
appStore.forceZhCN();

app.mount('#app');
