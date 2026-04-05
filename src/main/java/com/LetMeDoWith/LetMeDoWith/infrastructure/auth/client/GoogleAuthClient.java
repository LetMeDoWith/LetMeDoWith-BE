package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.client;

import com.LetMeDoWith.LetMeDoWith.application.auth.client.AuthClient;
import com.LetMeDoWith.LetMeDoWith.application.auth.dto.OidcPublicKeyResDto;
import com.LetMeDoWith.LetMeDoWith.common.redis.CacheName;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
//@Profile("!dev")
@CacheConfig(cacheNames = CacheName.GOOGLE_PUBLIC_KEY)
public class GoogleAuthClient implements AuthClient {

    private final WebClient webClient;

    @Override
    @Cacheable(key = "'AuthPublicKey-Google'")
    public Mono<OidcPublicKeyResDto> getPublicKeyList() {
        return webClient
                .get()
                .uri("https://www.googleapis.com/oauth2/v3/certs")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OidcPublicKeyResDto.class);
    }
}
