package com.LetMeDoWith.LetMeDoWith.common.auth.client;

import com.LetMeDoWith.LetMeDoWith.common.auth.dto.OidcPublicKeyResDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiAuthException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import reactor.core.publisher.Mono;

public interface AuthClient {

    // TODO: Public key 캐싱 주기에 대한 설정 필요
    Mono<OidcPublicKeyResDto> getPublicKeyList();

    default void invalidateCache() {
        throw new RestApiAuthException(FailResponseStatus.OIDC_ID_TOKEN_PUBKEY_NOT_FOUND);
    }
}
