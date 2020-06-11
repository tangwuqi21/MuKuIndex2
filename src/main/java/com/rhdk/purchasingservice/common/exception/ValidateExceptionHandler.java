package com.rhdk.purchasingservice.common.exception;

import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@Slf4j
public class ValidateExceptionHandler {

  /**
   * 字段验证异常处理
   *
   * @param
   * @param e 异常信息
   * @return
   * @throws Exception
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  public ResponseEnvelope methodArgumentNotValidExceptionErrorHandler(
      HttpServletRequest request, Exception e) {
    log.error("【字段验证异常拦截】" + "[" + request.getRequestURI() + "]" + "接口出现错误," + e.getMessage());
    MethodArgumentNotValidException c = (MethodArgumentNotValidException) e;
    List<FieldError> errors = c.getBindingResult().getFieldErrors();
    StringBuffer errorMsg = new StringBuffer();
    for (FieldError err : errors) {
      String message = err.getDefaultMessage();
      errorMsg.append(err.getField() + ":" + message + ",");
    }

    return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), errorMsg.toString());
  }
}
