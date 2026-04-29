package com.zifang.ctc;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


// bean的生命周期 https://blog.csdn.net/qq_38974073/article/details/131905145
@Component
public class TestComponent implements InitializingBean, DisposableBean {

    // 在属性设置后调用
    @Override
    public void afterPropertiesSet() {
        System.out.println("InitializingBean's afterPropertiesSet method is called");
        // 执行初始化操作
    }

    // 在销毁前调用
    @Override
    public void destroy() {
        System.out.println("DisposableBean's destroy method is called");
        // 执行资源释放操作
    }

    // 在属性设置后立即调用
    @PostConstruct
    public void init() {
        System.out.println("@PostConstruct annotated method is called");
        // 执行初始化操作
    }

    // 在销毁前调用
    @PreDestroy
    public void cleanUp() {
        System.out.println("@PreDestroy annotated method is called");
        // 执行资源释放操作
    }
}
