package com.LetMeDoWith.LetMeDoWith.common.enums.notice;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.converter.NoticeTypeConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = NoticeTypeConverter.class)
public enum NoticeType implements BaseEnum {
    NOTICE("NOTICE", "공지"),
    EVENT("EVENT", "이벤트"),
    ;

    private final String code;
    private final String description;
}