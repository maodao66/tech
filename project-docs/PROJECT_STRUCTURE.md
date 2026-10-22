# 项目结构

```text
.
├── backend/
│   ├── tech-finance-base/                 # 基础包（非服务）
│   ├── tech-finance-gateway/              # 网关
│   ├── tech-finance-system/
│   │   ├── tech-finance-system-client/    # Feign（服务间调用）
│   │   └── tech-finance-system-biz/       # controller/service/impl/dao/entity
│   ├── tech-finance-workflow/
│   │   ├── tech-finance-workflow-client/
│   │   └── tech-finance-workflow-biz/
│   └── tech-finance-external/
│       ├── tech-finance-external-client/
│       └── tech-finance-external-biz/
├── frontend/
├── sql/schema.sql
├── project-docs/                  # 项目文档（本目录）
├── .env.example
└── README.md
```


## biz 分层

对前端：

```text
controller → service → service.impl → dao(+xml) / entity
```

服务间（Feign）：

```text
*-client(Feign) → feign.impl(FeignImpl) → service → service.impl → dao
```

- `*-client`：Feign 契约（给消费方依赖）
- `feign.impl`：实现契约的 `@RestController`，内部只调 Service
- 不直接调对方 controller

## 服务间调用示例

```xml
<dependency>
    <groupId>com.techfinance</groupId>
    <artifactId>tech-finance-system-client</artifactId>
</dependency>
```

```java
@Resource
private SystemClient systemClient;
```

注册中心：Nacos  
数据层：MyBatis（XML）+ MyBatis-Plus + GoldenDB（MySQL 兼容）  
暂不使用 Docker。
