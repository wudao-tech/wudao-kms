package com.wudao.kms.llm.llmmode.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import com.wudao.kms.llm.llmmode.enums.ModelTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 大模型实体类
 *
 * @author wudao
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("llm_model")
public class LLMModel extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 模型
     */
    @TableField("model")
    private String model;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 提供方
     */
    @TableField("provider")
    private String provider;

    /**
     * 模型类型
     */
    @TableField("model_type")
    private ModelTypeEnum modelType;

    /**
     * API地址
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * 是否需要用户token
     */
    @TableField("need_user_token_flag")
    private Boolean needUserTokenFlag;

    /**
     * 状态：0-禁用，1-启用，2-维护中
     */
    @TableField("status")
    private Integer status;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    @Schema(description = "模型icon")
    private String modelIcon;
    @Schema(description = "多模态模型")
    private Boolean multimodal = false;
    @Schema(description = "联网搜索")
    private Boolean webSearch = false;
    @Schema(description = "深度思考模型")
    private Boolean deepThinkingModel = false;
    @Schema(description = "厂商code")
    private String providerCode;
    @Schema(description = "文档")
    private String document;
    @Schema(description = "token长度：1、2k以下 2、2-4 3、4k-8k 4、8k-32k、5、32k以上")
    private Integer tokenType;
    @Schema(description = "最大token长度")
    private Integer maxTokens;
    @Schema(description = "是否流式输出")
    private Boolean streamFlag = true;
}