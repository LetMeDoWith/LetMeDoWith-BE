package com.LetMeDoWith.LetMeDoWith.dto.requestDto;

import lombok.Builder;

@Builder
public record CreateTokenRefreshReqDto(String accessToken, String refreshToken) {
}
