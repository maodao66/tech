package com.techfinance.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表公共字段，业务实体继承即可。
 * <p>
 * 系统表（tf_sys_*、批处理）主键用 {@code id}。
 * 业务表（投向/贷款/认定）主键用表缩写+numb（如 {@code ct_numb}），
 * 在子类上自行声明 {@code @TableId}，并视情况覆盖/忽略本类 {@code id}。
 */
@Data
public abstract class BaseEntity {

    /** 主键（系统表）；业务表可作技术号或不用 */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 创建人 */
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 修改人 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    /** 归属人 */
    @TableField(fill = FieldFill.INSERT)
    private String ownerUser;

    /** 创建机构 */
    @TableField(fill = FieldFill.INSERT)
    private String createInst;

    /** 修改机构 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateInst;

    /** 归属机构 */
    @TableField(fill = FieldFill.INSERT)
    private String ownerInst;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 修改时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 归属时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime ownTime;

    /** 删除标识：0 正常 / 1 删除 */
    @TableLogic
    private String deleted;

    /** 数据版本（乐观锁） */
    @Version
    @TableField(fill = FieldFill.INSERT)
    private String version;
}
