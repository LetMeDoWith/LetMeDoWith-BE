package com.LetMeDoWith.LetMeDoWith.common.enums.notification;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.converter.NotificationTypeConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = NotificationTypeConverter.class)
public enum NotificationType implements BaseEnum {
    NORMAL("NORMAL", "일반"),
    EVENT("EVENT", "이벤트"),
    ;

    private final String code;
    private final String description;
}
