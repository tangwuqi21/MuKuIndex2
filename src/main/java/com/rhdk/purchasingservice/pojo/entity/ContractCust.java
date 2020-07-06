package com.rhdk.purchasingservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 合同客户
 *
 * @author YYF
 * @since 2020-06-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("T_CONTRACT_CUST")
@KeySequence(value = "T_CONTRACT_CUST_SEQ", clazz = Long.class)
public class ContractCust extends Model<ContractCust> {

  private static final long serialVersionUID = 1L;

  /** 自增id */
  @TableId(value = "ID", type = IdType.INPUT)
  private Long id;
  /** 合同id */
  @TableField("CONTRACT_ID")
  private Long contractId;
  /** 客户id */
  @TableField("CUST_ID")
  private Long custId;
  /** 客户类型 */
  @TableField("CUST_TYPE")
  private String custType;
  /** 银行账户id */
  @TableField("BANK_ACCOUNT_ID")
  private Long bankAccountId;
  /** 描述 */
  @TableField("DSCP")
  private String dscp;
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
  /** 所属公司id */
  @TableField(value = "ORG_ID", fill = FieldFill.INSERT)
  private Long orgId;
  /** 逻辑删除（0:正常 1:删除） */
  @TableField("DEL_FLAG")
  @TableLogic
  private Integer delFlag;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
