package com.wudao.kms.agent.node.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 节点属性定义
 * 包含前端展示所需的元数据、输入输出定义等
 */
@Data
@Accessors(chain = true)
public class NodeAttribute {
    
    /**
     * 节点图标
     */
    private String icon;
    
    /**
     * 节点颜色
     */
    private String color;
    
    /**
     * 输入参数定义
     */
    private List<NodeField> inputs;
    
    /**
     * 输出参数定义
     */
    private List<NodeField> outputs;
    
    /**
     * 前端表单配置 (JSON Schema 或其他格式)
     */
    private Object formConfig;
}