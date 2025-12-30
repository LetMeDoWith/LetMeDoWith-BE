package com.LetMeDoWith.LetMeDoWith.infrastructure.redis;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicySpec;
import com.LetMeDoWith.LetMeDoWith.common.cache.RedisValueType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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

    /**
     * Redis 작업을 수행하되, 실패하거나 값이 없으면 빈 Optional을 반환한다. (예외를 로그로 남기고 안전하게 무시함)
     *
     * @param operation Redis 수행 로직 (Lambda)
     * @param <T>       반환 타입
     * @return Redis 값 또는 Optional.empty()
     */
    public <T> Optional<T> execute(Supplier<T> operation) {
        try {
            T result = operation.get();
            return Optional.ofNullable(result);
        } catch (DataAccessException e) {
            log.warn("Redis Operation Failed. Cause: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 반환값이 없는(void) Redis 작업(저장/삭제)을 안전하게 수행한다. 실패 시 로그만 남기고 진행 (Fire and Forget)
     *
     * @param operation Redis 수행 로직 (Lambda)
     */
    public void execute(Runnable operation) {
        try {
            operation.run();
        } catch (DataAccessException e) {
            log.warn("Redis Write/Delete Operation Failed. Cause: {}", e.getMessage());
        }
    }

    // ==================================================================================
    // Value Ops (String/Object)
    // ==================================================================================

    public <T> void set(CachePolicySpec policy, String key, T value) {
        validatePolicy(policy, RedisValueType.STRING);
        String fullKey = buildKey(policy, key);

        execute(() -> {
            redisTemplate.opsForValue().set(fullKey, value);
            applyTtl(fullKey, policy);
        });
    }

    public <T> Optional<T> get(CachePolicySpec policy, String key, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.STRING);
        String fullKey = buildKey(policy, key);

        return execute(() -> {
            Object value = redisTemplate.opsForValue().get(fullKey);
            if (value == null) {
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        });
    }

    // ==================================================================================
    // List Ops
    // ==================================================================================

    public <T> void pushRightAll(CachePolicySpec policy, String key, List<T> list) {
        validatePolicy(policy, RedisValueType.LIST);
        if (list == null || list.isEmpty()) {
            return;
        }
        String fullKey = buildKey(policy, key);

        execute(() -> {
            redisTemplate.opsForList().rightPushAll(fullKey, (List<Object>) list);
            applyTtl(fullKey, policy);
        });
    }

    public <T> List<T> getList(CachePolicySpec policy, String key, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.LIST);
        String fullKey = buildKey(policy, key);

        return execute(() -> {
                    List<Object> rawList = redisTemplate.opsForList().range(fullKey, 0, -1);
                    if (rawList == null || rawList.isEmpty()) {
                        return Collections.<T>emptyList();
                    }

                    return rawList.stream()
                            .map(item -> objectMapper.convertValue(item, clazz))
                            .collect(Collectors.toList());
                })
                .orElseGet(Collections::emptyList);
    }

    public <T> void removeList(CachePolicySpec policy, String key, T value) {
        validatePolicy(policy, RedisValueType.LIST);
        String fullKey = buildKey(policy, key);

        execute(() -> redisTemplate.opsForList().remove(fullKey, 1, value));
    }

    // ==================================================================================
    // ZSet Ops
    // ==================================================================================

    public <T> void zAdd(CachePolicySpec policy, String key, T value, double score) {
        validatePolicy(policy, RedisValueType.ZSET);
        String fullKey = buildKey(policy, key);

        execute(() -> {
            redisTemplate.opsForZSet().add(fullKey, value, score);
            applyTtl(fullKey, policy);
        });
    }

    public <T> Set<T> zRange(CachePolicySpec policy, String key, long start, long end, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.ZSET);
        String fullKey = buildKey(policy, key);

        return execute(() -> {
                    Set<Object> rawSet = redisTemplate.opsForZSet().range(fullKey, start, end);
                    if (rawSet == null || rawSet.isEmpty()) {
                        return Collections.<T>emptySet();
                    }

                    return rawSet.stream()
                            .map(item -> objectMapper.convertValue(item, clazz))
                            .collect(Collectors.toSet());
                })
                .orElseGet(Collections::emptySet);
    }

    // ==================================================================================
    // Hash Ops
    // ==================================================================================

    public void putHash(CachePolicySpec policy, String key, Object dto) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        execute(() -> {
            Map<String, Object> map = objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {});
            redisTemplate.opsForHash().putAll(fullKey, map);
            applyTtl(fullKey, policy);
        });
    }

    public <T> Optional<T> getHash(CachePolicySpec policy, String key, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        return execute(() -> {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(fullKey);
            if (entries.isEmpty()) {
                return null;
            }
            return objectMapper.convertValue(entries, clazz);
        });
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

        execute(() -> redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                RedisTemplate<String, Object> template = (RedisTemplate<String, Object>) operations;
                for (T dto : dtoList) {
                    String key = keyMapper.apply(dto);
                    String fullKey = buildKey(policy, key);

                    Map<String, Object> map =
                            objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {});
                    template.opsForHash().putAll(fullKey, map);

                    if (policy.ttl() != null) {
                        template.expire(fullKey, policy.ttl().toMillis(), TimeUnit.MILLISECONDS);
                    }
                }
                return null;
            }
        }));
    }

    /**
     * 다건의 DTO를 각각 별도의 Redis Key(Hash)에서 일괄 조회 (Pipelining 적용)
     *
     * @param policy CachePolicySpec
     * @param keys   조회할 Key 식별자 목록
     * @param clazz  DTO 타입
     * @param <T>    DTO 타입
     * @return 조회된 DTO 리스트 (중간에 실패하거나 없는 경우 해당 항목 제외, 전체 실패 시 Empty List)
     */
    public <T> List<T> getHashes(CachePolicySpec policy, List<String> keys, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.HASH);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        return execute(() -> {
                    List<Object> results = redisTemplate.executePipelined(new SessionCallback<Object>() {
                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                            RedisTemplate<String, Object> template = (RedisTemplate<String, Object>) operations;
                            for (String key : keys) {
                                template.opsForHash().entries(buildKey(policy, key));
                            }
                            return null;
                        }
                    });

                    // executePipelined는 항상 List를 반환
                    return results.stream()
                            .filter(obj -> obj instanceof Map && !((Map<?, ?>) obj).isEmpty())
                            .map(obj -> objectMapper.convertValue(obj, clazz))
                            .collect(Collectors.toList());
                })
                .orElseGet(Collections::emptyList);
    }

    public <T> void putHashField(CachePolicySpec policy, String key, String field, T value) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        execute(() -> {
            redisTemplate.opsForHash().put(fullKey, field, value);
            applyTtl(fullKey, policy);
        });
    }

    public <T> Optional<T> getHashField(CachePolicySpec policy, String key, String field, Class<T> clazz) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        return execute(() -> {
            Object value = redisTemplate.opsForHash().get(fullKey, field);
            if (value == null) {
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        });
    }

    public void deleteHashField(CachePolicySpec policy, String key, String field) {
        validatePolicy(policy, RedisValueType.HASH);
        String fullKey = buildKey(policy, key);

        execute(() -> redisTemplate.opsForHash().delete(fullKey, field));
    }
}
