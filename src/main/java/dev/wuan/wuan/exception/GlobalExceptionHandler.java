package dev.wuan.wuan.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 全局异常处理器
 * 用于统一处理系统中抛出的各类异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * 处理业务异常
   * @param ex 业务异常
   * @param request Web请求
   * @return 响应实体
   */
  @ExceptionHandler(value = {BusinessException.class})
  public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
    log.error("业务异常处理 ===> ", ex);
    return createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage(),
        ex,
        request);
  }

  /**
   * 处理参数校验异常
   * @param ex 参数校验异常
   * @param headers HTTP头
   * @param status HTTP状态码
   * @param request Web请求
   * @return 响应实体
   */
  @SuppressWarnings("NullableProblems")
  @Override
  @Nullable 
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error("参数校验异常处理 ===> ", ex);
    return createErrorResponse(status, ex.getMessage(), ex, request);
  }

  /**
   * 处理请求拒绝异常
   * @param ex 请求拒绝异常
   * @param request Web请求
   * @return 响应实体
   */
  @ExceptionHandler(value = {RequestRejectedException.class})
  public ResponseEntity<Object> handleRequestRejectedException(
      RequestRejectedException ex, WebRequest request) {
    log.error("请求拒绝异常处理 ===> ", ex);
    return createErrorResponse(
        HttpStatus.BAD_REQUEST,
        ex.getMessage(),
        ex,
        request);
  }

  /**
   * 处理访问拒绝异常
   * @param ex 访问拒绝异常
   * @return 响应实体
   */
  @ExceptionHandler(value = {AccessDeniedException.class})
  public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
    throw ex;
  }

  /**
   * 处理所有未捕获的系统异常
   * @param ex 异常
   * @param request Web请求
   * @return 响应实体
   */
  @ExceptionHandler(value = {Throwable.class})
  public ResponseEntity<Object> handleException(Throwable ex, WebRequest request) {
    log.error("系统异常处理 ===> ", ex);
    return createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "系统错误",
        ex,
        request);
  }

  /**
   * 创建错误响应
   * @param status HTTP状态码
   * @param message 错误信息
   * @param ex 异常
   * @param request Web请求
   * @return 响应实体
   */
  private ResponseEntity<Object> createErrorResponse(
      HttpStatusCode status, String message, Throwable ex, WebRequest request) {
    ErrorResponseException errorResponseException = new ErrorResponseException(
        status,
        ProblemDetail.forStatusAndDetail(status, message),
        ex.getCause());
    return handleExceptionInternal(
        errorResponseException,
        errorResponseException.getBody(),
        errorResponseException.getHeaders(),
        errorResponseException.getStatusCode(),
        request);
  }
}
