package com.wudao.kms.agent.node.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 节点定义注解
 * 用于替代 NodeFactory 的元数据功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface NodeDef {
    
    /**
     * 节点类型 (如: BRANCH, LLM)
     */
    String type();

    /**
     * 节点名称
     */
    String name() default "";

    /**
     * 节点描述
     */
    String description() default "";
}