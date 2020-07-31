package com.mukutech.seapersonservice.common.enums;

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
    SERVER_ERROR(500, "failure");

    private Integer code;

    private String message;
}
