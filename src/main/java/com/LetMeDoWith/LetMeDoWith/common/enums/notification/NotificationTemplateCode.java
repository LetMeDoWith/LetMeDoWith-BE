package com.LetMeDoWith.LetMeDoWith.common.enums.notification;

import com.LetMeDoWith.LetMeDoWith.common.converter.NotificationTemplateCodeConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = NotificationTemplateCodeConverter.class)
public enum NotificationTemplateCode implements BaseEnum {
    FEEDBACK_RECEIVED_1("FEEDBACK_RECEIVED_1", "잡도리1"),
    FEEDBACK_RECEIVED_2("FEEDBACK_RECEIVED_2", "잡도리2"),
    FEEDBACK_RECEIVED_3("FEEDBACK_RECEIVED_3", "잡도리3"),
    FEEDBACK_RECEIVED_4("FEEDBACK_RECEIVED_4", "잡도리4"),
    LIKE_RECEIVED("LIKE_RECEIVED", "공감"),
    NUDGE_DORI_COMPLETE_10M("NUDGE_DORI_COMPLETE_10M", "도리 Todo 재촉 (10분 경과)"),
    NUDGE_DORI_COMPLETE_30M("NUDGE_DORI_COMPLETE_30M", "도리 Todo 재촉 (30분 경과)"),
    NUDGE_DORI_COMPLETE_50M("NUDGE_DORI_COMPLETE_50M", "도리 Todo 재촉 (50분 경과)"),
    NUDGE_DORI_START("NUDGE_DORI_START", "도리 Todo 시작 재촉 (10분전)"),
    NUDGE_TODO_START("NUDGE_TODO_START", "Todo 시작 재촉 (10분전)");

    private final String code;
    private final String description;
}
