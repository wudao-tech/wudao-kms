export interface NoticeVO extends BaseEntity {
  noticeId: number;
  noticeTitle: string;
  noticeType: string;
  noticeContent: string;
  status: number;
  remark: string;
  createdByName: string;
}

export interface NoticeQuery extends PageQuery {
  noticeTitle: string;
  createdByName: string;
  status: number;
  noticeType: string;
}

export interface NoticeForm {
  noticeId: number | string | undefined;
  noticeTitle: string;
  noticeType: string;
  noticeContent: string;
  status: number;
  remark: string;
  createdByName: string;
}
