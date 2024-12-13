package dev.wuan.wuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"dev.wuan.wuan", "org.jooq.generated"})
public class ApplicationService {

  public static void main(String[] args) {
    SpringApplication.run(ApplicationService.class, args);
  }
}
