package com.wudao.kms.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient配置
 */
@Configuration
public class WebClientConfig {

//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder()
//                .codecs(configurer -> configurer
//                        .defaultCodecs()
//                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB
//                .build();
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}