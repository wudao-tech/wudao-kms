export interface DictTypeVO extends BaseEntity {
  type: number | string;
  name: string;
  category: string;
  remark: string;
}

export interface DictTypeForm {
  type: number | string | undefined;
  name: string;
  category: string;
  remark: string;
}

export interface DictTypeQuery extends PageQuery {
  name: string;
  category: string;
}
