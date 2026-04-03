package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            String memberId = AuthUtil.getMemberId();
            if (memberId == null) return Optional.of("system");
            else return Optional.of(memberId);
        };
    }

    @Bean(name = "auditingDateTimeProvider")
    @Profile("test")
    public DateTimeProvider testAuditingDateTimeProvider() {
        return () -> Optional.of(SystemTimeUtil.now());
    }

    @Bean(name = "auditingDateTimeProvider")
    @Profile("!test")
    public DateTimeProvider systemAuditingDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}
