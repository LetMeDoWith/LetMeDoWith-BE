package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.common.cache.CacheName;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
@Profile("!dev")
public class CacheConfig {

    @Bean
    public CacheManager socialProviderPublicKeyCacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(1L)); // TODO - default TTL

        Map<String, RedisCacheConfiguration> individualConfiguration = new HashMap<>();
        // TODO - 각 Social Provider 마다 API Refresh Time 고려하여 TTL 설정 변경 필요
        individualConfiguration.put(CacheName.APPLE_PUBLIC_KEY, defaultConfig.entryTtl(Duration.ofMinutes(1L)));
        individualConfiguration.put(CacheName.GOOGLE_PUBLIC_KEY, defaultConfig.entryTtl(Duration.ofMinutes(1L)));
        individualConfiguration.put(CacheName.KAKAO_PUBLIC_KEY, defaultConfig.entryTtl(Duration.ofMinutes(1L)));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(individualConfiguration)
                .build();
    }
}
