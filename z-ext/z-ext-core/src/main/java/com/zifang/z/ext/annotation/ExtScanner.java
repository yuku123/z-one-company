package com.zifang.z.ext.annotation;

import com.zifang.z.ext.core.registry.ExtImplDefinition;
import com.zifang.z.ext.core.registry.ExtPointDefinition;
import com.zifang.z.ext.core.registry.ExtRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * 扩展点扫描器
 * 扫描并注册@ExtPoint和@ExtImpl注解标记的类
 */
public class ExtScanner implements BeanFactoryPostProcessor, ApplicationContextAware {

    private String[] basePackages;
    private ApplicationContext applicationContext;

    public ExtScanner(String[] basePackages) {
        this.basePackages = basePackages;
    }

    public ExtScanner() {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 注册后置处理器来扫描
        // 这里简化处理，实际应该使用ClassPathScanningCandidateComponentProvider
        scanExtPoints();
        scanExtImpls();
    }

    private void scanExtPoints() {
        if (basePackages == null || basePackages.length == 0) {
            return;
        }

        for (String basePackage : basePackages) {
            Set<Class<?>> classes = scanClasses(basePackage, ExtPoint.class);
            for (Class<?> clazz : classes) {
                registerExtPoint(clazz);
            }
        }
    }

    private void scanExtImpls() {
        if (basePackages == null || basePackages.length == 0) {
            return;
        }

        for (String basePackage : basePackages) {
            Set<Class<?>> classes = scanClasses(basePackage, ExtImpl.class);
            for (Class<?> clazz : classes) {
                registerExtImpl(clazz);
            }
        }
    }

    private void registerExtPoint(Class<?> clazz) {
        ExtPoint annotation = clazz.getAnnotation(ExtPoint.class);

        ExtPointDefinition definition = new ExtPointDefinition();
        definition.setPoint(annotation.value());
        definition.setInterfaceClass(clazz);
        definition.setType(annotation.type());
        definition.setDescription(annotation.description());
        definition.setVersion(annotation.version());
        definition.setOrder(annotation.order());

        ExtRegistry.registerPoint(definition);

        System.out.println("[Ext] Registered extension point: " + annotation.value());
    }

    private void registerExtImpl(Class<?> clazz) {
        ExtImpl annotation = clazz.getAnnotation(ExtImpl.class);

        ExtImplDefinition definition = new ExtImplDefinition();
        definition.setPoint(annotation.point());
        definition.setName(annotation.name());
        definition.setImplClass(clazz);
        definition.setType(annotation.type());
        definition.setDescription(annotation.description());
        definition.setOrder(annotation.order());
        definition.setEnabled(annotation.enabled());
        definition.setCondition(annotation.condition());
        definition.setWeight(annotation.weight());

        // 检查是否配置了RPC地址
        // 可以从配置文件或环境变量读取
        String rpcAddress = System.getProperty("ext.rpc." + annotation.point() + "." + annotation.name() + ".address");
        String rpcPort = System.getProperty("ext.rpc." + annotation.point() + "." + annotation.name() + ".port");

        if (rpcAddress != null) {
            definition.setRpcAddress(rpcAddress);
            definition.setRpcPort(rpcPort != null ? Integer.parseInt(rpcPort) : 8080);
        }

        ExtRegistry.registerImpl(definition);

        System.out.println("[Ext] Registered implementation: " + annotation.point() + " -> " + annotation.name());
    }

    /**
     * 简单的类扫描实现
     * 实际项目中可以使用Spring的ClassPathScanningCandidateComponentProvider
     */
    private Set<Class<?>> scanClasses(String basePackage, Class<? extends Annotation> annotationClass) {
        // 这里简化处理，返回空集
        // 实际需要使用类扫描器
        // 可以使用: org.springframework.core.type.classreading.CachingMetadataReaderFactory
        return new HashSet<>();
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}