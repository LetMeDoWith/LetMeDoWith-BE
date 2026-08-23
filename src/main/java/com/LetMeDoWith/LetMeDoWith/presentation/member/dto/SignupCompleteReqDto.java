package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
@Schema(description = "회원가입 완료 요청시 필요한 추가 개인정보")
public record SignupCompleteReqDto(
        @Schema(description = "회원의 닉네임", example = "두윗러123") String nickname,
        @Schema(description = "회원의 생년월일", example = "1995-11-04") LocalDate dateOfBirth,
        @Schema(description = "회원의 성별", example = "M") Gender gender,
        @Schema(description = "회원의 약관 항목별 동의 여부", implementation = CreateMemberTermAgreeReqDto.class)
                CreateMemberTermAgreeReqDto agreements) {}
