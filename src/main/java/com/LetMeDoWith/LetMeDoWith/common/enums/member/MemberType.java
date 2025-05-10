package com.LetMeDoWith.LetMeDoWith.common.enums.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberType implements BaseEnum {
    USER("USER", "일반"),
    ADMIN("ADMIN", "관리자");

    private final String code;
    private final String description;
}
