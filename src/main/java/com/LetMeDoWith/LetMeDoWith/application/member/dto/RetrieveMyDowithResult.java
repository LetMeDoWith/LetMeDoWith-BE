package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import com.LetMeDoWith.LetMeDoWith.domain.member.repository.dto.MemberDowithQueryDto;

public record RetrieveMyDowithResult(
        String memberId, String nickname, String selfDescription, String profileImageUrl, Long successDowithCount) {

    public static RetrieveMyDowithResult from(MemberDowithQueryDto queryDto) {
        return new RetrieveMyDowithResult(
                queryDto.memberId(),
                queryDto.nickname(),
                queryDto.selfDescription(),
                queryDto.profileImageUrl(),
                queryDto.successDowithCount());
    }
}
