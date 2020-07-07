package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 合同客户
 *
 * @author YYF
 * @since 2020-06-12
 */
@Getter
@Setter
public class ContractCustDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  // @ApiModelProperty(value = "自增id")
  // private Long id;

  @ApiModelProperty(value = "合同id", hidden = true)
  private Long contractId;

  @ApiModelProperty(value = "客户id")
  @NotNull(message = "客户id不能为空")
  private Long custId;

  @ApiModelProperty(value = "客户类型(1.主承租人2.副承租人)")
  @NotBlank(message = "客户类型不能为空")
  private String custType;

  @ApiModelProperty(value = "银行账户id")
  @NotNull(message = "银行账户id不能为空")
  private Long bankAccountId;

  @ApiModelProperty(value = "描述", hidden = true)
  private String dscp;

  @ApiModelProperty(value = "创建人", hidden = true)
  private Long createBy;

  @ApiModelProperty(value = "创建日期", hidden = true)
  private Date createDate;

  @ApiModelProperty(value = "修改人", hidden = true)
  private Long updateBy;

  @ApiModelProperty(value = "修改日期", hidden = true)
  private Date updateDate;

  @ApiModelProperty(value = "所属公司id", hidden = true)
  private Long orgId;

  @ApiModelProperty(value = "逻辑删除（0:正常 1:删除）", hidden = true)
  private Integer delFlag;
}
