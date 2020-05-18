package com.rhdk.purchasingservice.pojo.query;

import com.rhdk.purchasingservice.pojo.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 客户管理
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-18
 */
@Getter
@Setter
public class CustomerQuery extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "自增id")
    private Long id;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "企业统一社会信用代码")
    private String code;

    @ApiModelProperty(value = "简称")
    private String shortname;
}
