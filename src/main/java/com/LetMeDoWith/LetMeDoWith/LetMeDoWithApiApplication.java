package com.LetMeDoWith.LetMeDoWith;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server")})
@SpringBootApplication(
        scanBasePackages = {
            "com.LetMeDoWith.LetMeDoWith.application",
            "com.LetMeDoWith.LetMeDoWith.presentation",
            "com.LetMeDoWith.LetMeDoWith.common",
            "com.LetMeDoWith.LetMeDoWith.domain",
            "com.LetMeDoWith.LetMeDoWith.infrastructure",
            "com.LetMeDoWith.LetMeDoWith.config.api",
        })
@EnableJpaRepositories(basePackages = "com.LetMeDoWith.LetMeDoWith.infrastructure")
@EntityScan(
        basePackages = {
            "com.LetMeDoWith.LetMeDoWith.common",
            "com.LetMeDoWith.LetMeDoWith.domain",
            "com.LetMeDoWith.LetMeDoWith.infrastructure",
        })
public class LetMeDoWithApiApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LetMeDoWithApiApplication.class);
        application.setDefaultProperties(Map.of("spring.config.name", "application,application-api"));
        application.run(args);
    }
}
