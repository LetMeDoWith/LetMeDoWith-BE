package com.LetMeDoWith.LetMeDoWith.common.exception;

/**
 * Open ID Connect ID Token의 공개키 목록 API 조회 결과에서 유저 ID Token의 kid와 일치하는 공개 키가 없을 때 발생한다.
 *
 * <p>주로 social provider 에서 공개키 목록을 갱신하여 서버에 캐싱된 값과 불일치할 때 발생한다.
 *
 * <p>FE에 전달되지 않고, BE에서 내부적으로 사용된다.
 */
public class OidcIdTokenPublicKeyNotFoundException extends RuntimeException {

    public OidcIdTokenPublicKeyNotFoundException() {
        super("OIDC ID Token 공개키 불일치!");
    }
}
