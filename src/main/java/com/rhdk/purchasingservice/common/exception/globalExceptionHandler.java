package com.rhdk.purchasingservice.common.exception;

import com.rhdk.purchasingservice.common.enums.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

@ControllerAdvice
@Slf4j
public class globalExceptionHandler {

  /**
   * 运行时异常处理
   *
   * @param e
   * @return
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public ExceptionView applicationException(RuntimeException e, HttpServletRequest request) {
    log.error("【运行时异常】：[{}],接口出现错误", request.getRequestURI(), e);
    return new ExceptionView(ResultEnum.FAIL.getCode(), e.getMessage());
  }

  /**
   * 异常处理
   *
   * @param e
   * @return
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ExceptionView applicationException(Exception e, HttpServletRequest request) {
    log.error("【运行时异常】：[{}],接口出现错误", request.getRequestURI(), e);
    return new ExceptionView(ResultEnum.FAIL.getCode(), e.getMessage());
  }

  /**
   * sql异常处理
   *
   * @param e
   * @param request
   * @return
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(SQLException.class)
  @ResponseBody
  public ExceptionView applicationException(SQLException e, HttpServletRequest request) {
    log.error("【运行时异常】：[{}],接口出现错误", request.getRequestURI(), e);
    return new ExceptionView(ResultEnum.FAIL.getCode(), "系统更新，请联系管理员！");
  }

  /**
   * 字段验证异常处理
   *
   * @param
   * @param e 异常信息
   * @return
   * @throws Exception
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ExceptionView methodArgumentNotValidExceptionErrorHandler(
      HttpServletRequest request, Exception e) {
    log.error("【字段验证异常拦截】" + "[" + request.getRequestURI() + "]" + "接口出现错误," + e.getMessage());
    MethodArgumentNotValidException c = (MethodArgumentNotValidException) e;
    List<FieldError> errors = c.getBindingResult().getFieldErrors();
    StringBuffer errorMsg = new StringBuffer();
    for (FieldError err : errors) {
      String message = err.getDefaultMessage();
      errorMsg.append(err.getField() + ":" + message + ",");
    }
    return new ExceptionView(ResultEnum.FAIL.getCode(), errorMsg.toString());
  }
}
