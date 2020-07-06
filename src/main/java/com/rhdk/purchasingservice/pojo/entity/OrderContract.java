package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 合同表
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_CONTRACT")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@KeySequence(value = "T_ORDER_CONTRACT_SEQ", clazz = Long.class)
public class OrderContract extends Model<OrderContract> {

  private static final long serialVersionUID = 1L;

  /** ID */
  @TableId(value = "ID", type = IdType.INPUT)
  private Long id;
  /** 单据日期 */
  @TableField("CONTRACT_DATE")
  private Date contractDate;
  /** 单据编码 */
  @TableField("CONTRACT_CODE")
  private String contractCode;
  /** 合同名称 */
  @TableField("CONTRACT_NAME")
  private String contractName;
  /** 合同类型,1-采购合同 */
  @TableField("CONTRACT_TYPE")
  private Integer contractType;
  /** 合同金额 */
  @TableField("CONTRACT_MONEY")
  private String contractMoney;

  /** 删除标识，0-正常，1-已删除 */
  @TableField("DEL_FLAG")
  @TableLogic
  private Integer delFlag;
  /** 创建时间 */
  @TableField(value = "CREATE_DATE", fill = FieldFill.INSERT)
  private Date createDate;
  /** 创建人 */
  @TableField(value = "CREATE_BY", fill = FieldFill.INSERT)
  private Long createBy;
  /** 修改时间 */
  @TableField(value = "UPDATE_DATE", fill = FieldFill.UPDATE)
  private Date updateDate;
  /** 修改人 */
  @TableField(value = "UPDATE_BY", fill = FieldFill.UPDATE)
  private Long updateBy;
  /** 备注 */
  @TableField("REMARK")
  private String remark;

  @TableField("ORG_ID")
  private Long orgId;

  /** 议案id */
  @TableField("MOTION_ID")
  private Long motionId;
  /** 项目id */
  @TableField("PROJECT_ID")
  private Long projectId;
  /** 项目经理id */
  @TableField("PROJECT_MANAGER_ID")
  private String projectManagerId;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
