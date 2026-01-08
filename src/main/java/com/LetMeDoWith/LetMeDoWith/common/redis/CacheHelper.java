package com.LetMeDoWith.LetMeDoWith.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            assert cache != null;
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
                        + value.getClass().getName() + ", but input parameter filedType: "
                        + fieldType.getName());
            }
            return fieldType.cast(value);
        } else {
            throw new IllegalArgumentException("CachePolicy redisValueType is not HASH for cache name: " + cacheName);
        }
    }

    public <T> List<T> getByRange(String cacheName, String key, long start, long end, Class<T> elementType) {
        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);
        String redisKey = this.buildRedisKey(cacheName, key);

        if (cachePolicy.redisValueType().equals(RedisValueType.LIST)) {
            List<Object> rawList = redisTemplate.opsForList().range(redisKey, start, end);
            if (rawList == null) {
                return Collections.emptyList();
            }
            List<T> resultList = new ArrayList<>();
            for (Object item : rawList) {
                T element = objectMapper.convertValue(item, elementType);
                resultList.add(element);
            }
            return resultList;
        } else {
            throw new IllegalArgumentException("CachePolicy redisValueType is not LIST for cache name: " + cacheName);
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
            assert cache != null;
            cache.put(key, value);
        }
    }

    public void push(String cacheName, String key, Object element) {
        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);
        String redisKey = this.buildRedisKey(cacheName, key);

        if (cachePolicy.redisValueType().equals(RedisValueType.LIST)) {
            redisTemplate.opsForList().rightPush(redisKey, element);
            if (cachePolicy.ttl() != null) {
                redisTemplate.expire(redisKey, cachePolicy.ttl());
            }
        } else {
            throw new IllegalArgumentException("CachePolicy redisValueType is not LIST for cache name: " + cacheName);
        }
    }

    public void remove(String cacheName, String key) {
        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);

        if (cachePolicy.redisValueType().equals(RedisValueType.HASH)
                || cachePolicy.redisValueType().equals(RedisValueType.LIST)) {
            String redisKey = this.buildRedisKey(cacheName, key);
            redisTemplate.delete(redisKey);
        } else {
            Cache cache = cacheManager.getCache(cacheName);
            assert cache != null;
            cache.evict(key);
        }
    }

    public <T> void remove(String cacheName, String key, T element) {
        CachePolicy cachePolicy = CachePolicy.fromCacheName(cacheName);

        if (!cachePolicy.redisValueType().equals(RedisValueType.LIST)) {
            throw new IllegalArgumentException("CachePolicy redisValueType is not LIST for cache name: " + cacheName);
        }

        String cacheKey = this.buildRedisKey(cacheName, key);

        redisTemplate.opsForList().remove(cacheKey, 0, element);
    }

    private String buildRedisKey(String cacheName, String key) {
        if (key == null || key.isEmpty()) {
            return cacheName;
        }
        return cacheName + "::" + key;
    }
}
