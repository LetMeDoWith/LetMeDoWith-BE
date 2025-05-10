package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BadgeStatusConverter extends AbstractCombinedConverter<BadgeStatus> {

    public BadgeStatusConverter() {
        super(BadgeStatus.class);
    }
}
