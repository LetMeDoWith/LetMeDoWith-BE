package com.LetMeDoWith.LetMeDoWith.common.code;

import com.LetMeDoWith.LetMeDoWith.common.cache.CacheName;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private static final String testUrl = "https://jsonplaceholder.typicode.com/todos/1";
    private final WebClient webClient;
    private final Map<String, Object> store = new HashMap<>();

    public String str1 = "value1";
    public String str2 = "value2";
    public int num1 = 100;

    @Cacheable(cacheNames = CacheName.GOOGLE_PUBLIC_KEY, key = "'publicKey1'")
    public TestDto cacheObject() {
        log.debug(">>>Test Method executed");
        TestDto testDto = TestDto.builder()
                .str1(this.str1)
                .str2(this.str2)
                .num1(this.num1)
                .build();
        store.put("testData", testDto);
        return testDto;
    }

    @Cacheable(cacheNames = CacheName.GOOGLE_PUBLIC_KEY, key = "'publicKey2'")
    public Mono<TestResponseDto> cacheMonoObject() {
        log.debug(">>>TestMono Method executed");
        return webClient
                .get()
                .uri(testUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
                        .bodyToMono(String.class)
                        .map(body -> new Exception()))
                .bodyToMono(TestResponseDto.class);
    }

    @Builder
    public record TestDto(String str1, String str2, int num1) {}

    public record TestResponseDto(Long userId, Long id, String title, Boolean completed) {}
}
