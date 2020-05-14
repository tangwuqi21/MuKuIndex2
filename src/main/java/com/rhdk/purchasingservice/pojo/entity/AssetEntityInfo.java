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
 * 资产
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_ASSET_ENTITY_INFO")
@KeySequence(value = "T_ASSET_ENTITY_INFO_SEQ", clazz = Long.class)
public class AssetEntityInfo extends Model<AssetEntityInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 资产类别id
     */
    @TableField("ASSET_CAT_ID")
    private Long assetCatId;
    /**
     * 资产模板id
     */
    @TableField("ASSET_TEMPL_ID")
    private Long assetTemplId;
    /**
     * 资产模板版本号
     */
    @TableField("ASSET_TEMPL_VER")
    private Integer assetTemplVer;
    /**
     * 名称
     */
    @TableField("NAME")
    private String name;
    /**
     * 资产业务编码
     */
    @TableField("ITEM_NO")
    private String itemNo;
    /**
     * 仓库id
     */
    @TableField("WAREHOUSE_ID")
    private Long warehouseId;
    /**
     * 资产标识 （0:虚资产 1:库存 2:消耗）
     */
    @TableField("ASSET_TYPE")
    private Integer assetType;
    /**
     * 采购入库单id（物化时需要）
     */
    @TableField("CHECKIN_ID")
    private Long checkinId;
    /**
     * 数量（量化管理时必需）
     */
    @TableField("AMOUNT")
    private Long amount;
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
