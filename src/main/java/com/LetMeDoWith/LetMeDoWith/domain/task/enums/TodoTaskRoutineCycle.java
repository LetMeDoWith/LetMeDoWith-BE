package com.LetMeDoWith.LetMeDoWith.domain.task.enums;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TodoTaskRoutineCycle implements BaseEnum {
    DAILY("DAILY", "매일 반복"),
    WEEKLY("WEEKLY", "매주 반복"),
    MONTHLY("MONTHLY", "매월 반복");

    private final String code;
    private final String description;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
