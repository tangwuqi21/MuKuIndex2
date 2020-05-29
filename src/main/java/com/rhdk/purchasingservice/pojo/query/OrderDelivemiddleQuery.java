package com.rhdk.purchasingservice.pojo.query;

import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 送货记录明细中间表
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Getter
@Setter
public class OrderDelivemiddleQuery extends BaseDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "ID", hidden = true)
  private Long id;

  @ApiModelProperty(value = "送货记录id", hidden = true)
  private Long deliveryId;

  @ApiModelProperty(value = "单据号", hidden = true)
  private String deliverydetailCode;

  @ApiModelProperty(value = "签收单号", hidden = true)
  private String signNo;

  @ApiModelProperty(value = "资产类型状态，1-物管，2-量管", hidden = true)
  private String assetType;

  @ApiModelProperty(value = "资产模板id", hidden = true)
  private Long moduleId;

  @ApiModelProperty(value = "资产模板版本号", hidden = true)
  private Integer moduleVersion;

  @ApiModelProperty(value = "单位", hidden = true)
  private String assetUnit;

  @ApiModelProperty(value = "数量", hidden = true)
  private String assetNumber;

  @ApiModelProperty(value = "单价", hidden = true)
  private Long assetPrice;

  @ApiModelProperty(value = "累计金额", hidden = true)
  private Long totalMoney;

  @ApiModelProperty(value = "明细附件url", hidden = true)
  private String fileUrl;

  @ApiModelProperty(value = "明细附件文件名", hidden = true)
  private String fileName;

  @ApiModelProperty(value = "资产类别Id", hidden = true)
  private Long assetCatId;

  @ApiModelProperty(value = "供应商名称")
  private String supplierName;
}
