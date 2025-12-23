package com.LetMeDoWith.LetMeDoWith.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.cache.CacheHelper;
import com.LetMeDoWith.LetMeDoWith.common.cache.CacheName;
import com.LetMeDoWith.LetMeDoWith.common.code.TestService;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class CacheTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private TestService testService;

    @Autowired
    private RedisTemplate<?, ?> redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheHelper cacheHelper;

    private String str1;
    private String str2;
    private int num1;

    private String str3;
    private String str4;
    private int num2;

    @BeforeEach
    void init() {
        this.str1 = this.testService.getStr1();
        this.str2 = this.testService.getStr2();
        this.num1 = this.testService.getNum1();
        this.str3 = "anotherValue1";
        this.str4 = "anotherValue2";
        this.num2 = 200;
    }

    @DisplayName("Spring Cache Cacheable을 통해서 데이터 삽입 후 CacheHelper를 통해 조회")
    @Test
    void cacheRedisValueTypeString() {
        // given
        String keyString = UUID.randomUUID().toString();
        Long keyNumber = 123L;
        testService.cacheObject(keyString, keyNumber);
        String key = keyString + "::" + keyNumber;

        // when
        TestService.TestDto cachedData = cacheHelper.get(CacheName.GOOGLE_PUBLIC_KEY, key,
            TestService.TestDto.class);

        // then
        assertThat(cachedData.str1()).isEqualTo(str1);
        assertThat(cachedData.str2()).isEqualTo(str2);
        assertThat(cachedData.num1()).isEqualTo(num1);
    }

    @DisplayName("CacheHelper를 통해 Redis Value Type Hash로 삽입 후 조회")
    @Test
    void cacheRedisValueTypeHash() {
        // given
        String dowithTaskId = UUID.randomUUID().toString();
        TestDto cacheTarget = TestDto.builder().str1(str1).str2(str2).num1(num1).build();

        // when
        cacheHelper.put(CacheName.DOWITH_TASK, dowithTaskId, cacheTarget);
        String cachedStr1 = cacheHelper.get(CacheName.DOWITH_TASK, dowithTaskId, "str1",
            String.class);
        int num1 = cacheHelper.get(CacheName.DOWITH_TASK, dowithTaskId, "num1", Integer.class);

        TestDto cachedTarget = cacheHelper.get(CacheName.DOWITH_TASK, dowithTaskId, TestDto.class);

        // then
        assertThat(cachedStr1).isEqualTo(this.str1);
        assertThat(num1).isEqualTo(this.num1);
        assertThat(cachedTarget.str1()).isEqualTo(this.str1);
        assertThat(cachedTarget.str2()).isEqualTo(this.str2);
        assertThat(cachedTarget.num1()).isEqualTo(this.num1);
    }

    @DisplayName("CacheHelper를 통해 Redis Value Type List로 삽입 후 조회")
    @Test
    void cacheRedisValueTypeList() {
        // given
        String listKey = UUID.randomUUID().toString();
        TestDto cacheTarget = new TestDto(str1, str2, num1);

        // when
        cacheHelper.push(CacheName.LAZY_DOWITH_TASK_MEMBERS, listKey, cacheTarget);
        List<TestDto> cachedTarget = cacheHelper.getByRange(CacheName.LAZY_DOWITH_TASK_MEMBERS,
            listKey, 0, -1, TestDto.class);

        // then
        assertThat(cachedTarget.get(0)).isEqualTo(cacheTarget);
    }

    @DisplayName("CacheHelper를 통해 Redis Value Type List로 삽입 후 삭제")
    @Test
    void cacheRedisValueTypeListRemove() {
        // given
        String listKey = UUID.randomUUID().toString();
        TestDto cacheTarget1 = new TestDto(str1, str2, num1);
        TestDto cacheTarget2 = new TestDto(str3, str4, num2);

        // when
        cacheHelper.push(CacheName.LAZY_DOWITH_TASK_MEMBERS, listKey, cacheTarget1);
        cacheHelper.push(CacheName.LAZY_DOWITH_TASK_MEMBERS, listKey, cacheTarget2);
        cacheHelper.remove(CacheName.LAZY_DOWITH_TASK_MEMBERS, listKey, cacheTarget1);
        List<TestDto> cachedTarget = cacheHelper.getByRange(CacheName.LAZY_DOWITH_TASK_MEMBERS,
            listKey, 0, -1, TestDto.class);

        // then
        assertThat(cachedTarget.size()).isEqualTo(1);
        assertThat(cachedTarget.get(0)).isEqualTo(cacheTarget2);
    }

    @Builder
    private record TestDto(String str1, String str2, int num1) {

    }

    record DifferentDTO(String val1, String val2) {

    }
}