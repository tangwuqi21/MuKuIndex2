package com.rhdk.purchasingservice.pojo.query;

import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 合同表
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Getter
@Setter
public class OrderContractQuery extends BaseDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "ID", hidden = true)
  private Long id;

  @ApiModelProperty(value = "单据日期", hidden = true)
  @NotNull(message = "单据日期不能为空")
  private Date contractDate;

  @ApiModelProperty(value = "单据编码", hidden = true)
  @NotNull(message = "单据编码不能为空")
  private String contractCode;

  @ApiModelProperty(value = "合同名称", hidden = true)
  @NotNull(message = "合同名称不能为空")
  private String contractName;

  @ApiModelProperty(value = "往来单位")
  @NotNull(message = "往来单位不能为空")
  private String contractCompany;

  @ApiModelProperty(value = "合同类型,1-采购合同", hidden = true)
  @NotNull(message = "合同类型不能为空")
  private Integer contractType;

  @ApiModelProperty(value = "合同金额", hidden = true)
  @NotNull(message = "合同金额不能为空")
  private Long contractMoney;

  @ApiModelProperty(value = "附件", hidden = true)
  @NotNull(message = "合同附件不能为空")
  private List<OrderAttachmentQuery> attachmentList;

  @ApiModelProperty(value = "备注", hidden = true)
  private String remark;
}
