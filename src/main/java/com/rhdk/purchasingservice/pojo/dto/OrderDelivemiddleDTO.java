package com.rhdk.purchasingservice.pojo.dto;

import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 送货记录明细中间表
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Getter
@Setter
public class OrderDelivemiddleDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "送货记录id")
    private Long deliveryId;

    @ApiModelProperty(value = "单据号")
    private String deliverydetailCode;

    @ApiModelProperty(value = "签收单号")
    private String signNo;

    @ApiModelProperty(value = "资产类型状态，1-物管，2-量管")
    private String wmType;

    @ApiModelProperty(value = "资产模板id")
    private Long moduleId;

    @ApiModelProperty(value = "资产模板版本号")
    private Integer moduleVersion;

    @ApiModelProperty(value = "单位")
    private String assetUnit;

    @ApiModelProperty(value = "数量")
    private String assetNumber;

    @ApiModelProperty(value = "单价")
    private Long assetPrice;

    @ApiModelProperty(value = "累计金额")
    private Long totalMoney;

    @ApiModelProperty(value = "明细附件url")
    private String fileUrl;

    @ApiModelProperty(value = "明细附件文件名")
    private String fileName;

    @ApiModelProperty(value = "资产类别Id")
    private Long assetCatId;

}
