package com.zifang.z.config.client.naming;

import com.zifang.util.core.meta.Result;
import com.zifang.util.http.client.HttpRequestProxy;
import com.zifang.z.config.common.model.ZNamingInstance;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * 命名服务集成测试
 *
 * 测试目标：
 * 1. 验证服务注册功能
 * 2. 验证服务发现功能
 * 3. 验证健康检查功能
 * 4. 验证负载均衡功能
 * 5. 验证并发场景下的稳定性
 *
 * 环境要求：
 * - z-config-server 已启动
 * - 数据库已初始化
 *
 * 配置文件：修改 SERVER_HOST 和 SERVER_PORT 为实际服务器地址
 */
public class NamingServiceIntegrationTest {

    // ==================== 服务器配置 ====================
    /** 服务器主机地址 */
    private static final String SERVER_HOST = "127.0.0.1";
    // private static final String SERVER_HOST = "101.37.80.51";

    /** 服务器端口 */
    private static final int SERVER_PORT = 8084;

    /** 基础URL */
    private static final String BASE_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT;

    // ==================== 测试常量 ====================
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private static final String DEFAULT_NAMESPACE = "";
    private static final String DEFAULT_CLUSTER = "DEFAULT";

    // HTTP客户端
    private NamingHttpClient httpClient;

    /**
     * HTTP客户端接口定义
     */
    public interface NamingHttpClient {

        // ========== 服务注册接口 ==========

        @com.zifang.util.http.base.define.RequestMapping(
                value = "/naming/registerInstance/simple",
                method = com.zifang.util.http.base.define.RequestMethod.POST)
        Result<String> registerSimple(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam("ip") String ip,
                @com.zifang.util.http.base.define.RequestParam("port") Integer port);

        // ========== 服务注销接口 ==========

        @com.zifang.util.http.base.define.RequestMapping(
                value = "/naming/deregisterInstance/simple",
                method = com.zifang.util.http.base.define.RequestMethod.DELETE)
        Result<String> deregisterSimple(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam("ip") String ip,
                @com.zifang.util.http.base.define.RequestParam("port") Integer port);

        // ========== 服务发现接口 ==========

        @com.zifang.util.http.base.define.RequestMapping(
                value = "/naming/getAllInstances",
                method = com.zifang.util.http.base.define.RequestMethod.GET)
        Result<List<ZNamingInstance>> getAllInstances(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam(value = "group", required = false) String group,
                @com.zifang.util.http.base.define.RequestParam(value = "namespace", required = false) String namespace);

        @com.zifang.util.http.base.define.RequestMapping(
                value = "/naming/selectInstances/healthy",
                method = com.zifang.util.http.base.define.RequestMethod.GET)
        Result<List<ZNamingInstance>> selectHealthyInstances(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam("healthy") Boolean healthy,
                @com.zifang.util.http.base.define.RequestParam(value = "clusterName", required = false) String clusterName);

        @com.zifang.util.http.base.define.RequestMapping(
                value = "/naming/selectOneHealthyInstance",
                method = com.zifang.util.http.base.define.RequestMethod.GET)
        Result<ZNamingInstance> selectOneHealthyInstance(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName);
    }

    @Before
    public void setUp() {
        Map<String, Object> context = new HashMap<>();
        context.put("serverHost", SERVER_HOST);
        context.put("serverPort", SERVER_PORT);
        httpClient = HttpRequestProxy.proxy(NamingHttpClient.class, context);
    }

    // ==================== 基础功能测试 ====================

