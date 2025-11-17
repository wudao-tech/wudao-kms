package com.wudao.kms.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自主配置数据库")
public class AssistantDbRequest {
    private Long datasourceId;
    private List<String> tables;
}
