package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 送货单
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_ORDER_DELIVERECORDS")
@KeySequence(value = "T_ORDER_DELIVERECORDS_SEQ", clazz = Long.class)
public class OrderDeliverecords extends Model<OrderDeliverecords> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 单据编码
     */
    @TableField("DELIVERY_CODE")
    private String deliveryCode;
    /**
     * 送货单名称
     */
    @TableField("DELIVERY_NAME")
    private String deliveryName;
    /**
     * 单据日期
     */
    @TableField("DELIVERY_DATE")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date deliveryDate;
    /**
     * 供应商id
     */
    @TableField("SUPPLIER_ID")
    private Long supplierId;
    /**
     * 签收地点
     */
    @TableField("SIGN_ADDRESS")
    private String signAddress;

    /**
     * 采购单id
     */
    @TableField("ORDER_ID")
    private Long orderId;
    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
    private Long createBy;
    /**
     * 创建日期
     */
    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
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
     * 所属公司id
     */
    @TableField("ORG_ID")
    private Long orgId;
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
