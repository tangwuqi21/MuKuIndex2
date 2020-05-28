package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 资产模版属性
 *
 * @author HUAPRO
 * @since 2020-05-13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetTmplInfoVO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "自增id（资产模板id）")
  private Long id;

  @ApiModelProperty(value = "资产类别id")
  private Long assetCatId;

  @ApiModelProperty(value = "名称")
  private String name;

  @ApiModelProperty(value = "资产业务编码")
  private String itemNo;

  @ApiModelProperty(value = "版本号")
  private Integer verNo;

  @ApiModelProperty(value = "描述")
  private String dscp;

  @ApiModelProperty(value = "所属公司id")
  private Long orgId;

  @ApiModelProperty(value = "单位属性Id")
  private Long unitId;

  @ApiModelProperty(value = "单位")
  private String unit;

  @ApiModelProperty(value = "单价Id")
  private Long priceId;

  @ApiModelProperty(value = "单价")
  private String price;

  @ApiModelProperty(value = "库管方式（1:量管 2:物管）")
  private Integer wmType;
}
