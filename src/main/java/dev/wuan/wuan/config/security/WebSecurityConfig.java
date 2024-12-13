package dev.wuan.wuan.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Web安全配置类
 * 用于配置Spring Security的Web安全相关功能
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  /** 用户详情服务 */
  private final UserDetailsServiceImpl userDetailsService;

  /** Cookie JWT管理组件 */
  private final CookieJwt cookieJwt;

  /** CORS配置源 */
  private final CorsConfigurationSource corsConfigurationSource;

  /**
   * 配置密码编码器
   * @return BCrypt密码编码器实例
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 配置认证管理器
   * @param authenticationConfiguration 认证配置
   * @return 认证管理器实例
   * @throws Exception 配置异常
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * 配置公共端点匹配器
   * 定义不需要认证就可以访问的URL路径
   * @return 请求匹配器实例
   */
  @Bean
  public RequestMatcher publicEndPointMatcher() {
    return new OrRequestMatcher(
        new AntPathRequestMatcher("/auth/sign-in", "POST"),
        new AntPathRequestMatcher("/auth/sign-up", "POST"),
        new AntPathRequestMatcher("/v3/api-docs/**", "GET"),
        new AntPathRequestMatcher("/swagger-ui/**", "GET"),
        new AntPathRequestMatcher("/swagger-ui.html", "GET"),
        new AntPathRequestMatcher("/error"));
  }

  /**
   * 配置安全过滤器链
   * 定义主要的安全配置，包括认证、授权、会话管理等
   * @param http HTTP安全配置对象
   * @return 配置好的安全过滤器链
   * @throws Exception 配置异常
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    RestfulAuthenticationEntryPointHandler restfulAuthenticationEntryPointHandler =
        new RestfulAuthenticationEntryPointHandler();
    /*
    <Stateless API CSRF protection>
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
    */
    http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource));
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(publicEndPointMatcher())
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            (exceptionHandling) ->
                exceptionHandling
                    .accessDeniedHandler(restfulAuthenticationEntryPointHandler)
                    .authenticationEntryPoint(restfulAuthenticationEntryPointHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterAt(
            new CookieJwtAuthenticationFilter(cookieJwt, userDetailsService),
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
