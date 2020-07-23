package com.mukutech.websiteservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Data
public class SysCorpDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

        @ApiModelProperty(value = "公司名称")
    private String name;

        @ApiModelProperty(value = "公司名称缩写")
    private String miniName;

        @ApiModelProperty(value = "地址")
    private String address;

        @ApiModelProperty(value = "电话")
    private String phone;

        @ApiModelProperty(value = "邮箱")
    private String email;

        @ApiModelProperty(value = "邮政编码")
    private String zipCode;

        @ApiModelProperty(value = "版权所有")
    private String copyright;

        @ApiModelProperty(value = "ICP备案号")
    private String icpCode;

        @ApiModelProperty(value = "用于自定义排序")
    private Integer orderId;

        @ApiModelProperty(value = "状态：1有效，0删除")
    private Integer state;

    }
