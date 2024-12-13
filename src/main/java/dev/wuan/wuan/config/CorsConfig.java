package dev.wuan.wuan.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 用于配置跨域资源共享(CORS)相关的设置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  /** 允许的源域名 */
  @Value("${cors.allowedOrigins}")
  private String allowedOrigins;

  /** 允许的HTTP方法 */
  @Value("${cors.allowedMethods}")
  private String allowedMethods;

  /** 允许的HTTP头 */
  @Value("${cors.allowedHeaders}")
  private String allowedHeaders;

  /**
   * 添加CORS映射
   * 为所有路径启用CORS支持
   * @param registry CORS注册表
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**");
  }

  /**
   * 配置CORS源
   * 详细配置跨域资源共享的各项参数
   * @return CORS配置源
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // 设置允许的源域名
    configuration.setAllowedOrigins(List.of(allowedOrigins));
    // 设置允许的HTTP方法
    configuration.setAllowedMethods(List.of(allowedMethods));
    // 设置允许的HTTP头
    configuration.setAllowedHeaders(List.of(allowedHeaders));
    // 允许发送凭证信息
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 对所有路径应用这些CORS配置
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