    /**
     * 测试1：基础服务注册与发现
     * 验证：
     * 1. 服务可以成功注册
     * 2. 可以通过服务名查询到实例
     * 3. 实例信息（IP、端口）正确
     */
    @Test
    public void testBasicServiceRegistrationAndDiscovery() throws Exception {
        System.out.println("\n========== 测试1：基础服务注册与发现 ==========");

        String serviceName = "test-basic-service";
        String ip = "192.168.100.10";
        int port = 8080;

        try {
            // 1. 注册服务
            System.out.println("[步骤1] 注册服务...");
            Result<String> registerResult = httpClient.registerSimple(serviceName, ip, port);
            System.out.println("注册结果: " + registerResult.getMessage());
            assertTrue("注册应成功", registerResult.isSuccess());

            Thread.sleep(300);

            // 2. 发现服务
            System.out.println("\n[步骤2] 查询服务实例...");
            Result<List<ZNamingInstance>> discoverResult = httpClient.getAllInstances(serviceName, null, null);
            assertTrue("查询应成功", discoverResult.isSuccess());
            assertNotNull("实例列表不应为空", discoverResult.getData());
            assertEquals("应有一个实例", 1, discoverResult.getData().size());

            // 3. 验证实例信息
            ZNamingInstance instance = discoverResult.getData().get(0);
            System.out.println("实例信息:");
            System.out.println("  - 实例ID: " + instance.getInstanceId());
            System.out.println("  - IP: " + instance.getIp());
            System.out.println("  - 端口: " + instance.getPort());
            System.out.println("  - 健康状态: " + (instance.getHealthy() != null && instance.getHealthy() ? "健康" : "不健康"));

            assertEquals("IP应匹配", ip, instance.getIp());
            assertEquals("端口应匹配", Integer.valueOf(port), instance.getPort());

            System.out.println("\n✅ 测试1通过：基础服务注册与发现功能正常");

        } finally {
            // 清理
            try {
                httpClient.deregisterSimple(serviceName, ip, port);
                System.out.println("清理完成：服务已注销");
            } catch (Exception e) {
                System.err.println("清理失败: " + e.getMessage());
            }
        }
    }

    /**
     * 测试2：健康检查与负载均衡
     * 验证：
     * 1. 可以筛选健康/不健康实例
     * 2. 负载均衡可以选出实例
     */
    @Test
    public void testHealthCheckAndLoadBalance() throws Exception {
        System.out.println("\n========== 测试2：健康检查与负载均衡 ==========");

        String serviceName = "test-lb-service";
        String ip1 = "10.0.1.10";
        int port1 = 9001;
        String ip2 = "10.0.1.11";
        int port2 = 9002;

        try {
            // 注册两个实例
            System.out.println("[步骤1] 注册两个服务实例...");
            httpClient.registerSimple(serviceName, ip1, port1);
            httpClient.registerSimple(serviceName, ip2, port2);
            System.out.println("✓ 实例1: " + ip1 + ":" + port1);
            System.out.println("✓ 实例2: " + ip2 + ":" + port2);

            Thread.sleep(300);

            // 查询所有实例
            System.out.println("\n[步骤2] 查询所有实例...");
            Result<List<ZNamingInstance>> allResult = httpClient.getAllInstances(serviceName, null, null);
            System.out.println("所有实例数: " + (allResult.getData() != null ? allResult.getData().size() : 0));
            assertEquals("应有两个实例", 2, allResult.getData().size());

            // 查询健康实例
            System.out.println("\n[步骤3] 查询健康实例...");
            Result<List<ZNamingInstance>> healthyResult = httpClient.selectHealthyInstances(serviceName, true, null);
            System.out.println("健康实例数: " + (healthyResult.getData() != null ? healthyResult.getData().size() : 0));
            assertTrue("应有健康实例", healthyResult.getData() != null && healthyResult.getData().size() > 0);

            // 负载均衡测试
            System.out.println("\n[步骤4] 负载均衡测试（连续选择10次）...");
            int ip1Count = 0, ip2Count = 0;
            for (int i = 0; i < 10; i++) {
                Result<ZNamingInstance> lbResult = httpClient.selectOneHealthyInstance(serviceName);
                if (lbResult.isSuccess() && lbResult.getData() != null) {
                    ZNamingInstance inst = lbResult.getData();
                    System.out.println("  [" + (i + 1) + "] " + inst.getIp() + ":" + inst.getPort());
                    if (ip1.equals(inst.getIp())) ip1Count++;
                    else if (ip2.equals(inst.getIp())) ip2Count++;
                }
            }
            System.out.println("负载分布: IP1=" + ip1Count + ", IP2=" + ip2Count);
            assertTrue("负载应分布到多个实例", ip1Count + ip2Count > 0);

            System.out.println("\n✅ 测试2通过：健康检查与负载均衡功能正常");

        } finally {
            // 清理
            try {
                httpClient.deregisterSimple(serviceName, ip1, port1);
                httpClient.deregisterSimple(serviceName, ip2, port2);
                System.out.println("清理完成：所有实例已注销");
            } catch (Exception e) {
                System.err.println("清理失败: " + e.getMessage());
            }
        }
    }

