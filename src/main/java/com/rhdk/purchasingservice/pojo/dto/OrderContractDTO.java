package com.rhdk.purchasingservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 合同表
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Getter
@Setter
public class OrderContractDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "单据日期")
    @NotNull(message = "单据日期不能为空")
    private Date contractDate;

    @ApiModelProperty(value = "单据编码")
    @NotNull(message = "单据编码不能为空")
    private String contractCode;

    @ApiModelProperty(value = "合同名称")
    @NotNull(message = "合同名称不能为空")
    private String contractName;

    @ApiModelProperty(value = "往来单位")
    @NotNull(message = "往来单位不能为空")
    private String contractCompany;

    @ApiModelProperty(value = "合同类型,1-采购合同")
    @NotNull(message = "合同类型不能为空")
    private Integer contractType;

    @ApiModelProperty(value = "合同金额")
    @NotNull(message = "合同金额不能为空")
    private Long contractMoney;

    @ApiModelProperty(value = "附件")
    @NotNull(message = "合同附件不能为空")
    private List<OrderAttachmentDTO> attachmentList;

    @ApiModelProperty(value = "删除标识，0-正常，1-已删除",hidden = true)
    private Integer delFlag;

    @ApiModelProperty(value = "创建时间",hidden = true)
    private Date createDate;

    @ApiModelProperty(value = "创建人",hidden = true)
    private Long createBy;

    @ApiModelProperty(value = "修改时间",hidden = true)
    private Date updateDate;

    @ApiModelProperty(value = "修改人",hidden = true)
    private Long updateBy;

    @ApiModelProperty(value = "备注")
    private String remark;

}
