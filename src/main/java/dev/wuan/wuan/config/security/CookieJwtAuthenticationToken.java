package dev.wuan.wuan.config.security;

import java.io.Serial;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Cookie JWT认证令牌
 * 用于处理基于Cookie中JWT的认证信息
 */
@Setter
@Getter
@ToString
public class CookieJwtAuthenticationToken extends AbstractAuthenticationToken {

  /** 序列化版本ID */
  @Serial private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

  /** 认证主体 */
  private final Object principal;

  /** 认证凭证(JWT令牌) */
  private String credentials;

  /**
   * 创建未认证的令牌
   * @param principal 认证主体
   * @param credentials JWT令牌
   */
  public CookieJwtAuthenticationToken(Object principal, String credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(false);
  }

  /**
   * 创建已认证的令牌
   * @param principal 认证主体
   * @param credentials JWT令牌
   * @param authorities 授权信息集合
   */
  public CookieJwtAuthenticationToken(
      Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true);
  }

  /**
   * 创建未认证的令牌的静态工厂方法
   * @param userIdentify 用户标识
   * @param token JWT令牌
   * @return 未认证的令牌实例
   */
  public static CookieJwtAuthenticationToken unauthenticated(String userIdentify, String token) {
    return new CookieJwtAuthenticationToken(userIdentify, token);
  }

  /**
   * 创建已认证的令牌的静态工厂方法
   * @param principal 用户详情
   * @param token JWT令牌
   * @param authorities 授权信息集合
   * @return 已认证的令牌实例
   */
  public static CookieJwtAuthenticationToken authenticated(
      UserDetails principal, String token, Collection<? extends GrantedAuthority> authorities) {
    return new CookieJwtAuthenticationToken(principal, token, authorities);
  }

  /**
   * 获取认证凭证
   * @return JWT令牌
   */
  @Override
  public String getCredentials() {
    return this.credentials;
  }

  /**
   * 获取认证主体
   * @return 认证主体对象
   */
  @Override
  public Object getPrincipal() {
    return this.principal;
  }
}
