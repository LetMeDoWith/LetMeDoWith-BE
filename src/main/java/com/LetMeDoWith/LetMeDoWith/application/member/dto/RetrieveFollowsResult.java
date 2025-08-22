package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RetrieveFollowsResult(Long totalCount, List<Follow> follows) {
    @Builder
    public record Follow(String id, String nickname, String selfDescription, String profileImageUrl) {
    }
}
