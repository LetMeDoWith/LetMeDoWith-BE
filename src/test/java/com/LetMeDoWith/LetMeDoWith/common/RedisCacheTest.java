package com.LetMeDoWith.LetMeDoWith.common;

import com.LetMeDoWith.LetMeDoWith.common.code.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RedisCacheTest {

    @Autowired
    private TestRepository testRepository;

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
        Assertions.assertThat(result2.val1()).isEqualTo(result1.val1());
        Assertions.assertThat(result2.val2()).isEqualTo(result1.val2());
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
        Assertions.assertThat(result2.toString()).isEqualTo(result1.toString());
    }
}
