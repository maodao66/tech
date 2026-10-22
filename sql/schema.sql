-- GoldenDB / MySQL 兼容
CREATE DATABASE IF NOT EXISTS tech_finance
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE tech_finance;

-- 业务表后续再加；公共字段约定：
-- id            varchar(64)     主键
-- create_user   varchar(64)     创建人
-- update_user   varchar(64)     修改人
-- owner_user    varchar(64)     归属人
-- create_inst   varchar(64)     创建机构
-- update_inst   varchar(64)     修改机构
-- owner_inst    varchar(64)     归属机构
-- create_time   datetime        创建时间
-- update_time   datetime        修改时间
-- own_time      datetime        归属时间
-- deleted       tinyint         删除标识（0正常 / 1删除）
-- version       int             数据版本（乐观锁）
