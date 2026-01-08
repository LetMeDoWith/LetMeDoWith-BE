package com.LetMeDoWith.LetMeDoWith.common.redis;

import java.time.Duration;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CachePolicy implements RedisPolicySpec {
    APPLE_PUBLIC_KEY(CacheName.APPLE_PUBLIC_KEY, RedisValueType.STRING, Duration.ofDays(7)),
    GOOGLE_PUBLIC_KEY(CacheName.GOOGLE_PUBLIC_KEY, RedisValueType.STRING, Duration.ofDays(7)),
    KAKAO_PUBLIC_KEY(CacheName.KAKAO_PUBLIC_KEY, RedisValueType.STRING, Duration.ofDays(7));

    private final String keyName;
    private final RedisValueType redisValueType;
    private final Duration ttl;

    public static CachePolicy fromCacheName(String cacheName) {
        for (CachePolicy policy : CachePolicy.values()) {
            if (policy.keyName.equals(cacheName)) {
                return policy;
            }
        }
        throw new IllegalArgumentException("No CachePolicy found for cache name: " + cacheName);
    }

    public String keyName() {
        return keyName;
    }

    public RedisValueType redisValueType() {
        return redisValueType;
    }

    public Duration ttl() {
        return ttl;
    }
}
