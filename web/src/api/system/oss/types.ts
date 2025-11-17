export interface OssVO extends BaseEntity {
  ossId: string | number;
  fileName: string;
  originalName: string;
  fileSuffix: string;
  url: string;
  createdByName: string;
  service: string;
}

export interface OssQuery extends PageQuery {
  fileName: string;
  originalName: string;
  fileSuffix: string;
  createdAt: string;
  service: string;
  orderByColumn: string;
  isAsc: string;
}
export interface OssForm {
  file: undefined | string;
}
