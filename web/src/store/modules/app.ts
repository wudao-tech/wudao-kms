import zhCN from 'element-plus/es/locale/lang/zh-cn';
import enUS from 'element-plus/es/locale/lang/en';
import LANGUAGE from '@/store/language_text'

export const useAppStore = defineStore('app', () => {
  const sidebarStatus = useStorage('sidebarStatus', '1');
  const sidebar = reactive({
    // opened: sidebarStatus.value ? !!+sidebarStatus.value : true,
    opened: '1',
    withoutAnimation: false,
    hide: false,
    menus: []
  });
  const device = ref<string>('desktop');
  const size = useStorage<'large' | 'default' | 'small'>('size', 'default');

  // 语言
  const language = useStorage('language', 'zh_CN');
  const languageObj: any = {
    en_US: enUS,
    zh_CN: zhCN
  };
  const locale = computed(() => {
    return languageObj[language.value];
  });
  const languageIndex = computed(() => {
    const a = {
      zh_CN: 0,
      en_US: 1
    }
    return a[language.value]
  })
  const _l = (...ids) => {
    if (ids.length === 0) return
    if (ids.length === 1) {
      const l = LANGUAGE.get(ids[0])
      if (!l) {
        // return '暂无此key'
        return ids[0]
      }
      return LANGUAGE.get(ids[0])[+languageIndex.value]
    }
    return ids.reduce((acc, n) => {
      acc = acc + (languageIndex.value === 0 ? '' : ' ') + LANGUAGE.get(n)[languageIndex.value]
      return acc
    }, '')
  }

  const toggleSideBar = (withoutAnimation: boolean) => {
    if (sidebar.hide) {
      return false;
    }

    sidebar.opened = !sidebar.opened;
    sidebar.withoutAnimation = withoutAnimation;
    if (sidebar.opened) {
      sidebarStatus.value = '1';
    } else {
      sidebarStatus.value = '0';
    }
  };

  const closeSideBar = ({ withoutAnimation }: any): void => {
    sidebarStatus.value = '0';
    sidebar.opened = false;
    sidebar.withoutAnimation = withoutAnimation;
  };
  const toggleDevice = (d: string): void => {
    device.value = d;
  };
  const setSize = (s: 'large' | 'default' | 'small'): void => {
    size.value = s;
  };
  const toggleSideBarHide = (status: boolean): void => {
    sidebar.hide = status;
  };

  const changeLanguage = (val: string): void => {
    language.value = val;
  };

  // 强制设置为中文
  const forceZhCN = (): void => {
    language.value = 'zh_CN';
  };

  return {
    device,
    sidebar,
    language,
    locale,
    size,
    changeLanguage,
    forceZhCN,
    toggleSideBar,
    closeSideBar,
    toggleDevice,
    setSize,
    toggleSideBarHide,
    _l
  };
});

export default useAppStore;
