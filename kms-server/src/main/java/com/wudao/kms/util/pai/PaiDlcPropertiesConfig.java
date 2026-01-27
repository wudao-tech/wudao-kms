package com.wudao.kms.util.pai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("pai.dlc")
@Schema(description = "pai dlc的配置项")
public class PaiDlcPropertiesConfig {
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private String ossPrefix;
}
