package dev.wuan.wuan.config.security;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * HTTP防火墙配置类
 * 用于配置Spring Security的HTTP防火墙和请求拒绝处理器
 */
@Configuration
public class HttpFireWallConfig {

  /**
   * 配置HTTP防火墙
   * @return 严格的HTTP防火墙实例
   */
  @Bean
  public HttpFirewall getHttpFirewall() {
    return new StrictHttpFirewall();
  }

  /**
   * 配置请求拒绝处理器
   * 当请求被防火墙拒绝时，返回400错误响应
   * @return 请求拒绝处理器实例
   */
  @Bean
  public RequestRejectedHandler requestRejectedHandler() {
    return (request, response, requestRejectedException) -> {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType(MediaType.TEXT_PLAIN_VALUE);
      try (PrintWriter writer = response.getWriter()) {
        writer.write(requestRejectedException.getMessage());
      }
    };
  }
}
