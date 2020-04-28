package com.rhdk.purchasingservice.common.config.security;

import com.alibaba.fastjson.JSON;
import com.rhdk.purchasingservice.common.exception.BizExceptionEnum;
import com.rhdk.purchasingservice.common.utils.response.ErrorResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            response.getWriter().write(JSON.toJSONString(new ErrorResponseData(BizExceptionEnum.TOKEN_DENIED.getCode(), BizExceptionEnum.TOKEN_DENIED.getMessage())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}