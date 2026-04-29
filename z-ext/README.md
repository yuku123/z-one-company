# Z-Ext 扩展平台 SDK

一个基于 RPC 的扩展平台 SDK，支持扩展点动态路由和热插拔。

## 模块结构

```
z-ext/
├── z-ext-core/                    # 核心模块
│   ├── annotation/               # 注解定义
│   │   ├── ExtPoint.java         # 扩展点标记
│   │   ├── ExtImpl.java          # 扩展实现标记
│   │   ├── ExtType.java          # 扩展类型枚举
│   │   ├── ExtImplType.java      # 实现类型枚举
│   │   ├── EnableExt.java        # 启用扩展平台
│   │   ├── ExtRegistrar.java     # 注册器
│   │   └── ExtScanner.java       # 扫描器
│   ├── core/
│   │   ├── registry/             # 注册中心
│   │   │   ├── ExtRegistry.java
│   │   │   ├── ExtPointDefinition.java
│   │   │   └── ExtImplDefinition.java
│   │   ├── proxy/                # 动态代理
│   │   │   ├── ExtProxyFactory.java
│   │   │   └── ExtInvocationHandler.java
│   │   └── router/               # 路由策略
│   │       ├── ExtRouter.java
│   │       ├── ExtRouterContext.java
│   │       └── DefaultExtRouter.java
│   └── rpc/                      # RPC调用
│       ├── client/
│       │   ├── ExtRpcInvoker.java
│       │   ├── ZRpcExtInvoker.java
│       │   └── ExtRpcInvokerFactory.java
│
├── z-ext-spring-boot-starter/    # Spring Boot Starter
│   └── src/main/java/.../starter/
│       ├── ExtAutoConfiguration.java
│       └── ExtProperties.java
│
├── z-ext-admin/                   # 管理后台
│
└── z-ext-admin-frontend/         # 前端
```

## 快速开始

### 1. 定义扩展点接口

```java
// 平台侧定义扩展点
@ExtPoint(value = "order.create", type = ExtType.SYNC, description = "订单创建服务")
public interface OrderCreateService {
    Order create(CreateOrderRequest request);
}
```

### 2. 平台默认实现

```java
// 平台默认实现
@ExtImpl(point = "order.create", name = "default", type = ExtImplType.PLATFORM)
public class DefaultOrderCreateService implements OrderCreateService {
    @Override
    public Order create(CreateOrderRequest request) {
        // 平台侧业务逻辑
        return orderRepository.save(request.toOrder());
    }
}
```

### 3. 业务方替换实现

```java
// 业务方三方实现 - 通过RPC调用
@ExtImpl(
    point = "order.create",
    name = "external",
    type = ExtImplType.EXTERNAL,
    description = "外部订单服务"
)
public class ExternalOrderCreateService implements OrderCreateService {
    // 实现类本身不需要写逻辑，通过RPC调用外部服务
    // RPC地址通过配置指定：ext.rpc.order.create.external.address=192.168.1.100
}
```

### 4. 业务使用

```java
@SpringBootApplication
@EnableExt(basePackages = "com.example")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Service
public class OrderBusinessService {
    @Autowired
    private OrderCreateService orderCreateService; // 实际是代理对象

    public Order createOrder(CreateOrderRequest request) {
        // 调用时由SDK根据路由规则决定执行哪个实现
        return orderCreateService.create(request);
    }
}
```

### 5. 配置

```yaml
# application.yml
z-ext:
  enabled: true
  base-packages:
    - com.example.ext
  default-rpc-port: 8080
  route-rules:
    - point: order.create
      condition: "env == 'prod'"
      target: external
```

## 扩展点类型

- **SYNC**: 同步执行，只选择一个实现执行
- **ASYNC**: 异步执行，不阻塞主流程
- **CHAIN**: 链式执行，多个实现按顺序依次执行

## 实现类型

- **PLATFORM**: 平台默认实现
- **EXTERNAL**: 外部实现，通过RPC调用
- **CUSTOM**: 业务自定义实现

## 技术栈

- Java 8+
- Spring Boot 2.7.x
- Netty 4.1.x
- 基于 z-rpc 进行RPC调用