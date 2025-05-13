package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import lombok.Builder;

@Builder
public record CreateTokenRefreshReqDto(String refreshToken) {}
