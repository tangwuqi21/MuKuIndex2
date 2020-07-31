package com.mukutech.seapersonservice.common.utils;


import com.mukutech.seapersonservice.common.enums.ResultEnum;
import com.mukutech.seapersonservice.common.utils.response.ResponseEnvelope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, Object> listToMap(
            List<Map<String, Object>> dataMap, String keyName, String valueName) {
        Map<String, Object> map = new HashMap<>();
        if (dataMap != null && !dataMap.isEmpty()) {
            for (Map<String, Object> map1 : dataMap) {
                String key = map1.get(keyName).toString();
                Object value = map1.get(valueName);
                map.put(key, value);
            }
        }
        return map;
    }
}
