package com.rhdk.purchasingservice.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
  UPDATE_FAIL(400, "编辑失败"),
  INSERT_FAIL(400, "公告/事项保存失败"),
  INSER_DATAVISIBILITY_FAIL(400, "设置可见性失败"),
  FALSE_DATATYPE(400, "错误的附件所属对象"),
  FALSE_DELETE_NOTICE(400, "公告删除失败"),
  FALSE_UPDATE_NOTICE(400, "公告更新失败"),
  FALSE_DELETE_DATAVISIBILITY(400, "可见性删除失败"),
  FALSE_GET_ID(400, "所属ID不能为空"),
  FAIL_GET_DATAVISIBILITY(400, "获取用户可见性失败"),

  FALSE_GET_DATAVISIBILITY(400, "id不能为空，获取可见性失败"),
  THE_FILENAME_ERROR(400, "文件名错误"),

  INCOMING_NO_EXIST(400, "进件资料不存在"),
  INCOMINGNUMBER_NO_EXIST(400, "进件编号不存在"),
  BUILD_STATION_NO_EXIST(400, "建站资料不存在"),
  BUILD_STATION_ALREADY_EXIST(400, "建站资料已存在"),
  CONTRACT_NO_EXIST(400, "合同资料不存在"),
  CONTRACT_ALREADY_EXIST(400, "合同资料已存在"),
  ACCEPTANCE_NO_EXIST(400, "验收申请不存在"),
  ACCEPTANCE_ALREADY_EXIST(400, "验收申请已存在"),
  REPORT_NO_EXIST(400, "验收报告不存在"),
  REPORT_ALREADY_EXIST(400, "验收报告已存在"),

  UNIFORM_CODE_ALREADY_EXISTS(400, "统一社会征信代码已存在"),
  DEALER_INFORMATION_NO_EXIST(400, "经销商信息不存在"),
  DEALER_INFORMATION_ALREADY_USED(400, "经销商信息已在使用"),

  SIGNATURE_DOES_NOT_EXIST(400, "签名无效"),
  ERROR_UPLOADING_ATTACHMENT(400, "上传附件错误"),

  ERROR_ACCOUNT_PASSWORD(500, "账号或者密码错误"),

  DIANEBAO_URL_ERROR(400, "电e宝接口调用失败"),
  DIANEBAO_PARAM_ERROR(400, "电e宝接口调用参数错误"),

  GEN_NUMBER_ALREADY_EXISTS(400, "发电户号重复"),
  GEN_NUMBER_INVALID(400, "发电户号无效"),

  SIGN_INFO_NOT_IMPORT(400, "签约信息未导入"),
  POWER_GENERATION_NO_INFO_ERROR(400, "发电户号相关信息有误");

  private int status;
  private String message;

  ExceptionEnum(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
