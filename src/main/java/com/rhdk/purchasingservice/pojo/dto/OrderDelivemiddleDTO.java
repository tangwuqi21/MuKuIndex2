package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 送货记录明细中间表
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Getter
@Setter
public class OrderDelivemiddleDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "ID", hidden = true)
  private Long id;

  @ApiModelProperty(value = "送货记录id", hidden = true)
  private Long deliveryId;

  @ApiModelProperty(value = "单据号")
  private String deliverydetailCode;

  @ApiModelProperty(value = "签收单号", hidden = true)
  private String signNo;

  @ApiModelProperty(value = "资产类别searchkey", hidden = true)
  private String assetCatSearchKey;

  @ApiModelProperty(value = "资产业务编码")
  private String itemNO;

  @ApiModelProperty(value = "资产类型状态，1-物管，2-量管")
  private String wmType;

  @ApiModelProperty(value = "资产模板id")
  private Long moduleId;

  @ApiModelProperty(value = "资产模板名称", hidden = true)
  private String moduleName;

  @ApiModelProperty(value = "资产模板版本号")
  private Integer moduleVersion;

  @ApiModelProperty(value = "数量")
  private Long assetNumber;

  @ApiModelProperty(value = "属性集合")
  private String prptIds;

  @ApiModelProperty(value = "累计金额")
  private Long totalMoney;

  @ApiModelProperty(value = "送货明细附件")
  private List<OrderAttachmentDTO> attachmentList;

  @ApiModelProperty(value = "资产类别Id")
  private Long assetCatId;

  /** 签收状态（签收状态，0-未签收，1-部分签收，2-已签收） */
  @ApiModelProperty(value = "签收状态", hidden = true)
  private Integer signStatus;

  @ApiModelProperty(value = "token", hidden = true)
  private String token;

  @ApiModelProperty(value = "送货明细备注")
  @Size(max = 100, message = "备注过长，只能输入100个字符以内的备注")
  private String remark;

  @ApiModelProperty(value = "资产id集合", hidden = true)
  private String assetIds;

  @ApiModelProperty(value = "创建人", hidden = true)
  private Long createBy;

  @ApiModelProperty(value = "机构id", hidden = true)
  private String orgId;

  @ApiModelProperty(value = "资产状态标识", hidden = true)
  private Integer assetStatus;

  @ApiModelProperty(value = "资产实体redisKey", hidden = true)
  private String assetKey;

  @ApiModelProperty(value = "pk值的Rediskey", hidden = true)
  private String pkValKey;

  @ApiModelProperty(value = "单位id", hidden = true)
  private Long unitId;

  @ApiModelProperty(value = "单价id", hidden = true)
  private Long priceId;
}
