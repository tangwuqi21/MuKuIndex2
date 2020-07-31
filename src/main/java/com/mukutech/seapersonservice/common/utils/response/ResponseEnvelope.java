package com.mukutech.seapersonservice.common.utils.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseEnvelope<T> {

    private Integer code;

    private String msg;

    private T data;

    // 自定义返回
    public ResponseEnvelope(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功返回
    public ResponseEnvelope(T data) {
        this.code = HttpStatus.OK.value();
        this.msg = HttpStatus.OK.name();
        this.data = data;
    }

    // 成功返回
    public ResponseEnvelope() {
    }
}
