package com.rhdk.purchasingservice.common.utils.response;

/**
 * 请求成功的返回
 *
 * @author huapro @Date 2019-08-19 Kevin
 */
public class SuccessResponseData extends ResponseData {

  public SuccessResponseData() {
    super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, null);
  }

  public SuccessResponseData(Object object) {
    super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, object);
  }

  public SuccessResponseData(Integer code, String message, Object object) {
    super(true, code, message, object);
  }
}
