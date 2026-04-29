package com.zifang.z.config.starter;

import java.lang.annotation.*;

/**
 * z-config 配置监听注解
 * 标记在 Spring Bean 上，用于监听指定 dataId 的配置变更
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ZConfigListener {

    /**
     * 要监听的配置 dataId
     */
    String dataId();

    /**
     * 配置分组（可选）
     */
    String group() default "DEFAULT_GROUP";

    /**
     * 配置变更后的回调方法名
     * 方法签名要求：无返回值，参数为 String (新配置内容) 或无参数
     */
    String callbackMethod() default "onConfigChanged";

    /**
     * 是否在初始化时立即触发一次回调
     */
    boolean fireOnInit() default true;
}