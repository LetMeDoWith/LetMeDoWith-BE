package com.LetMeDoWith.LetMeDoWith.domain.task.enums;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DowithTaskStatus implements BaseEnum {
    WAIT("WAIT", "대기"),
    SUCCESS("SUCCESS", "성공(인증)"),
    COMPLETE("COMPLETE", "완료"),
    FAIL("FAIL", "실패");

    public final String code;
    public final String description;
}
