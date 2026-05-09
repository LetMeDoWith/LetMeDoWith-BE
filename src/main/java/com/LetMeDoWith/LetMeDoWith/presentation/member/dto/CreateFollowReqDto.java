package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "팔로우 등록 요청")
@Builder
public record CreateFollowReqDto(
        @Schema(description = "팔로우할 대상 회원 ID (TSID)", example = "01J0C7R5Z5X1Y") String followMemberId) {
}
