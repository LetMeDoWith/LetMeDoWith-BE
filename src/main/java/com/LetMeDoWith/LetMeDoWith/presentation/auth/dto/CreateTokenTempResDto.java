package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개발 환경 임시 토큰 발급 응답")
public record CreateTokenTempResDto(
        @Schema(description = "발급된 Access Token (JWT)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                String accessToken) {}
