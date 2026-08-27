package com.LetMeDoWith.LetMeDoWith.domain.member.repository.dto;

public record MemberDowithQueryDto(
        String memberId, String nickname, String selfDescription, String profileImageUrl, Long successDowithCount) {}
