# GoldenDB / 数据层说明

GoldenDB 与 MySQL 协议兼容，使用 `mysql-connector-j` 即可。

## 已接入服务

- `tech-finance-system`
- `tech-finance-workflow`

网关、外部对接服务默认不连库。

## 技术选型

| 层 | 说明 |
| --- | --- |
| MyBatis | 原生能力：接口 + XML（`resources/mapper/**/*.xml`） |
| MyBatis-Plus `3.5.17` | 基于 MyBatis 增强：`BaseMapper`、分页、自动填充、逻辑删除 |
| 驱动 | `mysql-connector-j`（GoldenDB 兼容） |

> 不要再单独引入 `mybatis` / `mybatis-spring-boot-starter`，以免与 Plus 版本冲突。Plus 已包含 MyBatis。

## 代码落点

- 公共实体：`com.techfinance.base.entity.BaseEntity`
- 公共配置：`com.techfinance.base.config.MybatisConfig`
- 分层：`controller` → `service` → `service.impl` → `dao`
- Dao 接口：各服务 `*.dao` 包（`@MapperScan`）
- Dao XML：各服务 `src/main/resources/mapper/`
- 实体：各服务 `*.entity` 包

## 配置要点

```yaml
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.techfinance.xxx
  configuration:
    map-underscore-to-camel-case: true
```

数据源：

```yaml
spring:
  datasource:
    url: jdbc:mysql://host:port/tech_finance?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: your_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 初始化

```bash
mysql -h127.0.0.1 -uroot -p < sql/schema.sql
```

业务表后续补充：实体继承 `BaseEntity`；简单 CRUD 用 `BaseMapper`，复杂 SQL 写 MyBatis XML。

公共字段：

| 字段 | 列名 | 说明 |
| --- | --- | --- |
| id | id | varchar(64) 主键 |
| createUser | create_user | 创建人 |
| updateUser | update_user | 修改人 |
| ownerUser | owner_user | 归属人 |
| createInst | create_inst | 创建机构 |
| updateInst | update_inst | 修改机构 |
| ownerInst | owner_inst | 归属机构 |
| createTime | create_time | 创建时间 |
| updateTime | update_time | 修改时间 |
| ownTime | own_time | 归属时间 |
| deleted | deleted | 删除标识 |
| version | version | 数据版本（乐观锁） |

时间、版本由 `MybatisConfig` 自动填充；人员/机构字段由业务写入当前用户与机构。
