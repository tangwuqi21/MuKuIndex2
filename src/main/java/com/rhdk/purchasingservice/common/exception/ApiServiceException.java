package com.rhdk.purchasingservice.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ApiServiceException extends Exception {

  /** 错误编码 */
  private Integer code;

  /** 错误的提示信息 */
  private String errorMessage;

  public ApiServiceException(AbstractBaseExceptionEnum exception) {
    super(exception.getMessage());
    this.code = exception.getCode();
    this.errorMessage = exception.getMessage();
  }

  /** 获取异常的类的具体名称 */
  public abstract String getExceptionClassName();
}
