package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record RetrieverFollowsResult(Long totalCount, List<Follow> follows) {
    @Builder
    public record Follow(String id, String nickname, String selfDescription, String profileImageUrl) {}
}
