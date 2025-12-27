package com.LetMeDoWith.LetMeDoWith;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
        scanBasePackages = {
            "com.LetMeDoWith.LetMeDoWith.batch",
            "com.LetMeDoWith.LetMeDoWith.infrastructure.feed",
            "com.LetMeDoWith.LetMeDoWith.infrastructure.redis",
            "com.LetMeDoWith.LetMeDoWith.config.batch",
        })
@EntityScan(
        basePackages = {
            "com.LetMeDoWith.LetMeDoWith.common",
            "com.LetMeDoWith.LetMeDoWith.domain",
            "com.LetMeDoWith.LetMeDoWith.infrastructure",
        })
public class LetMeDoWithBatchApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LetMeDoWithBatchApplication.class);
        application.setDefaultProperties(Map.of("spring.config.name", "application,application-batch"));
        application.run(args);
    }
}
