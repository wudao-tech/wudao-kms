package com.wudao.kms.llm.provider.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ModelProvider extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("provider_code")
    @Schema(description = "code，目前qwen,deepseek,zhipu,moonshot,openai,ollama")
    private String providerCode;

    @TableField("type")
    @Schema(description = "云服务商还是其他")
    private String type;

    @TableField("api_key")
    private String apiKey;

    @TableField("endpoint")
    @Schema(description = "openai|ollama使用，端点|baseUrl")
    private String endpoint;

    @TableField("completions_path")
    @Schema(description = "openai使用，后缀")
    private String completionsPath;

    @TableField("status")
    @Schema(description = "测试连接状态")
    private String status;
}
