export interface ConfigVO extends BaseEntity {
  code: string;
  name: string;
  value: string;
  type: number;
  remark: string;
}

export interface ConfigForm {
  code: string | undefined;
  name: string;
  value: string;
  type: number;
  remark: string;
}

export interface ConfigQuery extends PageQuery {
  name: string;
  code: string;
  type: number;
}
