package com.LetMeDoWith.LetMeDoWith.common.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheHelper {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    private final ObjectMapper objectMapper;

    public <T> T get(String cacheName, String key, Class<T> type) {

        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);

        if (cachePolicy.redisValueType().equals(RedisValueType.HASH)) {
            String redisKey = this.buildRedisKey(cacheName, key);
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisKey);
            if (entries.isEmpty()) {
                return null;
            }
            return objectMapper.convertValue(entries, type);
        } else {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                throw new IllegalStateException("No Cache found for cache name: " + cacheName);
            }
            return cache.get(key, type);
        }
    }

    public <T> T get(String cacheName, String key, String field, Class<T> fieldType) {

        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);
        String cacheKey = this.buildRedisKey(cacheName, key);

        if (cachePolicy.redisValueType().equals(RedisValueType.HASH)) {
            Object value = redisTemplate.opsForHash().get(cacheKey, field);
            if (value == null) {
                return null;
            }

            if (!fieldType.isInstance(value)) {
                throw new IllegalArgumentException("Cached value type mismatch. Expected: "
                        + value.getClass().getName() + ", but input parameter filedType: " + fieldType.getName());
            }
            return fieldType.cast(value);
        } else {
            throw new IllegalArgumentException("CachePolicy redisValueType is not HASH for cache name: " + cacheName);
        }
    }

    public void put(String cacheName, String key, Object value) {

        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);
        String redisKey = this.buildRedisKey(cacheName, key);

        if (cachePolicy.redisValueType().equals(RedisValueType.HASH)) {
            Map<?, ?> mapValue;
            if (value instanceof Map<?, ?> rawMap) {
                mapValue = rawMap;
            } else {
                mapValue = objectMapper.convertValue(value, Map.class);
            }
            redisTemplate.opsForHash().putAll(redisKey, mapValue);
            redisTemplate.expire(redisKey, cachePolicy.ttl());
        } else {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) throw new IllegalStateException("No Cache found for cache name: " + cacheName);
            cache.put(key, value);
        }
    }

    private String buildRedisKey(String cacheName, String key) {
        return cacheName + "::" + key;
    }
}
