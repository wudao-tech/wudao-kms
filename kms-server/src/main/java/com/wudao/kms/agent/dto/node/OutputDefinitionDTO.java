package com.wudao.kms.agent.dto.node;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 节点输出定义DTO
 * 用于描述节点的输出结构，支持复杂嵌套类型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutputDefinitionDTO {
    
    @Schema(description = "输出字段名称")
    private String fieldName;
    
    @Schema(description = "显示名称")
    private String displayName;
    
    @Schema(description = "字段类型：STRING, NUMBER, BOOLEAN, OBJECT, LIST, JSON_OBJECT")
    private String fieldType;
    
    @Schema(description = "字段描述")
    private String description;
    
    @Schema(description = "是否必定存在")
    private boolean required;
    
    @Schema(description = "当fieldType为OBJECT时，定义对象的属性结构")
    private List<OutputDefinitionDTO> properties;
    
    @Schema(description = "当fieldType为LIST时，定义列表元素的类型结构")
    private OutputDefinitionDTO itemType;
    
    @Schema(description = "示例值")
    private Object example;
    
    /**
     * 创建基本类型输出定义
     */
    public static OutputDefinitionDTO createBasicType(String fieldName, String displayName, String fieldType, String description) {
        return OutputDefinitionDTO.builder()
                .fieldName(fieldName)
                .displayName(displayName)
                .fieldType(fieldType)
                .description(description)
                .required(true)
                .build();
    }
    
    /**
     * 创建对象类型输出定义
     */
    public static OutputDefinitionDTO createObjectType(String fieldName, String displayName, String description, List<OutputDefinitionDTO> properties) {
        return OutputDefinitionDTO.builder()
                .fieldName(fieldName)
                .displayName(displayName)
                .fieldType("OBJECT")
                .description(description)
                .required(true)
                .properties(properties)
                .build();
    }
    
    /**
     * 创建列表类型输出定义
     */
    public static OutputDefinitionDTO createListType(String fieldName, String displayName, String description, OutputDefinitionDTO itemType) {
        return OutputDefinitionDTO.builder()
                .fieldName(fieldName)
                .displayName(displayName)
                .fieldType("LIST")
                .description(description)
                .required(true)
                .itemType(itemType)
                .build();
    }
}