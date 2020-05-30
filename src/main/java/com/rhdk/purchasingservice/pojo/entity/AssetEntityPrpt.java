package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 资产属性值
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_ASSET_ENTITY_PRPT")
@KeySequence(value = "T_ASSET_ENTITY_PRPT_SEQ", clazz = Long.class)
public class AssetEntityPrpt extends Model<AssetEntityPrpt> {

  private static final long serialVersionUID = 1L;

  /** 自增id */
  @TableId(value = "ID", type = IdType.INPUT)
  private Long id;
  /** 资产id */
  @TableField("ASSET_ID")
  private Long assetId;
  /** 资产属性id */
  @TableField("PRPT_ID")
  private Long prptId;
  /** 属性代码 */
  @TableField("CODE")
  private String code;
  /** 属性值参照id(选择项时) */
  @TableField("PRPT_VAL_ID")
  private Long prptValId;
  /** 属性值 */
  @TableField("VAL")
  private String val;
  /** 创建人 */
  @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
  private Long createBy;
  /** 创建日期 */
  @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
  private Date createDate;
  /** 修改人 */
  @TableField(value = "UPDATE_BY", fill = FieldFill.UPDATE)
  private Long updateBy;
  /** 修改日期 */
  @TableField(value = "UPDATE_DATE", fill = FieldFill.UPDATE)
  private Date updateDate;
  /** 逻辑删除（0:正常 1:删除） */
  @TableField("DEL_FLAG")
  @TableLogic
  private Integer delFlag;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
