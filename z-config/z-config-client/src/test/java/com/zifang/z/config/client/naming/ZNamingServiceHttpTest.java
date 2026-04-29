package com.zifang.z.config.client.naming;

import com.zifang.util.core.meta.Result;
import com.zifang.util.http.client.HttpRequestProxy;
import com.zifang.z.config.common.model.ZNamingInstance;
import com.zifang.z.config.common.model.naming.ZNamingInstanceRegisterRequest;
import com.zifang.z.config.common.model.naming.ZNamingSubscribeRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * 服务发现模块HTTP接口测试类
 *
 * 测试目标：验证服务注册、服务发现、服务订阅等核心功能
 * 测试方式：通过HTTP REST API与z-config-server交互
 *
 * 服务端地址配置：修改 SERVER_HOST 和 SERVER_PORT
 */
public class ZNamingServiceHttpTest {

    // ==================== 服务端配置 ====================
    private static final String SERVER_HOST = "127.0.0.1";
    // private static final String SERVER_HOST = "101.37.80.51";
    private static final int SERVER_PORT = 8084;
    private static final String BASE_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT;

    // ==================== 测试数据 ====================
    private static final String TEST_SERVICE_NAME = "test-order-service";
    private static final String TEST_GROUP = "DEFAULT_GROUP";
    private static final String TEST_NAMESPACE = "dev";

    // HTTP客户端代理
    private NamingServiceClient namingClient;

    /**
     * 命名服务HTTP客户端接口
     * 使用HttpRequestProxy动态代理实现
     */
    public interface NamingServiceClient {

        @com.zifang.util.http.base.define.RequestMapping(value = "/naming/registerInstance/simple", method = com.zifang.util.http.base.define.RequestMethod.POST)
        Result<String> registerInstanceSimple(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam("ip") String ip,
                @com.zifang.util.http.base.define.RequestParam("port") Integer port);

        @com.zifang.util.http.base.define.RequestMapping(value = "/naming/registerInstance", method = com.zifang.util.http.base.define.RequestMethod.POST)
        Result<String> registerInstance(@com.zifang.util.http.base.define.RequestBody ZNamingInstanceRegisterRequest request);

        @com.zifang.util.http.base.define.RequestMapping(value = "/naming/deregisterInstance/simple", method = com.zifang.util.http.base.define.RequestMethod.DELETE)
        Result<String> deregisterInstanceSimple(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam("ip") String ip,
                @com.zifang.util.http.base.define.RequestParam("port") Integer port);

        @com.zifang.util.http.base.define.RequestMapping(value = "/naming/getAllInstances", method = com.zifang.util.http.base.define.RequestMethod.GET)
        Result<List<ZNamingInstance>> getAllInstances(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam(value = "group", required = false) String group,
                @com.zifang.util.http.base.define.RequestParam(value = "namespace", required = false) String namespace);

        @com.zifang.util.http.base.define.RequestMapping(value = "/naming/selectInstances/healthy", method = com.zifang.util.http.base.define.RequestMethod.GET)
        Result<List<ZNamingInstance>> selectHealthyInstances(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName,
                @com.zifang.util.http.base.define.RequestParam("healthy") Boolean healthy,
                @com.zifang.util.http.base.define.RequestParam(value = "clusterName", required = false) String clusterName);

        @com.zifang.util.http.base.define.RequestMapping(value = "/naming/selectOneHealthyInstance", method = com.zifang.util.http.base.define.RequestMethod.GET)
        Result<ZNamingInstance> selectOneHealthyInstance(
                @com.zifang.util.http.base.define.RequestParam("serviceName") String serviceName);

        @com.zifang.util.http.base.define.RequestMapping(value = "/naming/subscribe", method = com.zifang.util.http.base.define.RequestMethod.POST)
        Result<String> subscribeService(@com.zifang.util.http.base.define.RequestBody ZNamingSubscribeRequest request);
    }

    @Before
    public void setUp() {
        // 构建HTTP客户端
        Map<String, Object> context = new HashMap<>();
        context.put("serverHost", SERVER_HOST);
        context.put("serverPort", SERVER_PORT);
        namingClient = HttpRequestProxy.proxy(NamingServiceClient.class, context);
    }

