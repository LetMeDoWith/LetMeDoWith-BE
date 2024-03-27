package com.LetMeDoWith.LetMeDoWith.dto.responseDto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CreateTokenRefreshResDto(String accessToken, LocalDateTime accessTokenExpireAt, String refreshToken) {
}
