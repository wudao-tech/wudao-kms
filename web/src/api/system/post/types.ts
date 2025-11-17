export interface PostVO extends BaseEntity {
  postId: number | string;
  deptId: number | string;
  postCode: string;
  postName: string;
  postCategory: string;
  deptName: string;
  postSort: number;
  status: number;
  remark: string;
}

export interface PostForm {
  postId: number | string | undefined;
  deptId: number | string | undefined;
  postCode: string;
  postName: string;
  postCategory: string;
  postSort: number;
  status: number;
  remark: string;
}

export interface PostQuery extends PageQuery {
  deptId: number | string;
  belongDeptId: number | string;
  postCode: string;
  postName: string;
  postCategory: string;
  status: number;
}
