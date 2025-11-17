import { MenuTypeEnum } from '@/enums/MenuTypeEnum';

/**
 * 菜单树形结构类型
 */
export interface MenuTreeOption {
  id: string | number;
  label: string;
  pid: string | number;
  weight: number;
  children?: MenuTreeOption[];
}

export interface RoleMenuTree {
  menus: MenuTreeOption[];
  checkedKeys: string[];
}

/**
 * 菜单查询参数类型
 */
export interface MenuQuery {
  keywords?: string;
  name?: string;
  status?: string;
}

/**
 * 菜单视图对象类型
 */
export interface MenuVO extends BaseEntity {
  parentName: string;
  pid: string | number;
  children: MenuVO[];
  id: string | number;
  name: string;
  sort: number;
  path: string;
  component: string;
  queryParam: string;
  isFrame: boolean;
  isCache: boolean;
  menuType: MenuTypeEnum;
  hidden: boolean;
  status: number;
  icon: string;
  remark: string;
}

export interface MenuForm {
  parentName?: string;
  pid?: string | number;
  children?: MenuForm[];
  id?: string | number;
  name: string;
  sort: number;
  path: string;
  component?: string;
  queryParam?: string;
  isFrame?: boolean;
  isCache?: boolean;
  menuType?: MenuTypeEnum;
  hidden?: boolean;
  status?: number;
  icon?: string;
  remark?: string;
  query?: string;
  perms?: string;
}
