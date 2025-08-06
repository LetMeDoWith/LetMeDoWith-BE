package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import lombok.Builder;

@Builder
public record UpdateMemberInfoReqDto(String nickname, String selfDescription, String profileImageUrl) {}
