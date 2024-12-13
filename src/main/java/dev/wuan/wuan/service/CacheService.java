package dev.wuan.wuan.service;

import dev.wuan.wuan.config.cache.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 缓存服务类
 * 用于管理验证码的缓存操作
 */
@Service
@Slf4j
public class CacheService {

  /**
   * 根据标识获取验证码
   * @param identify 标识
   * @return 验证码，如果不存在返回null
   */
  @Cacheable(value = CacheConfig.VERIFY_CODE, key = "{#identify}", unless = "#result == null")
  public String getVerifyCodeBy(String identify) {
    log.debug("从缓存获取验证码, identify: {}", identify);
    return null;
  }

  /**
   * 更新或插入验证码
   * @param identify 标识
   * @param value 验证码值
   * @return 更新后的验证码
   */
  @CachePut(value = CacheConfig.VERIFY_CODE, key = "{#identify}")
  public String upsertVerifyCodeBy(String identify, String value) {
    log.debug("更新验证码缓存, identify: {}, value: {}", identify, value);
    return value;
  }

  /**
   * 根据标识删除验证码
   * @param identify 标识
   */
  @CacheEvict(value = CacheConfig.VERIFY_CODE, key = "{#identify}")
  public void removeVerifyCodeBy(String identify) {
    log.debug("删除验证码缓存, identify: {}", identify);
  }

  /**
   * 清除所有验证码缓存
   */
  @CacheEvict(value = CacheConfig.VERIFY_CODE, allEntries = true)
  public void clearAllVerifyCode() {
    log.debug("清除所有验证码缓存");
  }
}
