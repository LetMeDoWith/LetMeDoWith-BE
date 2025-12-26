package com.LetMeDoWith.LetMeDoWith.common.auth.provider;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.jwt.rtk-duration-day}")
    private Long rtkDurationDay;

    /** 서버 Refresh Token 생성 */
    public RefreshToken generateToken(String memberId, String accessToken, String userAgent) {
        // redis에 저장
        return refreshTokenRepository.save(
                RefreshToken.of(memberId, accessToken, userAgent, rtkDurationDay * 24 * 60 * 60));
    }

    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepository
                .getRefreshToken(token)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.TOKEN_EXPIRED_BY_ADMIN));
    }
}
