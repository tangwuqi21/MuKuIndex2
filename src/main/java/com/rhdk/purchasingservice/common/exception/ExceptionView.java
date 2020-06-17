package com.rhdk.purchasingservice.common.exception;

import lombok.Data;

@Data
public class ExceptionView {

  private Integer code;

  private String msg;

  public ExceptionView(Integer code, String errorMessage) {
    this.code = code;
    this.msg = errorMessage;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getErrorMessage() {
    return msg;
  }

  public void setErrorMessage(String errorMessage) {
    this.msg = errorMessage;
  }
}
