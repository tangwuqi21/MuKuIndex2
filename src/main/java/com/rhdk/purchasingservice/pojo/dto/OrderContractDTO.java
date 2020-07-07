package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class OrderContractDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "ID")
  private Long id;

  @ApiModelProperty(value = "单据日期")
  @NotNull(message = "单据日期不能为空")
  private Date contractDate;

  @ApiModelProperty(value = "单据编码", hidden = true)
  private String contractCode;

  @ApiModelProperty(value = "合同主体id", hidden = true)
  private Long contractId;

  @ApiModelProperty(value = "合同名称")
  @NotNull(message = "合同名称不能为空")
  @Size(max = 50, message = "合同名称过长，只能输入50个字符以内的名称")
  private String contractName;

  @ApiModelProperty(value = "往来单位", hidden = true)
  @NotNull(message = "往来单位不能为空")
  private String contractCompany;

  @ApiModelProperty(value = "合同类型,1-采购合同；2-经租合同;")
  @NotNull(message = "合同类型不能为空")
  private Integer contractType;

  @ApiModelProperty(value = "合同金额")
  @NotNull(message = "合同金额不能为空")
  private String contractMoney;

  @ApiModelProperty(value = "附件")
  private List<OrderAttachmentDTO> attachmentList;

  @ApiModelProperty(value = "备注")
  @Size(max = 100, message = "备注过长，只能输入100个字符以内的备注")
  private String remark;

  @ApiModelProperty(value = "议案id")
  @NotNull(message = "议案id不能为空")
  private Long motionId;

  //  @ApiModelProperty(value = "项目id")
  //  @NotNull(message = "项目id不能为空")
  //  private Long projectId;

  @ApiModelProperty(value = "项目经理")
  @NotNull(message = "项目经理不能为空")
  private String projectManagerId;

  @ApiModelProperty(value = "商业伙伴列表")
  @NotEmpty(message = "商业伙伴不能为空")
  @Valid
  private List<ContractCustDTO> contractCustList;
}
