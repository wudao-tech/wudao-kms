package com.wudao.kms.util.pai;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.pai_dlc20201203.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PAI DLC 配置类
 */
@Configuration
@RequiredArgsConstructor
public class PaiDlcConfig {
    
    private final PaiDlcPropertiesConfig dlcPropertiesConfig;

    @Bean("asyncClient")
    public AsyncClient asyncClient() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(dlcPropertiesConfig.getAccessKey())
                .accessKeySecret(dlcPropertiesConfig.getSecretKey())
                .build());

        return AsyncClient.builder()
                .region(dlcPropertiesConfig.getRegion()) // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(dlcPropertiesConfig.getEndpoint())
                ).build();
    }
}