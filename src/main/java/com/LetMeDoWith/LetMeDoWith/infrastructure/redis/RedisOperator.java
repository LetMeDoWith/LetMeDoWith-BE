package com.LetMeDoWith.LetMeDoWith.infrastructure.redis;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicySpec;
import com.LetMeDoWith.LetMeDoWith.common.cache.RedisValueType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisOperator {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis Key 생성
     *
     * @param policy CachePolicySpec
     * @param key    식별자 (없을 경우 빈 문자열)
     * @return prefix::key 형태의 full key
     */
    private String buildKey(CachePolicySpec policy, String key) {
        if (key == null || key.isBlank()) {
            return policy.cacheName();
        }
        return policy.cacheName() + "::" + key;
    }

    /**
     * TTL 적용 (Policy에 TTL이 설정된 경우에만)
     *
     * @param fullKey Redis Full Key
     * @param policy  CachePolicySpec
     */
    private void applyTtl(String fullKey, CachePolicySpec policy) {
        if (policy.ttl() != null) {
            redisTemplate.expire(fullKey, policy.ttl().toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * CachePolicy의 RedisValueType 검증
     */
    private void validatePolicy(CachePolicySpec policy, RedisValueType expectedType) {
        if (policy.redisValueType() != expectedType) {
            throw new IllegalArgumentException(String.format(
                    "Invalid CachePolicy. Expected: %s, Actual: %s, Policy: %s",
                    expectedType, policy.redisValueType(), policy.name()));
        }
    }

    // ==================================================================================
    // Value Ops (String/Object)
    // ==================================================================================

    public void set(CachePolicySpec policy, String key, Object value) {
        validatePolicy(policy, RedisValueType.STRING);
        String fullKey = buildKey(policy, key);

        redisTemplate.opsForValue().set(fullKey, value);
        applyTtl(fullKey, policy);
    }

    public <T> T get(CachePolicySpec policy, String key, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.STRING);
        String fullKey = buildKey(policy, key);

        Object value = redisTemplate.opsForValue().get(fullKey);
        if (value == null) {
            return null;
        }
        return objectMapper.convertValue(value, clazz);
    }

    // ==================================================================================
    // List Ops
    // ==================================================================================

    public void pushRightAll(CachePolicySpec policy, String key, List<Object> list) {
        validatePolicy(policy, RedisValueType.LIST);
        if (list == null || list.isEmpty()) {
            return;
        }
        String fullKey = buildKey(policy, key);

        redisTemplate.opsForList().rightPushAll(fullKey, list);
        applyTtl(fullKey, policy);
    }

    public <T> List<T> getList(CachePolicySpec policy, String key, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.LIST);
        String fullKey = buildKey(policy, key);

        List<Object> rawList = redisTemplate.opsForList().range(fullKey, 0, -1);
        if (rawList == null || rawList.isEmpty()) {
            return Collections.emptyList();
        }

        return rawList.stream()
                .map(item -> objectMapper.convertValue(item, clazz))
                .collect(Collectors.toList());
    }

    public void removeList(CachePolicySpec policy, String key, Object value) {
        validatePolicy(policy, RedisValueType.LIST);
        String fullKey = buildKey(policy, key);

        // count > 0: 처음부터 count개 만큼 삭제, count < 0: 끝에서부터, count = 0: 모든 value 삭제
        // 여기서는 1개만 지우는 것으로 가정 (필요시 파라미터화 가능하지만 일단 1로 고정)
        redisTemplate.opsForList().remove(fullKey, 1, value);
    }

    // ==================================================================================
    // ZSet Ops
    // ==================================================================================

    public void zAdd(CachePolicySpec policy, String key, Object value, double score) {
        validatePolicy(policy, RedisValueType.ZSET);
        String fullKey = buildKey(policy, key);

        redisTemplate.opsForZSet().add(fullKey, value, score);
        applyTtl(fullKey, policy);
    }

    public <T> Set<T> zRange(CachePolicySpec policy, String key, long start, long end, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.ZSET);
        String fullKey = buildKey(policy, key);

        Set<Object> rawSet = redisTemplate.opsForZSet().range(fullKey, start, end);
        if (rawSet == null || rawSet.isEmpty()) {
            return Collections.emptySet();
        }

        return rawSet.stream()
                .map(item -> objectMapper.convertValue(item, clazz))
                .collect(Collectors.toSet());
    }

    // ==================================================================================
    // Hash Ops
    // ==================================================================================

    public void putHash(CachePolicySpec policy, String key, Object dto) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        Map<String, Object> map = objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {});
        redisTemplate.opsForHash().putAll(fullKey, map);
        applyTtl(fullKey, policy);
    }

    public <T> T getHash(CachePolicySpec policy, String key, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(fullKey);
        if (entries.isEmpty()) {
            return null;
        }
        return objectMapper.convertValue(entries, clazz);
    }

    /**
     * 다건의 DTO를 각각 별도의 Redis Key(Hash)로 저장 (Pipelining 적용)
     *
     * @param policy    CachePolicySpec
     * @param dtoList   저장할 DTO 리스트
     * @param keyMapper DTO에서 Key 식별자를 추출하는 함수
     * @param <T>       DTO 타입
     */
    public <T> void putHashes(CachePolicySpec policy, List<T> dtoList, Function<T, String> keyMapper) {
        validatePolicy(policy, RedisValueType.HASH);
        if (dtoList == null || dtoList.isEmpty()) {
            return;
        }

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (T dto : dtoList) {
                String key = keyMapper.apply(dto);
                String fullKey = buildKey(policy, key);

                Map<String, Object> map = objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {});
                redisTemplate.opsForHash().putAll(fullKey, map);

                if (policy.ttl() != null) {
                    redisTemplate.expire(fullKey, policy.ttl().toMillis(), TimeUnit.MILLISECONDS);
                }
            }
            return null;
        });
    }

    public void putHashField(CachePolicySpec policy, String key, String field, Object value) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        redisTemplate.opsForHash().put(fullKey, field, value);
        applyTtl(fullKey, policy);
    }

    public <T> T getHashField(CachePolicySpec policy, String key, String field, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        Object value = redisTemplate.opsForHash().get(fullKey, field);
        if (value == null) {
            return null;
        }
        return objectMapper.convertValue(value, clazz);
    }

    public void deleteHashField(CachePolicySpec policy, String key, String field) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        redisTemplate.opsForHash().delete(fullKey, field);
    }
}
