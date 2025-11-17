package com.wudao.kms.agent.dto.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 输出定义构建器
 * 提供便利的方法来构建复杂的输出结构定义
 */
public class OutputDefinitionBuilder {
    
    private List<OutputDefinitionDTO> outputs = new ArrayList<>();
    
    /**
     * 添加基本类型字段
     */
    public OutputDefinitionBuilder addBasicField(String fieldName, String displayName, String fieldType, String description) {
        outputs.add(OutputDefinitionDTO.createBasicType(fieldName, displayName, fieldType, description));
        return this;
    }
    
    /**
     * 添加字符串字段
     */
    public OutputDefinitionBuilder addStringField(String fieldName, String displayName, String description) {
        return addBasicField(fieldName, displayName, "STRING", description);
    }
    
    /**
     * 添加数字字段
     */
    public OutputDefinitionBuilder addNumberField(String fieldName, String displayName, String description) {
        return addBasicField(fieldName, displayName, "NUMBER", description);
    }
    
    /**
     * 添加布尔字段
     */
    public OutputDefinitionBuilder addBooleanField(String fieldName, String displayName, String description) {
        return addBasicField(fieldName, displayName, "BOOLEAN", description);
    }
    
    /**
     * 添加对象字段
     */
    public OutputDefinitionBuilder addObjectField(String fieldName, String displayName, String description, OutputDefinitionDTO... properties) {
        outputs.add(OutputDefinitionDTO.createObjectType(fieldName, displayName, description, Arrays.asList(properties)));
        return this;
    }
    
    /**
     * 添加对象字段（使用列表）
     */
    public OutputDefinitionBuilder addObjectField(String fieldName, String displayName, String description, List<OutputDefinitionDTO> properties) {
        outputs.add(OutputDefinitionDTO.createObjectType(fieldName, displayName, description, properties));
        return this;
    }
    
    /**
     * 添加列表字段
     */
    public OutputDefinitionBuilder addListField(String fieldName, String displayName, String description, OutputDefinitionDTO itemType) {
        outputs.add(OutputDefinitionDTO.createListType(fieldName, displayName, description, itemType));
        return this;
    }
    
    /**
     * 添加字符串列表
     */
    public OutputDefinitionBuilder addStringListField(String fieldName, String displayName, String description) {
        OutputDefinitionDTO stringItem = OutputDefinitionDTO.createBasicType("item", "列表项", "STRING", "字符串项");
        return addListField(fieldName, displayName, description, stringItem);
    }
    
    /**
     * 添加对象列表
     */
    public OutputDefinitionBuilder addObjectListField(String fieldName, String displayName, String description, OutputDefinitionDTO... itemProperties) {
        OutputDefinitionDTO objectItem = OutputDefinitionDTO.createObjectType("item", "列表项", "对象项", Arrays.asList(itemProperties));
        return addListField(fieldName, displayName, description, objectItem);
    }
    
    /**
     * 构建输出定义列表
     */
    public List<OutputDefinitionDTO> build() {
        return new ArrayList<>(outputs);
    }
    
    /**
     * 创建新的构建器实例
     */
    public static OutputDefinitionBuilder create() {
        return new OutputDefinitionBuilder();
    }
    
    /**
     * 示例：创建复杂的输出结构
     * 展示如何定义：
     * - queryCode: String
     * - message: String  
     * - result: Object(name: String, id: Number, items: List<Object(name: String, value: String)>)
     * - userList: List<Object(name: String, email: String)>
     */
    public static List<OutputDefinitionDTO> createComplexExample() {
        // 定义result对象中items列表的元素结构
        OutputDefinitionDTO itemObject = OutputDefinitionDTO.createObjectType("item", "项目", "单个项目对象", 
            Arrays.asList(
                OutputDefinitionDTO.createBasicType("name", "名称", "STRING", "项目名称"),
                OutputDefinitionDTO.createBasicType("value", "值", "STRING", "项目值")
            )
        );
        
        // 定义result对象的属性结构
        List<OutputDefinitionDTO> resultProperties = Arrays.asList(
            OutputDefinitionDTO.createBasicType("name", "名称", "STRING", "结果名称"),
            OutputDefinitionDTO.createBasicType("id", "ID", "NUMBER", "结果ID"),
            OutputDefinitionDTO.createListType("items", "项目列表", "包含的项目列表", itemObject)
        );
        
        // 定义userList列表的元素结构  
        OutputDefinitionDTO userObject = OutputDefinitionDTO.createObjectType("user", "用户", "用户对象",
            Arrays.asList(
                OutputDefinitionDTO.createBasicType("name", "姓名", "STRING", "用户姓名"),
                OutputDefinitionDTO.createBasicType("email", "邮箱", "STRING", "用户邮箱")
            )
        );
        
        // 构建完整的输出定义
        return OutputDefinitionBuilder.create()
                .addStringField("queryCode", "查询代码", "执行的查询代码")
                .addStringField("message", "消息", "执行结果消息")
                .addObjectField("result", "结果", "详细的结果对象", resultProperties)
                .addListField("userList", "用户列表", "用户对象列表", userObject)
                .addBooleanField("success", "执行状态", "是否执行成功")
                .addNumberField("timestamp", "时间戳", "执行时间戳")
                .build();
    }
}