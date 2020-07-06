package com.rhdk.purchasingservice.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户信息展示类
 *
 * @author LMYOU
 * @since 2020-05-14
 */
@Data
public class CustomerInfoVO implements Serializable {
  private static final long serialVersionUID = 1L;
  /** 主键id */
  @ApiModelProperty(value = "自增id")
  private Long id;
  /** 客户id */
  @ApiModelProperty(value = "客户id")
  private Long custId;
  /** 客户名称 */
  @ApiModelProperty(value = "客户名称")
  private String custName;

  /** 统一社会信用证代码 */
  @ApiModelProperty(value = "统一社会信用证代码")
  private String unifiedSocialCredit;

  /** 法定代表人或授权代表 */
  @ApiModelProperty(value = "法定代表人或授权代表")
  private String legalPerson;

  /** 客户地址 */
  @ApiModelProperty(value = "客户地址")
  private String address;

  /** 类型id */
  /*@ApiModelProperty(value = "类型id")
  private Long typeId;*/
  /** 伙伴类型 */
  @ApiModelProperty(value = "伙伴类型id")
  private String custType;

  /** 伙伴类型名称 */
  @ApiModelProperty(value = "伙伴类型名称")
  private String custTypeName;

  /** 银行id */
  @ApiModelProperty(value = "银行id")
  private Long bankId;
  /** 银行账号 */
  @ApiModelProperty(value = "银行账号")
  private String bankAccountNum;
  /** 账户名 */
  @ApiModelProperty(value = "账户名")
  private String bankAccountName;

  /** 银行全称 */
  @ApiModelProperty(value = "银行全称")
  private String bankFullName;

  /** 分行名称 */
  @ApiModelProperty(value = "分行名称")
  private String bankBranchName;
}
