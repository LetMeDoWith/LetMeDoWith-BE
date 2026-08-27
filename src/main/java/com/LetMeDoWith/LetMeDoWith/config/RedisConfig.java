package com.LetMeDoWith.LetMeDoWith.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .build();

        // TODO - 실제 자격증명 인입 확인용 임시 로그. 확인 끝나면 제거할 것 (비밀번호는 마스킹해서 출력)
        log.info(
                "Redis connect info - host: {}, port: {}, username: {}, password: {}",
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getUsername(),
                maskPassword(redisProperties.getPassword()));

        // Single Redis Server
        RedisStandaloneConfiguration standaloneConfig =
                new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        if (redisProperties.getUsername() != null) {
            standaloneConfig.setUsername(redisProperties.getUsername());
        }
        if (redisProperties.getPassword() != null) {
            standaloneConfig.setPassword(redisProperties.getPassword());
        }

        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }

    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "(empty)";
        }
        if (password.length() <= 4) {
            return "*".repeat(password.length());
        }
        return password.substring(0, 2) + "*".repeat(password.length() - 4) + password.substring(password.length() - 2);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // key / value 직렬화 설정
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);

        // HASH field / value 직렬화 설정
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    //    @Bean
    //    @Qualifier("legacyRedisTemplate")
    //    public RedisTemplate<?, ?> legacyRedisTemplate() {
    //        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
    //        redisTemplate.setConnectionFactory(redisConnectionFactory());
    //        redisTemplate.afterPropertiesSet();
    //        return redisTemplate;
    //    }
}
