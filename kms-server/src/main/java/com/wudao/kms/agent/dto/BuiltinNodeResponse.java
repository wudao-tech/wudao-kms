package com.wudao.kms.agent.dto;

import com.wudao.kms.agent.dto.node.NodeConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuiltinNodeResponse {
    private String nodeName;

    private List<NodeConfigDTO> nodeConfigs;
}
