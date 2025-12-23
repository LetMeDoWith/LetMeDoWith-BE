package com.LetMeDoWith.LetMeDoWith.common.cache;

import java.time.Duration;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CachePolicy implements CachePolicySpec {
    APPLE_PUBLIC_KEY(CacheName.APPLE_PUBLIC_KEY, RedisValueType.STRING, Duration.ofDays(7)),
    GOOGLE_PUBLIC_KEY(CacheName.GOOGLE_PUBLIC_KEY, RedisValueType.STRING, Duration.ofDays(7)),
    KAKAO_PUBLIC_KEY(CacheName.KAKAO_PUBLIC_KEY, RedisValueType.STRING, Duration.ofDays(7)),

    DOWITH_TASK(CacheName.DOWITH_TASK, RedisValueType.HASH, Duration.ofDays(14)),
    LAZY_DOWITH_TASK_MEMBERS(CacheName.LAZY_DOWITH_TASK_MEMBERS, RedisValueType.LIST, null);

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
