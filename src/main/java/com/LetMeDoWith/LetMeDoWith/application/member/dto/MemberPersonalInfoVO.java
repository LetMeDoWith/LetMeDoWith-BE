package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MemberPersonalInfoVO(
        String nickname,
        String selfDescription,
        Gender gender,
        LocalDate dateOfBirth,
        String profileImageUrl) {}
