package com.LetMeDoWith.LetMeDoWith;

import io.sentry.Sentry;
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

        try {
            throw new Exception("This is a test. for Sentry");
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        // Counter metric - track occurrences
        Sentry.metrics().count("button_click", 1.0);

        // Gauge metric - track a value that can go up and down
        Sentry.metrics().gauge("queue_size", 42.0);

        // Distribution metric - track a value distribution
        Sentry.metrics().distribution("response_time", 150.0);
    }
}
