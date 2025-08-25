package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "멤버 정보 수정 요청")
public record UpdateMemberInfoReqDto(
    @Schema(description = "닉네임") String nickname,
    @Schema(description = "자기소개") String selfDescription,
    @Schema(description = "프로필 이미지 URL") String profileImageUrl) {}
