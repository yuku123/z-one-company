package com.zifang.z.config.starter;

import com.zifang.z.config.client.config.ZConfigService;
import com.zifang.z.config.client.config.listener.ZConfigListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * ZConfig 的 Spring 集成处理器
 * 扫描 {@link com.zifang.z.config.starter.ZConfigListener} 注解，为 Bean 注册配置监听
 */
@Component
public class ZConfigBeanPostProcessor implements BeanPostProcessor, EnvironmentAware {

    // 注入你的 z-config 核心客户端（需替换为实际的客户端类）
    private final ZConfigService zConfigClient;
    private Environment environment;

    // 构造函数注入 z-config 客户端
    public ZConfigBeanPostProcessor(ZConfigService zConfigClient) {
        this.zConfigClient = zConfigClient;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 扫描类上的 ZConfigListener 注解
        com.zifang.z.config.starter.ZConfigListener annotation = bean.getClass().getAnnotation(com.zifang.z.config.starter.ZConfigListener.class);
        if (annotation != null) {
            processZConfigListener(bean, annotation);
        }
        return bean;
    }

    /**
     * 处理配置监听注解，注册监听器
     */
    private void processZConfigListener(Object bean, com.zifang.z.config.starter.ZConfigListener annotation) {
        // 解析注解属性（支持 Spring EL 表达式解析）
        String dataId = environment.resolvePlaceholders(annotation.dataId());
        String group = environment.resolvePlaceholders(annotation.group());
        String callbackMethodName = annotation.callbackMethod();
        boolean fireOnInit = annotation.fireOnInit();

        // 查找回调方法
        Method callbackMethod = findCallbackMethod(bean.getClass(), callbackMethodName);
        if (callbackMethod == null) {
            throw new IllegalArgumentException(
                    String.format("Bean %s 中未找到配置变更回调方法: %s",
                            bean.getClass().getName(), callbackMethodName));
        }

        // 设置方法可访问（即使是私有方法）
        callbackMethod.setAccessible(true);

        // 注册配置监听器到 z-config 客户端
        zConfigClient.addListener(dataId, group, new ZConfigListener() {
            @Override
            public void receiveConfigInfo(String newConfig) {
                try {
                    // 根据方法参数类型调用回调
                    Class<?>[] parameterTypes = callbackMethod.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        callbackMethod.invoke(bean);
                    } else if (parameterTypes.length == 1 && parameterTypes[0] == String.class) {
                        callbackMethod.invoke(bean, newConfig);
                    } else {
                        throw new IllegalArgumentException(
                                "回调方法参数不支持，仅支持无参或单个 String 参数");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("执行配置变更回调方法失败", e);
                }
            }
        });

        // 初始化时触发一次回调
        if (fireOnInit) {
            String initialConfig = zConfigClient.getConfig(dataId, group, 0L).getData();
            try {
                Class<?>[] parameterTypes = callbackMethod.getParameterTypes();
                if (parameterTypes.length == 0) {
                    callbackMethod.invoke(bean);
                } else if (parameterTypes.length == 1 && parameterTypes[0] == String.class) {
                    callbackMethod.invoke(bean, initialConfig);
                }
            } catch (Exception e) {
                throw new RuntimeException("初始化触发配置回调方法失败", e);
            }
        }
    }

    /**
     * 查找回调方法（支持父类方法）
     */
    private Method findCallbackMethod(Class<?> beanClass, String methodName) {
        try {
            return beanClass.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            // 尝试查找带 String 参数的方法
            try {
                return beanClass.getDeclaredMethod(methodName, String.class);
            } catch (NoSuchMethodException ex) {
                // 递归查找父类
                Class<?> superClass = beanClass.getSuperclass();
                if (superClass != null && superClass != Object.class) {
                    return findCallbackMethod(superClass, methodName);
                }
                return null;
            }
        }
    }
}