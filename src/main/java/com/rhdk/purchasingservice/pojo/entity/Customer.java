package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客户管理
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_CUSTOMER")
@KeySequence(value = "T_CUSTOMER_SEQ", clazz = Long.class)
public class Customer extends Model<Customer> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 客户名称
     */
    @TableField("CUS_NAME")
    private String cusName;
    /**
     * 企业统一社会信用代码
     */
    @TableField("CODE")
    private String code;
    /**
     * 简称
     */
    @TableField("SHORTNAME")
    private String shortname;
    /**
     * 所属公司id
     */
    @TableField("ORG_ID")
    private Long orgId;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
    private Long createBy;
    /**
     * 创建日期
     */
    @TableField(value ="CREATE_DATE",fill = FieldFill.INSERT)
    private Date createDate;
    /**
     * 修改人
     */
    @TableField(value = "UPDATE_BY", fill = FieldFill.UPDATE)
    private Long updateBy;
    /**
     * 修改日期
     */
    @TableField(value = "UPDATE_DATE", fill = FieldFill.UPDATE)
    private Date updateDate;
    /**
     * 逻辑删除（0:正常 1:删除）
     */
    @TableField("DEL_FLAG")
    @TableLogic
    private Integer delFlag;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
