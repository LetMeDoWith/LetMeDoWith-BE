package com.LetMeDoWith.LetMeDoWith.infrastructure.redis;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.common.redis.CachePolicy;
import com.LetMeDoWith.LetMeDoWith.common.redis.RedisPolicySpec;
import com.LetMeDoWith.LetMeDoWith.common.redis.RedisValueType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

@ExtendWith(MockitoExtension.class)
class RedisOperatorTest {

    @InjectMocks
    private RedisOperator redisOperator;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    // 테스트용 CachePolicy Enum 정의 (실제 Enum을 mocking하거나 확장하기 어려우므로 실제 Enum 사용)
    // 단, 테스트를 위해 실제 CachePolicy의 값 중 적절한 것을 골라 사용하거나,
    // 만약 테스트 전용 Policy가 필요하다면 실제 코드에 추가해야 함.
    // 여기서는 기존 CachePolicy에 정의된 것들을 활용하여 테스트 진행.

    // 가정:
    // APPLE_PUBLIC_KEY -> STRING, TTL 7 days
    // DOWITH_TASK -> HASH, TTL 14 days
    // LAZY_DOWITH_TASK_MEMBERS -> LIST, TTL null

    @Test
    @DisplayName("Value Ops - Set 성공")
    void testSet_Success() {
        // Given
        CachePolicy policy = CachePolicy.APPLE_PUBLIC_KEY; // STRING type
        String key = "testKey";
        String value = "testValue";
        String expectedFullKey = policy.keyName() + "::" + key;

        // When
        redisOperator.set(policy, key, value);

        // Then
        verify(valueOperations).set(expectedFullKey, value);
        verify(redisTemplate).expire(eq(expectedFullKey), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("Value Ops - Get 성공")
    void testGet_Success() {
        // Given
        CachePolicy policy = CachePolicy.APPLE_PUBLIC_KEY;
        String key = "testKey";
        String expectedFullKey = policy.keyName() + "::" + key;
        String expectedValue = "testValue";

        when(valueOperations.get(expectedFullKey)).thenReturn(expectedValue);

        // When
        Optional<String> result = redisOperator.get(policy, key, String.class);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedValue, result.get());
    }

    @Test
    @DisplayName("Policy Type Mismatch 예외 발생")
    void testValidatePolicy_Fail() {
        // Given
        CachePolicy policy = CachePolicy.DOWITH_TASK; // HASH type

        // When & Then
        // String 연산인 set을 HASH 타입 정책으로 호출하면 예외 발생해야 함
        assertThrows(IllegalArgumentException.class, () -> {
            redisOperator.set(policy, "key", "value");
        });
    }

    @Test
    @DisplayName("List Ops - PushRightAll 성공")
    void testPushRightAll_Success() {
        // Given
        CachePolicy policy = CachePolicy.DOWITH_TASK_IDS; // LIST type
        String key = "listKey";
        List<Object> list = Arrays.asList("item1", "item2");
        String expectedFullKey = policy.keyName() + "::" + key;

        // When
        redisOperator.pushRightAll(policy, key, list);

        // Then
        verify(listOperations).rightPushAll(expectedFullKey, list);
        // TTL이 null인 정책이므로 expire 호출되지 않아야 함 (CachePolicy 확인 필요)
        if (policy.ttl() != null) {
            verify(redisTemplate).expire(eq(expectedFullKey), anyLong(), eq(TimeUnit.MILLISECONDS));
        } else {
            verify(redisTemplate, never()).expire(anyString(), anyLong(), any());
        }
    }

    @Test
    @DisplayName("Hash Ops - PutHash (Single DTO) 성공")
    void testPutHash_Success() {
        // Given
        CachePolicy policy = CachePolicy.DOWITH_TASK; // HASH type
        String key = "hashKey";
        String expectedFullKey = policy.keyName() + "::" + key;
        TestDto dto = new TestDto("name", 123);

        // When
        redisOperator.putHash(policy, key, dto);

        // Then
        verify(hashOperations).putAll(eq(expectedFullKey), anyMap());
        verify(redisTemplate).expire(eq(expectedFullKey), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("Hash Ops - PutHashes (Pipeline) 성공")
    void testPutHashes_Pipeline_Success() {
        // Given
        CachePolicy policy = CachePolicy.DOWITH_TASK;
        List<TestDto> dtoList = Arrays.asList(new TestDto("user1", 10), new TestDto("user2", 20));
        Function<TestDto, String> keyMapper = TestDto::getName;

        // When
        redisOperator.putHashes(policy, dtoList, keyMapper);

        // Then
        verify(redisTemplate).executePipelined(any(SessionCallback.class));
    }

    @Test
    @DisplayName("Hash Ops - GetHashes (Pipeline) 성공")
    void testGetHashes_Pipeline_Success() {
        // Given
        CachePolicy policy = CachePolicy.DOWITH_TASK;
        List<String> keys = Arrays.asList("user1", "user2");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "user1");
        map1.put("age", 10);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "user2");
        map2.put("age", 20);

        List<Object> pipelineResults = Arrays.asList(map1, map2);

        when(redisTemplate.executePipelined(any(SessionCallback.class))).thenReturn(
            pipelineResults);

        // When
        List<TestDto> result = redisOperator.getHashes(policy, keys, TestDto.class);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getName());
        assertEquals("user2", result.get(1).getName());
        verify(redisTemplate).executePipelined(any(SessionCallback.class));
    }

