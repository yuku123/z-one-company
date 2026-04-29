package com.zifang.z.meta.common.constant;

/**
 * 全局常量
 */
public class GlobalConstant {

    /**
     * 超级管理员租户ID
     */
    public static final long SUPER_TENANT_ID = 1L;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页数量
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页数量
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 租户类型
     */
    public static class TenantType {
        public static final String SYSTEM = "system";
        public static final String ENTERPRISE = "enterprise";
        public static final String PERSONAL = "personal";
    }

    /**
     * 应用类型
     */
    public static class AppType {
        public static final String WEB = "web";
        public static final String API = "api";
        public static final String MOBILE = "mobile";
        public static final String MINIAPP = "miniapp";
    }

    /**
     * 字典类型
     */
    public static class DictType {
        public static final String SYSTEM = "system";
        public static final String CUSTOM = "custom";
    }

    /**
     * HTTP 方法
     */
    public static class ApiMethod {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
    }

    /**
     * 认证类型
     */
    public static class AuthType {
        public static final String NONE = "none";
        public static final String BASIC = "basic";
        public static final String BEARER = "bearer";
        public static final String OAUTH2 = "oauth2";
    }
}