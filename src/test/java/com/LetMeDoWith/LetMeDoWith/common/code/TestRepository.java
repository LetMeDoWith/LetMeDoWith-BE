package com.LetMeDoWith.LetMeDoWith.common.code;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
@CacheConfig(cacheNames = "APPLE", cacheManager = "socialProviderPublicKeyCacheManager")
public class TestRepository {

    private final WebClient webClient;

    private static String testUrl = "https://jsonplaceholder.typicode.com/todos/1";

    private Map<String, Object> store = new HashMap<>();

    @Cacheable(key = "'publicKey-String'")
    public TestDto testObject() {
        log.debug(">>>Test Method executed");
        TestDto testDto = TestDto.builder().val1("value1").val2("value2").build();
        store.put("testData", testDto);
        return testDto;
    }

    @Cacheable(key = "'publicKey-Mono'")
    public Mono<TestResponseDto> testMono() {
        log.debug(">>>TestMono Method executed");
        return webClient
                .get()
                .uri(testUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class).map(body -> new Exception()))
                .bodyToMono(TestResponseDto.class);
    }

    @Builder
    public static record TestDto(String val1, String val2) {}

    public record TestResponseDto(Long userId, Long id, String title, Boolean completed) {}
}
