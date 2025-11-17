package com.wudao.kms.dto.knowledgefile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeTempDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    // 知识库id
    private Long knowledgeBaseId;
    // 知识空间id
    private Long knowledgeSpaceId;
    // 用户id
    private Long userId;
    // 文件上传临时信息
    private List<KnowledgeFileUploadTempDTO> knowledgeFileUploadTempDTOList;
    // 知识分段配置
    private KnowLedgeSegmentConfig knowLedgeSegmentConfig;
    private String createType;
}
