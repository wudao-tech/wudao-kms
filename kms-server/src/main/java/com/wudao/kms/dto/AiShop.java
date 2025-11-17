package com.wudao.kms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AiShop{
    private Long id;

    private String assistantId;

    private String name;

    private String description;

    private String logo;

    private Integer tradeId;

    private Integer subjectId;

    private String tag;

    private Integer sourceType; // 1-官方, 2-用户

    private Integer commentCount;

    private Integer collectCount;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableField(exist = false)
    private Page<AiShop> page;

}