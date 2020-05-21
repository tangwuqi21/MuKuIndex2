package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * 资产模版属性
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_ASSET_TMPL_INFO")
@KeySequence(value = "T_ASSET_TMPL_INFO_SEQ", clazz = Long.class)
public class AssetTmplInfo extends Model<AssetTmplInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id（资产模板id）
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 资产类别id
     */
    @TableField("ASSET_CAT_ID")
    private Long assetCatId;
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
     * 版本号(1,2,3...)
     */
    @TableField("VER_NO")
    private Integer verNo;
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
    /**
     * 库管方式（1:量管 2:物管）
     */
    @TableField("WM_TYPE")
    private Integer wmType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
