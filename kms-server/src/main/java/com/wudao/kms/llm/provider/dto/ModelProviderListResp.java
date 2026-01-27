package com.wudao.kms.llm.provider.dto;

import com.wudao.kms.llm.provider.domain.ModelProvider;
import lombok.Data;

@Data
public class ModelProviderListResp extends ModelProvider {
    private Integer modelCount;
    private String createByName;
}
