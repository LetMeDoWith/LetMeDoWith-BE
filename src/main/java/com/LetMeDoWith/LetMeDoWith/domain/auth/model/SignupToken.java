package com.LetMeDoWith.LetMeDoWith.domain.auth.model;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class SignupToken {

    private String token;
    private LocalDateTime expireAt;

    public static SignupToken of(
            String memberId, String issuer, Long signUpTokenDurationMin, SecretKey secretKey) {
        Date nowDate = new Date();

        long accessExpireTime = signUpTokenDurationMin * 60 * 1000L;
        Date expireAt = new Date(nowDate.getTime() + accessExpireTime);

        String signupToken =
                Jwts.builder()
                        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                        .setIssuer(issuer)
                        .setIssuedAt(nowDate)
                        .setExpiration(expireAt)
                        .setSubject("SIGNUP")
                        .claim("memberId", memberId)
                        .signWith(secretKey)
                        .compact();

        return SignupToken.builder()
                .token(signupToken)
                .expireAt(LocalDateTime.ofInstant(expireAt.toInstant(), ZoneId.systemDefault()))
                .build();
    }
}
