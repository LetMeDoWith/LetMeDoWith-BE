package com.LetMeDoWith.LetMeDoWith.common.redis;

import java.time.Duration;

public interface RedisPolicySpec {

    String keyName();

    RedisValueType redisValueType();

    Duration ttl();

    String name();
}