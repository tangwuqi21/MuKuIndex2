package com.rhdk.purchasingservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultEnum {

  // 统一结果返回码
  SUCCESS(0, "success"),
  FAIL(-1, "请求失败"),
  AUTH_ERROR(403, "认证失败"),
  NO_HANDLER(404, "接口不存在"),
  SERVER_ERROR(500, "failure"),
  // 附件上传不能为空
  FILE_NOTNULL(-1, "附件不能为空"),

  ID_NOTNULL(-1, "更新操作主键id不能为空"),

  TEMPLATE_NOTFORMAT(-1, "模板格式不正确"),

  TEMPLATE_CELLNULL(-1, "资产属性值不能为空"),

  TEMPLATE_ROWTWO(-1, "附件内容有重复行"),

  CREATE_FILEERROR(-1, "附件创建失败，请重新上传"),

  DETAIL_NOTNULL(-1, "送货明细记录不能为空"),

  DELIVER_MIDDLDETAILENULL(-1, "送货记录明细详情信息为空"),

  FEGIN_TEMPLINFONULL(-1, "远程调用模板信息为空"),

  FEGIN_DETAILLISTNULL(-1, "远程获取资产明细清单信息为空"),

  DELIVER_MIDDLENULL(-1, "送货记录明细信息为空");

  private Integer code;

  private String message;
}
