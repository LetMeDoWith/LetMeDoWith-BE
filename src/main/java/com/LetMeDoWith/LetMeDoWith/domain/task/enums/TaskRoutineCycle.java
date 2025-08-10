package com.LetMeDoWith.LetMeDoWith.domain.task.enums;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TaskRoutineCycle implements BaseEnum {
    DAILY("DAILY", "매일 반복"),
    WEEKLY("WEEKLY", "매주 반복"),
    MONTHLY("MONTHLY", "매월 반복");

    private final String code;
    private final String description;

    public static TaskRoutineCycle fromCode(String code) {
        for (TaskRoutineCycle cycle : TaskRoutineCycle.values()) {
            if (cycle.getCode().equals(code)) {
                return cycle;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
