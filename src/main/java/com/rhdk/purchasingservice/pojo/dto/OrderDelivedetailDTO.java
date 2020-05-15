package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 送货明细
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Getter
@Setter
public class OrderDelivedetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "自增id")
    private Long id;

    @ApiModelProperty(value = "中间表id")
    private Long middleId;

    @ApiModelProperty(value = "资产名称")
    private String assetName;

    @ApiModelProperty(value = "物料号")
    private String itemNo;

    @ApiModelProperty(value = "资产Id")
    private Long assetId;

    @ApiModelProperty(value = "数量（量化管理时必需）")
    private Long assetNumber;

    @ApiModelProperty(value = "单位")
    private String assetUnit;

}
