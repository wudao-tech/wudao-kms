export interface DictDataQuery extends PageQuery {
  name: string;
  category: string;
  label: string;
}

export interface DictDataVO extends BaseEntity {
  dictCode: string;
  label: string;
  value: string;
  cssClass: string;
  listClass: ElTagType;
  dictSort: number;
  remark: string;
}

export interface DictDataForm {
  category?: string;
  dictCode: string | undefined;
  label: string;
  value: string;
  cssClass: string;
  listClass: ElTagType;
  dictSort: number;
  remark: string;
}
