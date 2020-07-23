package com.mukutech.websiteservice.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseDTO {
    @ApiModelProperty(value = "第几页数据",name = "currentPage", example = "1")
    private int currentPage = 1;

    @ApiModelProperty(value = "一页有几条数据",name = "pageSize", example = "10")
    private int pageSize = 10;
}
