# 科技金融管理系统一期库表设计（CR262533）

> 范围：需求书标明「一阶段实现」的投向管理、科技贷款认定、后管基础。  
> 库：GoldenDB（MySQL 协议）。公共字段与 `BaseEntity` 一致。  
> 命名：业务表统一 `tf_` 前缀。

## 1. 主键与公共字段

### 主键

| 范围 | 主键 |
| --- | --- |
| 系统表 `tf_sys_*`、批处理 | `id`（不变） |
| 业务表（投向 / 贷款 / 认定） | 表名缩写 + `numb`；`id` 可选非主键 |

业务表主键对照：

| 表 | 缩写 | 主键 |
| --- | --- | --- |
| tf_loan_detail | ld | ld_numb |
| tf_loan_handler | lh | lh_numb |
| tf_cert_task | ct | ct_numb |
| tf_cert_task_map | ctm | ctm_numb |
| tf_cert_flow | cf | cf_numb |
| tf_cert_attach | ca | ca_numb |
| tf_cert_sync_log | csl | csl_numb |
| tf_ind_industry | ii | ii_numb |
| tf_ind_industry_chg | iic | iic_numb |
| tf_ind_tech_node | itn | itn_numb |
| tf_ind_invest_rule | iir | iir_numb |

业务表之间关联用对方 `*_numb`（如 `ct_numb`、`ld_numb`、`category_itn_numb`）。

### 字段顺序

`id` → 本表业务字段（业务表含 `*_numb`）→ 公共字段（`create_user` … `version`）

### 公共字段（每张表都带，放在表末尾）

| 列 | 类型 | 说明 |
| --- | --- | --- |
| id | varchar(64) | 第一列；系统表主键；业务表可选技术号 |
| create_user / update_user / owner_user | varchar(64) | 人 |
| create_inst / update_inst / owner_inst | varchar(64) | 机构 |
| create_time / update_time / own_time | datetime | 时间 |
| deleted | varchar(8) | 0正常 / 1删除 |
| version | varchar(16) | 乐观锁 |

以下各表只列业务字段（含本表主键字段时从略）。

**关联约定**：活数据只存编码/`*_numb`，名称查询时关联主表；主数据改名后页面自动看到新名称。名称快照只写在变更历史、审批流水 `snapshot_json` 等留痕字段里。

---

## 2. 模块与表一览

| 模块 | 表名 | 说明 |
| --- | --- | --- |
| 投向 | `tf_ind_industry` | 国民经济行业 |
| 投向 | `tf_ind_industry_chg` | 行业变更留痕 |
| 投向 | `tf_ind_tech_node` | 科技金融产业树（门类→重点产品） |
| 投向 | `tf_ind_invest_rule` | 投向规则（行业 ↔ 产业） |
| 认定 | `tf_loan_detail` | 数仓同步的贷款明细 |
| 认定 | `tf_loan_handler` | 机构经办人 |
| 认定 | `tf_cert_task` | 认定待办 / 主单 |
| 认定 | `tf_cert_task_map` | 任务上的投向映射（可多条） |
| 认定 | `tf_cert_flow` | 认定 / 调整审批流水 |
| 认定 | `tf_cert_attach` | 补充材料 |
| 认定 | `tf_cert_sync_log` | 认定结果同步监管集市日志 |
| 后管 | `tf_sys_org` | 机构（综管同步） |
| 后管 | `tf_sys_user` | 用户 / 行员 |
| 后管 | `tf_sys_role` | 角色 |
| 后管 | `tf_sys_perm` | 权限 / 菜单 |
| 后管 | `tf_sys_user_role` | 用户-角色 |
| 后管 | `tf_sys_role_perm` | 角色-权限 |
| 后管 | `tf_sys_ops_flow` | 后管敏感操作审核 |
| 后管 | `tf_sys_audit_log` | 操作审计 |
| 公共 | `tf_batch_job_log` | 日终批处理日志 |

---

## 3. 投向管理

### 3.1 `tf_ind_industry` 国民经济行业

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| industry_code | varchar(16) | 国民经济行业代码，业务唯一 |
| industry_name | varchar(450) | 国民经济行业名称 |
| status | varchar(16) | 1有效 / 0已删除 |
| sync_date | date | 最近同步日期 |
| source_system | varchar(64) | 来源：信贷 / 票据 / 产数 等 |

索引：`uk_industry_code(industry_code)`，`idx_name(industry_name(64))`。

### 3.2 `tf_ind_industry_chg` 行业变更记录

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| industry_id | varchar(64) | 关联当前行业记录 |
| before_code / before_name | varchar | 变更前 |
| after_code / after_name | varchar | 变更后 |
| change_type | varchar(16) | 1新增 / 2修改 / 3删除 |
| change_date | date | 变更日期 |

### 3.3 `tf_ind_tech_node` 科技金融产业树

