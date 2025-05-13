package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record RetrieveFollowsResDto(List<Follow> follows) {

    @Builder
    public record Follow(
            String id, String nickname, String selfDescription, String profileImageUrl) {}
}