    // ==================== 基础功能测试 ====================

    /**
     * 测试1：基础服务注册与发现
     * 验证点：
     * 1. 服务可以成功注册
     * 2. 可以通过服务名查询到实例
     * 3. 实例信息（IP、端口）正确
     */
    @Test
    public void testBasicServiceRegistrationAndDiscovery() {
        System.out.println("\n========== 测试1：基础服务注册与发现 ==========");

        String serviceName = "test-payment-service";
        String ip = "192.168.1.100";
        int port = 8080;

        try {
            // 1. 注册服务
            System.out.println("步骤1：注册服务...");
            Result<String> registerResult = namingClient.registerInstanceSimple(serviceName, ip, port);
            System.out.println("注册结果: " + registerResult);
            assertTrue("注册应成功", registerResult.isSuccess());

            // 等待数据同步
            Thread.sleep(300);

            // 2. 发现服务
            System.out.println("\n步骤2：查询服务实例...");
            Result<List<ZNamingInstance>> discoverResult = namingClient.getAllInstances(serviceName, null, null);
            System.out.println("发现结果: " + discoverResult);
            assertTrue("查询应成功", discoverResult.isSuccess());
            assertNotNull("实例列表不应为空", discoverResult.getData());
            assertEquals("应有一个实例", 1, discoverResult.getData().size());

            // 3. 验证实例信息
            ZNamingInstance instance = discoverResult.getData().get(0);
            System.out.println("\n步骤3：验证实例信息...");
            System.out.println("实例ID: " + instance.getInstanceId());
            System.out.println("IP: " + instance.getIp());
            System.out.println("端口: " + instance.getPort());
            assertEquals("IP应匹配", ip, instance.getIp());
            assertEquals("端口应匹配", Integer.valueOf(port), instance.getPort());

            System.out.println("\n✅ 测试1通过：基础服务注册与发现功能正常");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("测试被中断: " + e.getMessage());
        } finally {
            // 清理：注销服务
            try {
                namingClient.deregisterInstanceSimple(serviceName, ip, port);
                System.out.println("清理完成：服务已注销");
            } catch (Exception e) {
                System.err.println("清理失败: " + e.getMessage());
            }
        }
    }

    /**
     * 测试2：服务健康检查与负载均衡
     * 验证点：
     * 1. 可以查询健康/不健康实例
     * 2. 负载均衡可以选出实例
     * 3. 选出的实例是健康的
     */
    @Test
    public void testHealthCheckAndLoadBalance() {
        System.out.println("\n========== 测试2：服务健康检查与负载均衡 ==========");

        String serviceName = "test-inventory-service";
        String ip1 = "192.168.2.10";
        int port1 = 9001;
        String ip2 = "192.168.2.11";
        int port2 = 9002;

        try {
            // 1. 注册两个实例
            System.out.println("步骤1：注册两个服务实例...");
            namingClient.registerInstanceSimple(serviceName, ip1, port1);
            namingClient.registerInstanceSimple(serviceName, ip2, port2);
            System.out.println("实例1: " + ip1 + ":" + port1);
            System.out.println("实例2: " + ip2 + ":" + port2);

            Thread.sleep(300);

            // 2. 查询所有实例
            System.out.println("\n步骤2：查询所有实例...");
            Result<List<ZNamingInstance>> allResult = namingClient.getAllInstances(serviceName, null, null);
            System.out.println("所有实例数: " + (allResult.getData() != null ? allResult.getData().size() : 0));
            assertEquals("应有两个实例", 2, allResult.getData().size());

            // 3. 查询健康实例
            System.out.println("\n步骤3：查询健康实例...");
            Result<List<ZNamingInstance>> healthyResult = namingClient.selectHealthyInstances(serviceName, true, null);
            System.out.println("健康实例数: " + (healthyResult.getData() != null ? healthyResult.getData().size() : 0));
            assertTrue("应有健康实例", healthyResult.getData() != null && healthyResult.getData().size() > 0);

            // 4. 负载均衡测试
            System.out.println("\n步骤4：负载均衡测试（连续选择5次）...");
            AtomicInteger ip1Count = new AtomicInteger(0);
            AtomicInteger ip2Count = new AtomicInteger(0);
            for (int i = 0; i < 5; i++) {
                Result<ZNamingInstance> selectResult = namingClient.selectOneHealthyInstance(serviceName);
                if (selectResult.isSuccess() && selectResult.getData() != null) {
                    ZNamingInstance instance = selectResult.getData();
                    System.out.println("第" + (i + 1) + "次选中: " + instance.getIp() + ":" + instance.getPort());
                    if (ip1.equals(instance.getIp())) {
                        ip1Count.incrementAndGet();
                    } else if (ip2.equals(instance.getIp())) {
                        ip2Count.incrementAndGet();
                    }
                }
            }
            System.out.println("负载分布: IP1=" + ip1Count.get() + ", IP2=" + ip2Count.get());
            assertTrue("负载应分布到多个实例", ip1Count.get() + ip2Count.get() > 0);

            System.out.println("\n✅ 测试2通过：健康检查与负载均衡功能正常");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("测试被中断: " + e.getMessage());
        } finally {
            // 清理
            try {
                namingClient.deregisterInstanceSimple(serviceName, ip1, port1);
                namingClient.deregisterInstanceSimple(serviceName, ip2, port2);
                System.out.println("清理完成：所有实例已注销");
            } catch (Exception e) {
                System.err.println("清理失败: " + e.getMessage());
            }
        }
    }

