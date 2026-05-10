package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 중복 검증 요청")
public record CheckNicknameReqDto(
        @Schema(description = "검증할 닉네임", example = "두윗러123", maxLength = 40) String nickname) {}
