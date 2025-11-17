package com.wudao.kms.agent.dto.node;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 节点dto给前端展示使用
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NodeConfigDTO {
    /**
     * 兼容旧版本的构造函数，不包含outputs参数
     */
    public NodeConfigDTO(String nodeName, String nodeType, String description, String nodeGroup,
                        List<NodeParameterDTO> parameters, Map<String, Object> defaultValues, Integer order) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.description = description;
        this.nodeGroup = nodeGroup;
        this.parameters = parameters;
        this.defaultValues = defaultValues;
        this.order = order;
        this.outputs = null; // 旧版本不包含outputs
    }
    private String nodeName;
    private String nodeType;
    private String description;
    @Schema(description = "节点分组，用于前端分类展示")
    private String nodeGroup;
    @Schema(description = "输入参数定义")
    private List<NodeParameterDTO> parameters;
    @Schema(description = "输出结构定义")
    private List<OutputDefinitionDTO> outputs;
    private Map<String, Object> defaultValues;
    @Schema(description = "排序")
    private Integer order;
}
