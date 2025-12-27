package com.LetMeDoWith.LetMeDoWith.config.batch;

import com.LetMeDoWith.LetMeDoWith.config.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** Batch 애플리케이션 전용 설정 집합 */
@Configuration
@Import({
    AsyncConfig.class,
    JacksonConfig.class,
    JpaAuditingConfiguration.class,
    QueryDslJpaConfig.class,
    RedisConfig.class,
    UtilConfig.class,
})
public class BatchAppConfig {}
