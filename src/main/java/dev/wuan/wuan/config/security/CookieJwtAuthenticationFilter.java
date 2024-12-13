package dev.wuan.wuan.config.security;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Cookie JWT认证过滤器
 * 用于处理基于Cookie中JWT令牌的用户认证
 */
@Slf4j
@Setter
public class CookieJwtAuthenticationFilter extends OncePerRequestFilter {

  /** Cookie JWT管理组件 */
  private final CookieJwt cookieJwt;

  /** 用户详情服务 */
  private final UserDetailsServiceImpl userDetailsService;

  public CookieJwtAuthenticationFilter(CookieJwt cookieJwt, UserDetailsServiceImpl userDetailsService) {
    this.cookieJwt = cookieJwt;
    this.userDetailsService = userDetailsService;
  }

  /**
   * 执行过滤器的主要逻辑
   * @param request HTTP请求
   * @param response HTTP响应
   * @param filterChain 过滤器链
   * @throws ServletException Servlet异常
   * @throws IOException IO异常
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 从请求中提取JWT令牌
    String token = cookieJwt.extractJwt(request);
    // 验证令牌是否有效
    if (StringUtils.isNotEmpty(token) && cookieJwt.verifyToken(token)) {
      try {
        // 加载用户详情
        UserDetails userDetails =
            userDetailsService.loadUserByUsername(cookieJwt.getSubject(token));
        // 创建认证令牌
        CookieJwtAuthenticationToken authenticated =
            CookieJwtAuthenticationToken.authenticated(
                userDetails, token, userDetails.getAuthorities());
        // 设置认证详情
        authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 将认证信息存储到安全上下文中
        SecurityContextHolder.getContext().setAuthentication(authenticated);
      } catch (Exception e) {
        // 记录无效用户ID的JWT错误
        log.error("jwt with invalid user id {}", cookieJwt.getSubject(token), e);
      }
    }
    // 继续执行过滤器链
    filterChain.doFilter(request, response);
  }
}
