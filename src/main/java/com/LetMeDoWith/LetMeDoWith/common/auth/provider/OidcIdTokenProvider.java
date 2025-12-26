package com.LetMeDoWith.LetMeDoWith.common.auth.provider;

import com.LetMeDoWith.LetMeDoWith.common.auth.client.AuthClient;
import com.LetMeDoWith.LetMeDoWith.common.auth.dto.OidcPublicKeyResDto;
import com.LetMeDoWith.LetMeDoWith.common.auth.dto.OidcPublicKeyResDto.OidcPublicKeyVO;
import com.LetMeDoWith.LetMeDoWith.common.auth.factory.SocialProviderAuthFactory;
import com.LetMeDoWith.LetMeDoWith.common.auth.util.EncryptUtil;
import com.LetMeDoWith.LetMeDoWith.common.auth.util.JwtUtil;
import com.LetMeDoWith.LetMeDoWith.common.exception.OidcIdTokenPublicKeyNotFoundException;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiAuthException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** OpenID Connect를 통한 인증 절차와 관련된 Provider */
@Component
@Slf4j
@RequiredArgsConstructor
public class OidcIdTokenProvider {

    private final SocialProviderAuthFactory socialProviderAuthFactory;
    private final AccessTokenProvider accessTokenProvider;
    private final String KID = "kid";

    @Value("${auth.oidc.aud.kakao}")
    private String KAKAO_AUD;

    @Value("${auth.oidc.aud.google}")
    private String GOOGLE_AUD;

    @Value("${auth.oidc.aud.apple}")
    private String APPLE_AUD;

    /**
     * Signature 검증이 완료된 ID Token을 얻는다.
     *
     * @param token 검증하려는 인코딩된 ID token
     * @param provider 자격증명 제공자
     * @return 서명을 검증 완료한 ID Token.
     */
    public Jws<Claims> getVerifiedOidcIdToken(SocialProvider provider, String token) {
        AuthClient client = socialProviderAuthFactory.getClient(provider);
        OidcPublicKeyResDto publicKeyList = client.getPublicKeyList().block();
        String aud = getAudValueForProvider(provider);
        String kid = getKidFromUnsignedTokenHeader(token, aud, provider.getIssUrl());

        try {

            OidcPublicKeyVO keyVO = publicKeyList.keys().stream()
                    .filter(key -> key.kid().equals(kid))
                    .findFirst()
                    .orElseThrow(OidcIdTokenPublicKeyNotFoundException::new);

            Key publicKey = EncryptUtil.getRSAPublicKey(keyVO.n(), keyVO.e());

            return JwtUtil.parseTokenToJws(token, publicKey);

        } catch (OidcIdTokenPublicKeyNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("일치하는 OIDC ID Token 공개키가 없습니다. API 응답 Cache를 갱신합니다.");
            // TODO: add method invalidates cache for public key.
            // client.invalidateCache()

            // Cache를 무효화 한 후, 공개키를 다시 조회한다.
            try {
                publicKeyList = client.getPublicKeyList().block();

                OidcPublicKeyVO keyVO = publicKeyList.keys().stream()
                        .filter(key -> key.kid().equals(kid))
                        .findFirst()
                        .orElseThrow(OidcIdTokenPublicKeyNotFoundException::new);

                Key publicKey = EncryptUtil.getRSAPublicKey(keyVO.n(), keyVO.e());

                return JwtUtil.parseTokenToJws(token, publicKey);
            } catch (OidcIdTokenPublicKeyNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                log.error("OIDC ID Token 공개키 갱신 실패. {} 공개키 서버의 문제일 수 있습니다.", provider.getCode());
                throw new RestApiAuthException(FailResponseStatus.OIDC_ID_TOKEN_PUBKEY_NOT_FOUND);
            }
        }
    }

    /**
     * Unsigned token에서 kid를 가져온다,
     *
     * @param token Unsigned ID token
     * @param aud 자격증명 제공자에서 발급한 어플리케이션 아이디
     * @param iss 자걱증명 제공자의 url
     * @return ID token의 kid 값
     */
    public String getKidFromUnsignedTokenHeader(String token, String aud, String iss) {
        return (String) getUnsignedTokenClaims(token, aud, iss).getHeader().get(KID);
    }

    /**
     * OIDC ID Token의 iss, aud, 만료일자를 검증하고, parse한다.
     *
     * @param token ID Token. Signature가 제거되있어야 한다.
     * @param aud 자격증명 제공자에서 발급한 어플리케이션 아이디
     * @param iss 자걱증명 제공자의 url
     * @return parsing된 ID Token.
     */
    private Jwt<Header, Claims> getUnsignedTokenClaims(String token, String aud, String iss) {
        try {
            return Jwts.parserBuilder()
                    .requireAudience(aud) // aud(provider에 지정한 어플리케이션 아이디) 가 같은지 확인
                    .requireIssuer(iss) // iss(issuer)가 알맞은 provider인지 확인
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token));
        } catch (ExpiredJwtException e) { // 파싱하면서 만료된 토큰인지 확인.
            throw new RestApiAuthException(FailResponseStatus.TOKEN_EXPIRED, e);
        } catch (SignatureException e) {
            throw new RestApiAuthException(FailResponseStatus.INVALID_TOKEN, e);
        } catch (Exception e) {
            log.error(e.toString());
            throw new RestApiAuthException(FailResponseStatus.INVALID_TOKEN, e);
        }
    }

    /**
     * Encoding된 Signed-token 에서 signature를 분리한다.
     *
     * @param signedToken signature를 포함하는 token 전문
     * @return signature가 제거된 token
     */
    private String getUnsignedToken(String signedToken) {
        String[] splitToken = signedToken.split("\\.");

        if (splitToken.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 Token입니다!");
        }

        return splitToken[0] + '.' + splitToken[1] + '.';
    }

    private String getAudValueForProvider(SocialProvider provider) {
        switch (provider) {
            case APPLE:
                return APPLE_AUD;
            case GOOGLE:
                return GOOGLE_AUD;
            case KAKAO:
            case DEV_KAKAO:
                return KAKAO_AUD;

            default:
                throw new RuntimeException("잘못된 Provider 정보입니다.");
        }
    }
}
