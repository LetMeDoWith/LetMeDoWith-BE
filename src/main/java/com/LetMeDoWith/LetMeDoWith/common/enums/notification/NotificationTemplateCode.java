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
    FEEDBACK_RECEIVED("FEEDBACK_RECEIVED", "잡도리"),
    LIKE_RECEIVED("LIKE_RECEIVED", "공감"),
    NUDGE_DORI_COMPLETE_10M("NUDGE_DORI_COMPLETE_10M", "도리 Todo 재촉 (10분 경과)"),
    NUDGE_DORI_COMPLETE_30M("NUDGE_DORI_COMPLETE_30M", "도리 Todo 재촉 (30분 경과)"),
    NUDGE_DORI_COMPLETE_50M("NUDGE_DORI_COMPLETE_50M", "도리 Todo 재촉 (50분 경과)"),
    ;

    private final String code;
    private final String description;
}
