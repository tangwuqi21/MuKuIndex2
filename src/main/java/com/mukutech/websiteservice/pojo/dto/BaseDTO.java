package com.mukutech.websiteservice.pojo.dto;

import lombok.Data;

@Data
public class BaseDTO {
    private int currentPage = 1;
    private int pageSize = 10;
}
