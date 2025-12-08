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

    private final RedisTemplate<Object, Object> redisTemplate;
    private final CacheManager cacheManager;

    private final ObjectMapper objectMapper;

    // 필요사항
    //// 캐시 name 기반으로 캐시 넣기
    //// 캐시 name 별로 Redis에 캐시하고 있는 자료구조가 다름
    //////// ex. Dowith 같은 경우, Hash 구조로 되어있음
    //// Hash 구조에서 key 기반으로 value 가져오기
    //// Hash 구조에서 key 기반으로 value 삭제하기
    //// 과정에서

    public <T> T get(String cacheName, String key, Class<T> type) {

        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);

        if (cachePolicy.redisValueType().equals(RedisValueType.HASH)) {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            if (entries.isEmpty()) {
                return null;
            }
            return objectMapper.convertValue(entries, type);
        } else {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                return null;
            }
            return cache.get(key, type);
        }
    }

    public <T> T get(String cacheName, String key, String field, Class<T> type) {

        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);

        if (cachePolicy.redisValueType().equals(RedisValueType.HASH)) {
            Object value = redisTemplate.opsForHash().get(key, field);
            if (value == null) {
                return null;
            }

            if (type.isInstance(value)) {
                return type.cast(value);
            }
            return objectMapper.convertValue(value, type);
        } else {
            throw new IllegalArgumentException("Cache type is not HASH for cache name: " + cacheName);
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
        } else {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) throw new IllegalArgumentException("No Cache found for cache name: " + cacheName);
            cache.put(key, value);
        }
    }

    private String buildRedisKey(String cacheName, String key) {
        return cacheName + "::" + key;
    }
}
