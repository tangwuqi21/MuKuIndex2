package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 送货单
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Getter
@Setter
public class OrderDeliverecordsDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "ID")
  private Long id;

  @ApiModelProperty(value = "单据日期")
  @NotNull(message = "单据日期不能为空")
  private Date deliveryDate;

  @ApiModelProperty(value = "单据编码")
  private String deliveryCode;

  @ApiModelProperty(value = "送货记录名称")
  @NotNull(message = "送货记录名称不能为空")
  private String deliveryName;

  @ApiModelProperty(value = "供应商id")
  @NotNull(message = "供应商不能为空")
  private Long supplierId;

  @ApiModelProperty(value = "签收地点")
  @NotNull(message = "签收地点不能为空")
  private String signAddress;

  @ApiModelProperty(value = "采购单id")
  private Long orderId;

  @ApiModelProperty(value = "送货附件")
  @NotNull(message = "送货附件不能为空")
  private List<OrderAttachmentDTO> attachmentList;

  @ApiModelProperty(value = "所属公司id", hidden = true)
  private Long orgId;

  @ApiModelProperty(value = "明细记录列表")
  @NotNull(message = "明细记录不能为空")
  private List<OrderDelivemiddleDTO> orderDelivemiddleDTOList;

  @ApiModelProperty(value = "送货记录备注")
  private String remark;
}
