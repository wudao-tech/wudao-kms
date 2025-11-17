package com.wudao.kms.agent.dto.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeParameterDTO {
    private String paramName;
    private String paramType; // STRING, LIST, BOOLEAN, etc.
    private String description;
    private boolean required;
    private Object defaultValue;
    private List<String> options; // for enum-like parameters
}
