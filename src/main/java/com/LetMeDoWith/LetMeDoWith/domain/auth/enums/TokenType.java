package com.LetMeDoWith.LetMeDoWith.domain.auth.enums;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType implements BaseEnum {
    ATK("ATK", "access token"),
    RTK("RTK", "refresh token"),
    SIGNUP("SIGNUP", "signup token");

    private final String code;
    public final String description;
}
