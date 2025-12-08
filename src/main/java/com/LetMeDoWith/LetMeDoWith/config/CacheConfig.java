package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicy;
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
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(1L));

        Map<String, RedisCacheConfiguration> individualConfiguration = new HashMap<>();
        individualConfiguration.put(
                CachePolicy.APPLE_PUBLIC_KEY.cacheName(), defaultConfig.entryTtl(CachePolicy.APPLE_PUBLIC_KEY.ttl()));
        individualConfiguration.put(
                CachePolicy.GOOGLE_PUBLIC_KEY.cacheName(), defaultConfig.entryTtl(CachePolicy.GOOGLE_PUBLIC_KEY.ttl()));
        individualConfiguration.put(
                CachePolicy.KAKAO_PUBLIC_KEY.cacheName(), defaultConfig.entryTtl(CachePolicy.KAKAO_PUBLIC_KEY.ttl()));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf)
                //                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(individualConfiguration)
                .build();
    }
}
