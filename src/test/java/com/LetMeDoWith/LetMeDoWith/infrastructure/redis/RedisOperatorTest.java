package com.LetMeDoWith.LetMeDoWith.infrastructure.redis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicy;
import com.LetMeDoWith.LetMeDoWith.common.cache.CachePolicySpec;
import com.LetMeDoWith.LetMeDoWith.common.cache.RedisValueType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.*;
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
import org.springframework.data.redis.core.*;

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
        String expectedFullKey = policy.cacheName() + "::" + key;

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
        String expectedFullKey = policy.cacheName() + "::" + key;
        String expectedValue = "testValue";

        when(valueOperations.get(expectedFullKey)).thenReturn(expectedValue);

        // When
        String result = redisOperator.get(policy, key, String.class);

        // Then
        assertEquals(expectedValue, result);
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
        CachePolicy policy = CachePolicy.LAZY_DOWITH_TASK_MEMBERS; // LIST type
        String key = "listKey";
        List<Object> list = Arrays.asList("item1", "item2");
        String expectedFullKey = policy.cacheName() + "::" + key;

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
        String expectedFullKey = policy.cacheName() + "::" + key;
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
        verify(redisTemplate).executePipelined(any(RedisCallback.class));
    }

    @Test
    @DisplayName("Hash Ops - PutHashField 성공")
    void testPutHashField_Success() {
        // Given
        CachePolicy policy = CachePolicy.DOWITH_TASK;
        String key = "hashKey";
        String field = "fieldName";
        String value = "fieldValue";
        String expectedFullKey = policy.cacheName() + "::" + key;

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
        CachePolicySpec policy = new CachePolicySpec() {
            @Override
            public String cacheName() {
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
        String expectedFullKey = policy.cacheName() + "::" + key;

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
        CachePolicySpec policy = new CachePolicySpec() {
            @Override
            public String cacheName() {
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
        String expectedFullKey = policy.cacheName() + "::" + key;
        Set<Object> expectedSet = new HashSet<>(Arrays.asList("value1", "value2"));

        when(zSetOperations.range(expectedFullKey, start, end)).thenReturn(expectedSet);

        // When
        Set<String> result = redisOperator.zRange(policy, key, start, end, String.class);

        // Then
        assertEquals(expectedSet.size(), result.size());
        assertTrue(result.contains("value1"));
        assertTrue(result.contains("value2"));
    }

    // 테스트용 DTO
    static class TestDto {
        private String name;
        private int age;

        public TestDto() {}

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
