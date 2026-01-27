package com.wudao.kms.entity.req;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeFileApproveReq {
    private List<Long> fileId;
    private String status;
}
