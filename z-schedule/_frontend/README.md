# Z-Job Admin Frontend

Z-Job 分布式任务调度平台的前端管理界面，基于 React + TypeScript + Ant Design + Vite 构建。

## 技术栈

- **React 18** - 前端框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Ant Design 5** - UI组件库
- **React Router 6** - 路由管理
- **Zustand** - 状态管理
- **ECharts** - 图表库
- **Axios** - HTTP客户端
- **Dayjs** - 日期处理

## 功能特性

### 1. 仪表盘 (Dashboard)
- 实时统计数据展示
  - 任务总数、运行中任务数
  - 在线执行器数量
  - 今日调度次数和成功率
- 可视化图表
  - 调度成功率趋势图
  - 任务状态分布饼图
- 最近调度记录
- 执行器负载状态

### 2. 任务管理 (Job Management)
- 任务列表
  - 支持搜索、筛选、分页
  - 显示任务状态、Cron表达式、路由策略等
- 新增/编辑任务
  - 任务描述、Cron表达式
  - 执行器Handler
  - 路由策略选择（轮询、随机、一致性哈希等）
  - 阻塞策略选择（串行、丢弃、覆盖）
  - 超时时间和重试次数
  - 任务参数和报警邮箱
- 任务操作
  - 启动/停止任务
  - 手动触发执行
  - 删除任务
  - 查看任务详情

### 3. 调度日志 (Job Logs)
- 日志列表
  - 按任务、时间范围筛选
  - 显示调度时间、执行时间、状态
  - 支持分页
- 日志详情
  - 调度参数和结果
  - 执行日志查看
  - 错误堆栈信息
- 日志清理
  - 按时间范围清理
  - 按任务清理

### 4. 执行器管理 (Executor Management)
- 执行器列表
  - 应用名称、标题
  - 注册方式（自动/手动）
  - 在线实例数
- 执行器详情
  - 注册节点列表
  - 每个节点的负载情况
  - 运行中的任务数
- 新增/编辑执行器
  - 应用名称（AppName）
  - 显示标题
  - 注册方式选择
  - 机器地址列表（手动注册时）

### 5. 系统设置 (Settings)
- 系统配置
  - 调度中心地址
  - 告警邮箱配置
  - 日志保留天数
- 安全设置
  - 用户管理
  - 角色权限
  - 登录日志
- 关于系统
  - 版本信息
  - 系统文档链接

## 项目结构

```
z-schedule-admin-frontend/
├── public/                 # 静态资源
├── src/
│   ├── components/         # 公共组件
│   │   ├── common/         # 通用组件
│   │   ├── job/            # 任务相关组件
│   │   └── dashboard/      # 仪表盘组件
│   ├── pages/              # 页面组件
│   │   ├── Dashboard/      # 仪表盘
│   │   ├── JobList/        # 任务列表
│   │   ├── JobDetail/      # 任务详情
│   │   ├── JobLog/         # 调度日志
│   │   ├── ExecutorList/   # 执行器列表
│   │   └── Settings/       # 系统设置
│   ├── layouts/            # 布局组件
│   │   └── MainLayout.tsx  # 主布局
│   ├── services/           # API服务
│   │   ├── api.ts          # axios封装
│   │   └── job.ts          # 任务相关API
│   ├── stores/             # 状态管理
│   │   └── useUserStore.ts # 用户状态
│   ├── types/              # TypeScript类型
│   │   ├── job.ts          # 任务类型
│   │   └── index.ts        # 类型导出
│   ├── utils/              # 工具函数
│   │   └── utils.ts        # 通用工具
│   ├── App.tsx             # 根组件
│   ├── main.tsx            # 入口文件
│   └── vite-env.d.ts       # Vite类型声明
├── index.html              # HTML模板
├── package.json            # 依赖配置
├── tsconfig.json           # TypeScript配置
├── vite.config.ts          # Vite配置
└── README.md               # 项目说明
```

## 快速开始

### 安装依赖

```bash
cd z-schedule-admin-frontend
npm install
```

### 启动开发服务器

```bash
npm run dev
```

应用将在 http://localhost:3000 启动

### 构建生产版本

```bash
npm run build
```

构建后的文件将位于 `dist` 目录

## 代理配置

开发服务器已配置代理，所有 `/api` 开头的请求将被代理到 `http://localhost:8080/z-schedule-admin`。

在 `vite.config.ts` 中可以修改代理配置：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '/z-schedule-admin'),
    },
  },
},
```

## 贡献指南

1. Fork 本仓库
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 许可证

[MIT](LICENSE) © Z-Job Team
