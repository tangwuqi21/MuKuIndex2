package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class DeliverecordInfoVO implements Serializable {
  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "明细单id")
  private Long id;

  @ApiModelProperty(value = "送货单编码")
  private String deliveryCode;

  @ApiModelProperty(value = "送货单名称")
  private String deliveryName;
}
