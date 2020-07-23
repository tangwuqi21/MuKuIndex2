package com.mukutech.websiteservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class SysJobDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表id主键", name = "id")
    private Long id;

    @ApiModelProperty(value = "职位编号", name = "code")
    private String code;

    @ApiModelProperty(value = "职位名称", name = "name")
    private String name;

    @ApiModelProperty(value = "所属部门", name = "department")
    private String department;

    @ApiModelProperty(value = "状态：1有效，0删除", name = "state")
    private Integer state;

    }
