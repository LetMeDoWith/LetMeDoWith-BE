package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateRefreshTokenResult;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateRefreshTokenResDto(
        @Schema(description = "member ID") String memberId,
        @Schema(description = "엑세스 토큰") AccessTokenDto accessToken,
        @Schema(description = "리프레쉬 토큰") RefreshTokenDto refreshToken) {

    public static CreateRefreshTokenResDto of(CreateRefreshTokenResult result) {
        return CreateRefreshTokenResDto.builder()
                .memberId(result.memberId())
                .accessToken(AccessTokenDto.from(result.accessToken()))
                .refreshToken(RefreshTokenDto.from(result.refreshToken()))
                .build();
    }

    @Builder
    public record AccessTokenDto(String token, LocalDateTime expireAt) {
        public static AccessTokenDto from(AccessToken accessToken) {
            return new AccessTokenDto(accessToken.getToken(), accessToken.getExpireAt());
        }
    }

    @Builder
    public record RefreshTokenDto(String token, LocalDateTime expireAt) {
        public static RefreshTokenDto from(RefreshToken refreshToken) {
            return new RefreshTokenDto(refreshToken.getToken(), refreshToken.calculateExpireAt());
        }
    }
}
