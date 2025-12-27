package com.LetMeDoWith.LetMeDoWith.config.api;

import com.LetMeDoWith.LetMeDoWith.config.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** API 애플리케이션 전용 설정 집합 */
@Configuration
@Import({
    AsyncConfig.class,
    CacheConfig.class,
    FeedbackCreationPolicyConfig.class,
    JacksonConfig.class,
    JpaAuditingConfiguration.class,
    QueryDslJpaConfig.class,
    RedisConfig.class,
    UtilConfig.class,
    WebClientConfig.class,
    WebConfig.class,
})
public class ApiAppConfig {}
