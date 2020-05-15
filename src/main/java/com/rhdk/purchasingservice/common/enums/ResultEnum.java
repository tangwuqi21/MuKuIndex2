package com.rhdk.purchasingservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultEnum {

  // 统一结果返回码
  SUCCESS(0, "success"),
  RETRY(-1, "请求失败,请稍后重试"),
  NO_VALID_PARAM(1, "请求参数有误"),
  FAIL(400, "请求失败"),
  AUTH_ERROR(403, "认证失败"),
  NO_HANDLER(404, "接口不存在"),
  SERVER_ERROR(500, "failure"),
  // 自定义结果返回码
  NAME_REPEAT(500, "该名称已存在"),
  WITHOUT_PERMISSION(500, "暂无该操作权限"),
  // 物资匹配提示语
  MATCH_FAILURE(500, "物资识别，匹配失败"),
  MATCH_MORE(500, "匹配失败，请联系管理员检查校验码设置"),
  MATCH_SUCCESS(0, "物资识别，匹配成功"),

  //附件上传不能为空
  FILE_NOTNULL(500, "附件不能为空"),

  TEMPLATE_NOTFORMAT(500, "模板格式不正确"),

  TEMPLATE_CELLNULL(500, "资产属性值不能为空"),

  DETAIL_NOTNULL(500, "记录明细不能为空");

  private Integer code;

  private String message;
}
