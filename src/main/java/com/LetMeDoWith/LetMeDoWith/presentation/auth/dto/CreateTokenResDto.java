package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.AuthTokenVO;
import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateTokenResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "토큰 발급 요청의 결과")
public record CreateTokenResDto(
        @Schema(description = "API 요청에 사용되는 Token", implementation = AuthTokenVO.class) AuthTokenVO atk,
        @Schema(description = "ATK 재발급에 사용되는 Token", implementation = AuthTokenVO.class)
                AuthTokenVO rtk,
        @Schema(description = "회원가입 검증에 사용되는 Token", implementation = AuthTokenVO.class)
                AuthTokenVO signupToken,
        @Schema(description = "회원의 Id", nullable = true) String memberId) {

    public static CreateTokenResDto fromCreateTokenResult(CreateTokenResult result) {
        return CreateTokenResDto.builder()
                .atk(result.atk())
                .rtk(result.rtk())
                .signupToken(result.signupToken())
                .memberId(result.memberId())
                .build();
    }
}
