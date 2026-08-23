package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateRefreshTokenResult;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateRefreshTokenResDto(
        @Schema(description = "member ID", example = "01234567890123456789012345") String memberId,
        @Schema(description = "엑세스 토큰") AccessTokenDto accessToken,
        @Schema(description = "리프레쉬 토큰") RefreshTokenDto refreshToken) {

    public static CreateRefreshTokenResDto of(CreateRefreshTokenResult result) {
        return CreateRefreshTokenResDto.builder()
                .memberId(result.memberId())
                .accessToken(AccessTokenDto.from(result.accessToken()))
                .refreshToken(RefreshTokenDto.from(result.refreshToken()))
                .build();
    }

    @Schema(description = "Access Token 정보")
    @Builder
    public record AccessTokenDto(
            @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...") String token,
            @Schema(description = "만료 일시", example = "2026-01-01T00:00:00") LocalDateTime expireAt) {
        public static AccessTokenDto from(AccessToken accessToken) {
            return new AccessTokenDto(accessToken.getToken(), accessToken.getExpireAt());
        }
    }

    @Schema(description = "Refresh Token 정보")
    @Builder
    public record RefreshTokenDto(
            @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...") String token,
            @Schema(description = "만료 일시", example = "2026-01-08T00:00:00") LocalDateTime expireAt) {
        public static RefreshTokenDto from(RefreshToken refreshToken) {
            return new RefreshTokenDto(refreshToken.getToken(), refreshToken.calculateExpireAt());
        }
    }
}
