package com.zifang.z.config.client.naming;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 命名服务测试套件
 * 运行所有服务发现相关的测试
 *
 * 执行方式:
 * 1. 在IDE中右键运行此类
 * 2. 命令行: mvn test -Dtest=NamingServiceTestSuite
 *
 * 测试覆盖:
 * - 基础功能: 注册、发现、注销
 * - 高级功能: 健康检查、负载均衡、集群隔离
 * - 性能测试: 并发注册、压力测试
 * - 集成测试: 完整流程验证
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // HTTP接口测试
    ZNamingServiceHttpTest.class,
    // 集成测试
    NamingServiceIntegrationTest.class,
    // 可以添加更多测试类
})
public class NamingServiceTestSuite {
    // 此类仅用于组织测试套件，不包含具体测试方法
}
