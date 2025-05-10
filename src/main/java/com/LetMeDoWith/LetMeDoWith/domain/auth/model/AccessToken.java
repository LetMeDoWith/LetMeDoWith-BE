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
public class AccessToken {

    private String token;
    private LocalDateTime expireAt;

    public static AccessToken of(
            Long memberId, String issuer, Long atkDurationMin, SecretKey secretKey) {
        Date nowDate = new Date();

        long accessExpireTime = atkDurationMin * 60 * 1000L;
        Date expireAt = new Date(nowDate.getTime() + accessExpireTime);

        String accessToken =
                Jwts.builder()
                        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                        .setIssuer(issuer)
                        .setIssuedAt(nowDate)
                        .setExpiration(expireAt)
                        .setSubject("ATK")
                        .claim("memberId", memberId)
                        .signWith(secretKey)
                        .compact();

        return AccessToken.builder()
                .token(accessToken)
                .expireAt(LocalDateTime.ofInstant(expireAt.toInstant(), ZoneId.systemDefault()))
                .build();
    }
}