树形存门类、大类、中类、小类、细类、重点产品和服务。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| parent_id | varchar(64) | 父节点，根为 null |
| node_level | tinyint | 1 门类 2 大类 3 中类 4 小类 5 细类 6 重点产品 |
| node_code | varchar(64) | 节点编码（大类代码等） |
| node_name | varchar(256) | 节点名称 |
| major_code | varchar(64) | 大类代码（level=2 时与 node_code 可互映） |
| status | varchar(16) | 1启用 / 0停用 |
| sort_no | varchar(16) | 排序 |

停用前校验：是否仍被 `tf_ind_invest_rule` 引用；停用走后管审核。

### 3.4 `tf_ind_invest_rule` 投向规则

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| industry_code | varchar(16) | 国民经济行业代码 → `tf_ind_industry` |
| category_id / major_id / mid_id / minor_id / fine_id / product_id | varchar(64) | 产业树节点 → `tf_ind_tech_node` |
| status | varchar(16) | 0草稿 / 1待审 / 2生效 / 3停用 |
| effective_date | date | 审批通过生效日 |

不存行业/产业名称。未停用规则按 `industry_code` + 各层节点做重复校验。新增 / 修改 / 停用均走 `tf_sys_ops_flow`。

---

## 4. 科技贷款认定

### 4.1 `tf_loan_detail` 贷款明细（数仓日终）

只存认定需要的关键字段，明细来源可在备注里写明源系统。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| loan_key | varchar(128) | 贷款业务唯一键（源系统+账号/借据等拼） |
| source_system | varchar(64) | 信贷 / 票据 / 产数 / 国结 等 |
| cust_no | varchar(64) | 客户号（无客户主数据；名称可放 raw_json） |
| account_no / iou_no | varchar | 账号 / 借据号 |
| industry_code | varchar(16) | 贷款投向行业 → `tf_ind_industry` |
| branch_l1_code / l2 / sub | varchar | 一二级分行、支行 → `tf_sys_org` |
| manager_no | varchar(64) | 主办客户经理 → `tf_sys_user` |
| loan_amt / loan_bal | varchar(32) | 金额 / 余额 |
| currency | varchar(8) | 币种 |
| value_date / mature_date | date | 起息 / 到期 |
| data_date | date | 数仓数据日期 |
| raw_json | mediumtext | 源端原始行（可含客户名称等） |

索引：`uk_loan_key(loan_key)`，`idx_data_date(data_date)`，`idx_manager(manager_no)`，`idx_industry(industry_code)`。

### 4.2 `tf_loan_handler` 机构经办人

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| branch_l1_code / branch_sub_code | varchar | 分行、支行 → `tf_sys_org` |
| handler_l1_no / handler_sub_no | varchar | 经办人 → `tf_sys_user` |
| status | varchar(16) | 1启用 / 0停用 |

### 4.3 `tf_cert_task` 认定任务

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| loan_id | varchar(64) | 关联 `tf_loan_detail.id` |
| loan_key | varchar(128) | 冗余 |
| branch_l1_code / handler_l1_no | varchar | 一级分行及经办人（只存编码） |
| branch_l2_code / handler_l2_no | varchar | 二级分行及经办人 |
| branch_sub_code / handler_sub_no | varchar | 支行及经办人（经理自贷款明细） |
| task_gen_date | date | 待办生成日 |
| task_status | varchar(32) | 见下表 |
| is_tech | varchar(8) | 1是 / 0否，规则映射初值，可改 |
| modify_reason | varchar(500) | 系统认定是、改为否时必填 |
| process_date | date | 任务处理日 |
| auditor / audit_date | varchar / date | 认定审核 |
| adj_applicant / adj_apply_date | varchar / date | 调整申请 |
| adj_auditor / adj_audit_date | varchar / date | 调整审核 |
| adj_approver / adj_approve_date | varchar / date | 调整审批 |
| locked | varchar(8) | 0未锁定 / 1已锁定 |
| result_sync_flag | varchar(16) | 0待同步 / 1已同步 / 2失败 |

**task_status 码值**

| 值 | 含义 |
| --- | --- |
| 0 | 待认定 |
| 1 | 已认定待审核 |
| 2 | 审核退回 |
| 3 | 已锁定 |
| 4 | 调整申请中 |
| 5 | 调整审核中 |
| 6 | 调整审批中 |

### 4.4 `tf_cert_task_map` 任务投向映射

一条任务可对应多条投向（规则一对多）。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| task_id | varchar(64) | 认定任务 |
| rule_id | varchar(64) | 命中规则，可空 |
| category_id / major_id / mid_id / minor_id / fine_id / product_id | varchar(64) | 产业节点 → `tf_ind_tech_node` |
| map_source | varchar(16) | 1自动 / 2手工 |

### 4.5 `tf_cert_flow` 认定 / 调整流水

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| task_id | varchar(64) | 任务 |
| flow_type | varchar(32) | 1认定提交 / 2认定审核 / 3调整申请 / 4调整审核 / 5调整审批 |
| action | varchar(16) | 1提交 / 2通过 / 3驳回 |
| operator / operate_time | varchar / datetime | 操作人、时间 |
| opinion | varchar(500) | 意见 |
| snapshot_json | mediumtext | 锁定/审批时写入的投向与机构人员名称快照，不随主数据变更 |

