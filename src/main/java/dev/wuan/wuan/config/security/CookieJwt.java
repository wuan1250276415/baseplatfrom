package dev.wuan.wuan.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Cookie JWT令牌管理组件
 * 用于处理JWT令牌的创建、验证和Cookie相关操作
 */
@Slf4j
@Component
@Getter
public class CookieJwt {

  /** JWT密钥 */
  private final String secret;

  /** JWT过期时间(分钟) */
  private final int expirationMin;

  /** Cookie名称 */
  private final String cookieName;

  /** JWT验证器 */
  private final JWTVerifier verifier;

  /**
   * 构造函数
   * @param secret JWT密钥
   * @param expirationMin JWT过期时间(分钟)
   * @param cookieName Cookie名称
   */
  public CookieJwt(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-min}") int expirationMin,
      @Value("${jwt.cookie-name}") String cookieName) {
    this.verifier = JWT.require(Algorithm.HMAC256(secret)).build();
    this.secret = secret;
    this.expirationMin = expirationMin;
    this.cookieName = cookieName;
  }

  /**
   * 获取JWT令牌中的主题
   * @param token JWT令牌
   * @return 主题内容
   */
  public String getSubject(String token) {
    return JWT.decode(token).getSubject();
  }

  /**
   * 验证JWT令牌
   * @param token JWT令牌
   * @return 验证结果
   */
  public Boolean verifyToken(String token) {
    try {
      verifier.verify(token);
      return Boolean.TRUE;
    } catch (JWTVerificationException e) {
      // 当JWT验证失败时返回false
      // 可能的原因包括:令牌过期、签名无效、令牌格式错误等
      return Boolean.FALSE;
    }
  }

  /**
   * 创建JWT令牌
   * @param userIdentify 用户标识
   * @return JWT令牌字符串
   */
  public String createJwt(String userIdentify) {
    return JWT.create()
        .withSubject(String.valueOf(userIdentify))
        .withIssuedAt(new Date())
        .withExpiresAt(
            Date.from(
                LocalDateTime.now()
                    .plusMinutes(expirationMin)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()))
        .sign(Algorithm.HMAC256(secret));
  }

  /**
   * 创建包含JWT的Cookie并添加到响应中
   * @param request HTTP请求
   * @param response HTTP响应
   * @param userIdentify 用户标识
   */
  public void createJwtCookie(
      HttpServletRequest request, HttpServletResponse response, String userIdentify) {
    response.addCookie(buildJwtCookiePojo(request, userIdentify));
  }

  /**
   * 构建JWT Cookie对象
   * @param request HTTP请求
   * @param userIdentify 用户标识
   * @return Cookie对象
   */
  public Cookie buildJwtCookiePojo(HttpServletRequest request, String userIdentify) {
    String contextPath = request.getContextPath();
    String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
    Cookie cookie = new Cookie(cookieName, createJwt(userIdentify));
    cookie.setPath(cookiePath);
    cookie.setMaxAge(expirationMin * 60);
    cookie.setSecure(request.isSecure());
    cookie.setHttpOnly(true);
    return cookie;
  }

  /**
   * 从请求中提取JWT令牌
   * @param request HTTP请求
   * @return JWT令牌字符串，如果不存在则返回null
   */
  public String extractJwt(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, cookieName);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  /**
   * 移除JWT Cookie
   * @param request HTTP请求
   * @param response HTTP响应
   */
  public void removeJwtCookie(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = WebUtils.getCookie(request, cookieName);
    if (cookie != null) {
      cookie.setMaxAge(0);
    }
    response.addCookie(cookie);
  }
}
