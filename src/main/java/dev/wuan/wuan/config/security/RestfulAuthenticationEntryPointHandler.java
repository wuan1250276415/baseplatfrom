package dev.wuan.wuan.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * RESTful认证入口点处理器
 * 用于处理未认证和无权限访问的请求
 */
public class RestfulAuthenticationEntryPointHandler
    implements AccessDeniedHandler, AuthenticationEntryPoint {

  /**
   * 处理未认证的请求
   * @param request HTTP请求
   * @param response HTTP响应
   * @param authException 认证异常
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  /**
   * 处理无权限访问的请求
   * @param request HTTP请求
   * @param response HTTP响应
   * @param accessDeniedException 访问拒绝异常
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException) {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
  }
}
