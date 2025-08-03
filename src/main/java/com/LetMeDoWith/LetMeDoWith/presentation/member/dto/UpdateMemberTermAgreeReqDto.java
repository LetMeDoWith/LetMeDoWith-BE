package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "약관 동의 정보 업데이트 요청")
public record UpdateMemberTermAgreeReqDto(
        @Schema(description = "약관 동의 여부") boolean isTermsAgree,
        @Schema(description = "개인정보 활용 동의 여부") boolean isPrivacyAgree,
        @Schema(description = "광고성 메세지 수신 동의 여부") boolean isAdvertisementAgree) {

    public static UpdateMemberTermAgreeReqDto of(
            boolean isTermsAgree, boolean isPrivacyAgree, boolean isAdvertisementAgree) {
        return new UpdateMemberTermAgreeReqDto(isTermsAgree, isPrivacyAgree, isAdvertisementAgree);
    }
}
