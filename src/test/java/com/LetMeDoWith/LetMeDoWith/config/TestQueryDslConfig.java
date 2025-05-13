package com.LetMeDoWith.LetMeDoWith.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/** Test class를 위힌 QueryDSL configuration. @DataJpaTest 를 포함하는 테스트에 본 설정을 import하여 사용한다. */
@TestConfiguration
public class TestQueryDslConfig {

    @PersistenceContext private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
