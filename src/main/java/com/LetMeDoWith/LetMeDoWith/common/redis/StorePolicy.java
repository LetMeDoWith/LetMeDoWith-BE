package com.LetMeDoWith.LetMeDoWith.common.redis;

import java.time.Duration;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StorePolicy implements RedisPolicySpec {
    FEEDBACK_AVAILABLE_DOWITH_TASKS(CacheName.FEEDBACK_AVAILABLE_DOWITH_TASKS, RedisValueType.HASH,
        Duration.ofHours(1)),
    LAZY_DOWITH_TASK_IDS(CacheName.LAZY_DOWITH_TASK_IDS, RedisValueType.LIST, Duration.ofHours(1));

    private final String keyName;
    private final RedisValueType redisValueType;
    private final Duration ttl;

    @Override
    public String keyName() {
        return keyName;
    }

    @Override
    public RedisValueType redisValueType() {
        return redisValueType;
    }

    @Override
    public Duration ttl() {
        return ttl;
    }
}