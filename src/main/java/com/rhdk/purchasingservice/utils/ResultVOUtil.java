package com.rhdk.purchasingservice.utils;

import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.response.ResponseData;
import org.apache.poi.ss.formula.functions.T;

public class ResultVOUtil {

  public static ResponseData returnSuccess(Integer code, String msg, T data) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(code);
    ResponseData.setData(data);
    return ResponseData;
  }

  public static ResponseData returnSuccess(Object data) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(ResultEnum.SUCCESS.getCode());

    ResponseData.setData(data);
    return ResponseData;
  }

  public static ResponseData returnSuccess() {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(ResultEnum.SUCCESS.getCode());
    return ResponseData;
  }

  public static ResponseData returnSuccess(Integer code, String msg) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(code);
    ResponseData.setData(null);
    return ResponseData;
  }

  public static ResponseData returnSuccess(ResultEnum resultEnum, Object object) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(resultEnum.getCode());
    ResponseData.setData(object);
    return ResponseData;
  }

  public static ResponseData returnFail(Integer code, String msg) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(code);
    return ResponseData;
  }

  public static ResponseData returnFail(Integer code, String msg, Object object) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(code);
    ResponseData.setData(object);
    return ResponseData;
  }

  public static ResponseData returnFail() {
    return returnFail(ResultEnum.FAIL);
  }

  public static ResponseData returnFail(ResultEnum resultEnum) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(resultEnum.getCode());
    return ResponseData;
  }

  public static ResponseData returnFail(ResultEnum resultEnum, Object object) {
    ResponseData ResponseData = new ResponseData();
    ResponseData.setCode(resultEnum.getCode());
    ResponseData.setData(object);
    return ResponseData;
  }
}
