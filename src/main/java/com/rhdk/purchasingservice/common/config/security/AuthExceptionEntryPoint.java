package com.rhdk.purchasingservice.common.config.security;

import com.alibaba.fastjson.JSON;
import com.rhdk.purchasingservice.common.exception.BizExceptionEnum;
import com.rhdk.purchasingservice.common.utils.response.ErrorResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint
{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws ServletException {
        Map<String, Object> map = new HashMap<String, Object>();
        Throwable cause = authException.getCause();

        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            if(cause instanceof InvalidTokenException) {
                response.getWriter().write(JSON.toJSONString(new ErrorResponseData(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.TOKEN_ERROR.getMessage())   ));

            }else{
                response.getWriter().write(JSON.toJSONString(new ErrorResponseData(BizExceptionEnum.TOKEN_EXCEPTION.getCode(), BizExceptionEnum.TOKEN_EXCEPTION.getMessage())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}