export interface LoginInfoVO {
  infoId: string | number;
  tenantId: string | number;
  userName: string;
  status: number;
  ipaddr: string;
  loginLocation: string;
  browser: string;
  os: string;
  msg: string;
  loginTime: string;
}

export interface LoginInfoQuery extends PageQuery {
  ipaddr: string;
  userName: string;
  status: number;
  orderByColumn: string;
  isAsc: string;
}
