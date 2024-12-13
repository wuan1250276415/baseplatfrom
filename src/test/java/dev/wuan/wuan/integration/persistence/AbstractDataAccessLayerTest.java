package dev.wuan.wuan.integration.persistence;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@JooqTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScans({@ComponentScan("jooq.tables.daos"), @ComponentScan("dev.wuan.wuan.repository")})
@Testcontainers
public class AbstractDataAccessLayerTest {

  public static MySQLContainer<?> mysql =
      new MySQLContainer<>("mysql:8.4.2").withDatabaseName("wuan").withUsername("root");

  @DynamicPropertySource
  static void mysqlProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mysql::getJdbcUrl);
    registry.add("spring.datasource.username", mysql::getUsername);
    registry.add("spring.datasource.password", mysql::getPassword);
    registry.add("spring.flyway.locations", () -> "classpath:db/migration/test");
    registry.add("spring.flyway.default-schema", () -> "wuan");
  }

  static {
    mysql.start();
  }
}
