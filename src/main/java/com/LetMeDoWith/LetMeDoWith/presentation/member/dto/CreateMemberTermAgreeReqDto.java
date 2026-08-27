package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "회원가입 시 약관 항목 별 동의 여부")
public record CreateMemberTermAgreeReqDto(
        @Schema(description = "이용약관 동의 여부 (항상 true)", example = "true") Boolean termsOfAgree,
        @Schema(description = "개인정보 활용 동의 여부 (항상 true)", example = "true") Boolean privacy,
        @Schema(description = "광고성 정보 동의 여부", example = "false") Boolean advertisement) {}
