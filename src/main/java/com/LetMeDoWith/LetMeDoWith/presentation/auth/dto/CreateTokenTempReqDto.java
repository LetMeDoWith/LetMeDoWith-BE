package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개발 환경 임시 토큰 발급 요청")
public record CreateTokenTempReqDto(
        @Schema(description = "개발용 계정 ID (서버에 설정된 값)", example = "dev-admin-id") String id,
        @Schema(description = "개발용 계정 비밀번호 (서버에 설정된 값)", example = "dev-admin-password") String password,
        @Schema(description = "토큰을 발급받을 회원 ID (TSID)", example = "01234567890123456789012345") String memberId) {}
