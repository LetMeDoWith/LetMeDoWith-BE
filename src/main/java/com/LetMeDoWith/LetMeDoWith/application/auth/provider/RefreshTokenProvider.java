package com.LetMeDoWith.LetMeDoWith.application.auth.provider;

import com.LetMeDoWith.LetMeDoWith.application.auth.repository.RefreshTokenRepository;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenProvider {

    @Value("${auth.jwt.rtk-duration-day}")
    private Long rtkDurationDay;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 서버 Refresh Token 생성
     *
     * @param memberId
     * @param accessToken
     * @param userAgent
     * @return
     */
    public RefreshToken createRefreshToken(Long memberId, String accessToken, String userAgent) {
        // redis에 저장
        return refreshTokenRepository.save(
                RefreshToken.of(memberId, accessToken, userAgent, rtkDurationDay * 24 * 60 * 60));
    }

    public RefreshToken findRefreshToken(String token) {
        return refreshTokenRepository
                .getRefreshToken(token)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.TOKEN_EXPIRED_BY_ADMIN));
    }
}
