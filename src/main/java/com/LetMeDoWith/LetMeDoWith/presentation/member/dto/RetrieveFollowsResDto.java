package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.RetrieveFollowsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "팔로우 목록 조회 응답")
@Builder
public record RetrieveFollowsResDto(@Schema(description = "팔로우 중인 회원 목록") List<Follow> follows) {

    public static RetrieveFollowsResDto of(RetrieveFollowsResult result) {
        List<Follow> follows = result.follows().stream()
                .map(e -> Follow.builder()
                        .id(e.id())
                        .nickname(e.nickname())
                        .selfDescription(e.selfDescription())
                        .profileImageUrl(e.profileImageUrl())
                        .build())
                .toList();
        return new RetrieveFollowsResDto(follows);
    }

    @Schema(description = "팔로우 회원 정보")
    @Builder
    public record Follow(
            @Schema(description = "회원 ID (TSID)", example = "01234567890123456789012345") String id,
            @Schema(description = "닉네임", example = "두윗러") String nickname,
            @Schema(description = "자기소개") String selfDescription,
            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png") String profileImageUrl) {}
}
