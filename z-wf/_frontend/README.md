# Z-WF 工作流管理系统前端

基于 React + TypeScript + LogicFlow 的工作流设计与审批平台。

## 技术栈

- **React 18** - UI 框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Ant Design 5** - UI 组件库
- **LogicFlow** - 流程图引擎
- **React Router 6** - 路由管理
- **Axios** - HTTP 客户端
- **Dayjs** - 日期处理

## 项目结构

```
z-wf-admin-frontend/
├── public/                 # 静态资源
├── src/
│   ├── components/        # 公共组件
│   │   ├── Layout/       # 布局组件
│   │   └── FlowDesigner/ # 流程设计器组件
│   │       └── nodes/    # 自定义节点
│   ├── pages/            # 页面组件
│   │   ├── Dashboard/    # 仪表盘
│   │   ├── TodoList/     # 待办列表
│   │   ├── DoneList/     # 已办列表
│   │   ├── MyProcesses/  # 我发起的流程
│   │   ├── TaskDetail/   # 任务详情
│   │   ├── ProcessDetail/# 流程详情
│   │   ├── ProcessDesigner/ # 流程设计器页面
│   │   └── ProcessList/  # 流程列表
│   ├── router/           # 路由配置
│   ├── services/         # API 服务
│   ├── styles/           # 全局样式
│   ├── types/            # TypeScript 类型
│   └── utils/            # 工具函数
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 开发环境配置

### 环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0 或 yarn >= 1.22.0

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 启动开发服务器

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
# 或
yarn build
```

### 代码检查

```bash
# TypeScript 类型检查
npm run type-check

# ESLint 检查
npm run lint
```

## API 接口说明

前端通过 Axios 与后端 API 通信，主要接口包括：

### 审批中心 API (`/approval-center`)

- `GET /dashboard` - 获取仪表盘统计数据
- `GET /tasks/todo` - 获取待办任务列表
- `GET /tasks/done` - 获取已办任务列表
- `GET /tasks/{taskId}` - 获取任务详情
- `POST /tasks/{taskId}/complete` - 完成任务审批
- `GET /my-processes` - 获取我发起的流程
- `GET /processes/{processInstanceId}` - 获取流程实例详情
- `POST /processes/start` - 启动流程实例
- `GET /processes/definitions` - 获取流程定义列表
- `DELETE /processes/{processInstanceId}` - 终止流程实例

### 流程设计器 API (`/designer`)

- `GET /flow-graph/{processDefinitionId}` - 获取流程图数据
- `POST /flow-graph/{processDefinitionId}` - 保存流程图
- `POST /deploy/{processDefinitionId}` - 部署流程
- `GET /approval-history/{processInstanceId}` - 获取审批历史

## 流程设计器使用说明

### 基本操作

1. **添加节点**: 从左侧拖拽面板拖拽节点到画布
2. **连接节点**: 从一个节点的锚点拖拽连线到另一个节点
3. **编辑节点**: 双击节点编辑文本，或右键点击打开属性面板
4. **删除节点/连线**: 选中后按 Delete 键或右键菜单删除
5. **平移画布**: 按住空格键拖拽画布
6. **缩放**: 使用鼠标滚轮或工具栏按钮

### 节点类型

- **开始节点**: 流程起点，绿色圆角矩形
- **审批节点**: 审批环节，蓝色矩形
- **条件节点**: 分支判断，橙色菱形
- **抄送节点**: 抄送通知，绿色虚线矩形
- **结束节点**: 流程终点，红色圆形

### 保存与部署

1. 点击"保存"按钮保存流程图
2. 点击"部署"按钮发布流程
3. 部署后的流程可以被发起实例

## 贡献指南

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -am 'Add some feature'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

## 许可证

MIT License
