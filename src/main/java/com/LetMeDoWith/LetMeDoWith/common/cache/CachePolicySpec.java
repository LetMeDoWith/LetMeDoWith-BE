package com.LetMeDoWith.LetMeDoWith.common.cache;

import java.time.Duration;

public interface CachePolicySpec {
    String cacheName();

    RedisValueType redisValueType();

    Duration ttl();

    String name();
}
