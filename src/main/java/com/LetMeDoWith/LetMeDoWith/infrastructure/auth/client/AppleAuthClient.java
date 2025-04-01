package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.client;

import com.LetMeDoWith.LetMeDoWith.application.auth.client.AuthClient;
import com.LetMeDoWith.LetMeDoWith.application.auth.dto.OidcPublicKeyResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Profile("!dev")
public class AppleAuthClient implements AuthClient {
    
    private final WebClient webClient;
    
    // TODO: 애플 OIDC id token 공개키 API URL 찾아서 넣을 것.
    @Override
    @Cacheable(key = "'AuthPublicKey-Apple'")
    public Mono<OidcPublicKeyResDto> getPublicKeyList() {
        return webClient.get()
                        .uri("TBD")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(OidcPublicKeyResDto.class);
    }
    
}