package com.LetMeDoWith.LetMeDoWith.common.enums.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskCompleteLevel implements BaseEnum {
    GOOD("3", "레벨3-초록불"),
    AVERAGE("2", "레벨2-노란불"),
    BAD("1", "레벨1-빨간불");

    private final String code;
    private final String description;
}
