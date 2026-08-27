package com.LetMeDoWith.LetMeDoWith.common.enums.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeStatus implements BaseEnum {
    ACTIVE("ACTIVE", "활성화"),
    INACTIVE("INACTIVE", "비활성화");

    private final String code;
    private final String description;
}
