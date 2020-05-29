package com.rhdk.purchasingservice.pojo.query;

import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CommonQuery extends BaseDTO implements Serializable {
  @ApiModelProperty(value = "合同名称")
  private String contractName;
}
