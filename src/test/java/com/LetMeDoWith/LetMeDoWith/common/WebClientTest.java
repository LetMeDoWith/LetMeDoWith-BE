package com.LetMeDoWith.LetMeDoWith.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class WebClientTest {

    @Autowired private WebClient webClient;

    private String testUrl = "https://jsonplaceholder.typicode.com/todos/1";

    @Test
    @DisplayName("Blocking/Sync Client Request 정상")
    void blockingSyncTest() {
        TestResponseDto responseBody =
                webClient
                        .get()
                        .uri(testUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(
                                HttpStatusCode::isError,
                                clientResponse ->
                                        clientResponse.bodyToMono(String.class).map(body -> new Exception()))
                        .bodyToMono(TestResponseDto.class)
                        .block();

        System.out.println("요청 완료 후 결과값 반환 Point");

        Assertions.assertThat(responseBody.userId).isEqualTo(1);
        Assertions.assertThat(responseBody.id).isEqualTo(1);
    }

    @Test
    @DisplayName("Non-Blocking/Async Client Request 정상")
    void nonBlockingAsyncTest() throws InterruptedException {

        webClient
                .get()
                .uri(testUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class).map(body -> new Exception()))
                .bodyToMono(TestResponseDto.class)
                .subscribe(
                        body -> {
                            System.out.println(body.toString());
                            Assertions.assertThat(body.userId).isEqualTo(1);
                            Assertions.assertThat(body.id).isEqualTo(1);
                        });

        System.out.println("요청 완료 후 결과값 반환 Point");

        Thread.sleep(3000);
    }

    private record TestResponseDto(Long userId, Long id, String title, Boolean completed) {}
}
