package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 送货记录明细中间表
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_ORDER_DELIVEMIDDLE")
@KeySequence(value = "T_ORDER_DELIVEMIDDLE_SEQ", clazz = Long.class)
public class OrderDelivemiddle extends Model<OrderDelivemiddle> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 送货记录id
     */
    @TableField("DELIVERY_ID")
    private Long deliveryId;
    /**
     * 单据号
     */
    @TableField("DELIVERYDETAIL_CODE")
    private String deliverydetailCode;
    /**
     * 签收单号
     */
    @TableField("SIGN_NO")
    private String signNo;
    /**
     * 资产模板id
     */
    @TableField("MODULE_ID")
    private Long moduleId;
    /**
     * 单位
     */
    @TableField("ASSET_UNIT")
    private String assetUnit;
    /**
     * 数量
     */
    @TableField("ASSET_NUMBER")
    private Long assetNumber;
    /**
     * 单价
     */
    @TableField("ASSET_PRICE")
    private Long assetPrice;
    /**
     * 累计金额
     */
    @TableField("TOTAL_MONEY")
    private Long totalMoney;
    /**
     * 删除标识，0-正常，1-已删除
     */
    @TableField("DEL_FLAG")
    @TableLogic
    private Integer delFlag;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
    private Long createBy;
    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_DATE", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateDate;
    /**
     * 修改人
     */
    @TableField(value = "UPDATE_BY", fill = FieldFill.UPDATE)
    private Long updateBy;
    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