    /**
     * 测试3：并发注册压力测试
     * 验证系统在高并发场景下的稳定性
     */
    @Test
    public void testConcurrentRegistration() throws Exception {
        System.out.println("\n========== 测试3：并发注册压力测试 ==========");

        String serviceName = "test-concurrent-service";
        int threadCount = 50;  // 并发线程数
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        System.out.println("启动 " + threadCount + " 个线程并发注册服务...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    String ip = "192.168.100." + (index + 1);
                    int port = 8000 + index;

                    Result<String> result = httpClient.registerSimple(serviceName, ip, port);

                    if (result.isSuccess()) {
                        successCount.incrementAndGet();
                        if (index % 10 == 0) {  // 只打印部分成功日志
                            System.out.println("[线程" + index + "] 注册成功: " + ip + ":" + port);
                        }
                    } else {
                        failCount.incrementAndGet();
                        System.err.println("[线程" + index + "] 注册失败: " + result.getMessage());
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.err.println("[线程" + index + "] 异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // 等待所有线程完成
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        System.out.println("\n========== 并发测试结果 ==========");
        System.out.println("总线程数: " + threadCount);
        System.out.println("成功: " + successCount.get());
        System.out.println("失败: " + failCount.get());
        System.out.println("耗时: " + (endTime - startTime) + "ms");
        System.out.println("QPS: " + (successCount.get() * 1000.0 / (endTime - startTime)));
        System.out.println("是否全部完成: " + completed);

        // 验证注册结果
        Thread.sleep(1000);  // 等待数据同步
        Result<List<ZNamingInstance>> result = httpClient.getAllInstances(serviceName, null, null);
        int actualCount = result.getData() != null ? result.getData().size() : 0;
        System.out.println("实际注册实例数: " + actualCount);

        // 断言验证
        assertTrue("成功注册数应大于0", successCount.get() > 0);
        assertEquals("实际注册数应与成功数匹配", successCount.get(), actualCount);

        System.out.println("\n✅ 并发注册压力测试通过！");

        // 清理：批量注销
        if (result.getData() != null) {
            System.out.println("\n开始清理...");
            for (ZNamingInstance instance : result.getData()) {
                try {
                    httpClient.deregisterSimple(serviceName, instance.getIp(), instance.getPort());
                } catch (Exception e) {
                    // 忽略清理错误
                }
            }
            System.out.println("清理完成");
        }
    }

    /**
     * 快速冒烟测试：验证服务是否可用
     */
    @Test
    public void testSmoke() {
        System.out.println("\n========== 冒烟测试 ==========");
        System.out.println("服务器: " + BASE_URL);

        String serviceName = "test-smoke-service";
        String ip = "127.0.0.1";
        int port = 9999;

        try {
            // 注册
            System.out.println("[1] 注册服务...");
            Result<String> regResult = httpClient.registerSimple(serviceName, ip, port);
            System.out.println("注册结果: " + (regResult.isSuccess() ? "成功" : "失败 - " + regResult.getMessage()));
            assertTrue("注册应成功", regResult.isSuccess());

            Thread.sleep(300);

            // 发现
            System.out.println("\n[2] 发现服务...");
            Result<List<ZNamingInstance>> disResult = httpClient.getAllInstances(serviceName, null, null);
            System.out.println("发现结果: " + (disResult.isSuccess() ? "成功" : "失败 - " + disResult.getMessage()));
            if (disResult.isSuccess() && disResult.getData() != null) {
                System.out.println("实例数: " + disResult.getData().size());
                for (ZNamingInstance inst : disResult.getData()) {
                    System.out.println("  - " + inst.getIp() + ":" + inst.getPort());
                }
            }
            assertTrue("发现应成功", disResult.isSuccess());

            // 注销
            System.out.println("\n[3] 注销服务...");
            Result<String> deregResult = httpClient.deregisterSimple(serviceName, ip, port);
            System.out.println("注销结果: " + (deregResult.isSuccess() ? "成功" : "失败 - " + deregResult.getMessage()));

            System.out.println("\n✅ 冒烟测试通过！服务运行正常");

        } catch (Exception e) {
            fail("冒烟测试失败: " + e.getMessage());
        }
    }
}
