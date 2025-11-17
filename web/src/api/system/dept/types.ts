/**
 * 部门查询参数
 */
export interface DeptQuery extends PageQuery {
  deptName?: string;
  deptCategory?: string;
  status?: number;
}

/**
 * 部门类型
 */
export interface DeptVO extends BaseEntity {
  id: number | string;
  parentName: string;
  parentId: number | string;
  children: DeptVO[];
  deptId: number | string;
  deptName: string;
  deptCategory: string;
  orderNum: number;
  leader: string;
  phone: string;
  email: string;
  status: number;
  deleted: boolean;
  ancestors: string;
  menuId: string | number;
}

/**
 * 部门表单类型
 */
export interface DeptForm {
  parentName?: string;
  pid?: number | string;
  children?: DeptForm[];
  id?: number | string;
  name?: string;
  code?: string;
  sort?: number;
  leaderId?: string;
  phone?: string;
  email?: string;
  status?: string;
  deleted?: boolean;
  ancestors?: string;
}
