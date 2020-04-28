package com.rhdk.purchasingservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** @Description: 话术枚举 @Author: YYF @CreateDate: 2020/4/22 15:37 @Version: 1.0 */
@AllArgsConstructor
@Getter
public enum VerbalTrickEnum {
  CREATE_TASK("任务", "创建了该任务"),
  EDITOR_TASK("任务", "编辑了该任务"),
  DELETE_TASK("任务", "删除了该任务"),
  PERFORM_TASK("任务", "执行了该任务"),
  CREATE_THEME("主题", "创建了该主题"),
  EDITOR_THEME("主题", "编辑了该主题"),
  DELETE_THEME("主题", "删除了该主题"),
  ;

  private String type;

  private String description;
}
