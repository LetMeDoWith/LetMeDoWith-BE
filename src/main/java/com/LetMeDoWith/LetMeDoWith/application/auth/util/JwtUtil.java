package com.LetMeDoWith.LetMeDoWith.application.auth.util;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiAuthException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JwtUtil {

    /**
     * String 형태의 JWT를 서명을 포함한 JWS 형태로 parsing 한다. parsing 과정에서 서명을 검증한다.
     *
     * @param token String 형태의 난독화된 JWT
     * @param key JWT를 서명한 키
     * @return pasring된 토큰
     */
    public Jws<Claims> parseTokenToJws(final String token, final Key key) {

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (SignatureException e) {
            throw new RestApiAuthException(FailResponseStatus.INVALID_TOKEN, e);
        } catch (ExpiredJwtException e) {
            throw new RestApiAuthException(FailResponseStatus.TOKEN_EXPIRED, e);
        } catch (Exception e) {
            log.error(e.toString());
            throw new RestApiAuthException(FailResponseStatus.INVALID_TOKEN, e);
        }
    }
}
