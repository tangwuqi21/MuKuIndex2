package com.rhdk.purchasingservice.pojo.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 资产模板常用过滤属性 */
@Getter
@Setter
public class TmplPrptsFilter {

  @ApiModelProperty(value = "资产模板id")
  private Long tmplId;

  @ApiModelProperty(value = "属性类型（0，一种资产通用属性，1一个资产个性化属性；后期根据实际操作判断是否取消）")
  private Integer defaultFlag;

  @ApiModelProperty(value = "是否唯一键，0-不是，1-是")
  private Integer pkFlag;

  /** 资产标识 （0:待签收 1:签收 2:入库 3:消耗 99:虚资产-用于组装） */
  @ApiModelProperty(value = "资产标识：-2,暂存，0:待签收 1:签收 2:入库 3:消耗 99:虚资产-用于组装")
  private Integer assetStatus;
  /** 资产模板版本号 */
  @ApiModelProperty(value = "资产模板版本号")
  private Integer assetTemplVer;

  @ApiModelProperty(value = "机构id")
  private Integer orgId;
}
