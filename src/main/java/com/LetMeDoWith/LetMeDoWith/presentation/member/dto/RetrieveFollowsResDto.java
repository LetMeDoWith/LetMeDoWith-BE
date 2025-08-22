package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.RetrieveFollowsResult;
import lombok.Builder;

import java.util.List;

@Builder
public record RetrieveFollowsResDto(List<Follow> follows) {

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

    @Builder
    public record Follow(String id, String nickname, String selfDescription, String profileImageUrl) {
    }
}
