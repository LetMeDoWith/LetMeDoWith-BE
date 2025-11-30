package com.LetMeDoWith.LetMeDoWith;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server")})
@SpringBootApplication
// @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
// DataSourceTransactionManagerAutoConfiguration.class,
// HibernateJpaAutoConfiguration.class})
public class LetMeDoWithApplication {

    public static void main(String[] args) {
        SpringApplication.run(LetMeDoWithApplication.class, args);
    }
}
