package com.zifang.z.ext.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展平台配置属性
 */
@ConfigurationProperties(prefix = "z-ext")
public class ExtProperties {

    /**
     * 是否启用扩展平台
     */
    private boolean enabled = true;

    /**
     * 扫描的基础包路径
     */
    private List<String> basePackages = new ArrayList<>();

    /**
     * 默认路由策略
     */
    private String defaultRouter = "default";

    /**
     * RPC默认端口
     */
    private int defaultRpcPort = 8080;

    /**
     * 路由规则配置
     */
    private List<RouteRule> routeRules = new ArrayList<>();

    // Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public String getDefaultRouter() {
        return defaultRouter;
    }

    public void setDefaultRouter(String defaultRouter) {
        this.defaultRouter = defaultRouter;
    }

    public int getDefaultRpcPort() {
        return defaultRpcPort;
    }

    public void setDefaultRpcPort(int defaultRpcPort) {
        this.defaultRpcPort = defaultRpcPort;
    }

    public List<RouteRule> getRouteRules() {
        return routeRules;
    }

    public void setRouteRules(List<RouteRule> routeRules) {
        this.routeRules = routeRules;
    }

    /**
     * 路由规则配置
     */
    public static class RouteRule {

        /**
         * 扩展点标识
         */
        private String point;

        /**
         * 条件表达式
         */
        private String condition;

        /**
         * 目标实现
         */
        private String target;

        /**
         * 权重
         */
        private int weight = 100;

        // Getters and Setters

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}