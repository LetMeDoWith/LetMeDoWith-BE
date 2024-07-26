package com.LetMeDoWith.LetMeDoWith.dto.command;

import com.LetMeDoWith.LetMeDoWith.enums.member.Gender;
import java.time.LocalDate;
import lombok.Builder;

/**
 * 회원가입 완료 요청 command
 *
 * @param nickname
 * @param dateOfBirth
 * @param gender
 * @param isTerms
 * @param isPrivacy
 * @param isAdvertisement
 */
@Builder
public record CreateSignupCompletedMemberCommand(
    String nickname,
    LocalDate dateOfBirth,
    Gender gender,
    boolean isTerms,
    boolean isPrivacy,
    boolean isAdvertisement
) {

}