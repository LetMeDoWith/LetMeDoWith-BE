package com.LetMeDoWith.LetMeDoWith.common.cache;

import java.time.Duration;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CachePolicy {
    APPLE_PUBLIC_KEY(CacheName.APPLE_PUBLIC_KEY, RedisValueType.STRING, Duration.ofMinutes(1L)),
    GOOGLE_PUBLIC_KEY(CacheName.GOOGLE_PUBLIC_KEY, RedisValueType.STRING, Duration.ofMinutes(1L)),
    KAKAO_PUBLIC_KEY(CacheName.KAKAO_PUBLIC_KEY, RedisValueType.STRING, Duration.ofMinutes(1L)),

    DOWITH_TASK(CacheName.DOWITH_TASK, RedisValueType.HASH, Duration.ofDays(14));

    private final String cacheName;
    private final RedisValueType redisValueType;
    private final Duration ttl;

    public static CachePolicy fromCacheName(String cacheName) {
        for (CachePolicy policy : CachePolicy.values()) {
            if (policy.cacheName.equals(cacheName)) {
                return policy;
            }
        }
        throw new IllegalArgumentException("No CachePolicy found for cache name: " + cacheName);
    }

    public String cacheName() {
        return cacheName;
    }

    public RedisValueType redisValueType() {
        return redisValueType;
    }

    public Duration ttl() {
        return ttl;
    }
}
