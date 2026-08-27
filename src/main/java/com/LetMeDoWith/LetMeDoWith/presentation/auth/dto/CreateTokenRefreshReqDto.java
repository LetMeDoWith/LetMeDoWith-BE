package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Access Token 재발급 요청")
@Builder
public record CreateTokenRefreshReqDto(
        @Schema(description = "기존 Refresh Token (JWT)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                String refreshToken) {}