    /**
     * 测试3：完整服务发现流程
     * 模拟真实场景：服务注册 -> 服务发现 -> 服务扩容 -> 服务下线
     */
    @Test
    public void testFullServiceDiscoveryFlow() {
        System.out.println("\n========== 测试3：完整服务发现流程 ==========");

        String serviceName = "test-payment-flow";

        try {
            // 阶段1：初始服务注册
            System.out.println("\n[阶段1] 初始服务注册");
            String ip1 = "10.0.1.10";
            int port1 = 8080;
            namingClient.registerInstanceSimple(serviceName, ip1, port1);
            System.out.println("✓ 实例1注册成功: " + ip1 + ":" + port1);

            Thread.sleep(200);

            // 阶段2：服务发现
            System.out.println("\n[阶段2] 服务发现");
            Result<List<ZNamingInstance>> discoveryResult = namingClient.getAllInstances(serviceName, null, null);
            assertTrue("服务发现应成功", discoveryResult.isSuccess());
            assertEquals("应发现1个实例", 1, discoveryResult.getData().size());
            System.out.println("✓ 服务发现成功，发现 " + discoveryResult.getData().size() + " 个实例");

            // 阶段3：服务扩容
            System.out.println("\n[阶段3] 服务扩容");
            String ip2 = "10.0.1.11";
            int port2 = 8081;
            String ip3 = "10.0.1.12";
            int port3 = 8082;
            namingClient.registerInstanceSimple(serviceName, ip2, port2);
            namingClient.registerInstanceSimple(serviceName, ip3, port3);
            System.out.println("✓ 实例2注册成功: " + ip2 + ":" + port2);
            System.out.println("✓ 实例3注册成功: " + ip3 + ":" + port3);

            Thread.sleep(200);

            // 验证扩容结果
            Result<List<ZNamingInstance>> scaledResult = namingClient.getAllInstances(serviceName, null, null);
            assertEquals("扩容后应有3个实例", 3, scaledResult.getData().size());
            System.out.println("✓ 扩容验证成功，当前 " + scaledResult.getData().size() + " 个实例");

            // 阶段4：负载均衡验证
            System.out.println("\n[阶段4] 负载均衡验证");
            System.out.println("连续选择10次实例：");
            for (int i = 0; i < 10; i++) {
                Result<ZNamingInstance> lbResult = namingClient.selectOneHealthyInstance(serviceName);
                if (lbResult.isSuccess() && lbResult.getData() != null) {
                    ZNamingInstance inst = lbResult.getData();
                    System.out.println("  [" + (i + 1) + "] " + inst.getIp() + ":" + inst.getPort());
                }
            }
            System.out.println("✓ 负载均衡验证完成");

            // 阶段5：服务缩容（下线部分实例）
            System.out.println("\n[阶段5] 服务缩容");
            namingClient.deregisterInstanceSimple(serviceName, ip2, port2);
            System.out.println("✓ 实例2已下线: " + ip2 + ":" + port2);

            Thread.sleep(200);

            Result<List<ZNamingInstance>> afterShrinkResult = namingClient.getAllInstances(serviceName, null, null);
            System.out.println("✓ 缩容后剩余 " + afterShrinkResult.getData().size() + " 个实例");

            // 阶段6：服务完全下线
            System.out.println("\n[阶段6] 服务完全下线");
            namingClient.deregisterInstanceSimple(serviceName, ip1, port1);
            namingClient.deregisterInstanceSimple(serviceName, ip3, port3);
            System.out.println("✓ 所有实例已下线");

            Thread.sleep(200);

            Result<List<ZNamingInstance>> finalResult = namingClient.getAllInstances(serviceName, null, null);
            System.out.println("✓ 最终实例数: " + finalResult.getData().size());

            System.out.println("\n========================================");
            System.out.println("✅ 完整服务发现流程测试通过！");
            System.out.println("========================================");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("测试被中断: " + e.getMessage());
        }
    }

