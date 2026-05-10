package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.RetrieveMyDowithResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이두윗 정보 조회 응답")
public record RetrieveMyDowithResDto(
        @Schema(description = "회원 ID") String memberId,
        @Schema(description = "닉네임") String nickname,
        @Schema(description = "상태 메세지") String selfDescription,
        @Schema(description = "프로필 이미지 URL") String profileImageUrl,
        @Schema(description = "전체 기간 성공한 두윗 갯수") Long successDowithCount) {

    public static RetrieveMyDowithResDto from(RetrieveMyDowithResult result) {
        return new RetrieveMyDowithResDto(
                result.memberId(),
                result.nickname(),
                result.selfDescription(),
                result.profileImageUrl(),
                result.successDowithCount());
    }
}
