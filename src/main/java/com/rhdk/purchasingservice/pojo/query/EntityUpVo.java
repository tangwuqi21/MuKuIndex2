package com.rhdk.purchasingservice.pojo.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EntityUpVo {
  @ApiModelProperty(value = "资产模板id")
  private Long assetTemplId;

  @ApiModelProperty(value = "要变更的资产状态")
  private Integer assetStatus;

  @ApiModelProperty(value = "原始资产状态")
  private Integer originalStatus;

  @ApiModelProperty(value = "资产数量")
  private Long amount;

  @ApiModelProperty(value = "资产ID集合")
  private List<Long> assetIds;

  @ApiModelProperty(value = "库管方式（1:量管 2:物管）")
  private Integer wmType;
}