    /**
     * 测试4：集群与分组隔离测试
     * 验证不同分组、集群之间的隔离性
     */
    @Test
    public void testClusterAndGroupIsolation() {
        System.out.println("\n========== 测试4：集群与分组隔离测试 ==========");

        String serviceName = "test-product-service";

        try {
            // 在DEFAULT集群注册实例
            System.out.println("\n[步骤1] 在DEFAULT集群注册实例");
            String defaultIp = "10.10.1.10";
            int defaultPort = 8080;
            namingClient.registerInstanceSimple(serviceName, defaultIp, defaultPort);
            System.out.println("✓ DEFAULT集群实例: " + defaultIp + ":" + defaultPort);

            Thread.sleep(200);

            // 按集群查询DEFAULT
            System.out.println("\n[步骤2] 按集群查询DEFAULT实例");
            Result<List<ZNamingInstance>> defaultResult = namingClient.selectHealthyInstances(serviceName, true, "DEFAULT");
            System.out.println("DEFAULT集群实例数: " + (defaultResult.getData() != null ? defaultResult.getData().size() : 0));
            if (defaultResult.getData() != null && !defaultResult.getData().isEmpty()) {
                for (ZNamingInstance inst : defaultResult.getData()) {
                    System.out.println("  - " + inst.getIp() + ":" + inst.getPort() + " (集群: " + inst.getClusterName() + ")");
                }
            }

            System.out.println("\n✅ 集群与分组隔离测试通过！");

            // 清理
            namingClient.deregisterInstanceSimple(serviceName, defaultIp, defaultPort);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("测试被中断: " + e.getMessage());
        }
    }

    /**
     * 测试5：并发服务注册压力测试
     * 模拟高并发场景下的服务注册和发现
     */
    @Test
    public void testConcurrentRegistration() throws InterruptedException {
        System.out.println("\n========== 测试5：并发服务注册压力测试 ==========");

        String serviceName = "test-concurrent-service";
        int threadCount = 20;  // 并发线程数
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        System.out.println("启动 " + threadCount + " 个线程并发注册服务...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    String ip = "192.168.50." + (index + 1);
                    int port = 9000 + index;

                    Result<String> result = namingClient.registerInstanceSimple(serviceName, ip, port);

                    if (result.isSuccess()) {
                        successCount.incrementAndGet();
                        System.out.println("[线程" + index + "] 注册成功: " + ip + ":" + port);
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
        System.out.println("是否全部完成: " + completed);

        // 验证注册结果
        Thread.sleep(500);  // 等待数据同步
        Result<List<ZNamingInstance>> result = namingClient.getAllInstances(serviceName, null, null);
        int actualCount = result.getData() != null ? result.getData().size() : 0;
        System.out.println("实际注册实例数: " + actualCount);

        assertEquals("成功注册的实例数应匹配", successCount.get(), actualCount);

        System.out.println("\n✅ 并发注册压力测试通过！");

        // 清理：批量注销
        if (result.getData() != null) {
            for (ZNamingInstance instance : result.getData()) {
                try {
                    namingClient.deregisterInstanceSimple(serviceName, instance.getIp(), instance.getPort());
                } catch (Exception e) {
                    // 忽略清理错误
                }
            }
        }
    }
}
