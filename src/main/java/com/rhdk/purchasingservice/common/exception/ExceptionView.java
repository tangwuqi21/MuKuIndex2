package com.rhdk.purchasingservice.common.exception;

import lombok.Data;

@Data
public class ExceptionView {

  private Integer code;

  private String errorMessage;

  public ExceptionView(Integer code, String errorMessage) {
    this.code = code;
    this.errorMessage = errorMessage;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
