package com.rhdk.purchasingservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PatrolCheckRuleEnum {
  open("开启", 1),
  close("关闭", 0);
  ;

  private String message;

  private Integer code;
}
