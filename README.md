# Tlias 智能学习辅助系统

一个前后端分离的**员工 / 班级 / 学员管理系统**，包含员工管理、部门管理、班级管理、学员管理、数据统计报表、文件上传、登录认证（JWT）等功能。

本项目采用 **monorepo** 组织方式：同一个仓库、同一个分支，用目录区分前后端。

```
Tlias/
├── tlias_backend/    # Spring Boot 后端（端口 8080）
└── tlias_frontend/   # Vue 3 前端（端口 5173）
```

## 技术栈

**后端** `tlias_backend/`

- Spring Boot 4.1.0（WebMVC）+ JDK 17
- MyBatis + PageHelper（分页）
- MySQL 8
- JWT（jjwt）登录鉴权
- AOP 操作日志
- 阿里云 OSS 文件上传
- Maven 构建

**前端** `tlias_frontend/`

- Vue 3 + Vite 8
- Vue Router 4
- Element Plus（组件 + 按需自动导入）
- Axios（请求拦截、统一响应处理）
- ECharts 6（数据报表图表）

## 本地运行

### 1. 准备数据库

1. 创建数据库 `tlias`（字符集 utf8mb4）
2. 执行建表 / 初始化数据脚本（见后端 `src/test/resources/sql/` 与 `接口文档.pdf`）

### 2. 配置本地密码（重要）

仓库中的 `application.properties` 只保留占位符，真实密码放在**不会提交**的 `application-dev.properties`：

```bash
cd tlias_backend/src/main/resources
cp application-dev.properties.example application-dev.properties
# 编辑 application-dev.properties，填入自己的 MySQL 用户名/密码
```

阿里云 OSS 的密钥通过环境变量注入（不会写进任何配置文件）：

```bash
# Windows PowerShell
$env:OSS_ACCESS_KEY_ID="你的AccessKeyId"
$env:OSS_ACCESS_KEY_SECRET="你的AccessKeySecret"
```

### 3. 启动后端

```bash
cd tlias_backend
mvn spring-boot:run     # 或 ./mvnw spring-boot:run
# 启动后访问 http://localhost:8080
```

### 4. 启动前端

```bash
cd tlias_frontend
npm install
npm run dev
# 启动后访问 http://localhost:5173
```

> 前端通过 Vite 代理把 `/api` 请求转发到后端 8080，因此两个服务需要同时运行。

## 功能一览

- 登录 / JWT 鉴权 / 修改密码
- 部门管理（增删改查）
- 员工管理（分页查询、增删改、批量删除、文件上传头像）
- 班级管理、学员管理
- 数据统计（ECharts 图表）
- 操作日志（AOP 切面自动记录）

## 说明

- 后端 `target/`、前端 `node_modules/` 等构建产物已通过各自目录的 `.gitignore` 排除。
- 任何含真实密码 / 密钥的文件均不进仓库（根目录 `.gitignore` 有兜底规则）。

<!-- TODO: 在此补充功能截图 -->
