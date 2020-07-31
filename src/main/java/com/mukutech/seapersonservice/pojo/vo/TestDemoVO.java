package com.mukutech.seapersonservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户基础属性表
 * </p>
 *
 * @author LMYOU
 * @since 2020-07-22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestDemoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "用户OFFSET")
    private Long id;

    @ApiModelProperty(value = "用户账号")
    private String userCode;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "年龄分段")
    private String agePeriod;

    @ApiModelProperty(value = "生日")
    private Date birthday;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "注册地")
    private String registerCity;

}