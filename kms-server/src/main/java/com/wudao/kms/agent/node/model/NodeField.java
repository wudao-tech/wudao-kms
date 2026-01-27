package com.wudao.kms.agent.node.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 节点字段定义 (用于输入/输出)
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class NodeField {
    /**
     * 字段标识
     */
    private String key;
    
    /**
     * 字段显示名称
     */
    private String label;
    
    /**
     * 字段类型 (string, boolean, flow, object, etc.)
     */
    private String type;
    
    /**
     * 字段描述
     */
    private String description;

    public NodeField(String key, String label, String type) {
        this.key = key;
        this.label = label;
        this.type = type;
    }
}