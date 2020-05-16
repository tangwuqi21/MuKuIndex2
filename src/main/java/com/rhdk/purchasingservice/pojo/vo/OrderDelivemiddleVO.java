package com.rhdk.purchasingservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 送货记录明细中间表
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDelivemiddleVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "送货记录id")
    private Long deliveryId;

    @ApiModelProperty(value = "单据号")
    private String deliverydetailCode;

    @ApiModelProperty(value = "签收单号")
    private String signNo;

    @ApiModelProperty(value = "资产模板id")
    private Long moduleId;

    @ApiModelProperty(value = "单位")
    private Long assetUnit;

    @ApiModelProperty(value = "数量")
    private String assetNumber;

    @ApiModelProperty(value = "单价")
    private Long assetPrice;

    @ApiModelProperty(value = "累计金额")
    private Long totalMoney;

    @ApiModelProperty(value = "删除标识，0-正常，1-已删除")
    private Integer delFlag;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

    @ApiModelProperty(value = "修改人")
    private Long updateBy;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "送货单号")
    private String deliveryCode;
    @ApiModelProperty(value = "送货单名称")
    private String deliveryName;
    @ApiModelProperty(value = "供应商id")
    private Long supplierId;
    @ApiModelProperty(value = "签收地址")
    private String signAddress;
    @ApiModelProperty(value = "合同编号")
    private String contractCode;
    @ApiModelProperty(value = "合同名称")
    private String contractName;
    @ApiModelProperty(value = "合同类型")
    private Integer contractType;


    @ApiModelProperty(value = "送货明细附件")
    private List<Map<String,Object>> attachmentList;

}