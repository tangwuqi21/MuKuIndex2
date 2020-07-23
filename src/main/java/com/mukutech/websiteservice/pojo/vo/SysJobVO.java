package com.mukutech.websiteservice.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysJobVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

        @ApiModelProperty(value = "职位编号")
    private String code;

        @ApiModelProperty(value = "职位名称")
    private String name;

        @ApiModelProperty(value = "所属部门")
    private String department;

        @ApiModelProperty(value = "状态：1有效，0删除")
    private Integer state;

    }