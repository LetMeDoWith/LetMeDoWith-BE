package com.LetMeDoWith.LetMeDoWith.common.enums.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus implements BaseEnum {
    NORMAL("NORMAL", "일반", null),
    SUSPENDED("SUSPENDED", "일시정지", FailResponseStatus.LOGIN_ATTEMPTED_SUSPENED),
    WITHDRAWN("WITHDRAWN", "탈퇴", FailResponseStatus.LOGIN_ATTEMPTED_WITHDRAWN),
    // 소셜 인증 이후 회원가입 완료 이전의 상태.
    SOCIAL_AUTHENTICATED(
            "SOCIAL_AUTHENTICATED", "소셜 인증됨", FailResponseStatus.LOGIN_ATTEMPTED_REGISTRATION_NOT_COMPLTETE);

    private final String code;
    private final String description;

    private final FailResponseStatus apiResponseStatus;
}
