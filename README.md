# tech-finance-platform

科技金融管理平台骨架（仅框架，不含业务）。

## 兼容基线（已锁定）

- **JDK 17**
- **Spring Boot 3.x**（暂不用 Boot 4）
- **Vue 3**

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- Nacos 3.x（建议 3.0.3+）

## 依赖版本（与基线对齐）

| 组件 | 版本 | 适配说明 |
| --- | --- | --- |
| Spring Boot | 3.5.15 | Boot 3 线；与 Cloud 2025.0.3 对齐 |
| Spring Cloud | 2025.0.3 | Northfields |
| Spring Cloud Alibaba | 2025.0.0.0 | 官方适配 Boot 3.5.x / Cloud 2025.0.x / JDK 17+ |
| MyBatis + MyBatis-Plus | 3.5.17 | XML Mapper 与 BaseMapper 都可用；GoldenDB 走 MySQL 驱动 |
| Vue | 3.5.x | Vue 3 |
| Vue Router | 4.6.x | Vue 3 稳定线 |
| Pinia | 4.x | peer：Vue ≥ 3.5.11 |
| Element Plus | 2.14.x | 仅支持 Vue 3 |
| Vite | 7.3.x | 与 Element Plus 生态匹配 |

## 模块说明

| 模块 | 类型 | 说明 |
| --- | --- | --- |
| `tech-finance-base` | 基础包 | 公共依赖 jar，**不是服务** |
| `tech-finance-gateway` | 服务 | API 网关 |
| `tech-finance-system-client` | Client | system Feign，供他服务调用 |
| `tech-finance-system-biz` | 服务 | 基础权限业务实现 |
| `tech-finance-workflow-client` | Client | workflow Feign |
| `tech-finance-workflow-biz` | 服务 | 业务流程业务实现 |
| `tech-finance-external-client` | Client | external Feign |
| `tech-finance-external-biz` | 服务 | 外部对接业务实现 |
| `frontend` | 前端 | Vue 3 + Element Plus |

## 快速开始

```bash
# 1. 环境变量
cp .env.example .env

# 2. 初始化库（GoldenDB / MySQL）
mysql -h127.0.0.1 -uroot -p < sql/schema.sql

# 3. 启动本地 Nacos（默认 127.0.0.1:8848）

# 4. 后端（需 JDK 17）
cd backend
mvn clean install
mvn -pl tech-finance-system/tech-finance-system-biz -am spring-boot:run
mvn -pl tech-finance-workflow/tech-finance-workflow-biz -am spring-boot:run
mvn -pl tech-finance-external/tech-finance-external-biz -am spring-boot:run
mvn -pl tech-finance-gateway spring-boot:run

# 5. 前端（Vue 3）
cd frontend
npm install
npm run dev
```

网关默认：`http://127.0.0.1:8080`

## 项目文档

详见根目录 [`project-docs/`](./project-docs/)。
