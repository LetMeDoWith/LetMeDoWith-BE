package com.LetMeDoWith.LetMeDoWith.infrastructure.redis;

import com.LetMeDoWith.LetMeDoWith.common.redis.RedisPolicySpec;

public class RedisKeyGenerator {

    public static String buildKey(RedisPolicySpec policy, String key) {
        if (key == null || key.isBlank()) {
            return policy.keyName();
        }
        return policy.keyName() + "::" + key;
    }
}
