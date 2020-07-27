package com.mukutech.websiteservice.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Getter
@Setter
public class SysAdviceDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表id主键", name = "id")
    private Long id;

    @ApiModelProperty(value = "留言标题")
    private String title;

    @ApiModelProperty(value = "留言内容")
    private String msg;

    @ApiModelProperty(value = "留言人邮箱")
    private String email;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "留言时间")
    private Date createTime;

    @ApiModelProperty(value = "状态：1有效，0删除")
    private Integer state;

    }
