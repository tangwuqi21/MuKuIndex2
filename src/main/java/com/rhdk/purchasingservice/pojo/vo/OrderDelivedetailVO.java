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

/**
 * <p>
 * 送货明细
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDelivedetailVO implements Serializable {
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

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @ApiModelProperty(value = "修改人")
    private Long updateBy;

    @ApiModelProperty(value = "修改日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

    @ApiModelProperty(value = "逻辑删除（0:正常 1:删除）")
    private Integer delFlag;

}