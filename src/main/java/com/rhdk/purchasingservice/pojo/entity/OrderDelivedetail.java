package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * 送货明细
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_ORDER_DELIVEDETAIL")
@KeySequence(value = "T_ORDER_DELIVEDETAIL_SEQ", clazz = Long.class)
public class OrderDelivedetail extends Model<OrderDelivedetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 中间表id
     */
    @TableField("MIDDLE_ID")
    private Long middleId;

    @TableField("ASSET_CAT_ID")
    private Long assetCatId;

    @TableField("ASSET_CAT_SEARCH_KEY")
    private String assetCatSearchKey;

    /**
     * 资产名称
     */
    @TableField("ASSET_NAME")
    private String assetName;
    /**
     * 物料号
     */
    @TableField("ITEM_NO")
    private String itemNo;
    /**
     * 资产Id
     */
    @TableField("ASSET_ID")
    private Long assetId;
    /**
     * 数量（量化管理时必需）
     */
    @TableField("ASSET_NUMBER")
    private Long assetNumber;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
    private Long createBy;
    /**
     * 创建日期
     */
    @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
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

    /**
     * 保存状态，（0-暂存，1-已提交）
     */
    @TableField("SAVE_STATUS")
    private Long saveStatus;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
