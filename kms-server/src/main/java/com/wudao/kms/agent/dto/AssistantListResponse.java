package com.wudao.kms.agent.dto;

import com.wudao.kms.agent.domain.Assistant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssistantListResponse extends Assistant {
    private String createByName;
    private Boolean collected;
}
