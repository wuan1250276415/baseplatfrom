package dev.wuan.wuan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

/**
 * 静态资源处理配置类
 * 用于配置静态资源的访问路径、缓存和编码等
 */
@Configuration
public class StaticResourceHandler implements WebMvcConfigurer {

  /** 静态资源的访问路径模式 */
  @Value("${static-resource.handler}")
  private String resourceHandler;

  /** 静态资源的实际存储位置 */
  @Value("${static-resource.locations}")
  private String resourceLocations;

  /**
   * 配置静态资源处理器
   * @param registry 资源处理器注册表
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        // 设置资源访问路径
        .addResourceHandler(resourceHandler)
        // 设置资源实际位置
        .addResourceLocations(resourceLocations)
        // 设置缓存时间为60秒
        .setCachePeriod(60)
        // 启用资源链
        .resourceChain(true)
        // 添加编码资源解析器,支持gzip等压缩格式
        .addResolver(new EncodedResourceResolver());
  }
}