### 4.6 `tf_cert_attach` 补充材料

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| task_id | varchar(64) | 任务 |
| file_name | varchar(256) | 文件名 |
| file_type | varchar(32) | 类型 |
| file_url | varchar(512) | 对象存储路径 |
| lake_sync_flag | varchar(16) | 0待同步 / 1已同步 / 2失败 |

### 4.7 `tf_cert_sync_log` 结果同步日志

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| data_date | date | 同步批次日 |
| task_id / loan_key | varchar | 业务键 |
| sync_type | varchar(16) | 1新增 / 2修改 |
| sync_status | varchar(16) | 1成功 / 0失败 |
| error_msg | varchar(1000) | 失败原因 |
| file_name | varchar(256) | 下发文件名 |

---

## 5. 后管基础

### 5.1 `tf_sys_org` 机构

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| org_code | varchar(64) | 机构号，唯一 |
| org_name | varchar(256) | 名称 |
| org_level | tinyint | 1 总行 2 一级分行 3 二级分行 4 支行 |
| parent_code | varchar(64) | 上级 |
| status | varchar(16) | 1启用 / 0停用 |

### 5.2 `tf_sys_user` 用户

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| user_no | varchar(64) | 行员号，唯一 |
| user_name | varchar(128) | 姓名 |
| org_code | varchar(64) | 所属机构，**可空** |
| mobile | varchar(32) | 手机（如需） |
| status | varchar(16) | 1启用 / 0停用 |
| last_login_time | datetime | 最近登录 |

扫码登录只做会话，不另建密码表。

初始化可先建用户、后挂机构：`org_code` 允许为空，不强制机构表已有对应数据；综管同步或后管维护后再写入。业务权限隔离时，无机构用户需单独约定（禁止登录 / 仅总行可见等）。

### 5.3 `tf_sys_role` / `tf_sys_perm`

- 角色：`role_code`、`role_name`、`status`（1启用/0停用）、`remark`
- 权限：`perm_code`、`perm_name`、`perm_type`（1菜单/2按钮/3接口）、`parent_id`、`path`、`sort_no`

关联表：`tf_sys_user_role`、`tf_sys_role_perm`。

### 5.4 `tf_sys_ops_flow` 后管操作审核

覆盖：投向规则新增/修改/停用、产业信息停用、用户/权限变更等。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| biz_type | varchar(64) | 1投向规则 / 2产业节点 / 3用户 / 4角色 / 5权限 |
| biz_id | varchar(64) | 业务主键 |
| action | varchar(32) | 1新增 / 2修改 / 3停用 / 4启用 |
| apply_user / apply_time | varchar / datetime | 申请人 |
| audit_user / audit_time | varchar / datetime | 审核人 |
| status | varchar(16) | 0待审 / 1通过 / 2驳回 |
| before_json / after_json | mediumtext | 变更前后 |
| opinion | varchar(500) | 审核意见 |

### 5.5 `tf_sys_audit_log` 审计

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| user_no / user_name | varchar | 操作人 |
| org_code | varchar(64) | 机构 |
| module | varchar(64) | 模块 |
| action | varchar(64) | 动作 |
| biz_id | varchar(64) | 业务键 |
| req_uri / req_ip | varchar | 请求 |
| detail | varchar(2000) | 摘要 |
| operate_time | datetime | 时间 |

---

## 6. 批处理

### `tf_batch_job_log`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| job_code | varchar(64) | INDUSTRY_SYNC / LOAN_SYNC / CERT_TASK_GEN / CERT_RESULT_SYNC / ORG_USER_SYNC … |
| data_date | date | 业务日 |
| start_time / end_time | datetime | 起止 |
| status | varchar(16) | 0运行中 / 1成功 / 2失败 |
| total_cnt / success_cnt / fail_cnt | int | 计数 |
| message | varchar(1000) | 说明 |
| checkpoint | varchar(256) | 断点续跑 |

---

## 7. 关系示意

```text
行业(industry) ──变更(chg)
产业树(tech_node)
        │
        └─► 投向规则(invest_rule) ──► 认定任务映射(task_map)
                                            ▲
贷款明细(loan_detail) ──► 认定任务(cert_task) ──► 流水(flow) / 附件(attach)
机构经办人(handler) ─────┘                      └─► 同步日志(sync_log)

用户(user) / 机构(org) / 角色(role) / 权限(perm)
后管审核(ops_flow) 覆盖规则、产业停用、用户权限变更
审计(audit_log) 全模块写操作
```

---

## 8. 不在本期落表

- 科技企业全景画像、名单制营销、驾驶舱指标宽表
- 监管报送口径模型、报表加工中间表（本期只做到认定结果文件出库）
- 客户主数据（需求明确：本系统不存）

---

## 9. DDL 落点

完整建表语句见根目录 [`sql/schema.sql`](../sql/schema.sql)。
