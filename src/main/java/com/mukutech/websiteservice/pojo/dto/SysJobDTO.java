package com.mukutech.websiteservice.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author SnowLee
 * @since 2020-07-27
 */
@Getter
@Setter
public class SysJobDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    @ApiModelProperty(value = "职位编号")
    private String code;

    @ApiModelProperty(value = "职位名称")
    private String name;

    @ApiModelProperty(value = "所属部门")
    private String department;

    @ApiModelProperty(value = "发布日期")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date publishDate;

    @ApiModelProperty(value = "状态：1有效，0删除")
    private Integer state;

    }