    @Test
    @DisplayName("Hash Ops - PutHashField 성공")
    void testPutHashField_Success() {
        // Given
        CachePolicy policy = CachePolicy.DOWITH_TASK;
        String key = "hashKey";
        String field = "fieldName";
        String value = "fieldValue";
        String expectedFullKey = policy.keyName() + "::" + key;

        // When
        redisOperator.putHashField(policy, key, field, value);

        // Then
        verify(hashOperations).put(expectedFullKey, field, value);
        verify(redisTemplate).expire(eq(expectedFullKey), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("ZSet Ops - ZAdd 성공")
    void testZAdd_Success() {
        // Given
        RedisPolicySpec policy = new RedisPolicySpec() {
            @Override
            public String keyName() {
                return "test:zset";
            }

            @Override
            public RedisValueType redisValueType() {
                return RedisValueType.ZSET;
            }

            @Override
            public Duration ttl() {
                return Duration.ofMinutes(10);
            }

            @Override
            public String name() {
                return "TEST_ZSET";
            }
        };

        String key = "zsetKey";
        String value = "zsetValue";
        double score = 1.0;
        String expectedFullKey = policy.keyName() + "::" + key;

        // When
        redisOperator.zAdd(policy, key, value, score);

        // Then
        verify(zSetOperations).add(expectedFullKey, value, score);
        verify(redisTemplate).expire(eq(expectedFullKey), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("ZSet Ops - ZRange 성공")
    void testZRange_Success() {
        // Given
        RedisPolicySpec policy = new RedisPolicySpec() {
            @Override
            public String keyName() {
                return "test:zset";
            }

            @Override
            public RedisValueType redisValueType() {
                return RedisValueType.ZSET;
            }

            @Override
            public Duration ttl() {
                return Duration.ofMinutes(10);
            }

            @Override
            public String name() {
                return "TEST_ZSET";
            }
        };

        String key = "zsetKey";
        long start = 0;
        long end = -1;
        String expectedFullKey = policy.keyName() + "::" + key;
        Set<Object> expectedSet = new HashSet<>(Arrays.asList("value1", "value2"));

        when(zSetOperations.range(expectedFullKey, start, end)).thenReturn(expectedSet);

        // When
        Set<String> result = redisOperator.zRange(policy, key, start, end, String.class);

        // Then
        assertEquals(expectedSet.size(), result.size());
        assertTrue(result.contains("value1"));
        assertTrue(result.contains("value2"));
    }

    @Test
    @DisplayName("execute(Supplier) - 성공 케이스")
    void testExecuteSupplier_Success() {
        // Given
        String expectedValue = "success";

        // When
        Optional<String> result = redisOperator.execute(() -> expectedValue);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedValue, result.get());
    }

    @Test
    @DisplayName("execute(Supplier) - 예외 발생 시 Optional.empty() 반환")
    void testExecuteSupplier_Exception() {
        // Given
        // When
        Optional<String> result = redisOperator.execute(() -> {
            throw new QueryTimeoutException("Redis Error");
        });

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("execute(Runnable) - 성공 케이스")
    void testExecuteRunnable_Success() {
        // Given
        // When & Then
        assertDoesNotThrow(() -> redisOperator.execute(() -> {
            // do nothing
        }));
    }

    @Test
    @DisplayName("execute(Runnable) - 예외 발생 시 예외 무시")
    void testExecuteRunnable_Exception() {
        // Given
        // When & Then
        assertDoesNotThrow(() -> redisOperator.execute((Runnable) () -> {
            throw new QueryTimeoutException("Redis Error");
        }));
    }

    // 테스트용 DTO
    static class TestDto {

        private String name;
        private int age;

        public TestDto() {
        }

        public TestDto(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}