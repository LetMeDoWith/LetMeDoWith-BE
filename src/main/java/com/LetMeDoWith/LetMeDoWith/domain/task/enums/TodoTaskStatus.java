package com.LetMeDoWith.LetMeDoWith.domain.task.enums;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TodoTaskStatus implements BaseEnum {
    WAIT("WAIT", "대기"),
    COMPLETE("COMPLETE", "완료");

    public final String code;
    public final String description;
}
