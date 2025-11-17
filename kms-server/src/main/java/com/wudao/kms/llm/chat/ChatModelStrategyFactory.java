package com.wudao.kms.llm.chat;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ChatModelStrategyFactory {

    @Resource
    private List<ChatModelStrategy> chatModelStrategies;

    /**
     * 根据厂商code获取对应的聊天策略
     * 
     * @param provideCode 厂商code
     * @return 对应的聊天策略实现
     * @throws IllegalArgumentException 当没有找到支持的策略时
     */
    public ChatModelStrategy getStrategy(String provideCode) {
        if (provideCode == null || provideCode.trim().isEmpty()) {
            log.warn(" 厂商code为空，使用默认策略");
            return getDefaultStrategy();
        }

        for (ChatModelStrategy strategy : chatModelStrategies) {
            if (strategy.supports(provideCode)) {
                return strategy;
            }
        }
        return getDefaultStrategy();
    }

    /**
     * 获取默认策略（DashScope）
     * 
     * @return 默认的聊天策略
     */
    private ChatModelStrategy getDefaultStrategy() {
        return chatModelStrategies.stream()
                .filter(strategy -> strategy.getClass().getSimpleName().contains("DashScope"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到默认的DashScope策略"));
    }

    /**
     * 获取所有可用的策略列表
     * 
     * @return 策略列表
     */
    public List<ChatModelStrategy> getAllStrategies() {
        return chatModelStrategies;
    }
}