package com.wudao.kms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI助手消息节点表 - 树状结构管理
 * 每条消息作为一个节点，通过父子关系构建对话树
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "ai_assistant_message_node", autoResultMap = true)
@Schema(description = "AI助手消息节点表")
public class AiAssistantMessageNode extends BaseEntity {
    
    @TableId(type = IdType.INPUT)
    @Schema(description = "节点ID (UUID)")
    private String nodeId;
    
    @Schema(description = "父节点ID")
    private String parentNodeId;
    
    @Schema(description = "子节点ID列表，JSON格式存储")
    @TableField(typeHandler = JacksonTypeHandler.class, jdbcType = org.apache.ibatis.type.JdbcType.OTHER)
    private List<String> childrenNodeIds;
    
    @Schema(description = "会话UUID")
    private String sessionUuid;
    
    @Schema(description = "所属轮次ID")
    private String turnId;
    
    @Schema(description = "消息类型: USER-用户消息, ASSISTANT-助手回复, SYSTEM-系统消息")
    private String messageType;
    
    @Schema(description = "在该轮次中的版本序号，从0开始")
    private Integer versionIndex = 0;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "思考数据（reasoning内容）")
    private String reasoningData;
    
    @Schema(description = "文本数据（最终输出文本）")
    private String textData;
    
    @Schema(description = "输入Token数量")
    private Integer inputTokens;
    
    @Schema(description = "输出Token数量")  
    private Integer outputTokens;
    
    @Schema(description = "总Token数量")
    private Integer totalTokens;
    
    @Schema(description = "Token成本")
    private BigDecimal tokenCost;
    
    @Schema(description = "文件记录，JSON格式存储文件信息")
    @TableField(typeHandler = JacksonTypeHandler.class, jdbcType = org.apache.ibatis.type.JdbcType.OTHER)
    private List<Map<String, Object>> fileRecords;
    
    @Schema(description = "工具调用记录，JSON格式")
    @TableField(typeHandler = JacksonTypeHandler.class, jdbcType = org.apache.ibatis.type.JdbcType.OTHER)
    private List<Map<String, Object>> toolCallRecords;
    
    @Schema(description = "模型名称")
    private String modelName;
    
    @Schema(description = "模型参数配置，JSON格式")
    @TableField(typeHandler = JacksonTypeHandler.class, jdbcType = org.apache.ibatis.type.JdbcType.OTHER)
    private Map<String, Object> modelParams;
    
    @Schema(description = "处理耗时(毫秒)")
    private Long processingTimeMs;
    
    @Schema(description = "消息状态: SUCCESS-成功, ERROR-失败, TIMEOUT-超时")
    private String processStatus;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @Schema(description = "错误代码")
    private String errorCode;
    
    @Schema(description = "消息来源: WEB-网页, API-接口, MOBILE-移动端")
    private String sourceType;
    
    @Schema(description = "用户代理信息")
    private String userAgent;
    
    @Schema(description = "客户端IP")
    private String clientIp;
    
    @Schema(description = "扩展信息，JSON格式")
    @TableField(typeHandler = JacksonTypeHandler.class, jdbcType = org.apache.ibatis.type.JdbcType.OTHER)
    private Map<String, Object> extendInfo;
    
    @Schema(description = "是否删除")
    private Boolean delFlag = false;
    
    @Schema(description = "是否为活跃节点（用于版本切换）")
    private Boolean isActive = true;
    
    @Schema(description = "是否为最佳回复（用于标记优质回答）")
    private Boolean isBestResponse = false;
    
    @Schema(description = "是否为错误回复（用于标记问题回答）")
    private Boolean isErrorResponse = false;
    
    /**
     * 添加子节点ID
     */
    public void addChildNodeId(String childNodeId) {
        if (this.childrenNodeIds == null) {
            this.childrenNodeIds = new java.util.ArrayList<>();
        }
        if (!this.childrenNodeIds.contains(childNodeId)) {
            this.childrenNodeIds.add(childNodeId);
        }
    }
    
    /**
     * 确保获取到正确的子节点ID列表
     */
    public List<String> getChildrenNodeIds() {
        // 如果为null，初始化为空列表
        if (this.childrenNodeIds == null) {
            this.childrenNodeIds = new java.util.ArrayList<>();
        }
        return this.childrenNodeIds;
    }
    
    /**
     * 移除子节点ID
     */
    public void removeChildNodeId(String childNodeId) {
        if (this.childrenNodeIds != null) {
            this.childrenNodeIds.remove(childNodeId);
        }
    }
    
    /**
     * 检查是否有子节点
     */
    public boolean hasChildren() {
        return this.childrenNodeIds != null && !this.childrenNodeIds.isEmpty();
    }
    
    /**
     * 检查是否为根节点
     */
    public boolean isRoot() {
        return this.parentNodeId == null;
    }
    
    /**
     * 检查是否为叶子节点
     */
    public boolean isLeaf() {
        return !hasChildren();
    }
    
    /**
     * 检查是否为最佳回复
     */
    public boolean isBestResponse() {
        return Boolean.TRUE.equals(this.isBestResponse);
    }
    
    /**
     * 检查是否为错误回复
     */
    public boolean isErrorResponse() {
        return Boolean.TRUE.equals(this.isErrorResponse);
    }
    
    /**
     * 设置为最佳回复
     */
    public void markAsBestResponse() {
        this.isBestResponse = true;
        this.isErrorResponse = false; // 互斥，不能同时是最佳和错误
    }
    
    /**
     * 设置为错误回复
     */
    public void markAsErrorResponse() {
        this.isErrorResponse = true;
        this.isBestResponse = false; // 互斥，不能同时是错误和最佳
    }
    
    /**
     * 清除特殊标记
     */
    public void clearSpecialMarks() {
        this.isBestResponse = false;
        this.isErrorResponse = false;
    }
    
    /**
     * 检查是否有特殊标记
     */
    public boolean hasSpecialMark() {
        return isBestResponse() || isErrorResponse();
    }
}