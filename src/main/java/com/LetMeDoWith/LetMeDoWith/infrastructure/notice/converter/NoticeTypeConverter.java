package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NoticeTypeConverter extends AbstractCombinedEnumConverter<NoticeType> {

    public NoticeTypeConverter() {
        super(NoticeType.class);
    }
}
