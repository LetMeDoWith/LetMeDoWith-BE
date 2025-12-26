package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.client;

import com.LetMeDoWith.LetMeDoWith.common.auth.client.AuthClient;
import com.LetMeDoWith.LetMeDoWith.common.auth.dto.OidcPublicKeyResDto;
import com.LetMeDoWith.LetMeDoWith.common.cache.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheName.KAKAO_PUBLIC_KEY)
public class KakaoAuthClient implements AuthClient {

    private final WebClient webClient;

    @Override
    @Cacheable(key = "'AuthPublicKey-Kakao'")
    public Mono<OidcPublicKeyResDto> getPublicKeyList() {
        return webClient
                .get()
                .uri("https://kauth.kakao.com/.well-known/jwks.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OidcPublicKeyResDto.class);
    }
}
