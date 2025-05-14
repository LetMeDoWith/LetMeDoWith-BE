package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class MemberTypeConverter extends AbstractCombinedEnumConverter<MemberType> {
    
    public MemberTypeConverter() {
        super(MemberType.class);
    }
}