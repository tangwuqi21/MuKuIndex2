package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 送货单
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDeliverecordsVO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "自增id")
  private Long id;

  @ApiModelProperty(value = "单据编码")
  private String deliveryCode;

  @ApiModelProperty(value = "送货单名称")
  private String deliveryName;

  @ApiModelProperty(value = "供应商名称")
  private String supplierName;

  @ApiModelProperty(value = "源单号")
  private String contractCode;

  @ApiModelProperty(value = "源单类型")
  private Integer contractType;

  @ApiModelProperty(value = "源单类型名称", hidden = true)
  private String contractTypeName;

  @ApiModelProperty(value = "源单名称")
  private String contractName;

  @ApiModelProperty(value = "单据日期")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date deliveryDate;

  @ApiModelProperty(value = "供应商id")
  private Long supplierId;

  @ApiModelProperty(value = "签收地点")
  private String signAddress;

  @ApiModelProperty(value = "采购单id")
  private Long orderId;

  @ApiModelProperty(value = "备注")
  private String remark;

  @ApiModelProperty(value = "创建人")
  private Long createBy;

  @ApiModelProperty(value = "创建日期")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createDate;

  @ApiModelProperty(value = "所属公司id")
  private Long orgId;

  @ApiModelProperty(value = "部门名称")
  private String deptName;

  @ApiModelProperty(value = "创建人名称")
  private String createName;

  @ApiModelProperty(value = "送货附件")
  private List<Map<String, Object>> attachmentList;

  @ApiModelProperty(value = "明细列表")
  private List<OrderDelivemiddleVO> delivemiddleVOList;

  /** 签收状态（签收状态，0-未签收，1-部分签收，2-已签收） */
  @ApiModelProperty(value = "签收状态")
  private Integer signStatus;

  @ApiModelProperty(value = "签收状态名称", hidden = true)
  private String signStatusName;

  @ApiModelProperty(value = "是否有附件", hidden = true)
  private String haveFile;

  @ApiModelProperty(value = "序号", hidden = true)
  private Integer no;
}
