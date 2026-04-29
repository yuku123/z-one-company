package com.zifang.z.rpc.spring.boot.starter.autoconfigure;

import com.zifang.z.rpc.annotation.ZRpcReference;
import com.zifang.z.rpc.annotation.ZRpcService;
import com.zifang.z.rpc.config.ReferenceConfig;
import com.zifang.z.rpc.config.ServiceConfig;
import com.zifang.z.rpc.spring.boot.starter.properties.ZRpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Z-RPC 自动配置类
 */
@Configuration
@ConditionalOnClass({ServiceConfig.class, ReferenceConfig.class})
@EnableConfigurationProperties(ZRpcProperties.class)
@ConditionalOnProperty(prefix = "z.rpc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ZRpcAutoConfiguration {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZRpcProperties zRpcProperties;

    /**
     * 服务 Bean 后处理器
     */
    @Bean
    public ZRpcServiceBeanPostProcessor zRpcServiceBeanPostProcessor() {
        return new ZRpcServiceBeanPostProcessor(zRpcProperties);
    }

    /**
     * 引用 Bean 后处理器
     */
    @Bean
    public ZRpcReferenceBeanPostProcessor zRpcReferenceBeanPostProcessor() {
        return new ZRpcReferenceBeanPostProcessor(zRpcProperties);
    }

    /**
     * Z-RPC 服务注解处理器
     */
    public static class ZRpcServiceBeanPostProcessor implements BeanPostProcessor {

        private final Logger log = LoggerFactory.getLogger(this.getClass());

        private final ZRpcProperties properties;
        private final Map<String, ServiceConfig<?>> serviceConfigs = new ConcurrentHashMap<>();

        public ZRpcServiceBeanPostProcessor(ZRpcProperties properties) {
            this.properties = properties;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            // 查找类上的 @ZRpcService 注解
            ZRpcService serviceAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), ZRpcService.class);

            if (serviceAnnotation != null) {
                log.info("Found ZRpcService on bean: {}, class: {}", beanName, bean.getClass().getName());
                exportService(bean, serviceAnnotation);
            }

            return bean;
        }

        private void exportService(Object bean, ZRpcService annotation) {
            try {
                ServiceConfig<Object> serviceConfig = new ServiceConfig<>();

                // 设置接口类
                Class<?> interfaceClass = annotation.interfaceClass();
                if (interfaceClass == void.class) {
                    // 如果没有指定接口，尝试从实现类获取
                    Class<?>[] interfaces = bean.getClass().getInterfaces();
                    if (interfaces.length > 0) {
                        interfaceClass = interfaces[0];
                    } else {
                        throw new IllegalStateException("No interface found for service: " + bean.getClass());
                    }
                }
                serviceConfig.setInterfaceClass((Class<Object>) interfaceClass);
                serviceConfig.setRef(bean);

                // 设置基本属性
                serviceConfig.setVersion(annotation.version());
                serviceConfig.setGroup(annotation.group());
                serviceConfig.setWeight(annotation.weight());
                serviceConfig.setDelay(annotation.delay());
                serviceConfig.setTimeout(annotation.timeout());
                serviceConfig.setRetries(annotation.retries());
                serviceConfig.setLoadbalance(annotation.loadbalance());
                serviceConfig.setCluster(annotation.cluster());

                // 设置网络和注册中心配置
                ZRpcProperties.ServerConfig serverConfig = properties.getServer();
                serviceConfig.setPort(serverConfig.getPort());
                serviceConfig.setHost(serverConfig.getHost());
                serviceConfig.setThreads(serverConfig.getThreads());

                if (properties.getRegistry().isEnabled()) {
                    serviceConfig.setRegistry(properties.getRegistry().getAddress());
                }

                // 导出服务
                serviceConfig.export();

                // 保存配置引用
                serviceConfigs.put(interfaceClass.getName(), serviceConfig);

                log.info("Service exported successfully: {}:{}", interfaceClass.getName(), annotation.version());

            } catch (Exception e) {
                log.error("Failed to export service: {}", bean.getClass(), e);
                throw new RuntimeException("Failed to export service", e);
            }
        }
    }

    /**
     * Z-RPC 引用注解处理器
     */
    public static class ZRpcReferenceBeanPostProcessor implements BeanPostProcessor {

        private final Logger log = LoggerFactory.getLogger(this.getClass());

        private final ZRpcProperties properties;

        public ZRpcReferenceBeanPostProcessor(ZRpcProperties properties) {
            this.properties = properties;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            // 查找字段上的 @ZRpcReference 注解
            Class<?> clazz = bean.getClass();
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    ZRpcReference reference = field.getAnnotation(ZRpcReference.class);
                    if (reference != null) {
                        Object proxy = createProxy(field.getType(), reference);
                        try {
                            field.setAccessible(true);
                            field.set(bean, proxy);
                            log.info("Injected ZRpcReference proxy for field: {}.{}",
                                    clazz.getSimpleName(), field.getName());
                        } catch (IllegalAccessException e) {
                            log.error("Failed to inject ZRpcReference proxy", e);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            // 查找 setter 方法上的 @ZRpcReference 注解
            clazz = bean.getClass();
            while (clazz != null) {
                for (Method method : clazz.getDeclaredMethods()) {
                    ZRpcReference reference = method.getAnnotation(ZRpcReference.class);
                    if (reference != null && isSetterMethod(method)) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        Object proxy = createProxy(paramType, reference);
                        try {
                            method.setAccessible(true);
                            method.invoke(bean, proxy);
                            log.info("Injected ZRpcReference proxy for method: {}.{}",
                                    clazz.getSimpleName(), method.getName());
                        } catch (Exception e) {
                            log.error("Failed to inject ZRpcReference proxy", e);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            return bean;
        }

        private boolean isSetterMethod(Method method) {
            return method.getName().startsWith("set")
                    && method.getParameterCount() == 1
                    && method.getReturnType() == void.class;
        }

        @SuppressWarnings("unchecked")
        private <T> T createProxy(Class<T> interfaceClass, ZRpcReference reference) {
            try {
                ReferenceConfig<T> referenceConfig = new ReferenceConfig<>();

                // 设置接口类
                Class<?> refInterfaceClass = reference.interfaceClass();
                if (refInterfaceClass == void.class) {
                    referenceConfig.setInterfaceClass(interfaceClass);
                } else {
                    referenceConfig.setInterfaceClass((Class<T>) refInterfaceClass);
                }

                // 设置基本属性
                referenceConfig.setVersion(reference.version());
                referenceConfig.setGroup(reference.group());
                referenceConfig.setTimeout(reference.timeout());
                referenceConfig.setRetries(reference.retries());
                referenceConfig.setLoadbalance(reference.loadbalance());
                referenceConfig.setCluster(reference.cluster());
                referenceConfig.setAsync(reference.async());
                referenceConfig.setOneway(reference.oneway());



                // 设置注册中心配置
                ZRpcProperties.ConsumerConfig consumerConfig = properties.getConsumer();
                referenceConfig.setTimeout(consumerConfig.getTimeout());
                referenceConfig.setRetries(consumerConfig.getRetries());
                referenceConfig.setLoadbalance(consumerConfig.getLoadbalance());
                referenceConfig.setCluster(consumerConfig.getCluster());

                if (properties.getRegistry().isEnabled()) {
                    referenceConfig.setRegistry(properties.getRegistry().getAddress());
                }

                // 获取代理
                return referenceConfig.get();

            } catch (Exception e) {
                log.error("Failed to create ZRpcReference proxy for: {}", interfaceClass, e);
                throw new RuntimeException("Failed to create ZRpcReference proxy", e);
            }
        }
    }
}
