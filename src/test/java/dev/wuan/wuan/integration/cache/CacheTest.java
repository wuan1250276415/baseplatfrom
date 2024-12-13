package dev.wuan.wuan.integration.cache;

import static org.assertj.core.api.Assertions.assertThat;

import dev.wuan.wuan.config.cache.CacheConfig;
import dev.wuan.wuan.service.CacheService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringJUnitConfig(classes = {CacheConfig.class, CacheService.class})
@Testcontainers
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class CacheTest {

  @Container
  public static GenericContainer<?> redisContainer =
      new GenericContainer<>(DockerImageName.parse("redis:7.4.0-alpine")).withExposedPorts(6379);

  @DynamicPropertySource
  static void rabbitProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redisContainer::getHost);
    registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
  }

  @BeforeAll
  static void beforeAll() {
    redisContainer.start();
  }

  @Resource private CacheService cacheService;

  @Test
  void
      getVerifyCodeBy_upsertVerifyCodeBy_whenSetCacheValue_subsequentGetCacheShouldReturnUpdatedValue() {
    cacheService.upsertVerifyCodeBy("WsxOtE0d6Vc1glZ", "ej1x8T4XiluV8D216");
    String verifyCode = cacheService.getVerifyCodeBy("WsxOtE0d6Vc1glZ");
    assertThat(verifyCode).isEqualTo("ej1x8T4XiluV8D216");
  }

  @Test
  void removeVerifyCodeBy_whenRemoveCacheValue_subsequentGetCacheShouldReturnNull() {
    cacheService.upsertVerifyCodeBy("WsxOtE0d6Vc1glZ", "ej1x8T4XiluV8D216");
    String verifyCode = cacheService.getVerifyCodeBy("WsxOtE0d6Vc1glZ");
    cacheService.removeVerifyCodeBy("WsxOtE0d6Vc1glZ");
    String verifyCode2 = cacheService.getVerifyCodeBy("WsxOtE0d6Vc1glZ");
    assertThat(verifyCode).isEqualTo("ej1x8T4XiluV8D216");
    assertThat(verifyCode2).isNull();
  }

  @Test
  void clearAllVerifyCode_whenCleanCache_subsequentGetCacheShouldReturnNewValue() {
    cacheService.upsertVerifyCodeBy("WsxOtE0d6Vc1glZ", "ej1x8T4XiluV8D216");
    cacheService.upsertVerifyCodeBy("hNYcK0MDjX4197", "Ll1v93jiXwHLji");
    String verifyCode1 = cacheService.getVerifyCodeBy("WsxOtE0d6Vc1glZ");
    String verifyCode2 = cacheService.getVerifyCodeBy("hNYcK0MDjX4197");
    cacheService.clearAllVerifyCode();
    String verifyCode3 = cacheService.getVerifyCodeBy("WsxOtE0d6Vc1glZ");
    String verifyCode4 = cacheService.getVerifyCodeBy("hNYcK0MDjX4197");
    assertThat(verifyCode1).isEqualTo("ej1x8T4XiluV8D216");
    assertThat(verifyCode2).isEqualTo("Ll1v93jiXwHLji");
    assertThat(verifyCode3).isNull();
    assertThat(verifyCode4).isNull();
  }
}
