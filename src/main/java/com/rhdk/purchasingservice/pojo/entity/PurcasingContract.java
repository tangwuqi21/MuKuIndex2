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
 * 采购合同
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_ORDER_CONTRACT")
@KeySequence(value = "T_ORDER_CONTRACT_SEQ", clazz = Long.class)
public class PurcasingContract extends Model<PurcasingContract> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 合同id
     */
    @TableField("CONTRACT_ID")
    private Long contractId;
    /**
     * 往来单位
     */
    @TableField("CONTRACT_COMPANY")
    private String contractCompany;
    /**
     * 订单状态（0:未完成 1:完成 2:部分完成）
     */
    @TableField("STATUS")
    private Integer status;
    /**
     * 描述
     */
    @TableField("DSCP")
    private String dscp;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
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
