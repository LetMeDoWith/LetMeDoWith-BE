package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BadgeStatusConverter extends AbstractCombinedEnumConverter<BadgeStatus> {
    
    public BadgeStatusConverter() {
        super(BadgeStatus.class);
    }
}