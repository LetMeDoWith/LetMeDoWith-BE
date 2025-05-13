package com.LetMeDoWith.LetMeDoWith.application.auth.dto;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.SignupToken;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "인증 과정에서 사용되는 토큰")
public record AuthTokenVO(
        @Schema(description = "토큰 본문") String token,
        @Schema(description = "토큰 만료시기") LocalDateTime expireAt) {

    /**
     * Refresh Token을 AuthTokenVO 타입으로 변환
     *
     * @param rtk
     * @return
     */
    public static AuthTokenVO from(RefreshToken rtk) {
        return new AuthTokenVO(rtk.getToken(), LocalDateTime.now().plusSeconds(rtk.getExpireSec()));
    }

    public static AuthTokenVO from(AccessToken atk) {
        return new AuthTokenVO(atk.getToken(), atk.getExpireAt());
    }

    public static AuthTokenVO from(SignupToken signUpToken) {
        return new AuthTokenVO(signUpToken.getToken(), signUpToken.getExpireAt());
    }
}
