package com.rhdk.purchasingservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PatrolMatchStatusEnum {
  // 匹配状态
  NO_MATCH("未匹配", "0"),
  BEYOND_MATCHING("超出预期", "2"),
  MATCH("已匹配", "1"),
  MATCH_MORE("多个结果", "3"),
  MATCH_FAILURE("匹配失败", "4");

  private String message;

  private String code;
}
