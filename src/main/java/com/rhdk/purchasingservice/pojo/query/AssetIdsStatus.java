package com.rhdk.purchasingservice.pojo.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AssetIdsStatus {
  @ApiModelProperty(value = "资产id集合")
  List<Long> assetIds;

  @ApiModelProperty(value = "资产状态")
  Integer status;
}
