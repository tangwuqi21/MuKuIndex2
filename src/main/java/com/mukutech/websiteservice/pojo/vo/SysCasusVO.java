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
public class SysCasusVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

        @ApiModelProperty(value = "案例编号")
    private String code;

        @ApiModelProperty(value = "案例名称")
    private String name;

        @ApiModelProperty(value = "备注/具体项目描述")
    private String node;

        @ApiModelProperty(value = "展示图片的地址")
    private String picture;

        @ApiModelProperty(value = "用于自定义排序")
    private Integer orderId;

        @ApiModelProperty(value = "状态：1有效，0删除")
    private Integer state;

    }