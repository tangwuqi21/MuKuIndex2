package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 合同客户
 *
 * @author YYF
 * @since 2020-06-12
 */
@Getter
@Setter
public class QueryContractCustDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "客户id")
  private Long custId;

  @ApiModelProperty(value = "客户类型")
  private String custType;

  @ApiModelProperty(value = "银行账户id")
  private Long bankAccountId;
}
