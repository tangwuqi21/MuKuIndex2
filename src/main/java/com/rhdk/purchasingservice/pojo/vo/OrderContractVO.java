package com.rhdk.purchasingservice.pojo.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
 @Data
 @Builder
 @NoArgsConstructor
 @AllArgsConstructor
 @JsonInclude(JsonInclude.Include.NON_NULL)
 public class OrderContractVO implements Serializable{
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID")
   private Long id;

    @ApiModelProperty(value = "单据日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
   private Date contractDate;

    @ApiModelProperty(value = "单据编码")
   private String contractCode;

    @ApiModelProperty(value = "合同名称")
   private String contractName;

    @ApiModelProperty(value = "往来单位")
   private String contractCompany;

    @ApiModelProperty(value = "合同类型,1-采购合同")
   private Integer contractType;

    @ApiModelProperty(value = "合同金额")
   private Long contractMoney;

    @ApiModelProperty(value = "附件集合")
    private List<OrderAttachment> attachmentList;

    @ApiModelProperty(value = "删除标识，0-正常，1-已删除")
   private Integer delFlag;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
   private Date createDate;

    @ApiModelProperty(value = "创建人")
   private Long createBy;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
   private Date updateDate;

    @ApiModelProperty(value = "修改人")
   private Long updateBy;

    @ApiModelProperty(value = "备注")
   private String remark;

    @ApiModelProperty(value = "创建人名称")
    private String createName;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

 }