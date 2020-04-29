package com.rhdk.purchasingservice.common.utils;


import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;

public class ResultVOUtil {

  public static <T> ResponseEnvelope returnSuccess(Integer code, String msg, T data) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(code);
    responseEnvelope.setMsg(msg);
    responseEnvelope.setData(data);
    return responseEnvelope;
  }

  public static ResponseEnvelope returnSuccess(Object data) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(ResultEnum.SUCCESS.getCode());
    responseEnvelope.setMsg(ResultEnum.SUCCESS.getMessage());
    responseEnvelope.setData(data);
    return responseEnvelope;
  }

  public static ResponseEnvelope returnSuccess() {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(ResultEnum.SUCCESS.getCode());
    responseEnvelope.setMsg(ResultEnum.SUCCESS.getMessage());
    return responseEnvelope;
  }

  public static ResponseEnvelope returnSuccess(Integer code, String msg) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(code);
    responseEnvelope.setMsg(msg);
    responseEnvelope.setData(null);
    return responseEnvelope;
  }

  public static ResponseEnvelope returnSuccess(ResultEnum resultEnum, Object object) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(resultEnum.getCode());
    responseEnvelope.setMsg(resultEnum.getMessage());
    responseEnvelope.setData(object);
    return responseEnvelope;
  }

  public static ResponseEnvelope returnFail(Integer code, String msg) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(code);
    responseEnvelope.setMsg(msg);
    return responseEnvelope;
  }

  public static ResponseEnvelope returnFail(Integer code, String msg, Object object) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(code);
    responseEnvelope.setMsg(msg);
    responseEnvelope.setData(object);
    return responseEnvelope;
  }

  public static ResponseEnvelope returnFail() {
    return returnFail(ResultEnum.FAIL);
  }

  public static ResponseEnvelope returnFail(ResultEnum resultEnum) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(resultEnum.getCode());
    responseEnvelope.setMsg(resultEnum.getMessage());
    return responseEnvelope;
  }

  public static ResponseEnvelope returnFail(ResultEnum resultEnum, Object object) {
    ResponseEnvelope responseEnvelope = new ResponseEnvelope();
    responseEnvelope.setCode(resultEnum.getCode());
    responseEnvelope.setMsg(resultEnum.getMessage());
    responseEnvelope.setData(object);
    return responseEnvelope;
  }
}
