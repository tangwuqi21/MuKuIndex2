package com.rhdk.purchasingservice.pojo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseDTO {
  private int currentPage = 1;
  private int pageSize = 10;
}
