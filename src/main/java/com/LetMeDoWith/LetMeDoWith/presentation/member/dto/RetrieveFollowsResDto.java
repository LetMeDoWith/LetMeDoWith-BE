package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.RetrieverFollowsResult;
import java.util.List;
import lombok.Builder;

@Builder
public record RetrieveFollowsResDto(List<Follow> follows) {

    public static RetrieveFollowsResDto of(RetrieverFollowsResult result) {
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
    public record Follow(String id, String nickname, String selfDescription, String profileImageUrl) {}
}
