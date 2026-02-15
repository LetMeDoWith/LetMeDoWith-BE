package com.LetMeDoWith.LetMeDoWith.application.auth.provider;

import com.LetMeDoWith.LetMeDoWith.application.auth.util.JwtUtil;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiAuthException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.TokenType;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.SignupToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignupTokenProvider {

    private final SecretKey secretKey;

    @Value("${auth.jwt.signup-duration-min}")
    private Long signupDurationMin;

    @Value("${auth.jwt.issuer}")
    private String issuer;

    @Autowired
    public SignupTokenProvider(@Value("${auth.jwt.secret}") String secret) {

        // plain secret Base64로 인코딩
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secret.getBytes());

        // SecretKey 객체 생성
        this.secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    /**
     * Signup token 생성. /token 엔드포인트의 응답으로 memberId를 응답한다.
     *
     * <p>이후 회원가입 완료 시점에 본 메서드의 JWT를 포함하여 요청하여 회원가입 요청을 인증한다.
     *
     * @param memberId 회원가입을 계속해서 진행할 member의 id.
     * @return
     */
    public SignupToken generateToken(String memberId) {
        return SignupToken.of(memberId, issuer, signupDurationMin, secretKey);
    }

    public String validateToken(final String token) {
        final Jws<Claims> claims = JwtUtil.parseTokenToJws(token, secretKey);

        if (claims.getBody().get("sub").equals(TokenType.SIGNUP.getCode())
                && claims.getBody().get("iss").equals(this.issuer)) {
            return claims.getBody().get("memberId").toString();
        } else {
            throw new RestApiAuthException(FailResponseStatus.INVALID_TOKEN);
        }
    }
}
