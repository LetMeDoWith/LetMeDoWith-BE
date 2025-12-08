package com.LetMeDoWith.LetMeDoWith.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.cache.CacheHelper;
import com.LetMeDoWith.LetMeDoWith.common.cache.CacheName;
import com.LetMeDoWith.LetMeDoWith.common.code.TestService;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheTest {

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

    @PostConstruct
    void init() {
        this.str1 = this.testService.str1;
        this.str2 = this.testService.str2;
        this.num1 = this.testService.num1;
    }

    @Test
    void objectTypeCacheSuccessTest() throws InterruptedException {

        // given

        // when
        //        TestService.TestDto result1 = testService.cacheObject();
        //        log.debug(result1.toString());
        //        Thread.sleep(2000);
        //        TestService.TestDto result2 = testService.cacheObject();
        //        log.debug(result1.toString());
        //
        //        // then
        //        assertThat(result2.val1()).isEqualTo(result1.val1());
        //        assertThat(result2.val2()).isEqualTo(result1.val2());
    }

    @Test
    void monoTypeNonBlockCacheSuccessTest() throws InterruptedException {

        // given

        // when
        testService.cacheMonoObject().subscribe(body -> log.debug(body.toString()));
        Thread.sleep(2000);
        testService.cacheMonoObject().subscribe(body -> log.debug(body.toString()));

        // then
        // Assertions.assertThat(result2.val1()).isEqualTo(result1.val1());
        // Assertions.assertThat(result2.val2()).isEqualTo(result1.val2());

    }

    @Test
    void monoTypeBlockCacheSuccessTest() throws InterruptedException {

        // given

        // when
        TestService.TestResponseDto result1 = testService.cacheMonoObject().block();
        Thread.sleep(2000);
        TestService.TestResponseDto result2 = testService.cacheMonoObject().block();

        // then
        assertThat(result2.toString()).isEqualTo(result1.toString());
    }

    @Test
    void cacheManagerTest() throws InterruptedException {

        TestService.TestDto result = testService.cacheObject();
        Cache cache = cacheManager.getCache(CacheName.GOOGLE_PUBLIC_KEY);
        Object object = cache.get("publicKey-String", Object.class);

        //        assertThat(object.val1()).isEqualTo(result.val1());
        //        assertThat(object.val2()).isEqualTo(result.val2());
    }

    @DisplayName("Spring Cache Cacheable을 통해서 데이터 삽입 후 CacheHelper를 통해 조회")
    @Test
    void test1() {
        // given
        testService.cacheObject();
        // when
        TestService.TestDto cachedData =
                cacheHelper.get(CacheName.GOOGLE_PUBLIC_KEY, "publicKey1", TestService.TestDto.class);

        // then
        assertThat(cachedData.str1()).isEqualTo(str1);
        assertThat(cachedData.str2()).isEqualTo(str2);
        assertThat(cachedData.num1()).isEqualTo(num1);
    }

    @Builder
    private record TestDto(String str1, String str2, int num1) {}

    record DifferentDTO(String val1, String val2) {}
}
