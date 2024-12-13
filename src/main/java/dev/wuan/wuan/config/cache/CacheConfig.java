package dev.wuan.wuan.config.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 缓存配置类
 */
@EnableCaching
@Configuration
public class CacheConfig {

  /**
   * 验证码缓存名称
   */
  public static final String VERIFY_CODE = "verifyCode";

  /**
   * 配置Redis缓存管理器
   * @param connectionFactory Redis连接工厂
   * @return Redis缓存管理器实例
   */
  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    // 创建缓存配置映射
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

    // 配置验证码缓存
    // 设置1分钟过期时间并禁用空值缓存
    cacheConfigurations.put(
        VERIFY_CODE,
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(1))  // 设置1分钟的过期时间
            .disableCachingNullValues());  // 禁止缓存null值

    // 构建并返回缓存管理器
    return RedisCacheManager.builder(connectionFactory)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
