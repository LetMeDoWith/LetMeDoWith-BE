package com.LetMeDoWith.LetMeDoWith.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.cache.CacheName;
import com.LetMeDoWith.LetMeDoWith.common.code.TestRepository;
import lombok.extern.slf4j.Slf4j;
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
    private TestRepository testRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void objectTypeCacheSuccessTest() throws InterruptedException {

        // given

        // when
        TestRepository.TestDto result1 = testRepository.testObject();
        log.debug(result1.toString());
        Thread.sleep(2000);
        TestRepository.TestDto result2 = testRepository.testObject();
        log.debug(result1.toString());

        // then
        assertThat(result2.val1()).isEqualTo(result1.val1());
        assertThat(result2.val2()).isEqualTo(result1.val2());
    }

    @Test
    void monoTypeNonBlockCacheSuccessTest() throws InterruptedException {

        // given

        // when
        testRepository.testMono().subscribe(body -> log.debug(body.toString()));
        Thread.sleep(2000);
        testRepository.testMono().subscribe(body -> log.debug(body.toString()));

        // then
        // Assertions.assertThat(result2.val1()).isEqualTo(result1.val1());
        // Assertions.assertThat(result2.val2()).isEqualTo(result1.val2());

    }

    @Test
    void monoTypeBlockCacheSuccessTest() throws InterruptedException {

        // given

        // when
        TestRepository.TestResponseDto result1 = testRepository.testMono().block();
        Thread.sleep(2000);
        TestRepository.TestResponseDto result2 = testRepository.testMono().block();

        // then
        assertThat(result2.toString()).isEqualTo(result1.toString());
    }

    @Test
    void cacheManagerTest() throws InterruptedException {

        TestRepository.TestDto result = testRepository.testObject();
        Cache cache = cacheManager.getCache(CacheName.GOOGLE_PUBLIC_KEY);
        TestRepository.TestDto testDto = cache.get("publicKey-String", TestRepository.TestDto.class);

        assertThat(testDto.val1()).isEqualTo(result.val1());
        assertThat(testDto.val2()).isEqualTo(result.val2());
    }
}
